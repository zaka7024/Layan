package LayanAST;

import LayanAST.Declarations.ID;
import Tokens.Token;

import java.util.ArrayList;
import java.util.List;

public class Program extends LayanAST {
    public List<LayanAST> statements;

    public Program(Token t, List<LayanAST> stat) {
        super(t);
        statements = new ArrayList<LayanAST>();
        statements = stat;
    }

    @Override
    public String toStringNode() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("( ");
        stringBuilder.append("Program ");
        for(LayanAST node: statements)
            stringBuilder.append(node.toStringNode());
        stringBuilder.append(" )");
        return stringBuilder.toString();
    }
}
