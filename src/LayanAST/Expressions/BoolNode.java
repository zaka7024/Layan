package LayanAST.Expressions;

import Tokens.Token;

public class BoolNode extends ExprNode {
    BoolNode(Token t) {
        super(t);
        evalType = boolType;
    }
}
