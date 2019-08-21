package LayanAST.Expressions;

import Tokens.Token;

public class StringNode extends ExprNode {
    public StringNode(Token t) {
        super(t);
        evalType = stringType;
    }

    @Override
    public String toStringNode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("( ");
        stringBuilder.append(token.text);
        stringBuilder.append(" )");
        return stringBuilder.toString();
    }
}
