package LayanAST.Expressions;

import Tokens.Token;

public class SubtractionNode extends ExprNode {
    private ExprNode left, right;
    SubtractionNode(ExprNode l, Token t, ExprNode r) {
        super(t);
        left = l;
        right = r;
    }
}
