package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLSelect extends SQLObjectImpl {
    private static final long serialVersionUID = 1L;

    protected SQLSelectQuery query;
    protected SQLOrderBy orderBy;

    public SQLSelect() {

    }

    public SQLSelectQuery getQuery() {
        return this.query;
    }

    public void setQuery(SQLSelectQuery query) {
        this.query = query;
    }

    public SQLOrderBy getOrderBy() {
        return this.orderBy;
    }

    public void setOrderBy(SQLOrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public void output(StringBuffer buf) {
        this.query.output(buf);
        buf.append(" ");

        if (this.orderBy != null) this.orderBy.output(buf);
    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.query);
            acceptChild(visitor, this.orderBy);
        }

        visitor.endVisit(this);
    }

}
