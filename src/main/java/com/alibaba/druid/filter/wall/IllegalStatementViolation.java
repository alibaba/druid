package com.alibaba.druid.filter.wall;

public class IllegalStatementViolation implements Violation {

    private String condition;

    public IllegalStatementViolation(){

    }

    public IllegalStatementViolation(String condition){
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

}
