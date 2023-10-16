package com.example.kek.syntax.analyzer.AST;

import com.example.kek.lexical.analyzer.token.Token;

import java.util.ArrayList;
import java.util.List;

public class ASTNode {

    private String type;
    private int start, end;
    private Token token;

    private ASTNode parent;
    private List<ASTNode> children = new ArrayList<>();


    public ASTNode(Token token) {
        this.token = token;
    }


}
