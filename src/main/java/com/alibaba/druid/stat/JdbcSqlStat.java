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

import static com.alibaba.druid.util.JdbcSqlStatUtils.get;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;

import javax.management.JMException;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.proxy.jdbc.StatementExecuteType;
import com.alibaba.druid.util.JMXUtils;
import com.alibaba.druid.util.Utils;

public final class JdbcSqlStat implements JdbcSqlStatMBean, Comparable<JdbcSqlStat> {

    private final String                                sql;
    private long                                        sqlHash;
    private long                                        id;
    private String                                      dataSource;
    private long                                        executeLastStartTime;

    private volatile long                               executeBatchSizeTotal;
    private volatile int                                executeBatchSizeMax;

    private volatile long                               executeSuccessCount;
    private volatile long                               executeSpanNanoTotal;
    private volatile long                               executeSpanNanoMax;
    private volatile int                                runningCount;
    private volatile int                                concurrentMax;
    private volatile long                               resultSetHoldTimeNano;
    private volatile long                               executeAndResultSetHoldTime;

    final static AtomicLongFieldUpdater<JdbcSqlStat>    executeBatchSizeTotalUpdater                    = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "executeBatchSizeTotal");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> executeBatchSizeMaxUpdater                      = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "executeBatchSizeMax");

    final static AtomicLongFieldUpdater<JdbcSqlStat>    executeSuccessCountUpdater                      = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "executeSuccessCount");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    executeSpanNanoTotalUpdater                     = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "executeSpanNanoTotal");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    executeSpanNanoMaxUpdater                       = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "executeSpanNanoMax");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> runningCountUpdater                             = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "runningCount");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> concurrentMaxUpdater                            = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "concurrentMax");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    resultSetHoldTimeNanoUpdater                    = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "resultSetHoldTimeNano");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    executeAndResultSetHoldTimeUpdater              = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "executeAndResultSetHoldTime");

    private String                                      name;
    private String                                      file;
    private String                                      dbType;

    private volatile long                               executeNanoSpanMaxOccurTime;

    private volatile long                               executeErrorCount;
    private volatile Throwable                          executeErrorLast;
    private volatile long                               executeErrorLastTime;

    private volatile long                               updateCount;
    private volatile long                               updateCountMax;
    private volatile long                               fetchRowCount;
    private volatile long                               fetchRowCountMax;

    private volatile long                               inTransactionCount;

    private volatile String                             lastSlowParameters;

    private boolean                                     removed                                         = false;

    private volatile long                               clobOpenCount;
    private volatile long                               blobOpenCount;
    private volatile long                               readStringLength;
    private volatile long                               readBytesLength;

    private volatile long                               inputStreamOpenCount;
    private volatile long                               readerOpenCount;

    final static AtomicLongFieldUpdater<JdbcSqlStat>    executeErrorCountUpdater                        = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "executeErrorCount");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    updateCountUpdater                              = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "updateCount");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    updateCountMaxUpdater                           = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "updateCountMax");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    fetchRowCountUpdater                            = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "fetchRowCount");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    fetchRowCountMaxUpdater                         = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "fetchRowCountMax");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    inTransactionCountUpdater                       = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "inTransactionCount");

    final static AtomicLongFieldUpdater<JdbcSqlStat>    clobOpenCountUpdater                            = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "clobOpenCount");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    blobOpenCountUpdater                            = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "blobOpenCount");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    readStringLengthUpdater                         = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "readStringLength");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    readBytesLengthUpdater                          = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "readBytesLength");

    final static AtomicLongFieldUpdater<JdbcSqlStat>    inputStreamOpenCountUpdater                     = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "inputStreamOpenCount");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    readerOpenCountUpdater                          = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "readerOpenCount");

    private volatile long                               histogram_0_1;
    private volatile long                               histogram_1_10;
    private volatile int                                histogram_10_100;
    private volatile int                                histogram_100_1000;
    private volatile int                                histogram_1000_10000;
    private volatile int                                histogram_10000_100000;
    private volatile int                                histogram_100000_1000000;
    private volatile int                                histogram_1000000_more;

    final static AtomicLongFieldUpdater<JdbcSqlStat>    histogram_0_1_Updater                           = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "histogram_0_1");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    histogram_1_10_Updater                          = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "histogram_1_10");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> histogram_10_100_Updater                        = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "histogram_10_100");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> histogram_100_1000_Updater                      = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "histogram_100_1000");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> histogram_1000_10000_Updater                    = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "histogram_1000_10000");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> histogram_10000_100000_Updater                  = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "histogram_10000_100000");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> histogram_100000_1000000_Updater                = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "histogram_100000_1000000");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> histogram_1000000_more_Updater                  = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "histogram_1000000_more");

    private volatile long                               executeAndResultHoldTime_0_1;
    private volatile long                               executeAndResultHoldTime_1_10;
    private volatile int                                executeAndResultHoldTime_10_100;
    private volatile int                                executeAndResultHoldTime_100_1000;
    private volatile int                                executeAndResultHoldTime_1000_10000;
    private volatile int                                executeAndResultHoldTime_10000_100000;
    private volatile int                                executeAndResultHoldTime_100000_1000000;
    private volatile int                                executeAndResultHoldTime_1000000_more;

    final static AtomicLongFieldUpdater<JdbcSqlStat>    executeAndResultHoldTime_0_1_Updater            = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "executeAndResultHoldTime_0_1");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    executeAndResultHoldTime_1_10_Updater           = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "executeAndResultHoldTime_1_10");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> executeAndResultHoldTime_10_100_Updater         = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "executeAndResultHoldTime_10_100");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> executeAndResultHoldTime_100_1000_Updater       = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "executeAndResultHoldTime_100_1000");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> executeAndResultHoldTime_1000_10000_Updater     = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "executeAndResultHoldTime_1000_10000");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> executeAndResultHoldTime_10000_100000_Updater   = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "executeAndResultHoldTime_10000_100000");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> executeAndResultHoldTime_100000_1000000_Updater = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "executeAndResultHoldTime_100000_1000000");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> executeAndResultHoldTime_1000000_more_Updater   = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "executeAndResultHoldTime_1000000_more");

    private volatile long                               fetchRowCount_0_1;
    private volatile long                               fetchRowCount_1_10;
    private volatile long                               fetchRowCount_10_100;
    private volatile int                                fetchRowCount_100_1000;
    private volatile int                                fetchRowCount_1000_10000;
    private volatile int                                fetchRowCount_10000_more;

    final static AtomicLongFieldUpdater<JdbcSqlStat>    fetchRowCount_0_1_Updater                       = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "fetchRowCount_0_1");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    fetchRowCount_1_10_Updater                      = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "fetchRowCount_1_10");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    fetchRowCount_10_100_Updater                    = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "fetchRowCount_10_100");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> fetchRowCount_100_1000_Updater                  = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "fetchRowCount_100_1000");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> fetchRowCount_1000_10000_Updater                = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "fetchRowCount_1000_10000");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> fetchRowCount_10000_more_Updater                = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "fetchRowCount_10000_more");

    private volatile long                               updateCount_0_1;
    private volatile long                               updateCount_1_10;
    private volatile long                               updateCount_10_100;
    private volatile int                                updateCount_100_1000;
    private volatile int                                updateCount_1000_10000;
    private volatile int                                updateCount_10000_more;

    final static AtomicLongFieldUpdater<JdbcSqlStat>    updateCount_0_1_Updater                         = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "updateCount_0_1");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    updateCount_1_10_Updater                        = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "updateCount_1_10");
    final static AtomicLongFieldUpdater<JdbcSqlStat>    updateCount_10_100_Updater                      = AtomicLongFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                            "updateCount_10_100");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> updateCount_100_1000_Updater                    = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "updateCount_100_1000");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> updateCount_1000_10000_Updater                  = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "updateCount_1000_10000");
    final static AtomicIntegerFieldUpdater<JdbcSqlStat> updateCount_10000_more_Updater                  = AtomicIntegerFieldUpdater.newUpdater(JdbcSqlStat.class,
                                                                                                                                               "updateCount_10000_more");

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

    public final static String getContextSqlName() {
        JdbcStatContext context = JdbcStatManager.getInstance().getStatContext();
        if (context == null) {
            return null;
        }
        return context.getName();
    }

    public final static void setContextSqlName(String val) {
        JdbcStatContext context = JdbcStatManager.getInstance().getStatContext();
        if (context == null) {
            context = JdbcStatManager.getInstance().createStatContext();
            JdbcStatManager.getInstance().setStatContext(context);
        }

        context.setName(val);
    }

    public final static String getContextSqlFile() {
        JdbcStatContext context = JdbcStatManager.getInstance().getStatContext();
        if (context == null) {
            return null;
        }
        return context.getFile();
    }

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
        concurrentMaxUpdater.set(this, 0);

        executeErrorCountUpdater.set(this, 0);
        executeErrorLast = null;
        executeErrorLastTime = 0;

        updateCountUpdater.set(this, 0);
        updateCountMaxUpdater.set(this, 0);
        fetchRowCountUpdater.set(this, 0);
        fetchRowCountMaxUpdater.set(this, 0);

        histogram_0_1_Updater.set(this, 0);
        histogram_1_10_Updater.set(this, 0);
        histogram_10_100_Updater.set(this, 0);
        histogram_100_1000_Updater.set(this, 0);
        histogram_1000_10000_Updater.set(this, 0);
        histogram_10000_100000_Updater.set(this, 0);
        histogram_100000_1000000_Updater.set(this, 0);
        histogram_1000000_more_Updater.set(this, 0);

        this.lastSlowParameters = null;
        inTransactionCountUpdater.set(this, 0);
        resultSetHoldTimeNanoUpdater.set(this, 0);
        executeAndResultSetHoldTimeUpdater.set(this, 0);

        fetchRowCount_0_1_Updater.set(this, 0);
        fetchRowCount_1_10_Updater.set(this, 0);
        fetchRowCount_10_100_Updater.set(this, 0);
        fetchRowCount_100_1000_Updater.set(this, 0);
        fetchRowCount_1000_10000_Updater.set(this, 0);
        fetchRowCount_10000_more_Updater.set(this, 0);

        updateCount_0_1_Updater.set(this, 0);
        updateCount_1_10_Updater.set(this, 0);
        updateCount_10_100_Updater.set(this, 0);
        updateCount_100_1000_Updater.set(this, 0);
        updateCount_1000_10000_Updater.set(this, 0);
        updateCount_10000_more_Updater.set(this, 0);

        executeAndResultHoldTime_0_1_Updater.set(this, 0);
        executeAndResultHoldTime_1_10_Updater.set(this, 0);
        executeAndResultHoldTime_10_100_Updater.set(this, 0);
        executeAndResultHoldTime_100_1000_Updater.set(this, 0);
        executeAndResultHoldTime_1000_10000_Updater.set(this, 0);
        executeAndResultHoldTime_10000_100000_Updater.set(this, 0);
        executeAndResultHoldTime_100000_1000000_Updater.set(this, 0);
        executeAndResultHoldTime_1000000_more_Updater.set(this, 0);

        blobOpenCountUpdater.set(this, 0);
        clobOpenCountUpdater.set(this, 0);
        readStringLengthUpdater.set(this, 0);
        readBytesLengthUpdater.set(this, 0);
        inputStreamOpenCountUpdater.set(this, 0);
        readerOpenCountUpdater.set(this, 0);
    }

    public JdbcSqlStatValue getValueAndReset() {
        return getValue(true);
    }

    public JdbcSqlStatValue getValue(boolean reset) {
        JdbcSqlStatValue val = new JdbcSqlStatValue();

        val.setDbType(dbType);
        val.setSql(sql);
        val.setSqlHash(getSqlHash());
        val.setId(id);
        val.setName(name);
        val.setFile(file);
        val.setExecuteLastStartTime(executeLastStartTime);
        if (reset) {
            executeLastStartTime = 0;
        }

        val.setExecuteBatchSizeTotal(get(this, executeBatchSizeTotalUpdater, reset));
        val.setExecuteBatchSizeMax(get(this, executeBatchSizeMaxUpdater, reset));

        val.setExecuteSuccessCount(get(this, executeSuccessCountUpdater, reset));
        val.setExecuteSpanNanoTotal(get(this, executeSpanNanoTotalUpdater, reset));
        val.setExecuteSpanNanoMax(get(this, executeSpanNanoMaxUpdater, reset));
        val.setExecuteNanoSpanMaxOccurTime(executeNanoSpanMaxOccurTime);
        if (reset) {
            executeNanoSpanMaxOccurTime = 0;
        }

        val.setRunningCount(this.runningCount);

        val.setConcurrentMax(get(this, concurrentMaxUpdater, reset));

        val.setExecuteErrorCount(get(this, executeErrorCountUpdater, reset));

        val.setExecuteErrorLast(executeErrorLast);
        if (reset) {
            executeErrorLast = null;
        }

        val.setExecuteErrorLastTime(executeErrorLastTime);
        if (reset) {
            executeErrorLastTime = 0;
        }

        val.setUpdateCount(get(this, updateCountUpdater, reset));
        val.setUpdateCountMax(get(this, updateCountMaxUpdater, reset));
        val.setFetchRowCount(get(this, fetchRowCountUpdater, reset));
        val.setFetchRowCountMax(get(this, fetchRowCountMaxUpdater, reset));

        val.histogram_0_1 = get(this, histogram_0_1_Updater, reset);
        val.histogram_1_10 = get(this, histogram_1_10_Updater, reset);
        val.histogram_10_100 = get(this, histogram_10_100_Updater, reset);
        val.histogram_100_1000 = get(this, histogram_100_1000_Updater, reset);
        val.histogram_1000_10000 = get(this, histogram_1000_10000_Updater, reset);
        val.histogram_10000_100000 = get(this, histogram_10000_100000_Updater, reset);
        val.histogram_100000_1000000 = get(this, histogram_100000_1000000_Updater, reset);
        val.histogram_1000000_more = get(this, histogram_1000000_more_Updater, reset);

        val.setLastSlowParameters(lastSlowParameters);
        if (reset) {
            lastSlowParameters = null;
        }

        val.setInTransactionCount(get(this, inTransactionCountUpdater, reset));
        val.setResultSetHoldTimeNano(get(this, resultSetHoldTimeNanoUpdater, reset));
        val.setExecuteAndResultSetHoldTime(get(this, executeAndResultSetHoldTimeUpdater, reset));

        val.fetchRowCount_0_1 = get(this, fetchRowCount_0_1_Updater, reset);
        val.fetchRowCount_1_10 = get(this, fetchRowCount_1_10_Updater, reset);
        val.fetchRowCount_10_100 = get(this, fetchRowCount_10_100_Updater, reset);
        val.fetchRowCount_100_1000 = get(this, fetchRowCount_100_1000_Updater, reset);
        val.fetchRowCount_1000_10000 = get(this, fetchRowCount_1000_10000_Updater, reset);
        val.fetchRowCount_10000_more = get(this, fetchRowCount_10000_more_Updater, reset);

        val.updateCount_0_1 = get(this, updateCount_0_1_Updater, reset);
        val.updateCount_1_10 = get(this, updateCount_1_10_Updater, reset);
        val.updateCount_10_100 = get(this, updateCount_10_100_Updater, reset);
        val.updateCount_100_1000 = get(this, updateCount_100_1000_Updater, reset);
        val.updateCount_1000_10000 = get(this, updateCount_1000_10000_Updater, reset);
        val.updateCount_10000_more = get(this, updateCount_10000_more_Updater, reset);

        val.executeAndResultHoldTime_0_1 = get(this, executeAndResultHoldTime_0_1_Updater, reset);
        val.executeAndResultHoldTime_1_10 = get(this, executeAndResultHoldTime_1_10_Updater, reset);
        val.executeAndResultHoldTime_10_100 = get(this, executeAndResultHoldTime_10_100_Updater, reset);
        val.executeAndResultHoldTime_100_1000 = get(this, executeAndResultHoldTime_100_1000_Updater, reset);
        val.executeAndResultHoldTime_1000_10000 = get(this, executeAndResultHoldTime_1000_10000_Updater, reset);
        val.executeAndResultHoldTime_10000_100000 = get(this, executeAndResultHoldTime_10000_100000_Updater, reset);
        val.executeAndResultHoldTime_100000_1000000 = get(this, executeAndResultHoldTime_100000_1000000_Updater, reset);
        val.executeAndResultHoldTime_1000000_more = get(this, executeAndResultHoldTime_1000000_more_Updater, reset);

        val.setBlobOpenCount(get(this, blobOpenCountUpdater, reset));
        val.setClobOpenCount(get(this, clobOpenCountUpdater, reset));
        val.setReadStringLength(get(this, readStringLengthUpdater, reset));
        val.setReadBytesLength(get(this, readBytesLengthUpdater, reset));
        val.setInputStreamOpenCount(get(this, inputStreamOpenCountUpdater, reset));
        val.setReaderOpenCount(get(this, readerOpenCountUpdater, reset));

        return val;
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

        if (delta < 1) {
            updateCount_0_1_Updater.incrementAndGet(this);
        } else if (delta < 10) {
            updateCount_1_10_Updater.incrementAndGet(this);
        } else if (delta < 100) {
            updateCount_10_100_Updater.incrementAndGet(this);
        } else if (delta < 1000) {
            updateCount_100_1000_Updater.incrementAndGet(this);
        } else if (delta < 10000) {
            updateCount_1000_10000_Updater.incrementAndGet(this);
        } else {
            updateCount_10000_more_Updater.incrementAndGet(this);
        }
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
    
    public long getSqlHash() {
        if (sqlHash == 0) {
            sqlHash = Utils.fnv_64(sql);
        }
        return sqlHash;
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

        if (delta < 1) {
            fetchRowCount_0_1_Updater.incrementAndGet(this);
        } else if (delta < 10) {
            fetchRowCount_1_10_Updater.incrementAndGet(this);
        } else if (delta < 100) {
            fetchRowCount_10_100_Updater.incrementAndGet(this);
        } else if (delta < 1000) {
            fetchRowCount_100_1000_Updater.incrementAndGet(this);
        } else if (delta < 10000) {
            fetchRowCount_1000_10000_Updater.incrementAndGet(this);
        } else {
            fetchRowCount_10000_more_Updater.incrementAndGet(this);
        }

    }

    public void addExecuteBatchCount(long batchSize) {
        executeBatchSizeTotalUpdater.addAndGet(this, batchSize);

        // executeBatchSizeMax
        for (;;) {
            int current = executeBatchSizeMaxUpdater.get(this);
            if (current >= batchSize) {
                break;
            }

            if (executeBatchSizeMaxUpdater.compareAndSet(this, current, (int) batchSize)) {
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
        int val = runningCountUpdater.incrementAndGet(this);

        for (;;) {
            int max = concurrentMaxUpdater.get(this);
            if (val <= max) {
                break;
            }

            if (concurrentMaxUpdater.compareAndSet(this, max, val)) {
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
            executeAndResultHoldTimeHistogramRecord(nanoSpan);
        }
    }

    private void executeAndResultHoldTimeHistogramRecord(long nanoSpan) {
        long millis = nanoSpan / 1000 / 1000;

        if (millis < 1) {
            executeAndResultHoldTime_0_1_Updater.incrementAndGet(this);
        } else if (millis < 10) {
            executeAndResultHoldTime_1_10_Updater.incrementAndGet(this);
        } else if (millis < 100) {
            executeAndResultHoldTime_10_100_Updater.incrementAndGet(this);
        } else if (millis < 1000) {
            executeAndResultHoldTime_100_1000_Updater.incrementAndGet(this);
        } else if (millis < 10000) {
            executeAndResultHoldTime_1000_10000_Updater.incrementAndGet(this);
        } else if (millis < 100000) {
            executeAndResultHoldTime_10000_100000_Updater.incrementAndGet(this);
        } else if (millis < 1000000) {
            executeAndResultHoldTime_100000_1000000_Updater.incrementAndGet(this);
        } else {
            executeAndResultHoldTime_1000000_more_Updater.incrementAndGet(this);
        }
    }

    private void histogramRecord(long nanoSpan) {
        long millis = nanoSpan / 1000 / 1000;

        if (millis < 1) {
            histogram_0_1_Updater.incrementAndGet(this);
        } else if (millis < 10) {
            histogram_1_10_Updater.incrementAndGet(this);
        } else if (millis < 100) {
            histogram_10_100_Updater.incrementAndGet(this);
        } else if (millis < 1000) {
            histogram_100_1000_Updater.incrementAndGet(this);
        } else if (millis < 10000) {
            histogram_1000_10000_Updater.incrementAndGet(this);
        } else if (millis < 100000) {
            histogram_10000_100000_Updater.incrementAndGet(this);
        } else if (millis < 1000000) {
            histogram_100000_1000000_Updater.incrementAndGet(this);
        } else {
            histogram_1000000_more_Updater.incrementAndGet(this);
        }
    }

    public void addExecuteTime(long nanoSpan) {
        executeSpanNanoTotalUpdater.addAndGet(this, nanoSpan);

        for (;;) {
            long current = executeSpanNanoMaxUpdater.get(this);
            if (current >= nanoSpan) {
                break;
            }

            if (executeSpanNanoMaxUpdater.compareAndSet(this, current, nanoSpan)) {
                // 可能不准确，但是绝大多数情况下都会正确，性能换取一致性
                executeNanoSpanMaxOccurTime = System.currentTimeMillis();
                break;
            }
        }

        histogramRecord(nanoSpan);
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

                // 35 - 39
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                SimpleType.LONG, //
                
                // 40 -
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
                
                // 40
                "HASH", //

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
        return getValue(false).getData();
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

    public long getHistogramSum() {
        long[] values = this.getHistogramValues();
        long sum = 0;
        for (int i = 0; i < values.length; ++i) {
            sum += values[i];
        }
        return sum;
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

    public long getExecuteAndResultHoldTimeHistogramSum() {
        long[] values = this.getExecuteAndResultHoldTimeHistogramValues();
        long sum = 0;
        for (int i = 0; i < values.length; ++i) {
            sum += values[i];
        }
        return sum;
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
        executeAndResultHoldTimeHistogramRecord((statementExecuteNano + resultHoldTimeNano) / 1000 / 1000);
        updateCount_0_1_Updater.incrementAndGet(this);
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

    @Override
    public int compareTo(JdbcSqlStat o) {
        if (o.sqlHash == this.sqlHash) {
            return 0;
        }
        
        return this.id < o.id ? -1 : 1;
    }
}
