package Symbols;

import LayanAST.LayanAST;

public class ObjectSymbol extends Symbol{

    public ObjectSymbol(String name, Type type, LayanAST def, Scope scope){
        super(name, type, def, scope);
    }
}
