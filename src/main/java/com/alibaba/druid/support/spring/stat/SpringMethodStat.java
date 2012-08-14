package com.alibaba.druid.support.spring.stat;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class SpringMethodStat {

    private final static ThreadLocal<SpringMethodStat> currentLocal        = new ThreadLocal<SpringMethodStat>();

    private final SpringMethodInfo                           methodInfo;

    private final AtomicInteger                        runningCount        = new AtomicInteger();
    private final AtomicInteger                        concurrentMax       = new AtomicInteger();
    private final AtomicLong                           executeCount        = new AtomicLong(0);
    private final AtomicLong                           executeErrorCount   = new AtomicLong(0);
    private final AtomicLong                           executeTimeNano     = new AtomicLong();

    private final AtomicLong                           jdbcFetchRowCount   = new AtomicLong();
    private final AtomicLong                           jdbcUpdateCount     = new AtomicLong();
    private final AtomicLong                           jdbcExecuteCount    = new AtomicLong();
    private final AtomicLong                           jdbcExecuteTimeNano = new AtomicLong();

    private final AtomicLong                           jdbcCommitCount     = new AtomicLong();
    private final AtomicLong                           jdbcRollbackCount   = new AtomicLong();

    public SpringMethodStat(SpringMethodInfo methodInfo){
        this.methodInfo = methodInfo;
    }
    
    public void reset() {
        concurrentMax.set(0);
        executeCount.set(0);
        executeErrorCount.set(0);
        executeTimeNano.set(0);
        
        jdbcFetchRowCount.set(0);
        jdbcUpdateCount.set(0);
        jdbcExecuteCount.set(0);
        jdbcExecuteTimeNano.set(0);
        
        jdbcCommitCount.set(0);
        jdbcRollbackCount.set(0);
    }

    public SpringMethodInfo getMethodInfo() {
        return methodInfo;
    }

    public static SpringMethodStat current() {
        return currentLocal.get();
    }

    public static void setCurrent(SpringMethodStat current) {
        currentLocal.set(current);
    }

    public void beforeInvoke() {
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

        executeCount.incrementAndGet();
    }

    public void afterInvoke(Throwable error, long nanos) {
        runningCount.decrementAndGet();
        executeTimeNano.addAndGet(nanos);

        if (error != null) {
            executeErrorCount.incrementAndGet();
        }
    }

    public int getRunningCount() {
        return this.runningCount.get();
    }

    public long getConcurrentMax() {
        return concurrentMax.get();
    }

    public long getExecuteCount() {
        return executeCount.get();
    }

    public long getExecuteErrorCount() {
        return executeErrorCount.get();
    }

    public long getExecuteTimeNano() {
        return executeTimeNano.get();
    }

    public long getExecuteTimeMillis() {
        return getExecuteTimeNano() / (1000 * 1000);
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

    public long getJdbcExecuteTimeMillis() {
        return getJdbcExecuteTimeNano() / (1000 * 1000);
    }

    public long getJdbcExecuteTimeNano() {
        return jdbcExecuteTimeNano.get();
    }

    public void addJdbcExecuteTimeNano(long nano) {
        jdbcExecuteTimeNano.addAndGet(nano);
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
        
        data.put("Class", this.getMethodInfo().getClassName());
        data.put("Method", this.getMethodInfo().getSignature());

        data.put("RunningCount", this.getRunningCount());
        data.put("ConcurrentMax", this.getConcurrentMax());
        data.put("ExecuteCount", this.getExecuteCount());
        data.put("ExecuteErrorCount", this.getExecuteErrorCount());
        data.put("ExecuteTimeMillis", this.getExecuteTimeMillis());

        data.put("JdbcCommitCount", this.getJdbcCommitCount());
        data.put("JdbcRollbackCount", this.getJdbcRollbackCount());

        data.put("JdbcExecuteCount", this.getJdbcExecuteCount());
        data.put("JdbcExecuteTimeMillis", this.getJdbcExecuteTimeMillis());
        data.put("JdbcFetchRowCount", this.getJdbcFetchRowCount());
        data.put("JdbcUpdateCount", this.getJdbcUpdateCount());

        return data;
    }
}
