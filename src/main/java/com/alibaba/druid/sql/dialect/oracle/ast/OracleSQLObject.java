package com.alibaba.druid.sql.dialect.oracle.ast;

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public abstract class OracleSQLObject extends SQLObjectImpl {
    private static final long serialVersionUID = 1L;

    public OracleSQLObject() {

    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        this.accept0((OracleASTVisitor) visitor);
    }

    protected abstract void accept0(OracleASTVisitor visitor);
}
