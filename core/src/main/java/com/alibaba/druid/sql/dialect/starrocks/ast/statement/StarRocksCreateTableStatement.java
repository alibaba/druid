package com.alibaba.druid.sql.dialect.starrocks.ast.statement;

import com.alibaba.druid.DbType;
import com.alibaba.druid.sql.ast.DistributedByType;
import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.ast.SQLOrderBy;
import com.alibaba.druid.sql.ast.statement.SQLAssignItem;
import com.alibaba.druid.sql.ast.statement.SQLCreateTableStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectOrderByItem;
import com.alibaba.druid.sql.dialect.starrocks.ast.StarRocksObject;
import com.alibaba.druid.sql.dialect.starrocks.visitor.StarRocksASTVisitor;
import com.alibaba.druid.sql.visitor.SQLASTVisitor;

import java.util.ArrayList;
import java.util.List;

public class StarRocksCreateTableStatement extends SQLCreateTableStatement implements StarRocksObject {
    protected SQLExpr engine;
    protected SQLOrderBy orderBy;
    protected DistributedByType distributedByType;
    protected final List<SQLSelectOrderByItem> distributedBy = new ArrayList<>();
    protected final List<SQLAssignItem> brokerProperties = new ArrayList<>();
    public StarRocksCreateTableStatement() {
        super(DbType.starrocks);
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

    public DistributedByType getDistributedByType() {
        return distributedByType;
    }

    public void setDistributedByType(DistributedByType distributedByType) {
        this.distributedByType = distributedByType;
    }

    public List<SQLSelectOrderByItem> getDistributedBy() {
        return distributedBy;
    }

    public List<SQLAssignItem> getBrokerProperties() {
        return brokerProperties;
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

    @Override
    protected void accept0(SQLASTVisitor v) {
        if (v instanceof StarRocksASTVisitor) {
            accept0((StarRocksASTVisitor) v);
            return;
        }
        super.accept0(v);
    }

    @Override
    public void accept0(StarRocksASTVisitor v) {
        if (v.visit(this)) {
            super.acceptChild(v);
            acceptChild(v, engine);
            acceptChild(v, orderBy);
        }
        v.endVisit(this);
    }
}
