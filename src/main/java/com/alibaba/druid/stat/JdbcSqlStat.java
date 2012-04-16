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
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import com.alibaba.druid.proxy.jdbc.StatementExecuteType;
import com.alibaba.druid.util.Histogram;
import com.alibaba.druid.util.IOUtils;
import com.alibaba.druid.util.JMXUtils;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public final class JdbcSqlStat implements JdbcSqlStatMBean {

    private final String     sql;
    private long             id;
    private String           dataSource;
    private long             executeLastStartTime;

    private final AtomicLong executeBatchSizeTotal = new AtomicLong();
    private final AtomicLong executeBatchSizeMax   = new AtomicLong();

    private final AtomicLong executeSuccessCount   = new AtomicLong();
    private final AtomicLong executeSpanNanoTotal  = new AtomicLong();
    private final AtomicLong executeSpanNanoMax    = new AtomicLong();
    private final AtomicLong runningCount          = new AtomicLong(0L);
    private final AtomicLong concurrentMax         = new AtomicLong();
    private final AtomicLong resultSetHoldTimeNano = new AtomicLong();
    private final  AtomicLong executeAndResultSetHoldTime = new AtomicLong();
    
    private String           name;
    private String           file;
    private String           dbType;

    private long             executeNanoSpanMaxOccurTime;

    private final AtomicLong executeErrorCount     = new AtomicLong();
    private Throwable        executeErrorLast;
    private long             executeErrorLastTime;

    private final AtomicLong updateCount           = new AtomicLong();
    private final AtomicLong fetchRowCount         = new AtomicLong();

    private final AtomicLong inTransactionCount    = new AtomicLong();

    
    private String           lastSlowParameters;

    private final Histogram  histogram             = new Histogram(new long[] { //
                                                                                //
            1, 10, 100, 1000, 10 * 1000, //
            100 * 1000, 1000 * 1000
                                                                   //
                                                                   });
    
    private final Histogram  executeAndResultHoldTimeHistogram             = new Histogram(new long[] { //
            //
    		1, 10, 100, 1000, 10 * 1000, //
    		100 * 1000, 1000 * 1000
//
    });

    
    private final Histogram  fetchRowCountHistogram             = new Histogram(new long[] { //
            1, 10, 100, 1000, 10 * 1000
    });

    
    private final Histogram  updateCountHistogram             = new Histogram(new long[] { //
            1, 10, 100, 1000, 10 * 1000
    });
    
    
    
    public JdbcSqlStat(String sql){
        this.sql = sql;
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

        executeBatchSizeTotal.set(0);
        executeBatchSizeMax.set(0);

        executeSuccessCount.set(0);
        executeSpanNanoTotal.set(0);
        executeSpanNanoMax.set(0);
        executeNanoSpanMaxOccurTime = 0;
        runningCount.set(0);
        concurrentMax.set(0);

        executeErrorCount.set(0);
        executeErrorLast = null;
        executeErrorLastTime = 0;

        updateCount.set(0);
        fetchRowCount.set(0);

        histogram.reset();
        this.lastSlowParameters = null;
        inTransactionCount.set(0);
        resultSetHoldTimeNano.set(0);
        executeAndResultSetHoldTime.set(0);
        fetchRowCountHistogram.reset();
        updateCountHistogram.reset();
        executeAndResultHoldTimeHistogram.reset();
    }

    public long getConcurrentMax() {
        return concurrentMax.get();
    }

    public long getRunningCount() {
        return runningCount.get();
    }

    public void addUpdateCount(int delta) {
        this.updateCount.addAndGet(delta);
        this.updateCountHistogram.recode(delta);
        this.fetchRowCountHistogram.recode(0);
        
    }

    public long getUpdateCount() {
        return updateCount.get();
    }

    public long getFetchRowCount() {
        return fetchRowCount.get();
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
        this.fetchRowCount.addAndGet(delta);
        this.fetchRowCountHistogram.recode(delta);
        
    }

    public void addExecuteBatchCount(long batchSize) {
        executeBatchSizeTotal.addAndGet(batchSize);

        // executeBatchSizeMax
        for (;;) {
            long current = executeBatchSizeMax.get();
            if (current < batchSize) {
                if (executeBatchSizeMax.compareAndSet(current, batchSize)) {
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
        return executeBatchSizeTotal.get();
    }

    public void incrementExecuteSuccessCount() {
        executeSuccessCount.incrementAndGet();
    }

    public void incrementRunningCount() {
        long val = runningCount.incrementAndGet();

        for (;;) {
            long max = concurrentMax.get();
            if (val > max) {
                if (concurrentMax.compareAndSet(max, val)) {
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
        runningCount.decrementAndGet();
    }

    public void decrementExecutingCount() {
        runningCount.decrementAndGet();
    }

    public long getExecuteSuccessCount() {
        return executeSuccessCount.get();
    }
    
    public void addExecuteTime(StatementExecuteType executeType, long nanoSpan) {
    	if (StatementExecuteType.ExecuteQuery != executeType) {
    		executeAndResultHoldTimeHistogram.recode((nanoSpan)/1000/1000);
    	}
    	addExecuteTime(nanoSpan);
    }

    public void addExecuteTime(long nanoSpan) {
        executeSpanNanoTotal.addAndGet(nanoSpan);

        for (;;) {
            long current = executeSpanNanoMax.get();
            if (current < nanoSpan) {
                if (executeSpanNanoMax.compareAndSet(current, nanoSpan)) {
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
        histogram.recode(millis);
    }

    public long getExecuteMillisTotal() {
        return executeSpanNanoTotal.get() / (1000 * 1000);
    }

    public long getExecuteMillisMax() {
        return executeSpanNanoMax.get() / (1000 * 1000);
    }

    public long getErrorCount() {
        return executeErrorCount.get();
    }

    @Override
    public long getExecuteBatchSizeMax() {
        return executeBatchSizeMax.get();
    }

    public long getInTransactionCount() {
        return inTransactionCount.get();
    }

    public void incrementInTransactionCount() {
        inTransactionCount.incrementAndGet();
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
                
                // 30 - 
                
                new ArrayType<Long>(SimpleType.LONG, true), //
                new ArrayType<Long>(SimpleType.LONG, true), //
                
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
                "LastSlowParameters",
                "ResultSetHoldTime",
                "ExecuteAndResultSetHoldTime",
                "FetchRowCountHistogram",
                
                // 30
                "EffectedRowCountHistogram",
                "ExecuteAndResultHoldTimeHistogram"
                
                
                
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
        map.put("ExecuteAndResultHoldTimeHistogram", this.getExecuteAndResultHoldTimeHistogram().toArray()); // 30
       

        return map;
    }

    public CompositeDataSupport getCompositeData() throws JMException {
        return new CompositeDataSupport(getCompositeType(), getData());
    }

    public Throwable getExecuteErrorLast() {
        return executeErrorLast;
    }

    public void error(Throwable error) {
        executeErrorCount.incrementAndGet();
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
        return resultSetHoldTimeNano.get();
    }

    public long getExecuteAndResultSetHoldTimeNano() {
        return executeAndResultSetHoldTime.get();
    }
    
    public void addResultSetHoldTimeNano(long nano) {
        resultSetHoldTimeNano.addAndGet(nano);
    }

    public void addResultSetHoldTimeNano(long statementExecuteNano, long resultHoldTimeNano) {
    	resultSetHoldTimeNano.addAndGet(resultHoldTimeNano);    	
    	executeAndResultSetHoldTime.addAndGet(statementExecuteNano+resultHoldTimeNano);
    	executeAndResultHoldTimeHistogram.recode((statementExecuteNano+resultHoldTimeNano)/1000/1000);
    	updateCountHistogram.recode(0);
    }
}
