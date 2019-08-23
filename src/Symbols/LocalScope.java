package Symbols;

import LayanAST.LayanAST;

import java.util.HashMap;
import java.util.Map;

public class LocalScope implements Scope{

    public Map<String, Symbol> members = new HashMap<>();
    private Scope enclosingScope;

    public LocalScope( Scope cs){
        enclosingScope = cs;
    }

    @Override
    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    @Override
    public String getScopeName() {
        return "local scope";
    }

    @Override
    public void define(Symbol symbol) {
        System.out.println("def " + symbol.name);
        members.put(symbol.name, symbol);
    }

    @Override
    public Symbol resolve(String name) {
        Symbol symbol = members.get(name);
        Scope scope = getEnclosingScope();
        while (scope != null && symbol != null){
            symbol = scope.resolve(name);
            scope = scope.getEnclosingScope();
        }

        return symbol;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getScopeName() + ": ");
        for (String name: members.keySet())
            stringBuilder.append(name + " ");
        return stringBuilder.toString();
    }
}
