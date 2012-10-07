package com.alibaba.druid.support.profile;

public class ProfileEntryReqStat {

    private long executeCount;
    private long executeTimeNanos;

    public long getExecuteCount() {
        return executeCount;
    }

    public void incrementExecuteCount() {
        this.executeCount++;
    }

    public long getExecuteTimeNanos() {
        return executeTimeNanos;
    }

    public void addExecuteTimeNanos(long nanos) {
        this.executeTimeNanos += nanos;
    }

}
