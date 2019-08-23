package LayanAST.Declarations;

import LayanAST.Expressions.ExprNode;
import LayanAST.LayanAST;
import Symbols.Symbol;
import Tokens.Token;

public class ID extends ExprNode {
    public Token name;
    public Symbol symbol;
    public ID(Token t) {
        super(t);
        name = t;
    }

    @Override
    public String toStringNode() {
        return name.text;
    }
}
