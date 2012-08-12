package com.alibaba.druid.support.http.stat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class WebURIStat {

    private final String                         uri;

    private final AtomicInteger                  runningCount      = new AtomicInteger();
    private final AtomicInteger                  concurrentMax     = new AtomicInteger();
    private final AtomicLong                     requestCount      = new AtomicLong(0);

    private final AtomicLong                     jdbcFetchRowCount = new AtomicLong();
    private final AtomicLong                     jdbcFetchRowPeak  = new AtomicLong();              // 单次请求读取行数的峰值

    private final AtomicLong                     jdbcUpdateCount   = new AtomicLong();
    private final AtomicLong                     jdbcUpdatePeak    = new AtomicLong();              // 单次请求更新行数的峰值

    private final AtomicLong                     jdbcExecuteCount  = new AtomicLong();
    private final AtomicLong                     jdbcExecutePeak   = new AtomicLong();              // 单次请求执行SQL次数的峰值

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

        requestCount.incrementAndGet();
    }

    public void afterInvoke(long nanoSpan) {
        runningCount.decrementAndGet();

        {
            WebRequestStat localStat = WebRequestStat.current();
            if (localStat != null) {
                {
                    long fetchRowCount = localStat.getJdbcFetchRowCount();

                    for (;;) {
                        long peak = jdbcFetchRowPeak.get();
                        if (fetchRowCount <= peak) {
                            break;
                        }

                        if (jdbcFetchRowPeak.compareAndSet(peak, fetchRowCount)) {
                            break;
                        }
                    }
                }
                {
                    long executeCount = localStat.getJdbcExecuteCount();

                    for (;;) {
                        long peak = jdbcExecutePeak.get();
                        if (executeCount <= peak) {
                            break;
                        }

                        if (jdbcExecutePeak.compareAndSet(peak, executeCount)) {
                            break;
                        }
                    }
                }
                {
                    long updateCount = localStat.getJdbcUpdateCount();

                    for (;;) {
                        long peak = jdbcUpdatePeak.get();
                        if (updateCount <= peak) {
                            break;
                        }

                        if (jdbcUpdatePeak.compareAndSet(peak, updateCount)) {
                            break;
                        }
                    }
                }
            }
        }

        currentLocal.set(null);
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

    public long getJdbcFetchRowPeak() {
        return jdbcFetchRowPeak.get();
    }

    public void addJdbcUpdateCount(int updateCount) {
        this.jdbcUpdateCount.addAndGet(updateCount);
    }

    public long getJdbcUpdateCount() {
        return jdbcUpdateCount.get();
    }

    public long getJdbcUpdatePeak() {
        return jdbcUpdatePeak.get();
    }

    public void incrementJdbcExecuteCount() {
        jdbcExecuteCount.incrementAndGet();
    }

    public long getJdbcExecuteCount() {
        return jdbcExecuteCount.get();
    }

    public long getJdbcExecutePeak() {
        return jdbcExecutePeak.get();
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

    public Map<String, Object> getStatData() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();

        data.put("URI", this.getUri());
        data.put("RunningCount", this.getRunningCount());
        data.put("ConcurrentMax", this.getConcurrentMax());
        data.put("RequestCount", this.getRequestCount());

        data.put("JdbcCommitCount", this.getJdbcCommitCount());
        data.put("JdbcRollbackCount", this.getJdbcRollbackCount());

        data.put("JdbcExecuteCount", this.getJdbcExecuteCount());
        data.put("JdbcExecutePeak", this.getJdbcExecutePeak());

        data.put("JdbcFetchRowCount", this.getJdbcFetchRowCount());
        data.put("JdbcFetchRowPeak", this.getJdbcFetchRowPeak());

        data.put("JdbcUpdateCount", this.getJdbcUpdateCount());
        data.put("JdbcUpdatePeak", this.getJdbcUpdatePeak());

        return data;
    }
}
