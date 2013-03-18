package com.alibaba.druid.wall;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class WallSqlStat {

    private volatile long                            executeCount;

    final static AtomicLongFieldUpdater<WallSqlStat> executeCountUpdater = AtomicLongFieldUpdater.newUpdater(WallSqlStat.class,
                                                                                                             "executeCount");

    public long incrementAndGetExecuteCount() {
        return executeCountUpdater.incrementAndGet(this);
    }

    public long getExecuteCount() {
        return executeCount;
    }
    
}
