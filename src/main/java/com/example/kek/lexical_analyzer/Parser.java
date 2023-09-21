package com.example.kek.lexical_analyzer;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Scanner;
import java.util.Set;

public class Parser {
    static Set<Character> special = Set.of(':', '[', ']', '{', '}', '(', ')', ';', '.', ',', '\"', '\'', '=', '-', '+',
            '*', '%', '/', '<', '>');

    static List<String> splitTokenCandidate(String input) {
        List<String> ans = new ArrayList<>();
        String pref = "";
        for (int i = 0; i < input.length(); i++) {
            if (special.contains(input.charAt(i))) {
                if (pref.length() != 0) {
                    ans.add(pref);
                }
                ans.add(String.valueOf(input.charAt(i)));
                pref = "";
            } else {
                pref = pref + input.charAt(i);
            }
        }
        if (pref.length() != 0){
            ans.add(pref);
        }
        return ans;
    }



    public static List<String> generateTokens(String input) {
        List<String> tokens = new ArrayList<>();
        try (Scanner sc = new Scanner(new File(input))) {
            while (sc.hasNext()) {
                String tokenCandidate = sc.next();
                tokens.addAll(splitTokenCandidate(tokenCandidate));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return tokens;
    }

}
