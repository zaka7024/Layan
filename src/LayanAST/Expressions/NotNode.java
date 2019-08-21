package LayanAST.Expressions;

import Tokens.Token;

public class NotNode extends ExprNode{
    private ExprNode expression;
    NotNode(Token t, ExprNode expr) {
        super(t);
        expression = expr;
    }
}
