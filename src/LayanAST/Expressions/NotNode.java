package LayanAST.Expressions;

import Tokens.Token;

public class NotNode extends ExprNode{
    public ExprNode expression;
    public NotNode(Token t, ExprNode expr) {
        super(t);
        expression = expr;
    }

    @Override
    public String toStringNode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("( ! ");
        stringBuilder.append(expression.toStringNode());
        stringBuilder.append(" )");
        stringBuilder.append(" )");
        stringBuilder.append("-> ");
        stringBuilder.append(evalType);
        return stringBuilder.toString();
    }
}
