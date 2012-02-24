package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public abstract class OracleStatementImpl extends SQLStatementImpl implements OracleStatement {

    private static final long serialVersionUID = 1L;

    protected void accept0(SQLASTVisitor visitor) {
        accept0((OracleASTVisitor) visitor);
    }

    public abstract void accept0(OracleASTVisitor visitor);
}
