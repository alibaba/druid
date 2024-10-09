package com.alibaba.druid.sql.dialect.bigquery.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.bigquery.visitor.BigQueryVisitor;
import com.alibaba.druid.sql.dialect.oracle.ast.expr.OracleDatetimeExpr;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class BigQueryDateTimeExpr extends OracleDatetimeExpr implements BigQueryObject {
    public BigQueryDateTimeExpr(SQLExpr expr, SQLExpr timeZone) {
        super(expr, timeZone);
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        if (v instanceof BigQueryVisitor) {
            accept0((BigQueryVisitor) v);
        }
    }

    @Override
    public void accept0(BigQueryVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getExpr());
            acceptChild(visitor, getTimeZone());
        }
        visitor.endVisit(this);
    }
}
