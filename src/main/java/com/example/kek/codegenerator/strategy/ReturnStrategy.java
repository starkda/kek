package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.CodeGenerator;
import com.example.kek.codegenerator.Value;
import com.example.kek.semantic.analyzer.AST.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

public class ReturnStrategy extends GenerationStrategy{
    Type retType;
    public ReturnStrategy(ASTNode nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables, Type retType){
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
        this.retType = retType;
    }

    @Override
    public void before() throws IOException {
        ReturnCall returnCall = (ReturnCall) nodeContext;
        Value value = new ExpressionStrategy(returnCall.getExpression(), variableContext, freeVariables).handleExpression();

        if (retType.getType() != null){
            if (retType.getType().getClass().equals(PrimitiveType.class)){
                String rawType =  ((PrimitiveType) retType.getType()).getTypePrim();
                value = switch (
                        mapTOJasminType(rawType)) {
                    case "I" -> castInteger(value);
                    case "D" -> castDouble(value);
                    case "Z" -> castBoolean(value);
                    default ->
                            throw new IllegalStateException("Unexpected value: " + ((PrimitiveType) ((VariableDeclaration) nodeContext).getType().getType()).getTypePrim());
                };
            }
        }

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
