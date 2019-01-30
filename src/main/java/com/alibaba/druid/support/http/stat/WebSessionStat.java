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
package com.alibaba.druid.support.http.stat;

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import static com.alibaba.druid.util.JdbcSqlStatUtils.get;

public class WebSessionStat {

    private final static Log                               LOG                                               = LogFactory.getLog(WebSessionStat.class);

    private final String                                   sessionId;

    private volatile int                                   runningCount;
    private volatile int                                   concurrentMax;
    final static AtomicIntegerFieldUpdater<WebSessionStat> runningCountUpdater                               = AtomicIntegerFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                    "runningCount");
    final static AtomicIntegerFieldUpdater<WebSessionStat> concurrentMaxUpdater                              = AtomicIntegerFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                    "concurrentMax");

    private volatile long                                  requestCount;
    private volatile long                                  requestErrorCount;
    private volatile long                                  requestTimeNano;

    final static AtomicLongFieldUpdater<WebSessionStat>    requestCountUpdater                               = AtomicLongFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                 "requestCount");
    final static AtomicLongFieldUpdater<WebSessionStat>    requestErrorCountUpdater                          = AtomicLongFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                 "requestErrorCount");
    final static AtomicLongFieldUpdater<WebSessionStat>    requestTimeNanoUpdater                            = AtomicLongFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                 "requestTimeNano");

    private volatile long                                  jdbcFetchRowCount;
    private volatile long                                  jdbcUpdateCount;
    private volatile long                                  jdbcExecuteCount;
    private volatile long                                  jdbcExecuteTimeNano;
    final static AtomicLongFieldUpdater<WebSessionStat>    jdbcFetchRowCountUpdater                          = AtomicLongFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                 "jdbcFetchRowCount");
    final static AtomicLongFieldUpdater<WebSessionStat>    jdbcUpdateCountUpdater                            = AtomicLongFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                 "jdbcUpdateCount");
    final static AtomicLongFieldUpdater<WebSessionStat>    jdbcExecuteCountUpdater                           = AtomicLongFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                 "jdbcExecuteCount");
    final static AtomicLongFieldUpdater<WebSessionStat>    jdbcExecuteTimeNanoUpdater                        = AtomicLongFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                 "jdbcExecuteTimeNano");

    private volatile long                                  jdbcCommitCount;
    private volatile long                                  jdbcRollbackCount;
    final static AtomicLongFieldUpdater<WebSessionStat>    jdbcCommitCountUpdater                            = AtomicLongFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                 "jdbcCommitCount");
    final static AtomicLongFieldUpdater<WebSessionStat>    jdbcRollbackCountUpdater                          = AtomicLongFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                 "jdbcRollbackCount");

    private long                                           createTimeMillis                                  = -1L;
    private volatile long                                  lastAccessTimeMillis                              = -1L;

    private String                                         remoteAddresses;

    private String                                         principal                                         = null;

    private String                                         userAgent;

    private volatile int                                   requestIntervalHistogram_0_1;
    private volatile int                                   requestIntervalHistogram_1_10;
    private volatile int                                   requestIntervalHistogram_10_100;
    private volatile int                                   requestIntervalHistogram_100_1000;
    private volatile int                                   requestIntervalHistogram_1000_10000;
    private volatile int                                   requestIntervalHistogram_10000_100000;
    private volatile int                                   requestIntervalHistogram_100000_1000000;
    private volatile int                                   requestIntervalHistogram_1000000_10000000;
    private volatile int                                   requestIntervalHistogram_10000000_more;

    final static AtomicIntegerFieldUpdater<WebSessionStat> requestIntervalHistogram_0_1_Updater              = AtomicIntegerFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                    "requestIntervalHistogram_0_1");
    final static AtomicIntegerFieldUpdater<WebSessionStat> requestIntervalHistogram_1_10_Updater             = AtomicIntegerFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                    "requestIntervalHistogram_1_10");
    final static AtomicIntegerFieldUpdater<WebSessionStat> requestIntervalHistogram_10_100_Updater           = AtomicIntegerFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                    "requestIntervalHistogram_10_100");
    final static AtomicIntegerFieldUpdater<WebSessionStat> requestIntervalHistogram_100_1000_Updater         = AtomicIntegerFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                    "requestIntervalHistogram_100_1000");
    final static AtomicIntegerFieldUpdater<WebSessionStat> requestIntervalHistogram_1000_10000_Updater       = AtomicIntegerFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                    "requestIntervalHistogram_1000_10000");
    final static AtomicIntegerFieldUpdater<WebSessionStat> requestIntervalHistogram_10000_100000_Updater     = AtomicIntegerFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                    "requestIntervalHistogram_10000_100000");
    final static AtomicIntegerFieldUpdater<WebSessionStat> requestIntervalHistogram_100000_1000000_Updater   = AtomicIntegerFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                    "requestIntervalHistogram_100000_1000000");
    final static AtomicIntegerFieldUpdater<WebSessionStat> requestIntervalHistogram_1000000_10000000_Updater = AtomicIntegerFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                    "requestIntervalHistogram_1000000_10000000");
    final static AtomicIntegerFieldUpdater<WebSessionStat> requestIntervalHistogram_10000000_more_Updater    = AtomicIntegerFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                                                    "requestIntervalHistogram_10000000_more");

    public WebSessionStat(String sessionId){
        super();
        this.sessionId = sessionId;
    }

    public void reset() {
        concurrentMaxUpdater.set(this, 0);
        requestCountUpdater.set(this, 0);
        requestErrorCountUpdater.set(this, 0);
        requestTimeNanoUpdater.set(this, 0);

        jdbcFetchRowCountUpdater.set(this, 0);
        jdbcUpdateCountUpdater.set(this, 0);
        jdbcExecuteCountUpdater.set(this, 0);
        jdbcExecuteTimeNanoUpdater.set(this, 0);
        jdbcCommitCountUpdater.set(this, 0);
        jdbcRollbackCountUpdater.set(this, 0);

        remoteAddresses = null;
        principal = null;

        requestIntervalHistogram_0_1_Updater.set(this, 0);
        requestIntervalHistogram_1_10_Updater.set(this, 0);
        requestIntervalHistogram_10_100_Updater.set(this, 0);
        requestIntervalHistogram_100_1000_Updater.set(this, 0);
        requestIntervalHistogram_1000_10000_Updater.set(this, 0);
        requestIntervalHistogram_10000_100000_Updater.set(this, 0);
        requestIntervalHistogram_100000_1000000_Updater.set(this, 0);
        requestIntervalHistogram_1000000_10000000_Updater.set(this, 0);
        requestIntervalHistogram_10000000_more_Updater.set(this, 0);
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public String getSessionId() {
        return sessionId;
    }

    public long getCreateTimeMillis() {
        return createTimeMillis;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public void setCreateTimeMillis(long createTimeMillis) {
        this.createTimeMillis = createTimeMillis;
    }

    public long getLastAccessTimeMillis() {
        return lastAccessTimeMillis;
    }

    public String getRemoteAddress() {
        return remoteAddresses;
    }

    public void setLastAccessTimeMillis(long lastAccessTimeMillis) {
        if (this.lastAccessTimeMillis > 0) {
            long interval = lastAccessTimeMillis - this.lastAccessTimeMillis;
            requestIntervalHistogramRecord(interval);
        }
        this.lastAccessTimeMillis = lastAccessTimeMillis;
    }

    private void requestIntervalHistogramRecord(long nanoSpan) {
        long millis = nanoSpan / 1000 / 1000;

        if (millis < 1) {
            requestIntervalHistogram_0_1_Updater.incrementAndGet(this);
        } else if (millis < 10) {
            requestIntervalHistogram_1_10_Updater.incrementAndGet(this);
        } else if (millis < 100) {
            requestIntervalHistogram_10_100_Updater.incrementAndGet(this);
        } else if (millis < 1000) {
            requestIntervalHistogram_100_1000_Updater.incrementAndGet(this);
        } else if (millis < 10000) {
            requestIntervalHistogram_1000_10000_Updater.incrementAndGet(this);
        } else if (millis < 100000) {
            requestIntervalHistogram_10000_100000_Updater.incrementAndGet(this);
        } else if (millis < 1000000) {
            requestIntervalHistogram_100000_1000000_Updater.incrementAndGet(this);
        } else if (millis < 10000000) {
            requestIntervalHistogram_1000000_10000000_Updater.incrementAndGet(this);
        } else {
            requestIntervalHistogram_10000000_more_Updater.incrementAndGet(this);
        }
    }

    public void beforeInvoke() {
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

        incrementRequestCount();

        WebRequestStat requestStat = WebRequestStat.current();
        if (requestStat != null) {
            this.setLastAccessTimeMillis(requestStat.getStartMillis());
        }
    }

    public void incrementRequestCount() {
        requestCountUpdater.incrementAndGet(this);
    }

    public void afterInvoke(Throwable error, long nanos) {
        runningCountUpdater.decrementAndGet(this);
        reacord(nanos);
    }

    public void reacord(long nanos) {
        requestTimeNanoUpdater.addAndGet(this, nanos);

        WebRequestStat requestStat = WebRequestStat.current();
        if (requestStat != null) {
            this.addJdbcExecuteCount(requestStat.getJdbcExecuteCount());
            this.addJdbcFetchRowCount(requestStat.getJdbcFetchRowCount());
            this.addJdbcUpdateCount(requestStat.getJdbcUpdateCount());
            this.addJdbcCommitCount(requestStat.getJdbcCommitCount());
            this.addJdbcRollbackCount(requestStat.getJdbcRollbackCount());
            this.addJdbcExecuteTimeNano(requestStat.getJdbcExecuteTimeNano());
        }
    }

    public void addRemoteAddress(String ip) {
        if (remoteAddresses == null) {
            this.remoteAddresses = ip;
            return;
        }

        if (remoteAddresses.contains(ip)) {
            return;
        }

        if (remoteAddresses.length() > 256) {
            return;
        }

        remoteAddresses += ';' + ip;
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

    public long getRequestErrorCount() {
        return requestErrorCount;
    }

    public long getRequestTimeNano() {
        return requestTimeNano;
    }

    public void addJdbcFetchRowCount(long delta) {
        jdbcFetchRowCountUpdater.addAndGet(this, delta);
    }

    public long getJdbcFetchRowCount() {
        return jdbcFetchRowCount;
    }

    public void addJdbcUpdateCount(long updateCount) {
        jdbcUpdateCountUpdater.addAndGet(this, updateCount);
    }

    public long getJdbcUpdateCount() {
        return jdbcUpdateCount;
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

    public long getJdbcExecuteTimeNano() {
        return jdbcExecuteTimeNano;
    }

    public void addJdbcExecuteTimeNano(long nano) {
        jdbcExecuteTimeNanoUpdater.addAndGet(this, nano);
    }

    public void incrementJdbcCommitCount() {
        jdbcCommitCountUpdater.incrementAndGet(this);
    }

    public long getJdbcCommitCount() {
        return jdbcCommitCount;
    }

    public void addJdbcCommitCount(long commitCount) {
        jdbcCommitCountUpdater.addAndGet(this, commitCount);
    }

    public void incrementJdbcRollbackCount() {
        jdbcRollbackCountUpdater.incrementAndGet(this);
    }

    public long getJdbcRollbackCount() {
        return jdbcRollbackCount;
    }

    public void addJdbcRollbackCount(long rollbackCount) {
        jdbcRollbackCountUpdater.addAndGet(this, rollbackCount);
    }

    public long[] getRequestInterval() {
        return new long[] {
                //
                requestIntervalHistogram_0_1, //
                requestIntervalHistogram_1_10, //
                requestIntervalHistogram_10_100, //
                requestIntervalHistogram_100_1000, //
                requestIntervalHistogram_1000_10000, //
                requestIntervalHistogram_10000_100000, //
                requestIntervalHistogram_100000_1000000, //
                requestIntervalHistogram_1000000_10000000, //
                requestIntervalHistogram_10000000_more //
        };
    }

    public Map<String, Object> getStatData() {
        return getValue(false).getStatData();
    }

    public WebSessionStatValue getValue(boolean reset) {
        WebSessionStatValue val = new WebSessionStatValue();

        val.sessionId = sessionId;
        val.runningCount = this.getRunningCount();
        val.concurrentMax = get(this, concurrentMaxUpdater, reset);
        val.requestCount = get(this, requestCountUpdater, reset);
        val.requestErrorCount = get(this, requestErrorCountUpdater, reset);
        val.requestTimeNano = get(this, requestTimeNanoUpdater, reset);
        val.jdbcFetchRowCount = get(this, jdbcFetchRowCountUpdater, reset);
        val.jdbcUpdateCount = get(this, jdbcUpdateCountUpdater, reset);
        val.jdbcExecuteCount = get(this, jdbcExecuteCountUpdater, reset);
        val.jdbcExecuteTimeNano = get(this, jdbcExecuteTimeNanoUpdater, reset);
        val.jdbcCommitCount = get(this, jdbcCommitCountUpdater, reset);
        val.jdbcRollbackCount = get(this, jdbcRollbackCountUpdater, reset);
        val.createTimeMillis = createTimeMillis;
        val.lastAccessTimeMillis = lastAccessTimeMillis;
        val.remoteAddress = remoteAddresses;
        val.principal = principal;
        val.userAgent = userAgent;

        val.requestIntervalHistogram_0_1 = get(this, requestIntervalHistogram_0_1_Updater, reset);
        val.requestIntervalHistogram_1_10 = get(this, requestIntervalHistogram_1_10_Updater, reset);
        val.requestIntervalHistogram_10_100 = get(this, requestIntervalHistogram_10_100_Updater, reset);
        val.requestIntervalHistogram_100_1000 = get(this, requestIntervalHistogram_100_1000_Updater, reset);
        val.requestIntervalHistogram_1000_10000 = get(this, requestIntervalHistogram_1000_10000_Updater, reset);
        val.requestIntervalHistogram_10000_100000 = get(this, requestIntervalHistogram_10000_100000_Updater, reset);
        val.requestIntervalHistogram_100000_1000000 = get(this, requestIntervalHistogram_100000_1000000_Updater, reset);
        val.requestIntervalHistogram_1000000_10000000 = get(this, requestIntervalHistogram_1000000_10000000_Updater,
                                                            reset);
        val.requestIntervalHistogram_10000000_more = get(this, requestIntervalHistogram_10000000_more_Updater, reset);

        return val;
    }
}
