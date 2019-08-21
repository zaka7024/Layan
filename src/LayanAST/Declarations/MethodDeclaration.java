package LayanAST.Declarations;

import LayanAST.Expressions.ExprNode;
import LayanAST.LayanAST;
import Tokens.Token;

public class MethodDeclaration extends LayanAST {
    Token id;
    BlockNode block;

    public MethodDeclaration(Token t, Token id, BlockNode blockNode) {
        super(t);
        this.id = id;
        block = blockNode;
    }
}
