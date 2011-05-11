package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OraclePLSQLCommitStatement extends SQLStatementImpl {
    private static final long serialVersionUID = 1L;

    public OraclePLSQLCommitStatement() {

    }

    protected void accept0(OracleASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }
}
