package LayanAST.Expressions;

import Tokens.Token;

public class ModulusNode extends ExprNode{

    private ExprNode left, right;
    ModulusNode(ExprNode l, Token t, ExprNode r) {
        super(t);
        left = l;
        right = r;
    }
}
