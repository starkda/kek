package com.example.kek.semantic.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public abstract class ASTNode {

    protected  List<Token> categorizedTokens;
    protected List<Token> code;

    protected Token lastToken;
    protected Token currentToken;

    public ASTNode(Token currentToken, List<Token> categorizedTokens) {
        this.currentToken = currentToken;
        this.categorizedTokens = categorizedTokens;
    }

    ASTNode(){
    }

    abstract void createNode() throws Exception;

}
