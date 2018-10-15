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
package com.alibaba.druid.stat;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.management.JMException;

import com.alibaba.druid.support.monitor.annotation.AggregateType;
import com.alibaba.druid.support.monitor.annotation.MField;
import com.alibaba.druid.support.monitor.annotation.MTable;
import com.alibaba.druid.util.JMXUtils;
import com.alibaba.druid.util.Utils;

@MTable(name = "druid_sql")
public class JdbcSqlStatValue {

    protected long      id;

    protected String    sql;

    @MField(groupBy = true, aggregate = AggregateType.None, hashFor = "sql", hashForType="sql")
    private long        sqlHash;

    @MField(aggregate = AggregateType.None)
    protected String    dataSource;

    @MField(name = "lastStartTime", aggregate = AggregateType.Max)
    protected long      executeLastStartTime;

    @MField(name = "batchTotal", aggregate = AggregateType.Sum)
    protected long      executeBatchSizeTotal;

    @MField(name = "batchToMax", aggregate = AggregateType.Max)
    protected int       executeBatchSizeMax;

    @MField(name = "execSuccessCount", aggregate = AggregateType.Sum)
    protected long      executeSuccessCount;

    @MField(name = "execNanoTotal", aggregate = AggregateType.Sum)
    protected long      executeSpanNanoTotal;

    @MField(name = "execNanoMax", aggregate = AggregateType.Sum)
    protected long      executeSpanNanoMax;

    @MField(name = "running", aggregate = AggregateType.Last)
    protected int       runningCount;

    @MField(aggregate = AggregateType.Max)
    protected int       concurrentMax;

    @MField(name = "rsHoldTime", aggregate = AggregateType.Sum)
    protected long      resultSetHoldTimeNano;

    @MField(name = "execRsHoldTime", aggregate = AggregateType.Sum)
    protected long      executeAndResultSetHoldTime;

    @MField(aggregate = AggregateType.None)
    protected String    name;

    @MField(aggregate = AggregateType.None)
    protected String    file;

    @MField(aggregate = AggregateType.None)
    protected String    dbType;

    @MField(name = "execNanoMaxOccurTime", aggregate = AggregateType.Max)
    protected long      executeNanoSpanMaxOccurTime;

    @MField(name = "errorCount", aggregate = AggregateType.Sum)
    protected long      executeErrorCount;

    protected Throwable executeErrorLast;

    @MField(name = "errorLastMsg", aggregate = AggregateType.Last)
    protected String    executeErrorLastMessage;

    @MField(name = "errorLastClass", aggregate = AggregateType.Last)
    protected String    executeErrorLastClass;

    @MField(name = "errorLastStackTrace", aggregate = AggregateType.Last)
    protected String    executeErrorLastStackTrace;

    @MField(name = "errorLastTime", aggregate = AggregateType.Last)
    protected long      executeErrorLastTime;

    @MField(aggregate = AggregateType.Sum)
    protected long      updateCount;

    @MField(aggregate = AggregateType.Sum)
    protected long      updateCountMax;

    @MField(aggregate = AggregateType.Sum)
    protected long      fetchRowCount;

    @MField(aggregate = AggregateType.Sum)
    protected long      fetchRowCountMax;

    @MField(name = "inTxnCount", aggregate = AggregateType.Sum)
    protected long      inTransactionCount;

    @MField(aggregate = AggregateType.Last)
    protected String    lastSlowParameters;

    @MField(aggregate = AggregateType.Sum)
    protected long      clobOpenCount;

    @MField(aggregate = AggregateType.Sum)
    protected long      blobOpenCount;

    @MField(aggregate = AggregateType.Sum)
    protected long      readStringLength;

    @MField(aggregate = AggregateType.Sum)
    protected long      readBytesLength;

    @MField(aggregate = AggregateType.Sum)
    protected long      inputStreamOpenCount;

    @MField(aggregate = AggregateType.Sum)
    protected long      readerOpenCount;

    @MField(name = "h1", aggregate = AggregateType.Sum)
    protected long      histogram_0_1;

    @MField(name = "h10", aggregate = AggregateType.Sum)
    protected long      histogram_1_10;

    @MField(name = "h100", aggregate = AggregateType.Sum)
    protected int       histogram_10_100;

    @MField(name = "h1000", aggregate = AggregateType.Sum)
    protected int       histogram_100_1000;

    @MField(name = "h10000", aggregate = AggregateType.Sum)
    protected int       histogram_1000_10000;

    @MField(name = "h100000", aggregate = AggregateType.Sum)
    protected int       histogram_10000_100000;

    @MField(name = "h1000000", aggregate = AggregateType.Sum)
    protected int       histogram_100000_1000000;

    @MField(name = "hmore", aggregate = AggregateType.Sum)
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

    @MField(name = "eh1", aggregate = AggregateType.Sum)
    protected long executeAndResultHoldTime_0_1;

    @MField(name = "eh10", aggregate = AggregateType.Sum)
    protected long executeAndResultHoldTime_1_10;

    @MField(name = "eh100", aggregate = AggregateType.Sum)
    protected int  executeAndResultHoldTime_10_100;

    @MField(name = "eh1000", aggregate = AggregateType.Sum)
    protected int  executeAndResultHoldTime_100_1000;

    @MField(name = "eh10000", aggregate = AggregateType.Sum)
    protected int  executeAndResultHoldTime_1000_10000;

    @MField(name = "eh100000", aggregate = AggregateType.Sum)
    protected int  executeAndResultHoldTime_10000_100000;

    @MField(name = "eh1000000", aggregate = AggregateType.Sum)
    protected int  executeAndResultHoldTime_100000_1000000;

    @MField(name = "ehmore", aggregate = AggregateType.Sum)
    protected int  executeAndResultHoldTime_1000000_more;

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

    @MField(name = "f1", aggregate = AggregateType.Sum)
    protected long fetchRowCount_0_1;

    @MField(name = "f10", aggregate = AggregateType.Sum)
    protected long fetchRowCount_1_10;

    @MField(name = "f100", aggregate = AggregateType.Sum)
    protected long fetchRowCount_10_100;

    @MField(name = "f1000", aggregate = AggregateType.Sum)
    protected int  fetchRowCount_100_1000;

    @MField(name = "f10000", aggregate = AggregateType.Sum)
    protected int  fetchRowCount_1000_10000;

    @MField(name = "fmore", aggregate = AggregateType.Sum)
    protected int  fetchRowCount_10000_more;

    public long[] getFetchRowHistogram() {
        return new long[] { fetchRowCount_0_1, //
                fetchRowCount_1_10, //
                fetchRowCount_10_100, //
                fetchRowCount_100_1000, //
                fetchRowCount_1000_10000, //
                fetchRowCount_10000_more, //
        };
    }

    @MField(name = "u1", aggregate = AggregateType.Sum)
    protected long updateCount_0_1;

    @MField(name = "u10", aggregate = AggregateType.Sum)
    protected long updateCount_1_10;

    @MField(name = "u100", aggregate = AggregateType.Sum)
    protected long updateCount_10_100;

    @MField(name = "u1000", aggregate = AggregateType.Sum)
    protected int  updateCount_100_1000;

    @MField(name = "u10000", aggregate = AggregateType.Sum)
    protected int  updateCount_1000_10000;

    @MField(name = "umore", aggregate = AggregateType.Sum)
    protected int  updateCount_10000_more;

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

    public long getSqlHash() {
        return sqlHash;
    }

    public void setSqlHash(long sqlHash) {
        this.sqlHash = sqlHash;
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

    public long getExecuteLastStartTimeMillis() {
        return executeLastStartTime;
    }

    public Date getExecuteLastStartTime() {
        if (executeLastStartTime <= 0) {
            return null;
        }

        return new Date(executeLastStartTime);
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

    public long getExecuteAndResultSetHoldTimeNano() {
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

    public long getExecuteNanoSpanMaxOccurTimeMillis() {
        return executeNanoSpanMaxOccurTime;
    }

    public Date getExecuteNanoSpanMaxOccurTime() {
        if (executeNanoSpanMaxOccurTime <= 0) {
            return null;
        }
        return new Date(executeNanoSpanMaxOccurTime);
    }

    public Date getExecuteErrorLastTime() {
        if (executeErrorLastTime <= 0) {
            return null;
        }
        return new Date(executeErrorLastTime);
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

        if (executeErrorLast != null) {
            this.executeErrorLastMessage = executeErrorLast.getMessage();
            this.executeErrorLastClass = executeErrorLast.getClass().getName();
            this.executeErrorLastStackTrace = Utils.toString(executeErrorLast.getStackTrace());
        }
    }

    public long getExecuteErrorLastTimeMillis() {
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

    public long[] getHistogramValues() {
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

    public long[] getFetchRowCountHistogramValues() {
        return new long[] {
                //
                fetchRowCount_0_1, //
                fetchRowCount_1_10, //
                fetchRowCount_10_100, //
                fetchRowCount_100_1000, //
                fetchRowCount_1000_10000, //
                fetchRowCount_10000_more //
        };
    }

    public long[] getUpdateCountHistogramValues() {
        return new long[] {
                //
                updateCount_0_1, //
                updateCount_1_10, //
                updateCount_10_100, //
                updateCount_100_1000, //
                updateCount_1000_10000, //
                updateCount_10000_more //
        };
    }

    public long[] getExecuteAndResultHoldTimeHistogramValues() {
        return new long[] {
                //
                executeAndResultHoldTime_0_1, //
                executeAndResultHoldTime_1_10, //
                executeAndResultHoldTime_10_100, //
                executeAndResultHoldTime_100_1000, //
                executeAndResultHoldTime_1000_10000, //
                executeAndResultHoldTime_10000_100000, //
                executeAndResultHoldTime_100000_1000000, //
                executeAndResultHoldTime_1000000_more //
        };
    }

    public long getResultSetHoldTimeMilis() {
        return getResultSetHoldTimeNano() / (1000 * 1000);
    }

    public long getExecuteAndResultSetHoldTimeMilis() {
        return getExecuteAndResultSetHoldTimeNano() / (1000 * 1000);
    }

    public Map<String, Object> getData() throws JMException {
        Map<String, Object> map = new HashMap<String, Object>(48);

        // 0 - 4
        map.put("ID", id);
        map.put("DataSource", dataSource);
        map.put("SQL", sql);
        map.put("ExecuteCount", getExecuteCount());
        map.put("ErrorCount", getExecuteErrorCount());

        // 5 - 9
        map.put("TotalTime", getExecuteMillisTotal());
        map.put("LastTime", getExecuteLastStartTime());
        map.put("MaxTimespan", getExecuteMillisMax());
        map.put("LastError", JMXUtils.getErrorCompositeData(this.getExecuteErrorLast()));
        map.put("EffectedRowCount", getUpdateCount());

        // 10 - 14
        map.put("FetchRowCount", getFetchRowCount());
        map.put("MaxTimespanOccurTime", getExecuteNanoSpanMaxOccurTime());
        map.put("BatchSizeMax", (long) getExecuteBatchSizeMax());
        map.put("BatchSizeTotal", getExecuteBatchSizeTotal());
        map.put("ConcurrentMax", (long) getConcurrentMax());

        // 15 -
        map.put("RunningCount", (long) getRunningCount()); // 15
        map.put("Name", getName()); // 16
        map.put("File", getFile()); // 17

        if (executeErrorLastMessage != null) {
            map.put("LastErrorMessage", executeErrorLastMessage); // 18
            map.put("LastErrorClass", executeErrorLastClass); // 19

            map.put("LastErrorStackTrace", executeErrorLastStackTrace); // 20
            map.put("LastErrorTime", new Date(executeErrorLastTime)); // 21
        } else {
            map.put("LastErrorMessage", null);
            map.put("LastErrorClass", null);
            map.put("LastErrorStackTrace", null);
            map.put("LastErrorTime", null);
        }

        map.put("DbType", dbType); // 22
        map.put("URL", null); // 23
        map.put("InTransactionCount", getInTransactionCount()); // 24

        map.put("Histogram", this.getHistogramValues()); // 25
        map.put("LastSlowParameters", lastSlowParameters); // 26
        map.put("ResultSetHoldTime", getResultSetHoldTimeMilis()); // 27
        map.put("ExecuteAndResultSetHoldTime", this.getExecuteAndResultSetHoldTimeMilis()); // 28
        map.put("FetchRowCountHistogram", this.getFetchRowCountHistogramValues()); // 29

        map.put("EffectedRowCountHistogram", this.getUpdateCountHistogramValues()); // 30
        map.put("ExecuteAndResultHoldTimeHistogram", this.getExecuteAndResultHoldTimeHistogramValues()); // 31
        map.put("EffectedRowCountMax", getUpdateCountMax()); // 32
        map.put("FetchRowCountMax", getFetchRowCountMax()); // 33
        map.put("ClobOpenCount", getClobOpenCount()); // 34

        map.put("BlobOpenCount", getBlobOpenCount()); // 35
        map.put("ReadStringLength", getReadStringLength()); // 36
        map.put("ReadBytesLength", getReadBytesLength()); // 37
        map.put("InputStreamOpenCount", getInputStreamOpenCount()); // 38
        map.put("ReaderOpenCount", getReaderOpenCount()); // 39

        map.put("HASH", getSqlHash()); // 40

        return map;
    }

    public long getHistogram_0_1() {
        return histogram_0_1;
    }

    public void setHistogram_0_1(long histogram_0_1) {
        this.histogram_0_1 = histogram_0_1;
    }

    public long getHistogram_1_10() {
        return histogram_1_10;
    }

    public void setHistogram_1_10(long histogram_1_10) {
        this.histogram_1_10 = histogram_1_10;
    }

    public int getHistogram_10_100() {
        return histogram_10_100;
    }

    public void setHistogram_10_100(int histogram_10_100) {
        this.histogram_10_100 = histogram_10_100;
    }

    public int getHistogram_100_1000() {
        return histogram_100_1000;
    }

    public void setHistogram_100_1000(int histogram_100_1000) {
        this.histogram_100_1000 = histogram_100_1000;
    }

    public int getHistogram_1000_10000() {
        return histogram_1000_10000;
    }

    public void setHistogram_1000_10000(int histogram_1000_10000) {
        this.histogram_1000_10000 = histogram_1000_10000;
    }

    public int getHistogram_10000_100000() {
        return histogram_10000_100000;
    }

    public void setHistogram_10000_100000(int histogram_10000_100000) {
        this.histogram_10000_100000 = histogram_10000_100000;
    }

    public int getHistogram_100000_1000000() {
        return histogram_100000_1000000;
    }

    public void setHistogram_100000_1000000(int histogram_100000_1000000) {
        this.histogram_100000_1000000 = histogram_100000_1000000;
    }

    public int getHistogram_1000000_more() {
        return histogram_1000000_more;
    }

    public void setHistogram_1000000_more(int histogram_1000000_more) {
        this.histogram_1000000_more = histogram_1000000_more;
    }

}
