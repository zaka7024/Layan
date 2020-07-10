package LayanAST.Expressions;

import Symbols.BuiltInTypeSymbol;
import Symbols.Type;
import Tokens.Token;

public class StringNode extends ExprNode {
    public StringNode(Token t) {
        super(t);
        evalType = new BuiltInTypeSymbol("string");
        ((BuiltInTypeSymbol) evalType).typeIndex = 2;
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
