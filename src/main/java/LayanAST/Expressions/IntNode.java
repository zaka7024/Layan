package LayanAST.Expressions;

import Symbols.BuiltInTypeSymbol;
import Symbols.Type;
import Tokens.Token;

public class IntNode extends ExprNode {

    public IntNode(Token t) {
        super(t);
        evalType = new BuiltInTypeSymbol("int");
        ((BuiltInTypeSymbol) evalType).typeIndex = 3;
    }

    @Override
    public String toStringNode() {
        return token.text;
    }
}
