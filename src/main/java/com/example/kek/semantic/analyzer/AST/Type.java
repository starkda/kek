package com.example.kek.semantic.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;
import lombok.Getter;

import java.util.List;
import java.util.Objects;

@Getter
public class Type extends ASTNode {
    public Token getLastToken() {
        return lastToken;
    }

    public int getLine() {
        return line;
    }

    public int getPosition() {
        return position;
    }

    public Type getType() {
        return type;
    }



    protected Type(){}

    public Type(Type type){
        this.type = type;
    }

    protected Token lastToken;

    protected  int line;
    protected int position;
//    protected PrimitiveType primitiveType;
//    protected UserType userType;
//    protected ArrayType arrayType;

    protected Type type;

    /**
     * Есть 4 возможых варианта типа:
     * 1 - KeyWord(real, boolean, integer)
     * 2 - KeyWord(array [ Expration  ]   )
     * 2.1 KeyWord(array  [  ]   при FunctionDeclaration)
     * 3 - Identifier - UserType
     * 4 - KeyWord(record) {VariableDeclaration}
     *  На 17.10.2023 пока 4-ое не делал.
     * @param token
     * @param categorizedTokens
     */
    Type(Token token, List<Token> categorizedTokens) throws Exception {
        super(token, categorizedTokens);
        createNode();
    }

    public void createNode() throws Exception {
        int currentPosition = currentToken.getOrderInTokenList(categorizedTokens);

        if(Objects.equals(currentToken.getCode(), "real") || Objects.equals(currentToken.getCode(), "boolean") || Objects.equals(currentToken.getCode(), "integer")) {
            this.type = new PrimitiveType(currentToken, currentToken.getCode());
            this.lastToken = type.lastToken;
        }
        if(currentToken.getClass().equals(com.example.kek.lexical.analyzer.token.Identifier.class) || Objects.equals(currentToken.getCode(), "record")) {
            this.type = new UserType(currentToken, categorizedTokens);
            this.lastToken = type.lastToken;
        }
        if(Objects.equals(currentToken.getCode(), "array")) {
            this.type = new ArrayType(currentToken, currentToken.getCode(), categorizedTokens);
            this.lastToken = type.lastToken;
        }

    }

}
