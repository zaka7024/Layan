package Interpreter;
import Interpreter.Spaces.ClassSpace;
import LayanAST.Conditions.ConditionNode;
import LayanAST.Conditions.IterationNode;
import LayanAST.Declarations.*;
import LayanAST.Expressions.*;
import LayanAST.Program;
import Interpreter.Spaces.FunctionSpace;
import Interpreter.Spaces.MemorySpace;
import LayanAST.LayanAST;
import LayanAST.Print;
import Symbols.BuiltInTypeSymbol;
import Symbols.ClassSymbol;
import Symbols.MethodSymbol;
import Tokens.Tokens;
import com.sun.org.apache.xpath.internal.operations.And;
import com.sun.org.apache.xpath.internal.operations.Or;

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
            case Tokens.CLASS: walkClassDeclaration((ClassDeclaration) root); break;
            case Tokens.FUNCTION: walkMethodDeclaration((MethodDeclaration) root); break;
            case Tokens.IF:
            case Tokens.WHILE: walkConditionNode((ConditionNode) root); break;
            case Tokens.FOR: walkIterationNode((IterationNode) root); break;
            case Tokens.MORETHAN: return walkMoreThanNode((MoreThanNode) root);
            case Tokens.LESSTHAN: return walkLessThanNode((LessThanNode) root);
            case Tokens.MORETHANOREQUAL: return walkMoreThanOrEqualNode((MoreThanOrEqualNode)root);
            case Tokens.LESSTHANOREQUAL: return walkLessThanOrEqualNode((LessThanOrEqualNode) root);
            case Tokens.AND: return walkAndNode((AndNode) root);
            case Tokens.OR: return walkOrNode((OrNode) root);
            case Tokens.NOT: return walkNotNode((NotNode) root);
            case Tokens.NOTEQUAL: return walkNotEqualNode((InequalityNode) root);
            case Tokens.EQUALITY: return walkEqualityNodeNode((EqualityNode) root);
            case Tokens.OPENCARLYBRACKET:
                walkBlock((BlockNode) root);

        }
        return null;
    }

    private MemorySpace getSpaceWithSymbol(String id){
        if(!callStack.empty() && callStack.peek().get(id) != null) return callStack.peek();
        //else if(globalSpace.get(id) != null) return globalSpace;
        return globalSpace; //TODO:: thr Exception

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
            return getSpaceWithSymbol(((ID) node).name.text).get(((ID) node).name.text);
        }else if(node instanceof FunctionCall){
            call((FunctionCall) node);
        }else if(node instanceof ObjectDeclaration){
            walkObjectDeclaration((ObjectDeclaration) node);
        }else if(node instanceof ResolutionObject){
            return walkResolutionObject((ResolutionObject) node);
        }
        return null;
    }

    private void call(FunctionCall call){
        MethodSymbol symbol = (MethodSymbol) call.id.symbol;

        FunctionSpace functionSpace = new FunctionSpace(symbol);
        MemorySpace previousSpace = currentSpace;

        // pass the args
        int i = 0;
        for(String name: symbol.parameters.keySet()){
            functionSpace.put(name, execute(call.args.get(i++)));
        }

        callStack.push(functionSpace);
        currentSpace = functionSpace;

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

    private void walkMethodDeclaration(MethodDeclaration node){
        currentSpace.put(node.id.name.text, node.id.symbol);
    }

    private void walkClassDeclaration(ClassDeclaration node){
        ClassSymbol classSymbol = (ClassSymbol) node.id.symbol;
        //execute(((ClassDeclaration)classSymbol.def).block);
    }

    private void walkObjectDeclaration(ObjectDeclaration node){
        ClassSymbol classSymbol = (ClassSymbol) node.type.symbol;
        //TODO:: define class instance, resolve member, check if there is or not,...
        ClassSpace classSpace = new ClassSpace(classSymbol);
        currentSpace.put(node.id.name.text, classSpace);
        MemorySpace previousSpace = currentSpace;
        currentSpace = classSpace;
        execute(((ClassDeclaration)classSymbol.def).block);
        currentSpace = previousSpace;
    }

    private Object walkResolutionObject(ResolutionObject node){
        MemorySpace previousSpace = currentSpace;
        MemorySpace previousGlobal = globalSpace;
        ClassSpace classSpace = (ClassSpace) currentSpace.get(node.type.name.text);
        globalSpace = classSpace;
        currentSpace = classSpace;
        if(classSpace.get(node.member.name.text) instanceof MethodSymbol){
            execute(node.functionCall);
        }
        globalSpace = previousGlobal;
        currentSpace = previousSpace;
        return classSpace.get(node.member.name.text);
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
        if(((BuiltInTypeSymbol)node.evalType).typeIndex == 2){ // string
            if(node.promoteToType != null
                    && ((BuiltInTypeSymbol)node.promoteToType).typeIndex == 3){
                int result = 0;
                for(int i=0;i<value.length();i++){
                    result += (int) value.charAt(i);
                }
                return result;
            }else{
                float result = 0;
                for(int i=0;i<value.length();i++){
                    result += (float) value.charAt(i);
                }
                return result;
            }
        }else if(((BuiltInTypeSymbol)node.evalType).typeIndex == 1){//boolean
            float result = 0;
            for(int i=0;i<value.length();i++){
                result += (float) value.charAt(i);
            }
            return result;
        }
        else{ // int or float
            if(node.promoteToType != null && ((BuiltInTypeSymbol)node.promoteToType).typeIndex == 3){
                return Integer.parseInt(value);
            }else if(node.promoteToType != null
                    && ((BuiltInTypeSymbol)node.promoteToType).typeIndex == 4){
                return Float.parseFloat(value);
            }
        }
        return value;
    }

    private Object walkAddNode(AddNode node){
        if(((BuiltInTypeSymbol)node.evalType).typeIndex == 4){ // float
            return cast(node, (Float.parseFloat(execute(node.left).toString()) +
                    Float.parseFloat(execute(node.right).toString())) + "");
        }else if(((BuiltInTypeSymbol)node.evalType).typeIndex == 3) { //int
            return Integer.parseInt(cast(node, Integer.parseInt(execute(node.left).toString()) +
                    Integer.parseInt(execute(node.right).toString()) + "").toString());
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
        String text = node.token.text.substring(1, node.token.text.length() - 1);
        //int
        if(node.promoteToType != null
                && ((BuiltInTypeSymbol)node.promoteToType).typeIndex == 3){
            int result = 0;
            for(int i=0;i<text.length();i++){
                result += (int) text.charAt(i);
            }
            return result;
        }else if(node.promoteToType != null
                && ((BuiltInTypeSymbol)node.promoteToType).typeIndex == 4){
            double result = 0;
            for(int i=0;i<text.length();i++){
                result += (double) text.charAt(i);
            }
            return result;
        }

        return text;
    }

    private boolean walkBoolNode(BoolNode node){
        return node.token.text.compareTo("true") == 0 ? true : false;
    }

    private boolean walkMoreThanNode(MoreThanNode node){
        Object left = execute(node.left);
        Object right = execute(node.right);
        if(left instanceof String){
            left = cast(node, left.toString());
        }
        if(right instanceof String){
            right = cast(node, right.toString());
        }
        return (Float.parseFloat(left.toString())) > Float.parseFloat(right.toString());
    }

    private boolean walkLessThanNode(LessThanNode node){
        Object left = execute(node.left);
        Object right = execute(node.right);
        if(left instanceof String){
            left = cast(node, left.toString());
        }
        if(right instanceof String){
            right = cast(node, right.toString());
        }
        return (Float.parseFloat(left.toString())) < Float.parseFloat(right.toString());
    }

    private boolean walkMoreThanOrEqualNode(MoreThanOrEqualNode node){
        Object left = execute(node.left);
        Object right = execute(node.right);
        if(left instanceof String){
            left = cast(node, left.toString());
        }
        if(right instanceof String){
            right = cast(node, right.toString());
        }
        return (Float.parseFloat(left.toString())) >= Float.parseFloat(right.toString());
    }

    private boolean walkLessThanOrEqualNode(LessThanOrEqualNode node){
        Object left = execute(node.left);
        Object right = execute(node.right);
        /*if(left instanceof String){//TODO:: resolve this
            left = cast(node, left.toString());
        }
        if(right instanceof String){
            right = cast(node, right.toString());
        }*/
        return (Float.parseFloat(left.toString())) <= Float.parseFloat(right.toString());
    }

    private boolean walkAndNode(AndNode node){
        return (boolean)execute(node.left) && (boolean)execute(node.right);
    }

    private boolean walkOrNode(OrNode node){
        return (boolean)execute(node.left) || (boolean)execute(node.right);
    }

    private boolean walkNotNode(NotNode node){
        return !((boolean)execute(node.expression));
    }

    private boolean walkNotEqualNode(InequalityNode node){
        return execute(node.left) != execute(node.right);
    }

    private boolean walkEqualityNodeNode(EqualityNode node){
        return execute(node.left) == execute(node.right);
    }

    private void walkConditionNode(ConditionNode node){
        boolean _switch = (boolean)execute(node.expression); // boolean java to string
        if(node.token.text == "if"){
            if(_switch){
                execute(node.truePart);
            }else if(node.falsePart != null){
                execute(node.falsePart);
            }
        }else{ // while statement
            if(_switch && node.falsePart != null){
                execute(node.falsePart);
            }
            while (_switch){
                execute(node.truePart);
                _switch = (boolean)execute(node.expression);
            }
        }
    }

    private void walkIterationNode(IterationNode node){
        walkVariableDeclaration((VariableDeclaration) node.forVariable);

        for(Object i = currentSpace.get(node.forVariable.type.name.text);
            (boolean) execute(node.expression); execute(node.assignment)){
            execute(node.blockNode);
        }
    }
}
