package com.example.kek.semantic.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UserType extends Type {
    private List<VariableDeclaration> variableDeclarations = new ArrayList<>();
    private List<Token> tokens;
    private Token token;
    private ASTIdentifier ident;
    public UserType(Token token, List<Token> categorizedTokens) throws Exception {
        super();
        this.token = token;
        this.tokens = categorizedTokens;
        this.lastToken = token;
        this.line = token.getLine();
        this.position = token.getPosition();
        if(Objects.equals(token.getCode(), "record"))
            createNode();
        else findUserType();

    }

    //(хз когда написал...) мб не нужно и стоит это делать на этапе анализа AST, иначе можно сразу подгружать готовый AST
    // 14.11.2023 использую это
    private void findUserType(){
        this.ident = new ASTIdentifier(token.getCode(), String.valueOf(token.getLine()));
    }

    public void createNode() throws Exception {
        int currentPosition = this.token.getOrderInTokenList(this.tokens);
        while(true){
            if(Objects.equals(tokens.get(currentPosition).getCode(), "record")){
                currentPosition++;
                continue;
            }
            if(Objects.equals(tokens.get(currentPosition).getCode(), "var")){
                variableDeclarations.add(new VariableDeclaration(tokens.get(currentPosition), tokens));
                // lastToken -> позиция этого токена во всех токена + 1
                currentPosition = variableDeclarations.get(variableDeclarations.size() - 1).lastToken.getOrderInTokenList(tokens) + 1;
                continue;
            }
            if(Objects.equals(tokens.get(currentPosition).getCode(), "end")){
                this.lastToken = tokens.get(currentPosition);
                break;
            }
            throw new Exception("Error: not a variableDeclaration  " + tokens.get(currentPosition).showCodeLinePosition() +
                    ", but expected variableDeclaration");
        }
    }

    public List<VariableDeclaration> getVariableDeclarations() {
        return variableDeclarations;
    }

    public List<Token> getTokens() {
        return tokens;
    }

    public Token getToken() {
        return token;
    }

    public ASTIdentifier getIdent() {
        return ident;
    }
}
