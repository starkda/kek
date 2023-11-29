package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.Value;
import com.example.kek.syntax.analyzer.AST.ASTNode;
import com.example.kek.syntax.analyzer.AST.ReturnCall;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

public class ReturnStrategy extends GenerationStrategy{

    public ReturnStrategy(ASTNode nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables){
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
    }

    @Override
    public void before() throws IOException {
        ReturnCall returnCall = (ReturnCall) nodeContext;
        Value value = new ExpressionStrategy(returnCall.getExpression(), variableContext, freeVariables).handleExpression();

        if (value.getVarNumber() != null){
            if (Objects.equals(value.getType(), "I")){
                loadInt(value.getVarNumber());
                appendTabbed("ireturn");
            }
            else if (Objects.equals(value.getType(), "Z")){
                loadBool(value.getVarNumber());
                appendTabbed("ireturn");

            }
            else if (Objects.equals(value.getType(), "D")){
                loadDouble(value.getVarNumber());
                appendTabbed("dreturn");
            }
        }
        else{
            if (Objects.equals(value.getType(), "I")){
                loadLiteral(value.getLiteralInt());
                appendTabbed("ireturn");
            }
            else if (Objects.equals(value.getType(), "Z")){
                loadLiteral(value.getLiteralBoolean());
                appendTabbed("ireturn");
            }
            else if (Objects.equals(value.getType(), "D")){
                loadLiteral(value.getLiteralDouble());
                appendTabbed("dreturn");
            }
        }
    }
    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return List.of();
    }
}
