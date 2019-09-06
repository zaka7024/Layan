import Interpreter.Interpreter;
import LayanAST.ASTVisitor;
import LayanAST.ASTVisitorDefine;
import LayanAST.ASTVisitorResolve;
import LayanAST.LayanAST;
import Lexer.Lexer;
import Parser.Parser;
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
        Parser parser = new Parser(lexer, 3);
        LayanAST ast = parser.parse();
        ASTVisitorDefine visitor = new ASTVisitorDefine(ast);
        ASTVisitorResolve visitorResolve = new ASTVisitorResolve(ast);
        Interpreter interpreter = new Interpreter(ast);
    }
}
