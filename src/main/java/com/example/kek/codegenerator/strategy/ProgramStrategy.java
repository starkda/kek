package com.example.kek.codegenerator.strategy;

import com.example.kek.process.Processor;

import java.io.IOException;

public class ProgramStrategy implements GenerationStrategy {
    @Override
    public void before() throws IOException {
        append(".class public Kek");
        append(".super java/lang/Object\n");
    }

    @Override
    public void after() throws IOException {
        append(".method public static main([Ljava/lang/String;)V");
        appendStackAllocation();
        appendTabbed(String.format("invokestatic KEK/%s()V", Processor.entryPoint));
        appendTabbed("return");
        append(".end method");
    }
}
