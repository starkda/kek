package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;

public class AbstractSyntaxTree {
    private ASTNode startedNode, currentNode;
    private List<Token > categorizedTokens;

    public AbstractSyntaxTree(List<Token> tokens){
        categorizedTokens = tokens;
    }

    public void generateAbstractSyntaxTree(){}
}
