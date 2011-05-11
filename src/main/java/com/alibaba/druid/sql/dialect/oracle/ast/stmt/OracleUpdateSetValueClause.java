package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.dialect.oracle.ast.visitor.OracleASTVisitor;

public class OracleUpdateSetValueClause extends OracleUpdateSetClause {
    private static final long serialVersionUID = 1L;

    public OracleUpdateSetValueClause() {

    }

    protected void accept0(OracleASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }
}
