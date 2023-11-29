package com.example.kek.semantic.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class VariableDeclaration extends ASTNode {
    public ASTIdentifier getIdent() {
        return ident;
    }

    public Expression getInitExp() {
        return initExp;
    }

    public Type getType() {
        return type;
    }

    public void setIdent(ASTIdentifier ident) {
        this.ident = ident;
    }

    public void setInitExp(Expression initExp) {
        this.initExp = initExp;
    }

    public void setType(Type type) {
        this.type = type;
    }

    private ASTIdentifier ident;
    private Expression initExp;
    private Type type;

    public VariableDeclaration(Token token, List<Token> categorizedTokens) throws Exception {
        super(token, categorizedTokens);
        createNode();
    }


    @Override
    void createNode() throws Exception {
        int currentPosition = this.currentToken.getOrderInTokenList(this.categorizedTokens);

        int type = 0;
        /* type 1: var Identifier : Type is Expr
         *  type 2: var Identifier : Type
         *  type 3: var Identifier is Expr
         *  type ~2.1(when function in function declaration array): var Identifier : array[] */

        if (!Objects.equals(categorizedTokens.get(currentPosition).getCode(), "var") || currentPosition + 1 == categorizedTokens.size())
            throw new Exception("Error variable declaration    " + categorizedTokens.get(currentPosition).showCodeLinePosition());
        currentPosition++;
        if (!categorizedTokens.get(currentPosition).getClass().equals(com.example.kek.lexical.analyzer.token.Identifier.class) || currentPosition + 2 >= categorizedTokens.size())
            throw new Exception("Error variable declaration    " + categorizedTokens.get(currentPosition).showCodeLinePosition());

        this.ident = new ASTIdentifier(categorizedTokens.get(currentPosition).getCode(), String.valueOf(categorizedTokens.get(currentPosition).getLine()));
        currentPosition++;


        if (Objects.equals(categorizedTokens.get(currentPosition).getCode(), ":")) {
            currentPosition++;
            this.type = new Type(categorizedTokens.get(currentPosition), categorizedTokens);
            currentPosition = this.type.lastToken.getOrderInTokenList(categorizedTokens) + 1;
            if (currentPosition < categorizedTokens.size() && Objects.equals(categorizedTokens.get(currentPosition).getCode(), "is")) {
                currentPosition++;
                this.initExp = new Expression(categorizedTokens.get(currentPosition), categorizedTokens);
                this.lastToken = this.initExp.lastToken;
            }
            else this.lastToken = this.type.lastToken;
        } else if (Objects.equals(categorizedTokens.get(currentPosition).getCode(), "is")) {
            this.initExp = new Expression(categorizedTokens.get(currentPosition + 1), categorizedTokens);
            this.lastToken = this.initExp.lastToken;
        } else
            throw new Exception("Error variable declaration    " + categorizedTokens.get(currentPosition).showCodeLinePosition());

    }
}
