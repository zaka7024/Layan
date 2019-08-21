package LayanAST.Expressions;

import Tokens.Token;

public class IntNode extends ExprNode {
    IntNode(Token t) {
        super(t);
        evalType = intType;
    }
}
