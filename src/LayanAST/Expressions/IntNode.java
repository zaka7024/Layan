package LayanAST.Expressions;

import Tokens.Token;

public class IntNode extends ExprNode {
    public IntNode(Token t) {
        super(t);
        evalType = intType;
    }

    @Override
    public String toStringNode() {
        return token.text;
    }
}
