package Tokens;

public class Token {
    public String text;
    public int type;

    public Token(String text, int type){
        this.text = text;
        this.type = type;
    }

    @Override
    public String toString() {
        return "Token<'" + text + "', " + type + ">";
    }
}
