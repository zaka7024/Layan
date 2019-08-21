package LayanAST.Expressions;

import Tokens.Token;

public class AndNode extends ExprNode {
    ExprNode left, right;
    AndNode(ExprNode l, Token t, ExprNode r) {
        super(t);
        left = l;
        right = r;
    }
}
