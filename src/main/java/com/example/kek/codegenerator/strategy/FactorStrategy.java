package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.Value;
import com.example.kek.lexical.analyzer.token.Token;
import com.example.kek.syntax.analyzer.AST.ASTNode;
import com.example.kek.syntax.analyzer.AST.Factor;
import com.example.kek.syntax.analyzer.AST.Simple;
import com.example.kek.syntax.analyzer.AST.Summand;

import java.io.IOException;
import java.util.*;

public class FactorStrategy extends GenerationStrategy {

    public FactorStrategy(ASTNode nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables) {
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
    }

    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return List.of();
    }

    public Value handleFactor() throws IOException {
        Factor factor = (Factor) nodeContext;
        if (Objects.isNull(factor.getSignOperators()) || factor.getSignOperators().isEmpty()) {
            Summand summand = factor.getSummands().get(0);
            SummandStrategy summandStrategy = new SummandStrategy(summand, variableContext, freeVariables);
            return summandStrategy.handleSummand();
        } else {
            boolean shouldExtend = false;
            List<Value> summands = new ArrayList<>();
            for (Summand summand : factor.getSummands()) {
                SummandStrategy summandStrategy = new SummandStrategy(summand, variableContext, freeVariables);
                Value curSummand = summandStrategy.handleSummand();
                if (Objects.equals(curSummand.getType(), "D")) {
                    shouldExtend = true;
                }
                summands.add(curSummand);
            }

            final boolean shouldExtendFinal = shouldExtend;
            summands = summands.stream()
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
                for (Value intValue : summands) {
                    if (newValue.getVarNumber() == null) {
                        if (intValue.getVarNumber() != null) loadInt(intValue.getVarNumber());
                        else loadLiteral(intValue.getLiteralInt());

                        storeInt(newVarNumber);
                        newValue.setVarNumber(newVarNumber);
                    } else {
                        loadInt(newValue.getVarNumber());

                        if (intValue.getVarNumber() != null) loadInt(intValue.getVarNumber());
                        else loadLiteral(intValue.getLiteralInt());

                        appendTabbed(getSignOperationsInt(((Factor) nodeContext).getSignOperators().get(operation_index)));
                        operation_index++;
                        storeInt(newValue.getVarNumber());
                    }
                }
            }
            else {
                freeVariables.pollFirst();
                newValue = new Value("D", null, null, null, null);
                declareVariable(newVarNumber, "D");
                for (Value doubleValue : summands) {
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
            case ("*") -> {
                yield "imul";
            }
            case("/") -> {
                yield "idiv";
            }
            case("%") -> {
                yield "idif";
            }
            default -> throw new IllegalStateException("Unexpected value: " + token.getCode());
        };
    }

    public String getSignOperationsDouble(Token token) throws IOException {
        return switch (token.getCode()){
            case ("*") -> {
                appendTabbed("iadd");
                yield "dmul";
            }
            case("/") -> {
                appendTabbed("idif");
                yield "ddiv";
            }
            case("%") -> {
                appendTabbed("idif");
                yield "ddif";
            }
            default -> throw new IllegalStateException("Unexpected value: " + token.getCode());
        };
    }
}
