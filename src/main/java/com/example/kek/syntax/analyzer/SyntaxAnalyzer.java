package com.example.kek.syntax.analyzer;

import com.example.kek.lexical.analyzer.token.Token;
import com.example.kek.syntax.analyzer.AST.AbstractSyntaxTree;

import java.util.List;

public class SyntaxAnalyzer {
    private AbstractSyntaxTree abstractSyntaxTree;

    public SyntaxAnalyzer(AbstractSyntaxTree abstractSyntaxTree){
        this.abstractSyntaxTree = abstractSyntaxTree;
    }

    public void showAbstractSyntaxTree(){
        System.out.println("some tree will here :) ");
    }

}
