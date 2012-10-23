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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.ServiceLoader;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.Lock;

import javax.management.JMException;
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
import com.alibaba.druid.filter.AutoLoad;
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
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.IOUtils;
import com.alibaba.druid.util.JMXUtils;
import com.alibaba.druid.util.JdbcUtils;

/**
 * @author ljw<ljw2083@alibaba-inc.com>
 * @author wenshao<szujobs@hotmail.com>
 */
public class DruidDataSource extends DruidAbstractDataSource implements DruidDataSourceMBean, ManagedDataSource, Referenceable, Closeable, Cloneable, ConnectionPoolDataSource {

    private final static Log                 LOG                     = LogFactory.getLog(DruidDataSource.class);

    private static final long                serialVersionUID        = 1L;

    // stats
    private long                             connectCount            = 0L;
    private long                             closeCount              = 0L;
    private final AtomicLong                 connectErrorCount       = new AtomicLong();
    private long                             recycleCount            = 0L;
    private long                             removeAbandonedCount    = 0L;
    private long                             notEmptyWaitCount       = 0L;
    private long                             notEmptySignalCount     = 0L;
    private long                             notEmptyWaitNanos       = 0L;

    private int                              activePeak              = 0;
    private long                             activePeakTime          = 0;
    private int                              poolingPeak             = 0;
    private long                             poolingPeakTime         = 0;

    // store
    private volatile DruidConnectionHolder[] connections;
    private int                              poolingCount            = 0;
    private int                              activeCount             = 0;
    private long                             discardCount            = 0;
    private int                              notEmptyWaitThreadCount = 0;
    private int                              notEmptyWaitThreadPeak  = 0;

    // threads
    private CreateConnectionThread           createConnectionThread;
    private DestroyConnectionThread          destoryConnectionThread;

    private final CountDownLatch             initedLatch             = new CountDownLatch(2);

    private volatile boolean                 enable                  = true;

    private boolean                          resetStatEnable         = true;

    private String                           initStackTrace;

    private volatile boolean                 closed                  = false;
    private long                             closeTimeMillis         = -1L;

    private JdbcDataSourceStat               dataSourceStat;

    public DruidDataSource(){
        this(false);
    }

    public DruidDataSource(boolean fairLock){
        super(fairLock);
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
            if (LOG.isInfoEnabled()) {
                LOG.info("{dataSource-" + this.getID() + "} restart");
            }

            this.close();
            this.resetStat();
            this.inited = false;
            this.enable = true;
            this.closed = false;
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
            createCount.set(0);
            destroyCount.set(0);
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
        if (this.poolPreparedStatements == value) {
            return;
        }

        this.poolPreparedStatements = value;

        if (!inited) {
            return;
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("set poolPreparedStatements " + this.poolPreparedStatements + " -> " + value);
        }

        if (!value) {
            lock.lock();
            try {

                for (int i = 0; i < poolingCount; ++i) {
                    DruidConnectionHolder connection = connections[i];

                    for (PreparedStatementHolder holder : connection.getStatementPool().getMap().values()) {
                        closePreapredStatement(holder);
                    }

                    connection.getStatementPool().getMap().clear();
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public void setMaxActive(int maxActive) {
        if (this.maxActive == maxActive) {
            return;
        }

        if (maxActive == 0) {
            throw new IllegalArgumentException("maxActive can't not set zero");
        }

        if (!inited) {
            this.maxActive = maxActive;
            return;
        }

        if (maxActive < this.minIdle) {
            throw new IllegalArgumentException("maxActive less than minIdle, " + maxActive + " < " + this.minIdle);
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("maxActive changed : " + this.maxActive + " -> " + maxActive);
        }

        lock.lock();
        try {
            int allCount = this.poolingCount + this.activeCount;

            if (maxActive > allCount) {
                this.connections = Arrays.copyOf(this.connections, maxActive);
            } else {
                this.connections = Arrays.copyOf(this.connections, allCount);
            }

            this.maxActive = maxActive;
        } finally {
            lock.unlock();
        }
    }

    public void setConnectProperties(Properties properties) {
        if (properties == null) {
            properties = new Properties();
        }

        if (inited) {
            if (!this.connectProperties.equals(properties)) {
                LOG.info("connectProperties changed : " + this.connectProperties + " -> " + properties);
            }
        }

        this.connectProperties = properties;
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

        boolean init = false;
        try {
            if (inited) {
                return;
            }

            init = true;

            initStackTrace = IOUtils.toString(Thread.currentThread().getStackTrace());

            this.id = DruidDriver.createDataSourceId();

            loadFilterFromSystemProperty();

            for (Filter filter : filters) {
                filter.init(this);
            }

            if (maxActive <= 0) {
                throw new IllegalArgumentException("illegal maxActive " + maxActive);
            }

            if (maxActive < minIdle) {
                throw new IllegalArgumentException("illegal maxActive " + maxActive);
            }

            if (getInitialSize() > maxActive) {
                throw new IllegalArgumentException("illegal initialSize " + this.initialSize + ", maxActieve "
                                                   + maxActive);
            }

            if (this.driverClass != null) {
                this.driverClass = driverClass.trim();
            }

            validationQueryCheck();

            if (this.jdbcUrl != null) {
                this.jdbcUrl = this.jdbcUrl.trim();
                initFromWrapDriverUrl();
            }

            initFromSPIServiceLoader();

            if (this.driver == null) {
                if (this.driverClass == null || this.driverClass.isEmpty()) {
                    this.driverClass = JdbcUtils.getDriverClassName(this.jdbcUrl);
                }

                if (MockDriver.class.getName().equals(driverClass)) {
                    driver = MockDriver.instance;
                } else {
                    driver = JdbcUtils.createDriver(driverClassLoader, driverClass);
                }
            } else {
                if (this.driverClass == null) {
                    this.driverClass = driver.getClass().getName();
                }
            }

            if (this.dbType == null || this.dbType.length() == 0) {
                this.dbType = JdbcUtils.getDbType(jdbcUrl, driverClass.getClass().getName());
            }

            initCheck();

            initExceptionSorter();
            initValidConnectionChecker();

            if (driver.getClass().getName().equals("com.mysql.jdbc.Driver")) {
                if (this.isPoolPreparedStatements()) {
                    LOG.error("mysql should not use 'PoolPreparedStatements'");
                }
            }

            dataSourceStat = new JdbcDataSourceStat(this.name, this.jdbcUrl, this.dbType);

            connections = new DruidConnectionHolder[maxActive];

            SQLException connectError = null;

            try {
                // 初始化连接
                for (int i = 0, size = getInitialSize(); i < size; ++i) {
                    Connection conn = createPhysicalConnection();
                    DruidConnectionHolder holder = new DruidConnectionHolder(this, conn);
                    connections[poolingCount++] = holder;
                }

                if (poolingCount > 0) {
                    poolingPeak = poolingCount;
                    poolingPeakTime = System.currentTimeMillis();
                }
            } catch (SQLException ex) {
                LOG.error("init datasource error", ex);
                connectError = ex;
            }

            createAndStartCreatorThread();
            createAndStartDestroyThread();

            initedLatch.await();

            initedTime = new Date();
            ObjectName objectName = DruidDataSourceStatManager.addDataSource(this, this.name);
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

            if (init && LOG.isInfoEnabled()) {
                LOG.info("{dataSource-" + this.getID() + "} inited");
            }
        }
    }

    private void createAndStartDestroyThread() {
        destoryConnectionThread = new DestroyConnectionThread("Druid-ConnectionPool-Destory");
        destoryConnectionThread.start();
    }

    protected void createAndStartCreatorThread() {
        createConnectionThread = new CreateConnectionThread("Druid-ConnectionPool-Create");
        createConnectionThread.start();
    }

    private void loadFilterFromSystemProperty() throws SQLException {
        String property = System.getProperty("druid.filters");

        if (property == null || property.length() == 0) {
            return;
        }

        this.setFilters(property);
    }

    /**
     * load filters from SPI ServiceLoader
     * 
     * @see ServiceLoader
     */
    private void initFromSPIServiceLoader() {

        String property = System.getProperty("druid.load.spifilter.skip");
        if (property != null) {
            return;
        }

        ServiceLoader<Filter> druidAutoFilterLoader = ServiceLoader.load(Filter.class);

        for (Filter autoFilter : druidAutoFilterLoader) {
            AutoLoad autoLoad = autoFilter.getClass().getAnnotation(AutoLoad.class);
            if (autoLoad != null && autoLoad.value()) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("load filter from spi :" + autoFilter.getClass().getName());
                }
                addFilter(autoFilter);
            }
        }
    }

    private void initFromWrapDriverUrl() throws SQLException {
        if (!jdbcUrl.startsWith(DruidDriver.DEFAULT_PREFIX)) {
            return;
        }

        DataSourceProxyConfig config = DruidDriver.parseConfig(jdbcUrl, null);
        this.driverClass = config.getRawDriverClassName();

        LOG.error("error url : '" + jdbcUrl + "', it should be : '" + config.getRawUrl() + "'");

        this.jdbcUrl = config.getRawUrl();
        if (this.name == null) {
            this.name = config.getName();
        }

        for (Filter filter : config.getFilters()) {
            addFilter(filter);
        }
    }

    /**
     * 会去重复
     * 
     * @param filter
     */
    private void addFilter(Filter filter) {
        boolean exists = false;
        for (Filter initedFilter : this.filters) {
            if (initedFilter.getClass() == filter.getClass()) {
                exists = true;
                break;
            }
        }

        if (!exists) {
            filter.init(this);
            this.filters.add(filter);
        }

    }

    private void validationQueryCheck() {
        if (!(isTestOnBorrow() || isTestOnReturn() || isTestWhileIdle())) {
            return;
        }

        if (this.getValidationQuery() != null && this.getValidationQuery().length() > 0) {
            return;
        }

        String errorMessage = "";

        if (isTestOnBorrow()) {
            errorMessage += "testOnBorrow is true, ";
        }

        if (isTestOnReturn()) {
            errorMessage += "testOnReturn is true, ";
        }

        if (isTestWhileIdle()) {
            errorMessage += "testWhileIdle is true, ";
        }

        LOG.error(errorMessage + "validationQuery not set");
    }

    protected void initCheck() throws SQLException {
        if (JdbcUtils.ORACLE.equals(this.dbType)) {
            isOracle = true;

            if (driver.getMajorVersion() < 10) {
                throw new SQLException("not support oracle driver " + driver.getMajorVersion() + "."
                                       + driver.getMinorVersion());
            }

            if (driver.getMajorVersion() == 10 && isUseOracleImplicitCache()) {
                this.getConnectProperties().setProperty("oracle.jdbc.FreeMemoryOnEnterImplicitCache", "true");
            }
        } else if (JdbcUtils.MYSQL.equals(this.dbType)) {
            if (!this.isTestWhileIdle()) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("Your dbType is mysql, recommend set testWhileIdle is true");
                }
            }
        }
    }

    private void initValidConnectionChecker() {
        String realDriverClassName = driver.getClass().getName();
        if (realDriverClassName.equals("com.mysql.jdbc.Driver")) {
            this.validConnectionChecker = new MySqlValidConnectionChecker();
        } else if (realDriverClassName.equals("oracle.jdbc.driver.OracleDriver")) {
            this.validConnectionChecker = new OracleValidConnectionChecker();
        } else if (realDriverClassName.equals("com.microsoft.jdbc.sqlserver.SQLServerDriver")) {
            this.validConnectionChecker = new MSSQLValidConnectionChecker();
        }
    }

    private void initExceptionSorter() {
        String realDriverClassName = driver.getClass().getName();
        if (realDriverClassName.equals("com.mysql.jdbc.Driver")) {
            this.exceptionSorter = new MySqlExceptionSorter();
        } else if (realDriverClassName.equals("oracle.jdbc.driver.OracleDriver")) {
            this.exceptionSorter = new OracleExceptionSorter();
        } else if (realDriverClassName.equals("com.informix.jdbc.IfxDriver")) {
            this.exceptionSorter = new InformixExceptionSorter();

        } else if (realDriverClassName.equals("com.sybase.jdbc2.jdbc.SybDriver")) {
            this.exceptionSorter = new SybaseExceptionSorter();

        } else if (realDriverClassName.equals("com.alibaba.druid.mock.MockDriver")) {
            this.exceptionSorter = new MockExceptionSorter();
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
                poolalbeConnection.setConnectStackTrace(stackTrace);
                poolalbeConnection.setConnectedTimeNano();
                poolalbeConnection.setTraceEnable(true);

                synchronized (activeConnections) {
                    activeConnections.put(poolalbeConnection, PRESENT);
                }
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
    public void discardConnection(Connection realConnection) {
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
        if (closed) {
            connectErrorCount.incrementAndGet();
            throw new DataSourceClosedException("dataSource already closed at " + new Date(closeTimeMillis));
        }

        if (!enable) {
            connectErrorCount.incrementAndGet();
            throw new DataSourceDisableException();
        }

        final long nanos = TimeUnit.MILLISECONDS.toNanos(maxWait);
        final int maxWaitThreadCount = getMaxWaitThreadCount();

        DruidConnectionHolder holder;
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            connectErrorCount.incrementAndGet();
            throw new SQLException("interrupt", e);
        }

        try {
            if (maxWaitThreadCount > 0) {
                if (notEmptyWaitThreadCount >= maxWaitThreadCount) {
                    connectErrorCount.incrementAndGet();
                    throw new SQLException("maxWaitThreadCount " + maxWaitThreadCount + ", current wait Thread count "
                                           + lock.getQueueLength());
                }
            }

            connectCount++;

            if (maxWait > 0) {
                holder = pollLast(nanos);
            } else {
                holder = takeLast();
            }

            activeCount++;
            if (activeCount > activePeak) {
                activePeak = activeCount;
                activePeakTime = System.currentTimeMillis();
            }

        } catch (InterruptedException e) {
            connectErrorCount.incrementAndGet();
            throw new SQLException(e.getMessage(), e);
        } catch (SQLException e) {
            connectErrorCount.incrementAndGet();
            throw e;
        } finally {
            lock.unlock();
        }

        holder.incrementUseCount();

        DruidPooledConnection poolalbeConnection = new DruidPooledConnection(holder);
        return poolalbeConnection;
    }

    public void handleConnectionException(DruidPooledConnection pooledConnection, Throwable t) throws SQLException {
        final DruidConnectionHolder holder = pooledConnection.getConnectionHolder();

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
                    synchronized (activeConnections) {
                        if (pooledConnection.isTraceEnable()) {
                            activeConnections.remove(pooledConnection);
                            pooledConnection.setTraceEnable(false);
                        }
                    }
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
        final DruidConnectionHolder holder = pooledConnection.getConnectionHolder();

        if (holder == null) {
            LOG.warn("connectionHolder is null");
            return;
        }

        final Connection physicalConnection = holder.getConnection();

        if (pooledConnection.isTraceEnable()) {
            synchronized (activeConnections) {
                if (pooledConnection.isTraceEnable()) {
                    Object oldInfo = activeConnections.remove(pooledConnection);
                    if (oldInfo == null) {
                        if (LOG.isWarnEnabled()) {
                            LOG.warn("remove abandonded failed. activeConnections.size " + activeConnections.size());
                        }
                    }
                    pooledConnection.setTraceEnable(false);
                }
            }
        }

        final boolean isAutoCommit = holder.isUnderlyingAutoCommit();
        final boolean isReadOnly = holder.isUnderlyingReadOnly();
        final boolean testOnReturn = this.isTestOnReturn();

        try {
            // check need to rollback?
            if ((!isAutoCommit) && (!isReadOnly)) {
                pooledConnection.rollback();
            }

            // reset holder, restore default settings, clear warnings
            holder.reset();

            if (testOnReturn) {
                boolean validate = testConnectionInternal(physicalConnection);
                if (!validate) {
                    JdbcUtils.close(physicalConnection);

                    destroyCount.incrementAndGet();

                    lock.lockInterruptibly();
                    try {
                        activeCount--;
                        closeCount++;
                    } finally {
                        lock.unlock();
                    }
                    return;
                }
            }

            if (!enable) {
                discardConnection(holder.getConnection());
                return;
            }

            final long lastActiveTimeMillis = System.currentTimeMillis();
            lock.lockInterruptibly();
            try {
                activeCount--;
                closeCount++;

                putLast(holder, lastActiveTimeMillis);
                recycleCount++;
            } finally {
                lock.unlock();
            }
        } catch (Throwable e) {
            JdbcUtils.close(physicalConnection);

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
                DruidConnectionHolder conn = connections[i];
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
                    DruidConnectionHolder connHolder = connections[i];

                    for (PreparedStatementHolder stmtHolder : connHolder.getStatementPool().getMap().values()) {
                        connHolder.getStatementPool().closeRemovedStatement(stmtHolder);
                    }
                    connHolder.getStatementPool().getMap().clear();

                    Connection physicalConnection = connHolder.getConnection();
                    physicalConnection.close();
                    connections[i] = null;
                    destroyCount.incrementAndGet();
                } catch (Exception ex) {
                    LOG.warn("close connection error", ex);
                }
            }
            poolingCount = 0;
            DruidDataSourceStatManager.removeDataSource(this);

            enable = false;
            notEmpty.signalAll();
            notEmptySignalCount++;

            this.closed = true;
            this.closeTimeMillis = System.currentTimeMillis();

            for (Filter filter : filters) {
                filter.destory();
            }
        } finally {
            lock.unlock();
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("{dataSource-" + this.getID() + "} closed");
        }
    }

    void putLast(DruidConnectionHolder e, long lastActiveTimeMillis) {
        e.setLastActiveTimeMillis(lastActiveTimeMillis);
        connections[poolingCount++] = e;

        if (poolingCount > poolingPeak) {
            poolingPeak = poolingCount;
            poolingPeakTime = lastActiveTimeMillis;
        }

        notEmpty.signal();
        notEmptySignalCount++;
    }

    DruidConnectionHolder takeLast() throws InterruptedException, SQLException {
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
        DruidConnectionHolder last = connections[poolingCount];
        connections[poolingCount] = null;

        return last;
    }

    DruidConnectionHolder pollLast(long nanos) throws InterruptedException, SQLException {
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
                        throw new GetConnectionTimeoutException("loopWaitCount " + i + ", wait millis "
                                                                + (nanos - estimate) / (1000 * 1000));
                    }
                }
            }

            poolingCount--;
            DruidConnectionHolder last = connections[poolingCount];
            connections[poolingCount] = null;

            return last;
        }
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        throw new UnsupportedOperationException("Not supported by DruidDataSource");
    }

    public long getCreateCount() {
        return createCount.get();
    }

    public long getDestroyCount() {
        return destroyCount.get();
    }

    public long getConnectCount() {
        lock.lock();
        try {
            return connectCount;
        } finally {
            lock.unlock();
        }
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
        lock.lock();
        try {
            return poolingPeak;
        } finally {
            lock.unlock();
        }
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
            this.setDaemon(true);
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
                    connection = createPhysicalConnection();
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

                DruidConnectionHolder holder = null;
                try {
                    holder = new DruidConnectionHolder(DruidDataSource.this, connection);
                } catch (SQLException ex) {
                    LOG.error("create connection holder error", ex);
                    break;
                }

                lock.lock();
                try {
                    connections[poolingCount++] = holder;

                    if (poolingCount > poolingPeak) {
                        poolingPeak = poolingCount;
                        poolingPeakTime = System.currentTimeMillis();
                    }

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
            this.setDaemon(true);
        }

        public void run() {
            initedLatch.countDown();

            for (;;) {
                // 从前面开始删除
                try {
                    if (closed) {
                        break;
                    }

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

        long currrentNanos = System.nanoTime();

        List<DruidPooledConnection> abandonedList = new ArrayList<DruidPooledConnection>();

        synchronized (activeConnections) {
            Iterator<DruidPooledConnection> iter = activeConnections.keySet().iterator();

            for (; iter.hasNext();) {
                DruidPooledConnection pooledConnection = iter.next();

                if (pooledConnection.isRunning()) {
                    continue;
                }

                long timeMillis = (currrentNanos - pooledConnection.getConnectedTimeNano()) / (1000 * 1000);

                if (timeMillis >= removeAbandonedTimeoutMillis) {
                    iter.remove();
                    pooledConnection.setTraceEnable(false);
                    abandonedList.add(pooledConnection);
                }
            }
        }

        if (abandonedList.size() > 0) {
            for (DruidPooledConnection pooledConnection : abandonedList) {
                JdbcUtils.close(pooledConnection);
                pooledConnection.abandond();
                removeAbandonedCount++;
                removeCount++;

                if (isLogAbandoned()) {
                    StringBuilder buf = new StringBuilder();
                    buf.append("abandon connection, open stackTrace\n");

                    StackTraceElement[] trace = pooledConnection.getConnectStackTrace();
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

    /** Instance key */
    protected String instanceKey = null;

    public Reference getReference() throws NamingException {
        final String className = getClass().getName();
        final String factoryName = className + "Factory"; // XXX: not robust
        Reference ref = new Reference(className, factoryName, null);
        ref.add(new StringRefAddr("instanceKey", instanceKey));
        return ref;
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
        Properties properties = new Properties();
        properties.putAll(connectProperties);
        if (properties.containsKey("password")) {
            properties.put("password", "******");
        }
        return properties.toString();
    }

    @Override
    public void shrink() {
        shrink(false);
    }

    public void shrink(boolean checkTime) {
        final List<DruidConnectionHolder> evictList = new ArrayList<DruidConnectionHolder>();
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            return;
        }

        try {
            final int checkCount = poolingCount - minIdle;
            for (int i = 0; i < checkCount; ++i) {
                DruidConnectionHolder connection = connections[i];

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

        for (DruidConnectionHolder item : evictList) {
            Connection connection = item.getConnection();
            JdbcUtils.close(connection);
            destroyCount.incrementAndGet();
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
        lock.lock();
        try {
            return notEmptyWaitThreadCount;
        } finally {
            lock.unlock();
        }
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
            DruidConnectionHolder conn = connections[i];
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
                DruidConnectionHolder conn = connections[i];
                if (conn != null) {
                    if (i != 0) {
                        buf.append(",");
                    }
                    buf.append("\n\t{\n\tID:");
                    buf.append(System.identityHashCode(conn.getConnection()));
                    PreparedStatementPool pool = conn.getStatementPool();

                    buf.append(", \n\tpoolStatements:[");

                    int entryIndex = 0;
                    try {
                        for (Map.Entry<PreparedStatementKey, PreparedStatementHolder> entry : pool.getMap().entrySet()) {
                            if (entryIndex != 0) {
                                buf.append(",");
                            }
                            buf.append("\n\t\t{hitCount:");
                            buf.append(entry.getValue().getHitCount());
                            buf.append(",sql:\"");
                            buf.append(entry.getKey().getSql());
                            buf.append("\"");
                            buf.append("\t}");

                            entryIndex++;
                        }
                    } catch (ConcurrentModificationException e) {
                        // skip ..
                    }

                    buf.append("\n\t\t]");

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
                DruidConnectionHolder connHolder = connections[i];
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

    public Map<String, Object> getStatDataForMBean() {
        try {
            Map<String, Object> map = new HashMap<String, Object>();

            // 0 - 4
            map.put("Name", this.getName());
            map.put("URL", this.getUrl());
            map.put("CreateCount", this.getCreateCount());
            map.put("DestroyCount", this.getDestroyCount());
            map.put("ConnectCount", this.getConnectCount());

            // 5 - 9
            map.put("CloseCount", this.getCloseCount());
            map.put("ActiveCount", this.getActivePeak());
            map.put("PoolingCount", this.getPoolingCount());
            map.put("LockQueueLength", this.getLockQueueLength());
            map.put("WaitThreadCount", this.getNotEmptyWaitThreadPeak());

            // 10 - 14
            map.put("InitialSize", this.getInitialSize());
            map.put("MaxActive", this.getMaxActive());
            map.put("MinIdle", this.getMinIdle());
            map.put("PoolPreparedStatements", this.isPoolPreparedStatements());
            map.put("TestOnBorrow", this.isTestOnBorrow());

            // 15 - 19
            map.put("TestOnReturn", this.isTestOnReturn());
            map.put("MinEvictableIdleTimeMillis", this.getMinEvictableIdleTimeMillis());
            map.put("ConnectErrorCount", this.getConnectErrorCount());
            map.put("CreateTimespanMillis", this.getCreateTimespanMillis());
            map.put("DbType", this.getDbType());

            // 20 - 24
            map.put("ValidationQuery", this.getValidationQuery());
            map.put("ValidationQueryTimeout", this.getValidationQueryTimeout());
            map.put("DriverClassName", this.getDriverClassName());
            map.put("Username", this.getUsername());
            map.put("RemoveAbandonedCount", this.getRemoveAbandonedCount());

            // 25 - 29
            map.put("NotEmptyWaitCount", this.getNotEmptyWaitCount());
            map.put("NotEmptyWaitNanos", this.getNotEmptyWaitNanos());
            map.put("ErrorCount", this.getErrorCount());
            map.put("ReusePreparedStatementCount", this.getCachedPreparedStatementHitCount());
            map.put("StartTransactionCount", this.getStartTransactionCount());

            // 30 - 34
            map.put("CommitCount", this.getCommitCount());
            map.put("RollbackCount", this.getRollbackCount());
            map.put("LastError", JMXUtils.getErrorCompositeData(this.getLastError()));
            map.put("LastCreateError", JMXUtils.getErrorCompositeData(this.getLastCreateError()));
            map.put("PreparedStatementCacheDeleteCount", this.getCachedPreparedStatementDeleteCount());

            // 35 - 39
            map.put("PreparedStatementCacheAccessCount", this.getCachedPreparedStatementAccessCount());
            map.put("PreparedStatementCacheMissCount", this.getCachedPreparedStatementMissCount());
            map.put("PreparedStatementCacheHitCount", this.getCachedPreparedStatementHitCount());
            map.put("PreparedStatementCacheCurrentCount", this.getCachedPreparedStatementCount());
            map.put("Version", this.getVersion());

            // 40 -
            map.put("LastErrorTime", this.getLastErrorTime());
            map.put("LastCreateErrorTime", this.getLastCreateErrorTime());
            map.put("CreateErrorCount", this.getCreateErrorCount());
            map.put("DiscardCount", this.getDiscardCount());

            return map;
        } catch (JMException ex) {
            throw new IllegalStateException("getStatData error", ex);
        }
    }

    public Map<String, Object> getStatData() {
        final int activeCount;
        final int activePeak;
        final Date activePeakTime;

        final int poolingCount;
        final int poolingPeak;
        final Date poolingPeakTime;

        final long connectCount;
        final long closeCount;

        lock.lock();
        try {
            poolingCount = this.poolingCount;
            poolingPeak = this.poolingPeak;
            poolingPeakTime = this.getPoolingPeakTime();

            activeCount = this.activeCount;
            activePeak = this.activePeak;
            activePeakTime = this.getActivePeakTime();

            connectCount = this.connectCount;
            closeCount = this.closeCount;
        } finally {
            lock.unlock();
        }
        Map<String, Object> dataMap = new LinkedHashMap<String, Object>();

        dataMap.put("Identity", System.identityHashCode(this));
        dataMap.put("Name", this.getName());
        dataMap.put("DbType", this.getDbType());
        dataMap.put("DriverClassName", this.getDriverClassName());

        dataMap.put("URL", this.getUrl());
        dataMap.put("UserName", this.getUsername());
        dataMap.put("FilterClassNames", this.getFilterClassNames());

        dataMap.put("WaitThreadCount", this.getWaitThreadCount());
        dataMap.put("NotEmptyWaitCount", this.getNotEmptyWaitCount());
        dataMap.put("NotEmptyWaitMillis", this.getNotEmptyWaitMillis());

        dataMap.put("PoolingCount", poolingCount);
        dataMap.put("PoolingPeak", poolingPeak);
        dataMap.put("PoolingPeakTime", poolingPeakTime);

        dataMap.put("ActiveCount", activeCount);
        dataMap.put("ActivePeak", activePeak);
        dataMap.put("ActivePeakTime", activePeakTime);

        dataMap.put("InitialSize", this.getInitialSize());
        dataMap.put("MinIdle", this.getMinIdle());
        dataMap.put("MaxActive", this.getMaxActive());

        dataMap.put("QueryTimeout", this.getQueryTimeout());
        dataMap.put("TransactionQueryTimeout", this.getTransactionQueryTimeout());
        dataMap.put("LoginTimeout", this.getLoginTimeout());
        dataMap.put("ValidConnectionCheckerClassName", this.getValidConnectionCheckerClassName());
        dataMap.put("ExceptionSorterClassName", this.getExceptionSorterClassName());

        dataMap.put("TestOnBorrow", this.isTestOnBorrow());
        dataMap.put("TestOnReturn", this.isTestOnReturn());
        dataMap.put("TestWhileIdle", this.isTestWhileIdle());

        dataMap.put("DefaultAutoCommit", this.isDefaultAutoCommit());
        dataMap.put("DefaultReadOnly", this.isDefaultAutoCommit());
        dataMap.put("DefaultTransactionIsolation", this.getDefaultTransactionIsolation());

        dataMap.put("LogicConnectCount", connectCount);
        dataMap.put("LogicCloseCount", closeCount);
        dataMap.put("LogicConnectErrorCount", this.getConnectErrorCount());

        dataMap.put("PhysicalConnectCount", this.getCreateCount());
        dataMap.put("PhysicalCloseCount", this.getDestroyCount());
        dataMap.put("PhysicalConnectErrorCount", this.getCreateErrorCount());

        dataMap.put("ExecuteCount", this.getExecuteCount());
        dataMap.put("ErrorCount", this.getErrorCount());
        dataMap.put("CommitCount", this.getCommitCount());
        dataMap.put("RollbackCount", this.getRollbackCount());

        dataMap.put("PSCacheAccessCount", this.getCachedPreparedStatementAccessCount());
        dataMap.put("PSCacheHitCount", this.getCachedPreparedStatementHitCount());
        dataMap.put("PSCacheMissCount", this.getCachedPreparedStatementMissCount());

        dataMap.put("StartTransactionCount", this.getStartTransactionCount());
        dataMap.put("TransactionHistogram", this.getTransactionHistogramValues());

        dataMap.put("ConnectionHoldTimeHistogram", this.getDataSourceStat().getConnectionHoldHistogram().toArray());
        dataMap.put("RemoveAbandoned", this.isRemoveAbandoned());
        dataMap.put("ClobOpenCount", this.getDataSourceStat().getClobOpenCount());
        dataMap.put("BlobOpenCount", this.getDataSourceStat().getBlobOpenCount());

        return dataMap;
    }

    public JdbcSqlStat getSqlStat(int sqlId) {
        return this.getDataSourceStat().getSqlStat(sqlId);
    }

    public JdbcSqlStat getSqlStat(long sqlId) {
        return this.getDataSourceStat().getSqlStat(sqlId);
    }

    public Map<String, JdbcSqlStat> getSqlStatMap() {
        return this.getDataSourceStat().getSqlStatMap();
    }

    public Lock getLock() {
        return lock;
    }
}
