package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.CodeGenerator;
import com.example.kek.codegenerator.Value;
import com.example.kek.lexical.analyzer.token.Token;
import com.example.kek.syntax.analyzer.AST.ASTNode;
import com.example.kek.syntax.analyzer.AST.Expression;
import com.example.kek.syntax.analyzer.AST.Relation;
import com.example.kek.syntax.analyzer.AST.Simple;
import lombok.AllArgsConstructor;

import javax.management.ConstructorParameters;
import java.io.IOException;
import java.util.*;

public class RelationStrategy extends GenerationStrategy{

    public RelationStrategy(ASTNode nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables){
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
    }

    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return List.of();
    }


    public Value handleRelation() throws IOException {
        Relation relation = (Relation) nodeContext;
        if (Objects.isNull(relation.getSignOperators()) || relation.getSignOperators().isEmpty()) {
            Simple simple = ((Relation) nodeContext).getSimples().get(0);
            SimpleStrategy simpleStrategy= new SimpleStrategy(simple, variableContext, freeVariables);
            return simpleStrategy.handleSimple();
        } else {
            List<Value> doubleValues = new ArrayList<>();
            for (Simple simple : relation.getSimples()) {
                SimpleStrategy simpleStrategy = new SimpleStrategy(simple, variableContext, freeVariables);
                Value val = simpleStrategy.handleSimple();
                doubleValues.add(castDouble(val));
            }

            int newVarNumber = freeVariables.pollFirst();
            Value newValue = new Value("Z", newVarNumber, null, null, null);
            declareVariable(newVarNumber, "Z");
            Value firstDouble = doubleValues.get(0);
            Value secondDouble = doubleValues.get(1);
            if (firstDouble.getVarNumber() != null) loadDouble(firstDouble.getVarNumber());
            else loadLiteral(firstDouble.getLiteralDouble());
            if (secondDouble.getVarNumber() != null) loadDouble(secondDouble.getVarNumber());
            else loadLiteral(secondDouble.getLiteralDouble());

            getSignOperation(((Relation) nodeContext).getSignOperators().get(0));

            storeBool(newValue.getVarNumber());

            return newValue;
        }

    }

    private int getSignOperation(Token token) throws IOException {
        int specific = CodeGenerator.getUniversalCounter();
        return switch (token.getCode()) {
            case ("<") -> {
                appendTabbed("dcmpg");
                appendTabbed(String.format("ifge notLessThan_%d", specific));
                appendTabbed("iconst_1");
                appendTabbed(String.format("goto endComparison_%d", specific));
                appendTabbed(String.format("notLessThan_%d:", specific));
                appendTabbed("iconst_0");
                appendTabbed(String.format("endComparison_%d:", specific));
                yield 0;
            }
            case ("<=") -> {
                appendTabbed("dcmpg");
                appendTabbed(String.format("ifgt notLessThanOrEqual_%d", specific));
                appendTabbed("iconst_1");
                appendTabbed(String.format("goto endComparison_%d", specific));
                appendTabbed(String.format("notLessThanOrEqual_%d:", specific));
                appendTabbed("iconst_0");
                appendTabbed(String.format("endComparison_%d:", specific));
                yield 0;
            }
            case (">") -> {
                appendTabbed("dcmpg");
                appendTabbed(String.format("ifle notGreaterThan_%d", specific));
                appendTabbed("iconst_1");
                appendTabbed(String.format("goto endComparison_%d", specific));
                appendTabbed(String.format("notGreaterThan_%d:", specific));
                appendTabbed("iconst_0");
                appendTabbed(String.format("endComparison_%d:", specific));
                yield 0;
            }
            case (">=") -> {
                appendTabbed("dcmpl");
                appendTabbed(String.format("iflt notGreaterThanOrEqual_%d", specific));
                appendTabbed("iconst_1");
                appendTabbed(String.format("goto endComparison_%d", specific));
                appendTabbed(String.format("notGreaterThanOrEqual_%d:", specific));
                appendTabbed("iconst_0");
                appendTabbed(String.format("endComparison_%d:", specific));
                yield 0;
            }
            default -> throw new RuntimeException("undefined sign operation during handling Relation");
        };
    }
}
