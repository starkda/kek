package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class Expression extends ASTNode {

    ArrayList<Token> logicOperators = new ArrayList<>();
    ArrayList<Relation> relations = new ArrayList<>();
    private final List<Token> tokens = new ArrayList<>();
    protected Token lastToken;

    public Expression(Token token, List<Token> categorizedTokens) throws Exception {
        super(token, categorizedTokens);
        createNode();
        //если следующий токен - := => начался уже новый Assignment с новым ModifiablePrimary => это убираем )
        if (this.lastToken.getOrderInTokenList(categorizedTokens) + 1 != categorizedTokens.size() &&
                categorizedTokens.get(this.lastToken.getOrderInTokenList(categorizedTokens) + 1).getCode().equals(":=")) {
            tokens.remove(relations.size() - 1);
            this.lastToken = relations.get(relations.size() - 1).lastToken;
        }
    }

    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(categorizedTokens);
        int controlFlag = -1;
        while (true) {
            if (currentPosition == categorizedTokens.size() ||
                    this.categorizedTokens.get(currentPosition).getCode().equals(":=") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("return") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("var") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("for") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("while") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("if") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("end") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals(")") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("]") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals(",") ||
                    this.categorizedTokens.get(currentPosition).getClass().equals(com.example.kek.lexical.analyzer.token.KeyWord.class) &&
                            !Objects.equals(categorizedTokens.get(currentPosition).getCode(), "true") &&
                            !Objects.equals(categorizedTokens.get(currentPosition).getCode(), "false") ||
                    Objects.equals(categorizedTokens.get(currentPosition).getCode(), "..")) {
                this.lastToken = relations.get(relations.size() - 1).lastToken;
                break;
            }
            if (this.categorizedTokens.get(currentPosition).getCode().equals("and") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("or") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("xor")) {
                if (controlFlag == 1)
                    throw new Exception("Error: illegal declaration in Expression    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                            ", two logic operators without Relation");
                controlFlag = 1;
                logicOperators.add(this.categorizedTokens.get(currentPosition));
                currentPosition++;
            } else {
                if (controlFlag == 0) {
                    this.lastToken = relations.get(relations.size() - 1).lastToken;
                    break;
                }
                controlFlag = 0;
                relations.add(new Relation(this.categorizedTokens.get(currentPosition), categorizedTokens));
                currentPosition = relations.get(relations.size() - 1).lastToken.getOrderInTokenList(categorizedTokens) + 1;
            }
        }
    }
}