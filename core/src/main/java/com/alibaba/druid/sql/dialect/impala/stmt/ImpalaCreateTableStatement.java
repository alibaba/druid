package com.alibaba.druid.sql.dialect.impala.stmt;

import com.alibaba.druid.sql.ast.SQLExpr;
import com.alibaba.druid.sql.dialect.hive.stmt.HiveCreateTableStatement;

public class ImpalaCreateTableStatement extends HiveCreateTableStatement {
    private boolean unCached;
    private SQLExpr cachedPool;
    private int cachedReplication = -1;

    public boolean isCached() {
        return cachedPool != null;
    }

    public SQLExpr getCachedPool() {
        return cachedPool;
    }

    public void setCachedPool(SQLExpr cachedPool) {
        this.cachedPool = cachedPool;
    }

    public int getCachedReplication() {
        return cachedReplication;
    }

    public void setCachedReplication(int cachedReplication) {
        this.cachedReplication = cachedReplication;
    }

    public boolean isUnCached() {
        return unCached;
    }

    public void setUnCached(boolean unCached) {
        this.unCached = unCached;
    }
}
