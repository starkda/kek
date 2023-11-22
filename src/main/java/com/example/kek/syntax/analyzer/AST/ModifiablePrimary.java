package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Identifier;
import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;
import java.util.Objects;

public class ModifiablePrimary extends ASTNode {

    private final int firstPosition;
    private ModifiablePrimary modifiablePrimary;

    private Expression expr;

    private Identifier ident;


    public ModifiablePrimary(Token currentToken, List<Token> categorizedTokens, int firstPosition) throws Exception {
        super(currentToken, categorizedTokens);
        this.firstPosition = firstPosition;
        createNode();
    }

    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(this.categorizedTokens);
        while (true) {
            if (
                    currentPosition == categorizedTokens.size() ||
                            !categorizedTokens.get(currentPosition).getClass().equals(com.example.kek.lexical.analyzer.token.Identifier.class) &&
                            !categorizedTokens.get(currentPosition).getCode().equals("[") &&
                            !categorizedTokens.get(currentPosition).getCode().equals(".") &&
                                    !categorizedTokens.get(currentPosition).getCode().equals("size")

            ) {
                if (currentPosition != this.firstPosition) {
                    this.lastToken = categorizedTokens.get(currentPosition - 1);
                    break;
                } else
                    throw new Exception("Error: illegal declaration in  ModifiablePrimary   " +
                            categorizedTokens.get(currentPosition).showCodeLinePosition() + " - empty left part ..... (-_-) ");
            } else if (categorizedTokens.get(currentPosition).getClass().equals(com.example.kek.lexical.analyzer.token.Identifier.class)) {
                this.ident = (Identifier) categorizedTokens.get(currentPosition);
                currentPosition = this.ident.getOrderInTokenList(categorizedTokens) + 1;
                this.modifiablePrimary = new ModifiablePrimary(categorizedTokens.get(currentPosition), this.categorizedTokens, this.firstPosition);
                currentPosition = this.modifiablePrimary.lastToken.getOrderInTokenList(categorizedTokens) + 1;
                this.lastToken = this.modifiablePrimary.lastToken;
                if(this.modifiablePrimary.ident == null)
                    this.modifiablePrimary = null;
            } else if (categorizedTokens.get(currentPosition).getCode().equals("[") && (currentPosition != this.firstPosition)) {
                currentPosition++;
                this.expr = new Expression(categorizedTokens.get(currentPosition), categorizedTokens);
                currentPosition = this.expr.lastToken.getOrderInTokenList(this.categorizedTokens) + 1;
                if (categorizedTokens.get(currentPosition).getCode().equals("]")) {
                    currentPosition++;
                    continue;
                } else
                    throw new Exception("Error: illegal declaration in  ModifiablePrimary   " +
                            categorizedTokens.get(currentPosition).showCodeLinePosition() + " , but expected ']'");
            } else if (categorizedTokens.get(currentPosition).getCode().equals("."))
                currentPosition++;

        }
    }

    public int getFirstPosition() {
        return firstPosition;
    }

    public ModifiablePrimary getModifiablePrimary() {
        return modifiablePrimary;
    }

    public Expression getExpr() {
        return expr;
    }

    public Identifier getIdent() {
        return ident;
    }
}
