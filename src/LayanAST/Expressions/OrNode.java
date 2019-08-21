package LayanAST.Expressions;

import Tokens.Token;

public class OrNode extends ExprNode {
    ExprNode left, right;
    OrNode(ExprNode l, Token t, ExprNode r) {
        super(t);
        left = l;
        right = r;
    }
}
