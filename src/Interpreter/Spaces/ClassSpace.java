package Interpreter.Spaces;

import Symbols.ClassSymbol;
import Symbols.Symbol;

import java.util.HashMap;
import java.util.Map;

public class ClassSpace extends MemorySpace {//class instance
    private ClassSymbol classSymbol;

    public ClassSpace(ClassSymbol cs){
        classSymbol = cs;
        entities = new HashMap<>();
    }
}
