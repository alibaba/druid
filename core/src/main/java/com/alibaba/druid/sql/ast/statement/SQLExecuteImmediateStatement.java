package com.alibaba.druid.sql.ast.statement;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLStatementImpl;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public abstract class SQLExecuteImmediateStatement extends SQLStatementImpl {
    protected SQLExpr dynamicSql;

    protected final List<SQLExpr> into = new ArrayList<>();

    public SQLExpr getDynamicSql() {
        return dynamicSql;
    }

    public void setDynamicSql(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.dynamicSql = x;
    }

    public List<SQLExpr> getInto() {
        return into;
    }

    protected void acceptChild(SQLASTVisitor v) {
        acceptChild(v, dynamicSql);
        acceptChild(v, into);
    }
}
