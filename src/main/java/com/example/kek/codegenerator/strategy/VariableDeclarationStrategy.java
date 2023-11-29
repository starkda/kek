package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.Value;
import com.example.kek.semantic.analyzer.AST.ASTNode;
import com.example.kek.semantic.analyzer.AST.Expression;
import com.example.kek.semantic.analyzer.AST.PrimitiveType;
import com.example.kek.semantic.analyzer.AST.VariableDeclaration;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.TreeSet;

public class VariableDeclarationStrategy extends GenerationStrategy{

    public VariableDeclarationStrategy(ASTNode nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables){
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
    }
    Value value;

    @Override
    public void before() throws IOException {


        Value resValue = new Value(null, null, null, null, null);
        Expression expression = ((VariableDeclaration) nodeContext).getInitExp();
        if (expression != null) {
            ExpressionStrategy expressionStrategy = new ExpressionStrategy(expression, variableContext, freeVariables);
            value = expressionStrategy.handleExpression();
            int newVarNumber = freeVariables.pollFirst();
            resValue.setVarNumber(newVarNumber);
            if (value.getVarNumber() != null) {
                resValue.setVarNumber(newVarNumber);

                if (Objects.equals(value.getType(), "I")) {
                    declareVariable(newVarNumber, "I");
                    loadInt(value.getVarNumber());
                    storeInt(newVarNumber);
                    resValue.setType("I");
                } else if (Objects.equals(value.getType(), "Z")) {
                    declareVariable(newVarNumber, "Z");
                    loadBool(value.getVarNumber());
                    storeBool(newVarNumber);
                    resValue.setType("Z");
                } else if (Objects.equals(value.getType(), "D")) {
                    declareVariable(newVarNumber, "D");
                    freeVariables.pollFirst();
                    loadDouble(value.getVarNumber());
                    storeDouble(newVarNumber);
                    resValue.setType("D");
                }
            } else {
                if (Objects.equals(value.getType(), "I")) {
                    declareVariable(newVarNumber, "I");
                    loadLiteral(value.getLiteralInt());
                    storeInt(newVarNumber);
                    resValue.setType("I");
                } else if (Objects.equals(value.getType(), "Z")) {
                    declareVariable(newVarNumber, "I");
                    loadLiteral(value.getLiteralBoolean());
                    storeDouble(newVarNumber);
                    resValue.setType("Z");
                } else if (Objects.equals(value.getType(), "D")) {
                    declareVariable(newVarNumber, "D");
                    freeVariables.pollFirst();
                    loadLiteral(value.getLiteralDouble());
                    storeDouble(newVarNumber);
                    resValue.setType("D");
                }
            }
        }
        else{
            int newVarNumber = freeVariables.pollFirst();
            resValue.setVarNumber(newVarNumber);
            String name = ((PrimitiveType) (((VariableDeclaration) nodeContext).getType().getType())).getTypePrim();
            String type = mapTOJasminType(name);
            declareVariable(newVarNumber, type);
            switch (type){
                case("I"):
                    loadLiteral(0);
                    storeInt(newVarNumber);
                    break;
                case("Z"):
                    loadLiteral(false);
                    storeBool(newVarNumber);
                    break;
                case("D"):
                    loadLiteral(0.0);
                    storeDouble(newVarNumber);
                    break;
            }
        }
        variableContext.put(((VariableDeclaration) nodeContext).getIdent().getName(), resValue);
    }
    @Override
    public void after(){
    }

    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return List.of();
    }
}
