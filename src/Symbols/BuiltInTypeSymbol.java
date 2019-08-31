package Symbols;

public class BuiltInTypeSymbol extends Symbol implements Type {

    public int typeIndex;

    public BuiltInTypeSymbol(String name){
        super(name);
        setTypeIndex();
    }

    @Override
    public String getTypeName() {
        return "<BuiltIntType" + name + ">";
    }

    private void setTypeIndex(){
        switch (name){
            case "userType": typeIndex = 1;break;
            case "boolean": typeIndex = 1;break;
            case "string": typeIndex = 2;break;
            case "int": typeIndex = 3;break;
            case "float": typeIndex = 4;break;
            case "void": typeIndex = 5;
        }
    }
}
