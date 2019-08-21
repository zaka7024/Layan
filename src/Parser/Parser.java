package Parser;

import LayanAST.Declarations.*;
import LayanAST.Expressions.*;
import LayanAST.LayanAST;
import LayanAST.EOF;
import Lexer.Lexer;
import Tokens.Token;
import Tokens.Tokens;
import java.util.ArrayList;
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

    private Token match(int type){// check if the current token in the stream it is what i expected
        if(getLookaheadType(1) == type){
            Token t = getLookaheadToken(1);
            consume();
            return t;
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

    public LayanAST parse(){
        return statements();
    } // start parse layan statements

    // start arithmetic expression rule
    private ExprNode expr(){
        //expr: and (('||') and)*
        //and: comp (('&&') and)*
        //comp: ar(('>' | '<' | '>=' | '<=' | '!=' | '==') ar)
        //ar: term (('+'|'-') term)*
        //term: factor (('*', '/', '%') factor)*
        //factor: + | - | '(' expr ')' |  ('!'boolean_expr) | NUMBER| STRING | 'true' | 'false'
        ExprNode node = and();
        while (getLookaheadType(1) == Tokens.OR){
            node =  new OrNode(node, match(getLookaheadType(1)), and());
        }
        return node;
    }

    private ExprNode and(){
        ExprNode node = comp();
        while (getLookaheadType(1) == Tokens.AND){
            node = new AndNode(node, match(Tokens.AND), comp());
        }
        return node;
    }

    private ExprNode comp(){
        ExprNode node = ar();
        List<Integer> tokens = Arrays.asList(Tokens.MORETHAN, Tokens.LESSTHAN,
                Tokens.MORETHANOREQUAL, Tokens.LESSTHANOREQUAL, Tokens.EQUALITY, Tokens.NOTEQUAL);
        while (tokens.contains(getLookaheadType(1))){
            switch (getLookaheadType(1)){
                case Tokens.MORETHAN: node = new MoreThanNode(node, match(Tokens.MORETHAN), ar()); break;
                case Tokens.LESSTHAN: node = new LessThanNode(node, match(Tokens.LESSTHAN), ar()); break;
                case Tokens.MORETHANOREQUAL: node = new MoreThanOrEqualNode(node, match(Tokens.MORETHANOREQUAL), ar()); break;
                case Tokens.LESSTHANOREQUAL: node = new LessThanOrEqualNode(node, match(Tokens.LESSTHANOREQUAL), ar()); break;
                case Tokens.EQUALITY: node = new EqualityNode(node, match(Tokens.EQUALITY), ar()); break;
                case Tokens.NOTEQUAL: node =  new InequalityNode(node, match(Tokens.NOTEQUAL), ar()); break;
            }
        }
        return node;
    }

    private ExprNode ar(){
        ExprNode node = term();
        while (getLookaheadType(1) == Tokens.PLUS || getLookaheadType(1)
        == Tokens.MINUS){
            if(getLookaheadType(1) == Tokens.PLUS){
                node = new AddNode(node, match(getLookaheadType(1)), term());
            }else{
                node =  new SubtractionNode(node, match(getLookaheadType(1)), term());
            }
        }
        return node;
    }

    private ExprNode term(){
        ExprNode node = factor();
        List<Integer> tokens = Arrays.asList(Tokens.MULTIPLICATION, Tokens.DIVISION,
                Tokens.MODULES);
        while (tokens.contains(getLookaheadType(1))){
            if(getLookaheadType(1) == Tokens.MULTIPLICATION){
                node = new MultiplicationNode(node, match(getLookaheadType(1)), factor());
            }else if(getLookaheadType(1) == Tokens.DIVISION){
                node = new DivisionNode(node, match(getLookaheadType(1)), factor());
            }
        }
        return node;
    }

    private ExprNode factor(){
        //TODO:: Alter the rule so can support function call and variable
        //factor: + | - | '(' expr ')' |  ('!'boolean_expr) | NUMBER| STRING | 'true' |
        // 'false' | Type(ID) ID | ID
        List<Integer> tokens = Arrays.asList(Tokens.NUMBER, Tokens.STRING, Tokens.BOOLEAN);
        if(tokens.contains(getLookaheadType(1))){
            switch (getLookaheadType(1)){
                case Tokens.NUMBER: return new IntNode(match(getLookaheadType(1)));
                case Tokens.STRING: return new StringNode(match(getLookaheadType(1)));
                case Tokens.BOOLEAN: return new BoolNode(match(getLookaheadType(1)));
            }
        }else if(getLookaheadType(1) == Tokens.PLUS){
            return new UnaryPositive(match(getLookaheadType(1)), expr());
        }else if(getLookaheadType(1) == Tokens.MINUS){
            return new UnaryNegative(match(getLookaheadType(1)), expr());
        }else if(getLookaheadType(1) == Tokens.ID){
            return new ID(match(getLookaheadType(1)));
        }
        else if(getLookaheadType(1) == Tokens.NOT){
            return new NotNode(match(getLookaheadType(1)), expr());
        }else if(getLookaheadType(1) == Tokens.OPENPARENTHESIS){
            match(Tokens.OPENPARENTHESIS);
            ExprNode exprNode = expr();
            match(Tokens.CLOSEPARENTHESIS);
            return exprNode;
        }else{
            throw new Error("Syntax Error");
        }
        return null;
    }

    private LayanAST statements(){ // function to determent which statement to parse using
        // lookahead buffer
        List<Integer> tokens = Arrays.asList(Tokens.ID, Tokens.TYPE, Tokens.FUNCTION, Tokens.CLASS,
                Tokens.IF, Tokens.FOR, Tokens.WHILE);
        List<Integer> declarationTokens = Arrays.asList(Tokens.TYPE, Tokens.CLASS, Tokens.FUNCTION);

        while (tokens.contains(getLookaheadType(1))){
            if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.EQUAL){
                assignmentStatements();
            }else if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.OPENPARENTHESIS){
                methodCall();
            }else if(declarationTokens.contains(getLookaheadType(1))){
                return declarationStatements();
            }else if(getLookaheadType(1) == Tokens.IF || getLookaheadType(1) == Tokens.WHILE){
                conditionStatements();
            }else if (getLookaheadType(1) == Tokens.FOR){
                iterationStatement();
            }
        }
        return new EOF(getLookaheadToken(1));
    }

    private LayanAST declarationStatements(){ // rule represent the declaration statements include
        // variable declaration and class declaration
        System.out.println("declarationStatements");
        if(getLookaheadType(1) == Tokens.TYPE){
            return variableDeclaration();
        }else if(getLookaheadType(1) == Tokens.FUNCTION) {
            return methodDeclaration();
        }else{
            return classDeclaration();
        }
    }

    private VariableDeclaration variableDeclaration(){
        //Variable Declaration Rule:
        // declaration_stat: DATA_TYPE ID ('=' expression)? ((',' ID)('=' expression)?)? ';'
        System.out.println("variableDeclaration");
        ID type = new ID(match(Tokens.TYPE));
        ID name = new ID(match(Tokens.ID));
        ExprNode exprNode = null;
        if(getLookaheadType(1) == Tokens.EQUAL){
            match(Tokens.EQUAL);
            exprNode = expr();
        }
        //TODO:: Rule for declaration list of variables
        while (getLookaheadType(1) == Tokens.COMMA){
            match(getLookaheadType(1));
            match(Tokens.ID);
            if(getLookaheadType(1) == Tokens.EQUAL){
                match(Tokens.EQUAL);
                expr();
            }
        }
        VariableDeclaration variableDeclaration = new VariableDeclaration(type, name, exprNode);
        match(Tokens.SEMICOLON);
        return variableDeclaration;
    }

    private LayanAST assignmentStatements(){
        // Assignment Statements Rule: assignment_stat: ID '=' expr
        System.out.println("assignmentStatements");
        ID name = new ID(match(Tokens.ID));
        Token equalToken = match(Tokens.EQUAL);
        ExprNode exprNode = expr();
        match(Tokens.SEMICOLON);
        return new EqualNode(name, equalToken, exprNode);
    }

    private List<LayanAST> functionStatements(){ // set of statements that can be inside the
        // function declaration
        List<Integer> tokens = Arrays.asList(Tokens.ID, Tokens.TYPE, Tokens.IF, Tokens.FOR, Tokens.WHILE);
        List<Integer> declarationTokens = Arrays.asList(Tokens.TYPE, Tokens.CLASS, Tokens.FUNCTION);
        List<LayanAST> layanASTList = new ArrayList<LayanAST>();
        while (tokens.contains(getLookaheadType(1))){
            if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.EQUAL){
                layanASTList.add(assignmentStatements());
            }else if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.OPENPARENTHESIS){
                methodCall();
            }else if(declarationTokens.contains(getLookaheadType(1))){
                layanASTList.add(declarationStatements());
            }else if(getLookaheadType(1) == Tokens.IF || getLookaheadType(1) == Tokens.WHILE){
                conditionStatements();
            }else if(getLookaheadType(1) == Tokens.FOR){
                iterationStatement();
            }else{
                throw new Error("Syntax Error");
            }
        }
        return layanASTList;
    }

    private MethodDeclaration methodDeclaration(){
        //'function' ID '(' parameters? ')' '{' function_statements '}'
        //parameters: Type ID (',' parameters)*
        //function_statements: declaration_statements | if_statement |
        // while_statement | for_statement
        System.out.println("methodDeclaration");
        Token functionToken = match(Tokens.FUNCTION);
        ID name = new ID(match(Tokens.ID));
        match(Tokens.OPENPARENTHESIS);

        List<VariableDeclaration> para = new ArrayList<>();
        parameters(para);

        match(Tokens.CLOSEPARENTHESIS);
        BlockNode blockNode = new BlockNode(match(Tokens.OPENCARLYBRACKET));
        blockNode.layanASTList.addAll(functionStatements());
        match(Tokens.CLOSECARLYBRACKET);

        return new MethodDeclaration(functionToken, name, blockNode);
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

    private void parameters(List<VariableDeclaration> para){
        // parameters: Type ID (',' parameters)*
        if(getLookaheadType(1) == Tokens.ID){
            para.add(variableDeclaration());
            //TODO::Init value for the args
            while (getLookaheadType(1) == Tokens.COMMA){
                parameters(para);
            }
        }
    }

    private List<LayanAST> classStatements(){// set of statements the can be inside the class declaration
        List<LayanAST> layanASTList = new ArrayList<LayanAST>();
        while (getLookaheadType(1) == Tokens.TYPE || getLookaheadType(1) == Tokens.FUNCTION){
            if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.EQUAL){
                layanASTList.add(assignmentStatements());
            }else if(getLookaheadType(1) == Tokens.TYPE){
                layanASTList.add(declarationStatements());
            }else if(getLookaheadType(1) == Tokens.FUNCTION){
                layanASTList.add(methodDeclaration());
            }else{
                throw new Error("Syntax Error");
            }
        }
        return layanASTList;
    }

    private ClassDeclaration classDeclaration(){
        //class_declaration: 'class' ID (':' TYPE) '{'class_statements'}'
        //class_statements: declaration_stat | method_declaration
        System.out.println("classDeclaration");
        Token classToken = match(Tokens.CLASS);
        Token name = match(Tokens.ID);
        ID superClass = null;
        if(getLookaheadType(1) == Tokens.COLON){
            match(getLookaheadType(1));
            superClass = new ID(match(Tokens.ID));
        }
        BlockNode blockNode = new BlockNode(match(Tokens.OPENCARLYBRACKET));
        blockNode.layanASTList.addAll(classStatements());
        match(Tokens.CLOSECARLYBRACKET);
        return new ClassDeclaration(classToken, name, blockNode, superClass);
    }

    private void objectDeclaration(){
        //object_declaration: ID(Type) ID '(' parameters ')'
        //parameters: declaration_stat (',' declaration_stat)*
        match(Tokens.ID);
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
