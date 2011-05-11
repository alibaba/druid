package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleUpdateSetListSingleColumnItem extends OracleUpdateSetListItem {
    private static final long serialVersionUID = 1L;

    private SQLExpr column;
    private SQLExpr value;

    public OracleUpdateSetListSingleColumnItem() {

    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.column);
        }

        visitor.endVisit(this);
    }

    public SQLExpr getColumn() {
        return this.column;
    }

    public void setColumn(SQLExpr column) {
        this.column = column;
    }

    public SQLExpr getValue() {
        return this.value;
    }

    public void setValue(SQLExpr value) {
        this.value = value;
    }
}
