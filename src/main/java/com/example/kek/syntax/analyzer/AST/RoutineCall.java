package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;
import java.util.Objects;

public class RoutineCall extends ASTNode {

    private ASTIdentifier ident;
    private List<Token> some_sheet;
    public RoutineCall(Token currentToken, List<Token> categorizedTokens) throws Exception {
        super(currentToken, categorizedTokens);
        createNode();
    }

    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(this.categorizedTokens);
        if(currentToken.getClass() == com.example.kek.lexical.analyzer.token.Identifier.class){
            this.ident = new ASTIdentifier(currentToken.getCode(), String.valueOf(currentToken.getLine()));
            currentPosition++;
            if(Objects.equals(categorizedTokens.get(currentPosition).getCode(), "(")){
                while(true){
                    currentPosition++;
                    if(currentPosition == categorizedTokens.size())
                        throw new Exception("Error: illegal declaration in RoutineCall, but expected RoutineCall declaration");
                    if(Objects.equals(categorizedTokens.get(currentPosition).getCode(), ")")){
                        this.lastToken = categorizedTokens.get(currentPosition);
                        categorizedTokens.get(currentPosition);
                        break;
                    }
                    some_sheet.add(categorizedTokens.get(currentPosition));
                }
            }
            else throw new Exception("Error: illegal declaration in RoutineCall    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                    ", but expected '('");
        }
        else throw new Exception("Error: illegal declaration in RoutineCall    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                ", but expected Identifier");
    }
}
