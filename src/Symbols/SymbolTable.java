package Symbols;

import LayanAST.Conditions.ConditionNode;
import LayanAST.Declarations.FunctionCall;
import LayanAST.Declarations.ID;
import LayanAST.Declarations.VariableDeclaration;
import LayanAST.Expressions.ExprNode;
import LayanAST.Expressions.NotNode;
import LayanAST.Expressions.ResolutionObject;
import LayanAST.LayanAST;
import com.sun.org.apache.regexp.internal.RE;

public class SymbolTable {

    public static final int userType = 0; // type for classes
    public static final int booleanType = 1;
    public static final int stringType = 2;
    public static final int intType = 3;
    public static final int floatType = 4;
    public static final int VOID = 5;

    static final Type _int = new BuiltInTypeSymbol("int");
    static final Type _boolean = new BuiltInTypeSymbol("bool");
    static final Type _string = new BuiltInTypeSymbol("string");
    static final Type _float = new BuiltInTypeSymbol("float");
    static final Type _void = new BuiltInTypeSymbol("void");

    public static Type[][] arithmeticResultType = new Type[][]{
                    /*  class         boolean     string      int         float       void*/
            /*class*/   {_void,     _void,      _void,      _void,      _void,      _void},
            /*boolean*/ {_void,     _void,      _void,      _void,      _void,      _void},
            /*string*/  {_void,     _void,      _string,    _int,       _float,     _void},
            /*int*/     {_void,     _void,      _int,       _int,       _float,     _void},
            /*float*/   {_void,     _void,      _float,     _float,     _float,     _void},
            /*void*/    {_void,     _void,      _void,      _void,      _void,      _void}
    };

    public static Type[][] comparisonType = new Type[][]{
            /*class boolean string int float void*/
            /*class*/ {_boolean, _boolean, _boolean, _boolean, _boolean, _boolean},
            /*boolean*/ {_boolean, _boolean, _boolean, _boolean, _boolean, _boolean},
            /*string*/ {_boolean, _boolean, _boolean, _boolean, _boolean, _boolean},
            /*int*/ {_boolean, _boolean, _boolean, _boolean, _boolean, _boolean},
            /*float*/ {_boolean, _boolean, _boolean, _boolean, _boolean, _boolean},
            /*void*/ {_boolean, _boolean, _boolean, _boolean, _boolean, _boolean}
    };

    public static Type[][] promoteFromTo = new Type[][]{
                    /*  class         boolean     string      int         float       void*/
            /*class*/   {null,     null,      null,      null,          null,      null},
            /*boolean*/ {null,     null,      null,      null,      null,      null},
            /*string*/  {null,     null,      null,    _int,       _float,     null},
            /*int*/     {null,     null,      null,       null,       _float,     null},
            /*float*/   {null,     null,      null,     null,     null,     null},
            /*void*/   {null,     null,      null,     null,     null,     null}
    };

    public Symbol resolve(LayanAST node){
        Symbol symbol = ((ID)node).scope.resolve(((ID) node).name.text);
        if(symbol.def == null){
            System.out.println("must be predefined symbol");
        }
        int refPosition = node.token.tokenIndex;
        int defPosition = ((Symbol)symbol).def.token.tokenIndex;
        if(refPosition < defPosition && ((ID) node).scope instanceof BaseScope
        && symbol.scope instanceof BaseScope) throw new Error(symbol.name + " must be defined");
        return symbol;
    }

    public Type getResultType(Type [][] table, ExprNode a, ExprNode b){
        int aIndex = ((BuiltInTypeSymbol)a.evalType).typeIndex;
        int bIndex = ((BuiltInTypeSymbol)b.evalType).typeIndex;

        a.promoteToType = promoteFromTo[aIndex][bIndex];
        b.promoteToType = promoteFromTo[aIndex][bIndex];

        Type result = table[aIndex][bIndex];
        if(result == _void){
            throw new Error(a.evalType.getTypeName() + ", " + b.evalType.getTypeName() + " have incompatible types");
        }

        return table[aIndex][bIndex];
    }

    public boolean canAssign(Type mainType, Type destinationType,
                             Type promotionType){
        return ((BuiltInTypeSymbol)mainType).compareTo((BuiltInTypeSymbol)destinationType) == 0
                || ((BuiltInTypeSymbol)mainType).compareTo((BuiltInTypeSymbol)promotionType) == 0;
    }

    public void call(FunctionCall functionCall){
        // promote args type using the actual parameters
        int i = 0;
        MethodSymbol methodSymbol = ((MethodSymbol)resolve(functionCall.id));
        for (Symbol para: methodSymbol.parameters.values()){
            ExprNode arg = functionCall.args.get(i);
            Type actualType = para.evalType;
            Type argsType = arg.evalType;

            int actualTypeIndex = ((BuiltInTypeSymbol) actualType).typeIndex;
            int argsTypeIndex = ((BuiltInTypeSymbol)argsType).typeIndex;

            // check if we need to promote the arg type to defined type
            arg.promoteToType = promoteFromTo[argsTypeIndex][actualTypeIndex];

            // check if can assign the args to actual para
            if(!canAssign(actualType, argsType, arg.promoteToType)){
                throw new Error(actualType.getTypeName() + ", " +
                        arg.evalType.getTypeName() + " have incompatible types");
            }
        }
    }

    public Type bop(ExprNode a, ExprNode b){
        return getResultType(arithmeticResultType, a,b);
    }

    public Type relop(ExprNode a, ExprNode b){
        return getResultType(comparisonType, a, b);
    }

    public Type eqop(ExprNode a, ExprNode b){
        return getResultType(comparisonType, a, b);
    }

    public void assign(ExprNode left, ExprNode right){
        int li = ((BuiltInTypeSymbol)left.evalType).typeIndex;
        int ri = ((BuiltInTypeSymbol)right.evalType).typeIndex;
        right.promoteToType = promoteFromTo[ri][li];
        // check compatible types
        if(!canAssign(left.evalType, right.evalType, right.promoteToType)){
            throw new Error((left.evalType.getTypeName())+ ", "
            + right.evalType.getTypeName() + " have incompatible types");
        }
    }

    public void memberAccess(ResolutionObject resolutionObject){
        if(resolutionObject.type.type.getClass() != ClassSymbol.class){
           throw new Error(resolutionObject.type.name.text + "must have class type");
        }
    }

    public void condition(ConditionNode conditionNode){
        if(conditionNode.expression.evalType != _boolean){
            throw new Error("condition statements must have boolean type");
        }
    }

    public Type unaryNot(NotNode a){
        // check if there is a void type
        if(a.evalType != _boolean){
            throw new Error(a.token.text + " must be a boolean");
        }
        return _boolean;
    }

    public Type unaryMinus(ExprNode a){
        if(!(a.evalType == _int || a.evalType == _float)){
            throw new Error(a.token.text + " must have int/float type");
        }
        return a.evalType;
    }
}
