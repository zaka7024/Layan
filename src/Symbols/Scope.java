package Symbols;

public interface Scope {
    Scope getEnclosingScope();
    Scope getParentScope();
    String getScopeName();
    void define(Symbol symbol);
    Symbol resolve(String name);
    Symbol resolveMember(String name);
}
