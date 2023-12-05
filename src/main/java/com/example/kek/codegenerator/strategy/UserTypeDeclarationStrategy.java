package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.CodeGenerator;
import com.example.kek.codegenerator.FileName;
import com.example.kek.semantic.analyzer.AST.ASTNode;
import com.example.kek.semantic.analyzer.AST.FieldDeclaration;
import com.example.kek.semantic.analyzer.AST.Type;
import com.example.kek.semantic.analyzer.AST.UserType;
import lombok.Getter;
import lombok.Setter;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static com.example.kek.codegenerator.CodeGenerator.recordFields;

@Setter
@Getter
public class UserTypeDeclarationStrategy extends GenerationStrategy {
    BufferedWriter persistentFile;
    String typeName;

    @Override
    public void before() throws IOException {
        recordFields.put(((UserType) nodeContext).getIdent().getName(), new ArrayList<>());

        typeName = FileName.directory + "/" + ((UserType) nodeContext).getIdent().getName();
        persistentFile = CodeGenerator.file;
        CodeGenerator.file = new BufferedWriter(new FileWriter(typeName));
        append(String.format(".class public %s", typeName));
        append(".super java/lang/Object");
    }

    @Override
    public void after() throws IOException {
        CodeGenerator.file.close();
        CodeGenerator.file = persistentFile;
    }

    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return ((UserType) ((Type)parent).getType()).getVariableDeclarations().stream().map(node -> {
            try {
                return (ASTNode) new FieldDeclaration(node.getCurrentToken(), node.getCategorizedTokens(),
                        ((UserType) nodeContext).getIdent().getName());
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).toList();
    }
}
