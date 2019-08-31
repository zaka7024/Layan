package LayanAST.Expressions;

import Symbols.BuiltInTypeSymbol;
import Symbols.Type;
import Tokens.Token;

public class FloatNode extends ExprNode {

    public FloatNode(Token t) {
        super(t);
        evalType = new BuiltInTypeSymbol("float");
        ((BuiltInTypeSymbol) evalType).typeIndex = 4;
    }

    @Override
    public String toStringNode() {
        return token.text;
    }
}
