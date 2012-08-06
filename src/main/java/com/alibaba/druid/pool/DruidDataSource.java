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

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ConcurrentModificationException;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Condition;

import javax.management.ObjectName;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

import com.alibaba.druid.TransactionTimeoutException;
import com.alibaba.druid.VERSION;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidPooledPreparedStatement.PreparedStatementKey;
import com.alibaba.druid.pool.vendor.InformixExceptionSorter;
import com.alibaba.druid.pool.vendor.MSSQLValidConnectionChecker;
import com.alibaba.druid.pool.vendor.MockExceptionSorter;
import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;
import com.alibaba.druid.pool.vendor.MySqlValidConnectionChecker;
import com.alibaba.druid.pool.vendor.OracleExceptionSorter;
import com.alibaba.druid.pool.vendor.OracleValidConnectionChecker;
import com.alibaba.druid.pool.vendor.SybaseExceptionSorter;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyConfig;
import com.alibaba.druid.proxy.jdbc.TransactionInfo;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcDataSourceStat;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.IOUtils;
import com.alibaba.druid.util.JdbcUtils;

/**
 * @author ljw<ljw2083@alibaba-inc.com>
 * @author wenshao<szujobs@hotmail.com>
 */
public class DruidDataSource extends DruidAbstractDataSource implements DruidDataSourceMBean, ManagedDataSource, Referenceable, Closeable, Cloneable, ConnectionPoolDataSource {

    public final static Log        LOG                     = LogFactory.getLog(DruidDataSource.class);

    private static final long       serialVersionUID        = 1L;

    private final Condition         notEmpty                = lock.newCondition();
    private final Condition         empty                   = lock.newCondition();

    // stats
    private long                    connectCount            = 0L;
    private long                    closeCount              = 0L;
    private final AtomicLong        connectErrorCount       = new AtomicLong();
    private long                    recycleCount            = 0L;
    private long                    createConnectionCount   = 0L;
    private long                    destroyCount            = 0L;
    private long                    removeAbandonedCount    = 0L;
    private long                    notEmptyWaitCount       = 0L;
    private long                    notEmptySignalCount     = 0L;
    private long                    notEmptyWaitNanos       = 0L;

    private int                     activePeak              = 0;
    private long                    activePeakTime          = 0;
    private int                     poolingPeak             = 0;
    private long                    poolingPeakTime         = 0;

    // store
    private ConnectionHolder[]      connections;
    private int                     poolingCount            = 0;
    private int                     activeCount             = 0;
    private long                    discardCount            = 0;
    private int                     notEmptyWaitThreadCount = 0;
    private int                     notEmptyWaitThreadPeak  = 0;

    // threads
    private CreateConnectionThread  createConnectionThread;
    private DestroyConnectionThread destoryConnectionThread;

    private final CountDownLatch    initedLatch             = new CountDownLatch(2);

    private boolean                 enable                  = true;

    private boolean                 resetStatEnable         = true;

    private String                  initStackTrace;

    private boolean                 closed                  = false;

    private JdbcDataSourceStat      dataSourceStat;

    public DruidDataSource(){
    }

    public String getInitStackTrace() {
        return initStackTrace;
    }

    public boolean isResetStatEnable() {
        return resetStatEnable;
    }

    public void setResetStatEnable(boolean resetStatEnable) {
        this.resetStatEnable = resetStatEnable;
    }

    public long getDiscardCount() {
        return discardCount;
    }

    public void restart() {
        lock.lock();
        try {
            this.close();
            this.resetStat();
            this.inited = false;
            this.enable = true;
        } finally {
            lock.unlock();
        }
    }

    public void resetStat() {
        if (!resetStatEnable) {
            return;
        }

        lock.lock();
        try {
            connectCount = 0;
            closeCount = 0;
            discardCount = 0;
            recycleCount = 0;
            createConnectionCount = 0;
            destroyCount = 0;
            removeAbandonedCount = 0;
            notEmptyWaitCount = 0;
            notEmptySignalCount = 0L;
            notEmptyWaitNanos = 0;

            activePeak = 0;
            activePeakTime = 0;
            poolingPeak = 0;
            createTimespan = 0;
            lastError = null;
            lastErrorTimeMillis = 0;
            lastCreateError = null;
            lastCreateErrorTimeMillis = 0;
        } finally {
            lock.unlock();
        }

        connectErrorCount.set(0);
        errorCount.set(0);
        commitCount.set(0);
        rollbackCount.set(0);
        startTransactionCount.set(0);
        cachedPreparedStatementHitCount.set(0);
        closedPreparedStatementCount.set(0);
        preparedStatementCount.set(0);
        transactionHistogram.reset();
        cachedPreparedStatementDeleteCount.set(0);
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

    public void setPoolPreparedStatements(boolean value) {
        lock.lock();
        try {
            if (this.poolPreparedStatements && (!value)) {
                for (int i = 0; i < poolingCount; ++i) {
                    ConnectionHolder connection = connections[i];

                    for (PreparedStatementHolder holder : connection.getStatementPool().getMap().values()) {
                        closePreapredStatement(holder);
                        decrementCachedPreparedStatementCount();
                    }

                    connection.getStatementPool().getMap().clear();
                }
            }
            super.setPoolPreparedStatements(value);
        } finally {
            lock.unlock();
        }
    }

    public boolean isInited() {
        return this.inited;
    }

    public void init() throws SQLException {
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

            initStackTrace = IOUtils.toString(Thread.currentThread().getStackTrace());

            this.id = DruidDriver.createDataSourceId();

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
            
            if (isTestOnBorrow() || isTestOnReturn() || isTestWhileIdle()) {
                if (this.getValidationQuery() == null || this.getValidationQuery().length() == 0) {
                    LOG.error("validationQuery not set");
                }
            }

            if (this.jdbcUrl != null) {
                this.jdbcUrl = this.jdbcUrl.trim();

                if (jdbcUrl.startsWith(DruidDriver.DEFAULT_PREFIX)) {
                    DataSourceProxyConfig config = DruidDriver.parseConfig(jdbcUrl, null);
                    this.driverClass = config.getRawDriverClassName();
                    
                    LOG.error("error url : '" + jdbcUrl + "', it should be : '" + config.getRawUrl() + "'");
                    
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

                if (MockDriver.class.getName().equals(driverClass)) {
                    driver = MockDriver.instance;
                } else {
                    driver = JdbcUtils.createDriver(driverClass);
                }
            } else {
                if (this.driverClass == null) {
                    this.driverClass = driver.getClass().getName();
                }
            }

            if (this.dbType == null || this.dbType.length() == 0) {
                this.dbType = JdbcUtils.getDbType(jdbcUrl, driverClass.getClass().getName());
            }

            if ("oracle".equals(this.dbType)) {
                isOracle = true;

                if (driver.getMajorVersion() < 10) {
                    throw new SQLException("not support oracle driver " + driver.getMajorVersion() + "."
                                           + driver.getMinorVersion());
                }

                if (driver.getMajorVersion() == 10 && isUseOracleImplicitCache()) {
                    this.getConnectProperties().setProperty("oracle.jdbc.FreeMemoryOnEnterImplicitCache", "true");
                }
            }

            String realDriverClassName = driver.getClass().getName();
            if (realDriverClassName.equals("com.mysql.jdbc.Driver")) {
                this.validConnectionChecker = new MySqlValidConnectionChecker();
                this.exceptionSorter = new MySqlExceptionSorter();

            } else if (realDriverClassName.equals("oracle.jdbc.driver.OracleDriver")) {
                this.validConnectionChecker = new OracleValidConnectionChecker();
                this.exceptionSorter = new OracleExceptionSorter();

            } else if (realDriverClassName.equals("com.microsoft.jdbc.sqlserver.SQLServerDriver")) {
                this.validConnectionChecker = new MSSQLValidConnectionChecker();

            } else if (realDriverClassName.equals("com.informix.jdbc.IfxDriver")) {
                this.exceptionSorter = new InformixExceptionSorter();

            } else if (realDriverClassName.equals("com.sybase.jdbc2.jdbc.SybDriver")) {
                this.exceptionSorter = new SybaseExceptionSorter();

            } else if (realDriverClassName.equals("com.alibaba.druid.mock.MockDriver")) {
                this.exceptionSorter = new MockExceptionSorter();
            }
            
            if (realDriverClassName.equals("com.mysql.jdbc.Driver")) {
                if (this.isPoolPreparedStatements()) {
                    LOG.error("mysql should not use 'PoolPreparedStatements'");
                }
            }

            dataSourceStat = new JdbcDataSourceStat(this.name, this.jdbcUrl, this.dbType);

            {
                String property = System.getProperty("druid.filters");
                if (property != null && property.length() > 0) {
                    this.setFilters(property);
                }
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
                    if (defaultAutoCommit != conn.getAutoCommit()) {
                        conn.setAutoCommit(defaultAutoCommit);
                    }
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

            initedTime = new Date();
            ObjectName objectName = DruidDataSourceStatManager.add(this, this.name);
            this.setObjectName(objectName);

            if (connectError != null && poolingCount == 0) {
                throw connectError;
            }
        } catch (SQLException e) {
            LOG.error("dataSource init error", e);
            throw e;
        } catch (InterruptedException e) {
            throw new SQLException(e.getMessage(), e);
        } finally {
            inited = true;

            lock.unlock();
        }
    }

    @Override
    public DruidPooledConnection getConnection() throws SQLException {
        return getConnection(maxWait);
    }

    public DruidPooledConnection getConnection(long maxWaitMillis) throws SQLException {
        init();

        if (filters.size() > 0) {
            FilterChainImpl filterChain = new FilterChainImpl(this);
            return filterChain.dataSource_connect(this, maxWaitMillis);
        } else {
            return getConnectionDirect(maxWaitMillis);
        }
    }

    @Override
    public PooledConnection getPooledConnection() throws SQLException {
        return getConnection(maxWait);
    }

    @Override
    public PooledConnection getPooledConnection(String user, String password) throws SQLException {
        throw new UnsupportedOperationException("Not supported by DruidDataSource");
    }

    public DruidPooledConnection getConnectionDirect(long maxWaitMillis) throws SQLException {
        final int maxWaitThreadCount = getMaxWaitThreadCount();
        if (maxWaitThreadCount > 0) {
            if (notEmptyWaitThreadCount > maxWaitThreadCount) {
                connectErrorCount.incrementAndGet();
                throw new SQLException("maxWaitThreadCount " + maxWaitThreadCount + ", current wait Thread count "
                                       + lock.getQueueLength());
            }
        }

        for (;;) {
            DruidPooledConnection poolalbeConnection = getConnectionInternal(maxWaitMillis);

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
                poolalbeConnection.setTraceEnable(true);
            }

            if (!this.isDefaultAutoCommit()) {
                poolalbeConnection.setAutoCommit(false);
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

        lock.lock();
        try {
            activeCount--;
            discardCount++;
        } finally {
            lock.unlock();
        }
    }

    private DruidPooledConnection getConnectionInternal(long maxWait) throws SQLException {
        DruidPooledConnection poolalbeConnection;

        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            connectErrorCount.incrementAndGet();
            throw new SQLException("interrupt", e);
        }

        try {
            if (!enable) {
                connectErrorCount.incrementAndGet();
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
            if (activeCount > activePeak) {
                activePeak = activeCount;
                activePeakTime = System.currentTimeMillis();
            }

            poolalbeConnection = new DruidPooledConnection(holder);
        } catch (InterruptedException e) {
            connectErrorCount.incrementAndGet();
            throw new SQLException(e.getMessage(), e);
        } catch (SQLException e) {
            connectErrorCount.incrementAndGet();
            throw e;
        } finally {
            lock.unlock();
        }
        return poolalbeConnection;
    }

    public void handleConnectionException(DruidPooledConnection pooledConnection, Throwable t) throws SQLException {
        final ConnectionHolder holder = pooledConnection.getConnectionHolder();

        errorCount.incrementAndGet();
        lastError = t;
        lastErrorTimeMillis = System.currentTimeMillis();

        if (t instanceof SQLException) {
            SQLException sqlEx = (SQLException) t;

            // broadcastConnectionError
            ConnectionEvent event = new ConnectionEvent(pooledConnection, sqlEx);
            for (ConnectionEventListener eventListener : holder.getConnectionEventListeners()) {
                eventListener.connectionErrorOccurred(event);
            }

            // exceptionSorter.isExceptionFatal
            if (exceptionSorter != null && exceptionSorter.isExceptionFatal(sqlEx)) {
                if (pooledConnection.isTraceEnable()) {
                    activeConnections.remove(pooledConnection);
                }
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
    protected void recycle(DruidPooledConnection pooledConnection) throws SQLException {
        final Connection conn = pooledConnection.getConnection();
        final ConnectionHolder holder = pooledConnection.getConnectionHolder();

        assert holder != null;

        if (pooledConnection.isTraceEnable()) {
            ActiveConnectionTraceInfo oldInfo = activeConnections.remove(pooledConnection);
            if (oldInfo == null) {
                LOG.warn("remove abandonded failed. activeConnections.size " + activeConnections.size());
            }
        }

        try {
            // 第一步，检查连接是否关闭
            if (conn == null) {
                lock.lockInterruptibly();
                try {
                    activeCount--;
                    closeCount++;
                } finally {
                    lock.unlock();
                }
                return;
            }

            final boolean isAutoCommit = holder.isUnderlyingAutoCommit();
            final boolean isReadOnly = holder.isUnderlyingReadOnly();

            // check need to rollback?
            if ((!isAutoCommit) && (!isReadOnly)) {
                pooledConnection.rollback();
            }

            // reset holder, restore default settings, clear warnings
            holder.reset();

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

            boolean neadDestory = false;
            lock.lockInterruptibly();
            try {
                if (holder.getModCount() == this.modCount) {
                    activeCount--;
                    closeCount++;

                    // 第六步，加入队列中(putLast)
                    putLast(holder);
                    recycleCount++;
                } else {
                    destroyCount++;
                    activeCount--;
                    closeCount++;
                    neadDestory = true;
                }
            } finally {
                lock.unlock();
            }

            if (neadDestory) {
                JdbcUtils.close(conn);
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

    public void clearStatementCache() throws SQLException {
        lock.lock();
        try {
            for (int i = 0; i < poolingCount; ++i) {
                ConnectionHolder conn = connections[i];
                conn.getStatementPool().clear();
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * close datasource
     */
    public void close() {
        lock.lock();
        try {
            if (this.closed) {
                return;
            }

            if (!this.inited) {
                return;
            }

            if (createConnectionThread != null) {
                createConnectionThread.interrupt();
            }

            if (destoryConnectionThread != null) {
                destoryConnectionThread.interrupt();
            }

            for (int i = 0; i < poolingCount; ++i) {
                try {
                    JdbcUtils.close(connections[i].getConnection());
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

            this.closed = true;

            for (Filter filter : filters) {
                filter.destory();
            }
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
                if (notEmptyWaitThreadCount > notEmptyWaitThreadPeak) {
                    notEmptyWaitThreadPeak = notEmptyWaitThreadCount;
                }
                try {
                    notEmpty.await(); // signal by recycle or creator
                } finally {
                    notEmptyWaitThreadCount--;
                }
                notEmptyWaitCount++;

                if (!enable) {
                    connectErrorCount.incrementAndGet();
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
        final long nanos = unit.toNanos(timeout);
        long estimate = nanos;

        for (int i = 0;; ++i) {
            if (poolingCount == 0) {
                empty.signal(); // send signal to CreateThread create connection

                if (estimate <= 0) {
                    throw new GetConnectionTimeoutException();
                }

                notEmptyWaitThreadCount++;
                if (notEmptyWaitThreadCount > notEmptyWaitThreadPeak) {
                    notEmptyWaitThreadPeak = notEmptyWaitThreadCount;
                }

                try {
                    long startEstimate = estimate;
                    estimate = notEmpty.awaitNanos(estimate); // signal by
                                                              // recycle or
                                                              // creator
                    notEmptyWaitCount++;
                    notEmptyWaitNanos += (startEstimate - estimate);

                    if (!enable) {
                        connectErrorCount.incrementAndGet();
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

                    if (createError != null) {
                        throw new GetConnectionTimeoutException(createError);
                    } else {
                        throw new GetConnectionTimeoutException("loopWaitCount " + i + ", wait millis " + (nanos - estimate)/(1000 * 1000));
                    }
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
        return connectErrorCount.get();
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

    public int getPoolingPeak() {
        return poolingPeak;
    }

    public Date getPoolingPeakTime() {
        if (poolingPeakTime <= 0) {
            return null;
        }

        return new Date(poolingPeakTime);
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

    public boolean isBusy() {
        lock.lock();
        try {
            return this.inited && this.activeCount == maxActive && this.poolingCount == 0;
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
                    }

                    // 防止创建超过maxActive数量的连接
                    if (activeCount + poolingCount >= maxActive) {
                        empty.await();
                        continue;
                    }

                } catch (InterruptedException e) {
                    lastCreateError = e;
                    lastErrorTimeMillis = System.currentTimeMillis();
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

                    if (poolingCount > poolingPeak) {
                        poolingPeak = poolingCount;
                        poolingPeakTime = System.currentTimeMillis();
                    }

                    errorCount = 0; // reset errorCount

                    notEmpty.signal();
                    notEmptySignalCount++;
                } catch (SQLException ex) {
                    LOG.error("create connection holder error", ex);
                    break;
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

        Iterator<Map.Entry<DruidPooledConnection, ActiveConnectionTraceInfo>> iter = activeConnections.entrySet().iterator();

        long currentMillis = System.currentTimeMillis();

        List<DruidPooledConnection> abondonedList = new ArrayList<DruidPooledConnection>();

        for (; iter.hasNext();) {
            Map.Entry<DruidPooledConnection, ActiveConnectionTraceInfo> entry = iter.next();
            DruidPooledConnection pooledConnection = entry.getKey();
            
            if (pooledConnection.isRunning()) {
                continue;
            }
            
            ActiveConnectionTraceInfo activeInfo = entry.getValue();
            long timeMillis = currentMillis - activeInfo.getConnectTime();

            if (timeMillis >= removeAbandonedTimeoutMillis) {
                JdbcUtils.close(pooledConnection);
                removeAbandonedCount++;
                removeCount++;
                abondonedList.add(pooledConnection);

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

        // multi-check dup close
        for (DruidPooledConnection conn : abondonedList) {
            activeConnections.remove(conn);
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

        private final DruidPooledConnection connection;
        private final long                  connectTime;
        private final StackTraceElement[]   stackTrace;

        public ActiveConnectionTraceInfo(DruidPooledConnection connection, long connectTime,
                                         StackTraceElement[] stackTrace){
            super();
            this.connection = connection;
            this.connectTime = connectTime;
            this.stackTrace = stackTrace;
        }

        public DruidPooledConnection getConnection() {
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

    public int getNotEmptyWaitThreadPeak() {
        lock.lock();
        try {
            return notEmptyWaitThreadPeak;
        } finally {
            lock.unlock();
        }
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

    public int getActivePeak() {
        return activePeak;
    }

    public Date getActivePeakTime() {
        if (activePeakTime <= 0) {
            return null;
        }

        return new Date(activePeakTime);
    }

    public String dump() {
        lock.lock();
        try {
            return this.toString();
        } finally {
            lock.unlock();
        }
    }

    public long getErrorCount() {
        return this.errorCount.get();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append("{");

        buf.append("\n\tCreateTime:\"");
        buf.append(IOUtils.toString(getCreatedTime()));
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

        if (this.isPoolPreparedStatements()) {
            buf.append("\n\n[");
            for (int i = 0; i < poolingCount; ++i) {
                ConnectionHolder conn = connections[i];
                if (conn != null) {
                    if (i != 0) {
                        buf.append(",");
                    }
                    buf.append("\n\t{\n\tID:");
                    buf.append(System.identityHashCode(conn.getConnection()));
                    PreparedStatementPool pool = conn.getStatementPool();

                    if (pool != null) {
                        buf.append(", \n\tpoolStatements:[");

                        int entryIndex = 0;
                        try {
                            for (Map.Entry<PreparedStatementKey, PreparedStatementHolder> entry : pool.getMap().entrySet()) {
                                if (entryIndex++ != 0) {
                                    buf.append(",");
                                }
                                buf.append("\n\t\t{hitCount:");
                                buf.append(entry.getValue().getHitCount());
                                buf.append(",sql:\"");
                                buf.append(entry.getKey().getSql());
                                buf.append("\"");
                                buf.append("\t}");
                            }
                        } catch (ConcurrentModificationException e) {
                            // skip ..
                        }

                        buf.append("\n\t\t]");
                    }

                    buf.append("\n\t}");
                }
            }
            buf.append("\n]");
        }

        return buf.toString();
    }

    public List<Map<String, Object>> getPoolingConnectionInfo() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        lock.lock();
        try {
            for (int i = 0; i < poolingCount; ++i) {
                ConnectionHolder connHolder = connections[i];
                Connection conn = connHolder.getConnection();

                Map<String, Object> map = new LinkedHashMap<String, Object>();
                map.put("id", System.identityHashCode(conn));
                map.put("useCount", connHolder.getUseCount());
                if (connHolder.getLastActiveTimeMillis() > 0) {
                    map.put("lastActiveTime", new Date(connHolder.getLastActiveTimeMillis()));
                }
                map.put("connectTime", new Date(connHolder.getTimeMillis()));
                map.put("holdability", connHolder.getUnderlyingHoldability());
                map.put("transactionIsolation", connHolder.getUnderlyingTransactionIsolation());
                map.put("autoCommit", connHolder.isUnderlyingAutoCommit());
                map.put("readoOnly", connHolder.isUnderlyingReadOnly());

                if (connHolder.isPoolPreparedStatements()) {
                    List<Map<String, Object>> stmtCache = new ArrayList<Map<String, Object>>();
                    PreparedStatementPool stmtPool = connHolder.getStatementPool();
                    for (PreparedStatementHolder stmtHolder : stmtPool.getMap().values()) {
                        Map<String, Object> stmtInfo = new LinkedHashMap<String, Object>();
                        
                        stmtInfo.put("sql", stmtHolder.getKey().getSql());
                        stmtInfo.put("defaultRowPretch", stmtHolder.getDefaultRowPretch());
                        stmtInfo.put("rowPrefetch", stmtHolder.getRowPrefetch());
                        stmtInfo.put("hitCount", stmtHolder.getHitCount());
                        
                        stmtCache.add(stmtInfo);
                    }
                    
                    map.put("pscache", stmtCache);
                }

                list.add(map);
            }
        } finally {
            lock.unlock();
        }
        return list;
    }

    public void logTransaction(TransactionInfo info) {
        long transactionMillis = info.getEndTimeMillis() - info.getStartTimeMillis();
        if (transactionThresholdMillis > 0 && transactionMillis > transactionThresholdMillis) {
            StringBuilder buf = new StringBuilder();
            buf.append("long time transaction, take ");
            buf.append(transactionMillis);
            buf.append(" ms : ");
            for (String sql : info.getSqlList()) {
                buf.append(sql);
                buf.append(";");
            }
            LOG.error(buf.toString(), new TransactionTimeoutException());
        }
    }

    @Override
    public String getVersion() {
        return VERSION.getVersionNumber();
    }

    @Override
    public JdbcDataSourceStat getDataSourceStat() {
        return dataSourceStat;
    }

    public Object clone() throws CloneNotSupportedException {
        return cloneDruidDataSource();
    }

    public DruidDataSource cloneDruidDataSource() {
        DruidDataSource x = new DruidDataSource();

        cloneTo(x);

        return x;
    }

}
