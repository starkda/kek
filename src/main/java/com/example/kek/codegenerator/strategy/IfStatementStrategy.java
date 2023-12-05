package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.Value;
import com.example.kek.semantic.analyzer.AST.ASTNode;
import com.example.kek.semantic.analyzer.AST.IfStatement;
import com.example.kek.semantic.analyzer.AST.Type;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

import static com.example.kek.codegenerator.CodeGenerator.getUniversalCounter;

public class IfStatementStrategy extends GenerationStrategy{
    Type retType;

    public IfStatementStrategy(IfStatement nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables, Type retType) {
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
        this.retType = retType;
    }

    @Override
    public void before() throws IOException {
        Value expr = new ExpressionStrategy(((IfStatement) nodeContext).getExpression(), variableContext, freeVariables).handleExpression();
        if (!Objects.equals(expr.getType(), "I") && !Objects.equals(expr.getType(), "Z")) {
            throw new RuntimeException("If statement expression must be bool or int");
        }
        int cnt = getUniversalCounter();

        loadValue(expr);
        appendTabbed(String.format("ifgt greaterThanZero_%d", cnt));
        (new BodyStrategy(((IfStatement) nodeContext).getBodyElse(), variableContext, freeVariables, retType, false)).before();
        appendTabbed(String.format("goto endIf_%d", cnt));
        appendTabbed(String.format("greaterThanZero_%d:", cnt));
        (new BodyStrategy(((IfStatement) nodeContext).getBodyThen(), variableContext, freeVariables, retType, false)).before();
        appendTabbed(String.format("endIf_%d:", cnt));
    }

    @Override
    public List<ASTNode> getChildren(ASTNode parent) throws IOException {
        return null;
    }
}
