package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.Value;
import com.example.kek.lexical.analyzer.token.Identifier;
import com.example.kek.semantic.analyzer.AST.*;

import java.io.IOException;
import java.util.*;

public class VariableDeclarationStrategy extends GenerationStrategy {

    public VariableDeclarationStrategy(ASTNode nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables) {
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
    }

    Value value;

    @Override
    public void before() throws IOException {
        Type tp = ((VariableDeclaration) nodeContext).getType().getType();

        if (tp.getClass().equals(ArrayType.class)) {
            ArrayType arrayInfo = (ArrayType) tp;
            Value arrSize = (new ExpressionStrategy(arrayInfo.getExp(), variableContext, freeVariables)).handleExpression();
            if (!Objects.equals(arrSize.getType(), "I")) throw new RuntimeException("array size is not integer");

            Value retVal = new Value(null, freeVariables.pollFirst(), null, null, null);

            String retValType = "[";
            if (tp.getType().getType().getClass().equals(PrimitiveType.class)) {
                retValType += mapTOJasminType(((PrimitiveType) tp.getType().getType()).getTypePrim());
                retVal.setType(retValType);
                declareVariable(retVal.getVarNumber(), retVal.getType());
                loadValue(arrSize);
                appendTabbed(String.format("newarray %s", switch (retValType) {
                    case ("[I") -> "int";
                    case ("[D") -> "double";
                    case ("[Z") -> "boolean";
                    default -> throw new RuntimeException("undefined [ TYPE");
                }));
                storeReference(retVal.getVarNumber());
            }
            if (tp.getType().getType().getClass().equals(UserType.class)) {
                retValType += mapTOReferenceJasminType(((UserType) tp.getType().getType()).getIdent().getName());
                retVal.setType(retValType);
                declareVariable(retVal.getVarNumber(), retVal.getType());
                loadValue(arrSize);
                appendTabbed(String.format("anewarray %s", retValType.substring(2, retValType.length() - 1)));
                storeReference(retVal.getVarNumber());
            }
            if (tp.getType().getType().getClass().equals(ArrayType.class)) {
                throw new RuntimeException("multi-dimensional arrays not implemented");
            }


            variableContext.put(((VariableDeclaration) nodeContext).getIdent().getName(), retVal);
            return;
        }

        if (tp.getClass().equals(UserType.class)) {
            List<Value> values = new ArrayList<>();
            if (((VariableDeclaration) nodeContext).getInitExp() != null) {
                RoutineCall constructor = ((VariableDeclaration) nodeContext).getInitExp().getRelations().get(0).getSimples().get(0).getFactors().get(0).getSummands().get(0).getRoutineCall();

                constructor.getFunctionParams().stream().map(expression -> {
                    try {
                        return (new ExpressionStrategy(expression, variableContext, freeVariables)).handleExpression();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }).forEach(values::add);
            }
            Value newVal = (new RecordVariableDeclarationStrategy(nodeContext, variableContext, freeVariables, values, ((UserType) (((VariableDeclaration) nodeContext).getType().getType())).getIdent().getName())).handleRecord();
            variableContext.put(((VariableDeclaration) nodeContext).getIdent().getName(), newVal);
            return;
        }

        Value resValue = new Value(null, null, null, null, null);
        Expression expression = ((VariableDeclaration) nodeContext).getInitExp();
        if (expression != null) {
            ExpressionStrategy expressionStrategy = new ExpressionStrategy(expression, variableContext, freeVariables);
            value = expressionStrategy.handleExpression();
            int newVarNumber = freeVariables.pollFirst();
            resValue.setVarNumber(newVarNumber);

            if (((VariableDeclaration) nodeContext).getType() != null) {
                if (((VariableDeclaration) nodeContext).getType().getType().getClass().equals(PrimitiveType.class)) {
                    String rawType = ((PrimitiveType) ((VariableDeclaration) nodeContext).getType().getType()).getTypePrim();
                    value = switch (mapTOJasminType(rawType)) {
                        case "I" -> castInteger(value);
                        case "D" -> castDouble(value);
                        case "Z" -> castBoolean(value);
                        default ->
                                throw new IllegalStateException("Unexpected value: " + ((PrimitiveType) ((VariableDeclaration) nodeContext).getType().getType()).getTypePrim());
                    };
                }
            }

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
        } else {
            int newVarNumber = freeVariables.pollFirst();
            resValue.setVarNumber(newVarNumber);
            String name = ((PrimitiveType) (((VariableDeclaration) nodeContext).getType().getType())).getTypePrim();
            String type = mapTOJasminType(name);
            declareVariable(newVarNumber, type);
            switch (type) {
                case ("I"):
                    resValue.setType("I");
                    resValue.setLiteralInt(0);
                    loadLiteral(0);
                    storeInt(newVarNumber);

                    break;
                case ("Z"):
                    resValue.setType("Z");
                    resValue.setLiteralBoolean(false);
                    loadLiteral(false);
                    storeBool(newVarNumber);
                    break;
                case ("D"):
                    resValue.setType("D");
                    resValue.setLiteralDouble(0.0);
                    loadLiteral(0.0);
                    storeDouble(newVarNumber);
                    break;
            }
        }
        variableContext.put(((VariableDeclaration) nodeContext).getIdent().getName(), resValue);
    }

    @Override
    public void after() {
    }

    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return List.of();
    }
}
