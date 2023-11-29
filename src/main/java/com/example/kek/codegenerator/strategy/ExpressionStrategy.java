package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.Value;
import com.example.kek.lexical.analyzer.token.Token;
import com.example.kek.semantic.analyzer.AST.ASTNode;
import com.example.kek.semantic.analyzer.AST.Expression;
import com.example.kek.semantic.analyzer.AST.Relation;

import java.io.IOException;
import java.util.*;

public class ExpressionStrategy extends GenerationStrategy {


    public ExpressionStrategy(ASTNode nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables){
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
    }

    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return List.of();
    }

    public Value handleExpression() throws IOException {
        Expression expression = (Expression) nodeContext;
        if (Objects.isNull(expression.getLogicOperators()) || expression.getLogicOperators().isEmpty()) {
            Relation relation = ((Expression) nodeContext).getRelations().get(0);
            RelationStrategy relationStrategy = new RelationStrategy(relation, variableContext, freeVariables);
            return relationStrategy.handleRelation();
        } else {
            List<Value> intValues = new ArrayList<>();
            for (Relation relation : expression.getRelations()) {
                RelationStrategy relationStrategy = new RelationStrategy(relation, variableContext, freeVariables);
                Value val = relationStrategy.handleRelation();
                intValues.add(castInteger(val));
            }


            int operation_index = 0;
            Integer newVarNumber = freeVariables.pollFirst();
            Value newValue = new Value("I", null, null, null, null);
            declareVariable(newVarNumber, "I");
            for (Value intValue : intValues) {
                if (newValue.getVarNumber() == null) {
                    if (intValue.getVarNumber() != null) loadInt(intValue.getVarNumber());
                    else loadLiteral(intValue.getLiteralInt());

                    storeInt(newVarNumber);
                    newValue.setVarNumber(newVarNumber);
                } else {
                    loadInt(newValue.getVarNumber());
                    if (intValue.getVarNumber() != null) loadInt(intValue.getVarNumber());
                    else loadLiteral(intValue.getLiteralInt());

                    appendTabbed(getBitwiseOperation(((Expression) nodeContext).getTokens().get(operation_index)));
                    operation_index++;
                    storeInt(newValue.getVarNumber());
                }
            }

            return newValue;
        }
    }

    public String getBitwiseOperation(Token token) {
        return switch (token.getCode()) {
            case ("and") -> "iand";
            case ("or") -> "ior";
            case ("xor") -> "ixor";
            default -> throw new RuntimeException("undefined bitwise operation during handling Expression");
        };
    }

}
