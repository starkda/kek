package com.example.kek.lexical.analyzer;

import com.example.kek.lexical.analyzer.token.*;

import java.io.*;
import java.util.*;
import java.util.regex.PatternSyntaxException;
import java.util.stream.Collectors;


public class LexicalAnalyzer {
    private final Set<String> specials = Set.of("#", "@", "\\$");

    private final Map<String, String> operatorsForSplit = Map.of("<=", " __lessOrEqual__ ", ">=", " __moreOrEqual__ ", "/=", " __notEqual__ ");
    private final Set<String> operators = Set.of("<=", ">=", "/=", ":", "[", "]", "{", "}", "(", ")", ";", ".", ",", "=", "-", "+",
            "\\*", "%", "/", "<", ">");
    private final Set<String> operatorsForCompare = Set.of(":=", "<=", ">=", "/=", ":", "[", "]", "{", "}", "(", ")", ";", ".", ",", "", "'", "=", "-", "+",
            "*", "%", "/", "<", ">");
    private final Set<String> keyWords = Set.of(
            "while", "loop", "reverse", "for", "in",
            "and", "or", "xor", "not", "true", "false",
            "var", "routine", "end",
            "integer", "real", "boolean",
            "record", "array", "is",
            "if", "then", "else");


    public List<Token> parseTokens(String sourceCode) {
        List<Token> tokens = new ArrayList<>();
        List<String> lines = List.of(sourceCode.split("\n"));
        int curLine = 0;
        int curPosition;
        for (String line: lines){
            curLine++;
            curPosition = 0;
            for (int i = 0; i < line.length(); i++){
                curPosition++;
                if (line.charAt(i) == ' ') continue;
                int l = i, r = i;
                while(r + 1 != line.length() && line.charAt(r + 1) != ' '){
                    r++;
                }

                while(r >= l){
                    if (line.substring(l, r + 1).matches("[0-9]+(\\.[0-9]+)?")) break;
                    if (line.substring(l, r + 1).matches("[A-Za-z]+[A-Za-z0-9]*")) break;
                    if (operators.contains(line.substring(l, r + 1))) break;
                    r--;
                }

                if (r < l){
                    System.err.printf("Illegal symbol at line %d position %d", curLine, curPosition);
                    System.exit(1);
                }
                tokens.add(new Token(line.substring(l, r + 1), curLine, curPosition));
                curPosition += (r - l);
                i = r;
            }
        }

        return tokens;
    }

    private String readCodeFromFile(String fileName) {
        StringBuilder allCode = new StringBuilder();
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            BufferedInputStream sc = new BufferedInputStream(fileInputStream, 200);
            int i;
            while ((i = sc.read()) != -1) {
                allCode.append((char) i);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return allCode.toString();
    }

    public List<Token> getTokensFromLexicalAnalyzer(String fileName){
        String sourceCode = readCodeFromFile(fileName);
        List<Token> rawTokens = parseTokens(sourceCode);
        return rawTokens.stream().map(this::categorizeToken).toList();
    }

    private Token categorizeToken(Token token) {
        if (operatorsForCompare.contains(token.getCode()))
            return new Operator(token.getCode(), token.getLine(), token.getPosition());
        else if (specials.contains(token.getCode()))
            return new SpecialSymbol(token.getCode(), token.getLine(), token.getPosition());
        else if (keyWords.contains(token.getCode()))
            return new KeyWord(token.getCode(), token.getLine(), token.getPosition());
        else if (isNumeric(token.getCode()))
            return new Literal(token.getCode(), token.getLine(), token.getPosition());
        return new Identifier(token.getCode(), token.getLine(), token.getPosition());

    }

    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }


}
