package LayanAST.Expressions;

import Tokens.Token;

public class BoolNode extends ExprNode {
    public BoolNode(Token t) {
        super(t);
        evalType = boolType;
    }

    @Override
    public String toStringNode() {
        return token.text;
    }
}
