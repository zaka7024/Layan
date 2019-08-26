package Symbols;

import LayanAST.Declarations.ID;
import LayanAST.Declarations.VariableDeclaration;
import LayanAST.LayanAST;

public class SymbolTable {

    public static Symbol resolve(LayanAST node){
        Symbol symbol = ((ID)node).scope.resolve(((ID) node).name.text);
        if(symbol.def == null){
            System.out.println("must be predefined symbol");
        }
        int refPosition = node.token.tokenIndex;
        int defPosition = ((VariableDeclaration)symbol.def).id.token.tokenIndex;
        if(refPosition < defPosition && ((ID) node).scope instanceof BaseScope
        && symbol.scope instanceof BaseScope) throw new Error(symbol.name + " must be defined");
        return symbol;
    }
}
