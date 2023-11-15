package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Program extends ASTNode {
    private List<TypeDeclaration> typeDeclarations = new ArrayList<>();
    private List<VariableDeclaration> variableDeclarations = new ArrayList<>();
    private List<RoutineDeclaration> routineDeclaration = new ArrayList<>();
    public Program(Token token, List<Token> categorizedTokens) throws Exception {
        super(token, categorizedTokens);
        createNode();
    }


    @Override
    void createNode() throws Exception {
        this.code = categorizedTokens;
        int currentPosition = this.currentToken.getOrderInTokenList(this.categorizedTokens);
        while (true){
            if(currentPosition == this.categorizedTokens.size()) {
                System.out.println("AST was created");
                return;
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
            if(Objects.equals(this.categorizedTokens.get(currentPosition).getCode(), "routine")) {
                this.routineDeclaration.add(new RoutineDeclaration(this.categorizedTokens.get(currentPosition), categorizedTokens));
                currentPosition = this.routineDeclaration.get(this.routineDeclaration.size() - 1).lastToken.getOrderInTokenList(categorizedTokens) + 1;
                continue;
            }

            throw new Exception("Error: illegal declaration in program    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
            ", but expected variableDeclarations, typeDeclarations, or routineDeclaration");
        }

    }

}
