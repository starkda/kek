package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.CodeGenerator;
import com.example.kek.codegenerator.Value;
import com.example.kek.semantic.analyzer.AST.ASTNode;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

//интрфейс того что нужно принтить находясь в конкретной вершине.
public abstract class GenerationStrategy {
    public ASTNode nodeContext = null;


    public Map<Value, String> staticContext = new HashMap<>();
    public Map<String, Value> variableContext = new HashMap<>();
    public TreeSet<Integer> freeVariables = new TreeSet<>(IntStream.range(0, 501).boxed().collect(Collectors.toSet()));

    public void before() throws IOException {
    }

    public void after() throws IOException {
    }

    abstract public List<ASTNode> getChildren(ASTNode parent);


    public void appendTabbed(String string) throws IOException {
        CodeGenerator.file.append("\t").append(string).append("\n");
    }

    public void append(String string) throws IOException {
        CodeGenerator.file.append(string).append("\n");
    }

    public void storeInt(int var) throws IOException {
        appendTabbed(String.format("istore %d", var));
    }

    public void storeDouble(int var) throws IOException {
        appendTabbed(String.format("dstore %d", var));
    }

    public void storeBool(int var) throws IOException {
        appendTabbed(String.format("istore %d", var));
    }

    public void declareInt(int ordinal) throws IOException {
        appendTabbed(String.format(".var %d is variable_%d I", ordinal, ordinal));
    }

    public void declareDouble(int ordinal) throws IOException {
        appendTabbed(String.format(".var %d is variable_%d D", ordinal, ordinal));
    }

    public void declareBool(int ordinal) throws IOException {
        appendTabbed(String.format(".var %d is variable_%d Z", ordinal, ordinal));
    }

    public void loadInt(int ordinal) throws IOException {
        appendTabbed(String.format("iload %d", ordinal));
    }

    public void loadBool(int ordinal) throws IOException {
        appendTabbed(String.format("iload %d", ordinal));
    }

    public void loadDouble(int ordinal) throws IOException {
        appendTabbed(String.format("dload %d", ordinal));
    }

    public void loadTrue() throws IOException {
        appendTabbed("iconst_1");
    }

    public void loadFalse() throws IOException {
        appendTabbed("iconst_0");
    }

    public void loadLiteral(boolean literal) throws IOException {
        appendTabbed(String.format("iconst_%d", literal? 1: 0));
    }

    public void loadLiteral(int literal) throws IOException {
        appendTabbed(String.format("ldc %d", literal));
    }

    public void loadLiteral(double literal) throws IOException {
        appendTabbed(String.format("ldc2_w %,.4f", literal));
    }

    public void add(String string) throws IOException {
        CodeGenerator.file.append(string);
    }

    public void declareVariable(int varNumber, String varType) throws IOException {
        appendTabbed(String.format(".var %d is variable_%d %s", varNumber, varNumber, varType));
    }

    public void appendStackAllocation() throws IOException {
        appendTabbed(".limit stack 50");
        appendTabbed(".limit locals 50");
    }

    public String mapTOJasminType(String rawType) {
        return switch (rawType) {
            case ("integer") -> "I";
            case ("real") -> "D";
            case ("boolean") -> "Z";
            default -> throw new RuntimeException();
        };
    }

    public Value castBoolean(Value value) throws IOException {
        if (value.getVarNumber() != null) {
            if (Objects.equals(value.getType(), "Z")) return value;

            else if (Objects.equals(value.getType(), "I")) {
                Integer newVarNumber = freeVariables.pollFirst();
                declareVariable(newVarNumber, "Z");
                appendTabbed(String.format("iload %d", value.getVarNumber()));
                appendTabbed(String.format("istore %d", newVarNumber));
                return new Value("Z", newVarNumber, null, null, null);
            } else {
                throw new RuntimeException("Illegal conversion: Double cannot be converted into Boolean");
            }
        }

        else {
            if (value.getLiteralBoolean() != null) return value;
            else if (value.getLiteralInt() != null) return new Value("Z", null, null,
                    value.getLiteralInt() == 1, null );
            else throw new RuntimeException("Illegal conversion: Double cannot be converted into Boolean");
        }
    }

    public Value castInteger(Value value) throws IOException {
        if (value.getVarNumber() != null){
            if (Objects.equals(value.getType(), "I")) return value;
            else if (Objects.equals(value.getType(), "Z")) {
                Integer newVarNumber = freeVariables.pollFirst();
                declareVariable(newVarNumber, "I");
                appendTabbed(String.format("iload %d", value.getVarNumber()));
                appendTabbed(String.format("istore %d", newVarNumber));
                return new Value("I", newVarNumber, null, null, null);
            }
            else {
                Integer newVarNumber = freeVariables.pollFirst();
                declareVariable(newVarNumber, "I");
                appendTabbed(String.format("dload %d", value.getVarNumber()));
                appendTabbed("d2i");
                appendTabbed(String.format("istore %d", newVarNumber));
                return new Value("I", newVarNumber, null, null, null);
            }
        }
        else{
            if (value.getLiteralInt() != null) return value;
            else if (value.getLiteralBoolean() != null) return new Value("I", null, null,
                    null, value.getLiteralBoolean() ? 1 : 0 );
            else return new Value("I", null, null,
                        null, value.getLiteralDouble().intValue());
        }
    }

    public Value castDouble(Value value) throws IOException {
        if (value.getVarNumber() != null) {
            if (Objects.equals(value.getType(), "D")) return value;
            else {
                Integer newVarNumber = freeVariables.pollFirst();
                freeVariables.pollFirst();
                declareVariable(newVarNumber, "D");
                appendTabbed(String.format("iload %d", value.getVarNumber()));
                appendTabbed("i2d");
                appendTabbed(String.format("dstore %d", newVarNumber));
                return new Value("D", newVarNumber, null, null, null);
            }
        }
        else{
            if (value.getLiteralDouble() != null) return value;
            else if (value.getLiteralInt() != null) return new Value("D", null, (double) value.getLiteralInt(),
                    null, null);
            else return new Value("D", null, value.getLiteralBoolean() ? 1.0 : 0.0,
                        null, null);
        }
    }
}
