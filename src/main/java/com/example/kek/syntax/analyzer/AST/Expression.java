package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Expression extends ASTNode{
    private final List<Token> tokens = new ArrayList<>();
    protected Token lastToken;

    public Expression(Token token, List<Token> categorizedTokens) throws Exception {
        super(token, categorizedTokens);
        defineTokens(token, categorizedTokens);
        if(!Objects.equals(this.tokens.get(this.tokens.size() - 1).getCode(), "]"))
            this.lastToken = this.tokens.get(this.tokens.size() - 1);
        else this.lastToken = this.tokens.get(this.tokens.size() - 2);
    }

    @Override
    void createNode() throws Exception {

    }

    private void defineTokens(Token token, List<Token> categorizedTokens) throws Exception {
        int squareBrackets = 0;
        int currentPosition = token.getOrderInTokenList(categorizedTokens);
        while (true) {
            if (currentPosition == categorizedTokens.size() || categorizedTokens.get(currentPosition).getClass().equals(com.example.kek.lexical.analyzer.token.KeyWord.class) && !Objects.equals(categorizedTokens.get(currentPosition).getCode(), "true") && !Objects.equals(categorizedTokens.get(currentPosition).getCode(), "false") || Objects.equals(categorizedTokens.get(currentPosition).getCode(), "..")) { // KeyWord или последний глобальный экспрешен.
                break;
            }
            if(categorizedTokens.get(currentPosition).getCode().equals("["))
                squareBrackets++;
            if(categorizedTokens.get(currentPosition).getCode().equals("]")) {
                if (squareBrackets > 0)
                    squareBrackets--;
                else break;
            }
            if (Objects.equals(categorizedTokens.get(currentPosition).getCode(), ":= ")) {
                this.tokens.remove(tokens.size() - 1);
                break;
            }
            tokens.add(categorizedTokens.get(currentPosition));
            currentPosition++;
        }

    }
}
