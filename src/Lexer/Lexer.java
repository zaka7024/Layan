package Lexer;

import Tokens.Token;
import Tokens.Tokens;

// Recursive Descent Lexer LL(1).
public class Lexer {
    private String input; // Layan code.
    private int index; // current input index.
    private char currentChar;

    public Lexer(String text){
        input = text;
        index = 0;
        if(input.length() <= 0) throw new IllegalArgumentException("Invalid Layan Code");
        currentChar = input.charAt(index);
    }

    private void consume(){ // move the pointer to the next char in the input stream.
        if(++index < input.length()){
            currentChar = input.charAt(index);
            return;
        }
        currentChar = (char)-1; // to know if we hit the end of the code.
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
        return generateToken(name.toString(), Tokens.ID);
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

    public Token getNextToken(){
        while (currentChar != (char)-1){
            if(isWhiteSpace(currentChar)){
                skipWhiteSpace();
                continue;
            }

            if(isChar(currentChar)) return ID();
            else if(isDigit(currentChar)) return NUMBER();
        }

        return new Token("EOF", Tokens.EOF);
    }


}
