package com.example.kek.semantic.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class RoutineCall extends ASTNode {

    private ASTIdentifier ident;
    private List<Expression> functionParams = new ArrayList<>();

    public RoutineCall(ASTIdentifier ident){
        this.ident = ident;
    }
    public RoutineCall(Token currentToken, List<Token> categorizedTokens) throws Exception {
        super(currentToken, categorizedTokens);
        createNode();
    }

    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(this.categorizedTokens);
        if(categorizedTokens.size() <= currentPosition)
            throw new Exception("Error: illegal declaration in RoutineCall, syntax error 'unbounded'");
        if(currentToken.getClass() == com.example.kek.lexical.analyzer.token.Identifier.class){
            this.ident = new ASTIdentifier(currentToken.getCode(), String.valueOf(currentToken.getLine()));
            currentPosition++;
            if(Objects.equals(categorizedTokens.get(currentPosition).getCode(), "(")){
                currentPosition++;
                while(true){
                    if(currentPosition == categorizedTokens.size())
                        throw new Exception("Error: illegal declaration in RoutineCall, but expected RoutineCall declaration");
                    if(categorizedTokens.get(currentPosition).getCode().equals(",")){
                        currentPosition++;
                        continue;
                    }
                    if(Objects.equals(categorizedTokens.get(currentPosition).getCode(), ")")){
                        this.lastToken = categorizedTokens.get(currentPosition);
                        categorizedTokens.get(currentPosition);
                        break;
                    }
                    functionParams.add(new Expression (categorizedTokens.get(currentPosition), categorizedTokens));
                    currentPosition = functionParams.get(functionParams.size() - 1).lastToken.getOrderInTokenList(categorizedTokens) + 1;
                }
            }
            else throw new Exception("Error: illegal declaration in RoutineCall    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                    ", but expected '('");
        }
        else throw new Exception("Error: illegal declaration in RoutineCall    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                ", but expected Identifier");
    }


    public ASTIdentifier getIdent() {
        return ident;
    }

    public List<Expression> getFunctionParams() {
        return functionParams;
    }
}
