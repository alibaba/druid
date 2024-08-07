package com.alibaba.druid.sql.dialect.impala.ast;

import com.alibaba.druid.sql.ast.SQLPartitionValue;

public class ImpalaSQLPartitionValue extends SQLPartitionValue {
    private Integer leftBound;
    private Integer rightBound;
    private Operator leftOperator;
    private Operator rightOperator;

    public void setOperator(Operator operator) {
        this.operator = operator;
    }

    public Integer getLeftBound() {
        return leftBound;
    }

    public void setLeftBound(Integer leftBound) {
        this.leftBound = leftBound;
    }

    public Integer getRightBound() {
        return rightBound;
    }

    public void setRightBound(Integer rightBound) {
        this.rightBound = rightBound;
    }

    public String constructPartitionName() {
        StringBuilder sb = new StringBuilder();
        sb.append("partition_").append(leftBound != null ? leftBound.toString() : "")
                .append("_").append(rightBound != null ? rightBound.toString() : "");
        return sb.toString();
    }

    public Operator getLeftOperator() {
        return leftOperator;
    }

    public void setLeftOperator(Operator leftOperator) {
        this.leftOperator = leftOperator;
    }

    public Operator getRightOperator() {
        return rightOperator;
    }

    public void setRightOperator(Operator rightOperator) {
        this.rightOperator = rightOperator;
    }
}
