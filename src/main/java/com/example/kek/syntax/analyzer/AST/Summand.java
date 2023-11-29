package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;
import lombok.Getter;

import java.util.List;

@Getter
public class Summand extends ASTNode {

    private Boolean boolLiteral = true;
    private String type = "";
    private int intLiteral = 0;
    private double realLiteral = 0.0;
    private ModifiablePrimary modifiablePrimary;
    private RoutineCall routineCall;

    public Summand(Token currentToken, List<Token> categorizedTokens) throws Exception {
        super(currentToken, categorizedTokens);
        createNode();
    }

    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(categorizedTokens);
        if (categorizedTokens.get(currentPosition).getCode().equals("true") || categorizedTokens.get(currentPosition).getCode().equals("false")) {
            this.type = "boolean";
            this.boolLiteral = Boolean.parseBoolean(categorizedTokens.get(currentPosition).getCode());
            this.lastToken = categorizedTokens.get(currentPosition);
        } else if (categorizedTokens.get(currentPosition).getClass().equals(com.example.kek.lexical.analyzer.token.Literal.class)) {
            if (categorizedTokens.get(currentPosition).getCode().contains(".")) {
                this.type = "real";
                this.realLiteral = Double.parseDouble(categorizedTokens.get(currentPosition).getCode());

            } else {
                this.type = "integer";
                this.intLiteral = Integer.parseInt(categorizedTokens.get(currentPosition).getCode());
            }
            this.lastToken = categorizedTokens.get(currentPosition);
        } else if (categorizedTokens.get(currentPosition).getClass().equals(com.example.kek.lexical.analyzer.token.Identifier.class)) {
            if (categorizedTokens.size() > currentPosition + 1 && categorizedTokens.get(currentPosition + 1).getCode().equals("(")) {
                this.type = "routineCall";
                this.routineCall = new RoutineCall(categorizedTokens.get(currentPosition), categorizedTokens);
                this.lastToken = this.routineCall.lastToken;
            } else {
                this.type = "modifiablePrimary";
                this.modifiablePrimary = new ModifiablePrimary(categorizedTokens.get(currentPosition), categorizedTokens, currentPosition);
                this.lastToken = this.modifiablePrimary.lastToken;
            }
        } else
            throw new Exception("Error: illegal declaration in Summand    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                    ", some illegal token");
    }

//    public boolean isSummand() throws Exception {
//        int currentPosition = this.currentToken.getOrderInTokenList(categorizedTokens);
//        if(categorizedTokens.get(currentPosition).getCode().equals("true") ||
//                categorizedTokens.get(currentPosition).getCode().equals("false") ||
//                categorizedTokens.get(currentPosition).getClass().equals(com.example.kek.lexical.analyzer.token.Literal.class)
//        )
//    }
}
