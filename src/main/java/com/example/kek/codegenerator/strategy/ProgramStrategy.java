package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.Value;
import com.example.kek.process.Processor;
import com.example.kek.syntax.analyzer.AST.ASTNode;
import com.example.kek.syntax.analyzer.AST.FieldDeclaration;
import com.example.kek.syntax.analyzer.AST.Program;
import com.example.kek.syntax.analyzer.AST.VariableDeclaration;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class ProgramStrategy extends GenerationStrategy {

    @Override
    public void before() throws IOException {
        append(".class public Kek");
        append(".super java/lang/Object");
    }

    @Override
    public void after() throws IOException {
        append("\n");
        append(".method public static main([Ljava/lang/String;)V");
        appendStackAllocation();
        appendTabbed(String.format("invokestatic Kek/%s()V", Processor.entryPoint));
        appendTabbed("return");
        append(".end method");
    }

    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return (((Program) parent).allMain).stream().map(node -> {
            if (node.getClass().equals(VariableDeclaration.class)) {
                try {
                    return new FieldDeclaration(node.getCurrentToken(), node.getCategorizedTokens());
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return node;

        }).toList();
    }
}
