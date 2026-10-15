package com.alibaba.druid.sql.dialect.clickhouse.ast;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateMaterializedViewStatement;
import com.alibaba.druid.sql.dialect.clickhouse.visitor.CKASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class CKCreateMaterializedViewStatement extends SQLCreateMaterializedViewStatement {
    private String onCluster;
    private SQLExpr engine;
    private SQLExpr ckPartitionBy;
    private SQLExpr primaryKey;
    private SQLOrderBy orderBy;
    private final List<SQLAssignItem> settings = new ArrayList<SQLAssignItem>();
    private boolean populate;

    public CKCreateMaterializedViewStatement() {
        super.dbType = DbType.clickhouse;
    }

    public String getOnCluster() {
        return onCluster;
    }

    public void setOnCluster(String onCluster) {
        this.onCluster = onCluster;
    }

    public SQLExpr getEngine() {
        return engine;
    }

    public void setEngine(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.engine = x;
    }

    public SQLExpr getCkPartitionBy() {
        return ckPartitionBy;
    }

    public void setCkPartitionBy(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.ckPartitionBy = x;
    }

    public SQLExpr getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(SQLExpr x) {
        if (x != null) {
            x.setParent(this);
        }
        this.primaryKey = x;
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

    public List<SQLAssignItem> getSettings() {
        return settings;
    }

    public boolean isPopulate() {
        return populate;
    }

    public void setPopulate(boolean populate) {
        this.populate = populate;
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof CKASTVisitor) {
            CKASTVisitor ckVisitor = (CKASTVisitor) visitor;
            if (ckVisitor.visit(this)) {
                acceptChild(visitor, getName());
                acceptChild(visitor, getTo());
                acceptChild(visitor, engine);
                acceptChild(visitor, ckPartitionBy);
                acceptChild(visitor, primaryKey);
                acceptChild(visitor, orderBy);
                acceptChild(visitor, settings);
                acceptChild(visitor, getQuery());
            }
            ckVisitor.endVisit(this);
        } else {
            super.accept0(visitor);
        }
    }
}
