package com.example.kek.semantic.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;
import java.util.Objects;

public class ForLoop extends ASTNode {

    private ASTIdentifier ident;
    private Range range;
    private Body body;
    public ForLoop(Token currentToken, List<Token> categorizedTokens) throws Exception {
        super(currentToken, categorizedTokens);
        createNode();
    }

    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(this.categorizedTokens);
        if(Objects.equals(currentToken.getCode(), "for")){
            currentPosition++;
            this.ident = new ASTIdentifier(categorizedTokens.get(currentPosition).getCode(), String.valueOf(categorizedTokens.get(currentPosition).getLine()));
            currentPosition++;
            this.range = new Range(categorizedTokens.get(currentPosition), categorizedTokens);
            currentPosition = range.lastToken.getOrderInTokenList(categorizedTokens) + 1;
            if(Objects.equals(categorizedTokens.get(currentPosition).getCode(), "loop")){
                currentPosition++;
                this.body = new Body(categorizedTokens.get(currentPosition), categorizedTokens);
                currentPosition = body.lastToken.getOrderInTokenList(categorizedTokens) + 1;
                if(Objects.equals(categorizedTokens.get(currentPosition).getCode(), "end")){
                    lastToken = categorizedTokens.get(currentPosition);
                }
                else throw new Exception("Error: illegal declaration in ForLoop    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                        ", but expected 'end'");
            }
            else throw new Exception("Error: illegal declaration in ForLoop    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                    ", but expected 'loop'");
        }
        else throw new Exception("Error: illegal declaration in ForLoop    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                ", but expected 'for'");
    }

    public ASTIdentifier getIdent() {
        return ident;
    }

    public Range getRange() {
        return range;
    }

    public Body getBody() {
        return body;
    }
}
