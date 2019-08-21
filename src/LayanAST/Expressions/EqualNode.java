package LayanAST.Expressions;

import Tokens.Token;

public class EqualNode extends ExprNode {
    private ExprNode left, right;
    public EqualNode(ExprNode l, Token t, ExprNode r) {
        super(t);
        left = l;
        right = r;
    }
}
