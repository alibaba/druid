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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.druid.util.Histogram;

public class WebSessionStat {

    private final String        sessionId;

    private final AtomicInteger runningCount             = new AtomicInteger();
    private final AtomicInteger concurrentMax            = new AtomicInteger();
    private final AtomicLong    requestCount             = new AtomicLong(0);
    private final AtomicLong    requestErrorCount        = new AtomicLong(0);
    private final AtomicLong    requestTimeNano          = new AtomicLong();

    private final AtomicLong    jdbcFetchRowCount        = new AtomicLong();
    private final AtomicLong    jdbcUpdateCount          = new AtomicLong();
    private final AtomicLong    jdbcExecuteCount         = new AtomicLong();
    private final AtomicLong    jdbcExecuteTimeNano      = new AtomicLong();

    private final AtomicLong    jdbcCommitCount          = new AtomicLong();
    private final AtomicLong    jdbcRollbackCount        = new AtomicLong();

    private long                createTimeMillis         = -1L;
    private volatile long       lastAccessTimeMillis     = -1L;

    private Set<String>         remoteAddresses          = new HashSet<String>();

    private String     principal                = null;

    private String              userAgent;

    private Histogram           requestIntervalHistogram = Histogram.makeHistogram(8);

    public WebSessionStat(String sessionId){
        super();
        this.sessionId = sessionId;
    }

    public void reset() {
        concurrentMax.set(0);
        requestCount.set(0);
        requestErrorCount.set(0);
        requestTimeNano.set(0);

        jdbcFetchRowCount.set(0);
        jdbcUpdateCount.set(0);
        jdbcExecuteCount.set(0);
        jdbcExecuteTimeNano.set(0);
        jdbcCommitCount.set(0);
        jdbcRollbackCount.set(0);

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

        incrementRequestCount();

        WebRequestStat requestStat = WebRequestStat.current();
        if (requestStat != null) {
            this.setLastAccessTimeMillis(requestStat.getStartMillis());
        }
    }

    public void incrementRequestCount() {
        requestCount.incrementAndGet();
    }

    public void afterInvoke(Throwable error, long nanos) {
        runningCount.decrementAndGet();
        reacord(nanos);
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
            this.addJdbcExecuteTimeNano(requestStat.getJdbcExecuteTimeNano());
        }
    }

    public void addRemoteAddress(String ip) {
        this.remoteAddresses.add(ip);
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

    public long getRequestErrorCount() {
        return requestErrorCount.get();
    }

    public long getRequestTimeNano() {
        return requestTimeNano.get();
    }

    public long getRequestTimeMillis() {
        return getRequestTimeNano() / (1000 * 1000);
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
