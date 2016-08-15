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

public class WebRequestStat {

    private long startNano;
    private long startMillis;
    private long endNano;
    private long jdbcCommitCount;
    private long jdbcRollbackCount;
    private long jdbcExecuteCount;
    private long jdbcUpdateCount;
    private long jdbcFetchRowCount;
    private long jdbcExecuteNano;
    private long jdbcExecuteErrorCount;

    private long jdbcPoolConnectCount;
    private long jdbcPoolCloseCount;

    private long jdbcResultSetOpenCount;
    private long jdbcResultSetCloseCount;

    public WebRequestStat(){

    }

    public WebRequestStat(long startNano){
        this.startNano = startNano;
    }

    public WebRequestStat(long startNano, long startMillis){
        this.startNano = startNano;
        this.startMillis = startMillis;
    }

    private static ThreadLocal<WebRequestStat> localRequestStat = new ThreadLocal<WebRequestStat>();

    public static WebRequestStat current() {
        return localRequestStat.get();
    }

    public static void set(WebRequestStat requestStat) {
        localRequestStat.set(requestStat);
    }

    public long getStartNano() {
        return startNano;
    }

    public void setStartNano(long startNano) {
        this.startNano = startNano;
    }

    public long getStartMillis() {
        return startMillis;
    }

    public void setStartMillis(long startMillis) {
        this.startMillis = startMillis;
    }

    public long getEndNano() {
        return endNano;
    }

    public void setEndNano(long endNano) {
        this.endNano = endNano;
    }

    public void addJdbcUpdateCount(long count) {
        this.jdbcUpdateCount += count;
    }

    public void addJdbcFetchRowCount(long count) {
        this.jdbcFetchRowCount += count;
    }

    public void incrementJdbcExecuteCount() {
        this.jdbcExecuteCount++;
    }

    public long getJdbcExecuteCount() {
        return jdbcExecuteCount;
    }

    public long getJdbcExecuteTimeNano() {
        return jdbcExecuteNano;
    }

    public void addJdbcExecuteTimeNano(long nano) {
        jdbcExecuteNano += nano;
    }

    public long getJdbcUpdateCount() {
        return jdbcUpdateCount;
    }

    public long getJdbcFetchRowCount() {
        return jdbcFetchRowCount;
    }

    public long getJdbcCommitCount() {
        return jdbcCommitCount;
    }

    public void incrementJdbcCommitCount() {
        this.jdbcCommitCount++;
    }

    public long getJdbcRollbackCount() {
        return jdbcRollbackCount;
    }

    public void incrementJdbcRollbackCount() {
        this.jdbcRollbackCount++;
    }

    public long getJdbcExecuteErrorCount() {
        return jdbcExecuteErrorCount;
    }

    public void incrementJdbcExecuteErrorCount() {
        jdbcExecuteErrorCount++;
    }

    public long getJdbcPoolConnectionOpenCount() {
        return jdbcPoolConnectCount;
    }

    public void incrementJdbcPoolConnectCount() {
        this.jdbcPoolConnectCount++;
    }

    public long getJdbcPoolConnectionCloseCount() {
        return jdbcPoolCloseCount;
    }

    public void incrementJdbcPoolCloseCount() {
        this.jdbcPoolCloseCount++;
    }

    public long getJdbcResultSetOpenCount() {
        return jdbcResultSetOpenCount;
    }

    public void incrementJdbcResultSetOpenCount() {
        this.jdbcResultSetOpenCount++;
    }

    public long getJdbcResultSetCloseCount() {
        return jdbcResultSetCloseCount;
    }

    public void incrementJdbcResultSetCloseCount() {
        this.jdbcResultSetCloseCount++;
    }

}
