package com.example.kek.semantic.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;
import java.util.Objects;

public class TypeDeclaration extends ASTNode {
    public ASTIdentifier getIdent() {
        return ident;
    }

    public Type getType() {
        return type;
    }

    private ASTIdentifier ident;
    private Type type;
    public TypeDeclaration(Token token, List<Token> categorizedTokens) throws Exception {
        super(token, categorizedTokens);
        createNode();
    }


    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(this.categorizedTokens);
        if(!Objects.equals(currentToken.getCode(), "type"))
            throw new Exception("Error: not a  TypeDeclaration  " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                    ", but expected TypeDeclaration");
        currentPosition++;
        this.ident = new ASTIdentifier(categorizedTokens.get(currentPosition).getCode(), String.valueOf(categorizedTokens.get(currentPosition).getLine()));
        currentPosition++;
        if(!Objects.equals(categorizedTokens.get(currentPosition).getCode(), "is"))
        {
            throw new Exception("Error: not a  TypeDeclaration  " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                    ", but expected 'is' ");
        }
        currentPosition++;
        this.type = new Type(categorizedTokens.get(currentPosition), categorizedTokens);
        this.lastToken = this.type.lastToken;
    }
}
