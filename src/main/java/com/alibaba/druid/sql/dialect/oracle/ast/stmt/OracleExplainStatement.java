package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.dialect.oracle.visitor.OracleASTVisitor;

public class OracleExplainStatement extends OracleStatementImpl {

    private static final long serialVersionUID = 1L;

    private SQLCharExpr            statementId;
    private SQLExpr           into;
    private SQLStatement      forStatement;

    @Override
    public void accept0(OracleASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, statementId);
            acceptChild(visitor, into);
            acceptChild(visitor, forStatement);
        }
        visitor.endVisit(this);
    }

    public SQLCharExpr getStatementId() {
        return statementId;
    }

    public void setStatementId(SQLCharExpr statementId) {
        this.statementId = statementId;
    }

    public SQLExpr getInto() {
        return into;
    }

    public void setInto(SQLExpr into) {
        this.into = into;
    }

    public SQLStatement getForStatement() {
        return forStatement;
    }

    public void setForStatement(SQLStatement forStatement) {
        this.forStatement = forStatement;
    }

}
