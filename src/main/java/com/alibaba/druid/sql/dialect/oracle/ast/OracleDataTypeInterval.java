package com.alibaba.druid.sql.dialect.oracle.ast;

import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.expr.SQLIntegerExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleIntervalType;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleDataTypeInterval extends SQLDataTypeImpl {
    private static final long serialVersionUID = 1L;

    private OracleIntervalType type;
    private OracleIntervalType toType;
    private SQLIntegerExpr precision;
    private SQLIntegerExpr fractionalSecondsPrecision;

    public OracleDataTypeInterval() {

    }

    public OracleIntervalType getType() {
        return this.type;
    }

    public void setType(OracleIntervalType type) {
        this.type = type;
    }

    public OracleIntervalType getToType() {
        return this.toType;
    }

    public void setToType(OracleIntervalType toType) {
        this.toType = toType;
    }

    public SQLIntegerExpr getPrecision() {
        return this.precision;
    }

    public void setPrecision(SQLIntegerExpr precision) {
        this.precision = precision;
    }

    public SQLIntegerExpr getFractionalSecondsPrecision() {
        return this.fractionalSecondsPrecision;
    }

    public void setFractionalSecondsPrecision(SQLIntegerExpr fractionalSecondsPrecision) {
        this.fractionalSecondsPrecision = fractionalSecondsPrecision;
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.precision);
            acceptChild(visitor, this.fractionalSecondsPrecision);
        }

        visitor.endVisit(this);
    }
}
