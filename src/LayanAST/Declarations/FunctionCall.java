package LayanAST.Declarations;

import LayanAST.Expressions.ExprNode;
import LayanAST.LayanAST;
import Tokens.Token;

import java.util.List;

public class FunctionCall extends LayanAST {
    public ID id;
    public List<ExprNode> args;
    public FunctionCall(ID id, List<ExprNode> args) {
        super(id.token);
        this.id = id;
        this.args = args;
    }

    @Override
    public String toStringNode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("( ");
        stringBuilder.append(id.name.text + " ");
        stringBuilder.append("args: ");
        for(ExprNode node: args){
            stringBuilder.append(node.toStringNode() + " ");
        }
        stringBuilder.append(" )");
        return stringBuilder.toString();
    }
}
