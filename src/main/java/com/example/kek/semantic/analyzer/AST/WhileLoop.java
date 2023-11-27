package com.example.kek.semantic.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;
import java.util.Objects;

public class WhileLoop extends ASTNode{

    private Expression expression;
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
            this.expression = new Expression(categorizedTokens.get(currentPosition), categorizedTokens);
            currentPosition = expression.lastToken.getOrderInTokenList(categorizedTokens) + 1;
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

    public Expression getExpression() {
        return expression;
    }

    public Body getBody() {
        return body;
    }
}
