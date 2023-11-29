package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.CodeGenerator;
import com.example.kek.codegenerator.Value;
import com.example.kek.syntax.analyzer.AST.ASTNode;
import com.example.kek.syntax.analyzer.AST.Expression;
import com.example.kek.syntax.analyzer.AST.RoutineCall;

import java.io.IOException;
import java.util.*;

public class RoutineCallStrategy extends GenerationStrategy {

    public RoutineCallStrategy(ASTNode nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables) {
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
    }

    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return List.of();
    }

    public Value handleRoutineCall() throws IOException {
        List<Value> arguments = ((RoutineCall) nodeContext).getFunctionParams().stream().map(expression -> {
            try {
                return (new ExpressionStrategy(expression, variableContext, freeVariables)).handleExpression();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).toList();

        arguments.forEach(value -> {
            try {
                if (value.getVarNumber() != null) {
                    if (Objects.equals(value.getType(), "I")) {
                        loadInt(value.getVarNumber());
                    } else if (Objects.equals(value.getType(), "Z")) {
                        loadBool(value.getVarNumber());
                    } else {
                        loadDouble(value.getVarNumber());
                    }

                } else {
                    if (value.getLiteralBoolean() != null) loadLiteral(value.getLiteralBoolean());
                    else if (value.getLiteralInt() != null) loadLiteral(value.getLiteralInt());
                    else loadLiteral(value.getLiteralDouble());
                }
            } catch (IOException e) {
                throw new RuntimeException("undefined Type");
            }
        });

        add(String.format("\tinvokestatic Kek/%s(", ((RoutineCall) nodeContext).getIdent().getName()));
        arguments.forEach(value -> {
            try {
                add(value.getType());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
        String returnType = CodeGenerator.returnTypes.get(((RoutineCall) nodeContext).getIdent().getName());
        append(String.format(")%s", returnType));

        int newVarNumber = freeVariables.pollFirst();
        Value resultingValue = new Value(returnType, null, null, null, null);
        resultingValue.setVarNumber(newVarNumber);

        if (Objects.equals(returnType, "I")) {
            declareVariable(newVarNumber, "I");
            storeInt(resultingValue.getVarNumber());
        } else if (Objects.equals(returnType, "Z")) {
            declareVariable(newVarNumber, "Z");
            storeBool(resultingValue.getVarNumber());
        } else if (Objects.equals(returnType, "D")) {
            freeVariables.pollFirst();
            declareVariable(newVarNumber, "D");
            storeDouble(resultingValue.getVarNumber());
        }

        return resultingValue;
    }


}
