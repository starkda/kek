package com.example.kek;

import com.example.kek.process.Processor;
import org.springframework.boot.autoconfigure.SpringBootApplication;

public class KekApplication {
    public static void main(String[] args) throws Exception {
        Processor processor = new Processor(args.length > 0 ? args[0] : "src/test/Programs/codegen_tests/while.txt","entryPoint");
        processor.process();
    }

}
