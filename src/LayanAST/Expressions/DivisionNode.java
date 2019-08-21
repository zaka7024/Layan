package LayanAST.Expressions;

import Tokens.Token;

public class DivisionNode extends ExprNode {
    private ExprNode left, right;
    DivisionNode(ExprNode l, Token t, ExprNode r) {
        super(t);
        left = l;
        right = r;
    }
}
