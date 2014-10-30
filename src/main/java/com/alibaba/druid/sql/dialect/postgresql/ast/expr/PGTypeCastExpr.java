package com.alibaba.druid.sql.dialect.postgresql.ast.expr;

import com.alibaba.druid.sql.ast.expr.SQLCastExpr;
import com.alibaba.druid.sql.dialect.postgresql.visitor.PGASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class PGTypeCastExpr extends SQLCastExpr implements PGExpr {

    @Override
    public void accept0(PGASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.expr);
            acceptChild(visitor, this.dataType);
        }
        visitor.endVisit(this);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        accept0((PGASTVisitor) visitor);
    }

}
