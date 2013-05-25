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
package com.alibaba.druid.stat;

public class JdbcSqlStatValue {

    protected String    sql;
    protected long      id;
    protected String    dataSource;
    protected long      executeLastStartTime;

    protected long      executeBatchSizeTotal;
    protected int       executeBatchSizeMax;

    protected long      executeSuccessCount;
    protected long      executeSpanNanoTotal;
    protected long      executeSpanNanoMax;
    protected int       runningCount;
    protected int       concurrentMax;
    protected long      resultSetHoldTimeNano;
    protected long      executeAndResultSetHoldTime;

    protected String    name;
    protected String    file;
    protected String    dbType;

    protected long      executeNanoSpanMaxOccurTime;

    protected long      executeErrorCount;
    protected Throwable executeErrorLast;
    protected long      executeErrorLastTime;

    protected long      updateCount;
    protected long      updateCountMax;
    protected long      fetchRowCount;
    protected long      fetchRowCountMax;

    protected long      inTransactionCount;

    protected String    lastSlowParameters;

    protected long      clobOpenCount;
    protected long      blobOpenCount;
    protected long      readStringLength;
    protected long      readBytesLength;

    protected long      inputStreamOpenCount;
    protected long      readerOpenCount;

    protected long      histogram_0_1;
    protected long      histogram_1_10;
    protected int       histogram_10_100;
    protected int       histogram_100_1000;
    protected int       histogram_1000_10000;
    protected int       histogram_10000_100000;
    protected int       histogram_100000_1000000;
    protected int       histogram_1000000_more;

    public long[] getExecuteHistogram() {
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

    public long executeAndResultHoldTime_0_1;
    public long executeAndResultHoldTime_1_10;
    public int  executeAndResultHoldTime_10_100;
    public int  executeAndResultHoldTime_100_1000;
    public int  executeAndResultHoldTime_1000_10000;
    public int  executeAndResultHoldTime_10000_100000;
    public int  executeAndResultHoldTime_100000_1000000;
    public int  executeAndResultHoldTime_1000000_more;

    public long[] getExecuteAndResultHoldHistogram() {
        return new long[] { executeAndResultHoldTime_0_1, //
                executeAndResultHoldTime_1_10, //
                executeAndResultHoldTime_10_100, //
                executeAndResultHoldTime_100_1000, //
                executeAndResultHoldTime_1000_10000, //
                executeAndResultHoldTime_10000_100000, //
                executeAndResultHoldTime_100000_1000000, //
                executeAndResultHoldTime_1000000_more, //
        };
    }

    public long fetchRowCount_0_1;
    public long fetchRowCount_1_10;
    public long fetchRowCount_10_100;
    public int  fetchRowCount_100_1000;
    public int  fetchRowCount_1000_10000;
    public int  fetchRowCount_10000_more;

    public long[] getFetchRowHistogram() {
        return new long[] { fetchRowCount_0_1, //
                fetchRowCount_1_10, //
                fetchRowCount_10_100, //
                fetchRowCount_100_1000, //
                fetchRowCount_1000_10000, //
                fetchRowCount_10000_more, //
        };
    }

    public long updateCount_0_1;
    public long updateCount_1_10;
    public long updateCount_10_100;
    public int  updateCount_100_1000;
    public int  updateCount_1000_10000;
    public int  updateCount_10000_more;

    public long[] getUpdateHistogram() {
        return new long[] { updateCount_0_1, //
                updateCount_1_10, //
                updateCount_10_100, //
                updateCount_100_1000, //
                updateCount_1000_10000, //
                updateCount_10000_more, //
        };
    }

    public long getExecuteCount() {
        return executeErrorCount + executeSuccessCount;
    }

    public long getExecuteMillisMax() {
        return executeSpanNanoMax / (1000 * 1000);
    }

    public long getExecuteMillisTotal() {
        return executeSpanNanoTotal / (1000 * 1000);
    }

    public String getSql() {
        return sql;
    }

    public void setSql(String sql) {
        this.sql = sql;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    public long getExecuteLastStartTime() {
        return executeLastStartTime;
    }

    public void setExecuteLastStartTime(long executeLastStartTime) {
        this.executeLastStartTime = executeLastStartTime;
    }

    public long getExecuteBatchSizeTotal() {
        return executeBatchSizeTotal;
    }

    public void setExecuteBatchSizeTotal(long executeBatchSizeTotal) {
        this.executeBatchSizeTotal = executeBatchSizeTotal;
    }

    public int getExecuteBatchSizeMax() {
        return executeBatchSizeMax;
    }

    public void setExecuteBatchSizeMax(int executeBatchSizeMax) {
        this.executeBatchSizeMax = executeBatchSizeMax;
    }

    public long getExecuteSuccessCount() {
        return executeSuccessCount;
    }

    public void setExecuteSuccessCount(long executeSuccessCount) {
        this.executeSuccessCount = executeSuccessCount;
    }

    public long getExecuteSpanNanoTotal() {
        return executeSpanNanoTotal;
    }

    public void setExecuteSpanNanoTotal(long executeSpanNanoTotal) {
        this.executeSpanNanoTotal = executeSpanNanoTotal;
    }

    public long getExecuteSpanNanoMax() {
        return executeSpanNanoMax;
    }

    public void setExecuteSpanNanoMax(long executeSpanNanoMax) {
        this.executeSpanNanoMax = executeSpanNanoMax;
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

    public long getResultSetHoldTimeNano() {
        return resultSetHoldTimeNano;
    }

    public void setResultSetHoldTimeNano(long resultSetHoldTimeNano) {
        this.resultSetHoldTimeNano = resultSetHoldTimeNano;
    }

    public long getExecuteAndResultSetHoldTime() {
        return executeAndResultSetHoldTime;
    }

    public void setExecuteAndResultSetHoldTime(long executeAndResultSetHoldTime) {
        this.executeAndResultSetHoldTime = executeAndResultSetHoldTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public long getExecuteNanoSpanMaxOccurTime() {
        return executeNanoSpanMaxOccurTime;
    }

    public void setExecuteNanoSpanMaxOccurTime(long executeNanoSpanMaxOccurTime) {
        this.executeNanoSpanMaxOccurTime = executeNanoSpanMaxOccurTime;
    }

    public long getExecuteErrorCount() {
        return executeErrorCount;
    }

    public void setExecuteErrorCount(long executeErrorCount) {
        this.executeErrorCount = executeErrorCount;
    }

    public Throwable getExecuteErrorLast() {
        return executeErrorLast;
    }

    public void setExecuteErrorLast(Throwable executeErrorLast) {
        this.executeErrorLast = executeErrorLast;
    }

    public long getExecuteErrorLastTime() {
        return executeErrorLastTime;
    }

    public void setExecuteErrorLastTime(long executeErrorLastTime) {
        this.executeErrorLastTime = executeErrorLastTime;
    }

    public long getUpdateCount() {
        return updateCount;
    }

    public void setUpdateCount(long updateCount) {
        this.updateCount = updateCount;
    }

    public long getUpdateCountMax() {
        return updateCountMax;
    }

    public void setUpdateCountMax(long updateCountMax) {
        this.updateCountMax = updateCountMax;
    }

    public long getFetchRowCount() {
        return fetchRowCount;
    }

    public void setFetchRowCount(long fetchRowCount) {
        this.fetchRowCount = fetchRowCount;
    }

    public long getFetchRowCountMax() {
        return fetchRowCountMax;
    }

    public void setFetchRowCountMax(long fetchRowCountMax) {
        this.fetchRowCountMax = fetchRowCountMax;
    }

    public long getInTransactionCount() {
        return inTransactionCount;
    }

    public void setInTransactionCount(long inTransactionCount) {
        this.inTransactionCount = inTransactionCount;
    }

    public String getLastSlowParameters() {
        return lastSlowParameters;
    }

    public void setLastSlowParameters(String lastSlowParameters) {
        this.lastSlowParameters = lastSlowParameters;
    }

    public long getClobOpenCount() {
        return clobOpenCount;
    }

    public void setClobOpenCount(long clobOpenCount) {
        this.clobOpenCount = clobOpenCount;
    }

    public long getBlobOpenCount() {
        return blobOpenCount;
    }

    public void setBlobOpenCount(long blobOpenCount) {
        this.blobOpenCount = blobOpenCount;
    }

    public long getReadStringLength() {
        return readStringLength;
    }

    public void setReadStringLength(long readStringLength) {
        this.readStringLength = readStringLength;
    }

    public long getReadBytesLength() {
        return readBytesLength;
    }

    public void setReadBytesLength(long readBytesLength) {
        this.readBytesLength = readBytesLength;
    }

    public long getInputStreamOpenCount() {
        return inputStreamOpenCount;
    }

    public void setInputStreamOpenCount(long inputStreamOpenCount) {
        this.inputStreamOpenCount = inputStreamOpenCount;
    }

    public long getReaderOpenCount() {
        return readerOpenCount;
    }

    public void setReaderOpenCount(long readerOpenCount) {
        this.readerOpenCount = readerOpenCount;
    }

}
