package Interpreter.Spaces;

import java.util.HashMap;
import java.util.Map;

public class MemorySpace {
    private Map<String, Object> entities;
    private String name;
    public MemorySpace(String n){
        entities = new HashMap<>();
        this.name = n;
    }

    public void put(String name, Object value){
        entities.put(name, value);
    }

    public void get(String name){
        entities.get(name);
    }
}
