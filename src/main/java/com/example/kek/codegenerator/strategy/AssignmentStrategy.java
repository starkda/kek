package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.Value;
import com.example.kek.semantic.analyzer.AST.ASTNode;
import com.example.kek.semantic.analyzer.AST.Assignment;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class AssignmentStrategy extends GenerationStrategy{
    public AssignmentStrategy(Assignment nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables) {
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
    }

    @Override
    public void before() throws IOException {
        Assignment assignment = (Assignment) nodeContext;
        Value exprValue = (new ExpressionStrategy(assignment.getExpression(), variableContext, freeVariables)).handleExpression();
        (new SetModifiablePrimaryStrategy(assignment.getModifiablePrimary(), variableContext, freeVariables, exprValue)).before();
    }
    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return null;
    }
}
