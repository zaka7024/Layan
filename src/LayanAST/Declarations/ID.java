package LayanAST.Declarations;

import LayanAST.Expressions.ExprNode;
import LayanAST.LayanAST;
import Tokens.Token;

public class ID extends ExprNode {
    Token name;
    public ID(Token t) {
        super(t);
        name = t;
    }

    @Override
    public String toStringNode() {
        return name.text;
    }
}
