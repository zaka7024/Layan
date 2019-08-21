package LayanAST.Expressions;

import Tokens.Token;

public class MoreThanNode extends ExprNode {
    ExprNode left, right;
    MoreThanNode(ExprNode l, Token t, ExprNode r) {
        super(t);
        left = l;
        right = r;
    }
}
