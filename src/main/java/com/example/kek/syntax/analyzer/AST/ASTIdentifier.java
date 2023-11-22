package com.example.kek.syntax.analyzer.AST;

public class ASTIdentifier {
    public String getName() {
        return name;
    }

    public String getStartingLine() {
        return startingLine;
    }

    private final String name;
    private final String startingLine;
    public ASTIdentifier(String name, String startingLine){
        this.name = name;
        this.startingLine = startingLine;
    }
}
