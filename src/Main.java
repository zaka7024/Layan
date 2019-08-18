import Lexer.Lexer;
import Tokens.Token;
import Tokens.Tokens;

public class Main {

    public static void main(String[] args) {
        Lexer lexer = new Lexer("22.43434356458456745");
        Token t = lexer.getNextToken();
        System.out.println(t);
        while (t.type != Tokens.EOF){
            t = lexer.getNextToken();
            System.out.println(t);
        }
    }
}
