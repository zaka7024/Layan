package LayanAST.Declarations;

import LayanAST.LayanAST;
import Tokens.Token;

public class ReturnNode extends LayanAST {
    public ReturnNode(Token t) {
        super(t);
    }

    @Override
    public String toStringNode() {
        return "return statement";
    }
}
