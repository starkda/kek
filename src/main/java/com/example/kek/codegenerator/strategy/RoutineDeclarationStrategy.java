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
           if (variableDeclaration.getType().getType().getClass().equals(PrimitiveType.class)) {
               PrimitiveType primitiveType = (PrimitiveType) variableDeclaration.getType().getType();
               add(mapTOJasminType(primitiveType.getTypePrim()));
           }
           if (variableDeclaration.getType().getType().getClass().equals(ArrayType.class)) {
               ArrayType arrayType = (ArrayType) variableDeclaration.getType().getType();
               String type = mapTOJasminType(((PrimitiveType) arrayType.getType().getType()).getTypePrim());
               add(mapToArrayRefJasminType(type));
           }
           if (variableDeclaration.getType().getType().getClass().equals(UserType.class)) {
               UserType userType = (UserType) variableDeclaration.getType().getType();
               String type = mapTOReferenceJasminType(userType.getIdent().getName());
               add((type));
           }
       }
       add(")");

       if (routineContext.getType() != null) {
           if (routineContext.getType().getType().getClass().equals(PrimitiveType.class)) {
               PrimitiveType primitiveType = (PrimitiveType) routineContext.getType().getType();
               append(mapTOJasminType(primitiveType.getTypePrim()));
           }
           if (routineContext.getType().getType().getClass().equals(ArrayType.class)) {
               ArrayType arrayType = (ArrayType) routineContext.getType().getType();
               String type = mapTOJasminType(((PrimitiveType) arrayType.getType().getType()).getTypePrim());
               add(mapToArrayRefJasminType(type));
           }
           if (routineContext.getType().getType().getClass().equals(UserType.class)) {
               UserType userType = (UserType) routineContext.getType().getType();
               String type = mapTOReferenceJasminType(userType.getIdent().getName());
               add(type);
           }
       }
       else append("V");

       appendStackAllocation();

        for(VariableDeclaration variableDeclaration : routineContext.getVariablesDeclaration()){
            if (variableDeclaration.getType().getType().getClass().equals(PrimitiveType.class)) {
                PrimitiveType primitiveType = (PrimitiveType) variableDeclaration.getType().getType();
                String type = mapTOJasminType(primitiveType.getTypePrim());
                int varNumber = freeVariables.pollFirst();
                if (Objects.equals(type, "D")) freeVariables.pollFirst();
                declareVariable(varNumber, type);
                Value value = new Value(type, varNumber, null, null, null);
                String variableName = variableDeclaration.getIdent().getName();
                variableContext.put(variableName, value);
            }
            else if (variableDeclaration.getType().getType().getClass().equals(ArrayType.class)) {
                ArrayType arrayType = (ArrayType) variableDeclaration.getType().getType();
                String type = mapToArrayRefJasminType(mapTOJasminType(((PrimitiveType) arrayType.getType().getType()).getTypePrim()));
                int varNumber = freeVariables.pollFirst();
                declareVariable(varNumber, type);
                String variableName = variableDeclaration.getIdent().getName();
                Value value = new Value(type, varNumber, null, null, null);
                variableContext.put(variableName, value);
            }
            else if (variableDeclaration.getType().getType().getClass().equals(UserType.class)) {
                UserType userType = (UserType) variableDeclaration.getType().getType();
                String type = mapTOReferenceJasminType(userType.getIdent().getName());
                int varNumber = freeVariables.pollFirst();
                declareVariable(varNumber, type);
                String variableName = variableDeclaration.getIdent().getName();
                Value value = new Value(type, varNumber, null, null, null);
                variableContext.put(variableName, value);

            }
        }

        BodyStrategy bodyStrategy = new BodyStrategy(((RoutineDeclaration) nodeContext).getBody(), variableContext, freeVariables, ((RoutineDeclaration) nodeContext).getType(), true);

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
