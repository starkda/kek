package com.example.kek.process;

import com.example.kek.lexical.analyzer.LexicalAnalyzer;
import com.example.kek.lexical.analyzer.token.Token;
import com.example.kek.syntax.analyzer.SyntaxAnalyzer;
import com.example.kek.syntax.analyzer.AST.AbstractSyntaxTree;
import java.util.List;

public class Processor {

    private final String fileName, entryPoint;
    private final LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();


    public Processor(String fileName, String entryPoint) {
        this.fileName = fileName;
        this.entryPoint = entryPoint;
    }

    public void process() throws Exception {
        List<Token> categorizeTokens = lexicalAnalyzer.getTokensFromLexicalAnalyzer(fileName);
      //  categorizeTokens.forEach(Token::print); // print all tokens after lexical analyze

        SyntaxAnalyzer syntaxAnalyzer = new SyntaxAnalyzer(new AbstractSyntaxTree(categorizeTokens, entryPoint));
        syntaxAnalyzer.showAbstractSyntaxTree();
    }

}
