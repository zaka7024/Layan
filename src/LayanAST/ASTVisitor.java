package LayanAST;

import LayanAST.Conditions.ConditionNode;
import LayanAST.Declarations.*;
import LayanAST.Expressions.AddNode;
import LayanAST.Expressions.SubtractionNode;
import LayanAST.Expressions.UnaryNegative;
import LayanAST.Expressions.UnaryPositive;
import Tokens.Tokens;

public class ASTVisitor {

    public ASTVisitor(LayanAST root){
        walk(root);
    }

    private void walk(LayanAST root){
        switch (root.token.type){
            case Tokens.TYPE: walkVariableDeclaration((VariableDeclaration) root); break;
            case Tokens.ID: walkID((ID) root); break;
            case Tokens.IF: walkConditionNode((ConditionNode) root); break;
            case Tokens.CLASS: walkClassDeclaration((ClassDeclaration) root); break;
            case Tokens.FUNCTION: walkMethodDeclaration((MethodDeclaration) root); break;
            case Tokens.OPENCARLYBRACKET: walkBlock((BlockNode) root); break;
            case Tokens.PLUS: walkAddNode((AddNode) root); break;
            case Tokens.MINUS: walkSubtractionNode((SubtractionNode) root); break;
        }
    }

    private void walkBlock(BlockNode blockNode){
        for(LayanAST node: blockNode.layanASTList){
            walk(node);
        }
    }

    private void walkVariableDeclaration(VariableDeclaration node){
        System.out.println("walkVariableDeclaration");
        System.out.println(node.toStringNode());
        walk(node.expression);
    }

    private void walkID(ID node){
        //
    }

    private void walkMethodDeclaration(MethodDeclaration node){
        System.out.println(node.toStringNode());
        walk(node.block);
    }

    private void walkClassDeclaration(ClassDeclaration node){
        System.out.println("walkClassDeclaration");
        walk(node.block);
    }

    private void walkAddNode(AddNode addNode){
        if(addNode == null) return;
        System.out.println("walkAddNode");
        System.out.println(addNode.toStringNode());
    }

    private void walkSubtractionNode(SubtractionNode addNode){
        if(addNode == null) return;
        System.out.println("walkSubtractionNode");
        System.out.println(addNode.toStringNode());
    }

    private void walkUnaryPositive(UnaryPositive unaryPositive){
        System.out.println(unaryPositive);
    }

    private void walkUnaryNegative(UnaryNegative unaryNegative){
        System.out.println(unaryNegative);
    }

    private void walkConditionNode(ConditionNode node){
        System.out.println("walkConditionNode");
        System.out.println(node.toStringNode());
    }
}
