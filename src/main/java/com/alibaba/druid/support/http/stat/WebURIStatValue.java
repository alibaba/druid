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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.support.profile.ProfileEntryStatValue;

public class WebURIStatValue {

    protected String                    uri;

    protected int                       runningCount;
    protected int                       concurrentMax;
    protected long                      requestCount;
    protected long                      requestTimeNano;

    protected long                      jdbcFetchRowCount;
    protected long                      jdbcFetchRowPeak;

    protected long                      jdbcUpdateCount;
    protected long                      jdbcUpdatePeak;

    protected long                      jdbcExecuteCount;
    protected long                      jdbcExecuteErrorCount;
    protected long                      jdbcExecutePeak;             // 单次请求执行SQL次数的峰值
    protected long                      jdbcExecuteTimeNano;

    protected long                      jdbcCommitCount;
    protected long                      jdbcRollbackCount;

    protected long                      jdbcPoolConnectionOpenCount;
    protected long                      jdbcPoolConnectionCloseCount;

    protected long                      jdbcResultSetOpenCount;
    protected long                      jdbcResultSetCloseCount;

    protected long                      errorCount;

    protected long                      lastAccessTimeMillis = -1L;

    private List<ProfileEntryStatValue> profileEntryStatValueList;

    public List<ProfileEntryStatValue> getProfileEntryStatValueList() {
        return profileEntryStatValueList;
    }

    public void setProfileEntryStatValueList(List<ProfileEntryStatValue> profileEntryStatValueList) {
        this.profileEntryStatValueList = profileEntryStatValueList;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
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

    public long getJdbcFetchRowPeak() {
        return jdbcFetchRowPeak;
    }

    public void setJdbcFetchRowPeak(long jdbcFetchRowPeak) {
        this.jdbcFetchRowPeak = jdbcFetchRowPeak;
    }

    public long getJdbcUpdateCount() {
        return jdbcUpdateCount;
    }

    public void setJdbcUpdateCount(long jdbcUpdateCount) {
        this.jdbcUpdateCount = jdbcUpdateCount;
    }

    public long getJdbcUpdatePeak() {
        return jdbcUpdatePeak;
    }

    public void setJdbcUpdatePeak(long jdbcUpdatePeak) {
        this.jdbcUpdatePeak = jdbcUpdatePeak;
    }

    public long getJdbcExecuteCount() {
        return jdbcExecuteCount;
    }

    public void setJdbcExecuteCount(long jdbcExecuteCount) {
        this.jdbcExecuteCount = jdbcExecuteCount;
    }

    public long getJdbcExecuteErrorCount() {
        return jdbcExecuteErrorCount;
    }

    public void setJdbcExecuteErrorCount(long jdbcExecuteErrorCount) {
        this.jdbcExecuteErrorCount = jdbcExecuteErrorCount;
    }

    public long getJdbcExecutePeak() {
        return jdbcExecutePeak;
    }

    public void setJdbcExecutePeak(long jdbcExecutePeak) {
        this.jdbcExecutePeak = jdbcExecutePeak;
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

    public long getJdbcPoolConnectionOpenCount() {
        return jdbcPoolConnectionOpenCount;
    }

    public void setJdbcPoolConnectionOpenCount(long jdbcPoolConnectionOpenCount) {
        this.jdbcPoolConnectionOpenCount = jdbcPoolConnectionOpenCount;
    }

    public long getJdbcPoolConnectionCloseCount() {
        return jdbcPoolConnectionCloseCount;
    }

    public void setJdbcPoolConnectionCloseCount(long jdbcPoolConnectionCloseCount) {
        this.jdbcPoolConnectionCloseCount = jdbcPoolConnectionCloseCount;
    }

    public long getJdbcResultSetOpenCount() {
        return jdbcResultSetOpenCount;
    }

    public void setJdbcResultSetOpenCount(long jdbcResultSetOpenCount) {
        this.jdbcResultSetOpenCount = jdbcResultSetOpenCount;
    }

    public long getJdbcResultSetCloseCount() {
        return jdbcResultSetCloseCount;
    }

    public void setJdbcResultSetCloseCount(long jdbcResultSetCloseCount) {
        this.jdbcResultSetCloseCount = jdbcResultSetCloseCount;
    }

    public long getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(long errorCount) {
        this.errorCount = errorCount;
    }

    public long getLastAccessTimeMillis() {
        return lastAccessTimeMillis;
    }

    public void setLastAccessTimeMillis(long lastAccessTimeMillis) {
        this.lastAccessTimeMillis = lastAccessTimeMillis;
    }

    public long getRequestTimeMillis() {
        return getRequestTimeNano() / (1000 * 1000);
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

        if (this.profileEntryStatValueList != null) {
            int size = profileEntryStatValueList.size();
            List<Map<String, Object>> profileDataList = new ArrayList<Map<String, Object>>(size);
            for (int i = 0; i < size; ++i) {
                profileDataList.add(profileEntryStatValueList.get(i).getData());
            }
            data.put("Profiles", profileDataList);
        }

        return data;
    }
}
