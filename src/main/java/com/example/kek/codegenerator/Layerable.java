package com.example.kek.codegenerator;

public interface Layerable {
    default void throwIfReference(Value value){
        switch (value.getType()){
            case ("D"), ("I"), ("Z") -> {}
            default -> throw new RuntimeException("Tried to use reference type during operation on primitives");
        }
    }

    default boolean isPrimitive(Value value){
        return switch (value.getType()){
            case ("D"), ("I"), ("Z") -> true;
            default -> false;
        };
    }

    default boolean isPrimitive(String type){
        return switch (type){
            case ("D"), ("I"), ("Z") -> true;
            default -> false;
        };
    }
}
