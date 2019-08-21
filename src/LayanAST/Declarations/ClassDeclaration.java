package LayanAST.Declarations;

import LayanAST.Expressions.ExprNode;
import LayanAST.LayanAST;
import Tokens.Token;

public class ClassDeclaration extends LayanAST {
    Token id;
    BlockNode block;

    public ClassDeclaration(Token t, Token id, BlockNode blockNode) {
        super(t);
        this.id = id;
        block = blockNode;
    }
}
