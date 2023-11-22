package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;

public class AbstractSyntaxTree {
    public Program getProgram() {
        return program;
    }

    private Program program;


    private final List<Token> categorizedTokens;


    public AbstractSyntaxTree(List<Token> categorizedTokens) throws Exception {
        this.categorizedTokens = categorizedTokens;
        generateAbstractSyntaxTree();
    }

    private void generateAbstractSyntaxTree() throws Exception {
        this.program = new Program(categorizedTokens.get(0), categorizedTokens);
    }
}
