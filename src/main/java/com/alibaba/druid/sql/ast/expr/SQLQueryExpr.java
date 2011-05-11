package com.alibaba.druid.sql.ast.expr;

import java.io.Serializable;

import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLQueryExpr extends SQLExprImpl implements Serializable {
    private static final long serialVersionUID = 1L;
    public SQLSelect subQuery;

    public SQLQueryExpr() {

    }

    public SQLQueryExpr(SQLSelect select) {

        this.subQuery = select;
    }

    public SQLSelect getSubQuery() {
        return this.subQuery;
    }

    public void setSubQuery(SQLSelect subQuery) {
        this.subQuery = subQuery;
    }

    public void output(StringBuffer buf) {
        this.subQuery.output(buf);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.subQuery);
        }

        visitor.endVisit(this);
    }
}
