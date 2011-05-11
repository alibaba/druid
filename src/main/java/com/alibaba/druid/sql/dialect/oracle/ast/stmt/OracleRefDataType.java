package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLDataTypeImpl;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleRefDataType extends SQLDataTypeImpl {
    private static final long serialVersionUID = 1L;

    private SQLExpr refObject;

    public OracleRefDataType() {

    }

    public SQLExpr getRefObject() {
        return this.refObject;
    }

    public void setRefObject(SQLExpr refObject) {
        this.refObject = refObject;
    }

    protected void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.refObject);
        }

        visitor.endVisit(this);
    }
}
