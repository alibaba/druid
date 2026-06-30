package com.alibaba.druid.sql.dialect.clickhouse.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLPrimaryKey;
import com.alibaba.druid.sql.dialect.clickhouse.visitor.CKASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class CKCreateTableStatement extends SQLCreateTableStatement {
    protected final List<SQLAssignItem> settings = new ArrayList<SQLAssignItem>();
    private SQLPrimaryKey primaryKey;
    private SQLExpr sampleBy;

    private SQLExpr ttl;
    private String onClusterName;

    public CKCreateTableStatement() {
        super(DbType.clickhouse);
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

    public SQLPrimaryKey getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(SQLPrimaryKey primaryKey) {
        if (primaryKey != null) {
            primaryKey.setParent(this);
        }
        this.primaryKey = primaryKey;
    }

    public SQLExpr getTtl() {
        return ttl;
    }

    public void setTtl(SQLExpr ttl) {
        if (ttl != null) {
            ttl.setParent(this);
        }
        this.ttl = ttl;
    }

    public String getOnClusterName() {
        return onClusterName;
    }

    public void setOnClusterName(String onClusterName) {
        this.onClusterName = onClusterName;
    }

    @Override
    public CKCreateTableStatement clone() {
        CKCreateTableStatement x = new CKCreateTableStatement();
        cloneTo(x);
        return x;
    }

    public void cloneTo(CKCreateTableStatement x) {
        super.cloneTo(x);

        for (SQLAssignItem item : settings) {
            SQLAssignItem item2 = item.clone();
            item2.setParent(x);
            x.settings.add(item2);
        }

        if (primaryKey != null) {
            x.setPrimaryKey((SQLPrimaryKey) primaryKey.clone());
        }

        if (sampleBy != null) {
            x.setSampleBy(sampleBy.clone());
        }

        if (ttl != null) {
            x.setTtl(ttl.clone());
        }

        x.onClusterName = onClusterName;
    }

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof CKASTVisitor) {
            CKASTVisitor vv = (CKASTVisitor) v;
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
