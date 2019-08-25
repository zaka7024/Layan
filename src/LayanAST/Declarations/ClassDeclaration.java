package LayanAST.Declarations;

import LayanAST.Expressions.ExprNode;
import LayanAST.LayanAST;
import Tokens.Token;

public class ClassDeclaration extends LayanAST {
    public ID id;
    public ID superClass;
    public BlockNode block;

    public ClassDeclaration(Token t, ID id, BlockNode blockNode, ID sc) {
        super(t);
        this.id = id;
        block = blockNode;
        superClass = sc;
    }

    @Override
    public String toStringNode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("( class ");
        stringBuilder.append(id.name.text);
        stringBuilder.append(block.toStringNode());
        stringBuilder.append(" )");
        return stringBuilder.toString();
    }
}
