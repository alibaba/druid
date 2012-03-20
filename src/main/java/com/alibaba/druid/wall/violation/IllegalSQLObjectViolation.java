package com.alibaba.druid.wall.violation;

import com.alibaba.druid.wall.Violation;

public class IllegalSQLObjectViolation implements Violation {

    private String sqlPart;

    public IllegalSQLObjectViolation(){

    }

    public IllegalSQLObjectViolation(String condition){
        this.sqlPart = condition;
    }

    public String getSqlPart() {
        return sqlPart;
    }

    public void setSqlPart(String sqlPart) {
        this.sqlPart = sqlPart;
    }

    public String toString() {
        return this.sqlPart;
    }

}
