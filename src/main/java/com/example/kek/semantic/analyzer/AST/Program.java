package com.example.kek.semantic.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Program extends ASTNode {
    private List<ASTNode> allMain = new ArrayList<>();
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
                this.allMain.add(new VariableDeclaration(this.categorizedTokens.get(currentPosition), categorizedTokens));
                // последний токен -> позиция последнего токена + 1
                currentPosition = this.allMain.get(this.allMain.size() - 1).lastToken.getOrderInTokenList(categorizedTokens) + 1;
                continue;
            }
            if(Objects.equals(this.categorizedTokens.get(currentPosition).getCode(), "type")) {
                this.allMain.add(new TypeDeclaration(this.categorizedTokens.get(currentPosition), categorizedTokens));
                currentPosition = this.allMain.get(this.allMain.size() - 1).lastToken.getOrderInTokenList(categorizedTokens) + 1;
                continue;
            }
            if(Objects.equals(this.categorizedTokens.get(currentPosition).getCode(), "routine")) {
                this.allMain.add(new RoutineDeclaration(this.categorizedTokens.get(currentPosition), categorizedTokens));
                currentPosition = this.allMain.get(this.allMain.size() - 1).lastToken.getOrderInTokenList(categorizedTokens) + 1;
                continue;
            }

            throw new Exception("Error: illegal declaration in program    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
            ", but expected variableDeclarations, typeDeclarations, or routineDeclaration");
        }

    }

    public List<ASTNode> getAllMain() {
        return allMain;
    }
}
