package com.example.kek.lexical.analyzer.token;

import lombok.Getter;

import java.util.List;

@Getter
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
        System.out.println(code + " =======   " + this.getClass() + ",  (line, position)" + " =======   " + "(" + line + ", " + position + ")");
    }

    public int getOrderInTokenList(List<Token> categorizedTokens) throws Exception {
        int i = 0;
        for(Token token: categorizedTokens)
            if(token.line == this.line && token.position == this.position)
                return i;
        else ++i;
        throw new Exception("can't to find token with " + showCodeLinePosition());
    }

    public String showCodeLinePosition(){
        return "code:" + this.code + "\n  line:" + this.line + "   position:" + this.position;
    }

}
