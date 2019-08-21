package LayanAST.Declarations;

import LayanAST.Expressions.ExprNode;
import LayanAST.LayanAST;
import Tokens.Token;

import java.util.ArrayList;
import java.util.List;

public class MethodDeclaration extends LayanAST {
    public ID id;
    public List<VariableDeclaration> parameters = new ArrayList<>();
    public BlockNode block;

    public MethodDeclaration( Token id,ID name, BlockNode blockNode) {
        super(id);
        this.id = name;
        block = blockNode;
    }

    @Override
    public String toStringNode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("( method ");
        stringBuilder.append(id.name.text + " ");
        stringBuilder.append(block.toStringNode());
        stringBuilder.append(" )");
        return stringBuilder.toString();
    }
}