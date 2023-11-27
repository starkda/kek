package com.example.kek.semantic.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;
import java.util.Objects;

public class IfStatement extends ASTNode{

    private Expression expression;
    private Body bodyThen;
    private Body bodyElse;
    public IfStatement(Token currentToken, List<Token> categorizedTokens) throws Exception {
        super(currentToken, categorizedTokens);
        createNode();
    }

    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(this.categorizedTokens);
        if(Objects.equals(currentToken.getCode(), "if")){
            currentPosition++;
            this.expression = new Expression(categorizedTokens.get(currentPosition), categorizedTokens);
            currentPosition = expression.lastToken.getOrderInTokenList(categorizedTokens) + 1;
            if(Objects.equals(categorizedTokens.get(currentPosition).getCode(), "then")){
                currentPosition++;

                this.bodyThen = new Body(categorizedTokens.get(currentPosition), categorizedTokens);
                currentPosition = bodyThen.lastToken.getOrderInTokenList(categorizedTokens) + 1;

                if(Objects.equals(categorizedTokens.get(currentPosition).getCode(), "else")){
                    currentPosition++;
                    this.bodyElse = new Body(categorizedTokens.get(currentPosition), categorizedTokens);
                    currentPosition = bodyElse.lastToken.getOrderInTokenList(categorizedTokens) + 1;
                }

                if(Objects.equals(categorizedTokens.get(currentPosition).getCode(), "end")){
                    lastToken = categorizedTokens.get(currentPosition);
                }
                else throw new Exception("Error: illegal declaration in ForLoop    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                        ", but expected 'end'");
            }
            else throw new Exception("Error: illegal declaration in ForLoop    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                    ", but expected 'loop'");
        }
        else throw new Exception("Error: illegal declaration in IfStatement    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                ", but expected 'if'");
    }

    public Expression getExpression() {
        return expression;
    }

    public Body getBodyThen() {
        return bodyThen;
    }

    public Body getBodyElse() {
        return bodyElse;
    }
}
