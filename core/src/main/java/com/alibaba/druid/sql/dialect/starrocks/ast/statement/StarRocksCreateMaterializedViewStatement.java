package com.alibaba.druid.sql.dialect.starrocks.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateMaterializedViewStatement;
import com.alibaba.druid.sql.dialect.starrocks.ast.StarRocksObject;
import com.alibaba.druid.sql.dialect.starrocks.visitor.StarRocksASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class StarRocksCreateMaterializedViewStatement extends SQLCreateMaterializedViewStatement implements StarRocksObject {
    private boolean refreshAsync;
    private boolean refreshManual;
    private boolean refreshImmediate;
    private boolean refreshDeferred;
    private SQLExpr refreshStart;
    private SQLExpr refreshEvery;
    private SQLOrderBy orderBy;
    private final List<SQLAssignItem> mvProperties = new ArrayList<>();

    public StarRocksCreateMaterializedViewStatement() {
        super();
        setDbType(DbType.starrocks);
    }

    public boolean isRefreshAsync() {
        return refreshAsync;
    }

    public void setRefreshAsync(boolean refreshAsync) {
        this.refreshAsync = refreshAsync;
    }

    public boolean isRefreshManual() {
        return refreshManual;
    }

    public void setRefreshManual(boolean refreshManual) {
        this.refreshManual = refreshManual;
    }

    public boolean isRefreshImmediate() {
        return refreshImmediate;
    }

    public void setRefreshImmediate(boolean refreshImmediate) {
        this.refreshImmediate = refreshImmediate;
    }

    public boolean isRefreshDeferred() {
        return refreshDeferred;
    }

    public void setRefreshDeferred(boolean refreshDeferred) {
        this.refreshDeferred = refreshDeferred;
    }

    public SQLExpr getRefreshStart() {
        return refreshStart;
    }

    public void setRefreshStart(SQLExpr refreshStart) {
        if (refreshStart != null) {
            refreshStart.setParent(this);
        }
        this.refreshStart = refreshStart;
    }

    public SQLExpr getRefreshEvery() {
        return refreshEvery;
    }

    public void setRefreshEvery(SQLExpr refreshEvery) {
        if (refreshEvery != null) {
            refreshEvery.setParent(this);
        }
        this.refreshEvery = refreshEvery;
    }

    public SQLOrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(SQLOrderBy orderBy) {
        if (orderBy != null) {
            orderBy.setParent(this);
        }
        this.orderBy = orderBy;
    }

    public List<SQLAssignItem> getMvProperties() {
        return mvProperties;
    }

    @Override
    public void accept0(StarRocksASTVisitor visitor) {
        if (visitor.visit(this)) {
            acceptChild(visitor, getName());
            acceptChild(visitor, getColumns());
            acceptChild(visitor, getPartitionBy());
            acceptChild(visitor, orderBy);
            acceptChild(visitor, refreshStart);
            acceptChild(visitor, refreshEvery);
            acceptChild(visitor, mvProperties);
            acceptChild(visitor, getQuery());
        }
        visitor.endVisit(this);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof StarRocksASTVisitor) {
            accept0((StarRocksASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }
}
