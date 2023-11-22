package com.example.kek.syntax.analyzer;

import com.example.kek.syntax.analyzer.AST.*;

import java.util.Objects;

public class SemanticAnalyzer {
    private AbstractSyntaxTree abstractSyntaxTree;
    private ProgramStack programStack;
    private final String entryPoint;

    public SemanticAnalyzer(AbstractSyntaxTree abstractSyntaxTree, String entryPoint) {
        this.entryPoint = entryPoint;
        this.abstractSyntaxTree = abstractSyntaxTree;
    }

    public void makeSemanticAnalysis() throws Exception {
        checkFieldOfView();
        simplifyExpressions();
        checkEntryPoint();
    }

    private void checkFieldOfView() throws Exception {
        // adding all global vars/types/records in stack (add scope)
        addAllGlobalToStack();

        if (abstractSyntaxTree.getProgram() != null && abstractSyntaxTree.getProgram().getAllMain() != null)
            for (ASTNode node : abstractSyntaxTree.getProgram().getAllMain()) {
                if (node.getClass().equals(VariableDeclaration.class)) {
                    checkVariableDeclaration(this.programStack.copy(), node, true);
                } else if (node.getClass().equals(RoutineDeclaration.class)) {
                    checkRoutineDeclaration(this.programStack.copy(), node, true);
                } else if (node.getClass().equals(TypeDeclaration.class)) {
                    checkTypeDeclaration(this.programStack.copy(), node, true);
                } else
                    throw new Exception("Error: illegal declaration in program (SemanticAnalyzer.checkFieldOfView) \n" +
                            ", expected 'some' node, but  required: " + node.getClass());
            }

    }

    private void addAllGlobalToStack() throws Exception {
        if (abstractSyntaxTree.getProgram() != null && abstractSyntaxTree.getProgram().getAllMain() != null)
            for (ASTNode node : abstractSyntaxTree.getProgram().getAllMain()) {
                programStack.addASTNode(node);
            }
    }

    private void checkVariableDeclaration(ProgramStack upperProgramStack, ASTNode astNode, Boolean isGlobalScope) throws Exception {
        if (((VariableDeclaration) astNode).getInitExp() != null) {
            if (((VariableDeclaration) astNode).getInitExp().getClass().equals(Expression.class)) {
                checkExpression(upperProgramStack, ((VariableDeclaration) astNode).getInitExp());
                simplifyExpression(upperProgramStack, ((VariableDeclaration) astNode).getInitExp());
            }
        }

        if (((VariableDeclaration) astNode).getType() != null && ((VariableDeclaration) astNode).getType().getType() != null) {
            if (((VariableDeclaration) astNode).getType().getType().getClass().equals(UserType.class)) {
                checkUserType(upperProgramStack.copy(), (UserType) ((VariableDeclaration) astNode).getType().getType());
            }
            if (((VariableDeclaration) astNode).getType().getType().getClass().equals(ArrayType.class)) {
                checkArrayType(upperProgramStack.copy(), (ArrayType) ((VariableDeclaration) astNode).getType().getType());
            }
        }

        if (((VariableDeclaration) astNode).getInitExp() != null &&
                ((VariableDeclaration) astNode).getType() != null && ((VariableDeclaration) astNode).getType().getType() != null) {
            if (((VariableDeclaration) astNode).getInitExp().isTypeKnown() &&
                    !((VariableDeclaration) astNode).getType().getType().getClass().
                            equals(((VariableDeclaration) astNode).getInitExp().getType().getClass()))
                throw new Exception("Error: illegal type in program (SemanticAnalyzer.checkVariableDeclaration) \n" +
                        ", expected " + ((VariableDeclaration) astNode).getType().getType().getClass() + "  , but  receive: "
                        + ((VariableDeclaration) astNode).getInitExp().getType().getClass());
        }
        if (!isGlobalScope)
            upperProgramStack.addASTNode(astNode);
    }

    private void checkUserType(ProgramStack upperProgramStack, UserType userType) {

    }

    private void checkArrayType(ProgramStack upperProgramStack, ArrayType arrayType) {

    }

    private void checkExpression(ProgramStack upperProgramStack, Expression expression) throws Exception {
        if (!(expression.getRelations().size() == expression.getLogicOperators().size() + 1))
            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkExpression) \n" +
                    ", expected relations == logicOperators + 1 ");
        for (int i = 0; i < expression.getRelations().size(); ++i)
            checkRelation(upperProgramStack, expression.getRelations().get(i));
    }

    private void checkRelation(ProgramStack upperProgramStack, Relation relation) throws Exception {
        if (!(relation.getSimples().size() == relation.getSignOperators().size() + 1))
            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkRelation) \n" +
                    ", expected factors == signOperators + 1 ");
        for (int i = 0; i < relation.getSimples().size(); ++i)
            checkAndSimplifySimple(upperProgramStack, relation.getSimples().get(i));
    }

    private void checkAndSimplifySimple(ProgramStack upperProgramStack, Simple simple) throws Exception {
        if (!(simple.getFactors().size() == simple.getSignOperators().size() + 1))
            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkSimple) \n" +
                    ", expected simples == signOperators + 1 ");
        for (int i = 0; i < simple.getFactors().size(); ++i)
            checkAndSimplifyFactor(upperProgramStack, simple.getFactors().get(i));
// TODO сделать это
//        for (int i = 0; i < simple.getFactors().size(); ++i)
//            if (i + 1 < simple.getSummands().size())
//                if (factor.getSummands().get(i).isSimple() && factor.getSummands().get(i + 1).isSimple()) {
//                    if (Objects.equals(factor.getSummands().get(i).getType(), "boolean") || factor.getSummands().get(i + 1).getType().equals("boolean")) {
//                        throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyFactor) \n" +
//                                ", expected Summand's type not 'boolean'");
//                    }
//                    if ((factor.getSummands().get(i + 1).getIntLiteral() == 0 || factor.getSummands().get(i + 1).getRealLiteral() == 0.0) &&
//                            (factor.getSignOperators().get(i).getCode().equals("/") || factor.getSignOperators().get(i).getCode().equals("%")))
//                        throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyFactor) \n" +
//                                ", division by zero");
//                    if ((Objects.equals(factor.getSummands().get(i).getType(), "real") || factor.getSummands().get(i + 1).getType().equals("real")) &&
//                            Objects.equals(factor.getSignOperators().get(i).getCode(), "%"))
//                        throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkAndSimplifyFactor) \n" +
//                                ", invalid operation '%' for 'real'");
//                    if (Objects.equals(factor.getSummands().get(i).getType(), "real") || factor.getSummands().get(i + 1).getType().equals("real")) {
//                        double literal = 0.0;
//                        if (Objects.equals(factor.getSignOperators().get(i).getCode(), "/")) {
//                            if (Objects.equals(factor.getSummands().get(i).getType(), "real") && factor.getSummands().get(i + 1).getType().equals("real"))
//                                literal = factor.getSummands().get(i).getRealLiteral() / factor.getSummands().get(i + 1).getRealLiteral();
//                            else if (Objects.equals(factor.getSummands().get(i).getType(), "real") && factor.getSummands().get(i + 1).getType().equals("integer"))
//                                literal = factor.getSummands().get(i).getRealLiteral() / factor.getSummands().get(i + 1).getIntLiteral();
//                            else
//                                literal = factor.getSummands().get(i).getIntLiteral() / factor.getSummands().get(i + 1).getRealLiteral();
//                        } else if (Objects.equals(factor.getSignOperators().get(i).getCode(), "*")) {
//                            if (Objects.equals(factor.getSummands().get(i).getType(), "real") && factor.getSummands().get(i + 1).getType().equals("real"))
//                                literal = factor.getSummands().get(i).getRealLiteral() * factor.getSummands().get(i + 1).getRealLiteral();
//                            else if (Objects.equals(factor.getSummands().get(i).getType(), "real") && factor.getSummands().get(i + 1).getType().equals("integer"))
//                                literal = factor.getSummands().get(i).getRealLiteral() * factor.getSummands().get(i + 1).getIntLiteral();
//                            else
//                                literal = factor.getSummands().get(i).getIntLiteral() * factor.getSummands().get(i + 1).getRealLiteral();
//                        }
//                        factor.getSummands().remove(i + 1);
//                        factor.getSignOperators().remove(i);
//                        factor.getSummands().remove(i);
//                        factor.getSummands().add(i, new Summand(literal));
//                    } else {
//                        int literal = 0;
//                        if (Objects.equals(factor.getSignOperators().get(i).getCode(), "/")) {
//                            literal = factor.getSummands().get(i).getIntLiteral() / factor.getSummands().get(i + 1).getIntLiteral();
//                        } else if (Objects.equals(factor.getSignOperators().get(i).getCode(), "*")) {
//                            literal = factor.getSummands().get(i).getIntLiteral() * factor.getSummands().get(i + 1).getIntLiteral();
//                        } else {
//                            literal = factor.getSummands().get(i).getIntLiteral() % factor.getSummands().get(i + 1).getIntLiteral();
//                        }
//                        factor.getSummands().remove(i + 1);
//                        factor.getSignOperators().remove(i);
//                        factor.getSummands().remove(i);
//                        factor.getSummands().add(i, new Summand(literal));
//                    }
//                    --i;
//                }
    }

    private void checkAndSimplifyFactor(ProgramStack upperProgramStack, Factor factor) throws Exception {
        if (!(factor.getSummands().size() == factor.getSignOperators().size() + 1))
            throw new Exception("Error: error in semantic of program (SemanticAnalyzer.checkFactor) \n" +
                    ", expected factors == signOperators + 1 ");
        for (int i = 0; i < factor.getSummands().size(); ++i)
            checkAndSimplifySummand(upperProgramStack, factor.getSummands().get(i));

        for (int i = 0; i < factor.getSummands().size(); ++i)
            if (i + 1 < factor.getSummands().size())
                if (factor.getSummands().get(i).isSimple() && factor.getSummands().get(i + 1).isSimple()) {
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
                    if (Objects.equals(factor.getSummands().get(i).getType(), "real") || factor.getSummands().get(i + 1).getType().equals("real")) {
                        double literal = 0.0;
                        if (Objects.equals(factor.getSignOperators().get(i).getCode(), "/")) {
                            if (Objects.equals(factor.getSummands().get(i).getType(), "real") && factor.getSummands().get(i + 1).getType().equals("real"))
                                literal = factor.getSummands().get(i).getRealLiteral() / factor.getSummands().get(i + 1).getRealLiteral();
                            else if (Objects.equals(factor.getSummands().get(i).getType(), "real") && factor.getSummands().get(i + 1).getType().equals("integer"))
                                literal = factor.getSummands().get(i).getRealLiteral() / factor.getSummands().get(i + 1).getIntLiteral();
                            else
                                literal = factor.getSummands().get(i).getIntLiteral() / factor.getSummands().get(i + 1).getRealLiteral();
                        } else if (Objects.equals(factor.getSignOperators().get(i).getCode(), "*")) {
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
                    } else {
                        int literal = 0;
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

    private void checkAndSimplifySummand(ProgramStack upperProgramStack, Summand summand) throws Exception {
        switch (summand.getType()) {
            case "modifiablePrimary":
                upperProgramStack.checkTableElement("variable", summand);
                if (upperProgramStack.getTableElementInitial("variable", summand).isSimple()) {
                    summand = upperProgramStack.getTableElementInitial("variable", summand).getSimple();
                }
                break;
            case "routineCall":
                upperProgramStack.checkTableElement("function", summand);
                ;
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

    private void simplifyExpression(ProgramStack upperProgramStack, Expression expression) {

    }


    private void checkRoutineDeclaration(ProgramStack upperProgramStack, ASTNode astNode, Boolean isGlobalScope) throws Exception {
        if (astNode.getClass().equals(VariableDeclaration.class)) {
            upperProgramStack.addASTNode(astNode);
        } else if (astNode.getClass().equals(RoutineDeclaration.class)) {
            upperProgramStack.addASTNode(astNode);
        } else if (astNode.getClass().equals(TypeDeclaration.class)) {
            upperProgramStack.addASTNode(astNode);
        } else throw new Exception("Error: illegal declaration in program (SemanticAnalyzer.checkLayers) \n" +
                ", expected 'some' node, but  required: " + astNode.getClass());
    }

    private void checkTypeDeclaration(ProgramStack upperProgramStack, ASTNode astNode, Boolean isGlobalScope) throws Exception {
        if (astNode.getClass().equals(VariableDeclaration.class)) {
            upperProgramStack.addASTNode(astNode);
        } else if (astNode.getClass().equals(RoutineDeclaration.class)) {
            upperProgramStack.addASTNode(astNode);
        } else if (astNode.getClass().equals(TypeDeclaration.class)) {
            upperProgramStack.addASTNode(astNode);
        } else throw new Exception("Error: illegal declaration in program (SemanticAnalyzer.checkLayers) \n" +
                ", expected 'some' node, but  required: " + astNode.getClass());
    }

    private void simplifyExpressions() {
    }

    private void checkEntryPoint() {

    }

    public void showAbstractSyntaxTree() {
        System.out.println("some tree will here :) ");
    }

}
