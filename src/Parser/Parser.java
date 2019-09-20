package Parser;

import LayanAST.Conditions.ConditionNode;
import LayanAST.Conditions.IterationNode;
import LayanAST.Declarations.*;
import LayanAST.Expressions.*;
import LayanAST.LayanAST;
import LayanAST.Program;
import LayanAST.Print;
import Lexer.Lexer;
import Symbols.BuiltInTypeSymbol;
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
        return program();
    } // start parse layan statements

    // start arithmetic expression rule
    private ExprNode expr(){
        //expr: and (('||') and)*
        //and: comp (('&&') and)*
        //comp: ar(('>' | '<' | '>=' | '<=' | '!=' | '==') ar)
        //ar: term (('+'|'-') term)*
        //term: factor (('*', '/', '%') factor)*
        //factor: + | - | '(' expr ')' |  ('!'boolean_expr) | INT | FLOAT | STRING | 'true' | 'false'
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
        //factor: + | - | '(' expr ')' |  ('!'boolean_expr) | FLOAT| INT | STRING | 'true' |
        // 'false' | Type(ID) ID | ID | resolution_object
        List<Integer> tokens = Arrays.asList(Tokens.FLOAT, Tokens.INT, Tokens.STRING, Tokens.BOOLEAN);
        if(tokens.contains(getLookaheadType(1))){
            switch (getLookaheadType(1)){
                case Tokens.FLOAT: return new FloatNode(match(getLookaheadType(1)));
                case Tokens.INT: return new IntNode(match(getLookaheadType(1)));
                case Tokens.STRING: return new StringNode(match(getLookaheadType(1)));
                case Tokens.BOOLEAN: return new BoolNode(match(getLookaheadType(1)));
            }
        }else if(getLookaheadType(1) == Tokens.PLUS){
            return new UnaryPositive(match(getLookaheadType(1)), factor());
        }else if(getLookaheadType(1) == Tokens.MINUS){
            return new UnaryNegative(match(getLookaheadType(1)), factor());
        }else if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.DOT){
            return resolutionObject();
        }
        else if(getLookaheadType(1) == Tokens.ID){
            return new ID(match(getLookaheadType(1)));
        }
        else if(getLookaheadType(1) == Tokens.NOT){
            return new NotNode(match(getLookaheadType(1)), expr());
        }else if(getLookaheadType(1) == Tokens.OPENPARENTHESIS){
            match(Tokens.OPENPARENTHESIS);
            ExprNode exprNode = expr();
            match(Tokens.CLOSEPARENTHESIS);
            return exprNode;
        }
        return null;
    }

    private Program program(){
        return statements();
    }

    private Program statements(){ // function to determent which statement to parse using
        // lookahead buffer
        List<Integer> tokens = Arrays.asList(Tokens.ID, Tokens.TYPE, Tokens.FUNCTION, Tokens.CLASS,
                Tokens.IF, Tokens.FOR, Tokens.WHILE, Tokens.PRINT);
        List<Integer> declarationTokens = Arrays.asList(Tokens.TYPE, Tokens.ID, Tokens.CLASS, Tokens.FUNCTION);

        List<LayanAST> programStatements = new ArrayList<LayanAST>();

        while (tokens.contains(getLookaheadType(1))){
            if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.EQUAL){
                programStatements.add(assignmentStatements());
            }else if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.OPENPARENTHESIS){
                programStatements.add(functionCallStatement());
            }else if(getLookaheadType(1) == Tokens.ID &&
                    getLookaheadType(2) == Tokens.DOT &&
                    getLookaheadType(3) == Tokens.ID &&
                    getLookaheadType(4) == Tokens.EQUAL){
                programStatements.add(assignmentStatements());
            }else if(getLookaheadType(1) == Tokens.ID &&
                    getLookaheadType(2) == Tokens.DOT){
                programStatements.add(resolutionStatement());
            }else if(declarationTokens.contains(getLookaheadType(1))){
                programStatements.add(declarationStatements());
            }else if(getLookaheadType(1) == Tokens.IF || getLookaheadType(1) == Tokens.WHILE){
                programStatements.add(conditionStatements());
            }else if (getLookaheadType(1) == Tokens.FOR){
                programStatements.add(iterationStatement());
            }else if(getLookaheadType(1) == Tokens.PRINT){
                programStatements.add(printStatement());
            }
        }
        //return new EOF(getLookaheadToken(1));
        return new Program(new Token("Program", Tokens.PROGRAM), programStatements);
    }

    private Print printStatement(){
        Token token = match(Tokens.PRINT);
        match(Tokens.OPENPARENTHESIS);
        ExprNode exprNode = expr();
        match(Tokens.CLOSEPARENTHESIS);
        match(Tokens.SEMICOLON);
        return new Print(token, exprNode);
    }

    private LayanAST declarationStatements(){ // rule represent the declaration statements include
        // variable declaration and class declaration
        if(getLookaheadType(1) == Tokens.TYPE){
            return variableDeclaration();
        }else if(getLookaheadType(1) == Tokens.FUNCTION) {
            return methodDeclaration();
        }else if(getLookaheadType(1) == Tokens.ID){
            return objectDeclaration();
        }else{
            return classDeclaration();
        }
    }

    private VariableDeclarationList variableDeclaration(){
        //Variable Declaration Rule:
        // declaration_stat: DATA_TYPE ID ('=' expression)? ((',' ID)('=' expression)?)? ';'

        List<VariableDeclaration> variableDeclarations = new ArrayList<>();

        ID type = new ID(match(Tokens.TYPE));
        ID name = new ID(match(Tokens.ID));
        name.evalType = new BuiltInTypeSymbol(type.name.text);
        ExprNode exprNode = null;
        if(getLookaheadType(1) == Tokens.EQUAL){
            match(Tokens.EQUAL);
            exprNode = expr();
        }
        variableDeclarations.add(new VariableDeclaration(type, name, exprNode));
        while (getLookaheadType(1) == Tokens.COMMA){
            match(getLookaheadType(1));
            name = new ID(match(Tokens.ID));
            if(getLookaheadType(1) == Tokens.EQUAL){
                match(Tokens.EQUAL);
                exprNode = expr();
            }
            variableDeclarations.add(new VariableDeclaration(type, name, exprNode));
        }
        match(Tokens.SEMICOLON);
        return new VariableDeclarationList(type, variableDeclarations);
    }

    private LayanAST assignmentStatements(){
        // Assignment Statements Rule: assignment_stat: (ID | ID '.' ID) '=' expression

        ID name = new ID(match(Tokens.ID));
        ID member = null;
        if(getLookaheadToken(1).type == Tokens.DOT &&
        getLookaheadToken(2).type == Tokens.ID){
            match(getLookaheadType(1));
            member = new ID(match(getLookaheadType(1)));
        }
        Token equalToken = match(Tokens.EQUAL);
        ExprNode exprNode = expr();
        match(Tokens.SEMICOLON);
        EqualNode equalNode = new EqualNode(name, equalToken, exprNode);
        equalNode.member = new ResolutionObject(name, member);
        return equalNode;
    }

    private ResolutionObject resolutionStatement(){
        ResolutionObject resolutionObject = resolutionObject();
        match(Tokens.SEMICOLON);
        return resolutionObject;
    }

    private ResolutionObject resolutionObject(){

        ID type = new ID(match(Tokens.ID));
        match(Tokens.DOT);
        ID member;
        ResolutionObject resolutionObject;
        // function call member
        FunctionCall functionCall =  null;
        if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.OPENPARENTHESIS){
            functionCall = functionCall();
            member = functionCall.id; // get function name
        }
        else member = new ID(match(Tokens.ID));
        resolutionObject = new ResolutionObject(type, member);
        resolutionObject.functionCall = functionCall;
        return resolutionObject;
    }

    private ReturnNode returnStatement(){
        Token token = match(Tokens.RETURN);
        match(Tokens.SEMICOLON);
        return new ReturnNode(token);
    }

    private List<LayanAST> functionStatements(){ // set of statements that can be inside the
        // function declaration
        List<Integer> tokens = Arrays.asList(Tokens.ID, Tokens.TYPE, Tokens.IF,
                Tokens.FOR, Tokens.WHILE, Tokens.PRINT, Tokens.RETURN);
        List<Integer> declarationTokens = Arrays.asList(Tokens.TYPE, Tokens.ID);
        List<LayanAST> layanASTList = new ArrayList<LayanAST>();
        while (tokens.contains(getLookaheadType(1))){
            if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.EQUAL){
                layanASTList.add(assignmentStatements());
            }else if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.OPENPARENTHESIS){
                layanASTList.add(functionCallStatement());
            }else if(getLookaheadType(1) == Tokens.ID &&
                    getLookaheadType(2) == Tokens.DOT &&
                    getLookaheadType(3) == Tokens.ID &&
                    getLookaheadType(4) == Tokens.EQUAL){
                layanASTList.add(assignmentStatements());
            }else if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.DOT){
                layanASTList.add(resolutionStatement());
            }else if(declarationTokens.contains(getLookaheadType(1))){
                layanASTList.add(declarationStatements());
            }else if(getLookaheadType(1) == Tokens.IF || getLookaheadType(1) == Tokens.WHILE){
                layanASTList.add(conditionStatements());
            }else if(getLookaheadType(1) == Tokens.FOR){
                layanASTList.add(iterationStatement());
            }else if(getLookaheadType(1) == Tokens.PRINT){
                layanASTList.add(printStatement());
            }
            else if(getLookaheadType(1) == Tokens.RETURN){
                layanASTList.add(returnStatement());
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
        Token functionToken = match(Tokens.FUNCTION);
        ID name = new ID(match(Tokens.ID));
        match(Tokens.OPENPARENTHESIS);

        List<VariableDeclaration> para = new ArrayList<>();
        parameters(para); // get all parameters

        match(Tokens.CLOSEPARENTHESIS);
        BlockNode blockNode = new BlockNode(match(Tokens.OPENCARLYBRACKET));
        blockNode.layanASTList.addAll(functionStatements());

        match(Tokens.CLOSECARLYBRACKET);
        MethodDeclaration methodDeclaration = new MethodDeclaration(functionToken, name, blockNode);
        methodDeclaration.parameters = para;

        return methodDeclaration;
    }

    private ExprNode methodCallParameters(){ // Work for class and method parameters
        return expr();
    }

    private void methodCall(){//TODO:: Delete this code.txt
        //ID '(' (expr ',')'* ')' ';'
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
        if(getLookaheadType(1) == Tokens.TYPE || getLookaheadType(1) == Tokens.ID){
            VariableDeclaration variableDeclaration =
                    new VariableDeclaration(new ID(match(getLookaheadType(1))), new ID(match(getLookaheadType(1))));
            //TODO::Init value for the args
            para.add(variableDeclaration);
            while (getLookaheadType(1) == Tokens.COMMA){
                match(getLookaheadType(1));
                parameters(para);
            }
        }
    }

    private List<LayanAST> classStatements(){// set of statements the can be inside the class declaration
        List<LayanAST> layanASTList = new ArrayList<LayanAST>();
        List<Integer> tokens = Arrays.asList(Tokens.ID, Tokens.TYPE, Tokens.FUNCTION);
        while (tokens.contains(getLookaheadType(1))){
            if(getLookaheadType(1) == Tokens.ID && getLookaheadType(2) == Tokens.EQUAL){
                layanASTList.add(assignmentStatements());
            }else if(getLookaheadType(1) == Tokens.TYPE){
                layanASTList.add(declarationStatements());
            }else if(getLookaheadType(1) == Tokens.FUNCTION){
                layanASTList.add(methodDeclaration());
            }else if(getLookaheadType(1) == Tokens.ID){
                layanASTList.add(objectDeclaration());
            }else{
                throw new Error("Syntax Error");
            }
        }
        return layanASTList;
    }

    private ClassDeclaration classDeclaration(){
        //class_declaration: 'class' ID (':' TYPE) '{'class_statements'}'
        //class_statements: declaration_stat | method_declaration
        Token classToken = match(Tokens.CLASS);
        ID name = new ID(match(Tokens.ID));
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

    private ObjectDeclaration objectDeclaration(){
        //object_declaration: Type(ID) ID '=' Type '(' ')' ';'
        //parameters: declaration_stat (',' declaration_stat)*
        ID type = new ID(match(Tokens.ID));
        ID name = new ID(match(Tokens.ID));
        name.evalType = new BuiltInTypeSymbol("void");
        if(getLookaheadType(1) == Tokens.EQUAL){
            match(getLookaheadType(1));
            match(Tokens.ID);
            match(Tokens.OPENPARENTHESIS);
            match(Tokens.CLOSEPARENTHESIS);
        }
        match(Tokens.SEMICOLON);
        return new ObjectDeclaration(type, name);
    }

    private ConditionNode conditionStatements(){
        //if_statement|while_statement: ('if'|'while) '(' boolean_expression ')' '{' statements '}'
        // ('else' '{' statements '}')?
        ConditionNode conditionNode = null;
        Token conditionToken;
        if(getLookaheadType(1) == Tokens.IF) conditionToken = match(Tokens.IF);
        else conditionToken = match(Tokens.WHILE);
        match(Tokens.OPENPARENTHESIS);
        ExprNode exprNode = expr();
        match(Tokens.CLOSEPARENTHESIS);

        BlockNode truePartBlock = new BlockNode(match(Tokens.OPENCARLYBRACKET));
        BlockNode falsePartBlock = null;
        truePartBlock.layanASTList.addAll(functionStatements()); // also the same statements fot ifCondition
        match(Tokens.CLOSECARLYBRACKET);

        // Check if there is an else part

        if(getLookaheadType(1) == Tokens.ELSE){
            match(getLookaheadType(1));
            falsePartBlock = new BlockNode(match(Tokens.OPENCARLYBRACKET));
            falsePartBlock.layanASTList.addAll(functionStatements());
            match(Tokens.CLOSECARLYBRACKET);
        }

        conditionNode = new ConditionNode(exprNode, conditionToken,truePartBlock, falsePartBlock);
        return conditionNode;
    }

    private IterationNode iterationStatement(){
        //for_statement: 'for' '(' variable_declaration boolean_expression ; statement')'
        // '{'stat'}'
        //stat:statements
        //statement: assignment_stat
        Token iterationToken = match(Tokens.FOR);
        match(Tokens.OPENPARENTHESIS);
        VariableDeclaration iterationVar = variableDeclaration().variableDeclarations.get(0);
        ExprNode exprNode = expr();
        match(Tokens.SEMICOLON);
        //TODO:: Make it work for function call
        EqualNode equalNode;
        ID name = new ID(match(Tokens.ID));
        Token equalToken = match(Tokens.EQUAL);
        ExprNode expr = expr();
        equalNode = new EqualNode(name, equalToken, expr);

        match(Tokens.CLOSEPARENTHESIS);
        BlockNode blockNode = new BlockNode(match(Tokens.OPENCARLYBRACKET));
        blockNode.layanASTList.addAll(functionStatements()); // also work for this rule
        match(Tokens.CLOSECARLYBRACKET);

        return new IterationNode(iterationVar, exprNode, iterationToken, equalNode, blockNode);
    }

    private FunctionCall functionCallStatement(){
        FunctionCall functionCall = functionCall();
        match(Tokens.SEMICOLON);
        return functionCall;
    }

    private FunctionCall functionCall(){
        //function_call: ID '(' ((expression) ',')'* ')'
        ID name = new ID(match(getLookaheadType(1)));
        List<ExprNode> args = new ArrayList<>();
        match(Tokens.OPENPARENTHESIS);
        ExprNode exprNode = expr();
        if(exprNode != null) args.add(exprNode);
        while (getLookaheadType(1) == Tokens.COMMA){
            match(getLookaheadType(1));
            exprNode = expr();
            if(exprNode != null) args.add(exprNode);
        }
        match(Tokens.CLOSEPARENTHESIS);
        return new FunctionCall(name, args);
    }

}
