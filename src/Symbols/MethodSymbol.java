package Symbols;

import LayanAST.LayanAST;

import java.util.HashMap;
import java.util.Map;

public class MethodSymbol extends Symbol implements Scope{

    public Map<String, Symbol> parameters = new HashMap<>();
    private Scope enclosingScope;

    public MethodSymbol(String name, Type type, Scope cs, LayanAST def){
        super(name, type, def, cs);
        enclosingScope = cs;
    }

    @Override
    public Scope getParentScope() {
        return getEnclosingScope();
    }

    @Override
    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    @Override
    public String getScopeName() {
        return "local scope(parameters)";
    }

    @Override
    public void define(Symbol symbol) {
        parameters.put(symbol.name, symbol);
    }

    @Override
    public Symbol resolve(String name) {
        Symbol symbol = parameters.get(name);
        Scope scope = getParentScope();
        while (scope != null && symbol != null){
            symbol = scope.resolve(name);
            if(symbol != null) break;
            scope = scope.getParentScope();
        }
        return symbol;
    }

    @Override
    public Symbol resolveMember(String name){
        return parameters.get(name);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getScopeName() + ": ");
        for (String name: parameters.keySet())
            stringBuilder.append(name + " ");
        return stringBuilder.toString();
    }
}
