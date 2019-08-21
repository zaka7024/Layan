package LayanAST.Expressions;

import Tokens.Token;

public class AndNode extends ExprNode {
    public ExprNode left, right;
    public AndNode(ExprNode l, Token t, ExprNode r) {
        super(t);
        left = l;
        right = r;
    }

    @Override
    public String toStringNode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("( && ");
        stringBuilder.append(left.toStringNode() + " ");
        stringBuilder.append(right.toStringNode());
        stringBuilder.append(" )");
        return stringBuilder.toString();
    }
}
