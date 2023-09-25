package com.example.kek.lexical.analyzer;

import com.example.kek.lexical.analyzer.token.*;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.PatternSyntaxException;


public class LexicalAnalyzer {
    private final Set<String> specials = Set.of("#", "@", "\\$");

    private final Set<String> operatorsForSplit = Set.of("<=", ">=", "/=");
    private final Set<String> operators = Set.of("<=", ">=", "/=", ":", "\\[", "]", "\\{", "}", "\\(", "\\)", ";", "\\.", ",", "\"", "'", "=", "-", "\\+",
            "\\*", "%", "/", "<", ">");
    private final Set<String> operatorsForCompare = Set.of("<=", ">=", "/=", ":", "[", "]", "{", "}", "(", ")", ";", ".", ",", "", "'", "=", "-", "+",
            "*", "%", "/", "<", ">");
    private final Set<String> keyWords = Set.of(
            "while", "loop", "reverse", "for", "in",
            "and", "or", "xor", "not", "true", "false",
            "var", "routine", "end",
            "integer", "real", "boolean",
            "record", "array", "is",
    "if", "then", "else");


    public List<String> tokenization(String preparedSourceCode) {
        List<String> ans = new ArrayList<>();
        String[] preOut = preparedSourceCode.split(" ");
        for (String candidate : preOut)
            if (!candidate.equals(""))
                ans.add(candidate);
        return ans;
    }

    private String readCodeFromFile(String fileName) {
        StringBuilder allCode = new StringBuilder();
        try (FileInputStream fileInputStream = new FileInputStream(fileName)) {
            BufferedInputStream sc = new BufferedInputStream(fileInputStream, 200);
            int i;
            while ((i = sc.read()) != -1) {
                allCode.append((char)i);
            }
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        return allCode.toString();
    }

    private String preprocessingSourceCode(String sourceCode) {
        try {
            sourceCode = sourceCode.replaceAll("\n", " ");
            for (String op : operatorsForSplit)
                sourceCode = sourceCode.replaceAll(op, " " + op + " ");
            for (String op : operators)
                sourceCode = sourceCode.replaceAll(op, " " + op + " ");

            for (String op : specials)
                sourceCode = sourceCode.replaceAll(op, " " + op + " ");
            return sourceCode;
        }
        catch (PatternSyntaxException e){
            System.err.println("preprocessingSourceCode\n\n" + e);
            return "";
        }
    }
    private Token categorizeToken(String token) {
        if (operatorsForCompare.contains(token))
            return new Operator(token);
        else if (specials.contains(token))
            return new SpecialSymbol(token);
        else if (keyWords.contains(token))
            return new KeyWord(token);
        else if(isNumeric(token))
            return new Literal(token);
        return new Identifier(token);

    }
    private boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e){
            return false;
        }
    }


    public List<String> generateTokens(String fileName)  {
        String sourceCode = readCodeFromFile(fileName);
        String readySourceCode = preprocessingSourceCode(sourceCode);
        return new ArrayList<>(tokenization(readySourceCode));
    }

    public List<Token> categorizeTokens(List<String> tokens) {
        List<Token> allCategorizeTokens = new ArrayList<>();
        for (String token : tokens) {
            allCategorizeTokens.add(categorizeToken(token));
        }
        return allCategorizeTokens;
    }
}
