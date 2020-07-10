package LayanAST;

import LayanAST.Expressions.ExprNode;
import Tokens.Token;

public class Print extends LayanAST {
    public ExprNode exprNode;
    public Print(Token t, ExprNode expr) {
        super(t);
        exprNode = expr;
    }

    @Override
    public String toStringNode() {
        return "Print";
    }
}
