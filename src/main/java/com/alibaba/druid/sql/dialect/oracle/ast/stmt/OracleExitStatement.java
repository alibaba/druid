package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleExitStatement extends OracleStatementImpl {

    private static final long serialVersionUID = 1L;
    private SQLExpr when;

    public SQLExpr getWhen() {
        return when;
    }

    public void setWhen(SQLExpr when) {
        this.when = when;
    }

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, when);
        }
        visitor.endVisit(this);
    }

}
