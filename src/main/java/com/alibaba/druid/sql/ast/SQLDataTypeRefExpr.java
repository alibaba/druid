package com.alibaba.druid.sql.ast;

import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLDataTypeRefExpr extends SQLExprImpl {
    private SQLDataType dataType;

    public SQLDataTypeRefExpr(SQLDataType dataType) {
        this.dataType = dataType;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SQLDataTypeRefExpr that = (SQLDataTypeRefExpr) o;

        return dataType != null ? dataType.equals(that.dataType) : that.dataType == null;
    }

    @Override
    public int hashCode() {
        return dataType != null ? dataType.hashCode() : 0;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, dataType);
        }
        v.endVisit(this);
    }

    @Override
    public SQLExpr clone() {
        return new SQLDataTypeRefExpr(
                dataType == null
                        ? null
                        : dataType.clone());
    }
}
