package Symbols;

import LayanAST.Declarations.ID;
import LayanAST.Declarations.VariableDeclaration;
import LayanAST.Expressions.ExprNode;
import LayanAST.Expressions.NotNode;
import LayanAST.LayanAST;

public class SymbolTable {

    public static final int VOID = -1;
    public static final int userType = 0; // type for classes
    public static final int intType = 1;
    public static final int floatType = 2;
    public static final int stringType = 3;
    public static final int booleanType = 4;

    static final Type _int = new BuiltInTypeSymbol("int");
    static final Type _boolean = new BuiltInTypeSymbol("bool");
    static final Type _string = new BuiltInTypeSymbol("string");
    static final Type _float = new BuiltInTypeSymbol("float");

    public static Symbol resolve(LayanAST node){
        Symbol symbol = ((ID)node).scope.resolve(((ID) node).name.text);
        if(symbol.def == null){
            System.out.println("must be predefined symbol");
        }
        int refPosition = node.token.tokenIndex;
        int defPosition = ((VariableDeclaration)symbol.def).id.token.tokenIndex;
        if(refPosition < defPosition && ((ID) node).scope instanceof BaseScope
        && symbol.scope instanceof BaseScope) throw new Error(symbol.name + " must be defined");
        return symbol;
    }

    public static Type bop(ExprNode a, ExprNode b){
        return a.evalType;
    }

    public static Type relop(ExprNode a, ExprNode b){
        return _boolean;
    }

    public static Type eqop(ExprNode a, ExprNode b){
        return _boolean;
    }

    public static Type unaryNot(NotNode a){
        return _boolean;
    }

    public static Type unaryMinus(ExprNode a){
        return a.evalType;
    }
}
