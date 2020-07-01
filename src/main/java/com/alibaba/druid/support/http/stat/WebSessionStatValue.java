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

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

public class WebSessionStatValue {

    String sessionId;

    int    runningCount;
    int    concurrentMax;
    long   requestCount;
    long   requestErrorCount;
    long   requestTimeNano;
    long   jdbcFetchRowCount;
    long   jdbcUpdateCount;
    long   jdbcExecuteCount;
    long   jdbcExecuteTimeNano;
    long   jdbcCommitCount;
    long   jdbcRollbackCount;
    long   createTimeMillis;
    long   lastAccessTimeMillis;
    String remoteAddress;
    String principal;
    String userAgent;

    int    requestIntervalHistogram_0_1;
    int    requestIntervalHistogram_1_10;
    int    requestIntervalHistogram_10_100;
    int    requestIntervalHistogram_100_1000;
    int    requestIntervalHistogram_1000_10000;
    int    requestIntervalHistogram_10000_100000;
    int    requestIntervalHistogram_100000_1000000;
    int    requestIntervalHistogram_1000000_10000000;
    int    requestIntervalHistogram_10000000_more;

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public int getRunningCount() {
        return runningCount;
    }

    public void setRunningCount(int runningCount) {
        this.runningCount = runningCount;
    }

    public int getConcurrentMax() {
        return concurrentMax;
    }

    public void setConcurrentMax(int concurrentMax) {
        this.concurrentMax = concurrentMax;
    }

    public long getRequestCount() {
        return requestCount;
    }

    public void setRequestCount(long requestCount) {
        this.requestCount = requestCount;
    }

    public long getRequestErrorCount() {
        return requestErrorCount;
    }

    public void setRequestErrorCount(long requestErrorCount) {
        this.requestErrorCount = requestErrorCount;
    }

    public long getRequestTimeNano() {
        return requestTimeNano;
    }

    public void setRequestTimeNano(long requestTimeNano) {
        this.requestTimeNano = requestTimeNano;
    }

    public long getJdbcFetchRowCount() {
        return jdbcFetchRowCount;
    }

    public void setJdbcFetchRowCount(long jdbcFetchRowCount) {
        this.jdbcFetchRowCount = jdbcFetchRowCount;
    }

    public long getJdbcUpdateCount() {
        return jdbcUpdateCount;
    }

    public void setJdbcUpdateCount(long jdbcUpdateCount) {
        this.jdbcUpdateCount = jdbcUpdateCount;
    }

    public long getJdbcExecuteCount() {
        return jdbcExecuteCount;
    }

    public void setJdbcExecuteCount(long jdbcExecuteCount) {
        this.jdbcExecuteCount = jdbcExecuteCount;
    }

    public long getJdbcExecuteTimeNano() {
        return jdbcExecuteTimeNano;
    }

    public void setJdbcExecuteTimeNano(long jdbcExecuteTimeNano) {
        this.jdbcExecuteTimeNano = jdbcExecuteTimeNano;
    }

    public long getJdbcCommitCount() {
        return jdbcCommitCount;
    }

    public void setJdbcCommitCount(long jdbcCommitCount) {
        this.jdbcCommitCount = jdbcCommitCount;
    }

    public long getJdbcRollbackCount() {
        return jdbcRollbackCount;
    }

    public void setJdbcRollbackCount(long jdbcRollbackCount) {
        this.jdbcRollbackCount = jdbcRollbackCount;
    }

    public long getCreateTimeMillis() {
        return createTimeMillis;
    }

    public void setCreateTimeMillis(long createTimeMillis) {
        this.createTimeMillis = createTimeMillis;
    }

    public long getLastAccessTimeMillis() {
        return lastAccessTimeMillis;
    }

    public void setLastAccessTimeMillis(long lastAccessTimeMillis) {
        this.lastAccessTimeMillis = lastAccessTimeMillis;
    }

    public String getRemoteAddress() {
        return remoteAddress;
    }

    public void setRemoteAddress(String remoteAddresses) {
        this.remoteAddress = remoteAddresses;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public long getRequestTimeMillis() {
        return getRequestTimeNano() / (1000 * 1000);
    }

    public Date getCreateTime() {
        if (createTimeMillis == -1L) {
            return null;
        }

        return new Date(createTimeMillis);
    }

    public Date getLastAccessTime() {
        if (lastAccessTimeMillis < 0L) {
            return null;
        }

        return new Date(lastAccessTimeMillis);
    }

    public long getJdbcExecuteTimeMillis() {
        return getJdbcExecuteTimeNano() / (1000 * 1000);
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
