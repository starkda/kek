package com.example.kek.semantic.analyzer;

import com.example.kek.semantic.analyzer.AST.*;
import com.example.kek.semantic.util.Converter;
import com.example.kek.semantic.util.LogicOperator;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SemanticAnalyzer {
    private AbstractSyntaxTree abstractSyntaxTree;
    private ProgramStack programStack;
    private final String entryPoint;
    private final Converter converter;
    private final LogicOperator logicOperator;

    public SemanticAnalyzer(AbstractSyntaxTree abstractSyntaxTree, String entryPoint) {
        this.entryPoint = entryPoint;
        this.abstractSyntaxTree = abstractSyntaxTree;
        this.converter = new Converter();
        this.logicOperator = new LogicOperator();
        this.programStack = new ProgramStack();
    }

    public void makeSemanticAnalysis() throws Exception {
        checkAndSimplifyProgram();
        checkEntryPoint();
    }

    private void checkAndSimplifyProgram() throws Exception {
        // adding all global vars/types/records in stack (add scope) (мб стоит убрать и добавлять обычным способом, но тогра есть вопос про видимость
        // в проге, т.е. прога будет видеть только то, что выше. Но стоит подумать, что будет при UserTypeDeclaration)
        addAllGlobalToStack();
        if (abstractSyntaxTree.getProgram() != null && abstractSyntaxTree.getProgram().getAllMain() != null)
            for (ASTNode node : abstractSyntaxTree.getProgram().getAllMain()) {
                if (node.getClass().equals(VariableDeclaration.class)) {
                    checkAndSimplifyVariableDeclaration(this.programStack.copy(), node, 0, true);
                } else if (node.getClass().equals(RoutineDeclaration.class)) {
                    checkAndSimplifyRoutineDeclaration(this.programStack.copy(), node);
                } else if (node.getClass().equals(TypeDeclaration.class)) {
                    checkTypeDeclaration(this.programStack.copy(), (TypeDeclaration) node, true, 0, true);
                } else
                    throw new Exception("Error: illegal declaration in program (SemanticAnalyzer.checkFieldOfView) \n" +
                            ", expected 'some' node, but  required: " + node.getClass());
            }

    }

    private void addAllGlobalToStack() throws Exception {
        if (abstractSyntaxTree.getProgram() != null && abstractSyntaxTree.getProgram().getAllMain() != null)
            for (ASTNode node : abstractSyntaxTree.getProgram().getAllMain()) {
                programStack.addASTNode(node, 0);
            }
    }

    private void checkAndSimplifyVariableDeclaration(ProgramStack upperProgramStack, ASTNode astNode, int level, boolean itIsNotLoop) throws Exception {
        // есть initial value
        if (((VariableDeclaration) astNode).getInitExp() != null) {
            checkAndSimplifyExpression(upperProgramStack, ((VariableDeclaration) astNode).getInitExp(), itIsNotLoop);
        }

        // тип указан явно
        if (((VariableDeclaration) astNode).getType() != null && ((VariableDeclaration) astNode).getType().getType() != null) {
            if (((VariableDeclaration) astNode).getType().getType().getClass().equals(UserType.class)) {
                checkUserType(upperProgramStack.copy(), (UserType) ((VariableDeclaration) astNode).getType().getType());
            }
            if (((VariableDeclaration) astNode).getType().getType().getClass().equals(ArrayType.class)) {
                checkArrayType(upperProgramStack.copy(), (ArrayType) ((VariableDeclaration) astNode).getType().getType());
            }
        }

        // записать тип, если type - пустой, а initExpresion - простой TODO сделать для сложных типов (ArrayType + UserType)
        if (((VariableDeclaration) astNode).getInitExp() != null && ((VariableDeclaration) astNode).getInitExp().isSimple() &&
                (((VariableDeclaration) astNode).getType() == null || (((VariableDeclaration) astNode).getType() != null &&
                        ((VariableDeclaration) astNode).getType().getType() == null))) {
            ((VariableDeclaration) astNode).setType(new Type(new PrimitiveType(((VariableDeclaration) astNode).getInitExp().getSimple().getValue())));
        }

        // проверка на то, что Type initial value == variable Type TODO сделать для сложных типов (ArrayType + UserType)
        if (((VariableDeclaration) astNode).getInitExp() != null &&
                ((VariableDeclaration) astNode).getType() != null && ((VariableDeclaration) astNode).getType().getType() != null) {
            // если тип у initExp - известный +
            if (((VariableDeclaration) astNode).getInitExp().isTypeKnown() &&
                    !((PrimitiveType) ((VariableDeclaration) astNode).getType().getType()).getTypePrim().
                            equals(((VariableDeclaration) astNode).getInitExp().getSimple().getType()))
                throw new Exception("Error: illegal type in program (SemanticAnalyzer.checkVariableDeclaration) \n" +
                        ", expected " + ((VariableDeclaration) astNode).getType().getType().getClass() + "  , but  receive: "
                        + ((VariableDeclaration) astNode).getInitExp().getType().getClass());
        }

        // добавить в програм-стек, если не глобал.
        if (!(level == 0))
            upperProgramStack.addASTNode(astNode, level);
    }

    private void checkUserType(ProgramStack upperProgramStack, UserType userType) {

    }

    private void checkArrayType(ProgramStack upperProgramStack, ArrayType arrayType) {

    }

    // здесь проверяется и упрощается текущий Expression (and, or, xor)
    private void checkAndSimplifyExpression(ProgramStack upperProgramStack, Expression expression, boolean itIsNotLoop) throws Exception {
        if (!(expression.getRelations().size() == expression.getLogicOperators().size() + 1))
            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyExpression) \n" +
                    ", expected relations == logicOperators + 1 ");
        for (int i = 0; i < expression.getRelations().size(); ++i)
            checkAndSimplifyRelation(upperProgramStack, expression.getRelations().get(i), itIsNotLoop);

        if (itIsNotLoop)

            for (int i = 0; i < expression.getRelations().size() - 1; ++i)
                if (i + 1 < expression.getRelations().size())
                    // если текущий и след. -> простые, т.е. их можно упростить.
                    if (expression.getRelations().get(i).isSimple() && expression.getRelations().get(i + 1).isSimple()) {


                        // работает не на 100% (24.11.2023, если будет время -> проверить)
                        // проверка, что это не real
                        if (Objects.equals(expression.getRelations().get(i).getSimple().getType(), "real") || expression.getRelations().get(i + 1).getSimple().getType().equals("real")) {
                            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyExpression) \n" +
                                    ", logic some real");
                        }
                        // проверка, что это не boolean  + int (!= 1 and != 0)
                        if (Objects.equals(expression.getRelations().get(i).getSimple().getType(), "boolean") && expression.getRelations().get(i + 1).getSimple().getType().equals("integer") &&
                                (expression.getRelations().get(i + 1).getSimple().getIntLiteral() != 1 && (expression.getRelations().get(i + 1).getSimple().getIntLiteral() != 0))) {
                            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyExpression) \n" +
                                    ", can't compare boolean and integer , which doesn't equal 1 and 0");
                        }
                        // проверка, что это не  int (!= 1 and != 0) + boolean
                        if (Objects.equals(expression.getRelations().get(i).getSimple().getType(), "integer") && expression.getRelations().get(i + 1).getSimple().getType().equals("boolean") &&
                                (expression.getRelations().get(i).getSimple().getIntLiteral() != 1 && (expression.getRelations().get(i).getSimple().getIntLiteral() != 0))) {
                            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyExpression) \n" +
                                    ", can't compare boolean and integer , which doesn't equal 1 and 0");
                        }

                        boolean literal;
                        // boolean, boolean
                        if (Objects.equals(expression.getRelations().get(i).getSimple().getType(), "boolean") && expression.getRelations().get(i + 1).getSimple().getType().equals("boolean")) {

                            if (Objects.equals(expression.getLogicOperators().get(i).getCode(), "and"))
                                literal = expression.getRelations().get(i).getSimple().getBoolLiteral() && expression.getRelations().get(i + 1).getSimple().getBoolLiteral();
                            else if (Objects.equals(expression.getLogicOperators().get(i).getCode(), "or"))
                                literal = expression.getRelations().get(i).getSimple().getBoolLiteral() || expression.getRelations().get(i + 1).getSimple().getBoolLiteral();
                            else if (Objects.equals(expression.getLogicOperators().get(i).getCode(), "xor"))
                                literal = logicOperator.xor(expression.getRelations().get(i).getSimple().getBoolLiteral(), expression.getRelations().get(i + 1).getSimple().getBoolLiteral());
                            else
                                throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyExpression) \n" +
                                        ", expected or, and, xor, but receive:" + expression.getLogicOperators().get(i).getCode());

                            expression.getRelations().remove(i + 1);
                            expression.getLogicOperators().remove(i);
                            expression.getRelations().remove(i);
                            expression.getRelations().add(i, new Relation(new Simple(new Factor(new Summand(literal)))));
                        }
                        // boolean, integer
                        else if (Objects.equals(expression.getRelations().get(i).getSimple().getType(), "boolean") && expression.getRelations().get(i + 1).getSimple().getType().equals("integer")) {

                            if (Objects.equals(expression.getLogicOperators().get(i).getCode(), "and"))
                                literal = expression.getRelations().get(i).getSimple().getBoolLiteral() && converter.convertIntToBoolean(expression.getRelations().get(i + 1).getSimple().getIntLiteral());
                            else if (Objects.equals(expression.getLogicOperators().get(i).getCode(), "or"))
                                literal = expression.getRelations().get(i).getSimple().getBoolLiteral() || converter.convertIntToBoolean(expression.getRelations().get(i + 1).getSimple().getIntLiteral());
                            else if (Objects.equals(expression.getLogicOperators().get(i).getCode(), "xor"))
                                literal = logicOperator.xor(expression.getRelations().get(i).getSimple().getBoolLiteral(), converter.convertIntToBoolean(expression.getRelations().get(i + 1).getSimple().getIntLiteral()));
                            else
                                throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyExpression) \n" +
                                        ", expected or, and, xor, but receive:" + expression.getLogicOperators().get(i).getCode());

                            expression.getRelations().remove(i + 1);
                            expression.getLogicOperators().remove(i);
                            expression.getRelations().remove(i);
                            expression.getRelations().add(i, new Relation(new Simple(new Factor(new Summand(literal)))));
                        }
                        // integer, boolean
                        else if (Objects.equals(expression.getRelations().get(i).getSimple().getType(), "boolean") && expression.getRelations().get(i + 1).getSimple().getType().equals("boolean")) {

                            if (Objects.equals(expression.getLogicOperators().get(i).getCode(), "and"))
                                literal = converter.convertIntToBoolean(expression.getRelations().get(i).getSimple().getIntLiteral()) && expression.getRelations().get(i + 1).getSimple().getBoolLiteral();
                            else if (Objects.equals(expression.getLogicOperators().get(i).getCode(), "or"))
                                literal = converter.convertIntToBoolean(expression.getRelations().get(i).getSimple().getIntLiteral()) || expression.getRelations().get(i + 1).getSimple().getBoolLiteral();
                            else if (Objects.equals(expression.getLogicOperators().get(i).getCode(), "xor"))
                                literal = logicOperator.xor(converter.convertIntToBoolean(expression.getRelations().get(i).getSimple().getIntLiteral()), expression.getRelations().get(i + 1).getSimple().getBoolLiteral());
                            else
                                throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyExpression) \n" +
                                        ", expected or, and, xor, but receive:" + expression.getLogicOperators().get(i).getCode());

                            expression.getRelations().remove(i + 1);
                            expression.getLogicOperators().remove(i);
                            expression.getRelations().remove(i);
                            expression.getRelations().add(i, new Relation(new Simple(new Factor(new Summand(literal)))));
                        }
                        // integer, integer
                        else if (Objects.equals(expression.getRelations().get(i).getSimple().getType(), "boolean") && expression.getRelations().get(i + 1).getSimple().getType().equals("boolean")) {

                            if (Objects.equals(expression.getLogicOperators().get(i).getCode(), "and"))
                                literal = converter.convertIntToBoolean(expression.getRelations().get(i).getSimple().getIntLiteral()) && converter.convertIntToBoolean(expression.getRelations().get(i + 1).getSimple().getIntLiteral());
                            else if (Objects.equals(expression.getLogicOperators().get(i).getCode(), "or"))
                                literal = converter.convertIntToBoolean(expression.getRelations().get(i).getSimple().getIntLiteral()) || converter.convertIntToBoolean(expression.getRelations().get(i + 1).getSimple().getIntLiteral());
                            else if (Objects.equals(expression.getLogicOperators().get(i).getCode(), "xor"))
                                literal = logicOperator.xor(converter.convertIntToBoolean(expression.getRelations().get(i).getSimple().getIntLiteral()), converter.convertIntToBoolean(expression.getRelations().get(i + 1).getSimple().getIntLiteral()));
                            else
                                throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyExpression) \n" +
                                        ", expected or, and, xor, but receive:" + expression.getLogicOperators().get(i).getCode());

                            expression.getRelations().remove(i + 1);
                            expression.getLogicOperators().remove(i);
                            expression.getRelations().remove(i);
                            expression.getRelations().add(i, new Relation(new Simple(new Factor(new Summand(literal)))));
                        } else
                            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyExpression) \n" +
                                    ", expected or, and, xor, but received: " + expression.getLogicOperators().get(i).getCode());

                        --i;
                    }
    }

    // здесь проверяется и упрощается текущий Relation (<, <=, >, >=, =, /=)
    private void checkAndSimplifyRelation(ProgramStack upperProgramStack, Relation relation, boolean itIsNotLoop) throws Exception {
        if (!(relation.getSimples().size() == relation.getSignOperators().size() + 1))
            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyRelation) \n" +
                    ", expected Relations == signOperators + 1 ");
        for (int i = 0; i < relation.getSimples().size(); ++i)
            checkAndSimplifySimple(upperProgramStack, relation.getSimples().get(i), itIsNotLoop);
        if (itIsNotLoop)
            for (int i = 0; i < relation.getSimples().size() - 1; ++i)
                if (i + 1 < relation.getSimples().size())
                    // если текущий и след. -> простые, т.е. их можно упростить.
                    if (relation.getSimples().get(i).isSimple() && relation.getSimples().get(i + 1).isSimple()) {


                        // работает не на 100% (24.11.2023, если будет время -> проверить)
                        // проверка, что это не boolean + real
                        if (Objects.equals(relation.getSimples().get(i).getSimple().getType(), "boolean") && relation.getSimples().get(i + 1).getSimple().getType().equals("real")) {
                            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyRelation) \n" +
                                    ", can't compare boolean and real");
                        }
                        // проверка, что это не real + boolean
                        if (Objects.equals(relation.getSimples().get(i).getSimple().getType(), "real") && relation.getSimples().get(i + 1).getSimple().getType().equals("boolean")) {
                            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyRelation) \n" +
                                    ", can't compare real and boolean");
                        }
                        // проверка, что это не boolean  + int (!= 1 and != 0)
                        if (Objects.equals(relation.getSimples().get(i).getSimple().getType(), "boolean") && relation.getSimples().get(i + 1).getSimple().getType().equals("integer") &&
                                (relation.getSimples().get(i + 1).getSimple().getIntLiteral() != 1 && (relation.getSimples().get(i + 1).getSimple().getIntLiteral() != 0))) {
                            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyRelation) \n" +
                                    ", can't compare boolean and integer , which doesn't equal 1 and 0");
                        }
                        // проверка, что это не  int (!= 1 and != 0) + boolean
                        if (Objects.equals(relation.getSimples().get(i).getSimple().getType(), "integer") && relation.getSimples().get(i + 1).getSimple().getType().equals("boolean") &&
                                (relation.getSimples().get(i).getSimple().getIntLiteral() != 1 && (relation.getSimples().get(i).getSimple().getIntLiteral() != 0))) {
                            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyRelation) \n" +
                                    ", can't compare boolean and integer , which doesn't equal 1 and 0");
                        }

                        boolean literal;
                        // real, real
                        if (Objects.equals(relation.getSimples().get(i).getSimple().getType(), "real") && relation.getSimples().get(i + 1).getSimple().getType().equals("real")) {

                            if (Objects.equals(relation.getSignOperators().get(i).getCode(), "<"))
                                literal = relation.getSimples().get(i).getSimple().getRealLiteral() < relation.getSimples().get(i + 1).getSimple().getRealLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "<="))
                                literal = relation.getSimples().get(i).getSimple().getRealLiteral() <= relation.getSimples().get(i + 1).getSimple().getRealLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), ">"))
                                literal = relation.getSimples().get(i).getSimple().getRealLiteral() > relation.getSimples().get(i + 1).getSimple().getRealLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), ">="))
                                literal = relation.getSimples().get(i).getSimple().getRealLiteral() >= relation.getSimples().get(i + 1).getSimple().getRealLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "="))
                                literal = relation.getSimples().get(i).getSimple().getRealLiteral() == relation.getSimples().get(i + 1).getSimple().getRealLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "/="))
                                literal = relation.getSimples().get(i).getSimple().getRealLiteral() != relation.getSimples().get(i + 1).getSimple().getRealLiteral();
                            else
                                throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyRelation) \n" +
                                        ", expected one of known sign, but receive:" + relation.getSignOperators().get(i).getCode());

                            relation.getSimples().remove(i + 1);
                            relation.getSignOperators().remove(i);
                            relation.getSimples().remove(i);
                            relation.getSimples().add(i, new Simple(new Factor(new Summand(literal))));
                        }
                        // real, integer
                        else if (Objects.equals(relation.getSimples().get(i).getSimple().getType(), "real") && relation.getSimples().get(i + 1).getSimple().getType().equals("integer")) {
                            if (Objects.equals(relation.getSignOperators().get(i).getCode(), "<"))
                                literal = relation.getSimples().get(i).getSimple().getRealLiteral() < relation.getSimples().get(i + 1).getSimple().getIntLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "<="))
                                literal = relation.getSimples().get(i).getSimple().getRealLiteral() <= relation.getSimples().get(i + 1).getSimple().getIntLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), ">"))
                                literal = relation.getSimples().get(i).getSimple().getRealLiteral() > relation.getSimples().get(i + 1).getSimple().getIntLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), ">="))
                                literal = relation.getSimples().get(i).getSimple().getRealLiteral() >= relation.getSimples().get(i + 1).getSimple().getIntLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "="))
                                literal = relation.getSimples().get(i).getSimple().getRealLiteral() == relation.getSimples().get(i + 1).getSimple().getIntLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "/="))
                                literal = relation.getSimples().get(i).getSimple().getRealLiteral() != relation.getSimples().get(i + 1).getSimple().getIntLiteral();
                            else
                                throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyRelation) \n" +
                                        ", expected one of known sign, but receive:" + relation.getSignOperators().get(i).getCode());

                            relation.getSimples().remove(i + 1);
                            relation.getSignOperators().remove(i);
                            relation.getSimples().remove(i);
                            relation.getSimples().add(i, new Simple(new Factor(new Summand(literal))));
                        }
                        // integer, real
                        else if (Objects.equals(relation.getSimples().get(i).getSimple().getType(), "integer") && relation.getSimples().get(i + 1).getSimple().getType().equals("real")) {
                            if (Objects.equals(relation.getSignOperators().get(i).getCode(), "<"))
                                literal = relation.getSimples().get(i).getSimple().getIntLiteral() < relation.getSimples().get(i + 1).getSimple().getRealLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "<="))
                                literal = relation.getSimples().get(i).getSimple().getIntLiteral() <= relation.getSimples().get(i + 1).getSimple().getRealLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), ">"))
                                literal = relation.getSimples().get(i).getSimple().getIntLiteral() > relation.getSimples().get(i + 1).getSimple().getRealLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), ">="))
                                literal = relation.getSimples().get(i).getSimple().getIntLiteral() >= relation.getSimples().get(i + 1).getSimple().getRealLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "="))
                                literal = relation.getSimples().get(i).getSimple().getIntLiteral() == relation.getSimples().get(i + 1).getSimple().getRealLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "/="))
                                literal = relation.getSimples().get(i).getSimple().getIntLiteral() != relation.getSimples().get(i + 1).getSimple().getRealLiteral();
                            else
                                throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyRelation) \n" +
                                        ", expected one of known sign, but receive:" + relation.getSignOperators().get(i).getCode());

                            relation.getSimples().remove(i + 1);
                            relation.getSignOperators().remove(i);
                            relation.getSimples().remove(i);
                            relation.getSimples().add(i, new Simple(new Factor(new Summand(literal))));
                        }
                        // integer, integer
                        else if (Objects.equals(relation.getSimples().get(i).getSimple().getType(), "integer") && relation.getSimples().get(i + 1).getSimple().getType().equals("integer")) {
                            if (Objects.equals(relation.getSignOperators().get(i).getCode(), "<"))
                                literal = relation.getSimples().get(i).getSimple().getIntLiteral() < relation.getSimples().get(i + 1).getSimple().getIntLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "<="))
                                literal = relation.getSimples().get(i).getSimple().getIntLiteral() <= relation.getSimples().get(i + 1).getSimple().getIntLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), ">"))
                                literal = relation.getSimples().get(i).getSimple().getIntLiteral() > relation.getSimples().get(i + 1).getSimple().getIntLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), ">="))
                                literal = relation.getSimples().get(i).getSimple().getIntLiteral() >= relation.getSimples().get(i + 1).getSimple().getIntLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "="))
                                literal = relation.getSimples().get(i).getSimple().getIntLiteral() == relation.getSimples().get(i + 1).getSimple().getIntLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "/="))
                                literal = relation.getSimples().get(i).getSimple().getIntLiteral() != relation.getSimples().get(i + 1).getSimple().getIntLiteral();
                            else
                                throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyRelation) \n" +
                                        ", expected one of known sign, but receive:" + relation.getSignOperators().get(i).getCode());

                            relation.getSimples().remove(i + 1);
                            relation.getSignOperators().remove(i);
                            relation.getSimples().remove(i);
                            relation.getSimples().add(i, new Simple(new Factor(new Summand(literal))));
                        }
                        // integer, boolean
                        else if (Objects.equals(relation.getSimples().get(i).getSimple().getType(), "integer") && relation.getSimples().get(i + 1).getSimple().getType().equals("boolean")) {
                            if (Objects.equals(relation.getSignOperators().get(i).getCode(), "<"))
                                literal = relation.getSimples().get(i).getSimple().getIntLiteral() < converter.convertBooleanToInt(relation.getSimples().get(i + 1).getSimple().getBoolLiteral());
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "<="))
                                literal = relation.getSimples().get(i).getSimple().getIntLiteral() <= converter.convertBooleanToInt(relation.getSimples().get(i + 1).getSimple().getBoolLiteral());
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), ">"))
                                literal = relation.getSimples().get(i).getSimple().getIntLiteral() > converter.convertBooleanToInt(relation.getSimples().get(i + 1).getSimple().getBoolLiteral());
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), ">="))
                                literal = relation.getSimples().get(i).getSimple().getIntLiteral() >= converter.convertBooleanToInt(relation.getSimples().get(i + 1).getSimple().getBoolLiteral());
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "="))
                                literal = relation.getSimples().get(i).getSimple().getIntLiteral() == converter.convertBooleanToInt(relation.getSimples().get(i + 1).getSimple().getBoolLiteral());
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "/="))
                                literal = relation.getSimples().get(i).getSimple().getIntLiteral() != converter.convertBooleanToInt(relation.getSimples().get(i + 1).getSimple().getBoolLiteral());
                            else
                                throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyRelation) \n" +
                                        ", expected one of known sign, but receive:" + relation.getSignOperators().get(i).getCode());

                            relation.getSimples().remove(i + 1);
                            relation.getSignOperators().remove(i);
                            relation.getSimples().remove(i);
                            relation.getSimples().add(i, new Simple(new Factor(new Summand(literal))));
                        }
                        // boolean, integer
                        else if (Objects.equals(relation.getSimples().get(i).getSimple().getType(), "boolean") && relation.getSimples().get(i + 1).getSimple().getType().equals("integer")) {
                            if (Objects.equals(relation.getSignOperators().get(i).getCode(), "<"))
                                literal = converter.convertBooleanToInt(relation.getSimples().get(i).getSimple().getBoolLiteral()) < relation.getSimples().get(i + 1).getSimple().getIntLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "<="))
                                literal = converter.convertBooleanToInt(relation.getSimples().get(i).getSimple().getBoolLiteral()) <= relation.getSimples().get(i + 1).getSimple().getIntLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), ">"))
                                literal = converter.convertBooleanToInt(relation.getSimples().get(i).getSimple().getBoolLiteral()) > relation.getSimples().get(i + 1).getSimple().getIntLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), ">="))
                                literal = converter.convertBooleanToInt(relation.getSimples().get(i).getSimple().getBoolLiteral()) >= relation.getSimples().get(i + 1).getSimple().getIntLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "="))
                                literal = converter.convertBooleanToInt(relation.getSimples().get(i).getSimple().getBoolLiteral()) == relation.getSimples().get(i + 1).getSimple().getIntLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "/="))
                                literal = converter.convertBooleanToInt(relation.getSimples().get(i).getSimple().getBoolLiteral()) != relation.getSimples().get(i + 1).getSimple().getIntLiteral();
                            else
                                throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyRelation) \n" +
                                        ", expected one of known sign, but receive:" + relation.getSignOperators().get(i).getCode());

                            relation.getSimples().remove(i + 1);
                            relation.getSignOperators().remove(i);
                            relation.getSimples().remove(i);
                            relation.getSimples().add(i, new Simple(new Factor(new Summand(literal))));
                        }
                        // boolean, boolean.g
                        else if (Objects.equals(relation.getSimples().get(i).getSimple().getType(), "boolean") && relation.getSimples().get(i + 1).getSimple().getType().equals("boolean")) {
                            if (Objects.equals(relation.getSignOperators().get(i).getCode(), "<"))
                                literal = converter.convertBooleanToInt(relation.getSimples().get(i).getSimple().getBoolLiteral()) < converter.convertBooleanToInt(relation.getSimples().get(i + 1).getSimple().getBoolLiteral());
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "<="))
                                literal = converter.convertBooleanToInt(relation.getSimples().get(i).getSimple().getBoolLiteral()) <= converter.convertBooleanToInt(relation.getSimples().get(i + 1).getSimple().getBoolLiteral());
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), ">"))
                                literal = converter.convertBooleanToInt(relation.getSimples().get(i).getSimple().getBoolLiteral()) > converter.convertBooleanToInt(relation.getSimples().get(i + 1).getSimple().getBoolLiteral());
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), ">="))
                                literal = converter.convertBooleanToInt(relation.getSimples().get(i).getSimple().getBoolLiteral()) >= converter.convertBooleanToInt(relation.getSimples().get(i + 1).getSimple().getBoolLiteral());
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "="))
                                literal = relation.getSimples().get(i).getSimple().getBoolLiteral() == relation.getSimples().get(i + 1).getSimple().getBoolLiteral();
                            else if (Objects.equals(relation.getSignOperators().get(i).getCode(), "/="))
                                literal = relation.getSimples().get(i).getSimple().getBoolLiteral() != relation.getSimples().get(i + 1).getSimple().getBoolLiteral();
                            else
                                throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyRelation) \n" +
                                        ", expected one of known sign, but receive:" + relation.getSignOperators().get(i).getCode());

                            relation.getSimples().remove(i + 1);
                            relation.getSignOperators().remove(i);
                            relation.getSimples().remove(i);
                            relation.getSimples().add(i, new Simple(new Factor(new Summand(literal))));
                        } else
                            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyRelation) \n" +
                                    ", expected one of matched type for compare, but received: " + relation.getSignOperators().get(i).getCode());
                        --i;
                    }

    }


    // здесь проверяется и упрощается текущий Simple (сложение, вычитание)
    private void checkAndSimplifySimple(ProgramStack upperProgramStack, Simple simple, boolean itIsNotLoop) throws Exception {
        if (!(simple.getFactors().size() == simple.getSignOperators().size() + 1))
            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifySimple) \n" +
                    ", expected simples == signOperators + 1 ");
        for (int i = 0; i < simple.getFactors().size(); ++i)
            checkAndSimplifyFactor(upperProgramStack, simple.getFactors().get(i), itIsNotLoop);

        if (itIsNotLoop)
            for (int i = 0; i < simple.getFactors().size() - 1; ++i)
                if (i + 1 < simple.getFactors().size())
                    // если текущий и след. -> простые, т.е. их можно упростить.
                    if (simple.getFactors().get(i).isSimple() && simple.getFactors().get(i + 1).isSimple()) {


                        // работает не на 100% (24.11.2023, если будет время -> проверить)
                        if (Objects.equals(simple.getFactors().get(i).getSimple().getType(), "boolean") || simple.getFactors().get(i + 1).getSimple().getType().equals("boolean")) {
                            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifySimple) \n" +
                                    ", expected Summand's type not 'boolean'");
                        }
                        // real, real
                        if (Objects.equals(simple.getFactors().get(i).getSimple().getType(), "real") && simple.getFactors().get(i + 1).getSimple().getType().equals("real")) {
                            double literal = 0.0;
                            if (Objects.equals(simple.getSignOperators().get(i).getCode(), "+"))
                                literal = simple.getFactors().get(i).getSimple().getRealLiteral() + simple.getFactors().get(i + 1).getSimple().getRealLiteral();
                            else if (Objects.equals(simple.getSignOperators().get(i).getCode(), "-"))
                                literal = simple.getFactors().get(i).getSimple().getRealLiteral() - simple.getFactors().get(i + 1).getSimple().getRealLiteral();

                            simple.getFactors().remove(i + 1);
                            simple.getSignOperators().remove(i);
                            simple.getFactors().remove(i);
                            simple.getFactors().add(i, new Factor(new Summand(literal)));
                        }
                        // real, integer
                        else if (Objects.equals(simple.getFactors().get(i).getSimple().getType(), "real") && simple.getFactors().get(i + 1).getSimple().getType().equals("integer")) {
                            double literal = 0.0;
                            if (Objects.equals(simple.getSignOperators().get(i).getCode(), "+"))
                                literal = simple.getFactors().get(i).getSimple().getRealLiteral() + simple.getFactors().get(i + 1).getSimple().getIntLiteral();
                            else if (Objects.equals(simple.getSignOperators().get(i).getCode(), "-"))
                                literal = simple.getFactors().get(i).getSimple().getRealLiteral() - simple.getFactors().get(i + 1).getSimple().getIntLiteral();

                            simple.getFactors().remove(i + 1);
                            simple.getSignOperators().remove(i);
                            simple.getFactors().remove(i);
                            simple.getFactors().add(i, new Factor(new Summand(literal)));
                        }
                        // integer, real
                        else if (Objects.equals(simple.getFactors().get(i).getSimple().getType(), "integer") && simple.getFactors().get(i + 1).getSimple().getType().equals("real")) {
                            double literal = 0.0;
                            if (Objects.equals(simple.getSignOperators().get(i).getCode(), "+"))
                                literal = simple.getFactors().get(i).getSimple().getIntLiteral() + simple.getFactors().get(i + 1).getSimple().getRealLiteral();
                            else if (Objects.equals(simple.getSignOperators().get(i).getCode(), "-"))
                                literal = simple.getFactors().get(i).getSimple().getIntLiteral() - simple.getFactors().get(i + 1).getSimple().getRealLiteral();

                            simple.getFactors().remove(i + 1);
                            simple.getSignOperators().remove(i);
                            simple.getFactors().remove(i);
                            simple.getFactors().add(i, new Factor(new Summand(literal)));
                        }
                        // integer, integer
                        else if (Objects.equals(simple.getFactors().get(i).getSimple().getType(), "integer") && simple.getFactors().get(i + 1).getSimple().getType().equals("integer")) {
                            int literal = 0;
                            if (Objects.equals(simple.getSignOperators().get(i).getCode(), "+"))
                                literal = simple.getFactors().get(i).getSimple().getIntLiteral() + simple.getFactors().get(i + 1).getSimple().getIntLiteral();
                            else if (Objects.equals(simple.getSignOperators().get(i).getCode(), "-"))
                                literal = simple.getFactors().get(i).getSimple().getIntLiteral() - simple.getFactors().get(i + 1).getSimple().getIntLiteral();

                            simple.getFactors().remove(i + 1);
                            simple.getSignOperators().remove(i);
                            simple.getFactors().remove(i);
                            simple.getFactors().add(i, new Factor(new Summand(literal)));
                        } else
                            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifySimple) \n" +
                                    ", expected one of matched type for compare, but received: " + simple.getFactors().get(i).getSimple().getType());
                        --i;
                    }
    }

    // проверка на правильность + упрощение Factor (умножение, деление, остаток от деления)
    private void checkAndSimplifyFactor(ProgramStack upperProgramStack, Factor factor, boolean itIsNotLoop) throws Exception {
        if (!(factor.getSummands().size() == factor.getSignOperators().size() + 1))
            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkFactor) \n" +
                    ", expected factors == signOperators + 1 ");
        for (int i = 0; i < factor.getSummands().size(); ++i)
            checkAndSimplifySummand(upperProgramStack, factor.getSummands().get(i), factor);

        if (itIsNotLoop)
            // здесь упрощается текущий Factor
            for (int i = 0; i < factor.getSummands().size() - 1; ++i)
                if (i + 1 < factor.getSummands().size())

                    // если текущий и соседний Factor-ы простые
                    if (factor.getSummands().get(i).isSimple() && factor.getSummands().get(i + 1).isSimple()) {
                        // Далее разбираются разные невозможные кейсы, которые не возможны на текущем уровне абстрации
                        if (Objects.equals(factor.getSummands().get(i).getType(), "boolean") || factor.getSummands().get(i + 1).getType().equals("boolean")) {
                            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyFactor) \n" +
                                    ", expected Summand's type not 'boolean'");
                        }
                        if ((factor.getSummands().get(i + 1).getIntLiteral() == 0 || factor.getSummands().get(i + 1).getRealLiteral() == 0.0) &&
                                (factor.getSignOperators().get(i).getCode().equals("/") || factor.getSignOperators().get(i).getCode().equals("%")))
                            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyFactor) \n" +
                                    ", division by zero");


                        if ((Objects.equals(factor.getSummands().get(i).getType(), "real") || factor.getSummands().get(i + 1).getType().equals("real")) &&
                                Objects.equals(factor.getSignOperators().get(i).getCode(), "%"))
                            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyFactor) \n" +
                                    ", invalid operation '%' for 'real'");


                        // => ответ будет real;
                        if (Objects.equals(factor.getSummands().get(i).getType(), "real") || factor.getSummands().get(i + 1).getType().equals("real")) {
                            double literal = 0.0;

                            if (Objects.equals(factor.getSignOperators().get(i).getCode(), "/")) {
                                if (Objects.equals(factor.getSummands().get(i).getType(), "real") && factor.getSummands().get(i + 1).getType().equals("real"))
                                    literal = factor.getSummands().get(i).getRealLiteral() / factor.getSummands().get(i + 1).getRealLiteral();
                                else if (Objects.equals(factor.getSummands().get(i).getType(), "real") && factor.getSummands().get(i + 1).getType().equals("integer"))
                                    literal = factor.getSummands().get(i).getRealLiteral() / factor.getSummands().get(i + 1).getIntLiteral();
                                else
                                    literal = factor.getSummands().get(i).getIntLiteral() / factor.getSummands().get(i + 1).getRealLiteral();
                            }
                            // замечсу что % с real уже обрабатывалось
                            else if (Objects.equals(factor.getSignOperators().get(i).getCode(), "*")) {
                                if (Objects.equals(factor.getSummands().get(i).getType(), "real") && factor.getSummands().get(i + 1).getType().equals("real"))
                                    literal = factor.getSummands().get(i).getRealLiteral() * factor.getSummands().get(i + 1).getRealLiteral();
                                else if (Objects.equals(factor.getSummands().get(i).getType(), "real") && factor.getSummands().get(i + 1).getType().equals("integer"))
                                    literal = factor.getSummands().get(i).getRealLiteral() * factor.getSummands().get(i + 1).getIntLiteral();
                                else
                                    literal = factor.getSummands().get(i).getIntLiteral() * factor.getSummands().get(i + 1).getRealLiteral();
                            }
                            factor.getSummands().remove(i + 1);
                            factor.getSignOperators().remove(i);
                            factor.getSummands().remove(i);
                            factor.getSummands().add(i, new Summand(literal));
                        }
                        // => 1-> int; 2->  int
                        else {
                            int literal;
                            if (Objects.equals(factor.getSignOperators().get(i).getCode(), "/")) {
                                literal = factor.getSummands().get(i).getIntLiteral() / factor.getSummands().get(i + 1).getIntLiteral();
                            } else if (Objects.equals(factor.getSignOperators().get(i).getCode(), "*")) {
                                literal = factor.getSummands().get(i).getIntLiteral() * factor.getSummands().get(i + 1).getIntLiteral();
                            } else {
                                literal = factor.getSummands().get(i).getIntLiteral() % factor.getSummands().get(i + 1).getIntLiteral();
                            }
                            factor.getSummands().remove(i + 1);
                            factor.getSignOperators().remove(i);
                            factor.getSummands().remove(i);
                            factor.getSummands().add(i, new Summand(literal));
                        }
                        --i;
                    }
    }


    // проверка на правильность + упрощение Summand (если какой-то ModifiablePrimary есть в стеке, то заменить
    private void checkAndSimplifySummand(ProgramStack upperProgramStack, Summand summand, Factor factor) throws Exception {
        switch (summand.getType()) {
            case "modifiablePrimary":
                upperProgramStack.checkTableElement("variable", summand);
                if (upperProgramStack.getTableElementInitial("variable", summand) != null &&
                        upperProgramStack.getTableElementInitial("variable", summand).isSimple()) {
                    factor.replaceSummand(summand, upperProgramStack.getTableElementInitial("variable", summand).getSimple());
                }
                break;
            case "routineCall":
                upperProgramStack.checkTableElement("function", summand);
                break;
            case "integer":
                break;
            case "real":
                break;
            case "boolean":
                break;
            default:
                throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifySummand) \n" +
                        ", expected type one of summand's types, but receive other");
        }
    }


    private void checkAndSimplifyRoutineDeclaration(ProgramStack upperProgramStack, ASTNode astNode) throws Exception {
        for (VariableDeclaration params : ((RoutineDeclaration) astNode).getVariablesDeclaration())
            checkAndSimplifyVariableDeclaration(upperProgramStack, params, 1, true);
        checkAndSimplifyBody(upperProgramStack, ((RoutineDeclaration) astNode).getBody(), 1, true);
    }

    // проверка Body
    private void checkAndSimplifyBody(ProgramStack upperProgramStack, Body body, int level, boolean itIsNotLoop) throws Exception {
        // Body состоит из Statement-ов
        for (ASTNode statement : body.getDeclarationsAndStatements()) {
            if (statement.getClass().equals(VariableDeclaration.class)) {
                checkAndSimplifyVariableDeclaration(upperProgramStack.copy(), statement, level, itIsNotLoop);
            } else if (statement.getClass().equals(TypeDeclaration.class)) {

                // TODO написать чекер для нод в програм стеке
                checkTypeDeclaration(upperProgramStack.copy(), (TypeDeclaration) statement, level == 0, level, itIsNotLoop);
            } else if (statement.getClass().equals(Statement.class)) {
                if (((Statement) statement).getStatement().getClass().equals(Assignment.class)) {
                    checkAndSimplifyAssignment(upperProgramStack, (Assignment) ((Statement) statement).getStatement(), level, itIsNotLoop);
                } else if (((Statement) statement).getStatement().getClass().equals(RoutineCall.class)) {
                    upperProgramStack.checkAstNodeExistence(((Statement) statement).getStatement(), "function", true, level);
                } else if (((Statement) statement).getStatement().getClass().equals(IfStatement.class)) {
                    checkAndSimplifyIfStatement(upperProgramStack, (IfStatement) ((Statement) statement).getStatement(), level, itIsNotLoop);
                } else if (((Statement) statement).getStatement().getClass().equals(ForLoop.class)) {
                    checkAndSimplifyForLoop(upperProgramStack, (ForLoop) ((Statement) statement).getStatement(), level, itIsNotLoop);
                } else if (((Statement) statement).getStatement().getClass().equals(WhileLoop.class)) {
                    checkAndSimplifyWhileLoop(upperProgramStack, (WhileLoop) ((Statement) statement).getStatement(), level, itIsNotLoop);
                } else if (((Statement) statement).getStatement().getClass().equals(ReturnCall.class)) {

                } else
                    throw new Exception("Error: illegal declaration in program (SemanticAnalyzer.checkAndSimplifyRoutineDeclaration) \n" +
                            ", expected one of Statements: Assignment, If, For, While, return,  , but  required: " + statement.getClass());


            } else
                throw new Exception("Error: illegal declaration in program (SemanticAnalyzer.checkAndSimplifyRoutineDeclaration) \n" +
                        ", expected Statement, RoutineCall or VariableDeclaration , but  required: " + statement.getClass());

        }
    }

    private void checkAndSimplifyAssignment(ProgramStack upperProgramStack, Assignment assignment, int level, boolean itIsNotLoop) throws Exception {
        checkAndSimplifyExpression(upperProgramStack, assignment.getExpression(), itIsNotLoop);
        upperProgramStack.replaceASTNode(assignment, level);
    }

    private void checkAndSimplifyIfStatement(ProgramStack upperProgramStack, IfStatement ifStatement, int level, boolean itIsNotLoop) throws Exception {
        checkAndSimplifyExpression(upperProgramStack, ifStatement.getExpression(), itIsNotLoop);
        checkAndSimplifyBody(upperProgramStack.copy(), ifStatement.getBodyThen(), level + 1, itIsNotLoop);
        if (ifStatement.getBodyElse() != null)
            checkAndSimplifyBody(upperProgramStack.copy(), ifStatement.getBodyElse(), level + 1, itIsNotLoop);
    }

    private void checkAndSimplifyForLoop(ProgramStack upperProgramStack, ForLoop forLoop, int level, boolean itIsNotLoop) throws Exception {
        upperProgramStack.checkAstNodeExistence(forLoop.getIdent(), "variable", true, level);
        checkAndSimplifyRange(upperProgramStack, forLoop.getRange(), itIsNotLoop);
        checkAndSimplifyBody(upperProgramStack.copy(), forLoop.getBody(), level + 1, false);
    }

    private void checkAndSimplifyRange(ProgramStack upperProgramStack, Range range, boolean itIsNotLoop) throws Exception {
        checkAndSimplifyExpression(upperProgramStack, range.getStartExp(), itIsNotLoop);
        checkAndSimplifyExpression(upperProgramStack, range.getEndExp(), itIsNotLoop);
    }

    private void checkAndSimplifyWhileLoop(ProgramStack upperProgramStack, WhileLoop whileLoop, int level, boolean itIsNotLoop) throws Exception {
        checkAndSimplifyExpression(upperProgramStack, whileLoop.getExpression(), itIsNotLoop);
        checkAndSimplifyBody(upperProgramStack.copy(), whileLoop.getBody(), level + 1, false);
    }


    private void checkTypeDeclaration(ProgramStack upperProgramStack, TypeDeclaration typeDeclaration, Boolean isGlobalScope, int level, boolean itIsNotLoop) throws Exception {
        upperProgramStack.checkAstNodeExistence(typeDeclaration.getIdent(), "variable", true, level);
        if (typeDeclaration.getType() != null && typeDeclaration.getType().getType().getClass().equals(UserType.class))
            upperProgramStack.checkAstNodeExistence(typeDeclaration.getIdent(), "variable", true, level);

    }


    // проверка что в существует точка вхождения в программу -> функция, более того там 0 аргументов (?)
    private void checkEntryPoint() throws Exception {
        try {
            programStack.checkAstNodeExistence(new RoutineCall(new ASTIdentifier(entryPoint, "0"), List.of(new Expression(), new Expression())), "function", true, 0);
        } catch (Exception ex) {
            throw new Exception("can't find entry point ");
        }
    }

    public void showAbstractSyntaxTree() {
        System.out.println("some tree will here :) ");
    }

}
