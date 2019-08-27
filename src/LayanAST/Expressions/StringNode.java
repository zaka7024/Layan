package LayanAST.Expressions;

import Symbols.BuiltInTypeSymbol;
import Tokens.Token;

public class StringNode extends ExprNode {
    public StringNode(Token t) {
        super(t);
        evalType = new BuiltInTypeSymbol("string");
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
