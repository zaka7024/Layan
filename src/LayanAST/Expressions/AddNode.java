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

    @Override
    public String toStringNode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("( + ");
        stringBuilder.append(left.toStringNode() + " ");
        stringBuilder.append(right.toStringNode());
        stringBuilder.append(" )");
        return stringBuilder.toString();
    }
}
