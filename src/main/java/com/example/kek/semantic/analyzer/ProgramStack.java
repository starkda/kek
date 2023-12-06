package com.example.kek.semantic.analyzer;


import com.example.kek.semantic.analyzer.AST.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProgramStack {

    ProgramStack(List<TableElement> table) {
        this.table = table;
    }

    ProgramStack() {
    }

    private final String VARIABLE = "variable";
    private final String FUNCTION = "function";
    private final String TYPE = "type";


    static class TableElement {
        // category is
        private String category;
        private String name;
        private Expression initial;
        private Type type;
        private String line;
        private List<Argument> arguments = new ArrayList<>();
        private int level;

        public Type getType() {
            return this.type;
        }
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

    public void checkTableElement(String category, Summand summand) throws Exception {
        switch (category) {
            case VARIABLE -> {
                // если это просто переменная
                if (summand.getModifiablePrimary().getModifiablePrimary() == null)
                    for (TableElement elem : this.table) {
                        if (Objects.equals(summand.getModifiablePrimary().getIdent().getName(), elem.name) &&
                                Objects.equals(elem.category, VARIABLE))
                            return;
                    }
                else return;
                throw new Exception("Error: illegal declaration in ProgramStack.checkTableElement(VARIABLE) \n" +
                        ", expected existing in table, but  receive : 'NOT' ");
            }
            case FUNCTION -> {
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
            case TYPE -> throw new Exception("Error: illegal declaration in ProgramStack.checkTableElement(TYPE) \n" +
                    ", expected 'NOT TYPE' ");
            default -> throw new Exception("Error: illegal declaration in ProgramStack.checkTableElement(VARIABLE) \n" +
                    ", expected VARIABLE or FUNCTION ");
        }
    }

    public Expression getTableElementInitial(String category, Summand summand) throws Exception {
        switch (category) {
            case VARIABLE -> {
                if (summand.getModifiablePrimary().getModifiablePrimary() == null)
                    for (TableElement elem : this.table) {
                        // если это просто переменная
                        if (Objects.equals(summand.getModifiablePrimary().getIdent().getName(), elem.name) &&
                                Objects.equals(elem.category, VARIABLE))
                            return elem.initial;
                    }
                else return null;
                throw new Exception("Error: illegal declaration in ProgramStack.getTableElementInitial(VARIABLE) \n" +
                        ", expected VARIABLE, but  receive : STRUCTURE ");
            }
            case FUNCTION -> {
                throw new Exception("Error: illegal declaration in ProgramStack.getTableElementInitial(FUNCTION) \n" +
                        ", expected existing in table, but  receive : 'NOT' ");
            }
            case TYPE ->
                    throw new Exception("Error: illegal declaration in ProgramStack.getTableElementInitial(TYPE) \n" +
                            ", expected 'NOT TYPE' ");
            default ->
                    throw new Exception("Error: illegal declaration in ProgramStack.getTableElementInitial(VARIABLE) \n" +
                            ", expected VARIABLE or FUNCTION ");
        }
    }

    public TableElement getTableElement(String category, ASTNode node) throws Exception {
        switch (category) {
            case VARIABLE -> {
                if ((node).getClass().equals(ASTIdentifier.class)) {
                    for (TableElement elem : this.table) {
                        // если это просто переменная (не элемент массива или класса, то)
                        if (((ASTIdentifier) node).getName().equals(elem.name) && elem.category.equals("variable")) {
                            return elem;
                        }
                    }

                    throw new Exception("Error: illegal declaration in ProgramStack.getTableElement(VARIABLE) \n" +
                            ", expected: some Type, but  receive : not found ");
                }
                throw new Exception("Error: illegal declaration in ProgramStack.getTableElement(VARIABLE) \n" +
                        ", expected VARIABLE, but  receive some trash");
            }
            case FUNCTION -> {
                if ((node).getClass().equals(RoutineCall.class)) {
                    for (TableElement elem : this.table) {
                        // если нейминг у функций одинаковый и кол-во параметров == => они сходятся
                        if (((RoutineCall) node).getIdent().getName().equals(elem.name) && elem.arguments != null &&
                                ((RoutineCall) node).getFunctionParams() != null && ((RoutineCall) node).getFunctionParams().size() == elem.arguments.size() &&
                                elem.category.equals("function")) {
                            return elem;
                        }
                    }
                    throw new Exception("Error: illegal declaration in ProgramStack.getTableElement(VARIABLE) \n" +
                            ", expected: some Type, but  receive : not found ");
                } else if ((node).getClass().equals(RoutineDeclaration.class)) {
                    for (TableElement elem : this.table) {
                        // если нейминг у функций одинаковый и кол-во параметров == => они сходятся
                        if (((RoutineDeclaration) node).getIdent().getName().equals(elem.name) && elem.arguments != null &&
                                ((RoutineDeclaration) node).getVariablesDeclaration() != null && ((RoutineDeclaration) node).getVariablesDeclaration().size() == elem.arguments.size() &&
                                elem.category.equals("function")) {
                            return elem;
                        }
                    }
                    throw new Exception("Error: illegal declaration in ProgramStack.getTableElement(VARIABLE) \n" +
                            ", expected: some Type, but  receive : not found ");
                }
                throw new Exception("Error: illegal declaration in ProgramStack.getTableElement(FUNCTION) \n" +
                        ", expected FUNCTION, but  receive some trash");
            }
            case TYPE -> {
                if (((Type) (node)).getType().getClass().equals(UserType.class)) {
                    for (TableElement elem : this.table) {
                        // если это просто переменная (не элемент массива или класса, то)
                        if (((UserType)((Type) node).getType()).getIdent().getName().equals(elem.name) && (elem.category.equals("type"))) {
                            return elem;
                        } else
                            throw new Exception("Error: illegal declaration in ProgramStack.getTableElement(TYPE) \n" +
                                    ", expected: other");
                    }
                    throw new Exception("Error: illegal declaration in ProgramStack.checkAstNodeExistence(TYPE) \n" +
                            ", expected User TYPE, but  receive: ");
                }
                throw new Exception("Error: illegal declaration in ProgramStack.checkAstNodeExistence(TYPE) \n" +
                        ", expected User TYPE, but  receive: ");
            }
            default ->
                    throw new Exception("Error: illegal declaration in ProgramStack.getTableElement(VARIABLE) \n" +
                            ", expected VARIABLE or FUNCTION ");
        }
    }

    public TableElement getAndRemoveTableElement(String category, ASTNode node) throws Exception {
        switch (category) {
            case VARIABLE -> {
                if ((node).getClass().equals(ASTIdentifier.class)) {
                    for (TableElement elem : this.table) {
                        // если это просто переменная (не элемент массива или класса, то)
                        if (((ASTIdentifier) node).getName().equals(elem.name) && elem.category.equals("variable")) {
                            table.remove(elem);
                            return elem;
                        }
                    }

                    throw new Exception("Error: illegal declaration in ProgramStack.getTableElementType(VARIABLE) \n" +
                            ", expected: some Type, but  receive : not found ");
                }
                throw new Exception("Error: illegal declaration in ProgramStack.getTableElementType(VARIABLE) \n" +
                        ", expected VARIABLE, but  receive some trash");
            }
            case FUNCTION -> {
                if ((node).getClass().equals(RoutineCall.class)) {
                    for (TableElement elem : this.table) {
                        // если нейминг у функций одинаковый и кол-во параметров == => они сходятся
                        if (((RoutineCall) node).getIdent().getName().equals(elem.name) && elem.arguments != null &&
                                ((RoutineCall) node).getFunctionParams() != null && ((RoutineCall) node).getFunctionParams().size() == elem.arguments.size() &&
                                elem.category.equals("function")) {
                            table.remove(elem);
                            return elem;
                        }
                    }
                    throw new Exception("Error: illegal declaration in ProgramStack.getTableElementType(VARIABLE) \n" +
                            ", expected: some Type, but  receive : not found ");
                } else if ((node).getClass().equals(RoutineDeclaration.class)) {
                    for (TableElement elem : this.table) {
                        // если нейминг у функций одинаковый и кол-во параметров == => они сходятся
                        if (((RoutineDeclaration) node).getIdent().getName().equals(elem.name) && elem.arguments != null &&
                                ((RoutineDeclaration) node).getVariablesDeclaration() != null && ((RoutineDeclaration) node).getVariablesDeclaration().size() == elem.arguments.size() &&
                                elem.category.equals("function")) {
                            table.remove(elem);
                            return elem;
                        }
                    }
                    throw new Exception("Error: illegal declaration in ProgramStack.getTableElementType(VARIABLE) \n" +
                            ", expected: some Type, but  receive : not found ");
                }
                throw new Exception("Error: illegal declaration in ProgramStack.checkAstNodeExistence(FUNCTION) \n" +
                        ", expected FUNCTION, but  receive some trash");
            }
            case TYPE ->
                    throw new Exception("Error: illegal declaration in ProgramStack.getTableElementInitial(TYPE) \n" +
                            ", expected 'NOT TYPE' ");
            default ->
                    throw new Exception("Error: illegal declaration in ProgramStack.getTableElementInitial(VARIABLE) \n" +
                            ", expected VARIABLE or FUNCTION ");
        }
    }

    // проверка на то существует ли элемент в таблице, второй элемент - должен ли существовать, при расхождении - вызывается исключение
    // level - уровень на котором ищется елемент.
    // если shouldExist -> false,  тогда елемент удаляется из таблицы
    public void checkAstNodeExistence(ASTNode node, String category, boolean shouldExist, int level) throws Exception {
        boolean isExist = false;
        boolean isLevelEquel = false;
        switch (category) {
            case VARIABLE -> {
                if ((node).getClass().equals(ASTIdentifier.class)) {
                    for (TableElement elem : this.table) {
                        // если это просто переменная (не элемент массива или класса, то)
                        if (((ASTIdentifier) node).getName().equals(elem.name) && elem.category.equals("variable")) {
                            if (elem.level == level)
                                isLevelEquel = true;
                            if (!shouldExist)
                                table.remove(elem);
                            isExist = true;
                            break;
                        }
                    }
                    if ((shouldExist && isExist) || (!shouldExist && (!isExist || !isLevelEquel))) {
                        return;
                    } else
                        throw new Exception("Error: illegal declaration in ProgramStack.checkAstNodeExistence(VARIABLE) \n" +
                                ", expected: " + shouldExist + ", but  receive : " + isExist + " ");
                }
                throw new Exception("Error: illegal declaration in ProgramStack.checkAstNodeExistence(VARIABLE) \n" +
                        ", expected VARIABLE, but  receive some trash");
            }
            case FUNCTION -> {
                if ((node).getClass().equals(RoutineCall.class)) {
                    for (TableElement elem : this.table) {
                        // если нейминг у функций одинаковый и кол-во параметров == => они сходятся
                        if (((RoutineCall) node).getIdent().getName().equals(elem.name) && elem.arguments != null &&
                                ((RoutineCall) node).getFunctionParams() != null && ((RoutineCall) node).getFunctionParams().size() == elem.arguments.size() &&
                                elem.category.equals("function")) {
                            if (elem.level == level)
                                isLevelEquel = true;
                            if (!shouldExist)
                                table.remove(elem);
                            isExist = true;
                            break;
                        }
                    }
                    if ((shouldExist && isExist) || (!shouldExist && (!isExist || !isLevelEquel))) {
                        return;
                    } else
                        throw new Exception("Error: illegal declaration in ProgramStack.checkAstNodeExistence(FUNCTION) \n" +
                                ", expected: " + shouldExist + ", but  receive : " + isExist + " ");
                } else if ((node).getClass().equals(RoutineDeclaration.class)) {
                    for (TableElement elem : this.table) {
                        // если нейминг у функций одинаковый и кол-во параметров == => они сходятся
                        if (((RoutineDeclaration) node).getIdent().getName().equals(elem.name) && elem.arguments != null &&
                                ((RoutineDeclaration) node).getVariablesDeclaration() != null && ((RoutineDeclaration) node).getVariablesDeclaration().size() == elem.arguments.size() &&
                                elem.category.equals("function")) {
                            if (elem.level == level)
                                isLevelEquel = true;
                            if (!shouldExist)
                                table.remove(elem);
                            isExist = true;
                            break;
                        }
                    }
                    if ((shouldExist && isExist) || (!shouldExist && (!isExist || !isLevelEquel))) {
                        return;
                    } else
                        throw new Exception("Error: illegal declaration in ProgramStack.checkAstNodeExistence(FUNCTION) \n" +
                                ", expected: " + shouldExist + ", but  receive : " + isExist + " ");
                }
                throw new Exception("Error: illegal declaration in ProgramStack.checkAstNodeExistence(FUNCTION) \n" +
                        ", expected FUNCTION, but  receive some trash");
            }
            case TYPE -> {
                if (((TypeDeclaration) (node)).getType().getType().getClass().equals(UserType.class)) {
                    for (TableElement elem : this.table) {
                        // если это просто переменная (не элемент массива или класса, то)
                        if (((TypeDeclaration) node).getIdent().getName().equals(elem.name)) {
                            if (!shouldExist)
                                table.remove(elem);
                            isExist = true;
                            break;
                        }
                    }
                    if (shouldExist == isExist) {
                        return;
                    } else
                        throw new Exception("Error: illegal declaration in ProgramStack.checkAstNodeExistence(TYPE) \n" +
                                ", expected: " + shouldExist + ", but  receive : " + isExist + " ");
                }
                throw new Exception("Error: illegal declaration in ProgramStack.checkAstNodeExistence(TYPE) \n" +
                        ", expected User TYPE, but  receive: " + ((TypeDeclaration) (node)).getType().getType().getClass());
            }
            default -> throw new Exception("Error: illegal declaration in ProgramStack.getTableElementInitial(*) \n" +
                    ", expected VARIABLE or FUNCTION or TYPE");
        }
    }

    // добавить элемент в таблицу (здесь есть проверка на то, есть ли элемент на данном уровне уже в таблице)
    public void addASTNode(ASTNode node, int level) throws Exception {

        // TODO добавить проверку уровня и повторения идентификатора
        TableElement tableElement = new TableElement();
        if (node.getClass().equals(VariableDeclaration.class)) {
            checkAstNodeExistence(((VariableDeclaration) node).getIdent(), VARIABLE, false, level);
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
            checkAstNodeExistence(node, FUNCTION, false, level);
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
            checkAstNodeExistence(node, TYPE, false, level);
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
        } else if (node.getClass().equals(Assignment.class)) {
            checkAstNodeExistence(((Assignment) node).getModifiablePrimary().getIdent(), VARIABLE, true, level);
            tableElement.category = VARIABLE;
            tableElement.name = ((Assignment) node).getModifiablePrimary().getIdent().getName();
            if (((Assignment) node).getExpression() != null)
                tableElement.initial = ((Assignment) node).getExpression();
            if (((VariableDeclaration) node).getType() != null)
                tableElement.type = ((VariableDeclaration) node).getType();
            if (((VariableDeclaration) node).getIdent().getStartingLine() != null)
                tableElement.line = ((VariableDeclaration) node).getIdent().getStartingLine();
            tableElement.arguments = null;
        } else throw new Exception("Error: illegal declaration in ProgramStack.addASTNode \n" +
                ", expected 'some' globa, but  receive: " + node.getClass());

        tableElement.level = level;
        table.add(tableElement);
    }

    public void replaceASTNode(ASTNode node, int level) throws Exception {

        // TODO добавить проверку уровня и повторения идентификатора
        TableElement tableElement = new TableElement();
        if (node.getClass().equals(VariableDeclaration.class)) {
            checkAstNodeExistence(((VariableDeclaration) node).getIdent(), VARIABLE, true, level);
            tableElement = getAndRemoveTableElement(VARIABLE, node);
            tableElement.initial = ((VariableDeclaration) node).getInitExp();

        } else if (node.getClass().equals(RoutineDeclaration.class)) {
            checkAstNodeExistence(node, FUNCTION, true, level);
            tableElement = getAndRemoveTableElement(FUNCTION, node);

        } else if (node.getClass().equals(TypeDeclaration.class)) {
            checkAstNodeExistence(node, TYPE, true, level);
            tableElement = getAndRemoveTableElement(TYPE, node);

        } else if (node.getClass().equals(Assignment.class)) {
            checkAstNodeExistence(((Assignment) node).getModifiablePrimary().getIdent(), VARIABLE, true, level);
            tableElement = getAndRemoveTableElement(VARIABLE, ((Assignment) node).getModifiablePrimary().getIdent());

            tableElement.initial = ((Assignment) node).getExpression();
        } else throw new Exception("Error: illegal declaration in ProgramStack.addASTNode \n" +
                ", expected 'some' globa, but  receive: " + node.getClass());

        tableElement.level = level;
        table.add(tableElement);
    }


    public ProgramStack copy() {
        List<TableElement> table = new ArrayList<>();
        for (TableElement tableElement : this.table) {
            TableElement newTableElement = new TableElement();
            newTableElement.category = tableElement.category;
            newTableElement.arguments = tableElement.arguments;
            newTableElement.level = tableElement.level;
            newTableElement.name = tableElement.name;
            newTableElement.line = tableElement.line;
            newTableElement.initial = tableElement.initial;
            newTableElement.type = tableElement.type;
            table.add(newTableElement);
        }
        return new ProgramStack(table);
    }

}
