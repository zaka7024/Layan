package LayanAST;
import LayanAST.Conditions.ConditionNode;
import LayanAST.Conditions.IterationNode;
import LayanAST.Declarations.*;
import LayanAST.Expressions.*;
import Symbols.*;
import Tokens.Tokens;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ASTVisitorDefine {

    private Scope currentScope; // represent the current scope in the scope tree

    public ASTVisitorDefine(LayanAST root){
        walk(root);
        System.out.println(currentScope);
    }

    private void walk(LayanAST root){
        switch (root.token.type){
            case Tokens.PROGRAM: walkProgram((Program) root); break;
            case Tokens.TYPE: walkDeclarations(root); break;
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
        // define all args
        for(LayanAST arg: node.args){
            walk(arg);
        }
    }

    private List<Symbol> walkDeclarations(LayanAST node){
        if(node instanceof ObjectDeclaration) return Collections.singletonList(walkObjectDeclaration((ObjectDeclaration) node));
        else if(node instanceof VariableDeclarationList) return walkVariableDeclarationList((VariableDeclarationList) node);
        return Collections.singletonList(walkVariableDeclaration((VariableDeclaration) node));
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
        }else if(node instanceof ArrayAccess){
            walkArrayAccess((ArrayAccess) node);
        }
    }

    private void walkArrayAccess(ArrayAccess node){
        walk(node.name);
        walk(node.index);
    }

    private void walkPrint(Print print){
        walk(print.exprNode);
    }

    private void walkAssignment(EqualNode node){
        node.id.scope = currentScope;
        if(node.member != null && node.member.member != null){
            walkResolutionObject(node.member);
        }
        walk(node.expression);
    }

    private List<Symbol> walkVariableDeclarationList(VariableDeclarationList node){
        List<Symbol> symbols = new ArrayList<Symbol>();
        for(VariableDeclaration item: node.variableDeclarations)
            symbols.add(walkVariableDeclaration(item));
        return symbols;
    }

    private void walkResolutionObject(ResolutionObject node){
        node.type.scope = currentScope;
        node.member.scope = currentScope;
    }

    private Symbol walkObjectDeclaration(ObjectDeclaration node){
        System.out.println(node.toStringNode());

        Type type = (ClassSymbol) currentScope.resolve(node.type.name.text);

        if(type == null){
            throw new Error("Undefined type " + node.type.name.text);
        }

        ObjectSymbol objectSymbol = new ObjectSymbol(node.id.name.text,type,
                node, currentScope);

        currentScope.define(objectSymbol);
        // Map the id with object symbol
        node.id.symbol = objectSymbol;
        // set the scope for id the current scope
        node.id.scope = currentScope;
        // set object type to a user define type
        objectSymbol.evalType = new BuiltInTypeSymbol("void");

        // Define object args if there is
        if(node.args != null){
            for (LayanAST ast: node.args
            ) {
                walk(ast);
            }
        }

        return objectSymbol;
    }

    private void walkMethodDeclaration(MethodDeclaration node){
        System.out.println(node.toStringNode());
        MethodSymbol methodSymbol = new MethodSymbol(node.id.name.text,
                new BuiltInTypeSymbol("void"), currentScope, node);
        currentScope.define(methodSymbol);
        // push scope(parameters scope) in the tree scope
        currentScope = methodSymbol;
        // map node's id to the method symbol
        node.id.symbol = methodSymbol;
        node.id.scope = currentScope;
        // define all parameters in the parameters scope
        for (LayanAST arg: node.parameters){
            List<Symbol> symbols = walkDeclarations(arg);
            for(Symbol item: symbols)
                currentScope.define(item);
        }
        // push local scope
        currentScope = new LocalScope(currentScope);

        walk(node.block);

        // set the block to this method symbol
        methodSymbol.functionBlock = node.block;

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
        if(node instanceof AddNode){
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
        if(node instanceof SubtractionNode){
            walkSubtractionNode((SubtractionNode) node);
        }else{
            walkUnaryNegative((UnaryNegative) node);
        }
    }

    private void walkSubtractionNode(SubtractionNode node){
        if(node == null) return;
        walk(node.left);
        walk(node.right);
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
        walk(node.truePart);
        if(node.falsePart != null) walk(node.falsePart);
    }

    private void walkIterationNode(IterationNode node){
        System.out.println(node.toStringNode());
        walkVariableDeclaration(node.forVariable);
        walk(node.expression);
        walk(node.assignment);
        walk(node.blockNode);
    }
}
