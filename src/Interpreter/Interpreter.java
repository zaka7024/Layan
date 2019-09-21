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
import edu.princeton.cs.algs4.StdDraw;

import java.util.Stack;

public class Interpreter {

    private MemorySpace globalSpace;
    private MemorySpace currentSpace;
    private Stack<FunctionSpace> callStack; // function call stack

    // StdDraw
    int windowWidth = 512, windowHeight = 512;

    public Interpreter(LayanAST root){
        System.out.println("\nInterpreter start");
        globalSpace = new MemorySpace("globals"); // define global space for global vars, classes,
        // and functions
        currentSpace = globalSpace;
        callStack = new Stack<>();
        execute(root);

        // StdDraw init
    }

    public Object execute(LayanAST root){
        switch (root.token.type){
            case Tokens.PROGRAM: walkProgram((Program) root); break;
            case Tokens.ID: return walkID(root);
            case Tokens.PRINT: print((Print) root); break;
            case Tokens.RETURN: _return((ReturnNode) root); break;
            case Tokens.TYPE: walkVariableDeclarationList((VariableDeclarationList) root); break;
            case Tokens.EQUAL: walkAssignment((EqualNode) root); break;
            case Tokens.PLUS: return walkPlus(root);
            case Tokens.MINUS: return walkMinus(root);
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
        else{
            MemorySpace space = globalSpace;
            while (space != null){
                if(space.contains(id)) return space;
                space = space.previousSpace;
            }
        }
        return null;
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

        // get params count
        int paramsCount = symbol.parameters.size();
        // get args count
        int argsCount = call.args.size();
        if(argsCount != paramsCount) throw new Error("function " + call.id.name.text
        + " takes more or less args");

        // pass the args
        int i = 0;
        for(String name: symbol.parameters.keySet()){
            functionSpace.put(name, execute(call.args.get(i++)));
        }

        callStack.push(functionSpace);
        currentSpace = functionSpace;

        if(symbol.name.compareTo("draw") == 0){
            Draw();
        }else if(symbol.name.compareTo("setPenSize") == 0){
            setPenSize(functionSpace);
        }else if(symbol.name.compareTo("setScale") == 0){
            setScale(functionSpace);
        }else if(symbol.name.compareTo("setCanvasSize") == 0){
            setCanvasSize(functionSpace);
        }else if(symbol.name.compareTo("wait") == 0){
            wait(functionSpace);
        }else if(symbol.name.compareTo("clear") == 0){
            clear();
        }else if(symbol.name.compareTo("initLayan") == 0){
            initLayan();
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
        MemorySpace previousSpace = currentSpace;
        currentSpace.put(node.id.name.text, classSpace);
        classSpace.previousSpace = globalSpace;
        globalSpace = classSpace;
        currentSpace = globalSpace;

        execute(((ClassDeclaration)classSymbol.def).block);

        currentSpace = previousSpace;
        globalSpace = classSpace.previousSpace;
    }

    private Object walkResolutionObject(ResolutionObject node){
        MemorySpace previousSpace = currentSpace;
        ClassSpace classSpace = (ClassSpace) currentSpace.get(node.type.name.text);
        currentSpace = classSpace;
        if(classSpace.get(node.member.name.text) instanceof MethodSymbol){
            classSpace.previousSpace = globalSpace;
            globalSpace = classSpace;
            execute(node.functionCall);
            globalSpace = classSpace.previousSpace;
        }
        currentSpace = previousSpace;
        return classSpace.get(node.member.name.text);
    }

    private void walkAssignment(EqualNode node){
        if(node.id.scope.resolve(node.id.name.text) == null){
            throw new Error("undefined variable " + node.id.name.text);
        }
        Object value = execute(node.expression);
        if(node.member != null && node.member.member != null) { // assign to member in object
            // TODO:: Check if the member is a function
            MemorySpace space  = getSpaceWithSymbol(node.member.type.name.text);
            ((ClassSpace)(space.get(node.member.type.name.text))).put(
                    node.member.member.name.text, value
            );
            return;
        }
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

    private Object walkPlus(LayanAST node){
        if(node instanceof AddNode){
            return walkAddNode((AddNode) node);
        }
        return walkUnaryPositive((UnaryPositive) node);
    }

    private Object walkUnaryPositive(UnaryPositive node){
        return execute(node.expression);
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

    private Object walkMinus(LayanAST node){
        if(node instanceof SubtractionNode) return walkSubtractionNode((SubtractionNode)node);
        return walkUnaryNegative((UnaryNegative)node);
    }

    private Object walkUnaryNegative(UnaryNegative node){
        if(execute(node.expression) instanceof Float) return -Float.parseFloat(execute(node.expression).toString());
        return -Integer.parseInt(execute(node.expression).toString());
    }

    private Object walkSubtractionNode(SubtractionNode node){
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

    //

    private void initLayan(){
        MemorySpace space = getSpaceWithSymbol("WIDTH");
        System.out.println("init");
        space.put("WIDTH", windowWidth);
    }

    private void setPenSize(FunctionSpace space){
        float size = Float.parseFloat(space.get("size").toString());
        StdDraw.setPenRadius(size);
    }

    private void setScale(FunctionSpace space){
        float min = Float.parseFloat(space.get("min").toString());
        float max = Float.parseFloat(space.get("max").toString());
        StdDraw.setScale(min, max);
    }

    private void setCanvasSize(FunctionSpace space){
        int width = Integer.parseInt(space.get("width").toString());
        int height = Integer.parseInt(space.get("height").toString());
        StdDraw.setCanvasSize(width, height);
        windowWidth = width;
        windowHeight = height;
    }

    private void wait(FunctionSpace space){
        int ms = Integer.parseInt(space.get("ms").toString());
        StdDraw.pause(ms);
    }

    private void clear(){
        StdDraw.clear();
    }

    private void Draw(){
        ClassSpace space = (ClassSpace) getSpaceWithSymbol("draw");
        switch (space.classSymbol.name){
            case "Point": drawPoint(space); break;
            case "Square": drawSquare(space); break;
            case "Rectangle": drawRectangle(space); break;
            case "Circle": drawCircle(space); break;
            case "Text": drawText(space); break;
        }
    }

    private void drawPoint(ClassSpace space){
        float x = Float.parseFloat(space.get("x").toString());
        float y = Float.parseFloat(space.get("y").toString());
        StdDraw.point(x, y);
    }

    private void drawSquare(ClassSpace space){
        float x = Float.parseFloat(space.get("x").toString());
        float y = Float.parseFloat(space.get("y").toString());
        float length = Float.parseFloat(space.get("length").toString());
        StdDraw.square(x, y, length);
    }

    private void drawCircle(ClassSpace space){
        float x = Float.parseFloat(space.get("x").toString());
        float y = Float.parseFloat(space.get("y").toString());
        float radius = Float.parseFloat(space.get("radius").toString());
        StdDraw.circle(x, y, radius);
    }

    private void drawRectangle(ClassSpace space){
        float x = Float.parseFloat(space.get("x").toString());
        float y = Float.parseFloat(space.get("y").toString());
        float width = Float.parseFloat(space.get("width").toString());
        float height= Float.parseFloat(space.get("height").toString());
        StdDraw.rectangle(x, y, width, height);
    }

    private void drawText(ClassSpace space){
        float x = Float.parseFloat(space.get("x").toString());
        float y = Float.parseFloat(space.get("y").toString());
        String text = space.get("value").toString();
        StdDraw.text(x, y, text);
    }
}
