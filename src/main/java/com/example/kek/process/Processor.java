package com.example.kek.process;

import com.example.kek.lexical.analyzer.LexicalAnalyzer;
import com.example.kek.lexical.analyzer.token.Token;
import com.example.kek.syntax.analyzer.SyntaxAnalyzer;
import java.util.List;

public class Processor {

    private final String fileName;
    private final LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();


    public Processor(String fileName) {
        this.fileName = fileName;
    }

    public void process() {
        List<Token> categorizeTokens = lexicalAnalyzer.getTokensFromLexicalAnalyzer(fileName);
        categorizeTokens.forEach(Token::print); // print all tokens after lexical analyze

        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(categorizeTokens);
        syntaxAnalyzer.showAbstractSyntaxTree();


    }

}
