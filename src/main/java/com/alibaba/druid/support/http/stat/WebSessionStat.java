package com.alibaba.druid.support.http.stat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class WebSessionStat {

    private final String        sessionId;

    private final AtomicInteger runningCount      = new AtomicInteger();
    private final AtomicInteger concurrentMax     = new AtomicInteger();
    private final AtomicLong    requestCount      = new AtomicLong(0);

    private final AtomicLong    jdbcFetchRowCount = new AtomicLong();
    private final AtomicLong    jdbcUpdateCount   = new AtomicLong();
    private final AtomicLong    jdbcExecuteCount  = new AtomicLong();

    private final AtomicLong    jdbcCommitCount   = new AtomicLong();
    private final AtomicLong    jdbcRollbackCount = new AtomicLong();

    private final AtomicLong    requestTimeNano   = new AtomicLong();

    public WebSessionStat(String sessionId){
        super();
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void beforeInvoke() {
        int running = runningCount.incrementAndGet();

        for (;;) {
            int max = concurrentMax.get();
            if (running > max) {
                if (concurrentMax.compareAndSet(max, running)) {
                    break;
                } else {
                    continue;
                }
            } else {
                break;
            }
        }

        requestCount.incrementAndGet();
    }

    public void afterInvoke(long nanoSpan) {
        runningCount.decrementAndGet();

        reacord(nanoSpan);
    }

    public void reacord(long nanos) {
        requestTimeNano.addAndGet(nanos);

        WebRequestStat requestStat = WebRequestStat.current();
        if (requestStat != null) {
            this.addJdbcExecuteCount(requestStat.getJdbcExecuteCount());
            this.addJdbcFetchRowCount(requestStat.getJdbcFetchRowCount());
            this.addJdbcUpdateCount(requestStat.getJdbcUpdateCount());
            this.addJdbcCommitCount(requestStat.getJdbcCommitCount());
            this.addJdbcRollbackCount(requestStat.getJdbcRollbackCount());
        }
    }

    public int getRunningCount() {
        return this.runningCount.get();
    }

    public long getConcurrentMax() {
        return concurrentMax.get();
    }

    public long getRequestCount() {
        return requestCount.get();
    }

    public void addJdbcFetchRowCount(long delta) {
        this.jdbcFetchRowCount.addAndGet(delta);
    }

    public long getJdbcFetchRowCount() {
        return jdbcFetchRowCount.get();
    }

    public void addJdbcUpdateCount(long updateCount) {
        this.jdbcUpdateCount.addAndGet(updateCount);
    }

    public long getJdbcUpdateCount() {
        return jdbcUpdateCount.get();
    }

    public void incrementJdbcExecuteCount() {
        jdbcExecuteCount.incrementAndGet();
    }

    public void addJdbcExecuteCount(long executeCount) {
        jdbcExecuteCount.addAndGet(executeCount);
    }

    public long getJdbcExecuteCount() {
        return jdbcExecuteCount.get();
    }

    public void incrementJdbcCommitCount() {
        jdbcCommitCount.incrementAndGet();
    }

    public long getJdbcCommitCount() {
        return jdbcCommitCount.get();
    }

    public void addJdbcCommitCount(long commitCount) {
        this.jdbcCommitCount.addAndGet(commitCount);
    }

    public void incrementJdbcRollbackCount() {
        jdbcRollbackCount.incrementAndGet();
    }

    public long getJdbcRollbackCount() {
        return jdbcRollbackCount.get();
    }
    
    public void addJdbcRollbackCount(long rollbackCount) {
        this.jdbcRollbackCount.addAndGet(rollbackCount);
    }
    
    public Map<String, Object> getStatData() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();

        data.put("SESSIONID", this.getSessionId());
        data.put("RunningCount", this.getRunningCount());
        data.put("ConcurrentMax", this.getConcurrentMax());
        data.put("RequestCount", this.getRequestCount());

        data.put("JdbcCommitCount", this.getJdbcCommitCount());
        data.put("JdbcRollbackCount", this.getJdbcRollbackCount());

        data.put("JdbcExecuteCount", this.getJdbcExecuteCount());
        data.put("JdbcFetchRowCount", this.getJdbcFetchRowCount());
        data.put("JdbcUpdateCount", this.getJdbcUpdateCount());

        return data;
    }
}
