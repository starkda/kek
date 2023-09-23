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
        List<Token> tokens = getTokensFromLexicalAnalyzer();
        for (Token token : tokens) {
            token.print();
        }
    }

    private List<Token> getTokensFromLexicalAnalyzer() {
        List<String> tokens = lexicalAnalyzer.generateTokens(fileName);
        return lexicalAnalyzer.categorizeTokens(tokens);
    }
}
