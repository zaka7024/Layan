package LayanAST.Expressions;

import Symbols.BuiltInTypeSymbol;
import Tokens.Token;

public class BoolNode extends ExprNode {
    public BoolNode(Token t) {
        super(t);
        evalType = new BuiltInTypeSymbol("bool");
    }

    @Override
    public String toStringNode() {
        return token.text;
    }
}
