package com.alibaba.druid.sql.dialect.postgresql.ast.expr;

import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class PGCharExpr extends SQLCharExpr implements PGExpr {
    private boolean cSytle;
    @Override
    public void accept0(PGASTVisitor visitor) {
        visitor.visit(this);
        visitor.endVisit(this);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PGASTVisitor) {
            accept0((PGASTVisitor) visitor);
        } else {
            visitor.visit(this);
            visitor.endVisit(this);
        }
    }

    public boolean isCSytle() {
        return cSytle;
    }

    public void setCSytle(boolean cSytle) {
        this.cSytle = cSytle;
    }
}
