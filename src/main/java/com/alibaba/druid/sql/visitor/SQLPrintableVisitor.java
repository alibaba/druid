package com.alibaba.druid.sql.visitor;

public interface SQLPrintableVisitor extends SQLASTVisitor {

    void print(char value);

    void print(String text);
}
