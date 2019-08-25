package LayanAST;

import LayanAST.Conditions.ConditionNode;
import LayanAST.Conditions.IterationNode;
import LayanAST.Declarations.*;
import LayanAST.Expressions.*;
import Symbols.*;
import Tokens.Tokens;

public class ASTVisitorResolve {

    public ASTVisitorResolve(LayanAST root){
        walk(root);
    }

    private void walk(LayanAST root){
        switch (root.token.type){
            case Tokens.PROGRAM: walkProgram((Program) root); break;
            case Tokens.TYPE: walkVariableDeclarationList((VariableDeclarationList) root); break;
            case Tokens.ID: walkID(root); break;
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
        }
    }

    private void walkProgram(Program program){
        for (LayanAST node: program.statements){
            walk(node);
        }
    }

    private void walkBlock(BlockNode blockNode){
        for(LayanAST node: blockNode.layanASTList){
            walk(node);
        }
    }

    private void walkVariableDeclaration(VariableDeclaration node){
        Symbol symbol = node.id.scope.resolve(node.type.name.text);
        node.type.symbol = symbol;
        node.id.symbol.type = (Type) node.id.scope.resolve(node.type.name.text);
        System.out.println("ref: " + (node.id.name.text + " -> " + (node.id.symbol)));
        if(node.expression != null) walk(node.expression);
    }

    private void walkAssignment(EqualNode node){
        Symbol symbol = node.id.scope.resolve(node.id.name.text);
        node.id.symbol = symbol;
        walk(node.id);
        walk(node.expression);
    }

    private void walkResolutionObject(ResolutionObject node){
        ObjectSymbol objectSymbol = (ObjectSymbol) node.type.scope.resolve(node.type.name.text);
        Symbol member = ((ClassSymbol)objectSymbol.type).scope.resolveMember(node.member.name.text);
        System.out.println("ref: " + (node.member.name.text + " -> " + (member)));
    }

    private void walkObjectDeclaration(ObjectDeclaration node){
        System.out.println(node.toStringNode());
        node.type.symbol = node.id.scope.resolve(node.type.name.text);
        node.id.symbol.type = (Type)node.id.scope.resolve(node.type.name.text);
    }

    private void walkID(LayanAST node){
        if(node instanceof ID){
            ((ID)node).symbol = ((ID)node).scope.resolve(((ID) node).name.text);
            System.out.println("ref: " + ((ID) node).name.text + " -> " + ((ID) node).symbol);
        }else if(node instanceof ObjectDeclaration){
            walkObjectDeclaration((ObjectDeclaration) node);
        }else if(node instanceof ResolutionObject){
            walkResolutionObject((ResolutionObject) node);
        }
    }

    private void walkVariableDeclarationList(VariableDeclarationList node){
        for(VariableDeclaration item: node.variableDeclarations)
            walkVariableDeclaration(item);
    }

    private void walkMethodDeclaration(MethodDeclaration node){
        walk(node.block);
    }

    private void walkClassDeclaration(ClassDeclaration node){
        if(node.superClass != null){
            ((ClassSymbol)node.id.symbol).superClass =
                    (ClassSymbol) node.id.scope.resolve(node.superClass.name.text);
        }
        walk(node.block);
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
