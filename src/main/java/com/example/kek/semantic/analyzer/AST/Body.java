package com.example.kek.semantic.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Body extends  ASTNode{


    // typeDeclarations + variableDeclarations + statements
    private final List<ASTNode> declarationsAndStatements = new ArrayList<>();
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
                this.declarationsAndStatements.add(new VariableDeclaration(this.categorizedTokens.get(currentPosition), categorizedTokens));
                // последний токен -> позиция последнего токена + 1
                currentPosition = this.declarationsAndStatements.get(this.declarationsAndStatements.size() - 1).lastToken.getOrderInTokenList(categorizedTokens) + 1;
                continue;
            }


            if(Objects.equals(this.categorizedTokens.get(currentPosition).getCode(), "type")) {
                this.declarationsAndStatements.add(new TypeDeclaration(this.categorizedTokens.get(currentPosition), categorizedTokens));
                currentPosition = this.declarationsAndStatements.get(this.declarationsAndStatements.size() - 1).lastToken.getOrderInTokenList(categorizedTokens) + 1;
                continue;
            }
            this.declarationsAndStatements.add(new Statement(this.categorizedTokens.get(currentPosition), categorizedTokens));
            currentPosition = this.declarationsAndStatements.get(this.declarationsAndStatements.size() - 1).lastToken.getOrderInTokenList(categorizedTokens) + 1;
        }
    }

    public List<ASTNode> getDeclarationsAndStatements() {
        return declarationsAndStatements;
    }
}
