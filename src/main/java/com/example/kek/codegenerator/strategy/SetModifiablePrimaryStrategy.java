package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.Layerable;
import com.example.kek.codegenerator.Value;
import com.example.kek.semantic.analyzer.AST.ASTNode;
import com.example.kek.semantic.analyzer.AST.ModifiablePrimary;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

public class SetModifiablePrimaryStrategy extends GenerationStrategy implements Layerable {
    Value settinValue;

    public SetModifiablePrimaryStrategy(ASTNode nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables, Value setValue) {
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
        this.settinValue = setValue;
    }
    @Override
    public void before() throws IOException {
        ModifiablePrimary curModifiablePrimary = (ModifiablePrimary) nodeContext;
        Value curValue = variableContext.get(curModifiablePrimary.getIdent().getName());
        if (isPrimitive(curValue)) {
            switch (curValue.getType()){
                case ("I") -> {
                    Value newVal = castInteger(settinValue);
                    loadValue(newVal);
                }
                case ("D") -> {
                    Value newVal = castDouble(settinValue);
                    loadValue(newVal);
                }
                case ("Z") -> {
                    Value newVal = castBoolean(settinValue);
                    loadValue(newVal);
                }
            }
            storeValue(curValue);
            return;
        }

        if (curModifiablePrimary.getModifiablePrimary() == null){
            if (curModifiablePrimary.getExpr() != null){
                Value arrInd = (new ExpressionStrategy(curModifiablePrimary.getExpr(), variableContext, freeVariables)).handleExpression();
                if (!Objects.equals(arrInd.getType(), "I")) throw new RuntimeException("array index must be integer");
                loadValue(curValue);
                switch (curValue.getType().substring(1)){
                    case ("I") -> {
                        Value newVal = castInteger(settinValue);
                        loadValue(arrInd);
                        loadValue(newVal);
                        appendTabbed("iastore");
                    }
                    case ("D") -> {
                        Value newVal = castDouble(settinValue);
                        loadValue(arrInd);
                        loadValue(newVal);
                        appendTabbed("dastore");
                    }
                    case ("Z") -> {
                        Value newVal = castBoolean(settinValue);
                        loadValue(arrInd);
                        loadValue(newVal);
                        appendTabbed("iastore");
                    }

                    default -> {
                        loadValue(arrInd);
                        loadValue(settinValue);
                        appendTabbed("aastore");
                    }
                }
            }
            else{
                loadValue(settinValue);
                storeValue(curValue);
            }
            return;
        }
    }



    @Override
    public List<ASTNode> getChildren(ASTNode parent) throws IOException {
        return null;
    }
}
