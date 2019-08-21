package LayanAST.Conditions;

import LayanAST.Declarations.BlockNode;
import LayanAST.Expressions.ExprNode;
import LayanAST.LayanAST;
import Tokens.Token;

public class ConditionNode extends LayanAST {
    public ExprNode expression;
    public BlockNode truePart, falsePart = null;
    public ConditionNode(ExprNode expr, Token t, BlockNode tp) {
        super(t);
        expression = expr;
        truePart = tp;
    }

    public ConditionNode(ExprNode expr, Token t, BlockNode tp, BlockNode fp) {
        super(t);
        expression = expr;
        truePart = tp;
        falsePart = fp;
    }

    @Override
    public String toStringNode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("( if ");
        stringBuilder.append(expression.toStringNode() + " ");
        stringBuilder.append(truePart.toStringNode());
        stringBuilder.append(" else ");
        stringBuilder.append(falsePart.toStringNode());
        stringBuilder.append(" )");
        return stringBuilder.toString();
    }
}
