package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.CodeGenerator;
import com.example.kek.codegenerator.Field;
import com.example.kek.semantic.analyzer.AST.ASTNode;
import com.example.kek.semantic.analyzer.AST.FieldDeclaration;
import com.example.kek.semantic.analyzer.AST.PrimitiveType;
import com.example.kek.semantic.analyzer.AST.UserType;

import java.io.IOException;
import java.util.List;

import static com.example.kek.codegenerator.CodeGenerator.recordFields;

public class FieldDeclarationStrategy extends GenerationStrategy{
    @Override
    public void before() throws IOException {

        String recordName = ((FieldDeclaration) nodeContext).getRecordName();
        append("\n");
        FieldDeclaration fieldDeclaration = (FieldDeclaration) nodeContext;
        if (fieldDeclaration.getType().getType().getClass().equals(PrimitiveType.class)) {
            PrimitiveType primitiveType = (PrimitiveType) fieldDeclaration.getType().getType();
            append(String.format(".field public %s %s", fieldDeclaration.getIdent().getName(),
                    mapTOJasminType(primitiveType.getTypePrim())));

            List<Field> curLst = recordFields.get(recordName);
            curLst.add(new Field(fieldDeclaration.getIdent().getName(), mapTOJasminType(primitiveType.getTypePrim())));
        }
        else if (fieldDeclaration.getType().getType().getClass().equals(UserType.class)) {
            UserType userType = (UserType) fieldDeclaration.getType().getType();
            append(String.format(".field public %s L%s;", fieldDeclaration.getIdent().getName(),
                   userType.getIdent().getName()));

            List<Field> curLst = recordFields.get(recordName);
            curLst.add(new Field(fieldDeclaration.getIdent().getName(), mapTOReferenceJasminType(userType.getIdent().getName())));
        }
    }

    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return List.of();
    }
}
