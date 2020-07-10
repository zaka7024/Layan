package LayanAST.Declarations;

import LayanAST.Expressions.ExprNode;
import LayanAST.LayanAST;
import Tokens.Token;

import java.util.ArrayList;
import java.util.List;

public class BlockNode extends LayanAST {
    public List<LayanAST> layanASTList = new ArrayList<LayanAST>();

    public BlockNode(Token t) {
        super(t);
    }

    @Override
    public String toStringNode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("(");
        for (LayanAST node: layanASTList)
            stringBuilder.append("[ " + node.toStringNode() + " ] ");
        stringBuilder.append(" )");
        return stringBuilder.toString();
    }
}
