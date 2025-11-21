package net.cybercake.hystats.commands.processors.filter;

import jdk.nashorn.internal.parser.TokenType;

public class Token {

    public enum Type {
        /* vars */ VARIABLE, NUMERIC_LITERAL, DOUBLE_LITERAL,
        /* oper */ EQUAL, NOT_EQUAL, GREATER_THAN, LESS_THAN, GREATER_THAN_EQUAL, LESS_THAN_EQUAL,
        /* bool */ AND, OR,
        /* sigs */ SIG_EOF
    }

    public final String token;
    public final TokenType type;

    public Token(String token, TokenType type) {
        this.token = token;
        this.type = type;
    }

}
