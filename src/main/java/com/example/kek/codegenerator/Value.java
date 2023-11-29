package com.example.kek.codegenerator;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@AllArgsConstructor
public class Value {
    String type;
    Integer varNumber;
    Double literalDouble;
    Boolean literalBoolean;
    Integer literalInt;
}
