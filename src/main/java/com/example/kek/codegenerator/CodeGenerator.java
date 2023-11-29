package com.example.kek.codegenerator;

import com.example.kek.codegenerator.strategy.FieldDeclarationStrategy;
import com.example.kek.codegenerator.strategy.GenerationStrategy;
import com.example.kek.codegenerator.strategy.ProgramStrategy;
import com.example.kek.codegenerator.strategy.RoutineDeclarationStrategy;
import com.example.kek.syntax.analyzer.AST.*;
import lombok.RequiredArgsConstructor;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

//предоствляет интерфейс для преобразовния ast в jasmin assembler
@RequiredArgsConstructor
public class CodeGenerator {
    final AbstractSyntaxTree abstractSyntaxTree;

    static int universalCounter = 0;
    Map<String, Value> staticContext = new HashMap<>();

    public static Map<String, String> returnTypes = new HashMap<>();
    public static BufferedWriter file;
    public void generateCode() {
        System.out.println(FileName.value);
        try(BufferedWriter fileRef = new BufferedWriter(new FileWriter(FileName.value));){
            file = fileRef;
            generationDfs(abstractSyntaxTree.program);
        }
        catch (IOException e){
            System.out.println("error happened");
        }
    }


    public void generationDfs(ASTNode astNode) throws IOException {
        GenerationStrategy generationStrategy = strategize(astNode);
        generationStrategy.before();
        for (ASTNode child : generationStrategy.getChildren(astNode)){
            generationDfs(child);
        }
        generationStrategy.after();
    }

    private GenerationStrategy strategize(ASTNode astNode) {
        GenerationStrategy generationStrategy;
        if (astNode.getClass().equals(Program.class)) {
            generationStrategy = new ProgramStrategy();
            Program program = (Program) astNode;
            for (ASTNode nodes: program.allMain){
                if (nodes.getClass().equals(RoutineDeclaration.class)) {
                    RoutineDeclaration routineDeclaration = (RoutineDeclaration) nodes;
                    String returnType = "V";
                    if (routineDeclaration.getType() != null) {
                        PrimitiveType primitiveType = (PrimitiveType) routineDeclaration.getType().getType();
                        returnType = mapTOJasminType(primitiveType.getTypePrim());
                    }

                    returnTypes.put(routineDeclaration.getIdent().getName(), returnType);
                }
            }
        }
        else if (astNode.getClass().equals(RoutineDeclaration.class)) {
            generationStrategy = new RoutineDeclarationStrategy();

        }
        else if (astNode.getClass().equals(FieldDeclaration.class)) {
            generationStrategy = new FieldDeclarationStrategy();
        }
         else {
             throw new RuntimeException("undefined AST node!!!!");
        }
         generationStrategy.nodeContext = astNode;
         return generationStrategy;
    }

    public static int getUniversalCounter(){
        return universalCounter++;
    }

    public String mapTOJasminType(String rawType) {
        return switch (rawType) {
            case ("integer") -> "I";
            case ("real") -> "D";
            case ("boolean") -> "Z";
            default -> throw new RuntimeException();
        };
    }
}
