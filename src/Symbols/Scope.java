package Symbols;

public interface Scope {
    Scope getEnclosingScope();
    String getScopeName();
    void define(Symbol symbol);
    Symbol resolve(String name);
}
