package LayanAST.Expressions;

import LayanAST.Declarations.FunctionCall;
import LayanAST.Declarations.ID;
import LayanAST.LayanAST;
import Tokens.Token;

public class ResolutionObject extends ExprNode {
    public ID type;
    public ID member;
    public FunctionCall functionCall;

    public ResolutionObject(ID type, ID member) {
        super(type.token);
        this.type = type;
        this.member = member;
    }

    @Override
    public String toStringNode() {
        return null;
    }
}
