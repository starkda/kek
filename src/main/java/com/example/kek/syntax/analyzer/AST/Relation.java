package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Relation extends ASTNode {

    ArrayList<Token> signOperators = new ArrayList<>();
    ArrayList<Simple> simples = new ArrayList<>();


    public Relation(Token currentToken, List<Token> categorizedTokens) throws Exception {
        super(currentToken, categorizedTokens);
        createNode();
    }

    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(categorizedTokens);
        int controlFlag = -1;
        while (true) {
            if (currentPosition == categorizedTokens.size() ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("and") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("or") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("xor")) {
                this.lastToken = simples.get(simples.size() - 1).lastToken;
                break;
            }
            if (this.categorizedTokens.get(currentPosition).getCode().equals(":=") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals(")") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("]") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals(",") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("return") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("var") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("for") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("while") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("if") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("end") ||
                    this.categorizedTokens.get(currentPosition).getClass().equals(com.example.kek.lexical.analyzer.token.KeyWord.class) &&
                            !Objects.equals(categorizedTokens.get(currentPosition).getCode(), "true") &&
                            !Objects.equals(categorizedTokens.get(currentPosition).getCode(), "false") ||
                    Objects.equals(categorizedTokens.get(currentPosition).getCode(), "..")) {
                this.lastToken = simples.get(simples.size() - 1).lastToken;
                break;
            }
            if (this.categorizedTokens.get(currentPosition).getCode().equals("<") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("<=") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals(">") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals(">=") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("=") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("/=")){
                if(controlFlag == 1)
                    throw new Exception("Error: illegal declaration in Relation    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                            ", two sign without Simple");
                controlFlag = 1;
                signOperators.add(this.categorizedTokens.get(currentPosition));
                currentPosition++;
            }
            else{
                if(controlFlag == 0) {
                    this.lastToken = simples.get(simples.size() - 1).lastToken;
                    break;
                }
                controlFlag = 0;
                simples.add(new Simple(this.categorizedTokens.get(currentPosition), categorizedTokens));
                currentPosition = simples.get(simples.size() - 1).lastToken.getOrderInTokenList(categorizedTokens) + 1;
            }
        }
    }
}
