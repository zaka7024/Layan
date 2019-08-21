package LayanAST.Expressions;

import Tokens.Token;

public class MultiplicationNode extends ExprNode{
    private ExprNode left, right;
    MultiplicationNode(ExprNode l, Token t, ExprNode r) {
        super(t);
        left = l;
        right = r;
    }
}
