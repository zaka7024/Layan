package Parser;

import Lexer.Lexer;
import Tokens.Token;
import Tokens.Tokens;

import java.util.ArrayList;
import java.util.List;

// Recursive Descent Parser LL(K)
public class Parser {
    private Lexer lexer;
    private Token [] lookaheadBuffer;
    private int k;
    private int index;

    public Parser(Lexer tokenizer, int k){
        lexer = tokenizer;
        this.k = k;
        lookaheadBuffer = new Token[k];
        for(int i = 0; i < k; i++){
            consume();
        }
    }

    private void consume(){
        lookaheadBuffer[index] = lexer.getNextToken();
        index = (index + 1) % k;
    }

    private void match(int type){
        if(getLookaheadType(1) == type){
            consume();
            return;
        }
        throw new Error("Syntax Error -> Expected token of type " + type + " found " + getLookaheadType(1));
    }

    private Token getLookaheadToken(int p){
        return lookaheadBuffer[(p - 1 + index) % k];
    }

    private int getLookaheadType(int p){
        return getLookaheadToken(p).type;
    }

    public void parse(){
        statements();
    }

    private void statements(){
        while (getLookaheadType(1) == Tokens.ID){
            if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.EQUAL){
                assignmentStatements();
            }else if(getLookaheadType(1) == Tokens.ID){
                declarationStatements();
            }
        }
    }

    private void declarationStatements(){
        System.out.println("declarationStatements");
        match(Tokens.ID);
        match(Tokens.ID);
        match(Tokens.EQUAL);
        match(Tokens.NUMBER);
        match(Tokens.SEMICOLON);
    }

    private void assignmentStatements(){
        System.out.println("assignmentStatements");
        match(Tokens.ID);
        match(Tokens.EQUAL);
        match(Tokens.NUMBER);
        match(Tokens.SEMICOLON);
    }
}
