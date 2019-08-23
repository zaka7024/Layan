package LayanAST;

import LayanAST.Conditions.ConditionNode;
import LayanAST.Conditions.IterationNode;
import LayanAST.Declarations.*;
import LayanAST.Expressions.AddNode;
import LayanAST.Expressions.SubtractionNode;
import LayanAST.Expressions.UnaryNegative;
import LayanAST.Expressions.UnaryPositive;
import LayanAST.LayanAST;
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
            case Tokens.IF:
            case Tokens.WHILE:
                walkConditionNode((ConditionNode) root); break;
            case Tokens.FOR: walkIterationNode((IterationNode) root); break;
            case Tokens.CLASS: walkClassDeclaration((ClassDeclaration) root); break;
            case Tokens.FUNCTION: walkMethodDeclaration((MethodDeclaration) root); break;
            case Tokens.OPENCARLYBRACKET: walkBlock((BlockNode) root); break;
            case Tokens.PLUS: walkPlus(root); break;
            case Tokens.MINUS: walkSubtractionNode(root); break;
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

    private Symbol walkVariableDeclaration(VariableDeclaration node){
        System.out.println(node.toStringNode());
        BuiltInTypeSymbol type = new BuiltInTypeSymbol(node.type.token.text); // get type name
        VariableSymbol variableSymbol = new VariableSymbol(node.id.token.text, type, null, currentScope);
        currentScope.define(variableSymbol); // define symbol in the current scope
        node.id.symbol = variableSymbol; // map var id field to variable symbol
        if(node.expression != null) walk(node.expression);
        return variableSymbol;
    }

    private void walkID(LayanAST node){
        String name = node.getClass().getTypeName();
        if(name == ID.class.getTypeName()){
            //
        }else if(name == ObjectDeclaration.class.getTypeName()){
            walkObjectDeclaration((ObjectDeclaration) node);
        }
    }

    private void walkVariableDeclarationList(VariableDeclarationList node){
        for(VariableDeclaration item: node.variableDeclarations)
            walkVariableDeclaration(item);
    }

    private void walkMethodDeclaration(MethodDeclaration node){
        System.out.println(node.toStringNode());
        MethodSymbol methodSymbol = new MethodSymbol(node.id.name.text, null, currentScope, node);
        currentScope.define(methodSymbol);
        // push scope(parameters scope) in the tree scope
        currentScope = methodSymbol;
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
        ClassSymbol classSymbol = new ClassSymbol(node.id.text, null, currentScope, node);
        // define class symbol in the current scope
        currentScope.define(classSymbol);
        // push the class scope in the scope tree
        currentScope = classSymbol;
        walk(node.block);
        System.out.println(currentScope);
        // pop the current scope(class scope)
        currentScope = classSymbol.getEnclosingScope();
    }

    private void walkObjectDeclaration(ObjectDeclaration node){
        System.out.println(node.toStringNode());
    }

    private void walkPlus(LayanAST node){
        String name = node.getClass().getTypeName();
        if(name.compareTo(AddNode.class.getTypeName()) == 0){
            walkAddNode((AddNode) node);
        }else{
            walkUnaryPositive((UnaryPositive) node);
        }
    }

    private void walkAddNode(AddNode addNode){
        if(addNode == null) return;
        System.out.println(addNode.toStringNode());
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

    private void walkUnaryPositive(UnaryPositive unaryPositive){
        System.out.println(unaryPositive);
    }

    private void walkUnaryNegative(UnaryNegative unaryNegative){
        System.out.println(unaryNegative);
    }

    private void walkConditionNode(ConditionNode node){
        System.out.println(node.toStringNode());
    }

    private void walkIterationNode(IterationNode node){
        System.out.println(node.toStringNode());
    }
}