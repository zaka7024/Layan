package LayanAST;

import Tokens.Token;

public abstract class LayanAST{
    private Token token; // Token represent the ast node

    public LayanAST(Token t){
        token = t;
    }
}
