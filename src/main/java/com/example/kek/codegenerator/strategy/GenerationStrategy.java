package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.CodeGenerator;

import java.io.IOException;

//интрфейс того что нужно принтить находясь в конкретной вершине.
public interface GenerationStrategy {
    default void before() throws IOException {}

    default void after() throws IOException{}


    default void appendTabbed(String string) throws IOException {
        CodeGenerator.file.append("\t").append(string).append("\n");
    }

    default void append(String string) throws IOException {
        CodeGenerator.file.append(string).append("\n");
    }

    default void appendStackAllocation() throws IOException {
        appendTabbed(".limit stack 50");
        appendTabbed(".limit locals 50");
    }
}
