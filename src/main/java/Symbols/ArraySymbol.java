package Symbols;

import LayanAST.LayanAST;

public class ArraySymbol extends Symbol {

    public ArraySymbol(String name, Type type, LayanAST def, Scope scope) {
        super(name, type, def, scope);
    }
}
