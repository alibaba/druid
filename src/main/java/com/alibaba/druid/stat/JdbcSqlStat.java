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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import javax.management.JMException;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.proxy.jdbc.StatementExecuteType;
import com.alibaba.druid.util.Histogram;
import com.alibaba.druid.util.IOUtils;
import com.alibaba.druid.util.JMXUtils;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public final class JdbcSqlStat implements JdbcSqlStatMBean {

    private final String                             sql;
    private long                                     id;
    private String                                   dataSource;
    private long                                     executeLastStartTime;

    private volatile long                            executeBatchSizeTotal;
    private volatile long                            executeBatchSizeMax;

    private volatile long                            executeSuccessCount;
    private volatile long                            executeSpanNanoTotal;
    private volatile long                            executeSpanNanoMax;
    private volatile long                            runningCount;
    private volatile long                            concurrentMax;
    private volatile long                            resultSetHoldTimeNano;
    private volatile long                            executeAndResultSetHoldTime;

    final static AtomicLongFieldUpdater<JdbcSqlStat> executeBatchSizeTotalUpdater;
    final static AtomicLongFieldUpdater<JdbcSqlStat> executeBatchSizeMaxUpdater;
    
    final static AtomicLongFieldUpdater<JdbcSqlStat> executeSuccessCountUpdater;
    final static AtomicLongFieldUpdater<JdbcSqlStat> executeSpanNanoTotalUpdater;
    final static AtomicLongFieldUpdater<JdbcSqlStat> executeSpanNanoMaxUpdater;
    final static AtomicLongFieldUpdater<JdbcSqlStat> runningCountUpdater;
    final static AtomicLongFieldUpdater<JdbcSqlStat> concurrentMaxUpdater;
    final static AtomicLongFieldUpdater<JdbcSqlStat> resultSetHoldTimeNanoUpdater;
    final static AtomicLongFieldUpdater<JdbcSqlStat> executeAndResultSetHoldTimeUpdater;
    static {
        executeBatchSizeTotalUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "executeBatchSizeTotal");
        executeBatchSizeMaxUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "executeBatchSizeMax");
        
        executeSuccessCountUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "executeSuccessCount");
        executeSpanNanoTotalUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "executeSpanNanoTotal");
        executeSpanNanoMaxUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "executeSpanNanoMax");
        runningCountUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "runningCount");
        concurrentMaxUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "concurrentMax");
        resultSetHoldTimeNanoUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "resultSetHoldTimeNano");
        executeAndResultSetHoldTimeUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                               "executeAndResultSetHoldTime");
    }

    private String                                   name;
    private String                                   file;
    private String                                   dbType;

    private long                                     executeNanoSpanMaxOccurTime;

    private volatile long                            executeErrorCount;
    private Throwable                                executeErrorLast;
    private long                                     executeErrorLastTime;

    private volatile long                            updateCount;
    private volatile long                            updateCountMax;
    private volatile long                            fetchRowCount;
    private volatile long                            fetchRowCountMax;

    private volatile long                            inTransactionCount;

    private String                                   lastSlowParameters;

    private boolean                                  removed                           = false;

    private volatile long                            clobOpenCount;
    private volatile long                            blobOpenCount;
    private volatile long                            readStringLength;
    private volatile long                            readBytesLength;

    private volatile long                            inputStreamOpenCount;
    private volatile long                            readerOpenCount;

    final static AtomicLongFieldUpdater<JdbcSqlStat> executeErrorCountUpdater;
    final static AtomicLongFieldUpdater<JdbcSqlStat> updateCountUpdater;
    final static AtomicLongFieldUpdater<JdbcSqlStat> updateCountMaxUpdater;
    final static AtomicLongFieldUpdater<JdbcSqlStat> fetchRowCountUpdater;
    final static AtomicLongFieldUpdater<JdbcSqlStat> fetchRowCountMaxUpdater;
    final static AtomicLongFieldUpdater<JdbcSqlStat> inTransactionCountUpdater;

    final static AtomicLongFieldUpdater<JdbcSqlStat> clobOpenCountUpdater;
    final static AtomicLongFieldUpdater<JdbcSqlStat> blobOpenCountUpdater;
    final static AtomicLongFieldUpdater<JdbcSqlStat> readStringLengthUpdater;
    final static AtomicLongFieldUpdater<JdbcSqlStat> readBytesLengthUpdater;

    final static AtomicLongFieldUpdater<JdbcSqlStat> inputStreamOpenCountUpdater;
    final static AtomicLongFieldUpdater<JdbcSqlStat> readerOpenCountUpdater;

    static {
        executeErrorCountUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "executeErrorCount");

        updateCountUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "updateCount");
        updateCountMaxUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "updateCountMax");
        fetchRowCountUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "fetchRowCount");
        fetchRowCountMaxUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "fetchRowCountMax");
        inTransactionCountUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "inTransactionCount");

        clobOpenCountUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "clobOpenCount");
        blobOpenCountUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "blobOpenCount");
        readStringLengthUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "readStringLength");
        readBytesLengthUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "readBytesLength");

        inputStreamOpenCountUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "inputStreamOpenCount");
        readerOpenCountUpdater = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class, "readerOpenCount");
    }

    private final Histogram                          histogram                         = new Histogram(new long[] { //
                                                                                                                    //
            1, 10, 100, 1000, 10 * 1000, //
            100 * 1000, 1000 * 1000
                                                                                                       //
                                                                                                       });

    private final Histogram                          executeAndResultHoldTimeHistogram = new Histogram(new long[] { //
                                                                                                       //
            1, 10, 100, 1000, 10 * 1000, //
            100 * 1000, 1000 * 1000
                                                                                                       //
                                                                                                       });

    private final Histogram                          fetchRowCountHistogram            = new Histogram(new long[] { //
                                                                                                       1, 10, 100,
            1000, 10 * 1000                                                                           });

    private final Histogram                          updateCountHistogram              = new Histogram(new long[] { //
                                                                                                       1, 10, 100,
            1000, 10 * 1000                                                                           });

    public JdbcSqlStat(String sql){
        this.sql = sql;
        this.id = DruidDriver.createSqlStatId();
    }

    public String getLastSlowParameters() {
        return lastSlowParameters;
    }

    public void setLastSlowParameters(String lastSlowParameters) {
        this.lastSlowParameters = lastSlowParameters;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public String getDataSource() {
        return dataSource;
    }

    public void setDataSource(String dataSource) {
        this.dataSource = dataSource;
    }

    @Deprecated
    public final static String getContextSqlName() {
        JdbcStatContext context = JdbcStatManager.getInstance().getStatContext();
        if (context == null) {
            return null;
        }
        return context.getName();
    }

    @Deprecated
    public final static void setContextSqlName(String val) {
        JdbcStatContext context = JdbcStatManager.getInstance().getStatContext();
        if (context == null) {
            context = JdbcStatManager.getInstance().createStatContext();
            JdbcStatManager.getInstance().setStatContext(context);
        }

        context.setName(val);
    }

    @Deprecated
    public final static String getContextSqlFile() {
        JdbcStatContext context = JdbcStatManager.getInstance().getStatContext();
        if (context == null) {
            return null;
        }
        return context.getFile();
    }

    @Deprecated
    public final static void setContextSqlFile(String val) {
        JdbcStatContext context = JdbcStatManager.getInstance().getStatContext();
        if (context == null) {
            context = JdbcStatManager.getInstance().createStatContext();
            JdbcStatManager.getInstance().setStatContext(context);
        }

        context.setFile(val);
    }

    public final static void setContextSql(String val) {
        JdbcStatContext context = JdbcStatManager.getInstance().getStatContext();
        if (context == null) {
            context = JdbcStatManager.getInstance().createStatContext();
            JdbcStatManager.getInstance().setStatContext(context);
        }

        context.setSql(val);
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

    public void reset() {
        executeLastStartTime = 0;

        executeBatchSizeTotalUpdater.set(this, 0);
        executeBatchSizeMaxUpdater.set(this, 0);

        executeSuccessCountUpdater.set(this, 0);
        executeSpanNanoTotalUpdater.set(this, 0);
        executeSpanNanoMaxUpdater.set(this, 0);
        executeNanoSpanMaxOccurTime = 0;
        runningCountUpdater.set(this, 0);
        concurrentMaxUpdater.set(this, 0);

        executeErrorCountUpdater.set(this, 0);
        executeErrorLast = null;
        executeErrorLastTime = 0;

        updateCountUpdater.set(this, 0);
        updateCountMaxUpdater.set(this, 0);
        fetchRowCountUpdater.set(this, 0);
        fetchRowCountMaxUpdater.set(this, 0);

        histogram.reset();
        this.lastSlowParameters = null;
        inTransactionCountUpdater.set(this, 0);
        resultSetHoldTimeNanoUpdater.set(this, 0);
        executeAndResultSetHoldTimeUpdater.set(this, 0);
        fetchRowCountHistogram.reset();
        updateCountHistogram.reset();
        executeAndResultHoldTimeHistogram.reset();

        blobOpenCountUpdater.set(this, 0);
        clobOpenCountUpdater.set(this, 0);
        readStringLengthUpdater.set(this, 0);
        readBytesLengthUpdater.set(this, 0);
        inputStreamOpenCountUpdater.set(this, 0);
        readerOpenCountUpdater.set(this, 0);
    }

    public long getConcurrentMax() {
        return concurrentMax;
    }

    public long getRunningCount() {
        return runningCount;
    }

    public void addUpdateCount(int delta) {
        if (delta > 0) {
            updateCountUpdater.addAndGet(this, delta);
        }
        for (;;) {
            long max = updateCountMaxUpdater.get(this);
            if (delta <= max) {
                break;
            }
            if (updateCountMaxUpdater.compareAndSet(this, max, delta)) {
                break;
            }
        }

        this.updateCountHistogram.record(delta);
    }

    public long getUpdateCount() {
        return updateCount;
    }

    public long getUpdateCountMax() {
        return updateCountMax;
    }

    public long getFetchRowCount() {
        return fetchRowCount;
    }

    public long getFetchRowCountMax() {
        return fetchRowCountMax;
    }

    public long getClobOpenCount() {
        return clobOpenCount;
    }

    public void incrementClobOpenCount() {
        clobOpenCountUpdater.incrementAndGet(this);
    }

    public long getBlobOpenCount() {
        return blobOpenCount;
    }

    public void incrementBlobOpenCount() {
        blobOpenCountUpdater.incrementAndGet(this);
    }

    public long getReadStringLength() {
        return readStringLength;
    }

    public void addStringReadLength(long length) {
        readStringLengthUpdater.addAndGet(this, length);
    }

    public long getReadBytesLength() {
        return readBytesLength;
    }

    public void addReadBytesLength(long length) {
        readBytesLengthUpdater.addAndGet(this, length);
    }

    public long getReaderOpenCount() {
        return readerOpenCount;
    }

    public void addReaderOpenCount(int count) {
        readerOpenCountUpdater.addAndGet(this, count);
    }

    public long getInputStreamOpenCount() {
        return inputStreamOpenCount;
    }

    public void addInputStreamOpenCount(int count) {
        inputStreamOpenCountUpdater.addAndGet(this, count);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getSql() {
        return sql;
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

    public void addFetchRowCount(long delta) {
        fetchRowCountUpdater.addAndGet(this, delta);
        for (;;) {
            long max = fetchRowCountMaxUpdater.get(this);
            if (delta <= max) {
                break;
            }
            if (fetchRowCountMaxUpdater.compareAndSet(this, max, delta)) {
                break;
            }
        }
        this.fetchRowCountHistogram.record(delta);

    }

    public void addExecuteBatchCount(long batchSize) {
        executeBatchSizeTotalUpdater.addAndGet(this, batchSize);

        // executeBatchSizeMax
        for (;;) {
            long current = executeBatchSizeMaxUpdater.get(this);
            if (current < batchSize) {
                if (executeBatchSizeMaxUpdater.compareAndSet(this, current, batchSize)) {
                    break;
                } else {
                    continue;
                }
            } else {
                break;
            }
        }
    }

    public long getExecuteBatchSizeTotal() {
        return executeBatchSizeTotal;
    }

    public void incrementExecuteSuccessCount() {
        executeSuccessCountUpdater.incrementAndGet(this);
    }

    public void incrementRunningCount() {
        long val = runningCountUpdater.incrementAndGet(this);

        for (;;) {
            long max = concurrentMaxUpdater.get(this);
            if (val > max) {
                if (concurrentMaxUpdater.compareAndSet(this, max, val)) {
                    break;
                } else {
                    continue;
                }
            } else {
                break;
            }
        }
    }

    public void decrementRunningCount() {
        runningCountUpdater.decrementAndGet(this);
    }

    public void decrementExecutingCount() {
        runningCountUpdater.decrementAndGet(this);
    }

    public long getExecuteSuccessCount() {
        return executeSuccessCount;
    }

    public void addExecuteTime(StatementExecuteType executeType, boolean firstResultSet, long nanoSpan) {
        addExecuteTime(nanoSpan);

        if (StatementExecuteType.ExecuteQuery != executeType && !firstResultSet) {
            executeAndResultHoldTimeHistogram.record((nanoSpan) / 1000 / 1000);
        }
    }

    public void addExecuteTime(long nanoSpan) {
        executeSpanNanoTotalUpdater.addAndGet(this, nanoSpan);

        for (;;) {
            long current = executeSpanNanoMaxUpdater.get(this);
            if (current < nanoSpan) {
                if (executeSpanNanoMaxUpdater.compareAndSet(this, current, nanoSpan)) {
                    // 可能不准确，但是绝大多数情况下都会正确，性能换取一致性
                    executeNanoSpanMaxOccurTime = System.currentTimeMillis();

                    break;
                } else {
                    continue;
                }
            } else {
                break;
            }
        }

        long millis = nanoSpan / (1000 * 1000);
        histogram.record(millis);
    }

    public long getExecuteMillisTotal() {
        return executeSpanNanoTotal / (1000 * 1000);
    }

    public long getExecuteMillisMax() {
        return executeSpanNanoMax / (1000 * 1000);
    }

    public long getErrorCount() {
        return executeErrorCount;
    }

    @Override
    public long getExecuteBatchSizeMax() {
        return executeBatchSizeMax;
    }

    public long getInTransactionCount() {
        return inTransactionCount;
    }

    public void incrementInTransactionCount() {
        inTransactionCountUpdater.incrementAndGet(this);
    }

    private static CompositeType COMPOSITE_TYPE = null;

    public static CompositeType getCompositeType() throws JMException {

        if (COMPOSITE_TYPE != null) {
            return COMPOSITE_TYPE;
        }

        OpenType<?>[] indexTypes = new OpenType<?>[] {
                // 0 - 4
                SimpleType.LONG, //
                SimpleType.STRING, //
                SimpleType.STRING, //
                SimpleType.LONG, //
                SimpleType.LONG, //

                // 5 - 9
                SimpleType.LONG, //
                SimpleType.DATE, //
                SimpleType.LONG, //
                JMXUtils.getThrowableCompositeType(), //
                SimpleType.LONG, //
                //

                // 10 - 14
                SimpleType.LONG, //
                SimpleType.DATE, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                //

                // 15 - 19
                SimpleType.LONG, //
                SimpleType.STRING, //
                SimpleType.STRING, //
                SimpleType.STRING, //
                SimpleType.STRING, //
                //

                // 20 - 24
                SimpleType.STRING, //
                SimpleType.DATE, //
                SimpleType.STRING, //
                SimpleType.LONG, //
                SimpleType.STRING, //

                // 25 - 29
                new ArrayType<Long>(SimpleType.LONG, true), //
                SimpleType.STRING, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                new ArrayType<Long>(SimpleType.LONG, true), //

                // 30 - 34
                new ArrayType<Long>(SimpleType.LONG, true), //
                new ArrayType<Long>(SimpleType.LONG, true), //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //

                // 35 -
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //

        };

        String[] indexNames = {
                // 0 - 4
                "ID", //
                "DataSource", //
                "SQL", //
                "ExecuteCount", //
                "ErrorCount", //

                // 5 - 9
                "TotalTime", //
                "LastTime", //
                "MaxTimespan", //
                "LastError", //
                "EffectedRowCount", //

                // 10 - 14
                "FetchRowCount", //
                "MaxTimespanOccurTime", //
                "BatchSizeMax", //
                "BatchSizeTotal", //
                "ConcurrentMax", //

                // 15 - 19
                "RunningCount", //
                "Name", //
                "File", //
                "LastErrorMessage", //
                "LastErrorClass", //

                // 20 - 24
                "LastErrorStackTrace", //
                "LastErrorTime", //
                "DbType", //
                "InTransactionCount", //
                "URL", //

                // 25 - 29
                "Histogram", //
                "LastSlowParameters", //
                "ResultSetHoldTime", //
                "ExecuteAndResultSetHoldTime", //
                "FetchRowCountHistogram", //

                // 30 - 34
                "EffectedRowCountHistogram", //
                "ExecuteAndResultHoldTimeHistogram", //
                "EffectedRowCountMax", //
                "FetchRowCountMax", //
                "ClobOpenCount",

                // 35 -
                "BlobOpenCount", //
                "ReadStringLength", //
                "ReadBytesLength", //
                "InputStreamOpenCount", //
                "ReaderOpenCount", //

        //
        };
        String[] indexDescriptions = indexNames;
        COMPOSITE_TYPE = new CompositeType("SqlStatistic", "Sql Statistic", indexNames, indexDescriptions, indexTypes);

        return COMPOSITE_TYPE;
    }

    public long getExecuteCount() {
        return getErrorCount() + getExecuteSuccessCount();
    }

    public Map<String, Object> getData() throws JMException {
        Map<String, Object> map = new HashMap<String, Object>();

        // 0 - 4
        map.put("ID", id);
        map.put("DataSource", dataSource);
        map.put("SQL", sql);
        map.put("ExecuteCount", getExecuteCount());
        map.put("ErrorCount", getErrorCount());

        // 5 - 9
        map.put("TotalTime", getExecuteMillisTotal());
        map.put("LastTime", getExecuteLastStartTime());
        map.put("MaxTimespan", getExecuteMillisMax());
        map.put("LastError", JMXUtils.getErrorCompositeData(this.getExecuteErrorLast()));
        map.put("EffectedRowCount", getUpdateCount());

        // 10 - 14
        map.put("FetchRowCount", getFetchRowCount());
        map.put("MaxTimespanOccurTime", getExecuteNanoSpanMaxOccurTime());
        map.put("BatchSizeMax", getExecuteBatchSizeMax());
        map.put("BatchSizeTotal", getExecuteBatchSizeTotal());
        map.put("ConcurrentMax", getConcurrentMax());

        // 15 -
        map.put("RunningCount", getRunningCount()); // 15
        map.put("Name", getName()); // 16
        map.put("File", getFile()); // 17

        Throwable lastError = this.executeErrorLast;
        if (lastError != null) {
            map.put("LastErrorMessage", lastError.getMessage()); // 18
            map.put("LastErrorClass", lastError.getClass().getName()); // 19

            map.put("LastErrorStackTrace", IOUtils.getStackTrace(lastError)); // 20
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

        map.put("Histogram", this.histogram.toArray()); // 25
        map.put("LastSlowParameters", lastSlowParameters); // 26
        map.put("ResultSetHoldTime", getResultSetHoldTimeMilis()); // 27
        map.put("ExecuteAndResultSetHoldTime", this.getExecuteAndResultSetHoldTimeMilis()); // 28
        map.put("FetchRowCountHistogram", this.getFetchRowCountHistogram().toArray()); // 29

        map.put("EffectedRowCountHistogram", this.getUpdateCountHistogram().toArray()); // 30
        map.put("ExecuteAndResultHoldTimeHistogram", this.getExecuteAndResultHoldTimeHistogram().toArray()); // 31
        map.put("EffectedRowCountMax", getUpdateCountMax()); // 32
        map.put("FetchRowCountMax", getFetchRowCountMax()); // 33
        map.put("ClobOpenCount", getClobOpenCount()); // 34

        map.put("BlobOpenCount", getBlobOpenCount()); // 35
        map.put("ReadStringLength", getReadStringLength()); // 36
        map.put("ReadBytesLength", getReadBytesLength()); // 37
        map.put("InputStreamOpenCount", getInputStreamOpenCount()); // 38
        map.put("ReaderOpenCount", getReaderOpenCount()); // 39

        return map;
    }

    public Histogram getHistogram() {
        return this.histogram;
    }

    public CompositeDataSupport getCompositeData() throws JMException {
        return new CompositeDataSupport(getCompositeType(), getData());
    }

    public Throwable getExecuteErrorLast() {
        return executeErrorLast;
    }

    public void error(Throwable error) {
        executeErrorCountUpdater.incrementAndGet(this);
        executeErrorLastTime = System.currentTimeMillis();
        executeErrorLast = error;

    }

    public long getResultSetHoldTimeMilis() {
        return getResultSetHoldTimeNano() / (1000 * 1000);
    }

    public long getExecuteAndResultSetHoldTimeMilis() {
        return getExecuteAndResultSetHoldTimeNano() / (1000 * 1000);
    }

    public Histogram getFetchRowCountHistogram() {
        return this.fetchRowCountHistogram;
    }

    public Histogram getUpdateCountHistogram() {
        return this.updateCountHistogram;
    }

    public Histogram getExecuteAndResultHoldTimeHistogram() {
        return this.executeAndResultHoldTimeHistogram;
    }

    public long getResultSetHoldTimeNano() {
        return resultSetHoldTimeNano;
    }

    public long getExecuteAndResultSetHoldTimeNano() {
        return executeAndResultSetHoldTime;
    }

    public void addResultSetHoldTimeNano(long nano) {
        resultSetHoldTimeNanoUpdater.addAndGet(this, nano);
    }

    public void addResultSetHoldTimeNano(long statementExecuteNano, long resultHoldTimeNano) {
        resultSetHoldTimeNanoUpdater.addAndGet(this, resultHoldTimeNano);
        executeAndResultSetHoldTimeUpdater.addAndGet(this, statementExecuteNano + resultHoldTimeNano);
        executeAndResultHoldTimeHistogram.record((statementExecuteNano + resultHoldTimeNano) / 1000 / 1000);
        updateCountHistogram.record(0);
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

}
