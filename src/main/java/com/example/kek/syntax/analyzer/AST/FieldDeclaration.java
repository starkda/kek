package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;

public class FieldDeclaration extends VariableDeclaration{
    public FieldDeclaration(Token token, List<Token> categorizedTokens) throws Exception {
        super(token, categorizedTokens);
    }
}
