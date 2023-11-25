package com.example.kek.codegenerator;

import com.example.kek.codegenerator.strategy.GenerationStrategy;
import com.example.kek.codegenerator.strategy.ProgramStrategy;
import com.example.kek.syntax.analyzer.AST.ASTNode;
import com.example.kek.syntax.analyzer.AST.AbstractSyntaxTree;
import com.example.kek.syntax.analyzer.AST.Assignment;
import com.example.kek.syntax.analyzer.AST.Program;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

//предоствляет интерфейс для преобразовния ast в jasmin assembler
@RequiredArgsConstructor
public class CodeGenerator {
    final AbstractSyntaxTree abstractSyntaxTree;
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
        //some subtree stuff
        generationStrategy.after();
    }

    private GenerationStrategy strategize(ASTNode astNode) {
        if (astNode.getClass().equals(Program.class)) {
            return new ProgramStrategy();
        }
         else {
             throw new RuntimeException("undefined AST node!!!!");
        }
    }
}
