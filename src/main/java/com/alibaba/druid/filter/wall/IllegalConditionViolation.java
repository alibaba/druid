package com.alibaba.druid.filter.wall;

public class IllegalConditionViolation implements Violation {

    private String condition;

    public IllegalConditionViolation(){

    }

    public IllegalConditionViolation(String condition){
        this.condition = condition;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

}
