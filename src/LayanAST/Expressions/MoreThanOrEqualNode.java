package LayanAST.Expressions;

import Tokens.Token;

public class MoreThanOrEqualNode extends ExprNode{
    ExprNode left, right;
    MoreThanOrEqualNode(ExprNode l, Token t, ExprNode r) {
        super(t);
        left = l;
        right = r;
    }
}
