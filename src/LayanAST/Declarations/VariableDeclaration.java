package LayanAST.Declarations;

import LayanAST.Expressions.ExprNode;
import LayanAST.LayanAST;
import Tokens.Token;

public class VariableDeclaration extends LayanAST {
    public ID type, id;
    public ExprNode expression;

    public VariableDeclaration(ID t, ID id) {
        super(t.name);
        type = t;
        this.id = id;
    }

    public VariableDeclaration(ID t, ID id, ExprNode exprNode) {
        super(t.name);
        type = t;
        this.id = id;
        expression = exprNode;
    }

    @Override
    public String toStringNode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("( ");
        stringBuilder.append(type.name.text + " ");
        stringBuilder.append(id.name.text + " ");
        stringBuilder.append(expression.toStringNode());
        stringBuilder.append(" )");
        return stringBuilder.toString();
    }
}
