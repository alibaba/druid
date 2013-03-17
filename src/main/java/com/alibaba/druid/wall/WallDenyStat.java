package com.alibaba.druid.wall;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class WallDenyStat {

    private volatile long                             denyCount;

    final static AtomicLongFieldUpdater<WallDenyStat> denyCountUpdater = AtomicLongFieldUpdater.newUpdater(WallDenyStat.class,
                                                                                                           "denyCount");

    public long incrementAndGetDenyCount() {
        return denyCountUpdater.incrementAndGet(this);
    }

    public long getDenyCount() {
        return denyCount;
    }
}
