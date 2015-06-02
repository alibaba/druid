package com.alibaba.druid.sql.dialect.postgresql.ast.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class PGShowStatement extends SQLStatementImpl implements PGSQLStatement {

    private SQLExpr expr;

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr expr) {
        this.expr = expr;
    }

    protected void accept0(SQLASTVisitor visitor) {
        accept0((PGASTVisitor) visitor);
    }

    @Override
    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
        }
        visitor.endVisit(this);
    }
}
