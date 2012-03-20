package com.alibaba.druid.wall.violation;

import com.alibaba.druid.wall.Violation;

public class SyntaxErrorViolation implements Violation {

    private final Exception exception;

    private final String    sql;

    public SyntaxErrorViolation(Exception exception, String sql){
        super();
        this.exception = exception;
        this.sql = sql;
    }

    public String toString() {
        return this.sql;
    }

    public Exception getException() {
        return exception;
    }

    public String getSql() {
        return sql;
    }

}
