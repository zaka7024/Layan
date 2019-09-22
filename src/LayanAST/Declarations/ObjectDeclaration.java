package LayanAST.Declarations;

import LayanAST.Expressions.ExprNode;
import LayanAST.LayanAST;

import java.util.List;

public class ObjectDeclaration extends LayanAST {
    public ID type, id;
    public List<ExprNode> args;

    public ObjectDeclaration(ID t, ID id) {
        super(t.name);
        type = t;
        this.id = id;
    }

    @Override
    public String toStringNode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("( ");
        stringBuilder.append(type.name.text + " ");
        stringBuilder.append(id.name.text + " ");
        stringBuilder.append(" )");
        return stringBuilder.toString();
    }
}
