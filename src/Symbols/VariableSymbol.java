package Symbols;

import LayanAST.LayanAST;

public class VariableSymbol extends Symbol{

    public VariableSymbol(String name, Type type, LayanAST def, Scope scope){
        super(name, type, def, scope);
    }
}
