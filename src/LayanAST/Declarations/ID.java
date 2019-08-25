package LayanAST.Declarations;

import LayanAST.Expressions.ExprNode;
import LayanAST.LayanAST;
import Symbols.Scope;
import Symbols.Symbol;
import Symbols.Type;
import Tokens.Token;

public class ID extends ExprNode {
    public Token name;
    public Symbol symbol;
    public Scope scope;
    public Type type;

    public ID(Token t) {
        super(t);
        name = t;
    }

    @Override
    public String toStringNode() {
        return name.text;
    }
}
