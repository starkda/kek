package com.example.kek.semantic.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;
import java.util.Objects;

public class Assignment extends ASTNode{

    public ModifiablePrimary getModifiablePrimary() {
        return modifiablePrimary;
    }

    public Expression getExpression() {
        return expression;
    }

    private ModifiablePrimary modifiablePrimary;
    private Expression expression;
    public Assignment(Token currentToken, List<Token> categorizedTokens) throws Exception {
        super(currentToken, categorizedTokens);
        createNode();
    }

    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(this.categorizedTokens);
        if(currentToken.getClass() == com.example.kek.lexical.analyzer.token.Identifier.class){
            this.modifiablePrimary = new ModifiablePrimary(currentToken, categorizedTokens, currentPosition);
            currentPosition = modifiablePrimary.lastToken.getOrderInTokenList(categorizedTokens) + 1;
            if (Objects.equals(categorizedTokens.get(currentPosition).getCode(), ":=")){
                currentPosition++;
                this.expression = new Expression(categorizedTokens.get(currentPosition), categorizedTokens);
                this.lastToken = this.expression.lastToken;
            }
            else throw new Exception("Error: illegal declaration in Assignment    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                    ", but expected ':=' ");
        }
        else throw new Exception("Error: illegal declaration in Assignment    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                ", but expected Identifier");
    }
}
