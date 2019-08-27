package LayanAST.Expressions;

import LayanAST.LayanAST;
import Symbols.Type;
import Tokens.Token;

public abstract class ExprNode extends LayanAST {

    public final static int intType = 1;
    public final static int floatType = 2;
    public final static int stringType = 3;
    public final static int boolType = 4;

    public Type evalType;

    public ExprNode(Token t){
        super(t);
    }

    @Override
    public String toStringNode() {
        return "";
    }
}
