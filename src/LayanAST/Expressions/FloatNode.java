package LayanAST.Expressions;

import Tokens.Token;

public class FloatNode extends ExprNode {
    FloatNode(Token t) {
        super(t);
        evalType = floatType;
    }

    @Override
    public String toStringNode() {
        return token.text;
    }
}
