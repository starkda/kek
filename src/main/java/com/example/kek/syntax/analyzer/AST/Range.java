package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;
import java.util.Objects;

public class Range extends ASTNode {
    private boolean reverse = false;
    private Expression startExp;
    private Expression endExp;
    public Range(Token currentToken, List<Token> categorizedTokens) throws Exception {
        super(currentToken, categorizedTokens);
        createNode();
    }

    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(this.categorizedTokens);
        if(Objects.equals(currentToken.getCode(), "in")){
            currentPosition++;
            if(Objects.equals(categorizedTokens.get(currentPosition).getCode(), "reverse"))
                this.reverse = true;
            startExp = new Expression(categorizedTokens.get(currentPosition), categorizedTokens);
            currentPosition = startExp.lastToken.getOrderInTokenList(categorizedTokens) + 1;
            if(Objects.equals(categorizedTokens.get(currentPosition).getCode(), "..")){
                currentPosition++;
                endExp = new Expression(categorizedTokens.get(currentPosition), categorizedTokens);
                lastToken = endExp.lastToken;
            }
            else throw new Exception("Error: illegal declaration in Range    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                    ", but expected '..'");
        }
        else throw new Exception("Error: illegal declaration in Range    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                ", but expected 'in'");
    }
}
