package com.example.kek.codegenerator.strategy;

import com.example.kek.semantic.analyzer.AST.ASTNode;
import com.example.kek.semantic.analyzer.AST.FieldDeclaration;
import com.example.kek.semantic.analyzer.AST.PrimitiveType;

import java.io.IOException;
import java.util.List;

public class FieldDeclarationStrategy extends GenerationStrategy{

    @Override
    public void before() throws IOException {
        append("\n");
        FieldDeclaration fieldDeclaration = (FieldDeclaration) nodeContext;
        PrimitiveType primitiveType = (PrimitiveType) fieldDeclaration.getType().getType();
        append(String.format(".field public %s %s", fieldDeclaration.getIdent().getName(),
                mapTOJasminType(primitiveType.getTypePrim())));

    }

    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return List.of();
    }
}
