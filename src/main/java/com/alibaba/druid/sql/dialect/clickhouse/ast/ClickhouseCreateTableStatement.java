package com.alibaba.druid.sql.dialect.clickhouse.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.dialect.clickhouse.visitor.ClickhouseVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class ClickhouseCreateTableStatement extends SQLCreateTableStatement {
    protected final List<SQLAssignItem> settings    = new ArrayList<SQLAssignItem>();
    private SQLOrderBy orderBy;
    private SQLExpr partitionBy;
    private SQLExpr sampleBy;

    public ClickhouseCreateTableStatement() {
        super(DbType.clickhouse);
    }

    public SQLOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SQLOrderBy x) {
        if (x != null) {
            x.setParent(this);
        }

        this.orderBy = x;
    }

    public SQLExpr getPartitionBy() {
        return partitionBy;
    }

    public void setPartitionBy(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }

        this.partitionBy = x;
    }

    public SQLExpr getSampleBy() {
        return sampleBy;
    }

    public void setSampleBy(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }

        this.sampleBy = x;
    }

    public List<SQLAssignItem> getSettings() {
        return settings;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof ClickhouseVisitor) {
            ClickhouseVisitor vv = (ClickhouseVisitor) v;
            if (vv.visit(this)) {
                acceptChild(vv);
            }
            vv.endVisit(this);
            return;
        }

        if (v.visit(this)) {
            acceptChild(v);
        }
        v.endVisit(this);
    }
}
