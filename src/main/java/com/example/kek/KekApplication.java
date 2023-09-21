package com.example.kek;

import com.example.kek.lexical_analyzer.Parser;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

public class KekApplication {
	public static void main(String[] args) {
		String input_file = "input.txt";
		if (args.length > 0){
			input_file = args[0];
		}

		System.out.println(Parser.generateTokens(input_file));
	}

}
