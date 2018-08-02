/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.support.spring.stat;

import com.alibaba.druid.support.profile.Profiler;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import static com.alibaba.druid.util.JdbcSqlStatUtils.get;

public class SpringMethodStat {

    private final static ThreadLocal<SpringMethodStat>       currentLocal                     = new ThreadLocal<SpringMethodStat>();

    private final SpringMethodInfo                           methodInfo;

    private final AtomicInteger                              runningCount                     = new AtomicInteger();
    private final AtomicInteger                              concurrentMax                    = new AtomicInteger();
    private final AtomicLong                                 executeCount                     = new AtomicLong(0);
    private final AtomicLong                                 executeErrorCount                = new AtomicLong(0);
    private final AtomicLong                                 executeTimeNano                  = new AtomicLong();

    private final AtomicLong                                 jdbcFetchRowCount                = new AtomicLong();
    private final AtomicLong                                 jdbcUpdateCount                  = new AtomicLong();
    private final AtomicLong                                 jdbcExecuteCount                 = new AtomicLong();
    private final AtomicLong                                 jdbcExecuteErrorCount            = new AtomicLong();
    private final AtomicLong                                 jdbcExecuteTimeNano              = new AtomicLong();

    private final AtomicLong                                 jdbcCommitCount                  = new AtomicLong();
    private final AtomicLong                                 jdbcRollbackCount                = new AtomicLong();

    private final AtomicLong                                 jdbcPoolConnectionOpenCount      = new AtomicLong();
    private final AtomicLong                                 jdbcPoolConnectionCloseCount     = new AtomicLong();

    private final AtomicLong                                 jdbcResultSetOpenCount           = new AtomicLong();
    private final AtomicLong                                 jdbcResultSetCloseCount          = new AtomicLong();

    private volatile Throwable                               lastError;
    private volatile long                                    lastErrorTimeMillis;

    private volatile long                                    histogram_0_1;
    private volatile long                                    histogram_1_10;
    private volatile long                                    histogram_10_100;
    private volatile long                                    histogram_100_1000;
    private volatile int                                     histogram_1000_10000;
    private volatile int                                     histogram_10000_100000;
    private volatile int                                     histogram_100000_1000000;
    private volatile int                                     histogram_1000000_more;

    final static AtomicLongFieldUpdater<SpringMethodStat>    histogram_0_1_Updater            = AtomicLongFieldUpdater.newUpdater(SpringMethodStat.class,
                                                                                                                                  "histogram_0_1");
    final static AtomicLongFieldUpdater<SpringMethodStat>    histogram_1_10_Updater           = AtomicLongFieldUpdater.newUpdater(SpringMethodStat.class,
                                                                                                                                  "histogram_1_10");
    final static AtomicLongFieldUpdater<SpringMethodStat>    histogram_10_100_Updater         = AtomicLongFieldUpdater.newUpdater(SpringMethodStat.class,
                                                                                                                                  "histogram_10_100");
    final static AtomicLongFieldUpdater<SpringMethodStat>    histogram_100_1000_Updater       = AtomicLongFieldUpdater.newUpdater(SpringMethodStat.class,
                                                                                                                                  "histogram_100_1000");
    final static AtomicIntegerFieldUpdater<SpringMethodStat> histogram_1000_10000_Updater     = AtomicIntegerFieldUpdater.newUpdater(SpringMethodStat.class,
                                                                                                                                     "histogram_1000_10000");
    final static AtomicIntegerFieldUpdater<SpringMethodStat> histogram_10000_100000_Updater   = AtomicIntegerFieldUpdater.newUpdater(SpringMethodStat.class,
                                                                                                                                     "histogram_10000_100000");
    final static AtomicIntegerFieldUpdater<SpringMethodStat> histogram_100000_1000000_Updater = AtomicIntegerFieldUpdater.newUpdater(SpringMethodStat.class,
                                                                                                                                     "histogram_100000_1000000");
    final static AtomicIntegerFieldUpdater<SpringMethodStat> histogram_1000000_more_Updater   = AtomicIntegerFieldUpdater.newUpdater(SpringMethodStat.class,
                                                                                                                                     "histogram_1000000_more");

    public SpringMethodStat(SpringMethodInfo methodInfo){
        this.methodInfo = methodInfo;
    }

    public SpringMethodStatValue getStatValue(boolean reset) {
        SpringMethodStatValue val = new SpringMethodStatValue();

        val.setClassName(this.getMethodInfo().getClassName());
        val.setSignature(this.getMethodInfo().getSignature());

        val.setRunningCount(this.getRunningCount());

        val.setConcurrentMax(get(this.concurrentMax, reset));
        val.setExecuteCount(get(this.executeCount, reset));
        val.setExecuteErrorCount(get(this.executeErrorCount, reset));
        val.setExecuteTimeNano(get(this.executeTimeNano, reset));

        val.setJdbcFetchRowCount(get(this.jdbcFetchRowCount, reset));
        val.setJdbcUpdateCount(get(this.jdbcUpdateCount, reset));
        val.setJdbcExecuteCount(get(this.jdbcExecuteCount, reset));
        val.setJdbcExecuteErrorCount(get(this.jdbcExecuteErrorCount, reset));
        val.setJdbcExecuteTimeNano(get(this.jdbcExecuteTimeNano, reset));

        val.setJdbcCommitCount(get(this.jdbcCommitCount, reset));
        val.setJdbcRollbackCount(get(this.jdbcRollbackCount, reset));

        val.setJdbcPoolConnectionOpenCount(get(this.jdbcPoolConnectionOpenCount, reset));
        val.setJdbcPoolConnectionCloseCount(get(this.jdbcPoolConnectionCloseCount, reset));

        val.setJdbcResultSetOpenCount(get(this.jdbcResultSetOpenCount, reset));
        val.setJdbcResultSetCloseCount(get(this.jdbcResultSetCloseCount, reset));

        val.setLastError(this.lastError);
        val.setLastErrorTimeMillis(this.lastErrorTimeMillis);
        if (reset) {
            this.lastError = null;
            this.lastErrorTimeMillis = 0;
        }
        
        val.histogram_0_1 = get(this, histogram_0_1_Updater, reset);
        val.histogram_1_10 = get(this, histogram_1_10_Updater, reset);
        val.histogram_10_100 = get(this, histogram_10_100_Updater, reset);
        val.histogram_100_1000 = get(this, histogram_100_1000_Updater, reset);
        val.histogram_1000_10000 = get(this, histogram_1000_10000_Updater, reset);
        val.histogram_10000_100000 = get(this, histogram_10000_100000_Updater, reset);
        val.histogram_100000_1000000 = get(this, histogram_100000_1000000_Updater, reset);
        val.histogram_1000000_more = get(this, histogram_1000000_more_Updater, reset);

        return val;
    }

    public void reset() {
        concurrentMax.set(0);
        executeCount.set(0);
        executeErrorCount.set(0);
        executeTimeNano.set(0);

        jdbcFetchRowCount.set(0);
        jdbcUpdateCount.set(0);
        jdbcExecuteCount.set(0);
        jdbcExecuteErrorCount.set(0);
        jdbcExecuteTimeNano.set(0);

        jdbcCommitCount.set(0);
        jdbcRollbackCount.set(0);

        jdbcPoolConnectionOpenCount.set(0);
        jdbcPoolConnectionCloseCount.set(0);

        jdbcResultSetOpenCount.set(0);
        jdbcResultSetCloseCount.set(0);

        lastError = null;
        lastErrorTimeMillis = 0;

        histogram_0_1_Updater.set(this, 0);
        histogram_1_10_Updater.set(this, 0);
        histogram_10_100_Updater.set(this, 0);
        histogram_100_1000_Updater.set(this, 0);
        histogram_1000_10000_Updater.set(this, 0);
        histogram_10000_100000_Updater.set(this, 0);
        histogram_100000_1000000_Updater.set(this, 0);
        histogram_1000000_more_Updater.set(this, 0);
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
                }
            } else {
                break;
            }
        }

        executeCount.incrementAndGet();

        Profiler.enter(methodInfo.getSignature(), Profiler.PROFILE_TYPE_SPRING);
    }

    public void afterInvoke(Throwable error, long nanos) {
        runningCount.decrementAndGet();
        executeTimeNano.addAndGet(nanos);
        histogramRecord(nanos);

        if (error != null) {
            executeErrorCount.incrementAndGet();
            lastError = error;
            lastErrorTimeMillis = System.currentTimeMillis();
        }

        Profiler.release(nanos);
    }

    private void histogramRecord(long nanos) {
        final long millis = nanos / 1000 / 1000;

        if (millis < 1) {
            histogram_0_1_Updater.incrementAndGet(this);
        } else if (millis < 10) {
            histogram_1_10_Updater.incrementAndGet(this);
        } else if (millis < 100) {
            histogram_10_100_Updater.incrementAndGet(this);
        } else if (millis < 1000) {
            histogram_100_1000_Updater.incrementAndGet(this);
        } else if (millis < 10000) {
            histogram_1000_10000_Updater.incrementAndGet(this);
        } else if (millis < 100000) {
            histogram_10000_100000_Updater.incrementAndGet(this);
        } else if (millis < 1000000) {
            histogram_100000_1000000_Updater.incrementAndGet(this);
        } else {
            histogram_1000000_more_Updater.incrementAndGet(this);
        }
    }

    public long[] getHistogramValues() {
        return new long[] {
                //
                histogram_0_1, //
                histogram_1_10, //
                histogram_10_100, //
                histogram_100_1000, //
                histogram_1000_10000, //
                histogram_10000_100000, //
                histogram_100000_1000000, //
                histogram_1000000_more //
        };
    }

    public Throwable getLastError() {
        return lastError;
    }

    public Date getLastErrorTime() {
        if (lastErrorTimeMillis <= 0) {
            return null;
        }

        return new Date(lastErrorTimeMillis);
    }

    public long getLastErrorTimeMillis() {
        return lastErrorTimeMillis;
    }

    public int getRunningCount() {
        return this.runningCount.get();
    }

    public int getConcurrentMax() {
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

    public long getJdbcExecuteErrorCount() {
        return jdbcExecuteErrorCount.get();
    }

    public void addJdbcExecuteErrorCount(long executeCount) {
        jdbcExecuteErrorCount.addAndGet(executeCount);
    }

    public void incrementJdbcExecuteErrorCount() {
        jdbcExecuteErrorCount.incrementAndGet();
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

    public long getJdbcPoolConnectionOpenCount() {
        return jdbcPoolConnectionOpenCount.get();
    }

    public void addJdbcPoolConnectionOpenCount(long delta) {
        jdbcPoolConnectionOpenCount.addAndGet(delta);
    }

    public void incrementJdbcPoolConnectionOpenCount() {
        jdbcPoolConnectionOpenCount.incrementAndGet();
    }

    public long getJdbcPoolConnectionCloseCount() {
        return jdbcPoolConnectionCloseCount.get();
    }

    public void addJdbcPoolConnectionCloseCount(long delta) {
        jdbcPoolConnectionCloseCount.addAndGet(delta);
    }

    public void incrementJdbcPoolConnectionCloseCount() {
        jdbcPoolConnectionCloseCount.incrementAndGet();
    }

    public long getJdbcResultSetOpenCount() {
        return jdbcResultSetOpenCount.get();
    }

    public void addJdbcResultSetOpenCount(long delta) {
        jdbcResultSetOpenCount.addAndGet(delta);
    }

    public void incrementJdbcResultSetOpenCount() {
        jdbcResultSetOpenCount.incrementAndGet();
    }

    public long getJdbcResultSetCloseCount() {
        return jdbcResultSetCloseCount.get();
    }

    public void addJdbcResultSetCloseCount(long delta) {
        jdbcResultSetCloseCount.addAndGet(delta);
    }

    public void incrementJdbcResultSetCloseCount() {
        jdbcResultSetCloseCount.incrementAndGet();
    }

    public Map<String, Object> getStatData() {
        return getStatValue(false).getData();
    }
}
