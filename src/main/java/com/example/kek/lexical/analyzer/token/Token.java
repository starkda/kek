package com.example.kek.lexical.analyzer.token;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Token {
    public Token(String code, int line, int position){
        this.code = code;
        this.line = line;
        this.position = position;
    }
    private final String code;
    private final int line;
    private final int position;
    public void print (){
        System.out.println(code + " =======   " + this.getClass());
    }

}
