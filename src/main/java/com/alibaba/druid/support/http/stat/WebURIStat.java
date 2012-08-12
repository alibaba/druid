package com.alibaba.druid.support.http.stat;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class WebURIStat {

    private final String                         uri;

    private final AtomicInteger                  runningCount      = new AtomicInteger();
    private final AtomicInteger                  concurrentMax     = new AtomicInteger();
    private final AtomicLong                     count             = new AtomicLong(0);

    private final AtomicLong                     jdbcFetchRowCount = new AtomicLong();
    private final AtomicLong                     jdbcFetchRowPeak  = new AtomicLong();
    private final AtomicLong                     jdbcUpdateCount   = new AtomicLong();
    private final AtomicLong                     jdbcExecuteCount  = new AtomicLong();
    private final AtomicLong                     jdbcCommitCount   = new AtomicLong();
    private final AtomicLong                     jdbcRollbackCount = new AtomicLong();

    private final static ThreadLocal<WebURIStat> currentLocal      = new ThreadLocal<WebURIStat>();

    public WebURIStat(String uri){
        super();
        this.uri = uri;
    }

    public static WebURIStat current() {
        return currentLocal.get();
    }

    public String getUri() {
        return uri;
    }

    public void beforeInvoke(String uri) {
        currentLocal.set(this);

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

        count.incrementAndGet();
    }

    public void afterInvoke(long nanoSpan) {
        runningCount.decrementAndGet();

        currentLocal.set(null);
    }

    public void addJdbcFetchRowCount(long delta) {
        this.jdbcFetchRowCount.addAndGet(delta);
    }

    public long getJdbcFetchRowCount() {
        return jdbcFetchRowCount.get();
    }

    public long getJdbcFetchRowPeak() {
        return jdbcFetchRowPeak.get();
    }

    public void addJdbcUpdateCount(int updateCount) {
        this.jdbcUpdateCount.addAndGet(updateCount);
    }

    public long getJdbcUpdateCount() {
        return jdbcUpdateCount.get();
    }

    public void incrementJdbcExecuteCount() {
        jdbcExecuteCount.incrementAndGet();
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

    public void incrementJdbcRollbackCount() {
        jdbcRollbackCount.incrementAndGet();
    }

    public long getJdbcRollbackCount() {
        return jdbcRollbackCount.get();
    }
}
