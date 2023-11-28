package com.example.kek.semantic.analyzer.AST;

public class ASTIdentifier extends ASTNode{
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

    @Override
    void createNode() throws Exception {

    }
}
