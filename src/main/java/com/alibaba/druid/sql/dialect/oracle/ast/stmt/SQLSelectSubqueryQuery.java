package com.alibaba.druid.sql.dialect.oracle.ast.stmt;

import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLSelectSubqueryQuery extends SQLSelectQuery {
    private static final long serialVersionUID = 1L;

    public SQLSelectSubqueryQuery() {

    }

    protected void accept0(SQLASTVisitor visitor) {
        visitor.visit(this);

        visitor.endVisit(this);
    }
}
