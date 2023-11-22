package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;

public class PrimitiveType extends Type {

    private String type;


    /**
     * Type - integer, real, boolean
     * На 17.10.2023.
     *
     * @param token
     */
    public PrimitiveType(Token token, String type) throws Exception {
        this.type = type;
        this.line = token.getLine();
        this.position = token.getPosition();
        this.lastToken = token;
    }

    public PrimitiveType(Token token, List<Token> categorizedTokens) throws Exception {
        super(token, categorizedTokens);
    }



    public String getTypePrim() {
        return this.type;
    }
}
