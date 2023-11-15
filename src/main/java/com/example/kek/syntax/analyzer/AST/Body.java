package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Body extends  ASTNode{


    private List<TypeDeclaration> typeDeclarations = new ArrayList<>();
    private List<VariableDeclaration> variableDeclarations = new ArrayList<>();
    private List<Statement> statements = new ArrayList<>();
    public Body(Token currentToken, List<Token> categorizedTokens) throws Exception {
        super(currentToken, categorizedTokens);
        createNode();

    }

    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(this.categorizedTokens);
        while(true){
            if(Objects.equals(categorizedTokens.get(currentPosition).getCode(), "end") || Objects.equals(categorizedTokens.get(currentPosition).getCode(), "else")){
                this.lastToken = categorizedTokens.get(currentPosition - 1);
                break;
            }
            if(Objects.equals(this.categorizedTokens.get(currentPosition).getCode(), "var")) {
                this.variableDeclarations.add(new VariableDeclaration(this.categorizedTokens.get(currentPosition), categorizedTokens));
                // последний токен -> позиция последнего токена + 1
                currentPosition = this.variableDeclarations.get(this.variableDeclarations.size() - 1).lastToken.getOrderInTokenList(categorizedTokens) + 1;
                continue;
            }


            if(Objects.equals(this.categorizedTokens.get(currentPosition).getCode(), "type")) {
                this.typeDeclarations.add(new TypeDeclaration(this.categorizedTokens.get(currentPosition), categorizedTokens));
                currentPosition = this.typeDeclarations.get(this.typeDeclarations.size() - 1).lastToken.getOrderInTokenList(categorizedTokens) + 1;
                continue;
            }
            this.statements.add(new Statement(this.categorizedTokens.get(currentPosition), categorizedTokens));
            currentPosition = this.statements.get(this.statements.size() - 1).lastToken.getOrderInTokenList(categorizedTokens) + 1;
        }
    }
}
