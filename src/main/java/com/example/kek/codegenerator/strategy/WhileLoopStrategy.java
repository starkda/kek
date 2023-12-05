package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.CodeGenerator;
import com.example.kek.codegenerator.Value;
import com.example.kek.semantic.analyzer.AST.ASTNode;
import com.example.kek.semantic.analyzer.AST.IfStatement;
import com.example.kek.semantic.analyzer.AST.Type;
import com.example.kek.semantic.analyzer.AST.WhileLoop;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

public class WhileLoopStrategy extends GenerationStrategy{
    Type retType;
    public WhileLoopStrategy(ASTNode nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables, Type retType) {
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
        this.retType = retType;
    }

    @Override
    public void before() throws IOException {
        int cnt = CodeGenerator.getUniversalCounter();
        Value expr = new ExpressionStrategy(((WhileLoop) nodeContext).getExpression(), variableContext, freeVariables).handleExpression();
        if (!Objects.equals(expr.getType(), "I") && !Objects.equals(expr.getType(), "Z")) {
            throw new RuntimeException("If statement expression must be bool or int");
        }

        appendTabbed(String.format("whileLoop_%d:", cnt));
        loadValue(expr);
        appendTabbed(String.format("ifle endWhile_%d", cnt));
        (new BodyStrategy(((WhileLoop) nodeContext).getBody(), variableContext, freeVariables, retType, false)).before();
        appendTabbed(String.format("goto whileLoop_%d", cnt));
        appendTabbed(String.format("endWhile_%d:", cnt));
    }

    @Override
    public List<ASTNode> getChildren(ASTNode parent) throws IOException {
        return null;
    }
}
