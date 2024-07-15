package com.alibaba.druid.sql.dialect.bigquery.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.expr.SQLAliasedExpr;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBase;
import com.alibaba.druid.sql.ast.statement.SQLTableSource;
import com.alibaba.druid.sql.dialect.bigquery.visitor.BigQueryVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class BigQuerySelectAsStruct
        extends SQLSelectQueryBase
        implements SQLSelectQuery, BigQueryObject {
    private final List<SQLAliasedExpr> items = new ArrayList<>();
    private SQLTableSource from;

    public void addItem(SQLAliasedExpr item) {
        item.setParent(this);
        items.add(item);
    }

    public void addItem(SQLExpr item, String alias) {
        items.add(
                new SQLAliasedExpr(item, alias));
    }

    public List<SQLAliasedExpr> getItems() {
        return items;
    }

    public SQLTableSource getFrom() {
        return from;
    }

    public void setFrom(SQLTableSource x) {
        if (x != null) {
            x.setParent(this);
        }
        this.from = x;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || getClass() != object.getClass()) {
            return false;
        }

        BigQuerySelectAsStruct that = (BigQuerySelectAsStruct) object;
        return items.equals(that.items);
    }

    @Override
    public int hashCode() {
        return items.hashCode();
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
            acceptChild(v, items);
        }
        v.endVisit(this);
    }

    @Override
    public BigQuerySelectAsStruct clone() {
        BigQuerySelectAsStruct x = new BigQuerySelectAsStruct();
        cloneTo(x);
        return x;
    }

    protected void cloneTo(BigQuerySelectAsStruct x) {
        super.cloneTo(x);
        for (SQLAliasedExpr item : items) {
            x.addItem(item.clone());
        }
        if (from != null) {
            x.setFrom(from.clone());
        }
    }

    public String toString() {
        return SQLUtils.toSQLString(this, DbType.bigquery);
    }
}
