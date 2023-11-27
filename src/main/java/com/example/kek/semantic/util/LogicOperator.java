package com.example.kek.semantic.util;

public class LogicOperator {
    public boolean xor(boolean a, boolean b){
        return !(a && b) && (a || b);
    }
}
