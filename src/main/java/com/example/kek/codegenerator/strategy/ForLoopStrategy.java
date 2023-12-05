package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.CodeGenerator;
import com.example.kek.codegenerator.Value;
import com.example.kek.semantic.analyzer.AST.ASTNode;
import com.example.kek.semantic.analyzer.AST.ForLoop;
import com.example.kek.semantic.analyzer.AST.IfStatement;
import com.example.kek.semantic.analyzer.AST.Type;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

public class ForLoopStrategy extends GenerationStrategy{
    Type retType;
    public ForLoopStrategy(ForLoop nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables, Type retType) {
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
        this.retType = retType;
    }

    public void before() throws IOException {
        ForLoop forLoop = (ForLoop) nodeContext;
        Value i = new Value("I", freeVariables.pollFirst(), null, null, null);
        Value l = (new ExpressionStrategy(forLoop.getRange().getStartExp(), variableContext, freeVariables)).handleExpression();
        Value r = (new ExpressionStrategy(forLoop.getRange().getEndExp(), variableContext, freeVariables)).handleExpression();

        if (!Objects.equals(l.getType(), "I") || !Objects.equals(r.getType(), "I")) {
            throw new RuntimeException("For loop expressions must be int");
        }

        declareVariable(i.getVarNumber(), i.getType());
        variableContext.put(forLoop.getIdent().getName(), i);
        loadValue(l);
        storeValue(i);

        int cnt = CodeGenerator.getUniversalCounter();
        appendTabbed(String.format("loopBody_%d:", cnt));
        (new BodyStrategy(((ForLoop) nodeContext).getBody(), variableContext, freeVariables, retType, false)).before();
        appendTabbed(String.format("iinc %d 1", i.getVarNumber()));
        appendTabbed(String.format("checkCondition_%d:", cnt));
        loadValue(i);
        loadValue(r);
        appendTabbed(String.format("if_icmpgt endFor_%d", cnt));
        appendTabbed(String.format("goto loopBody_%d", cnt));
        appendTabbed(String.format("endFor_%d:", cnt));

    }

    @Override
    public List<ASTNode> getChildren(ASTNode parent) throws IOException {
        return null;
    }
}
