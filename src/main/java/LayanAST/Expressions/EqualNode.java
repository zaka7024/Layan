package LayanAST.Expressions;

import LayanAST.Declarations.ID;
import Tokens.Token;

public class EqualNode extends ExprNode {
    public ExprNode expression;
    public ID id;
    public ResolutionObject member; // assign to object member
    public EqualNode(ID id, Token t, ExprNode expr) {
        super(t);
        this.id = id;
        expression = expr;
    }

    @Override
    public String toStringNode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("( = ");
        stringBuilder.append(id.token.text + " ");
        stringBuilder.append(expression.toStringNode());
        stringBuilder.append(" )");
        return stringBuilder.toString();
    }
}
