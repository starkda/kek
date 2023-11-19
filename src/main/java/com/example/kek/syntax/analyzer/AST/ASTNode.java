package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.ArrayList;
import java.util.List;

public abstract class ASTNode {

    protected final List<Token> categorizedTokens;
    protected List<Token> code;

    protected Token lastToken;
    protected Token currentToken;

    public ASTNode(Token currentToken, List<Token> categorizedTokens) {
        this.currentToken = currentToken;
        this.categorizedTokens = categorizedTokens;
    }

    abstract void createNode() throws Exception;

}
