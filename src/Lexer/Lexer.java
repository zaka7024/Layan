package Lexer;

import Tokens.Token;
import Tokens.Tokens;

import java.util.HashMap;

// Recursive Descent Lexer LL(1).
public class Lexer {
    private String input; // layan code.txt.
    private int index; // current input index.
    private char currentChar;
    private HashMap<String, Token> keywords = new HashMap<String, Token>(); // represent the preserved
    // key words of layan

    public Lexer(String text){
        input = text;
        index = 0;
        if(input.length() <= 0) throw new IllegalArgumentException("Invalid Layan Code");
        currentChar = input.charAt(index);
        initKeywords();
    }

    private void initKeywords(){//map each keyword with it's token
        keywords.put("function", new Token("function", Tokens.FUNCTION));
        keywords.put("class", new Token("class", Tokens.CLASS));
        keywords.put("if", new Token("if", Tokens.IF));
        keywords.put("else", new Token("else", Tokens.ELSE));
        keywords.put("while", new Token("while", Tokens.WHILE));
        keywords.put("for", new Token("for", Tokens.FOR));
        keywords.put("true", new Token("true", Tokens.BOOLEAN));
        keywords.put("false", new Token("false", Tokens.BOOLEAN));
    }

    private void consume(){ // move the pointer to the next char in the input stream.
        if(++index < input.length()){
            currentChar = input.charAt(index);
            return;
        }
        currentChar = (char)-1; // to know if we hit the end of the code.txt.
    }

    private char peek(){
        if((index + 1) >= input.length()) return ' ';
        return input.charAt(index + 1);
    }

    private Token generateToken(String text, int type){
        return new Token(text, type);
    }

    private boolean isChar(char c){
        return (c >= 'a' && c <= 'z'|| c >= 'A' && c <= 'Z');
    }

    private boolean isDigit(char d){
        return d >= '0' && d <= '9';
    }

    private boolean isWhiteSpace(char c){
        return (c == ' ' || c == '\n' || c == '\t');
    }

    private void skipWhiteSpace(){// avoid redundant chars.
        while (isWhiteSpace(currentChar)) consume();
    }

    private void skipComment(){
        match('/');
        match('*');
        for(; index < input.length() - 1; consume()){
            if(input.substring(index, index + 2).compareTo("*/") == 0) break;
        }
        match('*');
        match('/');
    }

    private Token isKeyword(String name){
        return keywords.get(name);
    }

    private void match(char c){// check if the current char in the stream it is what i expected
        if(currentChar == c){
            consume();
            return;
        }
        throw new Error("Expected " + c + " found " + currentChar);
    }

    private Token ID(){ // Lexical rule to generate ID token.
        // ID Rule: (('a'..'z' | 'A'..'Z')+(NUMBER*))+
        StringBuilder name = new StringBuilder();
        do{
            name.append(currentChar);
            consume();
        }
        while (isChar(currentChar));
        while (isDigit(currentChar)){
            name.append(ID().text);
            consume();
        }
        if(isKeyword(name.toString()) != null) return isKeyword(name.toString());
        else return generateToken(name.toString(), Tokens.ID);
    }

    private Token NUMBER(){ // Lexical rule to generate NUMBER token.
        // This rule work for integer and floating point number.
        // NUMBER Rule: ('0'..'9')+('.' ('0'..'9')+)?
        StringBuilder number = new StringBuilder();
        while (isDigit(currentChar)){
            number.append(currentChar);
            consume();
        }
        if(currentChar == '.'){
            //TODO:: Make decision to if should be a one digit at least after the '.'
            number.append('.');
            consume();
            while (isDigit(currentChar)){
                number.append(currentChar);
                consume();
            }
        }
        return generateToken(number.toString(), Tokens.NUMBER);
    }

    private String STRING(){ // Lexical rule to generate STRING token
        //STRING Rule: ' " ' (NAME | NUMBER)* ' " '
        match('"');
        StringBuilder string = new StringBuilder();
        string.append("\"");
        for(; index < input.length() - 1; consume()){
            string.append(currentChar);
            if(currentChar == '"') break;
        }
        match('"');
        string.append("\"");
        return string.toString();
    }

    public Token getNextToken(){
        while (currentChar != (char)-1){
            if(isWhiteSpace(currentChar)){
                skipWhiteSpace();
                continue;
            }

            if(isChar(currentChar)) return ID();
            else if(isDigit(currentChar)) return NUMBER();
            else if(currentChar == '.'){
                match('.');
                return generateToken(".", Tokens.DOT);
            }
            else if(currentChar == '=' && peek() == '='){
                match('='); match('=');
                return generateToken("==", Tokens.EQUALITY);
            }
            else if(currentChar == '='){
                match('=');
                return generateToken("=", Tokens.EQUAL);
            }
            else if(currentChar == ';'){
                match(';');
                return generateToken(";", Tokens.SEMICOLON);
            }else if(currentChar == '{'){
                match('{');
                return generateToken("{", Tokens.OPENCARLYBRACKET);
            }else if(currentChar == '}'){
                match('}');
                return generateToken("}", Tokens.CLOSECARLYBRACKET);
            }else if(currentChar == '('){
                match('(');
                return generateToken("(", Tokens.OPENPARENTHESIS);
            }else if(currentChar == ')'){
                match(')');
                return generateToken(")", Tokens.CLOSEPARENTHESIS);
            }else if(currentChar == ','){
                match(',');
                return generateToken(",", Tokens.COMMA);
            }else if(currentChar == '"'){
                return generateToken(STRING(), Tokens.STRING);
            }else if(currentChar == '>' && peek() == '='){
                match('>'); match('=');
                return generateToken(">=", Tokens.MORETHANOREQUAL);
            }else if(currentChar == '>'){
                match('>');
                return generateToken(">", Tokens.MORETHAN);
            }else if(currentChar == '<' && peek() == '='){
                match('<'); match('=');
                return generateToken("<=", Tokens.LESSTHANOREQUAL);
            }else if(currentChar == '<'){
                match('<');
                return generateToken("<", Tokens.LESSTHAN);
            }else if(currentChar == '!' && peek() == '='){
                match('!'); match('=');
                return generateToken("!=", Tokens.NOTEQUAL);
            }else if(currentChar == '!'){
                match('!');
                return generateToken("!", Tokens.NOT);
            }else if(currentChar == '&'){
                match('&'); match('&');
                return generateToken("&&", Tokens.AND);
            }else if(currentChar == '|'){
                match('|'); match('|');
                return generateToken("||", Tokens.OR);
            }else if(currentChar == '*'){
                match('*');
                return generateToken("*", Tokens.MULTIPLICATION);
            }
            else if(currentChar == '/' && peek() == '*'){
                skipComment();
                continue;
            }else if(currentChar == '/'){
                match('/');
                return generateToken("/", Tokens.DIVISION);
            }else if(currentChar == '-'){
                match('-');
                return generateToken("-", Tokens.MINUS);
            }else if(currentChar == '+'){
                match('+');
                return generateToken("+", Tokens.PLUS);
            }else if(currentChar == '%'){
                match('%');
                return generateToken("%", Tokens.MODULES);
            }else if(currentChar == ':'){
                match(':');
                return generateToken(":", Tokens.COLON);
            }
            else throw new Error("Unexpected char: " + currentChar);
        }

        return new Token("EOF", Tokens.EOF);
    }
}
