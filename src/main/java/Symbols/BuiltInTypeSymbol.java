package Symbols;

public class BuiltInTypeSymbol extends Symbol implements Type, Comparable<BuiltInTypeSymbol> {

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
        if ("userType".equals(name)) {
            typeIndex = 0;
        } else if ("bool".equals(name)) {
            typeIndex = 1;
        } else if ("string".equals(name)) {
            typeIndex = 2;
        } else if ("int".equals(name)) {
            typeIndex = 3;
        } else if ("float".equals(name)) {
            typeIndex = 4;
        } else if ("void".equals(name)) {
            typeIndex = 5;
        }
    }

    @Override
    public int compareTo(BuiltInTypeSymbol that) {
        if(that == null) return -1;
        return this.typeIndex == that.typeIndex ? 0 : -1;
    }
}
