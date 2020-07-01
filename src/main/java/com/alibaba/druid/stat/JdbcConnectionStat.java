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

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import com.alibaba.druid.util.Histogram;
import com.alibaba.druid.util.JMXUtils;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class JdbcConnectionStat implements JdbcConnectionStatMBean {

    private final AtomicInteger activeCount           = new AtomicInteger();
    private final AtomicInteger activeCountMax        = new AtomicInteger();

    private final AtomicInteger connectingCount       = new AtomicInteger();
    private final AtomicInteger connectingMax         = new AtomicInteger();

    private final AtomicLong    connectCount          = new AtomicLong();
    private final AtomicLong    connectErrorCount     = new AtomicLong();
    private Throwable           connectErrorLast;
    private final AtomicLong    connectNanoTotal      = new AtomicLong(0);                       // 连接建立消耗时间总和（纳秒）
    private final AtomicLong    connectNanoMax        = new AtomicLong(0);                       // 连接建立消耗最大时间（纳秒）

    private final AtomicLong    errorCount            = new AtomicLong();

    private final AtomicLong    aliveNanoTotal        = new AtomicLong();
    private Throwable           lastError;
    private long                lastErrorTime;

    private long                connectLastTime       = 0;

    private final AtomicLong    closeCount            = new AtomicLong(0);                       // 执行Connection.close的计数
    private final AtomicLong    transactionStartCount = new AtomicLong(0);
    private final AtomicLong    commitCount           = new AtomicLong(0);                       // 执行commit的计数
    private final AtomicLong    rollbackCount         = new AtomicLong(0);                       // 执行rollback的计数

    private final AtomicLong    aliveNanoMin          = new AtomicLong();
    private final AtomicLong    aliveNanoMax          = new AtomicLong();

    private final Histogram     histogram             = new Histogram(TimeUnit.SECONDS, new long[] { //
                                                                      1, 5, 15, 60, 300, 1800 });

    public JdbcConnectionStat(){

    }

    public void reset() {
        connectingMax.set(0);
        connectErrorCount.set(0);
        errorCount.set(0);
        aliveNanoTotal.set(0);
        aliveNanoMin.set(0);
        aliveNanoMax.set(0);
        lastError = null;
        lastErrorTime = 0;
        connectLastTime = 0;

        connectCount.set(0);
        closeCount.set(0);
        transactionStartCount.set(0);
        commitCount.set(0);
        rollbackCount.set(0);
        connectNanoTotal.set(0);
        connectNanoMax.set(0);

        histogram.reset();
    }

    public void beforeConnect() {
        int invoking = connectingCount.incrementAndGet();

        for (;;) {
            int max = connectingMax.get();
            if (invoking > max) {
                if (connectingMax.compareAndSet(max, invoking)) {
                    break;
                } else {
                    continue;
                }
            } else {
                break;
            }
        }

        connectCount.incrementAndGet();
        connectLastTime = System.currentTimeMillis();
    }

    public void afterConnected(long delta) {
        connectingCount.decrementAndGet();
        connectNanoTotal.addAndGet(delta);
        for (;;) {
            // connectNanoMax
            long max = connectNanoMax.get();
            if (delta > max) {
                if (connectNanoMax.compareAndSet(max, delta)) {
                    break;
                } else {
                    continue;
                }
            } else {
                break;
            }
        }

        activeCount.incrementAndGet();
    }

    public long getConnectNanoMax() {
        return this.connectNanoMax.get();
    }

    public long getConnectMillisMax() {
        return this.connectNanoMax.get() / (1000 * 1000);
    }

    public void setActiveCount(int activeCount) {
        this.activeCount.set(activeCount);

        for (;;) {
            int max = activeCountMax.get();
            if (activeCount > max) {
                if (activeCountMax.compareAndSet(max, activeCount)) {
                    break;
                } else {
                    continue;
                }
            } else {
                break;
            }
        }
    }

    public int getActiveCount() {
        return activeCount.get();
    }

    public int getAtiveCountMax() {
        return this.activeCount.get();
    }

    public long getErrorCount() {
        return errorCount.get();
    }

    public int getConnectingCount() {
        return connectingCount.get();
    }

    public int getConnectingMax() {
        return connectingMax.get();
    }

    public long getAliveTotal() {
        return aliveNanoTotal.get();
    }

    public long getAliveNanoMin() {
        return aliveNanoMin.get();
    }

    public long getAliveMillisMin() {
        return aliveNanoMin.get() / (1000 * 1000);
    }

    public long getAliveNanoMax() {
        return aliveNanoMax.get();
    }

    public long getAliveMillisMax() {
        return aliveNanoMax.get() / (1000 * 1000);
    }

    public void afterClose(long aliveNano) {
        activeCount.decrementAndGet();
        aliveNanoTotal.addAndGet(aliveNano);

        for (;;) {
            long max = aliveNanoMax.get();
            if (aliveNano > max) {
                if (aliveNanoMax.compareAndSet(max, aliveNano)) {
                    break;
                } else {
                    continue;
                }
            } else {
                break;
            }
        }

        for (;;) {
            long min = aliveNanoMin.get();
            if (min == 0 || aliveNano < min) {
                if (aliveNanoMin.compareAndSet(min, aliveNano)) {
                    break;
                } else {
                    continue;
                }
            } else {
                break;
            }
        }

        long aliveMillis = aliveNano / (1000 * 1000);
        histogram.record(aliveMillis);
    }

    public Throwable getErrorLast() {
        return lastError;
    }

    public Throwable getConnectErrorLast() {
        return this.connectErrorLast;
    }

    public Date getErrorLastTime() {
        if (lastErrorTime <= 0) {
            return null;
        }

        return new Date(lastErrorTime);
    }

    public void connectError(Throwable error) {
        connectErrorCount.incrementAndGet();
        connectErrorLast = error;

        errorCount.incrementAndGet();
        lastError = error;
        lastErrorTime = System.currentTimeMillis();
    }

    public void error(Throwable error) {
        errorCount.incrementAndGet();
        lastError = error;
        lastErrorTime = System.currentTimeMillis();
    }

    @Override
    public long getCloseCount() {
        return this.closeCount.get();
    }

    @Override
    public long getCommitCount() {
        return this.commitCount.get();
    }

    @Override
    public long getConnectCount() {
        return connectCount.get();
    }

    @Override
    public long getConnectMillis() {
        return connectNanoTotal.get() / (1000 * 1000);
    }

    @Override
    public int getActiveMax() {
        return this.activeCountMax.get();
    }

    @Override
    public long getRollbackCount() {
        return rollbackCount.get();
    }

    @Override
    public long getConnectErrorCount() {
        return connectErrorCount.get();
    }

    @Override
    public Date getConnectLastTime() {
        if (connectLastTime == 0) {
            return null;
        }

        return new Date(connectLastTime);
    }

    public void incrementConnectionCloseCount() {
        closeCount.incrementAndGet();
    }

    public void incrementConnectionCommitCount() {
        commitCount.incrementAndGet();
    }

    public void incrementConnectionRollbackCount() {
        rollbackCount.incrementAndGet();
    }

    public void incrementTransactionStartCount() {
        transactionStartCount.incrementAndGet();
    }

    public long getTransactionStartCount() {
        return transactionStartCount.get();
    }

    public static class Entry implements EntryMBean {

        private long         id;
        private long         establishTime;
        private long         establishNano;
        private Date         connectTime;
        private long         connectTimespanNano;
        private Exception    connectStackTraceException;

        private String       lastSql;
        private Exception    lastStatementStatckTraceException;
        protected Throwable  lastError;
        protected long       lastErrorTime;
        private final String dataSource;

        public Entry(String dataSource, long connectionId){
            this.id = connectionId;
            this.dataSource = dataSource;
        }

        public void reset() {
            this.lastSql = null;
            this.lastStatementStatckTraceException = null;
            this.lastError = null;
            this.lastErrorTime = 0;
        }

        public Date getEstablishTime() {
            if (establishTime <= 0) {
                return null;
            }
            return new Date(establishTime);
        }

        public void setEstablishTime(long establishTime) {
            this.establishTime = establishTime;
        }

        public long getEstablishNano() {
            return establishNano;
        }

        public void setEstablishNano(long establishNano) {
            this.establishNano = establishNano;
        }

        public Date getConnectTime() {
            return connectTime;
        }

        public void setConnectTime(Date connectTime) {
            this.connectTime = connectTime;
        }

        public long getConnectTimespanNano() {
            return connectTimespanNano;
        }

        public void setConnectTimespanNano(long connectTimespanNano) {
            this.connectTimespanNano = connectTimespanNano;
        }

        public String getLastSql() {
            return lastSql;
        }

        public void setLastSql(String lastSql) {
            this.lastSql = lastSql;
        }

        public String getConnectStackTrace() {
            if (connectStackTraceException == null) {
                return null;
            }

            StringWriter buf = new StringWriter();
            connectStackTraceException.printStackTrace(new PrintWriter(buf));
            return buf.toString();
        }

        public void setConnectStackTrace(Exception connectStackTraceException) {
            this.connectStackTraceException = connectStackTraceException;
        }

        public String getLastStatementStatckTrace() {
            if (lastStatementStatckTraceException == null) {
                return null;
            }

            StringWriter buf = new StringWriter();
            lastStatementStatckTraceException.printStackTrace(new PrintWriter(buf));
            return buf.toString();
        }

        public void setLastStatementStatckTrace(Exception lastStatementStatckTrace) {
            this.lastStatementStatckTraceException = lastStatementStatckTrace;
        }

        public void error(Throwable lastError) {
            this.lastError = lastError;
            this.lastErrorTime = System.currentTimeMillis();
        }

        public Date getLastErrorTime() {
            if (lastErrorTime <= 0) {
                return null;
            }
            return new Date(lastErrorTime);
        }

        private static String[] indexNames        = { "ID", "ConnectTime", "ConnectTimespan", "EstablishTime",
                                                          "AliveTimespan", "LastSql", "LastError", "LastErrorTime",
                                                          "ConnectStatckTrace", "LastStatementStackTrace", "DataSource" };
        private static String[] indexDescriptions = indexNames;

        public static CompositeType getCompositeType() throws JMException {
            OpenType<?>[] indexTypes = new OpenType<?>[] { SimpleType.LONG, SimpleType.DATE, SimpleType.LONG,
                    SimpleType.DATE, SimpleType.LONG,

                    SimpleType.STRING, JMXUtils.getThrowableCompositeType(), SimpleType.DATE, SimpleType.STRING,
                    SimpleType.STRING,

                    SimpleType.STRING };

            return new CompositeType("ConnectionStatistic", "Connection Statistic", indexNames, indexDescriptions,
                                     indexTypes);
        }

        public String getDataSource() {
            return this.dataSource;
        }

        public CompositeDataSupport getCompositeData() throws JMException {
            Map<String, Object> map = new HashMap<String, Object>();

            map.put("ID", id);
            map.put("ConnectTime", getConnectTime());
            map.put("ConnectTimespan", getConnectTimespanNano() / (1000 * 1000));
            map.put("EstablishTime", getEstablishTime());
            map.put("AliveTimespan", (System.nanoTime() - getEstablishNano()) / (1000 * 1000));

            map.put("LastSql", getLastSql());
            map.put("LastError", JMXUtils.getErrorCompositeData(this.lastError));
            map.put("LastErrorTime", getLastErrorTime());
            map.put("ConnectStatckTrace", getConnectStackTrace());
            map.put("LastStatementStackTrace", getLastStatementStatckTrace());

            map.put("DataSource", this.getDataSource());

            return new CompositeDataSupport(getCompositeType(), map);
        }
    }

    public interface EntryMBean {

        Date getEstablishTime();

        long getEstablishNano();

        Date getConnectTime();

        long getConnectTimespanNano();

        String getLastSql();

        String getConnectStackTrace();

        String getLastStatementStatckTrace();

        Date getLastErrorTime();

        void reset();
    }

    public long[] getHistorgramValues() {
        return this.histogram.toArray();
    }

    public long[] getHistogramRanges() {
        return this.histogram.getRanges();
    }
}
