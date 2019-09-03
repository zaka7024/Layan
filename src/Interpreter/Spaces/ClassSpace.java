package Interpreter.Spaces;

import Symbols.ClassSymbol;
import Symbols.Symbol;

import java.util.Map;

public class ClassSpace {
    private Map<String, Object> members;
    private ClassSymbol classSymbol;

    public ClassSpace(ClassSymbol cs){
        classSymbol = cs;
        mapAll(cs);
    }

    // map all fields in the class symbol and its super class.
    private void mapAll(ClassSymbol cs){
        for(Symbol member: cs.fields.values()){
            //TODO:: compute the value
            members.put(member.name, null);
        }

        if(cs.superClass != null) mapAll(cs.superClass);
    }
}
