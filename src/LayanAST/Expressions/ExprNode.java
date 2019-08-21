package LayanAST.Expressions;

import LayanAST.LayanAST;
import Tokens.Token;

public abstract class ExprNode extends LayanAST {

    final int intType = 1;
    final int floatType = 2;
    final int stringType = 3;
    final int boolType = 4;

    int evalType;

    ExprNode(Token t){
        super(t);
    }

    public int getEvalType() {
        return evalType;
    }
}
