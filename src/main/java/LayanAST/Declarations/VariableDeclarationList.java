package LayanAST.Declarations;

import LayanAST.Expressions.ExprNode;
import LayanAST.LayanAST;

import java.util.ArrayList;
import java.util.List;

public class VariableDeclarationList extends LayanAST {
    public List<VariableDeclaration> variableDeclarations;

    public VariableDeclarationList(ID t, List<VariableDeclaration> vars) {
        super(t.name);
        variableDeclarations = new ArrayList<VariableDeclaration>();
        variableDeclarations = vars;
    }

    @Override
    public String toStringNode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("( ");
        for (VariableDeclaration node: variableDeclarations)
            stringBuilder.append(node.toStringNode());
        stringBuilder.append(" )");
        return stringBuilder.toString();
    }
}
