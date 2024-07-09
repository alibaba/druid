package com.alibaba.druid.sql.dialect.bigquery.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.ast.expr.SQLCharExpr;
import com.alibaba.druid.sql.dialect.bigquery.visitor.BigQueryVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class BigQueryAssertStatement extends SQLStatementImpl implements BigQueryObject{
    private SQLExpr expr;
    private SQLCharExpr as;

    public SQLExpr getExpr() {
        return expr;
    }

    public void setExpr(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.expr = x;
    }

    public SQLCharExpr getAs() {
        return as;
    }

    public void setAs(SQLCharExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.as = x;
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        if (v instanceof BigQueryVisitor) {
            accept0((BigQueryVisitor) v);
        }
    }

    @Override
    public void accept0(BigQueryVisitor v) {
        if (v.visit(this)) {
            acceptChild(v, expr);
            acceptChild(v, as);
        }
        v.endVisit(this);
    }
}
