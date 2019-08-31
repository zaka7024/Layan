package LayanAST.Expressions;

import Symbols.BuiltInTypeSymbol;
import Symbols.Type;
import Tokens.Token;

public class BoolNode extends ExprNode {

    public BoolNode(Token t) {
        super(t);
        evalType = new BuiltInTypeSymbol("bool");
        ((BuiltInTypeSymbol) evalType).typeIndex = 1;
    }

    @Override
    public String toStringNode() {
        return token.text;
    }
}
