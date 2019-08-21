package LayanAST.Expressions;

import Tokens.Token;

public class StringNode extends ExprNode {
    StringNode(Token t) {
        super(t);
        evalType = stringType;
    }
}
