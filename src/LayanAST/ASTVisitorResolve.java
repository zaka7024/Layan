package LayanAST;

import LayanAST.Conditions.ConditionNode;
import LayanAST.Conditions.IterationNode;
import LayanAST.Declarations.*;
import LayanAST.Expressions.*;
import Symbols.*;
import Tokens.Tokens;
import com.sun.org.apache.xpath.internal.operations.And;

public class ASTVisitorResolve {
    
    SymbolTable symbolTable = new SymbolTable();
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
        MethodSymbol methodSymbol = (MethodSymbol) node.id.scope.resolve(node.id.name.text);
        node.id.symbol = methodSymbol;
        System.out.println("ref: " + (node.id.name.text + " -> " + (node.id.symbol)));
        System.out.println(node.toStringNode());
    }

    private void walkVariableDeclaration(VariableDeclaration node){
        // get the variable symbol
        VariableSymbol symbol = (VariableSymbol) node.id.scope.resolve(node.id.name.text);
        // resolve it's type
        symbol.type = (Type) node.id.scope.resolve(node.type.name.text);
        node.type.symbol = node.id.scope.resolve(node.type.name.text);
        System.out.println("ref: " + (node.id.name.text + " -> " + (node.id.symbol)));
        if(node.expression != null){
            walk(node.expression);
            symbolTable.assign(node.id, node.expression);
        }
    }

    private void walkAssignment(EqualNode node){
        Symbol symbol = node.id.scope.resolve(node.id.name.text);
        node.id.symbol = symbol;
        walk(node.id);
        walk(node.expression);
        // promote the expression type to left type
        symbolTable.assign(node.id, node.expression);
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
            ((ID) node).symbol = symbolTable.resolve(node);
            ((ID) node).evalType = symbolTable.resolve(node).type;
            Symbol symbol = (BuiltInTypeSymbol) ((ID) node).evalType;
            System.out.println("ref: " + ((ID) node).name.text + " -> " + ((ID) node).symbol);
        }else if(node instanceof ObjectDeclaration){
            walkObjectDeclaration((ObjectDeclaration) node);
        }else if(node instanceof ResolutionObject){
            walkResolutionObject((ResolutionObject) node);
        }else if(node instanceof FunctionCall){
            walkFunctionCall((FunctionCall) node);
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

    private void walkMultiplicationNode(MultiplicationNode node){
        node.evalType = symbolTable.bop(node.left, node.right);
        walk(node.left);
        walk(node.right);
        System.out.println(node.toStringNode());
    }

    private void walkDivisionNode(DivisionNode node){
        walk(node.left);
        walk(node.right);
        node.evalType = symbolTable.bop(node.left, node.right);
        System.out.println(node.toStringNode());
    }

    private void walkSubtractionNode(SubtractionNode node){
        walk(node.left);
        walk(node.right);
        node.evalType = symbolTable.bop(node.left, node.right);
        System.out.println(node.toStringNode());
    }

    private void walkPlus(LayanAST node){
        if(node instanceof AddNode){
            walkAddNode((AddNode) node);
        }else{
            walkUnaryPositive((UnaryPositive) node);
        }
    }

    private void walkAddNode(AddNode addNode){
        walk(addNode.left);
        walk(addNode.right);
        addNode.evalType = symbolTable.bop(addNode.left, addNode.right);
        System.out.println(addNode.toStringNode());
    }

    private void walkMinus(LayanAST node){
        if(node instanceof SubtractionNode){
            walkSubtractionNode((SubtractionNode) node);
        }else{
            walkUnaryNegative((UnaryNegative) node);
        }
    }

    private void walkAndNode(AndNode node){
        walk(node.left);
        walk(node.right);
        node.evalType = symbolTable.relop(node.left, node.right);
        System.out.println(node.toStringNode());
    }

    private void walkOrNode(OrNode node){
        walk(node.left);
        walk(node.right);
        node.evalType = symbolTable.relop(node.left, node.right);
        System.out.println(node.toStringNode());
    }

    private void walkNotEqualNode(InequalityNode node){
        walk(node.left);
        walk(node.right);
        node.evalType = symbolTable.relop(node.left, node.right);
        symbolTable.eqop(node.right, node.left);
        System.out.println(node.toStringNode());
    }

    private void walkEqualityNode(EqualityNode node){
        walk(node.left);
        walk(node.right);
        node.evalType = symbolTable.relop(node.left, node.right);
        System.out.println(node.toStringNode());
    }

    private void walkLessThanNode(LessThanNode node){
        walk(node.left);
        walk(node.right);
        node.evalType = symbolTable.relop(node.left, node.right);
        System.out.println(node.toStringNode());
    }

    private void walkMoreThanNode(MoreThanNode node){
        walk(node.left);
        walk(node.right);
        node.evalType = symbolTable.relop(node.left, node.right);
        System.out.println(node.toStringNode());
    }

    private void walkMoreThanOrEqualNode(MoreThanOrEqualNode node){
        walk(node.left);
        walk(node.right);
        node.evalType = symbolTable.relop(node.left, node.right);
        System.out.println(node.toStringNode());
    }

    private void walkLessThanOrEqualNode(LessThanOrEqualNode node){
        walk(node.left);
        walk(node.right);
        node.evalType = symbolTable.relop(node.left, node.right);
        System.out.println(node.toStringNode());
    }

    private void walkModulusNode(ModulusNode node){
        walk(node.left);
        walk(node.right);
        node.evalType = symbolTable.relop(node.left, node.right);
        System.out.println(node.toStringNode());
    }

    private void walkNotNode(NotNode node){
        walk(node.expression);
        System.out.println(node.toStringNode());
        node.evalType = symbolTable.unaryNot(node);
    }

    private void walkUnaryPositive(UnaryPositive unaryPositive){
        walk(unaryPositive.expression);
        unaryPositive.evalType = symbolTable.unaryMinus(unaryPositive.expression);
        System.out.println(unaryPositive);
    }

    private void walkUnaryNegative(UnaryNegative unaryNegative){
        walk(unaryNegative.expression);
        unaryNegative.evalType = symbolTable.unaryMinus(unaryNegative.expression);
        System.out.println(unaryNegative);
    }

    private void walkConditionNode(ConditionNode node){
        System.out.println(node.toStringNode());
    }

    private void walkIterationNode(IterationNode node){
        System.out.println(node.toStringNode());
    }
}
