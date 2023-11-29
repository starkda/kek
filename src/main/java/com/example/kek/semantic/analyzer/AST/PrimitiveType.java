package com.example.kek.semantic.analyzer.AST;

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

    public PrimitiveType(String value) throws Exception {
        if (value.equals("true") || value.equals("false"))
            this.type = "boolean";
        else if (value.contains("."))
            this.type = "real";

        else
            this.type = "integer";

    }


    public String getTypePrim() {
        return this.type;
    }
}
