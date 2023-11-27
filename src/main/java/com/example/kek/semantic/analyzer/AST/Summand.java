package com.example.kek.semantic.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;

public class Summand extends ASTNode {

    private String type = "";
    private int intLiteral = -1000000;
    private double realLiteral = -1000000.0;
    private boolean boolLiteral = true;
    private ModifiablePrimary modifiablePrimary;
    private RoutineCall routineCall;

    public Summand(Token currentToken, List<Token> categorizedTokens) throws Exception {
        super(currentToken, categorizedTokens);
        createNode();
    }

    public Summand(double realLiteral) throws Exception{
        this.realLiteral = realLiteral;
        this.type = "real";
    }

    public Summand(int intLiteral) throws Exception{
        this.intLiteral = intLiteral;
        this.type = "integer";
    }

    public Summand(boolean boolLiteral) throws Exception{
        this.boolLiteral = boolLiteral;
        this.type = "boolean";
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


    public String getType() {
        return type;
    }

    public int getIntLiteral() {
        return intLiteral;
    }

    public double getRealLiteral() {
        return realLiteral;
    }

    public boolean getBoolLiteral() {
        return boolLiteral;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setIntLiteral(int intLiteral) {
        this.intLiteral = intLiteral;
    }

    public void setRealLiteral(double realLiteral) {
        this.realLiteral = realLiteral;
    }

    public void setBoolLiteral(boolean boolLiteral) {
        this.boolLiteral = boolLiteral;
    }

    public void setModifiablePrimary(ModifiablePrimary modifiablePrimary) {
        this.modifiablePrimary = modifiablePrimary;
    }

    public void setRoutineCall(RoutineCall routineCall) {
        this.routineCall = routineCall;
    }

    public ModifiablePrimary getModifiablePrimary() {
        return modifiablePrimary;
    }

    public RoutineCall getRoutineCall() {
        return routineCall;
    }

    public boolean isSimple() throws Exception {
        return switch (this.type) {
            case "modifiablePrimary" -> false;
            case "routineCall" -> false;
            case "integer" -> true;
            case "real" -> true;
            case "boolean" -> true;
            default -> throw new Exception("Error: error in semantic of program (Summand.isSimple()) \n" +
                    ", expected type one of summand's types, but receive other");
        };
    }

    public String getValue() throws Exception {
        switch (type) {
            case "integer":
                return String.valueOf(intLiteral);
            case "real":
                return String.valueOf(realLiteral);
            case "boolean":
                return String.valueOf(boolLiteral);
            default:
                throw new Exception("Error: error in semantic of program (Summand.getValue) \n" +
                        ", expected type one of Primitive types, but receive other");
        }
    }

    public Summand getSimple() {
        return this;
    }
}
