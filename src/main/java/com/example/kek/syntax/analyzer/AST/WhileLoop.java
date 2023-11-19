package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;
import java.util.Objects;

public class WhileLoop extends ASTNode{

    private ASTIdentifier ident;
    private Body body;
    public WhileLoop(Token currentToken, List<Token> categorizedTokens) throws Exception {
        super(currentToken, categorizedTokens);
        createNode();
    }

    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(this.categorizedTokens);
        if(Objects.equals(currentToken.getCode(), "while")){
            currentPosition++;
            this.ident = new ASTIdentifier(categorizedTokens.get(currentPosition).getCode(), String.valueOf(categorizedTokens.get(currentPosition).getLine()));
            currentPosition++;
            if(Objects.equals(categorizedTokens.get(currentPosition).getCode(), "loop")){
                currentPosition++;
                this.body = new Body(categorizedTokens.get(currentPosition), categorizedTokens);
                currentPosition = body.lastToken.getOrderInTokenList(categorizedTokens);
                if(Objects.equals(categorizedTokens.get(currentPosition).getCode(), "end")){
                    lastToken = categorizedTokens.get(currentPosition);
                }
                else throw new Exception("Error: illegal declaration in WhileLoop    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                        ", but expected 'end'");
            }
            else throw new Exception("Error: illegal declaration in WhileLoop    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                    ", but expected 'loop'");
        }
        else throw new Exception("Error: illegal declaration in WhileLoop    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                ", but expected 'while'");
    }
}
