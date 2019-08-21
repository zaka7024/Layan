package Parser;

import Lexer.Lexer;
import Tokens.Token;
import Tokens.Tokens;
import java.util.Arrays;
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
        throw new Error("Syntax Error -> Expected " + Tokens.getTokenName(type) + " found " +
                Tokens.getTokenName(getLookaheadType(1)));
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
        //expr: and (('||') and)*
        //and: comp (('&&') and)*
        //comp: ar(('>' | '<' | '>=' | '<=' | '!=' | '==') ar)
        //ar: term (('+'|'-') term)*
        //term: factor (('*', '/', '%') factor)*
        //factor: + | - | '(' expr ')' |  ('!'boolean_expr) | NUMBER| STRING | 'true' | 'false'
        and();
        while (getLookaheadType(1) == Tokens.OR){
            match(getLookaheadType(1));
            and();
        }
    }

    private void ar(){
        term();
        while (getLookaheadType(1) == Tokens.PLUS || getLookaheadType(1)
        == Tokens.MINUS){
            match(getLookaheadType(1));
            term();
        }
    }

    private void term(){
        factor();
        List<Integer> tokens = Arrays.asList(Tokens.MULTIPLICATION, Tokens.DIVISION,
                Tokens.MODULES);
        while (tokens.contains(getLookaheadType(1))){
            match(getLookaheadType(1));
            factor();
        }
    }

    private void factor(){
        //TODO:: Alter the rule so can support function call and variable
        //factor: + | - | '(' expr ')' |  ('!'boolean_expr) | NUMBER| STRING | 'true' |
        // 'false' | Type(ID) ID | ID
        List<Integer> tokens = Arrays.asList(Tokens.NUMBER, Tokens.STRING, Tokens.BOOLEAN,
                Tokens.PLUS, Tokens.MINUS);
        if(tokens.contains(getLookaheadType(1))){
            match(getLookaheadType(1));
            return;
        }else if(getLookaheadType(1) == Tokens.ID &&
                getLookaheadType(2) == Tokens.OPENPARENTHESIS){
            objectDeclaration();
        }else if(getLookaheadType(1) == Tokens.ID){
            match(getLookaheadType(1));
        }
        else if(getLookaheadType(1) == Tokens.NOT){
            match(Tokens.NOT);
            expr();
            return;
        }else if(getLookaheadType(1) == Tokens.OPENPARENTHESIS){
            match(Tokens.OPENPARENTHESIS);
            expr();
            match(Tokens.CLOSEPARENTHESIS);
        }else{
            throw new Error("Syntax Error");
        }
    }

    private void comp(){
        ar();
        List<Integer> tokens = Arrays.asList(Tokens.MORETHAN, Tokens.LESSTHAN,
                Tokens.MORETHANOREQUAL, Tokens.LESSTHANOREQUAL, Tokens.EQUALITY, Tokens.NOTEQUAL);
        while (tokens.contains(getLookaheadType(1))){
            if(getLookaheadType(1) == Tokens.MORETHAN) match(Tokens.MORETHAN);
            else if(getLookaheadType(1) == Tokens.LESSTHAN)match(Tokens.LESSTHAN);
            else if(getLookaheadType(1) == Tokens.MORETHANOREQUAL)match(Tokens.MORETHANOREQUAL);
            else if(getLookaheadType(1) == Tokens.LESSTHANOREQUAL)match(Tokens.LESSTHANOREQUAL);
            else if(getLookaheadType(1) == Tokens.EQUALITY)match(Tokens.EQUALITY);
            else match(Tokens.NOTEQUAL);
            ar();
        }
    }

    private void and(){
        comp();
        while (getLookaheadType(1) == Tokens.AND){
            match(Tokens.AND);
            comp();
        }
    }

    private void statements(){ // function to determent which statement to parse using
        // lookahead buffer
        List<Integer> tokens = Arrays.asList(Tokens.ID, Tokens.FUNCTION, Tokens.CLASS,
                Tokens.IF, Tokens.FOR, Tokens.WHILE);
        while (tokens.contains(getLookaheadType(1))){
            if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.EQUAL){
                assignmentStatements();
            }else if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.OPENPARENTHESIS){
                methodCall();
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
        //Variable Declaration Rule:
        // declaration_stat: DATA_TYPE ID ('=' expression)? ((',' ID)('=' expression)?)? ';'
        System.out.println("variableDeclaration");
        match(Tokens.ID);
        match(Tokens.ID);
        if(getLookaheadType(1) == Tokens.EQUAL){
            match(Tokens.EQUAL);
            expr();
        }
        while (getLookaheadType(1) == Tokens.COMMA){
            match(getLookaheadType(1));
            match(Tokens.ID);
            if(getLookaheadType(1) == Tokens.EQUAL){
                match(Tokens.EQUAL);
                expr();
            }
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
        List<Integer> tokens = Arrays.asList(Tokens.ID, Tokens.IF, Tokens.FOR, Tokens.WHILE);
        while (tokens.contains(getLookaheadType(1))){
            if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.EQUAL){
                assignmentStatements();
            }else if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.OPENPARENTHESIS){
                methodCall();
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

    private void methodCallParameters(){ // Work for class and method parameters
        expr();
    }

    private void methodCall(){
        //ID '(' (expr ',')'* ')' ';'
        System.out.println("methodCall");
        match(Tokens.ID);
        match(Tokens.OPENPARENTHESIS);
        methodCallParameters();
        while (getLookaheadType(1) == Tokens.COMMA){
            match(getLookaheadType(1));
            methodCallParameters();
        }
        match(Tokens.CLOSEPARENTHESIS);
        match(Tokens.SEMICOLON);
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
        //class_declaration: 'class' ID (':' TYPE) '{'class_statements'}'
        //class_statements: declaration_stat | method_declaration
        System.out.println("classDeclaration");
        match(Tokens.CLASS);
        match(Tokens.ID);
        if(getLookaheadType(1) == Tokens.COLON){
            match(getLookaheadType(1));
            match(Tokens.ID);
        }
        match(Tokens.OPENCARLYBRACKET);
        classStatements();
        match(Tokens.CLOSECARLYBRACKET);
    }

    private void objectDeclaration(){
        //object_declaration: Type '(' parameters ')'
        //parameters: declaration_stat (',' declaration_stat)*
        match(Tokens.ID);
        match(Tokens.OPENPARENTHESIS);
        methodCallParameters();
        while (getLookaheadType(1) == Tokens.COMMA){
            match(getLookaheadType(1));
            methodCallParameters();
        }
        match(Tokens.CLOSEPARENTHESIS);
    }

    private void conditionStatements(){
        //if_statement|while_statement: ('if'|'while) '(' boolean_expression ')' '{' statements '}'
        // ('else' '{' statements '}')?
        System.out.println("conditionStatements");
        if(getLookaheadType(1) == Tokens.IF) match(Tokens.IF);
        else match(Tokens.WHILE);
        match(Tokens.OPENPARENTHESIS);
        expr();
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
        expr();
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
