package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.Value;
import com.example.kek.semantic.analyzer.AST.*;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

public class RoutineDeclarationStrategy extends GenerationStrategy {

    @Override
    public void before() throws IOException {
       append("\n");
       var routineContext = (RoutineDeclaration) nodeContext;
       add(String.format(".method public static %s(", routineContext.getIdent().getName()));
       for(VariableDeclaration variableDeclaration : routineContext.getVariablesDeclaration()){
           PrimitiveType primitiveType = (PrimitiveType) variableDeclaration.getType().getType();
           add(mapTOJasminType(primitiveType.getTypePrim()));
       }
       add(")");

       if (routineContext.getType() != null) {
           PrimitiveType primitiveType = (PrimitiveType) routineContext.getType().getType();
           append(mapTOJasminType(primitiveType.getTypePrim()));
       }
       else append("V");

       appendStackAllocation();

        for(VariableDeclaration variableDeclaration : routineContext.getVariablesDeclaration()){

            PrimitiveType primitiveType = (PrimitiveType) variableDeclaration.getType().getType();
            String type = mapTOJasminType(primitiveType.getTypePrim());
            int varNumber = freeVariables.pollFirst();
            if (Objects.equals(type, "D")) freeVariables.pollFirst();
            declareVariable(varNumber, type);
            Value value = new Value(type, varNumber, null, null, null);
            String variableName = variableDeclaration.getIdent().getName();
            variableContext.put(variableName, value);
        }

        BodyStrategy bodyStrategy = new BodyStrategy(((RoutineDeclaration) nodeContext).getBody(), variableContext, freeVariables);

        bodyStrategy.before();
        bodyStrategy.after();
    }

    @Override
    public void after() throws IOException {
        append(".end method");
    }

    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return List.of();
    }
}
