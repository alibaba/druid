package com.alibaba.druid.sql.dialect.bigquery.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLAliasedExpr;
import com.alibaba.druid.sql.ast.statement.SQLExecuteImmediateStatement;
import com.alibaba.druid.sql.dialect.bigquery.visitor.BigQueryVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class BigQueryExecuteImmediateStatement
        extends SQLExecuteImmediateStatement {
    private final List<SQLAliasedExpr> using = new ArrayList<>();

    public List<SQLAliasedExpr> getUsing() {
        return using;
    }

    public void addUsing(SQLExpr x) {
        x.setParent(this);
        using.add(new SQLAliasedExpr(x));
    }

    public void addUsing(SQLExpr x, String alias) {
        x.setParent(this);
        using.add(new SQLAliasedExpr(x, alias));
    }

    @Override
    public void accept0(SQLASTVisitor v) {
        if (v instanceof BigQueryVisitor) {
            accept0((BigQueryVisitor) v);
        } else {
            super.accept0(v);
        }
    }

    public void accept0(BigQueryVisitor v) {
        if (v.visit(this)) {
            acceptChild(v);
        }
    }

    protected void acceptChild(SQLASTVisitor v) {
        super.acceptChild(v);
        acceptChild(v, using);
    }
}
