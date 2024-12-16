package com.alibaba.druid.sql.dialect.bigquery.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLExprImpl;
import com.alibaba.druid.sql.ast.SQLName;
import com.alibaba.druid.sql.dialect.bigquery.visitor.BigQueryVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

public class BigQueryTableExpr extends SQLExprImpl implements BigQueryObject {
    private SQLName name;

    public SQLName getName() {
        return name;
    }

    public void setName(SQLName x) {
        if (x != null) {
            x.setParent(this);
        }
        this.name = x;
    }

    @Override
    public boolean equals(Object o) {
        return false;
    }

    @Override
    public int hashCode() {
        return 0;
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
            acceptChild(v);
        }
        v.endVisit(this);
    }

    @Override
    public SQLExpr clone() {
        return null;
    }

    public void acceptChild(SQLASTVisitor v) {
        acceptChild(v, name);
    }
}
