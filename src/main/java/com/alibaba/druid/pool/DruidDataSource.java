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
package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;

import com.alibaba.druid.VERSION;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.vendor.InformixExceptionSorter;
import com.alibaba.druid.pool.vendor.MSSQLValidConnectionChecker;
import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;
import com.alibaba.druid.pool.vendor.MySqlValidConnectionChecker;
import com.alibaba.druid.pool.vendor.OracleExceptionSorter;
import com.alibaba.druid.pool.vendor.OracleValidConnectionChecker;
import com.alibaba.druid.pool.vendor.SybaseExceptionSorter;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyConfig;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.util.JdbcUtils;

/**
 * @author ljw<ljw2083@alibaba-inc.com>
 * @author wenshao<szujobs@hotmail.com>
 */
public class DruidDataSource extends DruidAbstractDataSource implements DruidDataSourceMBean, ManagedDataSource, Referenceable {

    private final static Log        LOG                     = LogFactory.getLog(DruidDataSource.class);

    private static final long       serialVersionUID        = 1L;

    private final ReentrantLock     lock                    = new ReentrantLock();

    private final Condition         notEmpty                = lock.newCondition();
    private final Condition         empty                   = lock.newCondition();

    // stats
    private long                    connectCount            = 0L;
    private long                    closeCount              = 0L;
    private long                    connectErrorCount       = 0L;
    private long                    recycleCount            = 0L;
    private long                    createConnectionCount   = 0L;
    private long                    destroyCount            = 0L;
    private long                    removeAbandonedCount    = 0L;
    private long                    notEmptyWaitCount       = 0L;
    private long                    notEmptySignalCount     = 0L;
    private long                    notEmptyWaitNanos       = 0L;

    // store
    private ConnectionHolder[]      connections;
    private int                     poolingCount            = 0;
    private int                     activeCount             = 0;
    private int                     notEmptyWaitThreadCount = 0;

    // threads
    private CreateConnectionThread  createConnectionThread;
    private DestroyConnectionThread destoryConnectionThread;

    private final CountDownLatch    initedLatch             = new CountDownLatch(2);

    private boolean                 enable                  = false;

    private boolean                 resetStatEnable         = true;

    public DruidDataSource(){
    }

    public boolean isResetStatEnable() {
        return resetStatEnable;
    }

    public void setResetStatEnable(boolean resetStatEnable) {
        this.resetStatEnable = resetStatEnable;
    }

    public void resetStat() {
        if (!resetStatEnable) {
            return;
        }

        lock.lock();
        try {
            connectCount = 0;
            closeCount = 0;
            connectErrorCount = 0;
            recycleCount = 0;
            createConnectionCount = 0;
            destroyCount = 0;
            removeAbandonedCount = 0;
            notEmptyWaitCount = 0;
            notEmptySignalCount = 0L;
            notEmptyWaitNanos = 0;
        } finally {
            lock.unlock();
        }
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        lock.lock();
        try {
            this.enable = enable;
            if (!enable) {
                notEmpty.signalAll();
                notEmptySignalCount++;
            }
        } finally {
            lock.unlock();
        }
    }

    private void init() throws SQLException {
        if (inited) {
            return;
        }

        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            throw new SQLException("interrupt", e);
        }

        try {
            if (inited) {
                return;
            }

            this.id = DruidDriver.createDataSourceId();

            enable = true;

            if (maxActive <= 0) {
                throw new IllegalArgumentException("illegal maxActive " + maxActive);
            }

            if (maxActive < minIdle) {
                throw new IllegalArgumentException("illegal maxActive " + maxActive);
            }

            if (maxIdle <= 0 || maxIdle < minIdle) {
                throw new IllegalArgumentException("illegal maxPoolSize");
            }

            if (getInitialSize() > maxActive) {
                throw new IllegalArgumentException("illegal initialSize");
            }

            if (this.driverClass != null) {
                this.driverClass = driverClass.trim();
            }

            if (this.jdbcUrl != null) {
                this.jdbcUrl = this.jdbcUrl.trim();

                if (jdbcUrl.startsWith(DruidDriver.DEFAULT_PREFIX)) {
                    DataSourceProxyConfig config = DruidDriver.parseConfig(jdbcUrl, null);
                    this.driverClass = config.getRawDriverClassName();
                    this.jdbcUrl = config.getRawUrl();
                    if (this.name == null) {
                        this.name = config.getName();
                    }
                    this.filters.addAll(config.getFilters());
                }
            }

            if (this.driver == null) {
                if (this.driverClass == null || this.driverClass.isEmpty()) {
                    this.driverClass = JdbcUtils.getDriverClassName(this.jdbcUrl);
                }

                try {
                    driver = (Driver) Class.forName(this.driverClass).newInstance();
                } catch (IllegalAccessException e) {
                    throw new SQLException(e.getMessage(), e);
                } catch (InstantiationException e) {
                    throw new SQLException(e.getMessage(), e);
                } catch (ClassNotFoundException e) {
                    throw new SQLException(e.getMessage(), e);
                }
            } else {
                if (this.driverClass == null) {
                    this.driverClass = driver.getClass().getName();
                }
            }

            this.dbType = JdbcUtils.getDbType(jdbcUrl, driverClass.getClass().getName());

            String realDriverClassName = driver.getClass().getName();
            if (realDriverClassName.equals("com.mysql.jdbc.Driver")) {
                this.validConnectionChecker = new MySqlValidConnectionChecker();
                this.exceptionSoter = new MySqlExceptionSorter();

            } else if (realDriverClassName.equals("oracle.jdbc.driver.OracleDriver")) {
                this.validConnectionChecker = new OracleValidConnectionChecker();
                this.exceptionSoter = new OracleExceptionSorter();

            } else if (realDriverClassName.equals("com.microsoft.jdbc.sqlserver.SQLServerDriver")) {
                this.validConnectionChecker = new MSSQLValidConnectionChecker();

            } else if (realDriverClassName.equals("com.informix.jdbc.IfxDriver")) {
                this.exceptionSoter = new InformixExceptionSorter();

            } else if (realDriverClassName.equals("com.sybase.jdbc2.jdbc.SybDriver")) {
                this.exceptionSoter = new SybaseExceptionSorter();
            }

            for (Filter filter : filters) {
                filter.init(this);
            }

            initConnectionFactory();

            connections = new ConnectionHolder[maxActive];

            SQLException connectError = null;

            try {
                // 初始化连接
                for (int i = 0, size = getInitialSize(); i < size; ++i) {
                    Connection conn = connectionFactory.createConnection();
                    conn.setAutoCommit(true);
                    connections[poolingCount++] = new ConnectionHolder(this, conn);
                }
            } catch (SQLException ex) {
                LOG.error("init datasource error", ex);
                connectError = ex;
            }

            createConnectionThread = new CreateConnectionThread("Druid-ConnectionPool-Create");
            createConnectionThread.setDaemon(true);
            destoryConnectionThread = new DestroyConnectionThread("Druid-ConnectionPool-Destory");
            destoryConnectionThread.setDaemon(true);

            createConnectionThread.start();
            destoryConnectionThread.start();

            initedLatch.await();
            inited = true;

            createdTime = new Date();
            DruidDataSourceStatManager.add(this);

            if (connectError != null && poolingCount == 0) {
                throw connectError;
            }
        } catch (InterruptedException e) {
            throw new SQLException(e.getMessage(), e);
        } finally {
            lock.unlock();
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        init();

        final int maxWaitThreadCount = getMaxWaitThreadCount();
        if (maxWaitThreadCount > 0) {
            if (notEmptyWaitThreadCount > maxWaitThreadCount) {
                lock.lock();
                try {
                    connectErrorCount++;
                } finally {
                    lock.unlock();
                }
                throw new SQLException("maxWaitThreadCount " + maxWaitThreadCount + ", current wait Thread count "
                                       + lock.getQueueLength());
            }
        }

        for (;;) {
            PoolableConnection poolalbeConnection = getConnectionInternal();

            if (isTestOnBorrow()) {
                boolean validate = testConnectionInternal(poolalbeConnection.getConnection());
                if (!validate) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("skip not validate connection.");
                    }

                    Connection realConnection = poolalbeConnection.getConnection();
                    discardConnection(realConnection);
                    continue;
                }
            } else {
                Connection realConnection = poolalbeConnection.getConnection();
                if (realConnection.isClosed()) {
                    discardConnection(null); // 传入null，避免重复关闭
                    continue;
                }

                if (isTestWhileIdle()) {
                    long idleMillis = System.currentTimeMillis()
                                      - poolalbeConnection.getConnectionHolder().getLastActiveTimeMillis();
                    if (idleMillis >= this.getTimeBetweenEvictionRunsMillis()) {
                        boolean validate = testConnectionInternal(poolalbeConnection.getConnection());
                        if (!validate) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("skip not validate connection.");
                            }

                            discardConnection(realConnection);
                            continue;
                        }
                    }
                }
            }

            if (isRemoveAbandoned()) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                activeConnections.put(poolalbeConnection,
                                      new ActiveConnectionTraceInfo(poolalbeConnection, System.currentTimeMillis(),
                                                                    stackTrace));
            }

            return poolalbeConnection;
        }
    }

    /**
     * 抛弃连接，不进行回收，而是抛弃
     * 
     * @param realConnection
     * @throws SQLException
     */
    private void discardConnection(Connection realConnection) throws SQLException {
        JdbcUtils.close(realConnection);

        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            throw new SQLException("interrupt", e);
        }

        try {
            activeCount--;
        } finally {
            lock.unlock();
        }
    }

    private PoolableConnection getConnectionInternal() throws SQLException {
        PoolableConnection poolalbeConnection;

        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            throw new SQLException("interrupt", e);
        }

        try {
            if (!enable) {
                connectErrorCount++;
                throw new DataSourceDisableException();
            }

            connectCount++;

            ConnectionHolder holder;

            if (maxWait > 0) {
                holder = pollLast(maxWait, TimeUnit.MILLISECONDS);
            } else {
                holder = takeLast();
            }

            if (holder == null) {
                throw new SQLException("can not get connection");
            }

            holder.incrementUseCount();
            activeCount++;

            poolalbeConnection = new PoolableConnection(holder);
        } catch (InterruptedException e) {
            connectErrorCount++;
            throw new SQLException(e.getMessage(), e);
        } catch (SQLException e) {
            connectErrorCount++;
            throw e;
        } finally {
            lock.unlock();
        }
        return poolalbeConnection;
    }

    public void handleConnectionException(PoolableConnection pooledConnection, Throwable t) throws SQLException {
        final ConnectionHolder holder = pooledConnection.getConnectionHolder();

        errorCount.incrementAndGet();

        if (t instanceof SQLException) {
            SQLException sqlEx = (SQLException) t;

            // broadcastConnectionError
            ConnectionEvent event = new ConnectionEvent(pooledConnection, sqlEx);
            for (ConnectionEventListener eventListener : holder.getConnectionEventListeners()) {
                eventListener.connectionErrorOccurred(event);
            }

            // exceptionSorter.isExceptionFatal
            if (exceptionSoter != null && exceptionSoter.isExceptionFatal(sqlEx)) {
                this.discardConnection(holder.getConnection());
                pooledConnection.disable();
            }

            throw sqlEx;
        } else {
            throw new SQLException("Error", t);
        }
    }

    /**
     * 回收连接
     */
    protected void recycle(PoolableConnection pooledConnection) throws SQLException {
        final Connection conn = pooledConnection.getConnection();
        final ConnectionHolder holder = pooledConnection.getConnectionHolder();

        assert holder != null;

        if (isRemoveAbandoned()) {
            activeConnections.remove(pooledConnection);
        }

        try {
            // 第一步，检查连接是否关闭
            if (conn == null || conn.isClosed()) {
                lock.lockInterruptibly();
                try {
                    activeCount--;
                    closeCount++;
                } finally {
                    lock.unlock();
                }
                return;
            }

            final boolean isAutoCommit = conn.getAutoCommit();
            final boolean isReadOnly = conn.isReadOnly();

            // 第二步，检查是否需要回滚
            if ((!isAutoCommit) && (!isReadOnly)) {
                conn.rollback();
            }

            // 第三步，清楚警告信息，重设autoCommit为true
            conn.clearWarnings();
            if (!isAutoCommit) {
                conn.setAutoCommit(true);
            }

            if (isTestOnReturn()) {
                boolean validate = testConnectionInternal(conn);
                if (!validate) {
                    JdbcUtils.close(conn);

                    lock.lockInterruptibly();
                    try {
                        destroyCount++;
                        activeCount--;
                        closeCount++;
                    } finally {
                        lock.unlock();
                    }
                    return;
                }
            }

            lock.lockInterruptibly();
            try {
                activeCount--;
                closeCount++;

                // 第六步，加入队列中(putLast)
                putLast(holder);
                recycleCount++;
            } finally {
                lock.unlock();
            }
        } catch (Throwable e) {
            JdbcUtils.close(conn);

            try {
                lock.lockInterruptibly();
            } catch (InterruptedException interruptEx) {
                throw new SQLException("interrupt", interruptEx);
            }

            try {
                activeCount--;
                closeCount++;
            } finally {
                lock.unlock();
            }

            throw new SQLException("recyle error", e);
        }
    }

    /**
     * close datasource
     */
    public void close() {
        lock.lock();
        try {
            if (createConnectionThread != null) {
                createConnectionThread.interrupt();
            }

            if (destoryConnectionThread != null) {
                destoryConnectionThread.interrupt();
            }

            for (int i = 0; i < poolingCount; ++i) {
                try {
                    connections[i].getConnection().close();
                    connections[i] = null;
                    destroyCount++;
                } catch (Exception ex) {
                    LOG.warn("close connection error", ex);
                }
            }
            poolingCount = 0;
            DruidDataSourceStatManager.remove(this);

            enable = false;
            notEmpty.signalAll();
            notEmptySignalCount++;
        } finally {
            lock.unlock();
        }
    }

    void incrementCreateCount() {
        createConnectionCount++;
    }

    void putLast(ConnectionHolder e) throws SQLException {
        if (!enable) {
            discardConnection(e.getConnection());
            return;
        }

        e.setLastActiveTimeMillis(System.currentTimeMillis());
        connections[poolingCount++] = e;

        notEmpty.signal();
        notEmptySignalCount++;
    }

    ConnectionHolder takeLast() throws InterruptedException, SQLException {
        try {
            while (poolingCount == 0) {
                empty.signal(); // send signal to CreateThread create connection
                notEmptyWaitThreadCount++;
                try {
                    notEmpty.await(); // signal by recycle or creator
                } finally {
                    notEmptyWaitThreadCount--;
                }
                notEmptyWaitCount++;

                if (!enable) {
                    connectErrorCount++;
                    throw new DataSourceDisableException();
                }
            }
        } catch (InterruptedException ie) {
            notEmpty.signal(); // propagate to non-interrupted thread
            notEmptySignalCount++;
            throw ie;
        }

        poolingCount--;
        ConnectionHolder last = connections[poolingCount];
        connections[poolingCount] = null;

        return last;
    }

    ConnectionHolder pollLast(long timeout, TimeUnit unit) throws InterruptedException, SQLException {
        long estimate = unit.toNanos(timeout);

        for (;;) {
            if (poolingCount == 0) {
                empty.signal(); // send signal to CreateThread create connection

                if (estimate <= 0) {
                    throw new GetConnectionTimeoutException();
                }

                notEmptyWaitThreadCount++;
                try {
                    long startEstimate = estimate;
                    estimate = notEmpty.awaitNanos(estimate); // signal by recycle or creator
                    notEmptyWaitCount++;
                    notEmptyWaitNanos += (startEstimate - estimate);

                    if (!enable) {
                        connectErrorCount++;
                        throw new DataSourceDisableException();
                    }
                } catch (InterruptedException ie) {
                    notEmpty.signal(); // propagate to non-interrupted thread
                    notEmptySignalCount++;
                    throw ie;
                } finally {
                    notEmptyWaitThreadCount--;
                }

                if (poolingCount == 0) {
                    if (estimate > 0) {
                        continue;
                    }

                    throw new GetConnectionTimeoutException();
                }
            }

            poolingCount--;
            ConnectionHolder last = connections[poolingCount];
            connections[poolingCount] = null;

            return last;
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("Not supported by DruidDataSource");
    }

    public long getCreateCount() {
        return createConnectionCount;
    }

    public long getDestroyCount() {
        return destroyCount;
    }

    public long getConnectCount() {
        return connectCount;
    }

    public long getCloseCount() {
        return closeCount;
    }

    public long getConnectErrorCount() {
        return connectErrorCount;
    }

    @Override
    public int getPoolingCount() {
        lock.lock();
        try {
            return poolingCount;
        } finally {
            lock.unlock();
        }
    }

    public long getRecycleCount() {
        return recycleCount;
    }

    public int getActiveCount() {
        lock.lock();
        try {
            return activeCount;
        } finally {
            lock.unlock();
        }
    }

    public long getRemoveAbandonedCount() {
        return removeAbandonedCount;
    }

    public class CreateConnectionThread extends Thread {

        public CreateConnectionThread(String name){
            super(name);
        }

        public void run() {
            initedLatch.countDown();

            int errorCount = 0;
            for (;;) {
                // addLast
                try {
                    lock.lockInterruptibly();
                } catch (InterruptedException e2) {
                    break;
                }

                try {
                    // 必须存在线程等待，才创建连接
                    if (poolingCount >= notEmptyWaitThreadCount) {
                        empty.await();
                        continue;
                    }

                    // 防止创建超过maxActive数量的连接
                    if (activeCount + poolingCount >= maxActive) {
                        empty.await();
                        continue;
                    }

                } catch (InterruptedException e) {
                    break;
                } catch (RuntimeException e) {
                    LOG.error("create connection error", e);
                } catch (Error e) {
                    LOG.error("create connection error", e);
                    break;
                } finally {
                    lock.unlock();
                }

                Connection connection = null;

                try {
                    connection = connectionFactory.createConnection();
                } catch (SQLException e) {
                    LOG.error("create connection error", e);

                    errorCount++;

                    if (errorCount > connectionErrorRetryAttempts && timeBetweenConnectErrorMillis > 0) {
                        if (breakAfterAcquireFailure) {
                            break;
                        }

                        try {
                            Thread.sleep(timeBetweenConnectErrorMillis);
                        } catch (InterruptedException interruptEx) {
                            break;
                        }
                    }
                } catch (RuntimeException e) {
                    LOG.error("create connection error", e);
                    continue;
                } catch (Error e) {
                    LOG.error("create connection error", e);
                    break;
                }

                if (connection == null) {
                    continue;
                }

                lock.lock();
                try {
                    connections[poolingCount++] = new ConnectionHolder(DruidDataSource.this, connection);

                    errorCount = 0; // reset errorCount

                    notEmpty.signal();
                    notEmptySignalCount++;
                } finally {
                    lock.unlock();
                }
            }
        }
    }

    public class DestroyConnectionThread extends Thread {

        public DestroyConnectionThread(String name){
            super(name);
        }

        public void run() {
            initedLatch.countDown();

            for (;;) {
                // 从前面开始删除
                try {
                    if (timeBetweenEvictionRunsMillis > 0) {
                        Thread.sleep(timeBetweenEvictionRunsMillis);
                    } else {
                        Thread.sleep(1000); //
                    }

                    if (Thread.interrupted()) {
                        break;
                    }

                    shrink(true);

                    if (isRemoveAbandoned()) {
                        removeAbandoned();
                    }
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

    }

    public int removeAbandoned() {
        int removeCount = 0;

        Iterator<Map.Entry<PoolableConnection, ActiveConnectionTraceInfo>> iter = activeConnections.entrySet().iterator();

        long currentMillis = System.currentTimeMillis();

        for (; iter.hasNext();) {
            Map.Entry<PoolableConnection, ActiveConnectionTraceInfo> entry = iter.next();
            ActiveConnectionTraceInfo activeInfo = entry.getValue();
            long timeMillis = currentMillis - activeInfo.getConnectTime();

            if (timeMillis >= removeAbandonedTimeoutMillis) {
                PoolableConnection pooledConnection = entry.getKey();
                JdbcUtils.close(pooledConnection);
                removeAbandonedCount++;
                removeCount++;

                if (isLogAbandoned()) {
                    StringBuilder buf = new StringBuilder();
                    buf.append("abandon connection, open stackTrace\n");

                    StackTraceElement[] trace = activeInfo.getStackTrace();
                    for (int i = 0; i < trace.length; i++) {
                        buf.append("\tat ");
                        buf.append(trace[i].toString());
                        buf.append("\n");
                    }

                    LOG.error(buf.toString());
                }
            }
        }

        return removeCount;
    }

    public DataSourceProxyConfig getConfig() {
        return null;
    }

    /** Instance key */
    protected String instanceKey = null;

    public Reference getReference() throws NamingException {
        final String className = getClass().getName();
        final String factoryName = className + "Factory"; // XXX: not robust
        Reference ref = new Reference(className, factoryName, null);
        ref.add(new StringRefAddr("instanceKey", instanceKey));
        return ref;
    }

    static class ActiveConnectionTraceInfo {

        private final PoolableConnection  connection;
        private final long                connectTime;
        private final StackTraceElement[] stackTrace;

        public ActiveConnectionTraceInfo(PoolableConnection connection, long connectTime, StackTraceElement[] stackTrace){
            super();
            this.connection = connection;
            this.connectTime = connectTime;
            this.stackTrace = stackTrace;
        }

        public PoolableConnection getConnection() {
            return connection;
        }

        public long getConnectTime() {
            return connectTime;
        }

        public StackTraceElement[] getStackTrace() {
            return stackTrace;
        }
    }

    @Override
    public List<String> getFilterClassNames() {
        List<String> names = new ArrayList<String>();
        for (Filter filter : filters) {
            names.add(filter.getClass().getName());
        }
        return names;
    }

    public int getRawDriverMajorVersion() {
        int version = -1;
        if (this.driver != null) {
            version = driver.getMajorVersion();
        }
        return version;
    }

    public int getRawDriverMinorVersion() {
        int version = -1;
        if (this.driver != null) {
            version = driver.getMinorVersion();
        }
        return version;
    }

    public String getProperties() {
        if (this.connectionProperties == null) {
            return null;
        }

        Properties properties = new Properties(this.connectionProperties);
        if (properties.contains("password")) {
            properties.put("password", "******");
        }
        return properties.toString();
    }

    @Override
    public void shrink() {
        shrink(false);
    }

    public void shrink(boolean checkTime) {
        final List<ConnectionHolder> evictList = new ArrayList<ConnectionHolder>();
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            return;
        }

        try {
            final int checkCount = poolingCount - minIdle;
            for (int i = 0; i < checkCount; ++i) {
                ConnectionHolder connection = connections[i];

                if (checkTime) {
                    long idleMillis = System.currentTimeMillis() - connection.getLastActiveTimeMillis();
                    if (idleMillis >= minEvictableIdleTimeMillis) {
                        evictList.add(connection);
                    } else {
                        break;
                    }
                } else {
                    evictList.add(connection);
                }
            }

            int removeCount = evictList.size();
            if (removeCount > 0) {
                System.arraycopy(connections, removeCount, connections, 0, poolingCount - removeCount);
                Arrays.fill(connections, poolingCount - removeCount, poolingCount, null);
                poolingCount -= removeCount;
            }
        } finally {
            lock.unlock();
        }

        for (ConnectionHolder item : evictList) {
            Connection connection = item.getConnection();
            JdbcUtils.close(connection);
            destroyCount++;
        }
    }

    public int getWaitThreadCount() {
        lock.lock();
        try {
            return lock.getWaitQueueLength(notEmpty);
        } finally {
            lock.unlock();
        }
    }

    public long getNotEmptyWaitCount() {
        return notEmptyWaitCount;
    }

    public int getNotEmptyWaitThreadCount() {
        return notEmptyWaitThreadCount;
    }

    public long getNotEmptySignalCount() {
        return notEmptySignalCount;
    }

    public long getNotEmptyWaitMillis() {
        return notEmptyWaitNanos / (1000 * 1000);
    }

    public long getNotEmptyWaitNanos() {
        return notEmptyWaitNanos;
    }

    public int getLockQueueLength() {
        return lock.getQueueLength();
    }
    
    public String dump() {
        lock.lock();
        try {
            return this.toString();
        } finally {
            lock.unlock();
        }
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append("{");

        buf.append("\n\tCreateTime:\"");
        buf.append(JdbcUtils.toString(getCreatedTime()));
        buf.append("\"");

        buf.append(",\n\tActiveCount:");
        buf.append(getActiveCount());

        buf.append(",\n\tPoolingCount:");
        buf.append(getPoolingCount());

        buf.append(",\n\tCreateCount:");
        buf.append(getCreateCount());

        buf.append(",\n\tDestroyCount:");
        buf.append(getDestroyCount());

        buf.append(",\n\tCloseCount:");
        buf.append(getCloseCount());

        buf.append(",\n\tConnectCount:");
        buf.append(getConnectCount());

        buf.append(",\n\tConnections:[");
        for (int i = 0; i < poolingCount; ++i) {
            ConnectionHolder conn = connections[i];
            if (conn != null) {
                if (i != 0) {
                    buf.append(",");
                }
                buf.append("\n\t\t");
                buf.append(conn.toString());
            }
        }
        buf.append("\n\t]");

        buf.append("\n}");

        return buf.toString();
    }

    @Override
    public String getVersion() {
        return VERSION.MajorVersion + "." + VERSION.MinorVersion + "." + VERSION.RevisionVersion + "-2011-09-11 17:31";
    }
}
