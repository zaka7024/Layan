package LayanAST.Expressions;

import Tokens.Token;

public class LessThanNode extends ExprNode {
    ExprNode left, right;
    LessThanNode(ExprNode l, Token t, ExprNode r) {
        super(t);
        left = l;
        right = r;
    }
}
