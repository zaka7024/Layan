package LayanAST.Declarations;

import LayanAST.LayanAST;
import Tokens.Token;

import java.util.List;

public class FunctionCall extends LayanAST {
    ID id;
    public List<LayanAST> args;
    public FunctionCall(ID id, List<LayanAST> args) {
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
        for(LayanAST node: args){
            stringBuilder.append(node.toStringNode() + " ");
        }
        stringBuilder.append(" )");
        return stringBuilder.toString();
    }
}
