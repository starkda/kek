package com.example.kek.syntax.analyzer;


import com.example.kek.syntax.analyzer.AST.ASTNode;
import com.example.kek.syntax.analyzer.AST.Expression;
import com.example.kek.syntax.analyzer.AST.Type;
import com.example.kek.syntax.analyzer.AST.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProgramStack {

    ProgramStack(List<TableElement> table) {
        this.table = table;
    }

    private final String VARIABLE = "variable";
    private final String FUNCTION = "function";
    private final String TYPE = "type";

    public void checkTableElement(String category, Summand summand) throws Exception {
        switch (category) {
            case "variable" -> {
                if (summand.getModifiablePrimary().getModifiablePrimary() == null)
                    for (TableElement elem : this.table) {
                        // если это просто переменная
                        if (Objects.equals(summand.getModifiablePrimary().getIdent().getCode(), elem.name) &&
                                Objects.equals(elem.category, VARIABLE))
                            return;
                    }
                throw new Exception("Error: illegal declaration in ProgramStack.checkTableElement(VARIABLE) \n" +
                        ", expected existing in table, but  receive : 'NOT' ");
            }
            case "function" -> {
                for (TableElement elem : this.table)
                    if (summand.getRoutineCall().getFunctionParams() != null &&
                            elem.arguments != null) {
                        if (elem.arguments.size() == summand.getRoutineCall().getFunctionParams().size() &&
                                elem.name.equals(summand.getRoutineCall().getIdent().getName()))
                            return;
                    } else if (elem.name.equals(summand.getRoutineCall().getIdent().getName()))
                        return;
                throw new Exception("Error: illegal declaration in ProgramStack.checkTableElement(FUNCTION) \n" +
                        ", expected existing in table, but  receive : 'NOT' ");
            }
            case "type" -> throw new Exception("Error: illegal declaration in ProgramStack.checkTableElement(TYPE) \n" +
                    ", expected 'NOT TYPE' ");
            default -> throw new Exception("Error: illegal declaration in ProgramStack.checkTableElement(VARIABLE) \n" +
                    ", expected VARIABLE or FUNCTION ");
        }
    }

    public Type getTableElementType(String category, Summand summand) throws Exception {
        switch (category) {
            case "variable" -> {
                if (summand.getModifiablePrimary().getModifiablePrimary() == null)
                    for (TableElement elem : this.table) {
                        // если это просто переменная
                        if (Objects.equals(summand.getModifiablePrimary().getIdent().getCode(), elem.name) &&
                                Objects.equals(elem.category, VARIABLE))
                            return elem.type;
                    }
                throw new Exception("Error: illegal declaration in ProgramStack.getTableElementType(VARIABLE) \n" +
                        ", expected existing in table, but  receive : 'NOT' ");
            }
            case "function" -> {
                for (TableElement elem : this.table)
                    if (summand.getRoutineCall().getFunctionParams() != null &&
                            elem.arguments != null) {
                        if (elem.arguments.size() == summand.getRoutineCall().getFunctionParams().size() &&
                                elem.name.equals(summand.getRoutineCall().getIdent().getName()))
                            return elem.type;
                    } else if (elem.name.equals(summand.getRoutineCall().getIdent().getName()))
                        return elem.type;
                throw new Exception("Error: illegal declaration in ProgramStack.getTableElementType(FUNCTION) \n" +
                        ", expected existing in table, but  receive : 'NOT' ");
            }
            case "type" -> throw new Exception("Error: illegal declaration in ProgramStack.getTableElementType(TYPE) \n" +
                    ", expected 'NOT TYPE' ");
            default -> throw new Exception("Error: illegal declaration in ProgramStack.getTableElementType(VARIABLE) \n" +
                    ", expected VARIABLE or FUNCTION ");
        }
    }

    public Expression getTableElementInitial(String category, Summand summand) throws Exception {
        switch (category) {
            case "variable" -> {
                if (summand.getModifiablePrimary().getModifiablePrimary() == null)
                    for (TableElement elem : this.table) {
                        // если это просто переменная
                        if (Objects.equals(summand.getModifiablePrimary().getIdent().getCode(), elem.name) &&
                                Objects.equals(elem.category, VARIABLE))
                                return elem.initial;
                    }
                throw new Exception("Error: illegal declaration in ProgramStack.getTableElementInitial(VARIABLE) \n" +
                        ", expected VARIABLE, but  receive : FUNCTION ");
            }
            case "function" -> {
                throw new Exception("Error: illegal declaration in ProgramStack.getTableElementInitial(FUNCTION) \n" +
                        ", expected existing in table, but  receive : 'NOT' ");
            }
            case "type" -> throw new Exception("Error: illegal declaration in ProgramStack.getTableElementInitial(TYPE) \n" +
                    ", expected 'NOT TYPE' ");
            default -> throw new Exception("Error: illegal declaration in ProgramStack.getTableElementInitial(VARIABLE) \n" +
                    ", expected VARIABLE or FUNCTION ");
        }
    }

    static class TableElement {
        // category is
        private String category;
        private String name;
        private Expression initial;
        private Type type;
        private String line;
        private List<Argument> arguments = new ArrayList<>();
    }

    // нужно для параметров функции
    static class Argument {
        Argument(Type type, String name) {
            this.type = type;
            this.name = name;
        }

        private Type type;
        private String name;
    }

    private List<TableElement> table = new ArrayList<>();

    public void addASTNode(ASTNode node) throws Exception {
        TableElement tableElement = new TableElement();
        if (node.getClass().equals(VariableDeclaration.class)) {
            tableElement.category = VARIABLE;
            tableElement.name = ((VariableDeclaration) node).getIdent().getName();
            if (((VariableDeclaration) node).getInitExp() != null)
                tableElement.initial = ((VariableDeclaration) node).getInitExp();
            if (((VariableDeclaration) node).getType() != null)
                tableElement.type = ((VariableDeclaration) node).getType();
            if (((VariableDeclaration) node).getIdent().getStartingLine() != null)
                tableElement.line = ((VariableDeclaration) node).getIdent().getStartingLine();
            tableElement.arguments = null;
        } else if (node.getClass().equals(RoutineDeclaration.class)) {
            tableElement.category = FUNCTION;
            tableElement.name = ((RoutineDeclaration) node).getIdent().getName();
            tableElement.initial = null;
            if (((RoutineDeclaration) node).getType() != null)
                tableElement.type = ((RoutineDeclaration) node).getType();
            if (((RoutineDeclaration) node).getIdent().getStartingLine() != null)
                tableElement.line = ((RoutineDeclaration) node).getIdent().getStartingLine();
            for (VariableDeclaration argument : ((RoutineDeclaration) node).getVariablesDeclaration())
                tableElement.arguments.add(new Argument(argument.getType(), argument.getIdent().getName()));
        } else if (node.getClass().equals(TypeDeclaration.class)) {
            tableElement.category = TYPE;
            tableElement.name = ((TypeDeclaration) node).getIdent().getName();
            tableElement.initial = null;
            if (((TypeDeclaration) node).getType() != null)
                tableElement.type = ((TypeDeclaration) node).getType();
            if (((TypeDeclaration) node).getIdent().getStartingLine() != null)
                tableElement.line = ((TypeDeclaration) node).getIdent().getStartingLine();
            if (((TypeDeclaration) node).getType() != null && ((TypeDeclaration) node).getType().getType() != null && ((UserType) (((TypeDeclaration) node).getType().getType())).getVariableDeclarations() != null)
                for (VariableDeclaration argument : ((UserType) (((TypeDeclaration) node).getType().getType())).getVariableDeclarations())
                    tableElement.arguments.add(new Argument(argument.getType(), argument.getIdent().getName()));
        } else throw new Exception("Error: illegal declaration in ProgramStack.addASTNode \n" +
                ", expected 'some' globa, but  receive: " + node.getClass());
    }

    public void addASTNode(Type userType) throws Exception {
        if (userType.getClass().equals(UserType.class)) {

        } else throw new Exception("Error: illegal declaration in ProgramStack.addASTNode(UserType) \n" +
                ", expected UserType, but  receive : " + userType.getClass());
    }


    public ProgramStack copy() {
        return new ProgramStack(this.table);
    }

}
