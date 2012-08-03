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

    private int[]  histogram;

    private int[]  executeAndResultHoldTimeHistogram;

    private int[]  updateCountHistogram;

    private int[]  fetchRowCountHistogram;

    public SqlInfo(){

    }

    public void setExecuteAndResultHoldTimeHistogram(long[] values) {
        executeAndResultHoldTimeHistogram = new int[values.length];
        for (int i = 0; i < values.length; ++i) {
            executeAndResultHoldTimeHistogram[i] = (int) values[i];
        }
    }

    public int[] getExecuteAndResultHoldTimeHistogram() {
        return executeAndResultHoldTimeHistogram;
    }

    public void setFetchRowCountHistogram(long[] values) {
        fetchRowCountHistogram = new int[values.length];
        for (int i = 0; i < values.length; ++i) {
            fetchRowCountHistogram[i] = (int) values[i];
        }
    }

    public int[] getFetchRowCountHistogram() {
        return fetchRowCountHistogram;
    }

    public void setUpdateCountHistogram(long[] values) {
        updateCountHistogram = new int[values.length];
        for (int i = 0; i < values.length; ++i) {
            updateCountHistogram[i] = (int) values[i];
        }
    }

    public int[] getUpdateCountHistogram() {
        return updateCountHistogram;
    }

    public void setHisogram(long[] values) {
        histogram = new int[values.length];
        for (int i = 0; i < values.length; ++i) {
            histogram[i] = (int) values[i];
        }
    }

    public int[] getHistogram() {
        return histogram;
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

    public void merge(SqlInfo o) {
        executeCount += o.executeCount;
        runningCount += o.runningCount;
        if (o.concurrentMax > concurrentMax) {
            concurrentMax = o.concurrentMax;
        }
        errorCount += o.errorCount;
        inTransactionCount += o.inTransactionCount;

        fetchRowCount += o.fetchRowCount;
        updateCount += o.updateCount;

        resultSetHoldTimeMilis += o.resultSetHoldTimeMilis;

        for (int i = 0; i < histogram.length; ++i) {
            this.histogram[i] += o.histogram[i];
        }

        for (int i = 0; i < executeAndResultHoldTimeHistogram.length; ++i) {
            this.executeAndResultHoldTimeHistogram[i] += o.executeAndResultHoldTimeHistogram[i];
        }

        for (int i = 0; i < updateCountHistogram.length; ++i) {
            this.updateCountHistogram[i] += o.updateCountHistogram[i];
        }

        for (int i = 0; i < fetchRowCountHistogram.length; ++i) {
            this.fetchRowCountHistogram[i] += o.fetchRowCountHistogram[i];
        }
    }
}
