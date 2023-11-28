package com.example.kek.semantic.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Simple extends ASTNode {

    ArrayList<Token> signOperators = new ArrayList<>();
    ArrayList<Factor> factors = new ArrayList<>();

    public Simple(Token currentToken, List<Token> categorizedTokens) throws Exception {
        super(currentToken, categorizedTokens);
        createNode();
    }

    public Simple(Factor factor) {
        factors.add(factor);
    }

    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(categorizedTokens);
        int controlFlag = -1;
        while (true) {
            if (currentPosition == categorizedTokens.size() ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("<") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("<=") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals(">") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals(">=") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("=") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("/=") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("and") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("or") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("xor")) {
                this.lastToken = factors.get(factors.size() - 1).lastToken;
                break;
            }
            if (
                    this.categorizedTokens.get(currentPosition).getCode().equals(":=") ||
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
                this.lastToken = factors.get(factors.size() - 1).lastToken;
                break;
            }
            if (this.categorizedTokens.get(currentPosition).getCode().equals("-") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("+")) {
                if (controlFlag == 1)
                    throw new Exception("Error: illegal declaration in Simple    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                            ", two sign without Factor");
                controlFlag = 1;
                signOperators.add(this.categorizedTokens.get(currentPosition));
                currentPosition++;
            } else {
                if (controlFlag == 0) {
                    this.lastToken = factors.get(factors.size() - 1).lastToken;
                    break;
                }
                controlFlag = 0;
                factors.add(new Factor(this.categorizedTokens.get(currentPosition), categorizedTokens));
                currentPosition = factors.get(factors.size() - 1).lastToken.getOrderInTokenList(categorizedTokens) + 1;
            }
        }
    }

    public ArrayList<Token> getSignOperators() {
        return signOperators;
    }

    public ArrayList<Factor> getFactors() {
        return factors;
    }

    public boolean isSimple() throws Exception {
        return factors.size() == 1 && signOperators.size() == 0 && factors.get(0).isSimple();
    }

    public Summand getSimple() throws Exception {
        if(this.isSimple())
            return factors.get(0).getSimple();
        else
            throw new Exception("Error: illegal declaration in Simple.getSimple(), it's not simple");
    }
}
