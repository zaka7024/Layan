package LayanAST.Declarations;

import LayanAST.Expressions.ExprNode;

public class ArrayDeclaration extends ExprNode {
    public ID name, type;
    public ExprNode size; // elements count

    public ArrayDeclaration(ID name, ID type, ExprNode size) {
        super(name.token);
        this.name = name;
        this.type = type;
        this.size = size;
    }
}
