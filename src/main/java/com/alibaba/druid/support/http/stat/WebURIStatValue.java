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

import com.alibaba.druid.support.monitor.annotation.AggregateType;
import com.alibaba.druid.support.monitor.annotation.MField;
import com.alibaba.druid.support.monitor.annotation.MTable;
import com.alibaba.druid.support.profile.ProfileEntryStatValue;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@MTable(name = "druid_weburi")
public class WebURIStatValue {

    @MField(groupBy = true, aggregate = AggregateType.None)
    protected String                    uri;

    @MField(aggregate = AggregateType.Last)
    protected int                       runningCount;

    @MField(aggregate = AggregateType.Max)
    protected int                       concurrentMax;

    @MField(aggregate = AggregateType.Sum)
    protected long                      requestCount;

    @MField(aggregate = AggregateType.Sum)
    protected long                      requestTimeNano;

    @MField(aggregate = AggregateType.Max)
    protected long                      requestTimeNanoMax;

    @MField(aggregate = AggregateType.Last)
    protected Date                      requestTimeNanoMaxOccurTime;

    @MField(aggregate = AggregateType.Sum)
    protected long                      jdbcFetchRowCount;

    @MField(aggregate = AggregateType.Max)
    protected long                      jdbcFetchRowPeak;

    @MField(aggregate = AggregateType.Sum)
    protected long                      jdbcUpdateCount;

    @MField(aggregate = AggregateType.Max)
    protected long                      jdbcUpdatePeak;

    @MField(aggregate = AggregateType.Sum)
    protected long                      jdbcExecuteCount;

    @MField(aggregate = AggregateType.Sum)
    protected long                      jdbcExecuteErrorCount;

    @MField(aggregate = AggregateType.Max)
    protected long                      jdbcExecutePeak;             // 单次请求执行SQL次数的峰值

    @MField(aggregate = AggregateType.Sum)
    protected long                      jdbcExecuteTimeNano;

    @MField(aggregate = AggregateType.Sum)
    protected long                      jdbcCommitCount;

    @MField(aggregate = AggregateType.Sum)
    protected long                      jdbcRollbackCount;

    @MField(aggregate = AggregateType.Sum)
    protected long                      jdbcPoolConnectionOpenCount;

    @MField(aggregate = AggregateType.Sum)
    protected long                      jdbcPoolConnectionCloseCount;

    @MField(aggregate = AggregateType.Sum)
    protected long                      jdbcResultSetOpenCount;

    @MField(aggregate = AggregateType.Sum)
    protected long                      jdbcResultSetCloseCount;

    @MField(aggregate = AggregateType.Sum)
    protected long                      errorCount;

    @MField(aggregate = AggregateType.Last)
    protected Date                      lastAccessTime = null;

    private List<ProfileEntryStatValue> profileEntryStatValueList;

    @MField(name = "h1", aggregate = AggregateType.Sum)
    protected long                      histogram_0_1;

    @MField(name = "h10", aggregate = AggregateType.Sum)
    protected long                      histogram_1_10;

    @MField(name = "h100", aggregate = AggregateType.Sum)
    protected long                      histogram_10_100;
    @MField(name = "h1000", aggregate = AggregateType.Sum)
    protected long                      histogram_100_1000;

    @MField(name = "h10000", aggregate = AggregateType.Sum)
    protected int                       histogram_1000_10000;

    @MField(name = "h100000", aggregate = AggregateType.Sum)
    protected int                       histogram_10000_100000;

    @MField(name = "h1000000", aggregate = AggregateType.Sum)
    protected int                       histogram_100000_1000000;

    @MField(name = "hmore", aggregate = AggregateType.Sum)
    protected int                       histogram_1000000_more;

    public long[] getHistogram() {
        return new long[] { histogram_0_1, //
                histogram_1_10, //
                histogram_10_100, //
                histogram_100_1000, //
                histogram_1000_10000, //
                histogram_10000_100000, //
                histogram_100000_1000000, //
                histogram_1000000_more, //
        };
    }

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

    public long getRequestTimeNanoMax() {
        return requestTimeNanoMax;
    }

    public void setRequestTimeNanoMax(long requestTimeNanoMax) {
        this.requestTimeNanoMax = requestTimeNanoMax;
    }

    public Date getRequestTimeNanoMaxOccurTime() {
        return requestTimeNanoMaxOccurTime;
    }

    public void setRequestTimeNanoMaxOccurTime(long requestTimeNanoMaxOccurTime) {
        if (requestTimeNanoMaxOccurTime > 0) {
            this.requestTimeNanoMaxOccurTime = new Date(requestTimeNanoMaxOccurTime);
        } else {
            this.requestTimeNanoMaxOccurTime = null;
        }
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

    public void setLastAccessTimeMillis(long lastAccessTimeMillis) {
        if (lastAccessTimeMillis > 0) {
            this.lastAccessTime = new Date(lastAccessTimeMillis);
        } else {
            this.lastAccessTime = null;
        }
    }

    public long getRequestTimeMillis() {
        return getRequestTimeNano() / (1000 * 1000);
    }

    public long getRequestTimeMillisMax() {
        return getRequestTimeNanoMax() / (1000 * 1000);
    }

    public Date getLastAccessTime() {
        return lastAccessTime;
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
        data.put("Histogram", this.getHistogram());

        if (this.profileEntryStatValueList != null) {
            int size = profileEntryStatValueList.size();
            List<Map<String, Object>> profileDataList = new ArrayList<Map<String, Object>>(size);
            for (ProfileEntryStatValue profileEntryStatValue : profileEntryStatValueList) {
                profileDataList.add(profileEntryStatValue.getData());
            }
            data.put("Profiles", profileDataList);
        }

        data.put("RequestTimeMillisMax", this.getRequestTimeMillisMax());
        data.put("RequestTimeMillisMaxOccurTime", this.getRequestTimeNanoMaxOccurTime());

        return data;
    }
}
