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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

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
    private String           name;
    private String           file;
    private String           dbType;

    private long             executeNanoSpanMaxOccurTime;

    private final AtomicLong executeErrorCount     = new AtomicLong();
    private Throwable        executeErrorLast;
    private long             executeErrorLastTime;

    private final AtomicLong updateCount           = new AtomicLong();
    private final AtomicLong fetchRowCount         = new AtomicLong();
    
    private AtomicInteger       count_0_2         = new AtomicInteger();
    private AtomicInteger       count_2_5         = new AtomicInteger();
    private AtomicInteger       count_5_10        = new AtomicInteger();
    private AtomicInteger       count_10_20       = new AtomicInteger();
    private AtomicInteger       count_20_50       = new AtomicInteger();
    private AtomicInteger       count_50_100      = new AtomicInteger();
    private AtomicInteger       count_100_200     = new AtomicInteger();
    private AtomicInteger       count_200_500     = new AtomicInteger();
    private AtomicInteger       count_500_1000    = new AtomicInteger();
    private AtomicInteger       count_1000_2000   = new AtomicInteger();
    private AtomicInteger       count_2000_5000   = new AtomicInteger();
    private AtomicInteger       count_5000_10000  = new AtomicInteger();
    private AtomicInteger       count_10000_20000 = new AtomicInteger();
    private AtomicInteger       count_20000_50000 = new AtomicInteger();
    private AtomicInteger       count_50000_more  = new AtomicInteger();

    public JdbcSqlStat(String sql){
        this.sql = sql;
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
        
        count_0_2.set(0);
        count_2_5.set(0);
        count_5_10.set(0);
        count_10_20.set(0);
        count_20_50.set(0);
        count_50_100.set(0);
        count_100_200.set(0);
        count_200_500.set(0);
        count_500_1000.set(0);
        count_1000_2000.set(0);
        count_2000_5000.set(0);
        count_5000_10000.set(0);
        count_10000_20000.set(0);
        count_20000_50000.set(0);
        count_50000_more.set(0);
    }

    public long getConcurrentMax() {
        return concurrentMax.get();
    }

    public long getRunningCount() {
        return runningCount.get();
    }

    public void addUpdateCount(int delta) {
        this.updateCount.addAndGet(delta);
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

    public void decrementExecutingCount() {
        runningCount.decrementAndGet();
    }

    public long getExecuteSuccessCount() {
        return executeSuccessCount.get();
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
        

        if (nanoSpan <= 2 * 1000 * 1000) {
            count_0_2.incrementAndGet();
        } else if (nanoSpan <= 5 * 1000 * 1000) {
            count_2_5.incrementAndGet();
        } else if (nanoSpan <= 10 * 1000 * 1000) {
            count_5_10.incrementAndGet();
        } else if (nanoSpan <= 20 * 1000 * 1000) {
            count_10_20.incrementAndGet();
        } else if (nanoSpan <= 50 * 1000 * 1000) {
            count_20_50.incrementAndGet();
        } else if (nanoSpan <= 100 * 1000 * 1000) {
            count_50_100.incrementAndGet();
        } else if (nanoSpan <= 200 * 1000 * 1000) {
            count_100_200.incrementAndGet();
        } else if (nanoSpan <= 500 * 1000 * 1000) {
            count_200_500.incrementAndGet();
        } else if (nanoSpan <= 1000 * 1000 * 1000) {
            count_500_1000.incrementAndGet();
        } else if (nanoSpan <= 2000 * 1000 * 1000) {
            count_1000_2000.incrementAndGet();
        } else if (nanoSpan <= 5000 * 1000 * 1000) {
            count_2000_5000.incrementAndGet();
        } else if (nanoSpan <= 10000 * 1000 * 1000) {
            count_5000_10000.incrementAndGet();
        } else if (nanoSpan <= 20000 * 1000 * 1000) {
            count_10000_20000.incrementAndGet();
        } else if (nanoSpan <= 50000 * 1000 * 1000) {
            count_20000_50000.incrementAndGet();
        } else {
            count_50000_more.incrementAndGet();
        }
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
    
    public int getCount_0_2() {
        return count_0_2.get();
    }

    public int getCount_2_5() {
        return count_2_5.get();
    }

    public int getCount_5_10() {
        return count_5_10.get();
    }

    public int getCount_10_20() {
        return count_10_20.get();
    }

    public int getCount_20_50() {
        return count_20_50.get();
    }

    public int getCount_50_100() {
        return count_50_100.get();
    }

    public int getCount_100_200() {
        return count_100_200.get();
    }

    public int getCount_200_500() {
        return count_200_500.get();
    }

    public int getCount_500_1000() {
        return count_500_1000.get();
    }

    public int getCount_1000_2000() {
        return count_1000_2000.get();
    }

    public int getCount_2000_5000() {
        return count_2000_5000.get();
    }

    public int getCount_5000_10000() {
        return count_5000_10000.get();
    }

    public int getCount_10000_20000() {
        return count_10000_20000.get();
    }

    public int getCount_20000_50000() {
        return count_20000_50000.get();
    }

    public int getCount_50000_more() {
        return count_50000_more.get();
    }

    private static CompositeType COMPOSITE_TYPE = null;

    public static CompositeType getCompositeType() throws JMException {

        if (COMPOSITE_TYPE != null) {
            return COMPOSITE_TYPE;
        }

        OpenType<?>[] indexTypes = new OpenType<?>[] { SimpleType.LONG, SimpleType.STRING, SimpleType.STRING, SimpleType.LONG, SimpleType.LONG,
                SimpleType.LONG, SimpleType.DATE, SimpleType.LONG, JMXUtils.getThrowableCompositeType(), SimpleType.LONG, SimpleType.LONG, SimpleType.DATE,
                SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.LONG, SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.STRING,
                SimpleType.STRING, SimpleType.DATE, SimpleType.STRING, SimpleType.STRING //
        
                , SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.INTEGER  //     
                , SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.INTEGER  //     
                , SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.INTEGER, SimpleType.INTEGER  // 
        };

        String[] indexNames = { "ID", "DataSource", "SQL", "ExecuteCount", "ErrorCount", "TotalTime", "LastTime", "MaxTimespan", "LastError",
                "EffectedRowCount", "FetchRowCount", "MaxTimespanOccurTime", "BatchSizeMax", "BatchSizeTotal", "ConcurrentMax", "RunningCount", "Name", "File",
                "LastErrorMessage", "LastErrorClass", "LastErrorStackTrace", "LastErrorTime", "DbType", "URL", //
                
                "Count_0_2", "Count_2_5", "Count_5_10", "Count_10_20", "Count_20_50" //

                , "Count_50_100", "Count_100_200", "Count_200_500", "Count_500_1000", "Count_1000_2000" //

                , "Count_2000_5000", "Count_5000_10000", "Count_10000_20000", "Count_20000_50000", "Count_50000_more", //
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

        map.put("ID", id);
        map.put("DataSource", dataSource);
        map.put("SQL", sql);
        map.put("ExecuteCount", getExecuteCount());
        map.put("ErrorCount", getErrorCount());
        map.put("TotalTime", getExecuteMillisTotal());

        map.put("LastTime", getExecuteLastStartTime());
        map.put("MaxTimespan", getExecuteMillisMax());
        map.put("LastError", JMXUtils.getErrorCompositeData(this.getExecuteErrorLast()));
        map.put("EffectedRowCount", getUpdateCount());
        map.put("FetchRowCount", getFetchRowCount());

        map.put("MaxTimespanOccurTime", getExecuteNanoSpanMaxOccurTime());
        map.put("BatchSizeMax", getExecuteBatchSizeMax());
        map.put("BatchSizeTotal", getExecuteBatchSizeTotal());
        map.put("ConcurrentMax", getConcurrentMax());
        map.put("RunningCount", getRunningCount());

        map.put("Name", getName());
        map.put("File", getFile());

        Throwable lastError = this.executeErrorLast;
        if (lastError != null) {
            map.put("LastErrorMessage", lastError.getMessage());
            map.put("LastErrorClass", lastError.getClass().getName());

            StringWriter buf = new StringWriter();
            lastError.printStackTrace(new PrintWriter(buf));
            map.put("LastErrorStackTrace", buf.toString());
            map.put("LastErrorTime", new Date(executeErrorLastTime));
        } else {
            map.put("LastErrorMessage", null);
            map.put("LastErrorClass", null);
            map.put("LastErrorStackTrace", null);
            map.put("LastErrorTime", null);
        }

        map.put("DbType", dbType);
        map.put("URL", null);
        
        map.put("Count_0_2", this.getCount_0_2());
        map.put("Count_2_5", this.getCount_2_5());
        map.put("Count_5_10", this.getCount_5_10());
        map.put("Count_10_20", this.getCount_10_20());
        map.put("Count_20_50", this.getCount_20_50());

        map.put("Count_50_100", this.getCount_50_100());
        map.put("Count_100_200", this.getCount_100_200());
        map.put("Count_200_500", this.getCount_200_500());
        map.put("Count_500_1000", this.getCount_500_1000());
        map.put("Count_1000_2000", this.getCount_1000_2000());

        map.put("Count_2000_5000", this.getCount_2000_5000());
        map.put("Count_5000_10000", this.getCount_5000_10000());
        map.put("Count_10000_20000", this.getCount_10000_20000());
        map.put("Count_20000_50000", this.getCount_20000_50000());
        map.put("Count_50000_more", this.getCount_50000_more());
        
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
}
