package com.example.kek.semantic.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Factor  extends ASTNode{

    ArrayList<Token> signOperators = new ArrayList<>();
    ArrayList<Summand> summands = new ArrayList<>();
    public Factor(Token currentToken, List<Token> categorizedTokens) throws Exception {
        super(currentToken, categorizedTokens);
        createNode();
    }

    public Factor(Summand summand) {
        summands.add(summand);
    }

    @Override
    void createNode()throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(categorizedTokens);
        int controlFlag = -1;
        while (true) {
            if (currentPosition == categorizedTokens.size() ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("+") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("-") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("<") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("<=") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals(">") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals(">=") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("=") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("/=") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("and") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("or") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("xor")) {
                this.lastToken = summands.get(summands.size() - 1).lastToken;
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
                this.lastToken = summands.get(summands.size() - 1).lastToken;
                break;
            }
            if (this.categorizedTokens.get(currentPosition).getCode().equals("*") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("/") ||
                    this.categorizedTokens.get(currentPosition).getCode().equals("%")){
                if(controlFlag == 1)
                    throw new Exception("Error: illegal declaration in Factor    " + categorizedTokens.get(currentPosition).showCodeLinePosition() +
                            ", two sign without Summand");
                controlFlag = 1;
                signOperators.add(this.categorizedTokens.get(currentPosition));
                currentPosition++;
            }
            else{
                if(controlFlag == 0) {
                    this.lastToken = summands.get(summands.size() - 1).lastToken;
                    break;
                }
                controlFlag = 0;
//                if(new Summand(this.categorizedTokens.get(currentPosition), categorizedTokens).isSummand)
                summands.add(new Summand(this.categorizedTokens.get(currentPosition), categorizedTokens));
                currentPosition = summands.get(summands.size() - 1).lastToken.getOrderInTokenList(categorizedTokens) + 1;
            }
        }
    }

    public ArrayList<Token> getSignOperators() {
        return signOperators;
    }

    public ArrayList<Summand> getSummands() {
        return summands;
    }

    public boolean isSimple() throws Exception {
        return summands.size() == 1 && signOperators.size() == 0 && summands.get(0).isSimple();
    }

    public Summand getSimple() throws Exception {
        if(this.isSimple())
            return summands.get(0).getSimple();
        else
            throw new Exception("Error: illegal declaration in Factor.getSimple(), it's not simple");
    }
}
