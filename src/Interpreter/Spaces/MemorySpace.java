package Interpreter.Spaces;

import java.util.HashMap;
import java.util.Map;

public class MemorySpace {
    public Map<String, Object> entities;
    public String name;
    public MemorySpace previousSpace;

    public MemorySpace(){

    }

    public MemorySpace(String n){
        entities = new HashMap<>();
        this.name = n;
    }

    public void put(String name, Object value){
        entities.put(name, value);
    }

    public Object get(String name){
        return entities.get(name);
    }
}
