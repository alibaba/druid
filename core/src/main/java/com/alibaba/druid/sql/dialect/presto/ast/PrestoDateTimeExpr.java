package com.alibaba.druid.sql.dialect.presto.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDatetimeExpr;
import com.alibaba.druid.sql.dialect.presto.visitor.PrestoASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class PrestoDateTimeExpr extends OracleDatetimeExpr implements PrestoObject {
    public PrestoDateTimeExpr(SQLExpr expr, SQLExpr timeZone) {
        super(expr, timeZone);
    }

    @Override
    public void accept0(PrestoASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getExpr());
            acceptChild(visitor, getTimeZone());
        }
        visitor.endVisit(this);
    }

    @Override
    public void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof PrestoASTVisitor) {
            accept0((PrestoASTVisitor) visitor);
        }
    }
}
