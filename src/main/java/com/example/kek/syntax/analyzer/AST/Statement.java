package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;
import java.util.Objects;

public class Statement extends ASTNode{

    private Assignment assignment;
    private RoutineCall routineCall;
    private WhileLoop whileLoop;
    private ForLoop forLoop;
    private IfStatement ifStatement;
    private ReturnCall returnCall;
    public Statement(Token currentToken, List<Token> categorizedTokens) throws Exception {
        super(currentToken, categorizedTokens);
        createNode();
    }

    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(this.categorizedTokens);
        if(Objects.equals(currentToken.getCode(), "if")){
            this.ifStatement = new IfStatement(currentToken, categorizedTokens);
            this.lastToken = ifStatement.lastToken;
        }
        else if(Objects.equals(currentToken.getCode(), "return")){
            this.returnCall = new ReturnCall(currentToken, categorizedTokens);
            this.lastToken = returnCall.lastToken;
        }
        else if(Objects.equals(currentToken.getCode(), "while")){
            this.whileLoop = new WhileLoop(currentToken, categorizedTokens);
            this.lastToken = whileLoop.lastToken;
        }
        else if(Objects.equals(currentToken.getCode(), "for")){
            this.forLoop = new ForLoop(currentToken, categorizedTokens);
            this.lastToken = forLoop.lastToken;
        }
        // RoutineCall
        else if(currentToken.getClass() == com.example.kek.lexical.analyzer.token.KeyWord.class){
            this.routineCall = new RoutineCall(this.currentToken, categorizedTokens);
            this.lastToken = routineCall.lastToken;
        }
        // значит это должен быть Assignment
        else {
            this.assignment = new Assignment(this.currentToken, categorizedTokens);
            this.lastToken = assignment.lastToken;
        }
    }
}
