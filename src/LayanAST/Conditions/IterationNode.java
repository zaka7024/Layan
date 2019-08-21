package LayanAST.Conditions;

import LayanAST.Declarations.BlockNode;
import LayanAST.Declarations.VariableDeclaration;
import LayanAST.Expressions.EqualNode;
import LayanAST.Expressions.ExprNode;
import LayanAST.LayanAST;
import Tokens.Token;

public class IterationNode extends LayanAST {
    public VariableDeclaration forVariable;
    public ExprNode expression;
    public EqualNode assignment;
    public BlockNode blockNode;
    public IterationNode(VariableDeclaration var, ExprNode expr, Token t, EqualNode equalNode, BlockNode block) {
        super(t);
        forVariable = var;
        expression = expr;
        assignment = equalNode;
        blockNode = block;
    }

    @Override
    public String toStringNode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("( ");
        stringBuilder.append("for ");
        stringBuilder.append(forVariable.toStringNode() +" ; ");
        stringBuilder.append(expression.toStringNode() + " ; ");
        stringBuilder.append(assignment.toStringNode());
        stringBuilder.append("->");
        stringBuilder.append(blockNode.toStringNode());
        return stringBuilder.toString();
    }
}
