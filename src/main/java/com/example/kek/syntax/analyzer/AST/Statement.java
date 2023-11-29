package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class Statement extends ASTNode{

//    private Assignment assignment;
//    private RoutineCall routineCall;
//    private WhileLoop whileLoop;
//    private ForLoop forLoop;
//    private IfStatement ifStatement;
//    private ReturnCall returnCall;

    private ASTNode statement;
    public Statement(Token currentToken, List<Token> categorizedTokens) throws Exception {
        super(currentToken, categorizedTokens);
        createNode();
    }

    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(this.categorizedTokens);
        if(Objects.equals(currentToken.getCode(), "if")){
            this.statement = new IfStatement(currentToken, categorizedTokens);
            this.lastToken = statement.lastToken;
        }
        else if(Objects.equals(currentToken.getCode(), "return")){
            this.statement = new ReturnCall(currentToken, categorizedTokens);
            this.lastToken = statement.lastToken;
        }
        else if(Objects.equals(currentToken.getCode(), "while")){
            this.statement = new WhileLoop(currentToken, categorizedTokens);
            this.lastToken = statement.lastToken;
        }
        else if(Objects.equals(currentToken.getCode(), "for")){
            this.statement = new ForLoop(currentToken, categorizedTokens);
            this.lastToken = statement.lastToken;
        }
        // RoutineCall
        else if(currentPosition + 1 < categorizedTokens.size() && categorizedTokens.get(currentPosition + 1).getCode().equals("(")){
            this.statement = new RoutineCall(this.currentToken, categorizedTokens);
            this.lastToken = statement.lastToken;
        }
        // значит это должен быть Assignment
        else {
            this.statement = new Assignment(this.currentToken, categorizedTokens);
            this.lastToken = statement.lastToken;
        }
    }
}
