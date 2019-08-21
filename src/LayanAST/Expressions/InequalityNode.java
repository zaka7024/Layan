package LayanAST.Expressions;

import Tokens.Token;

public class InequalityNode extends ExprNode {
    ExprNode left, right;
    InequalityNode(ExprNode l, Token t, ExprNode r) {
        super(t);
        left = l;
        right = r;
    }
}
