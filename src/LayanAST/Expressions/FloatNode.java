package LayanAST.Expressions;

import Symbols.BuiltInTypeSymbol;
import Tokens.Token;

public class FloatNode extends ExprNode {
    public FloatNode(Token t) {
        super(t);
        evalType = new BuiltInTypeSymbol("float");
    }

    @Override
    public String toStringNode() {
        return token.text;
    }
}
