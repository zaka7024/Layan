package LayanAST;
import LayanAST.Conditions.ConditionNode;
import LayanAST.Conditions.IterationNode;
import LayanAST.Declarations.*;
import LayanAST.Expressions.*;
import Symbols.*;
import Tokens.Tokens;

public class ASTVisitorDefine {

    private Scope currentScope; // represent the current scope in the scope tree

    public ASTVisitorDefine(LayanAST root){
        walk(root);
        System.out.println(currentScope);
    }

    private void walk(LayanAST root){
        switch (root.token.type){
            case Tokens.PROGRAM: walkProgram((Program) root); break;
            case Tokens.TYPE: walkVariableDeclarationList((VariableDeclarationList) root); break;
            case Tokens.ID: walkID(root); break;
            case Tokens.PRINT: walkPrint((Print) root); break;
            case Tokens.IF:
            case Tokens.WHILE:
                walkConditionNode((ConditionNode) root); break;
            case Tokens.EQUAL:
                walkAssignment((EqualNode) root); break;
            case Tokens.FOR: walkIterationNode((IterationNode) root); break;
            case Tokens.CLASS: walkClassDeclaration((ClassDeclaration) root); break;
            case Tokens.FUNCTION: walkMethodDeclaration((MethodDeclaration) root); break;
            case Tokens.OPENCARLYBRACKET: walkBlock((BlockNode) root); break;
            case Tokens.PLUS: walkPlus(root); break;
            case Tokens.MINUS: walkMinus(root); break;
            case Tokens.MULTIPLICATION: walkMultiplicationNode((MultiplicationNode) root); break;
            case Tokens.DIVISION: walkDivisionNode((DivisionNode) root); break;
            case Tokens.EQUALITY: walkEqualityNode((EqualityNode) root); break;
            case Tokens.NOTEQUAL: walkNotEqualNode((InequalityNode) root); break;
            case Tokens.MORETHAN: walkMoreThanNode((MoreThanNode) root); break;
            case Tokens.MORETHANOREQUAL: walkMoreThanOrEqualNode((MoreThanOrEqualNode) root); break;
            case Tokens.LESSTHAN: walkLessThanNode((LessThanNode) root); break;
            case Tokens.LESSTHANOREQUAL: walkLessThanOrEqualNode((LessThanOrEqualNode) root); break;
            case Tokens.NOT: walkNotNode((NotNode) root); break;
            case Tokens.AND: walkAndNode((AndNode) root); break;
            case Tokens.OR: walkOrNode((OrNode) root); break;
            case Tokens.MODULES: walkModulusNode((ModulusNode) root); break;
        }
    }

    private void walkProgram(Program program){
        // push global scope in the scope tree
        ProgramSymbol global = new ProgramSymbol("Program", currentScope);
        currentScope = global;
        for (LayanAST node: program.statements){
            walk(node);
        }
    }

    private void walkBlock(BlockNode blockNode){
        for(LayanAST node: blockNode.layanASTList){
            walk(node);
        }
    }

    private void walkFunctionCall(FunctionCall node){
        System.out.println(node.toStringNode());
        node.id.scope = currentScope;
    }

    private Symbol walkVariableDeclaration(VariableDeclaration node){
        System.out.println(node.toStringNode());
        VariableSymbol variableSymbol = new VariableSymbol(node.id.token.text, null,
                node, currentScope);
        currentScope.define(variableSymbol); // define symbol in the current scope
        node.id.symbol = variableSymbol; // map var id field to variable symbol
        node.id.scope = currentScope;
        if(node.expression != null) walk(node.expression);
        return variableSymbol;
    }

    private void walkID(LayanAST node){
        if(node instanceof ID){
            ((ID) node).scope = currentScope;
        }else if(node instanceof ObjectDeclaration){
            walkObjectDeclaration((ObjectDeclaration) node);
        }else if(node instanceof ResolutionObject){
            walkResolutionObject((ResolutionObject) node);
        }else if(node instanceof FunctionCall){
            walkFunctionCall((FunctionCall)node);
        }
    }

    private void walkPrint(Print print){
        walk(print.exprNode);
    }

    private void walkAssignment(EqualNode node){
        node.id.scope = currentScope;
        walk(node.expression);
    }

    private void walkVariableDeclarationList(VariableDeclarationList node){
        for(VariableDeclaration item: node.variableDeclarations)
            walkVariableDeclaration(item);
    }

    private void walkResolutionObject(ResolutionObject node){
        node.type.scope = currentScope;
        node.member.scope = currentScope;
    }

    private void walkObjectDeclaration(ObjectDeclaration node){
        System.out.println(node.toStringNode());
        ObjectSymbol objectSymbol = new ObjectSymbol(node.id.name.text,null,
                node, currentScope);
        currentScope.define(objectSymbol);
        // Map the id with object symbol
        node.id.symbol = objectSymbol;
        // set the scope for id the current scope
        node.id.scope = currentScope;
    }

    private void walkMethodDeclaration(MethodDeclaration node){
        System.out.println(node.toStringNode());
        MethodSymbol methodSymbol = new MethodSymbol(node.id.name.text, new BuiltInTypeSymbol("void"), currentScope, node);
        currentScope.define(methodSymbol);
        // push scope(parameters scope) in the tree scope
        currentScope = methodSymbol;
        // map node's id to the method symbol
        node.id.symbol = methodSymbol;
        node.id.scope = currentScope;
        // define all parameters in the parameters scope
        for (LayanAST arg: node.parameters)
            currentScope.define(walkVariableDeclaration((VariableDeclaration) arg));
        // push local scope
        LocalScope localScope = new LocalScope(currentScope);
        currentScope = localScope;
        walk(node.block);

        System.out.println(currentScope);
        currentScope = currentScope.getEnclosingScope(); // pop block scope
        System.out.println(currentScope);
        currentScope = currentScope.getEnclosingScope(); // pop parameters scope
    }

    private void walkClassDeclaration(ClassDeclaration node){
        System.out.println(node.toStringNode());
        // create Class symbol (Scope)
        ClassSymbol classSymbol = new ClassSymbol(node.id.name.text, null, currentScope, node);
        // define class symbol in the current scope
        currentScope.define(classSymbol);
        // push the class scope in the scope tree
        currentScope = classSymbol;
        // map node's id symbol to class symbol
        node.id.symbol = classSymbol;
        node.id.scope = classSymbol;
        ((ClassSymbol) currentScope).scope = currentScope;
        walk(node.block);
        System.out.println(currentScope);
        // pop the current scope(class scope)
        currentScope = classSymbol.getEnclosingScope();
    }

    private void walkPlus(LayanAST node){
        String name = node.getClass().getTypeName();
        if(name.compareTo(AddNode.class.getTypeName()) == 0){
            walkAddNode((AddNode) node);
        }else{
            walkUnaryPositive((UnaryPositive) node);
        }
    }

    private void walkMultiplicationNode(MultiplicationNode node){
        walk(node.left);
        walk(node.right);
    }

    private void walkDivisionNode(DivisionNode node){
        walk(node.left);
        walk(node.right);
    }

    private void walkAddNode(AddNode addNode){
        if(addNode == null) return;
        System.out.println(addNode.toStringNode());
        walk(addNode.left);
        walk(addNode.right);
    }

    private void walkMinus(LayanAST node){
        String name = node.getClass().getTypeName();
        if(name.compareTo(SubtractionNode.class.getTypeName()) == 0){
            walkSubtractionNode((SubtractionNode) node);
        }else{
            walkUnaryNegative((UnaryNegative) node);
        }
    }

    private void walkSubtractionNode(LayanAST addNode){
        if(addNode == null) return;
        System.out.println(addNode.toStringNode());
    }

    private void walkAndNode(AndNode node){
        walk(node.left);
        walk(node.right);
    }

    private void walkOrNode(OrNode node){
        walk(node.left);
        walk(node.right);
    }

    private void walkNotEqualNode(InequalityNode node){
        walk(node.left);
        walk(node.right);
    }

    private void walkEqualityNode(EqualityNode node){
        walk(node.left);
        walk(node.right);
    }

    private void walkLessThanNode(LessThanNode node){
        walk(node.left);
        walk(node.right);
    }

    private void walkMoreThanNode(MoreThanNode node){
        walk(node.left);
        walk(node.right);
    }

    private void walkMoreThanOrEqualNode(MoreThanOrEqualNode node){
        walk(node.left);
        walk(node.right);
    }

    private void walkLessThanOrEqualNode(LessThanOrEqualNode node){
        walk(node.left);
        walk(node.right);
    }

    private void walkModulusNode(ModulusNode node){
        walk(node.left);
        walk(node.right);
    }

    private void walkNotNode(NotNode node){
        walk(node.expression);
    }

    private void walkUnaryPositive(UnaryPositive unaryPositive){
        System.out.println(unaryPositive);
    }

    private void walkUnaryNegative(UnaryNegative unaryNegative){
        System.out.println(unaryNegative);
    }

    private void walkConditionNode(ConditionNode node){
        walk(node.expression);

    }

    private void walkIterationNode(IterationNode node){
        System.out.println(node.toStringNode());
    }
}
