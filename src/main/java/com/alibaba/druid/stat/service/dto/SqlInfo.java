package com.alibaba.druid.stat.service.dto;

public class SqlInfo {

    private String sql;
    private int    executeCount;
    private int    runningCount;
    private int    concurrentMax;
    private int    executeErrorCount;
    private int    inTransactionCount;

    private long   fetchRowCount;
    private long   updateCount;

    public SqlInfo(){

    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public int getExecuteCount() {
        return executeCount;
    }

    public void setExecuteCount(int executeCount) {
        this.executeCount = executeCount;
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

    public int getExecuteErrorCount() {
        return executeErrorCount;
    }

    public void setExecuteErrorCount(int executeErrorCount) {
        this.executeErrorCount = executeErrorCount;
    }

    public int getInTransactionCount() {
        return inTransactionCount;
    }

    public void setInTransactionCount(int inTransactionCount) {
        this.inTransactionCount = inTransactionCount;
    }

    public long getFetchRowCount() {
        return fetchRowCount;
    }

    public void setFetchRowCount(long fetchRowCount) {
        this.fetchRowCount = fetchRowCount;
    }

    public long getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(long updateCount) {
        this.updateCount = updateCount;
    }

}
