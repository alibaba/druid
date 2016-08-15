/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.support.http.stat;

import com.alibaba.druid.support.profile.ProfileStat;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import static com.alibaba.druid.util.JdbcSqlStatUtils.get;

public class WebURIStat {

    private final String                               uri;

    private volatile int                               runningCount;
    private volatile int                               concurrentMax;
    private volatile long                              requestCount;
    private volatile long                              requestTimeNano;
    final static AtomicIntegerFieldUpdater<WebURIStat> runningCountUpdater                 = AtomicIntegerFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                                  "runningCount");
    final static AtomicIntegerFieldUpdater<WebURIStat> concurrentMaxUpdater                = AtomicIntegerFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                                  "concurrentMax");
    final static AtomicLongFieldUpdater<WebURIStat>    requestCountUpdater                 = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "requestCount");
    final static AtomicLongFieldUpdater<WebURIStat>    requestTimeNanoUpdater              = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "requestTimeNano");

    private volatile long                              jdbcFetchRowCount;
    private volatile long                              jdbcFetchRowPeak;                                                                                       // 单次请求读取行数的峰值
    final static AtomicLongFieldUpdater<WebURIStat>    jdbcFetchRowCountUpdater            = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "jdbcFetchRowCount");
    final static AtomicLongFieldUpdater<WebURIStat>    jdbcFetchRowPeakUpdater             = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "jdbcFetchRowPeak");

    private volatile long                              jdbcUpdateCount;
    private volatile long                              jdbcUpdatePeak;                                                                                         // 单次请求更新行数的峰值
    final static AtomicLongFieldUpdater<WebURIStat>    jdbcUpdateCountUpdater              = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "jdbcUpdateCount");
    final static AtomicLongFieldUpdater<WebURIStat>    jdbcUpdatePeakUpdater               = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "jdbcUpdatePeak");

    private volatile long                              jdbcExecuteCount;
    private volatile long                              jdbcExecuteErrorCount;
    private volatile long                              jdbcExecutePeak;                                                                                        // 单次请求执行SQL次数的峰值
    private volatile long                              jdbcExecuteTimeNano;
    final static AtomicLongFieldUpdater<WebURIStat>    jdbcExecuteCountUpdater             = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "jdbcExecuteCount");
    final static AtomicLongFieldUpdater<WebURIStat>    jdbcExecuteErrorCountUpdater        = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "jdbcExecuteErrorCount");
    final static AtomicLongFieldUpdater<WebURIStat>    jdbcExecutePeakUpdater              = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "jdbcExecutePeak");
    final static AtomicLongFieldUpdater<WebURIStat>    jdbcExecuteTimeNanoUpdater          = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "jdbcExecuteTimeNano");

    private volatile long                              jdbcCommitCount;
    private volatile long                              jdbcRollbackCount;
    final static AtomicLongFieldUpdater<WebURIStat>    jdbcCommitCountUpdater              = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "jdbcCommitCount");
    final static AtomicLongFieldUpdater<WebURIStat>    jdbcRollbackCountUpdater            = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "jdbcRollbackCount");

    private volatile long                              jdbcPoolConnectionOpenCount;
    private volatile long                              jdbcPoolConnectionCloseCount;
    final static AtomicLongFieldUpdater<WebURIStat>    jdbcPoolConnectionOpenCountUpdater  = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "jdbcPoolConnectionOpenCount");
    final static AtomicLongFieldUpdater<WebURIStat>    jdbcPoolConnectionCloseCountUpdater = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "jdbcPoolConnectionCloseCount");

    private volatile long                              jdbcResultSetOpenCount;
    private volatile long                              jdbcResultSetCloseCount;

    private volatile long                              errorCount;

    private volatile long                              lastAccessTimeMillis                = -1L;

    private volatile ProfileStat                       profiletat                          = new ProfileStat();

    final static AtomicLongFieldUpdater<WebURIStat>    jdbcResultSetOpenCountUpdater       = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "jdbcResultSetOpenCount");
    final static AtomicLongFieldUpdater<WebURIStat>    jdbcResultSetCloseCountUpdater      = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "jdbcResultSetCloseCount");
    final static AtomicLongFieldUpdater<WebURIStat>    errorCountUpdater                   = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "errorCount");
    final static AtomicLongFieldUpdater<WebURIStat>    lastAccessTimeMillisUpdater         = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "lastAccessTimeMillis");

    private final static ThreadLocal<WebURIStat>       currentLocal                        = new ThreadLocal<WebURIStat>();

    private volatile long                              histogram_0_1;
    private volatile long                              histogram_1_10;
    private volatile long                              histogram_10_100;
    private volatile long                              histogram_100_1000;
    private volatile int                               histogram_1000_10000;
    private volatile int                               histogram_10000_100000;
    private volatile int                               histogram_100000_1000000;
    private volatile int                               histogram_1000000_more;

    final static AtomicLongFieldUpdater<WebURIStat>    histogram_0_1_Updater               = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "histogram_0_1");
    final static AtomicLongFieldUpdater<WebURIStat>    histogram_1_10_Updater              = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "histogram_1_10");
    final static AtomicLongFieldUpdater<WebURIStat>    histogram_10_100_Updater            = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "histogram_10_100");
    final static AtomicLongFieldUpdater<WebURIStat>    histogram_100_1000_Updater          = AtomicLongFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                               "histogram_100_1000");
    final static AtomicIntegerFieldUpdater<WebURIStat> histogram_1000_10000_Updater        = AtomicIntegerFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                                  "histogram_1000_10000");
    final static AtomicIntegerFieldUpdater<WebURIStat> histogram_10000_100000_Updater      = AtomicIntegerFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                                  "histogram_10000_100000");
    final static AtomicIntegerFieldUpdater<WebURIStat> histogram_100000_1000000_Updater    = AtomicIntegerFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                                  "histogram_100000_1000000");
    final static AtomicIntegerFieldUpdater<WebURIStat> histogram_1000000_more_Updater      = AtomicIntegerFieldUpdater.newUpdater(WebURIStat.class,
                                                                                                                                  "histogram_1000000_more");

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

    public void beforeInvoke() {
        currentLocal.set(this);

        int running = runningCountUpdater.incrementAndGet(this);

        for (;;) {
            int max = concurrentMaxUpdater.get(this);
            if (running > max) {
                if (concurrentMaxUpdater.compareAndSet(this, max, running)) {
                    break;
                }
            } else {
                break;
            }
        }

        requestCountUpdater.incrementAndGet(this);

        WebRequestStat requestStat = WebRequestStat.current();
        if (requestStat != null) {
            this.setLastAccessTimeMillis(requestStat.getStartMillis());
        }
    }

    public void afterInvoke(Throwable error, long nanos) {
        runningCountUpdater.decrementAndGet(this);
        requestTimeNanoUpdater.addAndGet(this, nanos);

        histogramRecord(nanos);

        if (error != null) {
            errorCountUpdater.incrementAndGet(this);
        }

        {
            WebRequestStat localStat = WebRequestStat.current();
            if (localStat != null) {
                {
                    long fetchRowCount = localStat.getJdbcFetchRowCount();
                    this.addJdbcFetchRowCount(fetchRowCount);

                    for (;;) {
                        long peak = jdbcFetchRowPeakUpdater.get(this);
                        if (fetchRowCount <= peak) {
                            break;
                        }

                        if (jdbcFetchRowPeakUpdater.compareAndSet(this, peak, fetchRowCount)) {
                            break;
                        }
                    }
                }
                {
                    long executeCount = localStat.getJdbcExecuteCount();
                    this.addJdbcExecuteCount(executeCount);

                    for (;;) {
                        long peak = jdbcExecutePeakUpdater.get(this);
                        if (executeCount <= peak) {
                            break;
                        }

                        if (jdbcExecutePeakUpdater.compareAndSet(this, peak, executeCount)) {
                            break;
                        }
                    }
                }
                {
                    long updateCount = localStat.getJdbcUpdateCount();
                    this.addJdbcUpdateCount(updateCount);

                    for (;;) {
                        long peak = jdbcUpdatePeakUpdater.get(this);
                        if (updateCount <= peak) {
                            break;
                        }

                        if (jdbcUpdatePeakUpdater.compareAndSet(this, peak, updateCount)) {
                            break;
                        }
                    }
                }

                jdbcExecuteErrorCountUpdater.addAndGet(this, localStat.getJdbcExecuteErrorCount());
                jdbcExecuteTimeNanoUpdater.addAndGet(this, localStat.getJdbcExecuteTimeNano());

                this.addJdbcPoolConnectionOpenCount(localStat.getJdbcPoolConnectionOpenCount());
                this.addJdbcPoolConnectionCloseCount(localStat.getJdbcPoolConnectionCloseCount());

                this.addJdbcResultSetOpenCount(localStat.getJdbcResultSetOpenCount());
                this.addJdbcResultSetCloseCount(localStat.getJdbcResultSetCloseCount());
            }
        }

        currentLocal.set(null);
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

    public int getRunningCount() {
        return this.runningCount;
    }

    public long getConcurrentMax() {
        return concurrentMax;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public long getRequestTimeNano() {
        return requestTimeNano;
    }

    public long getRequestTimeMillis() {
        return getRequestTimeNano() / (1000 * 1000);
    }

    public void addJdbcFetchRowCount(long delta) {
        jdbcFetchRowCountUpdater.addAndGet(this, delta);
    }

    public long getJdbcFetchRowCount() {
        return jdbcFetchRowCount;
    }

    public long getJdbcFetchRowPeak() {
        return jdbcFetchRowPeak;
    }

    public void addJdbcUpdateCount(long updateCount) {
        jdbcUpdateCountUpdater.addAndGet(this, updateCount);
    }

    public long getJdbcUpdateCount() {
        return jdbcUpdateCount;
    }

    public long getJdbcUpdatePeak() {
        return jdbcUpdatePeak;
    }

    public void incrementJdbcExecuteCount() {
        jdbcExecuteCountUpdater.incrementAndGet(this);
    }

    public void addJdbcExecuteCount(long executeCount) {
        jdbcExecuteCountUpdater.addAndGet(this, executeCount);
    }

    public long getJdbcExecuteCount() {
        return jdbcExecuteCount;
    }

    public long getJdbcExecuteErrorCount() {
        return jdbcExecuteErrorCount;
    }

    public long getJdbcExecutePeak() {
        return jdbcExecutePeak;
    }

    public long getJdbcExecuteTimeMillis() {
        return getJdbcExecuteTimeNano() / (1000 * 1000);
    }

    public long getJdbcExecuteTimeNano() {
        return jdbcExecuteTimeNano;
    }

    public void incrementJdbcCommitCount() {
        jdbcCommitCountUpdater.incrementAndGet(this);
    }

    public long getJdbcCommitCount() {
        return jdbcCommitCount;
    }

    public void incrementJdbcRollbackCount() {
        jdbcRollbackCountUpdater.incrementAndGet(this);
    }

    public long getJdbcRollbackCount() {
        return jdbcRollbackCount;
    }

    public void setLastAccessTimeMillis(long lastAccessTimeMillis) {
        this.lastAccessTimeMillis = lastAccessTimeMillis;
    }

    public Date getLastAccessTime() {
        if (lastAccessTimeMillis < 0L) {
            return null;
        }

        return new Date(lastAccessTimeMillis);
    }

    public long getLastAccessTimeMillis() {
        return lastAccessTimeMillis;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public long getJdbcPoolConnectionOpenCount() {
        return jdbcPoolConnectionOpenCount;
    }

    public void addJdbcPoolConnectionOpenCount(long delta) {
        jdbcPoolConnectionOpenCountUpdater.addAndGet(this, delta);
    }

    public void incrementJdbcPoolConnectionOpenCount() {
        jdbcPoolConnectionOpenCountUpdater.incrementAndGet(this);
    }

    public long getJdbcPoolConnectionCloseCount() {
        return jdbcPoolConnectionCloseCount;
    }

    public void addJdbcPoolConnectionCloseCount(long delta) {
        jdbcPoolConnectionCloseCountUpdater.addAndGet(this, delta);
    }

    public void incrementJdbcPoolConnectionCloseCount() {
        jdbcPoolConnectionCloseCountUpdater.incrementAndGet(this);
    }

    public long getJdbcResultSetOpenCount() {
        return jdbcResultSetOpenCount;
    }

    public void addJdbcResultSetOpenCount(long delta) {
        jdbcResultSetOpenCountUpdater.addAndGet(this, delta);
    }

    public long getJdbcResultSetCloseCount() {
        return jdbcResultSetCloseCount;
    }

    public void addJdbcResultSetCloseCount(long delta) {
        jdbcResultSetCloseCountUpdater.addAndGet(this, delta);
    }

    public ProfileStat getProfiletat() {
        return profiletat;
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

    public WebURIStatValue getValue(boolean reset) {
        WebURIStatValue val = new WebURIStatValue();

        val.setUri(uri);

        val.setRunningCount(runningCount);
        val.setConcurrentMax(get(this, concurrentMaxUpdater, reset));
        val.setRequestCount(get(this, requestCountUpdater, reset));
        val.setRequestTimeNano(get(this, requestTimeNanoUpdater, reset));

        val.setJdbcFetchRowCount(get(this, jdbcFetchRowCountUpdater, reset));
        val.setJdbcFetchRowPeak(get(this, jdbcFetchRowPeakUpdater, reset));

        val.setJdbcUpdateCount(get(this, jdbcUpdateCountUpdater, reset));
        val.setJdbcUpdatePeak(get(this, jdbcUpdatePeakUpdater, reset));

        val.setJdbcExecuteCount(get(this, jdbcExecuteCountUpdater, reset));
        val.setJdbcExecuteErrorCount(get(this, jdbcExecuteErrorCountUpdater, reset));
        val.setJdbcExecutePeak(get(this, jdbcExecutePeakUpdater, reset));
        val.setJdbcExecuteTimeNano(get(this, jdbcExecuteTimeNanoUpdater, reset));

        val.setJdbcCommitCount(get(this, jdbcCommitCountUpdater, reset));
        val.setJdbcRollbackCount(get(this, jdbcRollbackCountUpdater, reset));

        val.setJdbcPoolConnectionOpenCount(get(this, jdbcPoolConnectionOpenCountUpdater, reset));
        val.setJdbcPoolConnectionCloseCount(get(this, jdbcPoolConnectionCloseCountUpdater, reset));

        val.setJdbcResultSetOpenCount(get(this, jdbcResultSetOpenCountUpdater, reset));
        val.setJdbcResultSetCloseCount(get(this, jdbcResultSetCloseCountUpdater, reset));

        val.setErrorCount(get(this, errorCountUpdater, reset));

        val.setLastAccessTimeMillis(get(this, lastAccessTimeMillisUpdater, reset));

        val.setProfileEntryStatValueList(this.getProfiletat().getStatValue(reset));
        
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

    public Map<String, Object> getStatData() {
        return getValue(false).getStatData();
    }
}
