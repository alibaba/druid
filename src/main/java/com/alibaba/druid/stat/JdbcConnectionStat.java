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
public class JdbcConnectionStat implements JdbcConnectionStatMBean {

    private final AtomicInteger concurrentCount = new AtomicInteger();
    private final AtomicInteger concurrentMax   = new AtomicInteger();

    private final AtomicLong    count           = new AtomicLong();
    private final AtomicLong    errorCount      = new AtomicLong();

    private final AtomicLong    nanoTotal       = new AtomicLong();
    private Throwable           lastError;
    private long                lastErrorTime;

    private long                lastSampleTime  = 0;

    private final AtomicLong    closeCount      = new AtomicLong(0);  // 执行Connection.close的计数
    private final AtomicLong    commitCount     = new AtomicLong(0);  // 执行commit的计数
    private final AtomicLong    rollbackCount   = new AtomicLong(0);  // 执行rollback的计数
    private final AtomicLong    connectNanoSpan = new AtomicLong(0);  // 连接建立消耗时间总和（纳秒）

    public void reset() {
        concurrentMax.set(0);
        errorCount.set(0);
        nanoTotal.set(0);
        lastError = null;
        lastErrorTime = 0;
        lastSampleTime = 0;

        closeCount.set(0);
        commitCount.set(0);
        rollbackCount.set(0);
        connectNanoSpan.set(0);
    }

    public void beforeConnect() {
        int invoking = concurrentCount.incrementAndGet();

        for (;;) {
            int max = concurrentMax.get();
            if (invoking > max) {
                if (concurrentMax.compareAndSet(max, invoking)) {
                    break;
                } else {
                    continue;
                }
            } else {
                break;
            }
        }

        count.incrementAndGet();
        lastSampleTime = System.currentTimeMillis();
    }

    public void setConcurrentCount(int concurrentCount) {
        this.concurrentCount.set(concurrentCount);

        for (;;) {
            int max = concurrentMax.get();
            if (concurrentCount > max) {
                if (concurrentMax.compareAndSet(max, concurrentCount)) {
                    break;
                } else {
                    continue;
                }
            } else {
                break;
            }
        }
    }

    public long getErrorCount() {
        return errorCount.get();
    }

    public int getRunningCount() {
        return concurrentCount.get();
    }

    public int getConcurrentMax() {
        return concurrentMax.get();
    }

    public long getCount() {
        return count.get();
    }

    public Date getLastConnectTime() {
        if (lastSampleTime == 0) {
            return null;
        }

        return new Date(lastSampleTime);
    }

    public long getNanoTotal() {
        return nanoTotal.get();
    }

    public void afterClose(long nanoSpan) {
        concurrentCount.decrementAndGet();

        nanoTotal.addAndGet(nanoSpan);
    }

    public Throwable getLastError() {
        return lastError;
    }

    public Date getLastErrorTime() {
        if (lastErrorTime <= 0) {
            return null;
        }

        return new Date(lastErrorTime);
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
        return this.getCount();
    }

    @Override
    public long getConnectMillis() {
        return connectNanoSpan.get() / (1000 * 1000);
    }

    @Override
    public long getActiveMax() {
        return this.getConcurrentMax();
    }

    @Override
    public long getRollbackCount() {
        return rollbackCount.get();
    }

    @Override
    public long getConnectErrorCount() {
        return this.getErrorCount();
    }

    @Override
    public Date getConnectLastTime() {
        return this.getLastConnectTime();
    }

    public void addConnectionConnectNano(long delta) {
        connectNanoSpan.addAndGet(delta);
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

        private static String[] indexNames        = { "id", "connectTime", "connectTimespan", "establishTime", "aliveTimespan", "lastSql", "lastError",
                                                          "lastErrorTime", "connectStatckTrace", "lastStatementStackTrace", "dataSource" };
        private static String[] indexDescriptions = indexNames;

        public static CompositeType getCompositeType() throws JMException {
            OpenType<?>[] indexTypes = new OpenType<?>[] { SimpleType.LONG, SimpleType.DATE, SimpleType.LONG, SimpleType.DATE, SimpleType.LONG,

            SimpleType.STRING, JMXUtils.getThrowableCompositeType(), SimpleType.DATE, SimpleType.STRING, SimpleType.STRING,

            SimpleType.STRING };

            return new CompositeType("ConnectionStatistic", "Connection Statistic", indexNames, indexDescriptions, indexTypes);
        }

        public String getDataSource() {
            return this.dataSource;
        }

        public CompositeDataSupport getCompositeData() throws JMException {
            Map<String, Object> map = new HashMap<String, Object>();

            map.put("id", id);
            map.put("connectTime", getConnectTime());
            map.put("connectTimespan", getConnectTimespanNano() / (1000 * 1000));
            map.put("establishTime", getEstablishTime());
            map.put("aliveTimespan", (System.nanoTime() - getEstablishNano()) / (1000 * 1000));

            map.put("lastSql", getLastSql());
            map.put("lastError", JMXUtils.getErrorCompositeData(this.lastError));
            map.put("lastErrorTime", getLastErrorTime());
            map.put("connectStatckTrace", getConnectStackTrace());
            map.put("lastStatementStackTrace", getLastStatementStatckTrace());

            map.put("dataSource", this.getDataSource());

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
}
