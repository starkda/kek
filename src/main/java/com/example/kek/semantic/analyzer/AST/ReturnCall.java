package com.example.kek.semantic.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class ReturnCall extends ASTNode {
    private Expression expression;
    public ReturnCall(Token currentToken, List<Token> categorizedTokens) throws Exception {
        super(currentToken, categorizedTokens);
        createNode();
    }

    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(this.categorizedTokens);
        if(Objects.equals(currentToken.getCode(), "return")) {
            currentPosition++;
            this.expression = new Expression(categorizedTokens.get(currentPosition), categorizedTokens);
            this.lastToken = this.expression.lastToken;
        }
        else throw new Exception("Error: illegal declaration in  ReturnCall   " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                ", but expected 'return'");
    }
}
