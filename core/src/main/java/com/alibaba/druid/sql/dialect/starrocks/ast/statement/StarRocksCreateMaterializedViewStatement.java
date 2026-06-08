package com.alibaba.druid.sql.dialect.starrocks.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLName;
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
    private SQLExpr buckets;
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

    public SQLExpr getBuckets() {
        return buckets;
    }

    public void setBuckets(SQLExpr buckets) {
        if (buckets != null) {
            buckets.setParent(this);
        }
        this.buckets = buckets;
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
        accept0((SQLASTVisitor) visitor);
    }

    @Override
    protected void accept0(SQLASTVisitor visitor) {
    @Override
    protected void accept0(SQLASTVisitor visitor) {
        if (visitor instanceof StarRocksASTVisitor) {
            accept0((StarRocksASTVisitor) visitor);
        } else {
            super.accept0(visitor);
        }
    }
            acceptChild(visitor, getName());
            acceptChild(visitor, getColumns());
            acceptChild(visitor, getComment());
            acceptChild(visitor, getPartitionBy());
            acceptChild(visitor, getDistributedBy());
            acceptChild(visitor, buckets);
            acceptChild(visitor, orderBy);
            acceptChild(visitor, refreshStart);
            acceptChild(visitor, refreshEvery);
            acceptChild(visitor, mvProperties);
            acceptChild(visitor, getQuery());
        }
        visitor.endVisit(this);
    }

    public StarRocksCreateMaterializedViewStatement clone() {
        StarRocksCreateMaterializedViewStatement x = new StarRocksCreateMaterializedViewStatement();
        cloneTo(x);

        x.refreshAsync = this.refreshAsync;
        x.refreshManual = this.refreshManual;
        x.refreshImmediate = this.refreshImmediate;
        x.refreshDeferred = this.refreshDeferred;

        if (this.buckets != null) {
            x.setBuckets(this.buckets.clone());
        }
        if (this.refreshStart != null) {
            x.setRefreshStart(this.refreshStart.clone());
        }
        if (this.refreshEvery != null) {
            x.setRefreshEvery(this.refreshEvery.clone());
        }
        if (this.orderBy != null) {
            x.setOrderBy(this.orderBy.clone());
        }
        for (SQLAssignItem item : this.mvProperties) {
            SQLAssignItem cloned = item.clone();
            cloned.setParent(x);
            x.mvProperties.add(cloned);
        }

        return x;
    }

    protected void cloneTo(StarRocksCreateMaterializedViewStatement x) {
        super.cloneTo(x);

        if (getName() != null) {
            x.setName(getName().clone());
        }
        x.setIfNotExists(isIfNotExists());
        for (SQLName col : getColumns()) {
            SQLName cloned = col.clone();
            cloned.setParent(x);
            x.getColumns().add(cloned);
        }
        if (getQuery() != null) {
            x.setQuery(getQuery().clone());
        }
        if (getPartitionBy() != null) {
            x.setPartitionBy(getPartitionBy().clone());
        }
        if (getComment() != null) {
            x.setComment(getComment().clone());
        }
        for (SQLName item : getDistributedBy()) {
            SQLName cloned = item.clone();
            cloned.setParent(x);
            x.getDistributedBy().add(cloned);
        }
        x.setRefreshFast(isRefreshFast());
        x.setRefreshComplete(isRefreshComplete());
        x.setRefreshForce(isRefreshForce());
        x.setRefreshOnCommit(isRefreshOnCommit());
        x.setRefreshOnDemand(isRefreshOnDemand());
        x.setBuildImmediate(isBuildImmediate());
        x.setBuildDeferred(isBuildDeferred());
    }
}
