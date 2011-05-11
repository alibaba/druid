package com.alibaba.druid.sql.ast.expr;

public enum SQLUnaryOperator {
    Plus("+"),
    Negative("-"),
    Not("!"),
    Compl("~"),
    Prior("PRIOR"),
    ConnectByRoot("CONNECT BY");

    public final String name;

    SQLUnaryOperator(String name) {
        this.name = name;
    }
}
