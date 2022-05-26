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
import java.lang.reflect.Array;
import java.util.Arrays;

public class Main {

    public static void main(String[] args) throws IOException {
        FileReader fileReader = null;
        BufferedReader bufferedReader;
        StringBuilder stringBuilder = new StringBuilder();
        String temp = "";
        try {
            fileReader = new FileReader
                    ("src/main/java/LayanStdLib.layan");
            bufferedReader = new BufferedReader(fileReader);
            while ((temp = bufferedReader.readLine()) != null)
                stringBuilder.append(temp);

            fileReader = new FileReader
                    ("C:\\Users\\ADMIN\\IdeaProjects\\Layan\\src\\main\\java\\code.txt");
            bufferedReader = new BufferedReader(fileReader);
            while ((temp = bufferedReader.readLine()) != null)
                stringBuilder.append(temp);

            System.out.println(Arrays.toString(args));

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(stringBuilder.toString());
        Lexer lexer = new Lexer(stringBuilder.toString());
        Parser parser = new Parser(lexer, 4);
        LayanAST ast = parser.parse();
        ASTVisitorDefine visitor = new ASTVisitorDefine(ast);
        ASTVisitorResolve visitorResolve = new ASTVisitorResolve(ast);
        Interpreter interpreter = new Interpreter(ast);
    }
}
