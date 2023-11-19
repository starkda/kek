package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;

public class AbstractSyntaxTree {
    private Program program;
    private final String entryPoint;
    private final List<Token> categorizedTokens;

    public AbstractSyntaxTree(List<Token> categorizedTokens, String entryPoint) throws Exception {
        this.categorizedTokens = categorizedTokens;
        this.entryPoint = entryPoint;
        generateAbstractSyntaxTree();
    }

    private void generateAbstractSyntaxTree() throws Exception {
        this.program = new Program(categorizedTokens.get(0), categorizedTokens);
    }
}
