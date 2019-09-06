package Interpreter;
import LayanAST.Expressions.*;
import LayanAST.Program;
import Interpreter.Spaces.FunctionSpace;
import Interpreter.Spaces.MemorySpace;
import LayanAST.Declarations.VariableDeclaration;
import LayanAST.Declarations.VariableDeclarationList;
import LayanAST.LayanAST;
import Symbols.BuiltInTypeSymbol;
import Symbols.Symbol;
import Tokens.Tokens;

import java.util.Stack;

public class Interpreter {

    private MemorySpace globalSpace;
    private MemorySpace currentSpace;
    private Stack<FunctionSpace> callStack; // function call stack

    public Interpreter(LayanAST root){
        System.out.println("\nInterpreter start");
        globalSpace = new MemorySpace("globals"); // define global space for global vars, classes,
        // and functions
        currentSpace = globalSpace;

        execute(root);
    }

    private Object execute(LayanAST root){
        switch (root.token.type){
            case Tokens.PROGRAM: walkProgram((Program) root); break;
            case Tokens.TYPE: walkVariableDeclarationList((VariableDeclarationList) root); break;
            case Tokens.EQUAL: walkAssignment((EqualNode) root); break;
            case Tokens.PLUS: return walkAddNode((AddNode) root);
            case Tokens.MINUS: return walkMinusNode((SubtractionNode) root);
            case Tokens.INT: return Integer.parseInt(root.token.text);
            case Tokens.FLOAT: return Float.parseFloat(root.token.text);
            case Tokens.STRING: return walkStringNode((StringNode) root);
            default: throw new Error("Unhandled Node type");
        }
        return null;
    }

    private MemorySpace getSpaceWithSymbol(String id){
        if(globalSpace.get(id) != null) return globalSpace;
        else return null;
        //TODO:: call stack
    }

    private void walkProgram(Program program){
        for (LayanAST node: program.statements){
            execute(node);
        }
    }

    private void walkVariableDeclarationList(VariableDeclarationList node){
        for(VariableDeclaration item: node.variableDeclarations)
            walkVariableDeclaration(item);
    }

    private void walkVariableDeclaration(VariableDeclaration node){
        currentSpace.put(node.id.name.text, node.expression);
    }

    private void walkAssignment(EqualNode node){
        if(node.id.scope.resolve(node.id.name.text) == null){
            throw new Error("undefined variable " + node.id.name.text);
        }
        Object value = execute(node.expression);
        MemorySpace space = getSpaceWithSymbol(node.id.name.text);
        space.put(node.id.name.text, value);
        System.out.println(node.id.name.text + ": " + currentSpace.get(node.id.name.text));
    }

    private Object cast(ExprNode node, String value){
        if(((BuiltInTypeSymbol)node.evalType).typeIndex == 2){
            if(node.promoteToType != null
                    && ((BuiltInTypeSymbol)node.promoteToType).typeIndex == 3){
                int result = 0;
                String v = value;
                for(int i=0;i<v.length();i++){
                    result += (int) value.charAt(i);
                }
                return result;
            }else if(node.promoteToType != null
                    && ((BuiltInTypeSymbol)node.promoteToType).typeIndex == 4){
                double result = 0;
                String v = value;
                for(int i=0;i<v.length();i++){
                    result += (double) value.charAt(i);
                }
                return result;
            }
            return value;
        }else{
            if(node.promoteToType != null && ((BuiltInTypeSymbol)node.promoteToType).typeIndex == 3){
                return Integer.parseInt(value);
            }else if(node.promoteToType != null && ((BuiltInTypeSymbol)node.promoteToType).typeIndex == 4){
                return Float.parseFloat(value);
            }else{
                return value;
            }
        }
    }

    private Object walkAddNode(AddNode node){
        if(((BuiltInTypeSymbol)node.evalType).typeIndex == 4){ // float
            return cast(node, (Float.parseFloat(execute(node.left).toString()) +
                    Float.parseFloat(execute(node.right).toString())) + "");
        }else if(((BuiltInTypeSymbol)node.evalType).typeIndex == 3) { //int
            return cast(node, Integer.parseInt(execute(node.left).toString()) +
                    Integer.parseInt(execute(node.right).toString()) + "");
        }else{ // string
            return cast(node,execute(node.left).toString() + execute(node.right).toString());
        }
    }

    private Object walkMinusNode(SubtractionNode node){
        if(((BuiltInTypeSymbol)node.evalType).typeIndex == 4){ // float
            return cast(node, (Float.parseFloat(execute(node.left).toString()) -
                    Float.parseFloat(execute(node.right).toString())) + "");
        }else if(((BuiltInTypeSymbol)node.evalType).typeIndex == 3) { //int
            return cast(node, Integer.parseInt(execute(node.left).toString()) -
                    Integer.parseInt(execute(node.right).toString()) + "");
        }else{ // string
            throw new Error("Unsupported operand types for -");
        }
    }

    private Object walkStringNode(StringNode node){
        //int
        if(node.promoteToType != null
                && ((BuiltInTypeSymbol)node.promoteToType).typeIndex == 3){
            int result = 0;
            String value = node.token.text.substring(1, node.token.text.length() - 1);
            for(int i=0;i<value.length();i++){
                result += (int) value.charAt(i);
            }
            return result;
        }else if(node.promoteToType != null
                && ((BuiltInTypeSymbol)node.promoteToType).typeIndex == 4){
            double result = 0;
            String value = node.token.text.substring(1, node.token.text.length() - 1);
            for(int i=0;i<value.length();i++){
                result += (double) value.charAt(i);
            }
            return result;
        }

        return node.token.text;
    }
}
