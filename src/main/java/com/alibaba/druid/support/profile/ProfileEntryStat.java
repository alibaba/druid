package com.alibaba.druid.support.profile;

import java.util.concurrent.atomic.AtomicLongFieldUpdater;

public class ProfileEntryStat {

    private volatile long                                         executeCount     = 0;
    private volatile long                                         executeTimeNanos = 0;

    private static final AtomicLongFieldUpdater<ProfileEntryStat> executeCountUpdater;
    private static final AtomicLongFieldUpdater<ProfileEntryStat> executeTimeNanosUpdater;

    static {
        executeCountUpdater = AtomicLongFieldUpdater.newUpdater(ProfileEntryStat.class, "executeCount");
        executeTimeNanosUpdater = AtomicLongFieldUpdater.newUpdater(ProfileEntryStat.class, "executeTimeNanos");
    }

    public long getExecuteCount() {
        return executeCount;
    }

    public void addExecuteCount(long delta) {
        executeCountUpdater.addAndGet(this, delta);
    }

    public long getExecuteTimeNanos() {
        return executeTimeNanos;
    }

    public void addExecuteTimeNanos(long delta) {
        executeTimeNanosUpdater.addAndGet(this, delta);
    }
}
