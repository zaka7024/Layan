package LayanAST.Expressions;

import Tokens.Token;

public class EqualityNode extends ExprNode {
    ExprNode left, right;
    EqualityNode(ExprNode l, Token t, ExprNode r) {
        super(t);
        left = l;
        right = r;
    }
}
