package LayanAST.Expressions;

import LayanAST.LayanAST;
import Tokens.Token;

public class AddNode extends ExprNode {
    private ExprNode left, right;
    public AddNode(ExprNode l, Token t, ExprNode r) {
        super(t);
        left = l;
        right = r;
    }
}
