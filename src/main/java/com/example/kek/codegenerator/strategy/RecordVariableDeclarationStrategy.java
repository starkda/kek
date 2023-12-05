package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.Field;
import com.example.kek.codegenerator.Value;
import com.example.kek.semantic.analyzer.AST.ASTNode;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static com.example.kek.codegenerator.CodeGenerator.recordFields;

public class RecordVariableDeclarationStrategy extends GenerationStrategy{
    List<Value> arguments;

    String name;

    public RecordVariableDeclarationStrategy(ASTNode nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables, List<Value> arguments, String name) {
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
        this.arguments = arguments;
        this.name = name;
    }


    public Value handleRecord() throws IOException {
        Value newValue = new Value(String.format("L%s;", name), freeVariables.pollFirst(), null, null, null);
        declareVariable(newValue.getVarNumber(), newValue.getType());
        appendTabbed(String.format("new %s", name));
        appendTabbed("dup");
        appendTabbed(String.format("invokespecial %s/<init>()V", name));
        storeReference(newValue.getVarNumber());

        List<Field> fields = recordFields.get(name);
        for (int i = 0; i < arguments.size(); i++){
            Field field = fields.get(i);
            Value value = arguments.get(i);
            value = switch (field.getType()){
                case ("I") -> castInteger(value);
                case ("D") -> castDouble(value);
                case ("Z") -> castBoolean(value);
                default -> value;
            };

            loadReference(newValue.getVarNumber());
            loadValue(value);

            appendTabbed(String.format("putfield %s/%s %s", name, field.getName(), value.getType()));
        }
        return newValue;
    }

    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return null;
    }
}
