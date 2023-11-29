package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.Value;
import com.example.kek.semantic.analyzer.AST.ASTNode;
import com.example.kek.semantic.analyzer.AST.Summand;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class SummandStrategy extends GenerationStrategy {

    public SummandStrategy(ASTNode nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables) {
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
    }

    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return List.of();
    }

    public Value handleSummand() throws IOException {
        Summand summand = (Summand) nodeContext;
        return switch (summand.getType()){
            case ("boolean") -> new Value("Z", null, null, summand.getBoolLiteral(), null);
            case("real") -> new Value("D", null, summand.getRealLiteral(), null, null);
            case ("integer") -> new Value("I", null, null, null, summand.getIntLiteral());
            case("routineCall") -> (new RoutineCallStrategy(summand.getRoutineCall(), variableContext, freeVariables)).handleRoutineCall();
            case("modifiablePrimary") -> variableContext.get(summand.getModifiablePrimary().getIdent().getName());
            default -> throw new RuntimeException("Unexpected value: " + summand.getType());
        };
    }
}
