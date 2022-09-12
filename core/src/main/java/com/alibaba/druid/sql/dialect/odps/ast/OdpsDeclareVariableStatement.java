package com.alibaba.druid.sql.dialect.odps.ast;

import com.alibaba.druid.sql.ast.SQLDataType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.odps.visitor.OdpsASTVisitor;

public class OdpsDeclareVariableStatement extends OdpsStatementImpl {
    private String variant;
    private SQLDataType dataType;
    private SQLExpr initValue;

    public OdpsDeclareVariableStatement() {
    }

    public OdpsDeclareVariableStatement(String variant, SQLExpr initValue) {
        this.variant = variant;
        this.initValue = initValue;
    }

    @Override
    protected void accept0(OdpsASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, dataType);
            acceptChild(v, initValue);
        }
        v.endVisit(this);
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public SQLExpr getInitValue() {
        return initValue;
    }

    public void setInitValue(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.initValue = x;
    }

    public SQLDataType getDataType() {
        return dataType;
    }

    public void setDataType(SQLDataType x) {
        if (x != null) {
            x.setParent(this);
        }
        this.dataType = x;
    }
}
