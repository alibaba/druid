package com.alibaba.druid.sql.visitor;

public interface ParameterizedVisitor extends SQLASTVisitor {

    int getReplaceCount();

    void incrementReplaceCunt();

    void print(char value);

    void print(String text);
}
