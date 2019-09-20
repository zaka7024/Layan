package Interpreter.Spaces;

import Symbols.ClassSymbol;
import Symbols.MethodSymbol;
import Symbols.Symbol;

import java.util.HashMap;
import java.util.Map;

public class ClassSpace extends MemorySpace {//class instance
    public ClassSymbol classSymbol;

    public ClassSpace(ClassSymbol cs){
        classSymbol = cs;
        entities = new HashMap<>();
        if(classSymbol.superClass != null){
            for(String filed: classSymbol.superClass.fields.keySet()){
                Object symbol = classSymbol.superClass.fields.get(filed);
                if(symbol instanceof MethodSymbol)
                    entities.put(filed, symbol);
                else entities.put(filed, "null");
            }
        }
    }
}
