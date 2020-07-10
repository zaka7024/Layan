package LayanAST;

import Tokens.Token;

public class EOF extends LayanAST {
    public EOF(Token t) {
        super(t);
    }

    @Override
    public String toStringNode() {
        return "EOF";
    }
}
