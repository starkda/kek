package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class RoutineDeclaration extends ASTNode {
    private ASTIdentifier ident;
    private List<VariableDeclaration> variablesDeclaration = new ArrayList<>();
    private Type type;
    private Body body;
    public RoutineDeclaration(Token currentToken, List<Token> categorizedTokens) throws Exception {
        super(currentToken, categorizedTokens);
        createNode();
    }

    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(this.categorizedTokens);
        currentPosition++;
        this.ident = new ASTIdentifier(categorizedTokens.get(currentPosition).getCode(), String.valueOf(categorizedTokens.get(currentPosition).getLine()));
        currentPosition++;
        if(Objects.equals(this.categorizedTokens.get(currentPosition).getCode(), "(")) {
            while (true) {
                currentPosition++;
                if(this.categorizedTokens.size() == currentPosition)
                    throw new Exception("Error: end of RoutineDeclaration :    , but expected '(' ");

                if(Objects.equals(this.categorizedTokens.get(currentPosition).getCode(), ")"))
                    break;
                this.variablesDeclaration.add(new VariableDeclaration(categorizedTokens.get(currentPosition), categorizedTokens));
                // id последнего токена
                currentPosition = this.variablesDeclaration.get(this.variablesDeclaration.size() - 1).lastToken.getOrderInTokenList(categorizedTokens);
            }
            currentPosition++;
            if(Objects.equals(this.categorizedTokens.get(currentPosition).getCode(), ":")){
                currentPosition++;
                this.type = new Type(categorizedTokens.get(currentPosition), categorizedTokens);
                currentPosition = this.type.lastToken.getOrderInTokenList(categorizedTokens) + 1;
            }
            if(Objects.equals(this.categorizedTokens.get(currentPosition).getCode(), "is")){
                currentPosition++;
                this.body = new Body(categorizedTokens.get(currentPosition), categorizedTokens);
                currentPosition = this.body.lastToken.getOrderInTokenList(categorizedTokens) + 1;
                if(!Objects.equals(this.categorizedTokens.get(currentPosition).getCode(), "end"))
                    throw new Exception("Error: illegal token in RoutineDeclaration :    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                            ", but expected 'is' ");
                else lastToken = this.categorizedTokens.get(currentPosition);
            }
            else throw new Exception("Error: illegal token in RoutineDeclaration :    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                    ", but expected 'is' ");
        }
        else throw new Exception("Error: illegal token in RoutineDeclaration :    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                ", but expected '(' ");
    }
}
