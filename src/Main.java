import Lexer.Lexer;
import Tokens.Token;
import Tokens.Tokens;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException {
        FileReader fileReader = null;
        try {
            fileReader = new FileReader("C:\\Users\\HP\\IdeaProjects\\Layan\\src\\code.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String temp = "";
        while ((temp = bufferedReader.readLine()) != null)
            stringBuilder.append(temp);


        Lexer lexer = new Lexer(stringBuilder.toString());
        Token t = lexer.getNextToken();
        System.out.println(t);
        while (t.type != Tokens.EOF){
            t = lexer.getNextToken();
            System.out.println(t);
        }
    }
}
