package com.example.kek.semantic.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;
import lombok.Getter;

import java.util.List;

@Getter
public class FieldDeclaration extends VariableDeclaration{
    String recordName;
    public FieldDeclaration(Token token, List<Token> categorizedTokens, String recordName) throws Exception {
        super(token, categorizedTokens);
        this.recordName = recordName;
    }
    public FieldDeclaration(Token token, List<Token> categorizedTokens) throws Exception {
        super(token, categorizedTokens);
    }
}
