package Symbols;

import LayanAST.LayanAST;

public abstract class Symbol{
    public String name;
    public Type type;
    public Type evalType;
    public LayanAST def; // from where the symbol is defined
    public Scope scope; // symbol scope

    public Symbol(String name){
        this.name = name;
    }

    public Symbol(String name, Type type, LayanAST def, Scope scope){
        this.name = name;
        this.type = type;
        this.def = def;
        this.scope = scope;
    }

    @Override
    public String toString() {
        return "symbol<" + name + ">";
    }
}
