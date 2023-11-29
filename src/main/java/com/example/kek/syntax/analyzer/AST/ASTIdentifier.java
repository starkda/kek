package com.example.kek.syntax.analyzer.AST;

import lombok.Getter;

@Getter
public class ASTIdentifier {
    private final String name;
    private final String startingLine;
    public ASTIdentifier(String name, String startingLine){
        this.name = name;
        this.startingLine = startingLine;
    }
}
