package com.alibaba.druid.sql.ast.expr;

import java.io.Serializable;

import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.statement.SQLSelect;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLExistsExpr extends SQLExprImpl implements Serializable {
    private static final long serialVersionUID = 1L;
    public boolean not = false;
    public SQLSelect subQuery;

    public SQLExistsExpr() {

    }

    public SQLExistsExpr(SQLSelect subQuery) {

        this.subQuery = subQuery;
    }

    public SQLExistsExpr(SQLSelect subQuery, boolean not) {

        this.subQuery = subQuery;
        this.not = not;
    }

    public boolean isNot() {
        return this.not;
    }

    public void setNot(boolean not) {
        this.not = not;
    }

    public SQLSelect getSubQuery() {
        return this.subQuery;
    }

    public void setSubQuery(SQLSelect subQuery) {
        this.subQuery = subQuery;
    }

    public void output(StringBuffer buf) {
        if (this.not) {
            buf.append("NOT ");
        }
        buf.append("EXISTS (");
        this.subQuery.output(buf);
        buf.append(")");
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.subQuery);
        }

        visitor.endVisit(this);
    }
}
