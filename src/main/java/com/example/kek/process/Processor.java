package com.example.kek.process;

import com.example.kek.codegenerator.CodeGenerator;
import com.example.kek.lexical.analyzer.LexicalAnalyzer;
import com.example.kek.lexical.analyzer.token.Token;
import com.example.kek.semantic.analyzer.SemanticAnalyzer;
import com.example.kek.semantic.analyzer.AST.AbstractSyntaxTree;
import java.util.List;

public class Processor {

    private final String fileName, entryPoint;
    private final LexicalAnalyzer lexicalAnalyzer = new LexicalAnalyzer();


    public Processor(String fileName, String entryPoint) {
        this.fileName = fileName;
        this.entryPoint = entryPoint;
    }

    public void process() throws Exception {
        List<Token> categorizeTokens = lexicalAnalyzer.genTokensFromLexicalAnalyzer(fileName);
      //  categorizeTokens.forEach(Token::print); // print all tokens after lexical analyze
        AbstractSyntaxTree ast = new AbstractSyntaxTree(categorizeTokens);

        SemanticAnalyzer semanticAnalyzer = new SemanticAnalyzer(ast, entryPoint);
        semanticAnalyzer.makeSemanticAnalysis();

        semanticAnalyzer.showAbstractSyntaxTree();
        CodeGenerator codeGenerator = new CodeGenerator(ast);
        codeGenerator.generateCode();
    }

}
