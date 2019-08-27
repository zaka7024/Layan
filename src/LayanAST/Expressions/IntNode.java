package LayanAST.Expressions;

import Symbols.BuiltInTypeSymbol;
import Tokens.Token;

public class IntNode extends ExprNode {
    public IntNode(Token t) {
        super(t);
        evalType = new BuiltInTypeSymbol("int");
    }

    @Override
    public String toStringNode() {
        return token.text;
    }
}
