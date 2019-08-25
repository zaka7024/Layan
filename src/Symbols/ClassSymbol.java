package Symbols;

import LayanAST.LayanAST;

import java.util.HashMap;
import java.util.Map;

public class ClassSymbol extends Symbol implements Scope, Type{

    public Map<String, Symbol> fields = new HashMap<>();
    private Scope enclosingScope;
    public ClassSymbol superClass;

    public ClassSymbol(String name, Type type, Scope cs, LayanAST def){
        super(name, type, def, cs);
        enclosingScope = cs;
    }

    @Override
    public Scope getEnclosingScope() {
        return enclosingScope;
    }

    @Override
    public Scope getParentScope() {
        if(superClass != null) return superClass;
        return getEnclosingScope();
    }

    @Override
    public String getScopeName() {
        return "local scope";
    }

    @Override
    public void define(Symbol symbol) {
        System.out.println("def " + symbol.name);
        fields.put(symbol.name, symbol);
    }

    @Override
    public Symbol resolve(String name) {
        Symbol symbol = fields.get(name);
        Scope scope = getParentScope();
        while (scope != null && symbol == null){
            symbol = scope.resolve(name);
            if(symbol != null) break;
            scope = scope.getParentScope();
        }
        return symbol;
    }

    @Override
    public Symbol resolveMember(String name){
        Symbol symbol = fields.get(name);
        if(symbol == null && superClass != null){
            symbol = superClass.resolveMember(name);
        }
        return symbol;
    }

    @Override
    public String getTypeName() {
        return this.name;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getScopeName() + ": ");
        for (String name: fields.keySet())
            stringBuilder.append(name + " ");
        return stringBuilder.toString();
    }
}
