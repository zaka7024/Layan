package LayanAST.Expressions;

import Tokens.Token;

public class LessThanOrEqualNode extends ExprNode{
    private ExprNode left, right;
    LessThanOrEqualNode(ExprNode l, Token t, ExprNode r) {
        super(t);
        left = l;
        right = r;
    }
}
