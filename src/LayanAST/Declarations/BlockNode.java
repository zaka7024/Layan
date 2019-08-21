package LayanAST.Declarations;

import LayanAST.Expressions.ExprNode;
import LayanAST.LayanAST;
import Tokens.Token;

import java.util.ArrayList;
import java.util.List;

public class BlockNode extends LayanAST {
    List<LayanAST> layanASTList = new ArrayList<LayanAST>();

    public BlockNode(Token t) {
        super(t);
    }
}
