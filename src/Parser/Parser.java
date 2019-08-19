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
        for(int i = 0; i < k; i++){ // full the lookaheadBuffer up to K token
            consume();
        }
    }

    private void consume(){ // Circular Buffer
        lookaheadBuffer[index] = lexer.getNextToken();
        index = (index + 1) % k;
    }

    private void match(int type){// check if the current token in the stream it is what i expected
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
    } // start parse layan statements

    // start arithmetic expression rule
    private void expr(){
        term();
        while (getLookaheadType(1) == Tokens.PLUS
                || getLookaheadType(1) == Tokens.MINUS){
            match(Tokens.PLUS);
            term();
        }
    }

    private void term(){
        factor();
        while (getLookaheadType(1) == Tokens.DIVISION
                || getLookaheadType(1) == Tokens.MULTIPLICATION){
            factor();
        }
    }

    private void factor(){
        if(getLookaheadType(1) == Tokens.NUMBER){
            match(Tokens.NUMBER);
            return;
        }else if(getLookaheadType(1) == Tokens.ID){
            match(Tokens.ID);
            return;
        }else if(getLookaheadType(1) == Tokens.PLUS){
            match(Tokens.PLUS);
            return;
        }else if(getLookaheadType(1) == Tokens.MINUS){
            match(Tokens.MINUS);
            return;
        }
        match(Tokens.OPENPARENTHESIS);
        expr();
        match(Tokens.CLOSEPARENTHESIS);
    }

    // end the arithmetic expression rule

    // start boolean expression rule
    private void boolean_expr(){
        //boolean expression:
        //bool: or(('>' | '<' | '>=' | '<=' | '==' | '!=') or)
        //or: and (('||') and)*
        //and: not (('&&') and)*
        //not: ('!'boolean_expr) | NUMBER| STRING | 'true' | 'false'
        or();
        while (getLookaheadType(1) == Tokens.MORETHAN
                || getLookaheadType(1) == Tokens.LESSTHAN
                || getLookaheadType(1) == Tokens.MORETHANOREQUAL
                || getLookaheadType(1) == Tokens.LESSTHANOREQUAL
                || getLookaheadType(1) == Tokens.EQUALITY
                || getLookaheadType(1) == Tokens.NOTEQUAL){
            if(getLookaheadType(1) == Tokens.MORETHAN) match(Tokens.MORETHAN);
            else if(getLookaheadType(1) == Tokens.LESSTHAN)match(Tokens.LESSTHAN);
            else if(getLookaheadType(1) == Tokens.MORETHANOREQUAL)match(Tokens.MORETHANOREQUAL);
            else if(getLookaheadType(1) == Tokens.LESSTHANOREQUAL)match(Tokens.LESSTHANOREQUAL);
            else if(getLookaheadType(1) == Tokens.EQUALITY)match(Tokens.EQUALITY);
            else match(Tokens.NOTEQUAL);
            or();
        }
    }
    private void or(){
        and();
        while (getLookaheadType(1) == Tokens.OR){
            match(Tokens.OR);
            and();
        }
    }

    private void and(){
        not();
        while (getLookaheadType(1) == Tokens.AND){
            match(Tokens.AND);
            not();
        }
    }

    private void not(){
        if(getLookaheadType(1) == Tokens.NOT){
            match(Tokens.NOT);
            boolean_expr();
            return;
        }else if(getLookaheadType(1) == Tokens.ID){
            match(Tokens.ID);
            return;
        }else if(getLookaheadType(1) == Tokens.NUMBER){
            match(Tokens.NUMBER);
            return;
        }

        match(Tokens.BOOLEAN);
    }

    // end the boolean expression rule

    private void statements(){ // function to determent which statement to parse using
        // lookahead buffer
        while (getLookaheadType(1) == Tokens.ID ||
                getLookaheadType(1) == Tokens.FUNCTION ||
        getLookaheadType(1) == Tokens.CLASS ||
        getLookaheadType(1) == Tokens.IF ||
        getLookaheadType(1) == Tokens.FOR ||
        getLookaheadType(1) == Tokens.WHILE){
            if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.EQUAL){
                assignmentStatements();
            }else if(getLookaheadType(1) == Tokens.ID){
                declarationStatements();
            }else if(getLookaheadType(1) == Tokens.FUNCTION){
                methodDeclaration();
            }else if(getLookaheadType(1) == Tokens.CLASS){
                classDeclaration();
            }else if(getLookaheadType(1) == Tokens.IF || getLookaheadType(1) == Tokens.WHILE){
                conditionStatements();
            }else if (getLookaheadType(1) == Tokens.FOR){
                iterationStatement();
            }
            else{
                throw new Error("Syntax Error");
            }
        }
    }

    private void declarationStatements(){ // rule represent the declaration statements include
        // variable declaration and class declaration
        System.out.println("declarationStatements");
        variableDeclaration();
    }

    private void variableDeclaration(){
        //Variable Declaration Rule: DATA_TYPE ID ('=' expression)?
        System.out.println("variableDeclaration");
        match(Tokens.ID);
        match(Tokens.ID);
        if(getLookaheadType(1) == Tokens.EQUAL){
            match(Tokens.EQUAL);
            expr();
        }
        match(Tokens.SEMICOLON);
    }

    private void assignmentStatements(){
        // Assignment Statements Rule: assignment_stat: ID '=' expr
        System.out.println("assignmentStatements");
        match(Tokens.ID);
        match(Tokens.EQUAL);
        match(Tokens.NUMBER);
        match(Tokens.SEMICOLON);
    }

    private void functionStatements(){ // set of statements that can be inside the
        // function declaration
        while (getLookaheadType(1) == Tokens.ID
                || getLookaheadType(1) == Tokens.IF
                || getLookaheadType(1) == Tokens.WHILE
                || getLookaheadType(1) == Tokens.FOR){
            if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.EQUAL){
                assignmentStatements();
            }else if(getLookaheadType(1) == Tokens.ID){
                declarationStatements();
            }else if(getLookaheadType(1) == Tokens.IF || getLookaheadType(1) == Tokens.WHILE){
                conditionStatements();
            }else if(getLookaheadType(1) == Tokens.FOR){
                iterationStatement();
            }else{
                throw new Error("Syntax Error");
            }
        }
    }

    private void methodDeclaration(){
        //'function' ID '(' parameters? ')' '{' function_statements '}'
        //parameters: Type ID (',' parameters)*
        //function_statements: declaration_statements | if_statement |
        // while_statement | for_statement
        System.out.println("methodDeclaration");
        match(Tokens.FUNCTION);
        match(Tokens.ID);
        match(Tokens.OPENPARENTHESIS);
        parameters();
        match(Tokens.CLOSEPARENTHESIS);
        match(Tokens.OPENCARLYBRACKET);
        functionStatements();
        match(Tokens.CLOSECARLYBRACKET);
    }

    private void parameters(){
        // parameters: Type ID (',' parameters)*
        if(getLookaheadType(1) == Tokens.ID){
            match(Tokens.ID);
            match(Tokens.ID);
            //TODO::Init value for the args
            while (getLookaheadType(1) == Tokens.COMMA){
                parameters();
            }
        }
    }

    private void classStatements(){// set of statements the can be inside the class declaration
        while (getLookaheadType(1) == Tokens.ID || getLookaheadType(1) == Tokens.FUNCTION){
            if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.EQUAL){
                assignmentStatements();
            }else if(getLookaheadType(1) == Tokens.ID){
                declarationStatements();
            }else if(getLookaheadType(1) == Tokens.FUNCTION){
                methodDeclaration();
            }else{
                throw new Error("Syntax Error");
            }
        }
    }

    private void classDeclaration(){
        //class_declaration: 'class' ID '{'class_statements'}'
        //class_statements: declaration_stat | method_declaration
        System.out.println("classDeclaration");
        match(Tokens.CLASS);
        match(Tokens.ID);
        match(Tokens.OPENCARLYBRACKET);
        classStatements();
        match(Tokens.CLOSECARLYBRACKET);
    }

    private void conditionStatements(){
        //if_statement|while_statement: ('if'|'while) '(' boolean_expression ')' '{' statements '}'
        // ('else' '{' statements '}')?
        System.out.println("conditionStatements");
        if(getLookaheadType(1) == Tokens.IF) match(Tokens.IF);
        else match(Tokens.WHILE);
        match(Tokens.OPENPARENTHESIS);
        boolean_expr();
        match(Tokens.CLOSEPARENTHESIS);
        match(Tokens.OPENCARLYBRACKET);
        functionStatements(); // also the same statements fot ifCondition
        match(Tokens.CLOSECARLYBRACKET);
    }

    private void iterationStatement(){
        //for_statement: 'for' '(' variable_declaration boolean_expression ; statement')'
        // '{'stat'}'
        //stat:statements
        //statement: assignment_stat
        System.out.println("iterationStatement");
        match(Tokens.FOR);
        match(Tokens.OPENPARENTHESIS);
        variableDeclaration();
        boolean_expr();
        match(Tokens.SEMICOLON);
        //TODO:: Make it work for function call
        match(Tokens.ID);
        match(Tokens.EQUAL);
        expr();
        match(Tokens.CLOSEPARENTHESIS);
        match(Tokens.OPENCARLYBRACKET);
        functionStatements(); // also work for this rule
        match(Tokens.CLOSECARLYBRACKET);
    }
}
