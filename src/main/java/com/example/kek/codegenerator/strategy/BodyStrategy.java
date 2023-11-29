package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.Value;
import com.example.kek.semantic.analyzer.AST.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class BodyStrategy extends GenerationStrategy{


    public BodyStrategy(ASTNode nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables){
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
    }
    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return null;
    }

    @Override
    public void before() throws IOException {
        List<ASTNode> bodyChildren = ((Body) nodeContext).getDeclarationsAndStatements();
        int ok = 0;
        for(ASTNode child : bodyChildren){
            if (child.getClass().equals(VariableDeclaration.class)){
                VariableDeclaration variableDeclaration = (VariableDeclaration) child;
                VariableDeclarationStrategy variableDeclarationStrategy = new VariableDeclarationStrategy(variableDeclaration, variableContext, freeVariables);
                variableDeclarationStrategy.before();
            }

            else if (child.getClass().equals(Statement.class)) {
                    ASTNode temp = ((Statement) child).getStatement();
                    if (temp.getClass().equals(ReturnCall.class)) {
                        ok = 1;
                        ReturnCall returnCall = (ReturnCall) temp;
                        ReturnStrategy returnStrategy = new ReturnStrategy(returnCall, variableContext, freeVariables);
                        returnStrategy.before();
                    }
            }

        }
        if (ok == 0){
            appendTabbed("return");
        }
    }
}
