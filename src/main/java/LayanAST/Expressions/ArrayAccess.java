package LayanAST.Expressions;

import LayanAST.Declarations.ID;
import Tokens.Token;

public class ArrayAccess extends ExprNode {
    public ID name;
    public ExprNode index;

    public ArrayAccess(ID name, ExprNode index) {
        super(name.token);
        this.name = name;
        this.index = index;
    }
}
