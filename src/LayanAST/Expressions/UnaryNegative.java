package LayanAST.Expressions;

import Tokens.Token;

public class UnaryNegative extends ExprNode {
    public ExprNode expression;
    public UnaryNegative(Token t, ExprNode exprNode) {
        super(t);
        expression = exprNode;
    }

    @Override
    public String toStringNode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("( - ");
        stringBuilder.append(expression.toStringNode());
        stringBuilder.append(" )");
        stringBuilder.append(" )");
        stringBuilder.append("-> ");
        stringBuilder.append(evalType);
        return stringBuilder.toString();
    }
}
