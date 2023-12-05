package com.example.kek.codegenerator;

import com.example.kek.codegenerator.strategy.GenerationStrategy;
import com.example.kek.semantic.analyzer.AST.ASTNode;
import com.example.kek.semantic.analyzer.AST.TypeDeclaration;

import java.util.List;

public class TypeDeclarationStrategy extends GenerationStrategy {
    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return List.of(((TypeDeclaration)parent).getType());
    }
}
