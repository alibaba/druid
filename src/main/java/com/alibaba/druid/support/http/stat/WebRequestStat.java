package com.alibaba.druid.support.http.stat;

public class WebRequestStat {

    private long startNano;
    private long endNano;
    private long jdbcCommitCount;
    private long jdbcRollbackCount;
    private long jdbcExecuteCount;
    private long jdbcUpdateCount;
    private long jdbcFetchRowCount;
    private long jdbcExecuteNano;

    public WebRequestStat(){

    }

    public WebRequestStat(long startNano){
        this.startNano = startNano;
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

    public long getJdbcExecuteNano() {
        return jdbcExecuteNano;
    }
    
    public void addJdbcExecuteNano(long nano) {
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

}
