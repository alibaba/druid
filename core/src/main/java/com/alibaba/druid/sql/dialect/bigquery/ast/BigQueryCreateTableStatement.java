package com.alibaba.druid.sql.dialect.bigquery.ast;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;

import java.util.ArrayList;
import java.util.List;

public class BigQueryCreateTableStatement
        extends SQLCreateTableStatement {
    protected final List<SQLExpr> partitionBy = new ArrayList<>();
    private SQLExpr collate;

    public List<SQLExpr> getPartitionBy() {
        return partitionBy;
    }

    public BigQueryCreateTableStatement clone() {
        BigQueryCreateTableStatement x = new BigQueryCreateTableStatement();
        cloneTo(x);
        return x;
    }

    protected void cloneTo(BigQueryCreateTableStatement x) {
        super.cloneTo(x);
        for (SQLExpr item : partitionBy) {
            SQLExpr cloned = item.clone();
            cloned.setParent(x);
            x.partitionBy.add(cloned);
        }
    }

    public SQLExpr getCollate() {
        return collate;
    }

    public void setCollate(SQLExpr collate) {
        this.collate = collate;
    }
}
