package LayanAST;

import Tokens.Token;

public abstract class LayanAST{
    public Token token; // Token represent the ast node

    public LayanAST(Token t){
        token = t;
    }

    public abstract String toStringNode();
}
