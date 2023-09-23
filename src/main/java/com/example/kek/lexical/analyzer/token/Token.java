package com.example.kek.lexical.analyzer.token;

public abstract class Token {
    public Token(String code){
        this.code = code;
    }
    private final String code;
    public void print (){
        System.out.println(code + " =======   " + this.getClass());
    }

}
