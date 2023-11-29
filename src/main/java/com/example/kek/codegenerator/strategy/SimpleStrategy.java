package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.Value;
import com.example.kek.lexical.analyzer.token.Token;
import com.example.kek.semantic.analyzer.AST.ASTNode;
import com.example.kek.semantic.analyzer.AST.Factor;
import com.example.kek.semantic.analyzer.AST.Simple;

import java.io.IOException;
import java.util.*;

public class SimpleStrategy extends GenerationStrategy {
    public SimpleStrategy(ASTNode nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables) {
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
    }

    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return List.of();
    }

    public Value handleSimple() throws IOException {
        Simple simple = (Simple) nodeContext;
        if (Objects.isNull(simple.getSignOperators()) || simple.getSignOperators().isEmpty()) {
            Factor factor = simple.getFactors().get(0);
            FactorStrategy factorStrategy = new FactorStrategy(factor, variableContext, freeVariables);
            return factorStrategy.handleFactor();
        } else {
            boolean shouldExtend = false;
            List<Value> factors = new ArrayList<>();
            for (Factor factor : simple.getFactors()) {
                FactorStrategy factorStrategy = new FactorStrategy(factor, variableContext, freeVariables);
                Value curFactor = factorStrategy.handleFactor();
                if (Objects.equals(curFactor.getType(), "D")) {
                    shouldExtend = true;
                }
                factors.add(curFactor);
            }

            final boolean shouldExtendFinal = shouldExtend;
            factors = factors.stream()
                    .map(element -> {
                        try {
                            return shouldExtendFinal ? castDouble(element) : castInteger(element);
                        } catch (IOException e) {
                            throw new RuntimeException("Error casting element", e);
                        }
                    })
                    .toList();

            Value newValue;
            int operation_index = 0;
            Integer newVarNumber = freeVariables.pollFirst();
            if (!shouldExtend) {
                newValue = new Value("I", null, null, null, null);
                declareVariable(newVarNumber, "I");
                for (Value intValue : factors) {
                    if (newValue.getVarNumber() == null) {
                        if (intValue.getVarNumber() != null) loadInt(intValue.getVarNumber());
                        else loadLiteral(intValue.getLiteralInt());

                        storeInt(newVarNumber);
                        newValue.setVarNumber(newVarNumber);
                    } else {
                        loadInt(newValue.getVarNumber());

                        if (intValue.getVarNumber() != null) loadInt(intValue.getVarNumber());
                        else loadLiteral(intValue.getLiteralInt());

                        appendTabbed(getSignOperationsInt(((Simple) nodeContext).getSignOperators().get(operation_index)));
                        operation_index++;
                        storeInt(newValue.getVarNumber());
                    }
                }
            }
            else {
                freeVariables.pollFirst();
                newValue = new Value("D", null, null, null, null);
                declareVariable(newVarNumber, "D");
                for (Value doubleValue : factors) {
                    if (newValue.getVarNumber() == null) {
                        if (doubleValue.getVarNumber() != null) loadDouble(doubleValue.getVarNumber());
                        else loadLiteral(doubleValue.getLiteralDouble());

                        storeDouble(newVarNumber);
                        newValue.setVarNumber(newVarNumber);
                    } else {
                        loadDouble(newValue.getVarNumber());

                        if (doubleValue.getVarNumber() != null) loadDouble(doubleValue.getVarNumber());
                        else loadLiteral(doubleValue.getLiteralDouble());

                        appendTabbed(getSignOperationsDouble(((Simple) nodeContext).getSignOperators().get(operation_index)));
                        operation_index++;
                        storeDouble(newValue.getVarNumber());
                    }
                }
            }
            return newValue;
        }

    }

    public String getSignOperationsInt(Token token) throws IOException {
        return switch (token.getCode()){
            case ("+") -> {
                yield "iadd";
            }
            case("-") -> {
                yield "isub";
            }
            default -> throw new IllegalStateException("Unexpected value: " + token.getCode());
        };
    }

    public String getSignOperationsDouble(Token token) throws IOException {
        return switch (token.getCode()){
            case ("+") -> {
                appendTabbed("fadd");
                yield "fadd";
            }
            case("-") -> {
                appendTabbed("fdif");
                yield "fdif";
            }
            default -> throw new IllegalStateException("Unexpected value: " + token.getCode());
        };
    }
}
