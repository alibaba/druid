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
package com.alibaba.druid.support.spring.stat;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import com.alibaba.druid.support.monitor.annotation.AggregateType;
import com.alibaba.druid.support.monitor.annotation.MField;
import com.alibaba.druid.support.monitor.annotation.MTable;
import com.alibaba.druid.util.Utils;

@MTable(name = "druid_springmethod")
public class SpringMethodStatValue {

    @MField(groupBy = true, aggregate=AggregateType.None)
    private String className;

    @MField(groupBy = true, aggregate=AggregateType.None)
    private String signature;

    @MField(aggregate = AggregateType.Last)
    private int    runningCount;

    @MField(aggregate = AggregateType.Max)
    private int    concurrentMax;

    @MField(aggregate = AggregateType.Sum)
    private long   executeCount;

    @MField(aggregate = AggregateType.Sum)
    private long   executeErrorCount;

    @MField(aggregate = AggregateType.Sum)
    private long   executeTimeNano;

    @MField(aggregate = AggregateType.Sum)
    private long   jdbcFetchRowCount;

    @MField(aggregate = AggregateType.Sum)
    private long   jdbcUpdateCount;

    @MField(aggregate = AggregateType.Sum)
    private long   jdbcExecuteCount;

    @MField(aggregate = AggregateType.Sum)
    private long   jdbcExecuteErrorCount;

    @MField(aggregate = AggregateType.Sum)
    private long   jdbcExecuteTimeNano;

    @MField(aggregate = AggregateType.Sum)
    private long   jdbcCommitCount;

    @MField(aggregate = AggregateType.Sum)
    private long   jdbcRollbackCount;

    @MField(aggregate = AggregateType.Sum)
    private long   jdbcPoolConnectionOpenCount;

    @MField(aggregate = AggregateType.Sum)
    private long   jdbcPoolConnectionCloseCount;

    @MField(aggregate = AggregateType.Sum)
    private long   jdbcResultSetOpenCount;

    @MField(aggregate = AggregateType.Sum)
    private long   jdbcResultSetCloseCount;

    @MField(aggregate = AggregateType.Last)
    private String lastErrorClass;

    @MField(aggregate = AggregateType.Last)
    private String lastErrorMessage;

    @MField(aggregate = AggregateType.Last)
    private String lastErrorStackTrace;

    @MField(aggregate = AggregateType.Last)
    private long   lastErrorTimeMillis;

    @MField(name = "h1", aggregate=AggregateType.Sum)
    long           histogram_0_1;

    @MField(name = "h10", aggregate=AggregateType.Sum)
    long           histogram_1_10;

    @MField(name = "h100", aggregate=AggregateType.Sum)
    long           histogram_10_100;

    @MField(name = "h1000", aggregate=AggregateType.Sum)
    long           histogram_100_1000;

    @MField(name = "h10000", aggregate=AggregateType.Sum)
    int            histogram_1000_10000;

    @MField(name = "h100000", aggregate=AggregateType.Sum)
    int            histogram_10000_100000;

    @MField(name = "h1000000", aggregate=AggregateType.Sum)
    int            histogram_100000_1000000;

    @MField(name = "hmore", aggregate=AggregateType.Sum)
    int            histogram_1000000_more;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
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

    public long getExecuteCount() {
        return executeCount;
    }

    public void setExecuteCount(long executeCount) {
        this.executeCount = executeCount;
    }

    public long getExecuteErrorCount() {
        return executeErrorCount;
    }

    public void setExecuteErrorCount(long executeErrorCount) {
        this.executeErrorCount = executeErrorCount;
    }

    public long getExecuteTimeNano() {
        return executeTimeNano;
    }

    public void setExecuteTimeNano(long executeTimeNano) {
        this.executeTimeNano = executeTimeNano;
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

    public long getJdbcExecuteErrorCount() {
        return jdbcExecuteErrorCount;
    }

    public void setJdbcExecuteErrorCount(long jdbcExecuteErrorCount) {
        this.jdbcExecuteErrorCount = jdbcExecuteErrorCount;
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

    public void setLastError(Throwable lastError) {
        if (lastError != null) {
            lastErrorClass = lastError.getClass().getName();
            lastErrorMessage = lastError.getMessage();
            lastErrorStackTrace = Utils.toString(lastError.getStackTrace());
        }
    }

    public long getLastErrorTimeMillis() {
        return lastErrorTimeMillis;
    }

    public void setLastErrorTimeMillis(long lastErrorTimeMillis) {
        this.lastErrorTimeMillis = lastErrorTimeMillis;
    }

    public long getExecuteTimeMillis() {
        return getExecuteTimeNano() / (1000 * 1000);
    }

    public long getJdbcExecuteTimeMillis() {
        return getJdbcExecuteTimeNano() / (1000 * 1000);
    }

    public Date getLastErrorTime() {
        if (lastErrorTimeMillis <= 0) {
            return null;
        }

        return new Date(lastErrorTimeMillis);
    }

    public long[] getHistogram() {
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

    public Map<String, Object> getData() {
        Map<String, Object> data = new LinkedHashMap<String, Object>();

        data.put("Class", this.getClassName());
        data.put("Method", this.getSignature());

        data.put("RunningCount", this.getRunningCount());
        data.put("ConcurrentMax", this.getConcurrentMax());
        data.put("ExecuteCount", this.getExecuteCount());
        data.put("ExecuteErrorCount", this.getExecuteErrorCount());
        data.put("ExecuteTimeMillis", this.getExecuteTimeMillis());

        data.put("JdbcCommitCount", this.getJdbcCommitCount());
        data.put("JdbcRollbackCount", this.getJdbcRollbackCount());

        data.put("JdbcPoolConnectionOpenCount", this.getJdbcPoolConnectionOpenCount());
        data.put("JdbcPoolConnectionCloseCount", this.getJdbcPoolConnectionCloseCount());

        data.put("JdbcResultSetOpenCount", this.getJdbcResultSetOpenCount());
        data.put("JdbcResultSetCloseCount", this.getJdbcResultSetCloseCount());

        data.put("JdbcExecuteCount", this.getJdbcExecuteCount());
        data.put("JdbcExecuteErrorCount", this.getJdbcExecuteErrorCount());
        data.put("JdbcExecuteTimeMillis", this.getJdbcExecuteTimeMillis());
        data.put("JdbcFetchRowCount", this.getJdbcFetchRowCount());
        data.put("JdbcUpdateCount", this.getJdbcUpdateCount());

        if (this.lastErrorClass == null) {
            data.put("LastError", null);
        } else {
            Map<String, Object> map = new LinkedHashMap<String, Object>(3);
            map.put("Class", lastErrorClass);
            map.put("Message", lastErrorMessage);
            map.put("StackTrace", lastErrorStackTrace);
            data.put("LastError", map);
        }

        data.put("LastErrorTime", this.getLastErrorTime());

        data.put("Histogram", this.getHistogram());

        return data;
    }
}
