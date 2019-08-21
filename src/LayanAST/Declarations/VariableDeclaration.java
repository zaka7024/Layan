package LayanAST.Declarations;

import LayanAST.Expressions.ExprNode;
import LayanAST.LayanAST;
import Tokens.Token;

public class VariableDeclaration extends LayanAST {
    private Token type, id;
    private ExprNode expression;

    public VariableDeclaration(Token t, Token id) {
        super(t);
        type = t;
        this.id = id;
    }

    public VariableDeclaration(Token t, Token id, ExprNode exprNode) {
        super(t);
        type = t;
        this.id = id;
        expression = exprNode;
    }
}
