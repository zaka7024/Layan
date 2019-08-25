package Symbols;

import java.util.HashMap;
import java.util.Map;

public class ProgramSymbol extends Symbol implements Scope{

    public Map<String, Symbol> globals = new HashMap<>();
    private Scope enclosingScope;

    public ProgramSymbol(String name, Scope cs){
        super(name);
        enclosingScope = cs;
        initBuiltInTypes();
    }

    private void initBuiltInTypes(){
        define(new BuiltInTypeSymbol("int"));
        define(new BuiltInTypeSymbol("float"));
        define(new BuiltInTypeSymbol("string"));
        define(new BuiltInTypeSymbol("bool"));
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
        return "global scope";
    }

    @Override
    public void define(Symbol symbol) {
        System.out.println("def " + symbol.name);
        globals.put(symbol.name, symbol);
    }

    @Override
    public Symbol resolve(String name) {
        Symbol symbol = globals.get(name);
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
        return globals.get(name);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getScopeName() + ": ");
        for (String name: globals.keySet())
            stringBuilder.append(name + " ");
        return stringBuilder.toString();
    }
}
