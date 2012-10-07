/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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

import java.util.Date;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import com.alibaba.druid.util.Histogram;

public class WebSessionStat {

    private final String                                   sessionId;

    private volatile int                                   runningCount;
    private volatile int                                   concurrentMax;
    final static AtomicIntegerFieldUpdater<WebSessionStat> runningCountUpdater        = AtomicIntegerFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                             "runningCount");
    final static AtomicIntegerFieldUpdater<WebSessionStat> concurrentMaxUpdater       = AtomicIntegerFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                             "concurrentMax");

    private volatile long                                  requestCount;
    private volatile long                                  requestErrorCount;
    private volatile long                                  requestTimeNano;

    final static AtomicLongFieldUpdater<WebSessionStat>    requestCountUpdater        = AtomicLongFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                          "requestCount");
    final static AtomicLongFieldUpdater<WebSessionStat>    requestErrorCountUpdater   = AtomicLongFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                          "requestErrorCount");
    final static AtomicLongFieldUpdater<WebSessionStat>    requestTimeNanoUpdater     = AtomicLongFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                          "requestTimeNano");

    private volatile long                                  jdbcFetchRowCount;
    private volatile long                                  jdbcUpdateCount;
    private volatile long                                  jdbcExecuteCount;
    private volatile long                                  jdbcExecuteTimeNano;
    final static AtomicLongFieldUpdater<WebSessionStat>    jdbcFetchRowCountUpdater   = AtomicLongFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                          "jdbcFetchRowCount");
    final static AtomicLongFieldUpdater<WebSessionStat>    jdbcUpdateCountUpdater     = AtomicLongFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                          "jdbcUpdateCount");
    final static AtomicLongFieldUpdater<WebSessionStat>    jdbcExecuteCountUpdater    = AtomicLongFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                          "jdbcExecuteCount");
    final static AtomicLongFieldUpdater<WebSessionStat>    jdbcExecuteTimeNanoUpdater = AtomicLongFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                          "jdbcExecuteTimeNano");

    private volatile long                                  jdbcCommitCount;
    private volatile long                                  jdbcRollbackCount;
    final static AtomicLongFieldUpdater<WebSessionStat>    jdbcCommitCountUpdater     = AtomicLongFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                          "jdbcCommitCount");
    final static AtomicLongFieldUpdater<WebSessionStat>    jdbcRollbackCountUpdater   = AtomicLongFieldUpdater.newUpdater(WebSessionStat.class,
                                                                                                                          "jdbcRollbackCount");

    private long                                           createTimeMillis           = -1L;
    private volatile long                                  lastAccessTimeMillis       = -1L;

    private Set<String>                                    remoteAddresses            = new HashSet<String>(2);

    private String                                         principal                  = null;

    private String                                         userAgent;

    private Histogram                                      requestIntervalHistogram   = Histogram.makeHistogram(8);

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

        remoteAddresses.clear();
        principal = null;

        requestIntervalHistogram.reset();
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

    public Date getCreateTime() {
        if (createTimeMillis == -1L) {
            return null;
        }

        return new Date(createTimeMillis);
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

    public Date getLastAccessTime() {
        if (lastAccessTimeMillis < 0L) {
            return null;
        }

        return new Date(lastAccessTimeMillis);
    }

    public Set<String> getRemoteAddresses() {
        return remoteAddresses;
    }

    public String getRemoteAddress() {
        if (remoteAddresses.size() == 0) {
            return null;
        }

        if (remoteAddresses.size() == 1) {
            return remoteAddresses.iterator().next();
        }

        StringBuilder buf = new StringBuilder();
        for (String item : remoteAddresses) {
            if (buf.length() != 0) {
                buf.append(";");
            }
            buf.append(item);
        }

        return buf.toString();
    }

    public void setLastAccessTimeMillis(long lastAccessTimeMillis) {
        if (this.lastAccessTimeMillis > 0) {
            long interval = lastAccessTimeMillis - this.lastAccessTimeMillis;
            requestIntervalHistogram.record(interval);
        }
        this.lastAccessTimeMillis = lastAccessTimeMillis;
    }

    public void beforeInvoke() {
        int running = runningCountUpdater.incrementAndGet(this);

        for (;;) {
            int max = concurrentMaxUpdater.get(this);
            if (running > max) {
                if (concurrentMaxUpdater.compareAndSet(this, max, running)) {
                    break;
                } else {
                    continue;
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
        this.remoteAddresses.add(ip);
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

    public long getRequestTimeMillis() {
        return getRequestTimeNano() / (1000 * 1000);
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

    public long getJdbcExecuteTimeMillis() {
        return getJdbcExecuteTimeNano() / (1000 * 1000);
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
        return requestIntervalHistogram.toArray();
    }

    public Map<String, Object> getStatData() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();

        data.put("SESSIONID", this.getSessionId());
        data.put("Principal", this.getPrincipal());
        data.put("RunningCount", this.getRunningCount());
        data.put("ConcurrentMax", this.getConcurrentMax());
        data.put("RequestCount", this.getRequestCount());
        data.put("RequestTimeMillisTotal", this.getRequestTimeMillis());
        data.put("CreateTime", this.getCreateTime());
        data.put("LastAccessTime", this.getLastAccessTime());
        data.put("RemoteAddress", this.getRemoteAddress());
        data.put("Principal", this.getPrincipal());

        data.put("JdbcCommitCount", this.getJdbcCommitCount());
        data.put("JdbcRollbackCount", this.getJdbcRollbackCount());

        data.put("JdbcExecuteCount", this.getJdbcExecuteCount());
        data.put("JdbcExecuteTimeMillis", this.getJdbcExecuteTimeMillis());
        data.put("JdbcFetchRowCount", this.getJdbcFetchRowCount());
        data.put("JdbcUpdateCount", this.getJdbcUpdateCount());

        data.put("UserAgent", this.getUserAgent());
        data.put("RequestInterval", this.getRequestInterval());

        return data;
    }
}
