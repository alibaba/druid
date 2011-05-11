package com.alibaba.druid.sql.ast;

public interface SQLSetQuantifier {
    // SQL 92
    public final static int ALL = 1;
    public final static int DISTINCT = 2;

    public final static int UNIQUE = 3;
    public final static int DISTINCTROW = 4;

    // <SetQuantifier>
}
