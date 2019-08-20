package Tokens;

public final class Tokens {
    public static final int EOF = -1;
    public static final int ID = 0;
    public static final int NUMBER = 1;
    public static final int EQUAL = 2;
    public static final int SEMICOLON = 3;
    public static final int FUNCTION = 4;
    public static final int OPENCARLYBRACKET = 5;
    public static final int CLOSECARLYBRACKET= 6;
    public static final int OPENPARENTHESIS = 7;
    public static final int CLOSEPARENTHESIS = 8;
    public static final int COMMA = 9;
    public static final int CLASS = 10;
    public static final int IF = 11;
    public static final int ELSE = 12;
    public static final int WHILE = 13;
    public static final int FOR = 14;
    public static final int STRING = 15;
    public static final int BOOLEAN = 16;
    public static final int MORETHAN = 17;
    public static final int LESSTHAN = 18;
    public static final int MORETHANOREQUAL = 19;
    public static final int LESSTHANOREQUAL = 20;
    public static final int NOT = 21;
    public static final int NOTEQUAL = 22;
    public static final int EQUALITY = 23;
    public static final int AND = 24;
    public static final int OR = 25;
    public static final int DOT = 26;
    public static final int PLUS = 27;
    public static final int MINUS = 28;
    public static final int DIVISION = 29;
    public static final int MULTIPLICATION = 30;
    public static final int MODULES = 31;
    public static final int COLON = 32;

public static String getTokenName(int type){
    switch (type){
        case EOF: return "EOF";
        case ID: return "ID";
        case NUMBER: return "NUMBER";
        case EQUAL: return "=";
        case SEMICOLON:return ";";
        case FUNCTION:return "FUNCTION";
        case OPENCARLYBRACKET:return "{";
        case CLOSECARLYBRACKET:return "}";
        case OPENPARENTHESIS:return "(";
        case CLOSEPARENTHESIS:return ")";
        case COMMA:return ",";
        case CLASS:return "CLASS";
        case IF:return "IF";
        case ELSE:return "ELSE";
        case WHILE:return "WHILE";
        case FOR:return "FOR";
        case STRING:return "STRING";
        case BOOLEAN:return "BOOLEAN";
        case MORETHAN:return ">";
        case LESSTHAN:return "<";
        case MORETHANOREQUAL:return ">=";
        case LESSTHANOREQUAL:return "<=";
        case NOT:return "!";
        case NOTEQUAL:return "!=";
        case EQUALITY:return "==";
        case AND :return "&&";
        case OR:return "||";
        case DOT:return ".";
        case PLUS:return "+";
        case MINUS:return "-";
        case DIVISION:return "/";
        case MULTIPLICATION:return "*";
        case MODULES:return "%";
        case COLON:return ":";
        default: return "Unknown Token";
        }
    }
}
