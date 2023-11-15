package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;
import java.util.Objects;

public class Type {

    protected Type(){}

    protected Token lastToken;

    protected  int line;
    protected int position;
    protected PrimitiveType primitiveType;
    protected UserType userType;
    protected ArrayType arrayType;

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
    public Type(Token token, List<Token> categorizedTokens) throws Exception {
        int currentPosition = token.getOrderInTokenList(categorizedTokens);

        if(Objects.equals(token.getCode(), "real") || Objects.equals(token.getCode(), "boolean") || Objects.equals(token.getCode(), "integer")) {
            this.primitiveType = new PrimitiveType(token, token.getCode());
            this.lastToken = primitiveType.lastToken;
        }
        if(token.getClass().equals(com.example.kek.lexical.analyzer.token.Identifier.class) || Objects.equals(token.getCode(), "record")) {
            this.userType = new UserType(token, categorizedTokens);
            this.lastToken = userType.lastToken;
        }
        if(Objects.equals(token.getCode(), "array")) {
            this.arrayType = new ArrayType(token, token.getCode(), categorizedTokens);
            this.lastToken = arrayType.lastToken;
        }

    }

}
