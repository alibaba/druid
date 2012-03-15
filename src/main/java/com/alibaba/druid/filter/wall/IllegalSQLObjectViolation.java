package com.alibaba.druid.filter.wall;

public class IllegalSQLObjectViolation implements Violation {

    private String condition;

    public IllegalSQLObjectViolation(){

    }

    public IllegalSQLObjectViolation(String condition){
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

}
