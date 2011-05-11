package com.alibaba.druid.sql.dialect.oracle.ast.expr;

import com.alibaba.druid.sql.ast.expr.SQLLiteralExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class OracleIntervalExpr extends SQLLiteralExpr {
    private String value;
    private OracleIntervalType type;
    private Integer precision;
    private Integer factionalSecondsPrecision;
    private OracleIntervalType toType;
    private Integer toFactionalSecondsPrecision;

    public OracleIntervalExpr() {

    }

    public String getValue() {
        return this.value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public OracleIntervalType getType() {
        return this.type;
    }

    public void setType(OracleIntervalType type) {
        this.type = type;
    }

    public Integer getPrecision() {
        return this.precision;
    }

    public void setPrecision(Integer precision) {
        this.precision = precision;
    }

    public Integer getFactionalSecondsPrecision() {
        return this.factionalSecondsPrecision;
    }

    public void setFactionalSecondsPrecision(Integer factionalSecondsPrecision) {
        this.factionalSecondsPrecision = factionalSecondsPrecision;
    }

    public OracleIntervalType getToType() {
        return this.toType;
    }

    public void setToType(OracleIntervalType toType) {
        this.toType = toType;
    }

    public Integer getToFactionalSecondsPrecision() {
        return this.toFactionalSecondsPrecision;
    }

    public void setToFactionalSecondsPrecision(Integer toFactionalSecondsPrecision) {
        this.toFactionalSecondsPrecision = toFactionalSecondsPrecision;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected void accept0(OracleASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
}
