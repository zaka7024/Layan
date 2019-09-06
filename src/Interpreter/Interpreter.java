package Interpreter;
import LayanAST.Conditions.ConditionNode;
import LayanAST.Declarations.*;
import LayanAST.Expressions.*;
import LayanAST.Program;
import Interpreter.Spaces.FunctionSpace;
import Interpreter.Spaces.MemorySpace;
import LayanAST.LayanAST;
import LayanAST.Print;
import Symbols.BuiltInTypeSymbol;
import Symbols.MethodSymbol;
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
        callStack = new Stack<>();
        execute(root);
    }

    private Object execute(LayanAST root){
        switch (root.token.type){
            case Tokens.PROGRAM: walkProgram((Program) root); break;
            case Tokens.ID: return walkID(root);
            case Tokens.PRINT: print((Print) root); break;
            case Tokens.RETURN: _return((ReturnNode) root); break;
            case Tokens.TYPE: walkVariableDeclarationList((VariableDeclarationList) root); break;
            case Tokens.EQUAL: walkAssignment((EqualNode) root); break;
            case Tokens.PLUS: return walkAddNode((AddNode) root);
            case Tokens.MINUS: return walkMinusNode((SubtractionNode) root);
            case Tokens.MULTIPLICATION: return walkMultiplicationNode((MultiplicationNode) root);
            case Tokens.DIVISION: return walkDivisionNode((DivisionNode) root);
            case Tokens.INT: return Integer.parseInt(root.token.text);
            case Tokens.FLOAT: return Float.parseFloat(root.token.text);
            case Tokens.STRING: return walkStringNode((StringNode) root);
            case Tokens.BOOLEAN: return walkBoolNode((BoolNode) root);
            case Tokens.IF:
            case Tokens.WHILE:
                walkConditionNode((ConditionNode) root); break;
            case Tokens.OPENCARLYBRACKET:
                walkBlock((BlockNode) root);

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

    private void print(Print print){
        Object value = execute(print.exprNode);
        System.out.println(value);
    }

    private void _return(ReturnNode node){
        throw new Error("return");
    }

    private Object walkID(LayanAST node){
        if(node instanceof ID){
            return currentSpace.get(((ID) node).name.text);
        }else if(node instanceof FunctionCall){
            call((FunctionCall) node);
        }
        return null;
    }

    private void call(FunctionCall call){
        MethodSymbol symbol = (MethodSymbol) call.id.symbol;

        FunctionSpace functionSpace = new FunctionSpace(symbol);
        callStack.push(functionSpace);
        MemorySpace previousSpace = currentSpace;
        currentSpace = functionSpace;

        // pass the args
        int i = 0;
        for(String name: symbol.parameters.keySet()){
            currentSpace.put(name, execute(call.args.get(i++)));
        }

        try{
            walkBlock(symbol.functionBlock);
        }catch (Error ex){
        }
        callStack.pop();
        currentSpace = previousSpace;
    }

    private void walkBlock(BlockNode node){
        for(LayanAST statement: node.layanASTList){
            execute(statement);
        }
    }

    private void walkVariableDeclarationList(VariableDeclarationList node){
        for(VariableDeclaration item: node.variableDeclarations)
            walkVariableDeclaration(item);
    }

    private void walkVariableDeclaration(VariableDeclaration node){
        Object value = null;
        if(node.expression != null){
            value = execute(node.expression);
        }
        currentSpace.put(node.id.name.text, value);
    }

    private void walkAssignment(EqualNode node){
        if(node.id.scope.resolve(node.id.name.text) == null){
            throw new Error("undefined variable " + node.id.name.text);
        }
        Object value = execute(node.expression);
        MemorySpace space = getSpaceWithSymbol(node.id.name.text);
        space.put(node.id.name.text, value);
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

    private Object walkMultiplicationNode(MultiplicationNode node){
        if(((BuiltInTypeSymbol)node.evalType).typeIndex == 4){ // float
            return cast(node, (Float.parseFloat(execute(node.left).toString()) *
                    Float.parseFloat(execute(node.right).toString())) + "");
        }else if(((BuiltInTypeSymbol)node.evalType).typeIndex == 3) { //int
            return cast(node, Integer.parseInt(execute(node.left).toString()) *
                    Integer.parseInt(execute(node.right).toString()) + "");
        }else{ // string
            throw new Error("Unsupported operand types for *");
        }
    }

    private Object walkDivisionNode(DivisionNode node){
        if(((BuiltInTypeSymbol)node.evalType).typeIndex == 4){ // float
            return cast(node, (Float.parseFloat(execute(node.left).toString()) /
                    Float.parseFloat(execute(node.right).toString())) + "");
        }else if(((BuiltInTypeSymbol)node.evalType).typeIndex == 3) { //int
            return cast(node, Integer.parseInt(execute(node.left).toString()) /
                    Integer.parseInt(execute(node.right).toString()) + "");
        }else{ // string
            throw new Error("Unsupported operand types for *");
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

    private Object walkBoolNode(BoolNode node){
        return node.token.text;
    }

    private void walkConditionNode(ConditionNode node){
        String _switch = execute(node.expression).toString();
        if(node.token.text == "if"){
            if(_switch.compareTo("true") == 0){
                execute(node.truePart);
            }else{
                execute(node.falsePart);
            }
        }else{ // while statement
            while (_switch.compareTo("true") == 0){
                execute(node.truePart);
            }
        }
    }
}
