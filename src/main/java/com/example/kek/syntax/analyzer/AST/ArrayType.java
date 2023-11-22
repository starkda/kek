package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;
import java.util.Objects;

public class ArrayType extends Type {
    private Expression exp;
    private Type type;
    public ArrayType(Token token, String code, List<Token> categorizedTokens) throws Exception {
        int currentPosition = token.getOrderInTokenList(categorizedTokens);
        if (!Objects.equals(categorizedTokens.get(currentPosition).getCode(), "array"))
            throw new Exception("Error ArrayType declaration    " + categorizedTokens.get(currentPosition).showCodeLinePosition()
                    + ", but expected 'array'");
        currentPosition++;
        if (!Objects.equals(categorizedTokens.get(currentPosition).getCode(), "["))
            throw new Exception("Error ArrayType declaration    " + categorizedTokens.get(currentPosition).showCodeLinePosition()
                    + ", but expected '['");
        currentPosition++;
        if (Objects.equals(categorizedTokens.get(currentPosition).getCode(), "]")){
            currentPosition++;
            this.type = new Type(categorizedTokens.get(currentPosition), categorizedTokens);
            this.lastToken = this.type.lastToken;
            return ;
        }
        this.exp = new Expression(categorizedTokens.get(currentPosition), categorizedTokens);
        currentPosition = this.exp.lastToken.getOrderInTokenList(categorizedTokens) + 1;
        if (!Objects.equals(categorizedTokens.get(currentPosition).getCode(), "]"))
            throw new Exception("Error ArrayType declaration    " + categorizedTokens.get(currentPosition).showCodeLinePosition()
                    + ", but expected ']'");
        currentPosition++;
        this.type = new Type(categorizedTokens.get(currentPosition), categorizedTokens);
        currentPosition = this.type.lastToken.getOrderInTokenList(categorizedTokens);
        this.lastToken = categorizedTokens.get(currentPosition);
    }

    public Expression getExp() {
        return exp;
    }

    @Override
    public Type getType() {
        return type;
    }
}
