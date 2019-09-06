package Interpreter.Spaces;

import Symbols.MethodSymbol;
import Symbols.Symbol;

import java.util.Map;

public class FunctionSpace extends MemorySpace{
    private Map<String, Object> parameters;

    public FunctionSpace(String n) {
        super(n);
    }

    public FunctionSpace(MethodSymbol ms) {

    }
}
