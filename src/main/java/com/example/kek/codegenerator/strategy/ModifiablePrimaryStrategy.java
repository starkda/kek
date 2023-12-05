package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.Field;
import com.example.kek.codegenerator.Layerable;
import com.example.kek.codegenerator.Value;
import com.example.kek.semantic.analyzer.AST.ASTNode;
import com.example.kek.semantic.analyzer.AST.ModifiablePrimary;

import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

import static com.example.kek.codegenerator.CodeGenerator.recordFields;

public class ModifiablePrimaryStrategy extends GenerationStrategy implements Layerable {
    public ModifiablePrimaryStrategy(ASTNode nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables) {
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
    }

    Value handleModifiablePrimary() throws IOException {
        ModifiablePrimary curModifiablePrimary = (ModifiablePrimary) nodeContext;
        Value curValue = variableContext.get(curModifiablePrimary.getIdent().getName());
        if (isPrimitive(curValue)) {
            return curValue;
        }

        if (curModifiablePrimary.getModifiablePrimary() == null){
            if (curModifiablePrimary.getExpr() != null) {
                Value ind = (new ExpressionStrategy(curModifiablePrimary.getExpr(), variableContext, freeVariables)).handleExpression();
                loadValue(curValue);
                if (!Objects.equals(ind.getType(), "I")) throw new RuntimeException("array index must be integer");
                loadValue(ind);
                switch (curValue.getType().substring(1)){
                    case ("I"), ("Z") -> appendTabbed("iaload");
                    case ("D") -> appendTabbed("daload");
                    default -> appendTabbed("aaload");
                }
                Value retValue = new Value(null, freeVariables.pollFirst(), null, null, null);
                retValue.setType(curValue.getType().substring(1));
                declareVariable(retValue.getVarNumber(), retValue.getType());
                storeValue(retValue);
                return retValue;
            }
            return curValue;
        }

        Value retValue = new Value(null, freeVariables.pollFirst(), null, null, null);
        Stream<Field> fields = recordFields.get(curValue.getType()
                .substring(1, curValue.getType().length() - 1))
                .stream();


        String baseClass = curValue.getType().substring(1, curValue.getType().length() - 1);
        String baseType = null;
        loadReference(curValue.getVarNumber());
        curModifiablePrimary = curModifiablePrimary.getModifiablePrimary();
        while (curModifiablePrimary != null) {

            ModifiablePrimary finalCurModifiablePrimary = curModifiablePrimary;
            List<Field> tmp = fields.toList();
            fields = tmp.stream();
            Field newField = tmp.stream().filter(candidate -> candidate.getName().equals(finalCurModifiablePrimary
                            .getIdent().getName())).findFirst()
                    .orElseThrow(() -> new RuntimeException("during resolving ModifiablePrimary field is absent"));
            baseType = newField.getType();
            appendTabbed(String.format("getfield %s/%s %s", baseClass, newField.getName(), baseType));

            if (curModifiablePrimary.getExpr() != null){
                Value ind = (new ExpressionStrategy(curModifiablePrimary.getExpr(), variableContext, freeVariables)).handleExpression();
                if (!Objects.equals(ind.getType(), "I")) throw new RuntimeException("array index must be integer");
                loadValue(ind);
                switch (curModifiablePrimary.getIdent().getName().substring(1)){
                    case ("I"), ("Z") -> appendTabbed("iaload");
                    case ("D") -> appendTabbed("daload");
                    default -> appendTabbed("aaload");
                }
            }

            if (curModifiablePrimary.getModifiablePrimary() != null) {
                baseClass = newField.getType()
                        .substring(1, newField.getType().length() - 1);
                fields = recordFields.get(newField.getType()
                                .substring(1, newField.getType().length() - 1))
                        .stream();
            }
            curModifiablePrimary = curModifiablePrimary.getModifiablePrimary();
        }

        retValue.setType(baseType);

        declareVariable(retValue.getVarNumber(), baseType);
        storeValue(retValue);
       /* field = field.substring(0, field.length() - 1);
        appendTabbed(String.format("getfield %s %s", field, retValue.getType()));
        storeValue(retValue); */

        return retValue;
    }

    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return null;
    }
}
