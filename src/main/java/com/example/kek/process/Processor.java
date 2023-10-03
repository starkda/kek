package com.example.kek.process;

import com.example.kek.lexical.analyzer.LexicalAnalyzer;
import com.example.kek.lexical.analyzer.token.Token;

import java.util.List;

public class Processor {

    private final String fileName;
    private final LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();

    public Processor(String fileName) {
        this.fileName = fileName;
    }

    public void process() {
        List<Token> tokens = lexicalAnalyzer.getTokensFromLexicalAnalyzer(fileName);
        for (Token token : tokens) {
            token.print();
        }
    }

}
