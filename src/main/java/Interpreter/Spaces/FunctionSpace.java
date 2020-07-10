package Interpreter.Spaces;

import Symbols.MethodSymbol;
import Symbols.Symbol;

import java.util.HashMap;
import java.util.Map;

public class FunctionSpace extends MemorySpace{
    public FunctionSpace(String n) {
        super(n);
    }

    public FunctionSpace(MethodSymbol ms) {
        entities = new HashMap<String, Object>();
    }
}
