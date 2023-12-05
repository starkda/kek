package com.example.kek.codegenerator.strategy;

import com.example.kek.codegenerator.Value;
import com.example.kek.semantic.analyzer.AST.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

public class BodyStrategy extends GenerationStrategy{
    Type retType;

    boolean isReturn;

    public BodyStrategy(ASTNode nodeContext, Map<String, Value> variableContext, TreeSet<Integer> freeVariables, Type retType, boolean isReturn){
        this.nodeContext = nodeContext;
        this.variableContext = variableContext;
        this.freeVariables = freeVariables;
        this.retType = retType;
        this.isReturn = isReturn;
    }
    @Override
    public List<ASTNode> getChildren(ASTNode parent) {
        return null;
    }

    @Override
    public void before() throws IOException {
        List<ASTNode> bodyChildren = ((Body) nodeContext).getDeclarationsAndStatements();
        int ok = 0;
        for(ASTNode child : bodyChildren){
            if (child.getClass().equals(VariableDeclaration.class)){
                VariableDeclaration variableDeclaration = (VariableDeclaration) child;
                VariableDeclarationStrategy variableDeclarationStrategy = new VariableDeclarationStrategy(variableDeclaration, variableContext, freeVariables);
                variableDeclarationStrategy.before();
            }

            else if (child.getClass().equals(Statement.class)) {
                    ASTNode temp = ((Statement) child).getStatement();
                    if (temp.getClass().equals(ReturnCall.class)) {
                        ok = 1;
                        ReturnCall returnCall = (ReturnCall) temp;
                        ReturnStrategy returnStrategy = new ReturnStrategy(returnCall, variableContext, freeVariables, retType);
                        returnStrategy.before();
                    }
                    if (temp.getClass().equals(Assignment.class)) {
                        Assignment assignment = (Assignment) temp;
                        AssignmentStrategy assignmentStrategy = new AssignmentStrategy(assignment, variableContext, freeVariables);
                        assignmentStrategy.before();
                    }
                    if (temp.getClass().equals(IfStatement.class)){
                        IfStatement ifStatement = (IfStatement) temp;
                        IfStatementStrategy ifStatementStrategy = new IfStatementStrategy(ifStatement, variableContext, freeVariables, retType);
                        ifStatementStrategy.before();
                    }
                    if (temp.getClass().equals(WhileLoop.class)) {
                        WhileLoop whileLoop = (WhileLoop) temp;
                        WhileLoopStrategy whileLoopStrategy = new WhileLoopStrategy(whileLoop, variableContext, freeVariables, retType);
                        whileLoopStrategy.before();
                    }
                    if (temp.getClass().equals(ForLoop.class)) {
                        ForLoop forLoop = (ForLoop) temp;
                        ForLoopStrategy forLoopStrategy = new ForLoopStrategy(forLoop, variableContext, freeVariables, retType);
                        forLoopStrategy.before();
                    }
            }

        }
        if (ok == 0 && isReturn && retType == null){
            appendTabbed("return");
        }
    }
}
