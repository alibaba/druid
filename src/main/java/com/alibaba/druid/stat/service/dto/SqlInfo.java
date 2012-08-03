package com.alibaba.druid.stat.service.dto;

public class SqlInfo {

    private String sql;
    private int    executeCount;
    private int    runningCount;
    private int    concurrentMax;
    private int    errorCount;
    private int    inTransactionCount;

    private long   fetchRowCount;
    private long   updateCount;

    private long   resultSetHoldTimeMilis;

    // 使用字段而不是数组为了节省内存空间
    private int    histogram_0;
    private int    histogram_1;
    private int    histogram_2;
    private int    histogram_3;
    private int    histogram_4;
    private int    histogram_5;
    private int    histogram_6;
    private int    histogram_7;

    private int    executeAndResultHoldTimeHistogram_0;
    private int    executeAndResultHoldTimeHistogram_1;
    private int    executeAndResultHoldTimeHistogram_2;
    private int    executeAndResultHoldTimeHistogram_3;
    private int    executeAndResultHoldTimeHistogram_4;
    private int    executeAndResultHoldTimeHistogram_5;
    private int    executeAndResultHoldTimeHistogram_6;
    private int    executeAndResultHoldTimeHistogram_7;

    private int    updateCountHistogram_0;
    private int    updateCountHistogram_1;
    private int    updateCountHistogram_2;
    private int    updateCountHistogram_3;
    private int    updateCountHistogram_4;
    private int    updateCountHistogram_5;

    private int    fetchRowCountHistogram_0;
    private int    fetchRowCountHistogram_1;
    private int    fetchRowCountHistogram_2;
    private int    fetchRowCountHistogram_3;
    private int    fetchRowCountHistogram_4;
    private int    fetchRowCountHistogram_5;

    public SqlInfo(){

    }

    public void setExecuteAndResultHoldTimeHistogram(long[] values) {
        executeAndResultHoldTimeHistogram_0 = (int) values[0];
        executeAndResultHoldTimeHistogram_1 = (int) values[1];
        executeAndResultHoldTimeHistogram_2 = (int) values[2];
        executeAndResultHoldTimeHistogram_3 = (int) values[3];
        executeAndResultHoldTimeHistogram_4 = (int) values[4];
        executeAndResultHoldTimeHistogram_5 = (int) values[5];
        executeAndResultHoldTimeHistogram_6 = (int) values[6];
        executeAndResultHoldTimeHistogram_7 = (int) values[7];
    }

    public int[] getExecuteAndResultHoldTimeHistogram() {
        return new int[] { executeAndResultHoldTimeHistogram_0, //
                executeAndResultHoldTimeHistogram_1, //
                executeAndResultHoldTimeHistogram_2, //
                executeAndResultHoldTimeHistogram_3, //
                executeAndResultHoldTimeHistogram_4, //
                executeAndResultHoldTimeHistogram_5, //
                executeAndResultHoldTimeHistogram_6, //
                executeAndResultHoldTimeHistogram_7, //
        };
    }

    public void setFetchRowCountHistogram(long[] values) {
        fetchRowCountHistogram_0 = (int) values[0];
        fetchRowCountHistogram_1 = (int) values[1];
        fetchRowCountHistogram_2 = (int) values[2];
        fetchRowCountHistogram_3 = (int) values[3];
        fetchRowCountHistogram_4 = (int) values[4];
        fetchRowCountHistogram_5 = (int) values[5];
    }

    public int[] getFetchRowCountHistogram() {
        return new int[] { fetchRowCountHistogram_0, //
                fetchRowCountHistogram_1, //
                fetchRowCountHistogram_2, //
                fetchRowCountHistogram_3, //
                fetchRowCountHistogram_4, //
                fetchRowCountHistogram_5, //
        };
    }

    public void setUpdateCountHistogram(long[] values) {
        updateCountHistogram_0 = (int) values[0];
        updateCountHistogram_1 = (int) values[1];
        updateCountHistogram_2 = (int) values[2];
        updateCountHistogram_3 = (int) values[3];
        updateCountHistogram_4 = (int) values[4];
        updateCountHistogram_5 = (int) values[5];
    }

    public int[] getUpdateCountHistogram() {
        return new int[] { updateCountHistogram_0, //
                updateCountHistogram_1, //
                updateCountHistogram_2, //
                updateCountHistogram_3, //
                updateCountHistogram_4, //
                updateCountHistogram_5, //
        };
    }

    public void setHisogram(long[] values) {
        histogram_0 = (int) values[0];
        histogram_1 = (int) values[1];
        histogram_2 = (int) values[2];
        histogram_3 = (int) values[3];
        histogram_4 = (int) values[4];
        histogram_5 = (int) values[5];
        histogram_6 = (int) values[6];
        histogram_7 = (int) values[7];
    }

    public int[] getHistogram() {
        return new int[] { histogram_0, //
                histogram_1, //
                histogram_2, //
                histogram_3, //
                histogram_4, //
                histogram_5, //
                histogram_6, //
                histogram_7, //
        };
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

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
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

    public long getResultSetHoldTimeMilis() {
        return resultSetHoldTimeMilis;
    }

    public void setResultSetHoldTimeMilis(long resultSetHoldTimeMilis) {
        this.resultSetHoldTimeMilis = resultSetHoldTimeMilis;
    }
    
    public void merge(SqlInfo sqlInfo) {
        
    }
}
