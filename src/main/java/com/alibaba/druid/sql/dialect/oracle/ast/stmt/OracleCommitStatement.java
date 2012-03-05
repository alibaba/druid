package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleCommitStatement extends OracleStatementImpl {

    private static final long serialVersionUID = 1L;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            
        }
        visitor.endVisit(this);
    }

}
