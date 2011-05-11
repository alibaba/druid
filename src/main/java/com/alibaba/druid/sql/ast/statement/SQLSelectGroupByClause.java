package com.alibaba.druid.sql.ast.statement;

import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLObjectImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class SQLSelectGroupByClause extends SQLObjectImpl {
    private static final long serialVersionUID = 1L;

    private final List<SQLExpr> items = new ArrayList<SQLExpr>();
    private SQLExpr having;

    public SQLSelectGroupByClause() {

    }

    protected void accept0(SQLASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, this.items);
            acceptChild(visitor, this.having);
        }

        visitor.endVisit(this);
    }

    public SQLExpr getHaving() {
        return this.having;
    }

    public void setHaving(SQLExpr having) {
        this.having = having;
    }

    public List<SQLExpr> getItems() {
        return this.items;
    }
}
