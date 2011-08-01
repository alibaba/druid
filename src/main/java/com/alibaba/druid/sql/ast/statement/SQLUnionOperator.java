package com.alibaba.druid.sql.ast.statement;

public enum SQLUnionOperator {
    UNION("UNION"), UNION_ALL("UNION ALL"), MINUS("MINUS"), INTERSECT("INTERSECT");

    public final String name;

    private SQLUnionOperator(String name){
        this.name = name;
    }

    public String toString() {
        return name;
    }
}
