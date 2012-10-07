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
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.druid.support.profile.ProfileStat;
import com.alibaba.druid.support.profile.Profiler;

public class WebURIStat {

    private final String                         uri;

    private final AtomicInteger                  runningCount                 = new AtomicInteger();
    private final AtomicInteger                  concurrentMax                = new AtomicInteger();
    private final AtomicLong                     requestCount                 = new AtomicLong(0);
    private final AtomicLong                     requestTimeNano              = new AtomicLong();

    private final AtomicLong                     jdbcFetchRowCount            = new AtomicLong();
    private final AtomicLong                     jdbcFetchRowPeak             = new AtomicLong();             // 单次请求读取行数的峰值

    private final AtomicLong                     jdbcUpdateCount              = new AtomicLong();
    private final AtomicLong                     jdbcUpdatePeak               = new AtomicLong();             // 单次请求更新行数的峰值

    private final AtomicLong                     jdbcExecuteCount             = new AtomicLong();
    private final AtomicLong                     jdbcExecuteErrorCount        = new AtomicLong();
    private final AtomicLong                     jdbcExecutePeak              = new AtomicLong();             // 单次请求执行SQL次数的峰值
    private final AtomicLong                     jdbcExecuteTimeNano          = new AtomicLong();

    private final AtomicLong                     jdbcCommitCount              = new AtomicLong();
    private final AtomicLong                     jdbcRollbackCount            = new AtomicLong();

    private final AtomicLong                     jdbcPoolConnectionOpenCount  = new AtomicLong();
    private final AtomicLong                     jdbcPoolConnectionCloseCount = new AtomicLong();

    private final AtomicLong                     jdbcResultSetOpenCount       = new AtomicLong();
    private final AtomicLong                     jdbcResultSetCloseCount      = new AtomicLong();

    private final AtomicLong                     errorCount                   = new AtomicLong();

    private volatile long                        lastAccessTimeMillis         = -1L;

    private ProfileStat                          profiletat                   = new ProfileStat();

    private final static ThreadLocal<WebURIStat> currentLocal                 = new ThreadLocal<WebURIStat>();

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

        WebRequestStat requestStat = WebRequestStat.current();
        if (requestStat != null) {
            this.setLastAccessTimeMillis(requestStat.getStartMillis());
        }

        Profiler.enter(uri, Profiler.PROFILE_TYPE_WEB);
    }

    public void afterInvoke(Throwable error, long nanos) {
        runningCount.decrementAndGet();
        requestTimeNano.addAndGet(nanos);

        if (error != null) {
            errorCount.incrementAndGet();
        }

        {
            WebRequestStat localStat = WebRequestStat.current();
            if (localStat != null) {
                {
                    long fetchRowCount = localStat.getJdbcFetchRowCount();
                    this.addJdbcFetchRowCount(fetchRowCount);

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
                    this.addJdbcExecuteCount(executeCount);

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
                    this.addJdbcUpdateCount(updateCount);

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

                this.jdbcExecuteErrorCount.addAndGet(localStat.getJdbcExecuteErrorCount());
                this.jdbcExecuteTimeNano.addAndGet(localStat.getJdbcExecuteTimeNano());

                this.addJdbcPoolConnectionOpenCount(localStat.getJdbcPoolConnectionOpenCount());
                this.addJdbcPoolConnectionCloseCount(localStat.getJdbcPoolConnectionCloseCount());

                this.addJdbcResultSetOpenCount(localStat.getJdbcResultSetOpenCount());
                this.addJdbcResultSetCloseCount(localStat.getJdbcResultSetCloseCount());
            }
        }

        currentLocal.set(null);

        Profiler.release(nanos);
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

    public long getJdbcFetchRowPeak() {
        return jdbcFetchRowPeak.get();
    }

    public void addJdbcUpdateCount(long updateCount) {
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

    public void addJdbcExecuteCount(long executeCount) {
        jdbcExecuteCount.addAndGet(executeCount);
    }

    public long getJdbcExecuteCount() {
        return jdbcExecuteCount.get();
    }

    public AtomicLong getJdbcExecuteErrorCount() {
        return jdbcExecuteErrorCount;
    }

    public long getJdbcExecutePeak() {
        return jdbcExecutePeak.get();
    }

    public long getJdbcExecuteTimeMillis() {
        return getJdbcExecuteTimeNano() / (1000 * 1000);
    }

    public long getJdbcExecuteTimeNano() {
        return jdbcExecuteTimeNano.get();
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
        return errorCount.get();
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

    public long getJdbcResultSetCloseCount() {
        return jdbcResultSetCloseCount.get();
    }

    public void addJdbcResultSetCloseCount(long delta) {
        jdbcResultSetCloseCount.addAndGet(delta);
    }

    public ProfileStat getProfiletat() {
        return profiletat;
    }

    public Map<String, Object> getStatData() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();

        data.put("URI", this.getUri());
        data.put("RunningCount", this.getRunningCount());
        data.put("ConcurrentMax", this.getConcurrentMax());
        data.put("RequestCount", this.getRequestCount());
        data.put("RequestTimeMillis", this.getRequestTimeMillis());
        data.put("ErrorCount", this.getErrorCount());
        data.put("LastAccessTime", this.getLastAccessTime());

        data.put("JdbcCommitCount", this.getJdbcCommitCount());
        data.put("JdbcRollbackCount", this.getJdbcRollbackCount());

        data.put("JdbcExecuteCount", this.getJdbcExecuteCount());
        data.put("JdbcExecuteErrorCount", this.getJdbcExecuteErrorCount());
        data.put("JdbcExecutePeak", this.getJdbcExecutePeak());
        data.put("JdbcExecuteTimeMillis", this.getJdbcExecuteTimeMillis());

        data.put("JdbcFetchRowCount", this.getJdbcFetchRowCount());
        data.put("JdbcFetchRowPeak", this.getJdbcFetchRowPeak());

        data.put("JdbcUpdateCount", this.getJdbcUpdateCount());
        data.put("JdbcUpdatePeak", this.getJdbcUpdatePeak());

        data.put("JdbcPoolConnectionOpenCount", this.getJdbcPoolConnectionOpenCount());
        data.put("JdbcPoolConnectionCloseCount", this.getJdbcPoolConnectionCloseCount());

        data.put("JdbcResultSetOpenCount", this.getJdbcResultSetOpenCount());
        data.put("JdbcResultSetCloseCount", this.getJdbcResultSetCloseCount());

        data.put("Profiles", this.getProfiletat().getStatData());

        return data;
    }
}
