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
package com.alibaba.druid.pool;

import com.alibaba.druid.DbType;
import com.alibaba.druid.TransactionTimeoutException;
import com.alibaba.druid.VERSION;
import com.alibaba.druid.filter.AutoLoad;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidPooledPreparedStatement.PreparedStatementKey;
import com.alibaba.druid.pool.vendor.*;
import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.proxy.jdbc.DataSourceProxyConfig;
import com.alibaba.druid.proxy.jdbc.TransactionInfo;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectQuery;
import com.alibaba.druid.sql.ast.statement.SQLSelectQueryBlock;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.sql.parser.SQLParserUtils;
import com.alibaba.druid.sql.parser.SQLStatementParser;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcDataSourceStat;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.stat.JdbcSqlStatValue;
import com.alibaba.druid.support.clickhouse.BalancedClickhouseDriver;
import com.alibaba.druid.support.clickhouse.BalancedClickhouseDriverNative;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.*;
import com.alibaba.druid.wall.WallFilter;
import com.alibaba.druid.wall.WallProviderStatValue;

import javax.management.JMException;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.naming.NamingException;
import javax.naming.Reference;
import javax.naming.Referenceable;
import javax.naming.StringRefAddr;
import javax.sql.ConnectionEvent;
import javax.sql.ConnectionEventListener;
import javax.sql.ConnectionPoolDataSource;
import javax.sql.PooledConnection;

import java.io.Closeable;
import java.net.Socket;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLRecoverableException;
import java.sql.Statement;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ljw [ljw2083@alibaba-inc.com]
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSource extends DruidAbstractDataSource
        implements DruidDataSourceMBean, ManagedDataSource, Referenceable, Closeable, Cloneable, ConnectionPoolDataSource, MBeanRegistration {
    private static final Log LOG = LogFactory.getLog(DruidDataSource.class);
    private static final long serialVersionUID = 1L;
    // stats
    private volatile long recycleErrorCount;
    private volatile long discardErrorCount;
    private volatile Throwable discardErrorLast;
    private long connectCount;
    private long closeCount;
    private volatile long connectErrorCount;
    private long recycleCount;
    private long removeAbandonedCount;
    private long notEmptyWaitCount;
    private long notEmptySignalCount;
    private long notEmptyWaitNanos;
    private int keepAliveCheckCount;
    private int activePeak;
    private long activePeakTime;
    private int poolingPeak;
    private long poolingPeakTime;
    private volatile int keepAliveCheckErrorCount;
    private volatile Throwable keepAliveCheckErrorLast;
    // store
    private volatile DruidConnectionHolder[] connections;
    private int poolingCount;
    private int activeCount;
    private volatile int createDirectCount;
    private volatile long discardCount;
    private int notEmptyWaitThreadCount;
    private int notEmptyWaitThreadPeak;
    //
    private DruidConnectionHolder[] evictConnections;
    private DruidConnectionHolder[] keepAliveConnections;
    // for clean connection old references.
    private volatile DruidConnectionHolder[] nullConnections;

    // threads
    private volatile ScheduledFuture<?> destroySchedulerFuture;
    private DestroyTask destroyTask;

    private final Map<CreateConnectionTask, Future<?>> createSchedulerFutures = new ConcurrentHashMap<>(16);
    private CreateConnectionThread createConnectionThread;
    private DestroyConnectionThread destroyConnectionThread;
    private LogStatsThread logStatsThread;
    private int createTaskCount;

    private volatile long createTaskIdSeed = 1L;
    private long[] createTasks;

    private volatile boolean enable = true;

    private boolean resetStatEnable = true;
    private volatile long resetCount;

    private String initStackTrace;

    private volatile boolean closing;
    private volatile boolean closed;
    private long closeTimeMillis = -1L;

    protected JdbcDataSourceStat dataSourceStat;

    private boolean useGlobalDataSourceStat;
    private boolean mbeanRegistered;
    private boolean logDifferentThread = true;
    private volatile boolean keepAlive;
    private boolean asyncInit;
    protected boolean killWhenSocketReadTimeout;
    protected boolean checkExecuteTime;

    private static List<Filter> autoFilters;
    private boolean loadSpifilterSkip;
    private volatile DataSourceDisableException disableException;

    protected static final AtomicLongFieldUpdater<DruidDataSource> recycleErrorCountUpdater
            = AtomicLongFieldUpdater.newUpdater(DruidDataSource.class, "recycleErrorCount");
    protected static final AtomicLongFieldUpdater<DruidDataSource> connectErrorCountUpdater
            = AtomicLongFieldUpdater.newUpdater(DruidDataSource.class, "connectErrorCount");
    protected static final AtomicLongFieldUpdater<DruidDataSource> resetCountUpdater
            = AtomicLongFieldUpdater.newUpdater(DruidDataSource.class, "resetCount");
    protected static final AtomicLongFieldUpdater<DruidDataSource> createTaskIdSeedUpdater
            = AtomicLongFieldUpdater.newUpdater(DruidDataSource.class, "createTaskIdSeed");
    protected static final AtomicLongFieldUpdater<DruidDataSource> discardErrorCountUpdater
            = AtomicLongFieldUpdater.newUpdater(DruidDataSource.class, "discardErrorCount");
    protected static final AtomicIntegerFieldUpdater<DruidDataSource> keepAliveCheckErrorCountUpdater
            = AtomicIntegerFieldUpdater.newUpdater(DruidDataSource.class, "keepAliveCheckErrorCount");
    protected static final AtomicIntegerFieldUpdater<DruidDataSource> createDirectCountUpdater
            = AtomicIntegerFieldUpdater.newUpdater(DruidDataSource.class, "createDirectCount");

    public DruidDataSource() {
        this(false);
    }

    public DruidDataSource(boolean fairLock) {
        super(fairLock);

        configFromPropeties(System.getProperties());
    }

    public boolean isAsyncInit() {
        return asyncInit;
    }

    public void setAsyncInit(boolean asyncInit) {
        this.asyncInit = asyncInit;
    }

    @Deprecated
    public void configFromPropety(Properties properties) {
        configFromPropeties(properties);
    }

    public void configFromPropeties(Properties properties) {
        DruidDataSourceUtils.configFromProperties(this, properties);
    }

    public boolean isKillWhenSocketReadTimeout() {
        return killWhenSocketReadTimeout;
    }

    public void setKillWhenSocketReadTimeout(boolean killWhenSocketTimeOut) {
        this.killWhenSocketReadTimeout = killWhenSocketTimeOut;
    }

    public boolean isUseGlobalDataSourceStat() {
        return useGlobalDataSourceStat;
    }

    public void setUseGlobalDataSourceStat(boolean useGlobalDataSourceStat) {
        this.useGlobalDataSourceStat = useGlobalDataSourceStat;
    }

    public boolean isKeepAlive() {
        return keepAlive;
    }

    public void setKeepAlive(boolean keepAlive) {
        this.keepAlive = keepAlive;
    }

    public String getInitStackTrace() {
        return initStackTrace;
    }

    public boolean isResetStatEnable() {
        return resetStatEnable;
    }

    public void setResetStatEnable(boolean resetStatEnable) {
        this.resetStatEnable = resetStatEnable;
        if (dataSourceStat != null) {
            dataSourceStat.setResetStatEnable(resetStatEnable);
        }
    }

    public long getDiscardCount() {
        return discardCount;
    }

    public void restart() throws SQLException {
        this.restart(null);
    }

    public void restart(Properties properties) throws SQLException {
        lock.lock();
        try {
            if (activeCount > 0) {
                throw new SQLException("can not restart, activeCount not zero. " + activeCount);
            }
            if (LOG.isInfoEnabled()) {
                LOG.info("{dataSource-" + this.getID() + "} restart");
            }

            this.close();
            this.resetStat();
            this.inited = false;
            this.enable = true;
            this.closed = false;

            if (properties != null) {
                configFromPropeties(properties);
            }
        } finally {
            lock.unlock();
        }
    }

    public void resetStat() {
        if (!isResetStatEnable()) {
            return;
        }

        lock.lock();
        try {
            connectCount = 0;
            closeCount = 0;
            discardCount = 0;
            recycleCount = 0;
            createCount = 0L;
            directCreateCount = 0;
            destroyCount = 0L;
            removeAbandonedCount = 0;
            notEmptyWaitCount = 0;
            notEmptySignalCount = 0L;
            notEmptyWaitNanos = 0;

            activePeak = activeCount;
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

        connectErrorCountUpdater.set(this, 0);
        errorCountUpdater.set(this, 0);
        commitCountUpdater.set(this, 0);
        rollbackCountUpdater.set(this, 0);
        startTransactionCountUpdater.set(this, 0);
        cachedPreparedStatementHitCountUpdater.set(this, 0);
        closedPreparedStatementCountUpdater.set(this, 0);
        preparedStatementCountUpdater.set(this, 0);
        transactionHistogram.reset();
        cachedPreparedStatementDeleteCountUpdater.set(this, 0);
        recycleErrorCountUpdater.set(this, 0);

        resetCountUpdater.incrementAndGet(this);
    }

    public long getResetCount() {
        return this.resetCount;
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
        setPoolPreparedStatements0(value);
    }

    private void setPoolPreparedStatements0(boolean value) {
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
                evictConnections = new DruidConnectionHolder[maxActive];
                keepAliveConnections = new DruidConnectionHolder[maxActive];
                nullConnections = new DruidConnectionHolder[maxActive];
            } else {
                this.connections = Arrays.copyOf(this.connections, allCount);
                evictConnections = new DruidConnectionHolder[allCount];
                keepAliveConnections = new DruidConnectionHolder[allCount];
                nullConnections = new DruidConnectionHolder[allCount];
            }

            this.maxActive = maxActive;
        } finally {
            lock.unlock();
        }
    }

    @SuppressWarnings("rawtypes")
    public void setConnectProperties(Properties properties) {
        if (properties == null) {
            properties = new Properties();
        }

        boolean equals;
        if (properties.size() == this.connectProperties.size()) {
            equals = true;
            for (Map.Entry entry : properties.entrySet()) {
                if (
                    !Objects.equals(
                        this.connectProperties.get(entry.getKey()),
                        entry.getValue()
                    )
                ) {
                    equals = false;
                    break;
                }
            }
        } else {
            equals = false;
        }

        if (!equals) {
            if (inited && LOG.isInfoEnabled()) {
                LOG.info("connectProperties changed : " + this.connectProperties + " -> " + properties);
            }

            configFromPropeties(properties);

            for (Filter filter : this.filters) {
                filter.configFromProperties(properties);
            }

            if (exceptionSorter != null) {
                exceptionSorter.configFromProperties(properties);
            }

            if (validConnectionChecker != null) {
                validConnectionChecker.configFromProperties(properties);
            }

            if (statLogger != null) {
                statLogger.configFromProperties(properties);
            }
        }

        this.connectProperties = properties;
    }

    public void init() throws SQLException {
        if (inited) {
            return;
        }

        // bug fixed for dead lock, for issue #2980
        DruidDriver.getInstance();

        final ReentrantLock lock = this.lock;
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

            initStackTrace = Utils.toString(Thread.currentThread().getStackTrace());

            this.id = DruidDriver.createDataSourceId();
            if (this.id > 1) {
                long delta = (this.id - 1) * 100000;
                connectionIdSeedUpdater.addAndGet(this, delta);
                statementIdSeedUpdater.addAndGet(this, delta);
                resultSetIdSeedUpdater.addAndGet(this, delta);
                transactionIdSeedUpdater.addAndGet(this, delta);
            }

            if (this.jdbcUrl != null) {
                this.jdbcUrl = this.jdbcUrl.trim();
                initFromWrapDriverUrl();
            }
            initTimeoutsFromUrlOrProperties();

            for (Filter filter : filters) {
                filter.init(this);
            }

            if (this.dbTypeName == null || this.dbTypeName.length() == 0) {
                this.dbTypeName = JdbcUtils.getDbType(jdbcUrl, null);
            }

            DbType dbType = DbType.of(this.dbTypeName);
            if (JdbcUtils.isMysqlDbType(dbType)) {
                boolean cacheServerConfigurationSet = false;
                if (this.connectProperties.containsKey("cacheServerConfiguration")) {
                    cacheServerConfigurationSet = true;
                } else if (this.jdbcUrl.indexOf("cacheServerConfiguration") != -1) {
                    cacheServerConfigurationSet = true;
                }
                if (cacheServerConfigurationSet) {
                    this.connectProperties.put("cacheServerConfiguration", "true");
                }
            }

            if (maxActive <= 0) {
                throw new IllegalArgumentException("illegal maxActive " + maxActive);
            }

            if (maxActive < minIdle) {
                throw new IllegalArgumentException("illegal maxActive " + maxActive);
            }

            if (getInitialSize() > maxActive) {
                throw new IllegalArgumentException("illegal initialSize " + this.initialSize + ", maxActive " + maxActive);
            }

            if (timeBetweenLogStatsMillis > 0 && useGlobalDataSourceStat) {
                throw new IllegalArgumentException("timeBetweenLogStatsMillis not support useGlobalDataSourceStat=true");
            }

            if (maxEvictableIdleTimeMillis < minEvictableIdleTimeMillis) {
                throw new SQLException("maxEvictableIdleTimeMillis must be grater than minEvictableIdleTimeMillis");
            }

            if (keepAlive && keepAliveBetweenTimeMillis <= timeBetweenEvictionRunsMillis) {
                throw new SQLException("keepAliveBetweenTimeMillis must be greater than timeBetweenEvictionRunsMillis");
            }

            if (this.driverClass != null) {
                this.driverClass = driverClass.trim();
            }

            initFromSPIServiceLoader();

            resolveDriver();

            initCheck();

            this.netTimeoutExecutor = new SynchronousExecutor();

            initExceptionSorter();
            initValidConnectionChecker();
            validationQueryCheck();

            if (isUseGlobalDataSourceStat()) {
                dataSourceStat = JdbcDataSourceStat.getGlobal();
                if (dataSourceStat == null) {
                    dataSourceStat = new JdbcDataSourceStat("Global", "Global", this.dbTypeName);
                    JdbcDataSourceStat.setGlobal(dataSourceStat);
                }
                if (dataSourceStat.getDbType() == null) {
                    dataSourceStat.setDbType(this.dbTypeName);
                }
            } else {
                dataSourceStat = new JdbcDataSourceStat(this.name, this.jdbcUrl, this.dbTypeName, this.connectProperties);
            }
            dataSourceStat.setResetStatEnable(this.resetStatEnable);

            connections = new DruidConnectionHolder[maxActive];
            evictConnections = new DruidConnectionHolder[maxActive];
            keepAliveConnections = new DruidConnectionHolder[maxActive];
            nullConnections = new DruidConnectionHolder[maxActive];

            SQLException connectError = null;

            if (createScheduler != null && asyncInit) {
                for (int i = 0; i < initialSize; ++i) {
                    submitCreateTask(true);
                }
            } else if (!asyncInit) {
                // init connections
                while (poolingCount < initialSize) {
                    try {
                        PhysicalConnectionInfo pyConnectInfo = createPhysicalConnection();
                        DruidConnectionHolder holder = new DruidConnectionHolder(this, pyConnectInfo);
                        connections[poolingCount++] = holder;
                    } catch (SQLException ex) {
                        LOG.error("init datasource error, url: " + this.getUrl(), ex);
                        if (initExceptionThrow) {
                            connectError = ex;
                            break;
                        } else {
                            Thread.sleep(3000);
                        }
                    }
                }

                if (poolingCount > 0) {
                    poolingPeak = poolingCount;
                    poolingPeakTime = System.currentTimeMillis();
                }
            }

            createAndLogThread();
            createAndStartCreatorThread();
            createAndStartDestroyThread();

            // await threads initedLatch to support dataSource restart.
            if (createConnectionThread != null) {
                createConnectionThread.getInitedLatch().await();
            }
            if (destroyConnectionThread != null) {
                destroyConnectionThread.getInitedLatch().await();
            }

            init = true;

            initedTime = new Date();
            registerMbean();

            if (connectError != null && poolingCount == 0) {
                throw connectError;
            }

            if (keepAlive) {
                if (createScheduler != null) {
                    // async fill to minIdle
                    for (int i = 0; i < minIdle - initialSize; ++i) {
                        submitCreateTask(true);
                    }
                } else {
                    empty.signal();
                }
            }

        } catch (SQLException e) {
            LOG.error("{dataSource-" + this.getID() + "} init error", e);
            throw e;
        } catch (InterruptedException e) {
            throw new SQLException(e.getMessage(), e);
        } catch (RuntimeException e) {
            LOG.error("{dataSource-" + this.getID() + "} init error", e);
            throw e;
        } catch (Error e) {
            LOG.error("{dataSource-" + this.getID() + "} init error", e);
            throw e;

        } finally {
            inited = true;
            lock.unlock();

            if (init && LOG.isInfoEnabled()) {
                String msg = "{dataSource-" + this.getID();

                if (this.name != null && !this.name.isEmpty()) {
                    msg += ",";
                    msg += this.name;
                }

                msg += "} inited";

                LOG.info(msg);
            }
        }
    }

    private void initTimeoutsFromUrlOrProperties() {
        // createPhysicalConnection will set the corresponding parameters based on dbType.
        if (jdbcUrl != null && (jdbcUrl.indexOf("connectTimeout=") != -1 || jdbcUrl.indexOf("socketTimeout=") != -1)) {
            String[] items = jdbcUrl.split("(\\?|&)");
            for (int i = 0; i < items.length; i++) {
                String item = items[i];
                if (item.startsWith("connectTimeout=")) {
                    String strVal = item.substring("connectTimeout=".length());
                    setConnectTimeout(strVal);
                } else if (item.startsWith("socketTimeout=")) {
                    String strVal = item.substring("socketTimeout=".length());
                    setSocketTimeout(strVal);
                }
            }
        }

        Object propertyConnectTimeout = connectProperties.get("connectTimeout");
        if (propertyConnectTimeout instanceof String) {
            setConnectTimeout((String) propertyConnectTimeout);
        } else if (propertyConnectTimeout instanceof Number) {
            setConnectTimeout(((Number) propertyConnectTimeout).intValue());
        }

        Object propertySocketTimeout = connectProperties.get("socketTimeout");
        if (propertySocketTimeout instanceof String) {
            setSocketTimeout((String) propertySocketTimeout);
        } else if (propertySocketTimeout instanceof Number) {
            setSocketTimeout(((Number) propertySocketTimeout).intValue());
        }
    }

    /**
     * Issue 5192,Issue 5457
     * @see <a href="https://dev.mysql.com/doc/connector-j/8.1/en/connector-j-reference-jdbc-url-format.html">MySQL Connection URL Syntax</a>
     * @see <a href="https://mariadb.com/kb/en/about-mariadb-connector-j/">About MariaDB Connector/J</a>
     * @param jdbcUrl
     * @return
     */
    private static boolean isMysqlOrMariaDBUrl(String jdbcUrl) {
        return jdbcUrl.startsWith("jdbc:mysql://") || jdbcUrl.startsWith("jdbc:mysql:loadbalance://")
            || jdbcUrl.startsWith("jdbc:mysql:replication://") || jdbcUrl.startsWith("jdbc:mariadb://")
            || jdbcUrl.startsWith("jdbc:mariadb:loadbalance://") || jdbcUrl.startsWith("jdbc:mariadb:replication://");
    }

    private void submitCreateTask(boolean initTask) {
        createTaskCount++;
        CreateConnectionTask task = new CreateConnectionTask(initTask);
        if (createTasks == null) {
            createTasks = new long[8];
        }

        boolean putted = false;
        for (int i = 0; i < createTasks.length; ++i) {
            if (createTasks[i] == 0) {
                createTasks[i] = task.taskId;
                putted = true;
                break;
            }
        }
        if (!putted) {
            long[] array = new long[createTasks.length * 3 / 2];
            System.arraycopy(createTasks, 0, array, 0, createTasks.length);
            array[createTasks.length] = task.taskId;
            createTasks = array;
        }

        this.createSchedulerFutures.put(task, createScheduler.submit(task));
    }

    private boolean clearCreateTask(long taskId) {
        if (createTasks == null) {
            return false;
        }

        if (taskId == 0) {
            return false;
        }

        for (int i = 0; i < createTasks.length; i++) {
            if (createTasks[i] == taskId) {
                createTasks[i] = 0;
                createTaskCount--;

                if (createTaskCount < 0) {
                    createTaskCount = 0;
                }

                if (createTaskCount == 0 && createTasks.length > 8) {
                    createTasks = new long[8];
                }
                return true;
            }
        }

        if (LOG.isWarnEnabled()) {
            LOG.warn("clear create task failed : " + taskId);
        }

        return false;
    }

    private void createAndLogThread() {
        if (this.timeBetweenLogStatsMillis <= 0) {
            return;
        }

        String threadName = "Druid-ConnectionPool-Log-" + System.identityHashCode(this);
        logStatsThread = new LogStatsThread(threadName);
        logStatsThread.start();

        this.resetStatEnable = false;
    }

    protected void createAndStartDestroyThread() {
        destroyTask = new DestroyTask();

        if (destroyScheduler != null) {
            long period = timeBetweenEvictionRunsMillis;
            if (period <= 0) {
                period = 1000;
            }
            destroySchedulerFuture = destroyScheduler.scheduleAtFixedRate(destroyTask, period, period,
                    TimeUnit.MILLISECONDS);
            return;
        }

        String threadName = "Druid-ConnectionPool-Destroy-" + System.identityHashCode(this);
        destroyConnectionThread = new DestroyConnectionThread(threadName);
        destroyConnectionThread.start();
    }

    protected void createAndStartCreatorThread() {
        if (createScheduler == null) {
            String threadName = "Druid-ConnectionPool-Create-" + System.identityHashCode(this);
            createConnectionThread = new CreateConnectionThread(threadName);
            createConnectionThread.start();
        }
    }

    /**
     * load filters from SPI ServiceLoader
     *
     * @see ServiceLoader
     */
    private void initFromSPIServiceLoader() {
        if (loadSpifilterSkip) {
            return;
        }

        if (autoFilters == null) {
            List<Filter> filters = new ArrayList<Filter>();
            ServiceLoader<Filter> autoFilterLoader = ServiceLoader.load(Filter.class);

            for (Filter filter : autoFilterLoader) {
                AutoLoad autoLoad = filter.getClass().getAnnotation(AutoLoad.class);
                if (autoLoad != null && autoLoad.value()) {
                    filters.add(filter);
                }
            }
            autoFilters = filters;
        }

        for (Filter filter : autoFilters) {
            if (LOG.isInfoEnabled()) {
                LOG.info("load filter from spi :" + filter.getClass().getName());
            }
            addFilter(filter);
        }
    }

    private void initFromWrapDriverUrl() throws SQLException {
        if (!jdbcUrl.startsWith(DruidDriver.DEFAULT_PREFIX)) {
            return;
        }

        DataSourceProxyConfig config = DruidDriver.parseConfig(jdbcUrl, null);
        this.driverClass = config.getRawDriverClassName();

        LOG.error("error url : '" + sanitizedUrl(jdbcUrl) + "', it should be : '" + config.getRawUrl() + "'");

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
        if (!(testOnBorrow || testOnReturn || testWhileIdle)) {
            return;
        }

        if (this.validConnectionChecker != null) {
            return;
        }

        if (this.validationQuery != null && this.validationQuery.length() > 0) {
            return;
        }

        if ("odps".equals(dbTypeName)) {
            return;
        }

        String errorMessage = "";

        if (testOnBorrow) {
            errorMessage += "testOnBorrow is true, ";
        }

        if (testOnReturn) {
            errorMessage += "testOnReturn is true, ";
        }

        if (testWhileIdle) {
            errorMessage += "testWhileIdle is true, ";
        }

        LOG.error(errorMessage + "validationQuery not set");
    }

    protected void resolveDriver() throws SQLException {
        if (this.driver == null) {
            if (this.driverClass == null || this.driverClass.isEmpty()) {
                this.driverClass = JdbcUtils.getDriverClassName(this.jdbcUrl);
            }

            if (MockDriver.class.getName().equals(driverClass)) {
                driver = MockDriver.instance;
            } else if ("com.alibaba.druid.support.clickhouse.BalancedClickhouseDriver".equals(driverClass)) {
                Properties info = new Properties();
                info.put("user", username);
                info.put("password", password);
                info.putAll(connectProperties);
                driver = new BalancedClickhouseDriver(jdbcUrl, info);
            } else if ("com.alibaba.druid.support.clickhouse.BalancedClickhouseDriverNative".equals(driverClass)) {
                Properties info = new Properties();
                info.put("user", username);
                info.put("password", password);
                info.putAll(connectProperties);
                driver = new BalancedClickhouseDriverNative(jdbcUrl, info);
            } else {
                if (jdbcUrl == null && (driverClass == null || driverClass.length() == 0)) {
                    throw new SQLException("url not set");
                }
                driver = JdbcUtils.createDriver(driverClassLoader, driverClass);
            }
        } else {
            if (this.driverClass == null) {
                this.driverClass = driver.getClass().getName();
            }
        }
    }

    protected void initCheck() throws SQLException {
        DbType dbType = DbType.of(this.dbTypeName);

        if (dbType == DbType.oracle) {
            isOracle = true;

            if (driver.getMajorVersion() < 10) {
                throw new SQLException("not support oracle driver " + driver.getMajorVersion() + "."
                        + driver.getMinorVersion());
            }

            if (driver.getMajorVersion() == 10 && isUseOracleImplicitCache()) {
                this.getConnectProperties().setProperty("oracle.jdbc.FreeMemoryOnEnterImplicitCache", "true");
            }

            oracleValidationQueryCheck();
        } else if (dbType == DbType.db2) {
            db2ValidationQueryCheck();
        } else if (dbType == DbType.mysql
                || JdbcUtils.MYSQL_DRIVER.equals(this.driverClass)
                || JdbcUtils.MYSQL_DRIVER_6.equals(this.driverClass)
                || JdbcUtils.MYSQL_DRIVER_603.equals(this.driverClass)
                || JdbcUtils.GOLDENDB_DRIVER.equals(this.driverClass)
                || JdbcUtils.GBASE8S_DRIVER.equals(this.driverClass)
        ) {
            isMySql = true;
        }

        if (removeAbandoned) {
            LOG.warn("removeAbandoned is true, not use in production.");
        }
    }

    private void oracleValidationQueryCheck() {
        if (validationQuery == null) {
            return;
        }
        if (validationQuery.length() == 0) {
            return;
        }

        SQLStatementParser sqlStmtParser = SQLParserUtils.createSQLStatementParser(validationQuery, this.dbTypeName);
        List<SQLStatement> stmtList = sqlStmtParser.parseStatementList();

        if (stmtList.size() != 1) {
            return;
        }

        SQLStatement stmt = stmtList.get(0);
        if (!(stmt instanceof SQLSelectStatement)) {
            return;
        }

        SQLSelectQuery query = ((SQLSelectStatement) stmt).getSelect().getQuery();
        if (query instanceof SQLSelectQueryBlock) {
            if (((SQLSelectQueryBlock) query).getFrom() == null) {
                LOG.error("invalid oracle validationQuery. " + validationQuery + ", may should be : " + validationQuery
                        + " FROM DUAL");
            }
        }
    }

    private void db2ValidationQueryCheck() {
        if (validationQuery == null) {
            return;
        }
        if (validationQuery.length() == 0) {
            return;
        }

        SQLStatementParser sqlStmtParser = SQLParserUtils.createSQLStatementParser(validationQuery, this.dbTypeName);
        List<SQLStatement> stmtList = sqlStmtParser.parseStatementList();

        if (stmtList.size() != 1) {
            return;
        }

        SQLStatement stmt = stmtList.get(0);
        if (!(stmt instanceof SQLSelectStatement)) {
            return;
        }

        SQLSelectQuery query = ((SQLSelectStatement) stmt).getSelect().getQuery();
        if (query instanceof SQLSelectQueryBlock) {
            if (((SQLSelectQueryBlock) query).getFrom() == null) {
                LOG.error("invalid db2 validationQuery. " + validationQuery + ", may should be : " + validationQuery
                        + " FROM SYSDUMMY");
            }
        }
    }

    private void initValidConnectionChecker() {
        if (this.validConnectionChecker != null) {
            return;
        }

        String realDriverClassName = driver.getClass().getName();
        if (JdbcUtils.isMySqlDriver(realDriverClassName)) {
            this.validConnectionChecker = new MySqlValidConnectionChecker();

        } else if (realDriverClassName.equals(JdbcConstants.ORACLE_DRIVER)
                || realDriverClassName.equals(JdbcConstants.ORACLE_DRIVER2)) {
            this.validConnectionChecker = new OracleValidConnectionChecker();

        } else if (realDriverClassName.equals(JdbcConstants.SQL_SERVER_DRIVER)
                || realDriverClassName.equals(JdbcConstants.SQL_SERVER_DRIVER_SQLJDBC4)
                || realDriverClassName.equals(JdbcConstants.SQL_SERVER_DRIVER_JTDS)) {
            this.validConnectionChecker = new MSSQLValidConnectionChecker();

        } else if (realDriverClassName.equals(JdbcConstants.POSTGRESQL_DRIVER)
                || realDriverClassName.equals(JdbcConstants.ENTERPRISEDB_DRIVER)
                || realDriverClassName.equals(JdbcConstants.OPENGAUSS_DRIVER)
                || realDriverClassName.equals(JdbcConstants.POLARDB_DRIVER)) {
            this.validConnectionChecker = new PGValidConnectionChecker();
        } else if (realDriverClassName.equals(JdbcConstants.OCEANBASE_DRIVER)
                || (realDriverClassName.equals(JdbcConstants.OCEANBASE_DRIVER2))) {
            DbType dbType = DbType.of(this.dbTypeName);
            this.validConnectionChecker = new OceanBaseValidConnectionChecker(dbType);
        }

    }

    private void initExceptionSorter() {
        if (exceptionSorter instanceof NullExceptionSorter) {
            if (driver instanceof MockDriver) {
                return;
            }
        } else if (this.exceptionSorter != null) {
            return;
        }

        for (Class<?> driverClass = driver.getClass(); ; ) {
            String realDriverClassName = driverClass.getName();
            if (realDriverClassName.equals(JdbcConstants.MYSQL_DRIVER) //
                    || realDriverClassName.equals(JdbcConstants.MYSQL_DRIVER_6)
                    || realDriverClassName.equals(JdbcConstants.MYSQL_DRIVER_603)) {
                this.exceptionSorter = new MySqlExceptionSorter();
                this.isMySql = true;
            } else if (realDriverClassName.equals(JdbcConstants.ORACLE_DRIVER)
                    || realDriverClassName.equals(JdbcConstants.ORACLE_DRIVER2)) {
                this.exceptionSorter = new OracleExceptionSorter();
            } else if (realDriverClassName.equals(JdbcConstants.OCEANBASE_DRIVER)) { // 写一个真实的 TestCase
                if (JdbcUtils.OCEANBASE_ORACLE.name().equalsIgnoreCase(dbTypeName)) {
                    this.exceptionSorter = new OceanBaseOracleExceptionSorter();
                } else {
                    this.exceptionSorter = new MySqlExceptionSorter();
                }
            } else if (realDriverClassName.equals("com.informix.jdbc.IfxDriver")) {
                this.exceptionSorter = new InformixExceptionSorter();

            } else if (realDriverClassName.equals("com.sybase.jdbc2.jdbc.SybDriver")) {
                this.exceptionSorter = new SybaseExceptionSorter();

            } else if (realDriverClassName.equals(JdbcConstants.POSTGRESQL_DRIVER)
                    || realDriverClassName.equals(JdbcConstants.ENTERPRISEDB_DRIVER)
                    || realDriverClassName.equals(JdbcConstants.POLARDB_DRIVER)) {
                this.exceptionSorter = new PGExceptionSorter();

            } else if (realDriverClassName.equals("com.alibaba.druid.mock.MockDriver")) {
                this.exceptionSorter = new MockExceptionSorter();
            } else if (realDriverClassName.contains("DB2")) {
                this.exceptionSorter = new DB2ExceptionSorter();
            } else if (realDriverClassName.equals(JdbcConstants.GOLDENDB_DRIVER)) {
                this.exceptionSorter = new MySqlExceptionSorter();
                this.isMySql = true;
            } else {
                Class<?> superClass = driverClass.getSuperclass();
                if (superClass != null && superClass != Object.class) {
                    driverClass = superClass;
                    continue;
                }
            }

            break;
        }
    }

    @Override
    public DruidPooledConnection getConnection() throws SQLException {
        return getConnection(maxWait);
    }

    public DruidPooledConnection getConnection(long maxWaitMillis) throws SQLException {
        if (jdbcUrl == null || jdbcUrl.isEmpty()) {
            LOG.warn("getConnection but jdbcUrl is not set,jdbcUrl=" + jdbcUrl + ",username=" + username);
            return null;
        }
        init();

        final int filtersSize = filters.size();
        if (filtersSize > 0) {
            FilterChainImpl filterChain = createChain();
            try {
                return filterChain.dataSource_connect(this, maxWaitMillis);
            } finally {
                recycleFilterChain(filterChain);
            }
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
        int notFullTimeoutRetryCnt = 0;
        for (; ; ) {
            // handle notFullTimeoutRetry
            DruidPooledConnection poolableConnection;
            try {
                poolableConnection = getConnectionInternal(maxWaitMillis);
            } catch (GetConnectionTimeoutException ex) {
                if (notFullTimeoutRetryCnt < this.notFullTimeoutRetryCount && !isFull()) {
                    notFullTimeoutRetryCnt++;
                    if (LOG.isWarnEnabled()) {
                        LOG.warn("get connection timeout retry : " + notFullTimeoutRetryCnt);
                    }
                    continue;
                }
                throw ex;
            }

            if (testOnBorrow) {
                boolean validated = testConnectionInternal(poolableConnection.holder, poolableConnection.conn);
                if (!validated) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("skip not validated connection.");
                    }

                    discardConnection(poolableConnection.holder);
                    continue;
                }
            } else {
                if (poolableConnection.conn.isClosed()) {
                    discardConnection(poolableConnection.holder); // 传入null，避免重复关闭
                    continue;
                }

                if (testWhileIdle) {
                    final DruidConnectionHolder holder = poolableConnection.holder;
                    long currentTimeMillis = System.currentTimeMillis();
                    long lastActiveTimeMillis = holder.lastActiveTimeMillis;
                    long lastExecTimeMillis = holder.lastExecTimeMillis;
                    long lastKeepTimeMillis = holder.lastKeepTimeMillis;

                    if (checkExecuteTime
                            && lastExecTimeMillis != lastActiveTimeMillis) {
                        lastActiveTimeMillis = lastExecTimeMillis;
                    }

                    if (lastKeepTimeMillis > lastActiveTimeMillis) {
                        lastActiveTimeMillis = lastKeepTimeMillis;
                    }

                    long idleMillis = currentTimeMillis - lastActiveTimeMillis;

                    if (idleMillis >= timeBetweenEvictionRunsMillis
                            || idleMillis < 0 // unexcepted branch
                    ) {
                        boolean validated = testConnectionInternal(poolableConnection.holder, poolableConnection.conn);
                        if (!validated) {
                            if (LOG.isDebugEnabled()) {
                                LOG.debug("skip not validated connection.");
                            }

                            discardConnection(poolableConnection.holder);
                            continue;
                        }
                    }
                }
            }

            if (removeAbandoned) {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                poolableConnection.connectStackTrace = stackTrace;
                poolableConnection.setConnectedTimeNano();
                poolableConnection.traceEnable = true;

                activeConnectionLock.lock();
                try {
                    activeConnections.put(poolableConnection, PRESENT);
                } finally {
                    activeConnectionLock.unlock();
                }
            }

            if (!this.defaultAutoCommit) {
                poolableConnection.setAutoCommit(false);
            }

            return poolableConnection;
        }
    }

    /**
     * 抛弃连接，不进行回收，而是抛弃
     *
     * @param conn the connection to be discarded
     * @return a boolean indicating whether the empty signal was called
     * @deprecated
     */
    @Override
    public boolean discardConnection(Connection conn) {
        boolean emptySignalCalled = false;
        if (conn == null) {
            return emptySignalCalled;
        }

        try {
            if (!conn.isClosed()) {
                conn.close();
            }
        } catch (SQLRecoverableException ignored) {
            discardErrorCountUpdater.incrementAndGet(this);
            // ignored
        } catch (Throwable e) {
            discardErrorCountUpdater.incrementAndGet(this);

            if (LOG.isDebugEnabled()) {
                LOG.debug("discard to close connection error", e);
            }
        }

        lock.lock();
        try {
            activeCount--;
            discardCount++;

            int fillCount = minIdle - (activeCount + poolingCount + createTaskCount);
            if (fillCount > 0) {
                emptySignalCalled = true;
                emptySignal(fillCount);
            }
        } finally {
            lock.unlock();
        }
        return emptySignalCalled;
    }

    @Override
    public boolean discardConnection(DruidConnectionHolder holder) {
        boolean emptySignalCalled = false;
        if (holder == null) {
            return emptySignalCalled;
        }

        Connection conn = holder.getConnection();
        if (conn != null) {
            JdbcUtils.close(conn);
        }

        Socket socket = holder.socket;
        if (socket != null) {
            JdbcUtils.close(socket);
        }

        lock.lock();
        try {
            if (holder.discard) {
                return emptySignalCalled;
            }

            if (holder.active) {
                activeCount--;
                holder.active = false;
            }
            discardCount++;

            holder.discard = true;

            int fillCount = minIdle - (activeCount + poolingCount + createTaskCount);
            if (fillCount > 0) {
                emptySignalCalled = true;
                emptySignal(fillCount);
            }
        } finally {
            lock.unlock();
        }
        return emptySignalCalled;
    }

    private DruidPooledConnection getConnectionInternal(long maxWait) throws SQLException {
        if (closed) {
            connectErrorCountUpdater.incrementAndGet(this);
            throw new DataSourceClosedException("dataSource already closed at " + new Date(closeTimeMillis));
        }

        if (!enable) {
            connectErrorCountUpdater.incrementAndGet(this);

            if (disableException != null) {
                throw disableException;
            }

            throw new DataSourceDisableException();
        }

        final int maxWaitThreadCount = this.maxWaitThreadCount;

        DruidConnectionHolder holder;

        long startTime = System.currentTimeMillis();  //进入循环等待之前，先记录开始尝试获取连接的时间
        final long expiredTime = startTime + maxWait;
        for (boolean createDirect = false; ; ) {
            if (createDirect) {
                try {
                    createStartNanosUpdater.set(this, System.nanoTime());
                    if (creatingCountUpdater.compareAndSet(this, 0, 1)) {
                        PhysicalConnectionInfo pyConnInfo = DruidDataSource.this.createPhysicalConnection();
                        holder = new DruidConnectionHolder(this, pyConnInfo);

                        creatingCountUpdater.decrementAndGet(this);
                        directCreateCountUpdater.incrementAndGet(this);

                        if (LOG.isDebugEnabled()) {
                            LOG.debug("conn-direct_create ");
                        }

                        final Lock lock = this.lock;
                        lock.lock();
                        try {
                            if (activeCount + poolingCount < maxActive) {
                                activeCount++;
                                holder.active = true;
                                if (activeCount > activePeak) {
                                    activePeak = activeCount;
                                    activePeakTime = System.currentTimeMillis();
                                }
                                break;
                            }
                        } finally {
                            lock.unlock();
                        }

                        JdbcUtils.close(pyConnInfo.getPhysicalConnection());
                    }
                } finally {
                    createDirect = false;
                    createDirectCountUpdater.decrementAndGet(this);
                }
            }

            final ReentrantLock lock = this.lock;
            try {
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                connectErrorCountUpdater.incrementAndGet(this);
                throw new SQLException("interrupt", e);
            }

            try {
                if (maxWaitThreadCount > 0
                        && notEmptyWaitThreadCount > maxWaitThreadCount) {
                    connectErrorCountUpdater.incrementAndGet(this);
                    throw new SQLException("maxWaitThreadCount " + maxWaitThreadCount + ", current wait Thread count "
                            + lock.getQueueLength());
                }

                if (onFatalError
                        && onFatalErrorMaxActive > 0
                        && activeCount >= onFatalErrorMaxActive) {
                    connectErrorCountUpdater.incrementAndGet(this);

                    StringBuilder errorMsg = new StringBuilder();
                    errorMsg.append("onFatalError, activeCount ")
                            .append(activeCount)
                            .append(", onFatalErrorMaxActive ")
                            .append(onFatalErrorMaxActive);

                    if (lastFatalErrorTimeMillis > 0) {
                        errorMsg.append(", time '")
                                .append(StringUtils.formatDateTime19(
                                        lastFatalErrorTimeMillis, TimeZone.getDefault()))
                                .append("'");
                    }

                    if (lastFatalErrorSql != null) {
                        errorMsg.append(", sql \n")
                                .append(lastFatalErrorSql);
                    }

                    throw new SQLException(
                            errorMsg.toString(), lastFatalError);
                }

                connectCount++;

                if (createScheduler != null
                        && poolingCount == 0
                        && activeCount < maxActive
                        && createDirectCountUpdater.get(this) == 0
                        && creatingCountUpdater.get(this) == 0
                        && createScheduler instanceof ScheduledThreadPoolExecutor) {
                    ScheduledThreadPoolExecutor executor = (ScheduledThreadPoolExecutor) createScheduler;
                    if (executor.getQueue().size() > 0) {
                        if (maxWait > 0 && System.currentTimeMillis() - startTime >= maxWait) {
                            holder = null;
                            break;
                        }
                        createDirect = true;
                        createDirectCountUpdater.incrementAndGet(this);
                        continue;
                    }
                }

                if (maxWait > 0) {
                    if (System.currentTimeMillis() < expiredTime) {
                        holder = pollLast(startTime, expiredTime);
                    } else {
                        holder = null;
                        break;
                    }
                } else {
                    holder = takeLast(startTime);
                }

                if (holder != null) {
                    if (holder.discard) {
                        holder = null;
                        if (maxWait > 0 && System.currentTimeMillis() >= expiredTime) {
                            break;
                        }
                        continue;
                    }

                    activeCount++;
                    holder.active = true;
                    if (activeCount > activePeak) {
                        activePeak = activeCount;
                        activePeakTime = System.currentTimeMillis();
                    }
                }
            } catch (InterruptedException e) {
                connectErrorCountUpdater.incrementAndGet(this);
                throw new SQLException(e.getMessage(), e);
            } catch (SQLException e) {
                connectErrorCountUpdater.incrementAndGet(this);
                throw e;
            } finally {
                lock.unlock();
            }

            break;
        }

        if (holder == null) {
            long waitMillis = System.currentTimeMillis() - startTime;

            final long activeCount;
            final long maxActive;
            final long creatingCount;
            final long createStartNanos;
            final long createErrorCount;
            final Throwable createError;
            try {
                lock.lock();
                activeCount = this.activeCount;
                maxActive = this.maxActive;
                creatingCount = this.creatingCount;
                createStartNanos = this.createStartNanos;
                createErrorCount = this.createErrorCount;
                createError = this.createError;
            } finally {
                lock.unlock();
            }

            StringBuilder buf = new StringBuilder(128);
            buf.append("wait millis ")
                    .append(waitMillis)
                    .append(", active ").append(activeCount)
                    .append(", maxActive ").append(maxActive)
                    .append(", creating ").append(creatingCount);

            if (creatingCount > 0 && createStartNanos > 0) {
                long createElapseMillis = (System.nanoTime() - createStartNanos) / (1000 * 1000);
                if (createElapseMillis > 0) {
                    buf.append(", createElapseMillis ").append(createElapseMillis);
                }
            }

            if (createErrorCount > 0) {
                buf.append(", createErrorCount ").append(createErrorCount);
            }

            List<JdbcSqlStatValue> sqlList = this.getDataSourceStat().getRuningSqlList();
            for (int i = 0; i < sqlList.size(); ++i) {
                if (i != 0) {
                    buf.append('\n');
                } else {
                    buf.append(", ");
                }
                JdbcSqlStatValue sql = sqlList.get(i);
                buf.append("runningSqlCount ").append(sql.getRunningCount());
                buf.append(" : ");
                buf.append(sql.getSql());
            }

            String errorMessage = buf.toString();

            if (createError != null) {
                throw new GetConnectionTimeoutException(errorMessage, createError);
            } else {
                throw new GetConnectionTimeoutException(errorMessage);
            }
        }

        holder.incrementUseCount();

        return new DruidPooledConnection(holder);
    }

    public void handleConnectionException(
            DruidPooledConnection pooledConnection,
            Throwable t,
            String sql
    ) throws SQLException {
        final DruidConnectionHolder holder = pooledConnection.getConnectionHolder();
        if (holder == null) {
            return;
        }

        errorCountUpdater.incrementAndGet(this);
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
                handleFatalError(pooledConnection, sqlEx, sql);
            }

            throw sqlEx;
        } else {
            throw new SQLException("Error", t);
        }
    }

    protected final void handleFatalError(
            DruidPooledConnection conn,
            SQLException error,
            String sql
    ) throws SQLException {
        final DruidConnectionHolder holder = conn.holder;

        if (conn.isTraceEnable()) {
            activeConnectionLock.lock();
            try {
                if (conn.isTraceEnable()) {
                    activeConnections.remove(conn);
                    conn.setTraceEnable(false);
                }
            } finally {
                activeConnectionLock.unlock();
            }
        }

        long lastErrorTimeMillis = this.lastErrorTimeMillis;
        if (lastErrorTimeMillis == 0) {
            lastErrorTimeMillis = System.currentTimeMillis();
        }

        if (sql != null && sql.length() > 1024) {
            sql = sql.substring(0, 1024);
        }

        boolean requireDiscard = false;
        // using dataSourceLock when holder dataSource isn't null because shrink used it to access fatal error variables.
        boolean hasHolderDataSource = (holder != null && holder.getDataSource() != null);
        ReentrantLock fatalErrorCountLock = hasHolderDataSource ? holder.getDataSource().lock : conn.lock;
        fatalErrorCountLock.lock();
        try {
            if ((!conn.closed) && !conn.disable) {
                conn.disable(error);
                requireDiscard = true;
            }

            lastFatalErrorTimeMillis = lastErrorTimeMillis;
            fatalErrorCount++;
            if (fatalErrorCount - fatalErrorCountLastShrink > onFatalErrorMaxActive) {
                // increase fatalErrorCountLastShrink to avoid that emptySignal would be called again by shrink.
                fatalErrorCountLastShrink++;
                onFatalError = true;
            } else {
                onFatalError = false;
            }
            lastFatalError = error;
            lastFatalErrorSql = sql;
        } finally {
            fatalErrorCountLock.unlock();
        }

        boolean emptySignalCalled = false;
        if (requireDiscard) {
            if (holder != null && holder.statementTrace != null) {
                holder.lock.lock();
                try {
                    for (Statement stmt : holder.statementTrace) {
                        JdbcUtils.close(stmt);
                    }
                } finally {
                    holder.lock.unlock();
                }
            }

            // decrease activeCount first to make sure the following emptySignal should be called successfully.
            emptySignalCalled = this.discardConnection(holder);
        }

        // holder.
        LOG.error("{conn-" + (holder != null ? holder.getConnectionId() : "null") + "} discard", error);

        if (!emptySignalCalled && onFatalError && hasHolderDataSource) {
            fatalErrorCountLock.lock();
            try {
                emptySignal();
            } finally {
                fatalErrorCountLock.unlock();
            }
        }
    }

    /**
     * 回收连接
     */
    protected void recycle(DruidPooledConnection pooledConnection) throws SQLException {
        final DruidConnectionHolder holder = pooledConnection.holder;

        if (holder == null) {
            LOG.warn("connectionHolder is null");
            return;
        }

        boolean asyncCloseConnectionEnable = this.removeAbandoned || this.asyncCloseConnectionEnable;
        boolean isSameThread = pooledConnection.ownerThread == Thread.currentThread();

        if (logDifferentThread //
                && (!asyncCloseConnectionEnable) //
                && !isSameThread
        ) {
            LOG.warn("get/close not same thread");
        }

        final Connection physicalConnection = holder.conn;

        if (pooledConnection.traceEnable) {
            Object oldInfo = null;
            activeConnectionLock.lock();
            try {
                if (pooledConnection.traceEnable) {
                    oldInfo = activeConnections.remove(pooledConnection);
                    pooledConnection.traceEnable = false;
                }
            } finally {
                activeConnectionLock.unlock();
            }
            if (oldInfo == null) {
                if (LOG.isWarnEnabled()) {
                    LOG.warn("remove abandoned failed. activeConnections.size " + activeConnections.size());
                }
            }
        }

        final boolean isAutoCommit = holder.underlyingAutoCommit;
        final boolean isReadOnly = holder.underlyingReadOnly;
        final boolean testOnReturn = this.testOnReturn;

        try {
            // check need to rollback?
            if ((!isAutoCommit) && (!isReadOnly)) {
                pooledConnection.rollback();
            }

            // reset holder, restore default settings, clear warnings
            if (!isSameThread) {
                final ReentrantLock lock = pooledConnection.lock;
                lock.lock();
                try {
                    holder.reset();
                } finally {
                    lock.unlock();
                }
            } else {
                holder.reset();
            }

            if (holder.discard) {
                return;
            }

            if (phyMaxUseCount > 0 && holder.useCount >= phyMaxUseCount) {
                discardConnection(holder);
                return;
            }

            if (physicalConnection.isClosed()) {
                lock.lock();
                try {
                    if (holder.active) {
                        activeCount--;
                        holder.active = false;
                    }
                    closeCount++;
                } finally {
                    lock.unlock();
                }
                return;
            }

            if (testOnReturn) {
                boolean validated = testConnectionInternal(holder, physicalConnection);
                if (!validated) {
                    JdbcUtils.close(physicalConnection);

                    destroyCountUpdater.incrementAndGet(this);

                    lock.lock();
                    try {
                        if (holder.active) {
                            activeCount--;
                            holder.active = false;
                        }
                        closeCount++;
                    } finally {
                        lock.unlock();
                    }
                    return;
                }
            }
            if (holder.initSchema != null) {
                holder.conn.setSchema(holder.initSchema);
                holder.initSchema = null;
            }

            if (!enable) {
                discardConnection(holder);
                return;
            }

            boolean result;
            final long currentTimeMillis = System.currentTimeMillis();

            if (phyTimeoutMillis > 0) {
                long phyConnectTimeMillis = currentTimeMillis - holder.connectTimeMillis;
                if (phyConnectTimeMillis > phyTimeoutMillis) {
                    discardConnection(holder);
                    return;
                }
            }

            lock.lock();
            try {
                if (holder.active) {
                    activeCount--;
                    holder.active = false;
                }
                closeCount++;

                result = putLast(holder, currentTimeMillis);
                recycleCount++;
            } finally {
                lock.unlock();
            }

            if (!result) {
                JdbcUtils.close(holder.conn);
                LOG.info("connection recycle failed.");
            }
        } catch (Throwable e) {
            holder.clearStatementCache();

            if (!holder.discard) {
                discardConnection(holder);
                holder.discard = true;
            }

            LOG.error("recycle error", e);
            recycleErrorCountUpdater.incrementAndGet(this);
        }
    }

    public long getRecycleErrorCount() {
        return recycleErrorCount;
    }

    public void clearStatementCache() throws SQLException {
        lock.lock();
        try {
            for (int i = 0; i < poolingCount; ++i) {
                DruidConnectionHolder conn = connections[i];

                if (conn.statementPool != null) {
                    conn.statementPool.clear();
                }
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * close datasource
     */
    public void close() {
        if (LOG.isInfoEnabled()) {
            LOG.info("{dataSource-" + this.getID() + "} closing ...");
        }

        lock.lock();
        try {
            if (this.closed) {
                return;
            }

            if (!this.inited) {
                return;
            }

            this.closing = true;

            if (logStatsThread != null) {
                logStatsThread.interrupt();
            }

            if (createConnectionThread != null) {
                createConnectionThread.interrupt();
            }

            if (destroyConnectionThread != null) {
                destroyConnectionThread.interrupt();
            }

            for (Future<?> createSchedulerFuture : createSchedulerFutures.values()) {
                createSchedulerFuture.cancel(true);
            }

            if (destroySchedulerFuture != null) {
                destroySchedulerFuture.cancel(true);
            }

            for (int i = 0; i < poolingCount; ++i) {
                DruidConnectionHolder connHolder = connections[i];

                for (PreparedStatementHolder stmtHolder : connHolder.getStatementPool().getMap().values()) {
                    connHolder.getStatementPool().closeRemovedStatement(stmtHolder);
                }
                connHolder.getStatementPool().getMap().clear();

                Connection physicalConnection = connHolder.getConnection();
                try {
                    physicalConnection.close();
                } catch (Exception ex) {
                    LOG.warn("close connection error", ex);
                }
                connections[i] = null;
                destroyCountUpdater.incrementAndGet(this);
            }
            poolingCount = 0;
            unregisterMbean();

            enable = false;
            notEmpty.signalAll();
            notEmptySignalCount++;

            this.closed = true;
            this.closeTimeMillis = System.currentTimeMillis();

            disableException = new DataSourceDisableException();

            for (Filter filter : filters) {
                filter.destroy();
            }
        } finally {
            this.closing = false;
            lock.unlock();
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("{dataSource-" + this.getID() + "} closed");
        }
    }

    public void registerMbean() {
        if (!mbeanRegistered) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    ObjectName objectName = DruidDataSourceStatManager.addDataSource(DruidDataSource.this,
                            DruidDataSource.this.name);

                    DruidDataSource.this.setObjectName(objectName);
                    DruidDataSource.this.mbeanRegistered = true;

                    return null;
                }
            });
        }
    }

    public void unregisterMbean() {
        if (mbeanRegistered) {
            AccessController.doPrivileged(new PrivilegedAction<Object>() {
                @Override
                public Object run() {
                    DruidDataSourceStatManager.removeDataSource(DruidDataSource.this);
                    DruidDataSource.this.mbeanRegistered = false;
                    return null;
                }
            });
        }
    }

    public boolean isMbeanRegistered() {
        return mbeanRegistered;
    }

    boolean putLast(DruidConnectionHolder e, long lastActiveTimeMillis) {
        if (activeCount + poolingCount >= maxActive || e.discard || this.closed || this.closing) {
            return false;
        }

        e.lastActiveTimeMillis = lastActiveTimeMillis;
        connections[poolingCount] = e;
        incrementPoolingCount();

        if (poolingCount > poolingPeak) {
            poolingPeak = poolingCount;
            poolingPeakTime = lastActiveTimeMillis;
        }

        notEmpty.signal();
        notEmptySignalCount++;

        return true;
    }

    private DruidConnectionHolder takeLast(long startTime) throws InterruptedException, SQLException {
        return pollLast(startTime, 0);
    }

    private DruidConnectionHolder pollLast(long startTime, long expiredTime) throws InterruptedException, SQLException {
        try {
            long awaitStartTime;
            long estimate = 0;
            while (poolingCount == 0) {
                // send signal to CreateThread create connection
                emptySignal();

                if (failFast && isFailContinuous()) {
                    throw new DataSourceNotAvailableException(createError);
                }

                awaitStartTime = System.currentTimeMillis();
                if (expiredTime != 0) {
                    estimate = expiredTime - awaitStartTime;
                    if (estimate <= 0) {
                        return null;
                    }
                }

                notEmptyWaitThreadCount++;
                if (notEmptyWaitThreadCount > notEmptyWaitThreadPeak) {
                    notEmptyWaitThreadPeak = notEmptyWaitThreadCount;
                }
                try {
                    // signal by recycle or creator
                    if (estimate == 0) {
                        notEmpty.await();
                    } else {
                        notEmpty.await(estimate, TimeUnit.MILLISECONDS);
                    }
                } finally {
                    notEmptyWaitThreadCount--;
                    notEmptyWaitCount++;
                    notEmptyWaitNanos += TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() - awaitStartTime);
                }

                if (!enable) {
                    connectErrorCountUpdater.incrementAndGet(this);
                    if (disableException != null) {
                        throw disableException;
                    }

                    throw new DataSourceDisableException();
                }

            }
        } catch (InterruptedException ie) {
            notEmpty.signal(); // propagate to non-interrupted thread
            notEmptySignalCount++;
            throw ie;
        }

        decrementPoolingCount();
        DruidConnectionHolder last = connections[poolingCount];
        connections[poolingCount] = null;

        long waitNanos = System.currentTimeMillis() - startTime;
        last.setLastNotEmptyWaitNanos(waitNanos);

        return last;
    }

    private final void decrementPoolingCount() {
        poolingCount--;
    }

    private final void incrementPoolingCount() {
        poolingCount++;
    }

    @Override
    public Connection getConnection(String username, String password) throws SQLException {
        if (this.username == null
                && this.password == null
                && username != null
                && password != null) {
            this.username = username;
            this.password = password;

            return getConnection();
        }

        if (!StringUtils.equals(username, this.username)) {
            throw new UnsupportedOperationException("Not supported by DruidDataSource");
        }

        if (!StringUtils.equals(password, this.password)) {
            throw new UnsupportedOperationException("Not supported by DruidDataSource");
        }

        return getConnection();
    }

    public long getCreateCount() {
        lock.lock();
        try {
            return createCount;
        } finally {
            lock.unlock();
        }
    }

    public long getDestroyCount() {
        lock.lock();
        try {
            return destroyCount;
        } finally {
            lock.unlock();
        }
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
        return connectErrorCountUpdater.get(this);
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

    public void logStats() {
        final DruidDataSourceStatLogger statLogger = this.statLogger;
        if (statLogger == null) {
            return;
        }

        DruidDataSourceStatValue statValue = getStatValueAndReset();

        statLogger.log(statValue);
    }

    public DruidDataSourceStatValue getStatValueAndReset() {
        DruidDataSourceStatValue value = new DruidDataSourceStatValue();

        lock.lock();
        try {
            value.setPoolingCount(this.poolingCount);
            value.setPoolingPeak(this.poolingPeak);
            value.setPoolingPeakTime(this.poolingPeakTime);

            value.setActiveCount(this.activeCount);
            value.setActivePeak(this.activePeak);
            value.setActivePeakTime(this.activePeakTime);

            value.setConnectCount(this.connectCount);
            value.setCloseCount(this.closeCount);
            value.setWaitThreadCount(lock.getWaitQueueLength(notEmpty));
            value.setNotEmptyWaitCount(this.notEmptyWaitCount);
            value.setNotEmptyWaitNanos(this.notEmptyWaitNanos);
            value.setKeepAliveCheckCount(this.keepAliveCheckCount);

            // reset
            this.poolingPeak = 0;
            this.poolingPeakTime = 0;
            this.activePeak = 0;
            this.activePeakTime = 0;
            this.connectCount = 0;
            this.closeCount = 0;
            this.keepAliveCheckCount = 0;

            this.notEmptyWaitCount = 0;
            this.notEmptyWaitNanos = 0;
        } finally {
            lock.unlock();
        }

        value.setName(this.getName());
        value.setDbType(this.dbTypeName);
        value.setDriverClassName(this.getDriverClassName());

        value.setUrl(this.getUrl());
        value.setUserName(this.getUsername());
        value.setFilterClassNames(this.getFilterClassNames());

        value.setInitialSize(this.getInitialSize());
        value.setMinIdle(this.getMinIdle());
        value.setMaxActive(this.getMaxActive());

        value.setQueryTimeout(this.getQueryTimeout());
        value.setTransactionQueryTimeout(this.getTransactionQueryTimeout());
        value.setLoginTimeout(this.getLoginTimeout());
        value.setValidConnectionCheckerClassName(this.getValidConnectionCheckerClassName());
        value.setExceptionSorterClassName(this.getExceptionSorterClassName());

        value.setTestOnBorrow(this.testOnBorrow);
        value.setTestOnReturn(this.testOnReturn);
        value.setTestWhileIdle(this.testWhileIdle);

        value.setDefaultAutoCommit(this.isDefaultAutoCommit());

        if (defaultReadOnly != null) {
            value.setDefaultReadOnly(defaultReadOnly);
        }
        value.setDefaultTransactionIsolation(this.getDefaultTransactionIsolation());

        value.setLogicConnectErrorCount(connectErrorCountUpdater.getAndSet(this, 0));

        value.setPhysicalConnectCount(createCountUpdater.getAndSet(this, 0));
        value.setPhysicalCloseCount(destroyCountUpdater.getAndSet(this, 0));
        value.setPhysicalConnectErrorCount(createErrorCountUpdater.getAndSet(this, 0));

        value.setExecuteCount(this.getAndResetExecuteCount());
        value.setErrorCount(errorCountUpdater.getAndSet(this, 0));
        value.setCommitCount(commitCountUpdater.getAndSet(this, 0));
        value.setRollbackCount(rollbackCountUpdater.getAndSet(this, 0));

        value.setPstmtCacheHitCount(cachedPreparedStatementHitCountUpdater.getAndSet(this, 0));
        value.setPstmtCacheMissCount(cachedPreparedStatementMissCountUpdater.getAndSet(this, 0));

        value.setStartTransactionCount(startTransactionCountUpdater.getAndSet(this, 0));
        value.setTransactionHistogram(this.getTransactionHistogram().toArrayAndReset());

        value.setConnectionHoldTimeHistogram(this.getDataSourceStat().getConnectionHoldHistogram().toArrayAndReset());
        value.setRemoveAbandoned(this.isRemoveAbandoned());
        value.setClobOpenCount(this.getDataSourceStat().getClobOpenCountAndReset());
        value.setBlobOpenCount(this.getDataSourceStat().getBlobOpenCountAndReset());

        value.setSqlSkipCount(this.getDataSourceStat().getSkipSqlCountAndReset());
        value.setSqlList(this.getDataSourceStat().getSqlStatMapAndReset());

        return value;
    }

    public long getRemoveAbandonedCount() {
        return removeAbandonedCount;
    }

    protected boolean put(PhysicalConnectionInfo physicalConnectionInfo) {
        DruidConnectionHolder holder = null;
        try {
            holder = new DruidConnectionHolder(DruidDataSource.this, physicalConnectionInfo);
        } catch (SQLException ex) {
            lock.lock();
            try {
                if (createScheduler != null) {
                    clearCreateTask(physicalConnectionInfo.createTaskId);
                }
            } finally {
                lock.unlock();
            }
            LOG.error("create connection holder error", ex);
            return false;
        }

        return put(holder, physicalConnectionInfo.createTaskId, false);
    }

    private boolean put(DruidConnectionHolder holder, long createTaskId, boolean checkExists) {
        lock.lock();
        try {
            if (this.closing || this.closed) {
                return false;
            }

            if (activeCount + poolingCount >= maxActive) {
                if (createScheduler != null) {
                    clearCreateTask(createTaskId);
                }
                return false;
            }

            if (checkExists) {
                for (int i = 0; i < poolingCount; i++) {
                    if (connections[i] == holder) {
                        return false;
                    }
                }
            }

            connections[poolingCount] = holder;
            incrementPoolingCount();

            if (poolingCount > poolingPeak) {
                poolingPeak = poolingCount;
                poolingPeakTime = System.currentTimeMillis();
            }

            notEmpty.signal();
            notEmptySignalCount++;

            if (createScheduler != null) {
                clearCreateTask(createTaskId);

                if (poolingCount + createTaskCount < notEmptyWaitThreadCount) {
                    emptySignal();
                }
            }
        } finally {
            lock.unlock();
        }
        return true;
    }

    public class CreateConnectionTask implements Runnable {
        private int errorCount;
        private boolean initTask;
        private final long taskId;

        public CreateConnectionTask() {
            taskId = createTaskIdSeedUpdater.getAndIncrement(DruidDataSource.this);
        }

        public CreateConnectionTask(boolean initTask) {
            taskId = createTaskIdSeedUpdater.getAndIncrement(DruidDataSource.this);
            this.initTask = initTask;
        }

        @Override
        public void run() {
            runInternal();
        }

        private void runInternal() {
            for (; ; ) {
                // addLast
                lock.lock();
                try {
                    if (closed || closing) {
                        clearCreateTask(taskId);
                        return;
                    }

                    boolean emptyWait = createError == null || poolingCount != 0;

                    if (emptyWait) {
                        // 必须存在线程等待，才创建连接
                        if (poolingCount >= notEmptyWaitThreadCount //
                                && (!(keepAlive && activeCount + poolingCount < minIdle)) // 在keepAlive场景不能放弃创建
                                && (!initTask) // 线程池初始化时的任务不能放弃创建
                                && !isFailContinuous() // failContinuous时不能放弃创建，否则会无法创建线程
                                && !isOnFatalError() // onFatalError时不能放弃创建，否则会无法创建线程
                        ) {
                            clearCreateTask(taskId);
                            return;
                        }
                    }

                    // 防止创建超过maxActive数量的连接
                    if (activeCount + poolingCount >= maxActive) {
                        clearCreateTask(taskId);
                        return;
                    }
                } finally {
                    lock.unlock();
                }

                PhysicalConnectionInfo physicalConnection = null;

                try {
                    physicalConnection = createPhysicalConnection();
                } catch (OutOfMemoryError e) {
                    LOG.error("create connection OutOfMemoryError, out memory. ", e);

                    errorCount++;
                    if (errorCount > connectionErrorRetryAttempts && timeBetweenConnectErrorMillis > 0) {
                        // fail over retry attempts
                        setFailContinuous(true);
                        if (failFast) {
                            lock.lock();
                            try {
                                notEmpty.signalAll();
                            } finally {
                                lock.unlock();
                            }
                        }

                        if (breakAfterAcquireFailure || closing || closed) {
                            lock.lock();
                            try {
                                clearCreateTask(taskId);
                            } finally {
                                lock.unlock();
                            }
                            return;
                        }

                        // reset errorCount
                        this.errorCount = 0;
                        createSchedulerFutures.put(this,
                                createScheduler.schedule(this, timeBetweenConnectErrorMillis, TimeUnit.MILLISECONDS));
                        return;
                    }
                } catch (SQLException e) {
                    LOG.error("create connection SQLException, url: " + sanitizedUrl(jdbcUrl), e);

                    errorCount++;
                    if (errorCount > connectionErrorRetryAttempts && timeBetweenConnectErrorMillis > 0) {
                        // fail over retry attempts
                        setFailContinuous(true);
                        if (failFast) {
                            lock.lock();
                            try {
                                notEmpty.signalAll();
                            } finally {
                                lock.unlock();
                            }
                        }

                        if (breakAfterAcquireFailure || closing || closed) {
                            lock.lock();
                            try {
                                clearCreateTask(taskId);
                            } finally {
                                lock.unlock();
                            }
                            return;
                        }

                        // reset errorCount
                        this.errorCount = 0;
                        createSchedulerFutures.put(this,
                                createScheduler.schedule(this, timeBetweenConnectErrorMillis, TimeUnit.MILLISECONDS));
                        return;
                    }
                } catch (RuntimeException e) {
                    LOG.error("create connection RuntimeException", e);
                    // unknown fatal exception
                    setFailContinuous(true);
                    continue;
                } catch (Error e) {
                    lock.lock();
                    try {
                        clearCreateTask(taskId);
                    } finally {
                        lock.unlock();
                    }
                    LOG.error("create connection Error", e);
                    // unknown fatal exception
                    setFailContinuous(true);
                    break;
                } catch (Throwable e) {
                    lock.lock();
                    try {
                        clearCreateTask(taskId);
                    } finally {
                        lock.unlock();
                    }

                    LOG.error("create connection unexpected error.", e);
                    break;
                }

                if (physicalConnection == null) {
                    continue;
                }

                physicalConnection.createTaskId = taskId;
                boolean result = put(physicalConnection);
                if (!result) {
                    JdbcUtils.close(physicalConnection.getPhysicalConnection());
                    LOG.info("put physical connection to pool failed.");
                }
                break;
            }
        }
    }

    public class CreateConnectionThread extends Thread {
        private final CountDownLatch initedLatch = new CountDownLatch(1);

        public CreateConnectionThread(String name) {
            super(name);
            this.setDaemon(true);
        }

        public CountDownLatch getInitedLatch() {
            return initedLatch;
        }

        public void run() {
            initedLatch.countDown();

            long lastDiscardCount = 0;
            int errorCount = 0;
            while (!closing && !closed && !Thread.currentThread().isInterrupted()) {
                // addLast
                try {
                    lock.lockInterruptibly();
                } catch (InterruptedException e2) {
                    break;
                }

                long discardCount = DruidDataSource.this.discardCount;
                boolean discardChanged = discardCount - lastDiscardCount > 0;
                lastDiscardCount = discardCount;

                try {
                    boolean emptyWait = createError == null
                            || poolingCount != 0
                            || discardChanged;

                    if (emptyWait
                            && asyncInit && createCount < initialSize) {
                        emptyWait = false;
                    }

                    if (emptyWait) {
                        // 必须存在线程等待，才创建连接
                        if (poolingCount >= notEmptyWaitThreadCount //
                                && (!(keepAlive && activeCount + poolingCount < minIdle))
                                && !isFailContinuous()
                        ) {
                            empty.await();
                        }
                    }

                    // 防止创建超过maxActive数量的连接
                    if (activeCount + poolingCount >= maxActive) {
                        empty.await();
                        continue;
                    }
                } catch (InterruptedException e) {
                    lastCreateError = e;
                    lastErrorTimeMillis = System.currentTimeMillis();

                    if ((!closing) && (!closed)) {
                        LOG.error("create connection thread interrupted, url: " + sanitizedUrl(jdbcUrl), e);
                    }
                    break;
                } finally {
                    lock.unlock();
                }

                PhysicalConnectionInfo connection = null;

                try {
                    connection = createPhysicalConnection();
                } catch (SQLException e) {
                    LOG.error("create connection SQLException, url: " + sanitizedUrl(jdbcUrl) + ", errorCode " + e.getErrorCode()
                        + ", state " + e.getSQLState(), e);

                    errorCount++;
                    if (errorCount > connectionErrorRetryAttempts && timeBetweenConnectErrorMillis > 0) {
                        // fail over retry attempts
                        setFailContinuous(true);
                        if (failFast) {
                            lock.lock();
                            try {
                                notEmpty.signalAll();
                            } finally {
                                lock.unlock();
                            }
                        }

                        if (breakAfterAcquireFailure || closing || closed) {
                            break;
                        }

                        try {
                            Thread.sleep(timeBetweenConnectErrorMillis);
                        } catch (InterruptedException interruptEx) {
                            break;
                        }
                    }
                } catch (RuntimeException e) {
                    LOG.error("create connection RuntimeException", e);
                    setFailContinuous(true);
                    continue;
                } catch (Error e) {
                    LOG.error("create connection Error", e);
                    break;
                }

                if (connection == null) {
                    continue;
                }

                boolean result = put(connection);
                if (!result) {
                    JdbcUtils.close(connection.getPhysicalConnection());
                    LOG.info("put physical connection to pool failed.");
                }

                // reset errorCount
                errorCount = 0;
            }
        }
    }

    public class DestroyConnectionThread extends Thread {
        private final CountDownLatch initedLatch = new CountDownLatch(1);

        public DestroyConnectionThread(String name) {
            super(name);
            this.setDaemon(true);
        }

        public CountDownLatch getInitedLatch() {
            return initedLatch;
        }

        public void run() {
            initedLatch.countDown();

            for (; !Thread.currentThread().isInterrupted(); ) {
                // 从前面开始删除
                try {
                    if (closed || closing) {
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

                    destroyTask.run();
                } catch (InterruptedException e) {
                    break;
                }
            }
        }

    }

    public class DestroyTask implements Runnable {
        public DestroyTask() {
        }

        @Override
        public void run() {
            shrink(true, keepAlive);

            if (isRemoveAbandoned()) {
                removeAbandoned();
            }
        }

    }

    public class LogStatsThread extends Thread {
        public LogStatsThread(String name) {
            super(name);
            this.setDaemon(true);
        }

        public void run() {
            try {
                for (; ; ) {
                    try {
                        logStats();
                    } catch (Exception e) {
                        LOG.error("logStats error", e);
                    }

                    Thread.sleep(timeBetweenLogStatsMillis);
                }
            } catch (InterruptedException e) {
                // skip
            }
        }
    }

    public int removeAbandoned() {
        int removeCount = 0;

        if (activeConnections.size() == 0) {
            return removeCount;
        }

        long currrentNanos = System.nanoTime();

        List<DruidPooledConnection> abandonedList = new ArrayList<DruidPooledConnection>();

        activeConnectionLock.lock();
        try {
            Iterator<DruidPooledConnection> iter = activeConnections.keySet().iterator();

            for (; iter.hasNext(); ) {
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
        } finally {
            activeConnectionLock.unlock();
        }

        if (abandonedList.size() > 0) {
            for (DruidPooledConnection pooledConnection : abandonedList) {
                final ReentrantLock lock = pooledConnection.lock;
                lock.lock();
                try {
                    if (pooledConnection.isDisable()) {
                        continue;
                    }
                } finally {
                    lock.unlock();
                }

                JdbcUtils.close(pooledConnection);
                pooledConnection.abandond();
                removeAbandonedCount++;
                removeCount++;

                if (isLogAbandoned()) {
                    StringBuilder buf = new StringBuilder();
                    buf.append("abandon connection, owner thread: ");
                    buf.append(pooledConnection.getOwnerThread().getName());
                    buf.append(", connected at : ");
                    buf.append(pooledConnection.getConnectedTimeMillis());
                    buf.append(", open stackTrace\n");

                    StackTraceElement[] trace = pooledConnection.getConnectStackTrace();
                    for (int i = 0; i < trace.length; i++) {
                        buf.append("\tat ");
                        buf.append(trace[i].toString());
                        buf.append("\n");
                    }

                    buf.append("ownerThread current state is ")
                            .append(pooledConnection.getOwnerThread().getState())
                            .append(", current stackTrace\n");
                    trace = pooledConnection.getOwnerThread().getStackTrace();
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

    /**
     * Instance key
     */
    protected String instanceKey;

    public Reference getReference() throws NamingException {
        final String className = getClass().getName();
        final String factoryName = className + "Factory"; // XXX: not robust
        Reference ref = new Reference(className, factoryName, null);
        ref.add(new StringRefAddr("instanceKey", instanceKey));
        ref.add(new StringRefAddr("url", this.getUrl()));
        ref.add(new StringRefAddr("username", this.getUsername()));
        ref.add(new StringRefAddr("password", this.getPassword()));
        // TODO ADD OTHER PROPERTIES
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
        shrink(false, false);
    }

    public void shrink(boolean checkTime) {
        shrink(checkTime, keepAlive);
    }

    public void shrink(boolean checkTime, boolean keepAlive) {
        if (poolingCount == 0) {
            return;
        }

        final Lock lock = this.lock;
        try {
            lock.lockInterruptibly();
        } catch (InterruptedException e) {
            return;
        }

        boolean needFill = false;
        int evictCount = 0;
        int keepAliveCount = 0;
        int fatalErrorIncrement = fatalErrorCount - fatalErrorCountLastShrink;
        fatalErrorCountLastShrink = fatalErrorCount;

        try {
            if (!inited) {
                return;
            }

            final int checkCount = poolingCount - minIdle;
            final long currentTimeMillis = System.currentTimeMillis();
            // remaining is the position of the next connection should be retained in the pool.
            int remaining = 0;
            int i = 0;
            for (; i < poolingCount; ++i) {
                DruidConnectionHolder connection = connections[i];

                if ((onFatalError || fatalErrorIncrement > 0) && (lastFatalErrorTimeMillis > connection.connectTimeMillis)) {
                    keepAliveConnections[keepAliveCount++] = connection;
                    continue;
                }

                if (checkTime) {
                    if (phyTimeoutMillis > 0) {
                        long phyConnectTimeMillis = currentTimeMillis - connection.connectTimeMillis;
                        if (phyConnectTimeMillis > phyTimeoutMillis) {
                            evictConnections[evictCount++] = connection;
                            continue;
                        }
                    }

                    long idleMillis = currentTimeMillis - connection.lastActiveTimeMillis;

                    if (idleMillis < minEvictableIdleTimeMillis
                            && idleMillis < keepAliveBetweenTimeMillis) {
                        break;
                    }

                    if (idleMillis >= minEvictableIdleTimeMillis) {
                        if (i < checkCount) {
                            evictConnections[evictCount++] = connection;
                            continue;
                        } else if (idleMillis > maxEvictableIdleTimeMillis) {
                            evictConnections[evictCount++] = connection;
                            continue;
                        }
                    }

                    if (keepAlive && idleMillis >= keepAliveBetweenTimeMillis
                            && currentTimeMillis - connection.lastKeepTimeMillis >= keepAliveBetweenTimeMillis) {
                        keepAliveConnections[keepAliveCount++] = connection;
                    } else {
                        if (i != remaining) {
                            // move the connection to the new position for retaining it in the pool.
                            connections[remaining] = connection;
                        }
                        remaining++;
                    }
                } else {
                    if (i < checkCount) {
                        evictConnections[evictCount++] = connection;
                    } else {
                        break;
                    }
                }
            }

            // shrink connections by HotSpot intrinsic function _arraycopy for performance optimization.
            int removeCount = evictCount + keepAliveCount;
            if (removeCount > 0) {
                int breakedCount = poolingCount - i;
                if (breakedCount > 0) {
                    // retains the connections that start at the break position.
                    System.arraycopy(connections, i, connections, remaining, breakedCount);
                    remaining += breakedCount;
                }
                // clean the old references of the connections that have been moved forward to the new positions.
                System.arraycopy(nullConnections, 0, connections, remaining, removeCount);
                poolingCount -= removeCount;
            }
            keepAliveCheckCount += keepAliveCount;

            if (keepAlive && poolingCount + activeCount < minIdle) {
                needFill = true;
            }
        } finally {
            lock.unlock();
        }

        if (evictCount > 0) {
            for (int i = 0; i < evictCount; ++i) {
                DruidConnectionHolder item = evictConnections[i];
                Connection connection = item.getConnection();
                JdbcUtils.close(connection);
                destroyCountUpdater.incrementAndGet(this);
            }
            // use HotSpot intrinsic function _arraycopy for performance optimization.
            System.arraycopy(nullConnections, 0, evictConnections, 0, evictConnections.length);
        }

        if (keepAliveCount > 0) {
            // keep order
            for (int i = keepAliveCount - 1; i >= 0; --i) {
                DruidConnectionHolder holder = keepAliveConnections[i];
                Connection connection = holder.getConnection();
                holder.incrementKeepAliveCheckCount();

                boolean validate = false;
                try {
                    this.validateConnection(connection);
                    validate = true;
                } catch (Throwable error) {
                    keepAliveCheckErrorLast = error;
                    keepAliveCheckErrorCountUpdater.incrementAndGet(this);
                    if (LOG.isDebugEnabled()) {
                        LOG.debug("keepAliveErr", error);
                    }
                }

                boolean discard = !validate;
                if (validate) {
                    holder.lastKeepTimeMillis = System.currentTimeMillis();
                    boolean putOk = put(holder, 0L, true);
                    if (!putOk) {
                        discard = true;
                    }
                }

                if (discard) {
                    try {
                        connection.close();
                    } catch (Exception error) {
                        discardErrorLast = error;
                        discardErrorCountUpdater.incrementAndGet(DruidDataSource.this);
                        if (LOG.isErrorEnabled()) {
                            LOG.error("discard connection error", error);
                        }
                    }

                    if (holder.socket != null) {
                        try {
                            holder.socket.close();
                        } catch (Exception error) {
                            discardErrorLast = error;
                            discardErrorCountUpdater.incrementAndGet(DruidDataSource.this);
                            if (LOG.isErrorEnabled()) {
                                LOG.error("discard connection error", error);
                            }
                        }
                    }

                    lock.lock();
                    try {
                        holder.discard = true;
                        discardCount++;

                        if (activeCount + poolingCount + createTaskCount < minIdle) {
                            needFill = true;
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
            this.getDataSourceStat().addKeepAliveCheckCount(keepAliveCount);
            // use HotSpot intrinsic function _arraycopy for performance optimization.
            System.arraycopy(nullConnections, 0, keepAliveConnections, 0, keepAliveConnections.length);
        }

        if (needFill) {
            lock.lock();
            try {
                int fillCount = minIdle - (activeCount + poolingCount + createTaskCount);
                emptySignal(fillCount);
            } finally {
                lock.unlock();
            }
        } else if (fatalErrorIncrement > 0) {
            lock.lock();
            try {
                emptySignal();
            } finally {
                lock.unlock();
            }
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
        return this.errorCount;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append("{");

        buf.append("\n\tCreateTime:\"");
        buf.append(Utils.toString(getCreatedTime()));
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
                map.put("connectionId", connHolder.getConnectionId());
                map.put("useCount", connHolder.getUseCount());
                if (connHolder.lastActiveTimeMillis > 0) {
                    map.put("lastActiveTime", new Date(connHolder.lastActiveTimeMillis));
                }
                if (connHolder.lastKeepTimeMillis > 0) {
                    map.put("lastKeepTimeMillis", new Date(connHolder.lastKeepTimeMillis));
                }
                map.put("connectTime", new Date(connHolder.getTimeMillis()));
                map.put("holdability", connHolder.getUnderlyingHoldability());
                map.put("transactionIsolation", connHolder.getUnderlyingTransactionIsolation());
                map.put("autoCommit", connHolder.underlyingAutoCommit);
                map.put("readoOnly", connHolder.isUnderlyingReadOnly());

                if (connHolder.isPoolPreparedStatements()) {
                    List<Map<String, Object>> stmtCache = new ArrayList<Map<String, Object>>();
                    PreparedStatementPool stmtPool = connHolder.getStatementPool();
                    for (PreparedStatementHolder stmtHolder : stmtPool.getMap().values()) {
                        Map<String, Object> stmtInfo = new LinkedHashMap<String, Object>();

                        stmtInfo.put("sql", stmtHolder.key.getSql());
                        stmtInfo.put("defaultRowPrefetch", stmtHolder.getDefaultRowPrefetch());
                        stmtInfo.put("rowPrefetch", stmtHolder.getRowPrefetch());
                        stmtInfo.put("hitCount", stmtHolder.getHitCount());

                        stmtCache.add(stmtInfo);
                    }

                    map.put("pscache", stmtCache);
                }
                map.put("keepAliveCheckCount", connHolder.getKeepAliveCheckCount());

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

    public Object clone() {
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
            map.put("ActiveCount", this.getActiveCount());
            map.put("PoolingCount", this.getPoolingCount());
            map.put("LockQueueLength", this.getLockQueueLength());
            map.put("WaitThreadCount", this.getNotEmptyWaitThreadCount());

            // 10 - 14
            map.put("InitialSize", this.getInitialSize());
            map.put("MaxActive", this.getMaxActive());
            map.put("MinIdle", this.getMinIdle());
            map.put("PoolPreparedStatements", this.isPoolPreparedStatements());
            map.put("TestOnBorrow", this.isTestOnBorrow());

            // 15 - 19
            map.put("TestOnReturn", this.isTestOnReturn());
            map.put("MinEvictableIdleTimeMillis", this.minEvictableIdleTimeMillis);
            map.put("ConnectErrorCount", this.getConnectErrorCount());
            map.put("CreateTimespanMillis", this.getCreateTimespanMillis());
            map.put("DbType", this.dbTypeName);

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
            map.put("ExecuteQueryCount", this.getExecuteQueryCount());

            map.put("ExecuteUpdateCount", this.getExecuteUpdateCount());
            map.put("InitStackTrace", this.getInitStackTrace());

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
        dataMap.put("DbType", this.dbTypeName);
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
        dataMap.put("DefaultReadOnly", this.getDefaultReadOnly());
        dataMap.put("DefaultTransactionIsolation", this.getDefaultTransactionIsolation());

        dataMap.put("LogicConnectCount", connectCount);
        dataMap.put("LogicCloseCount", closeCount);
        dataMap.put("LogicConnectErrorCount", this.getConnectErrorCount());

        dataMap.put("PhysicalConnectCount", this.getCreateCount());
        dataMap.put("PhysicalCloseCount", this.getDestroyCount());
        dataMap.put("PhysicalConnectErrorCount", this.getCreateErrorCount());

        dataMap.put("DiscardCount", this.getDiscardCount());

        dataMap.put("ExecuteCount", this.getExecuteCount());
        dataMap.put("ExecuteUpdateCount", this.getExecuteUpdateCount());
        dataMap.put("ExecuteQueryCount", this.getExecuteQueryCount());
        dataMap.put("ExecuteBatchCount", this.getExecuteBatchCount());
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
        dataMap.put("KeepAliveCheckCount", this.getDataSourceStat().getKeepAliveCheckCount());

        dataMap.put("KeepAlive", this.isKeepAlive());
        dataMap.put("FailFast", this.isFailFast());
        dataMap.put("MaxWait", this.getMaxWait());
        dataMap.put("MaxWaitThreadCount", this.getMaxWaitThreadCount());
        dataMap.put("PoolPreparedStatements", this.isPoolPreparedStatements());
        dataMap.put("MaxPoolPreparedStatementPerConnectionSize", this.getMaxPoolPreparedStatementPerConnectionSize());
        dataMap.put("MinEvictableIdleTimeMillis", this.minEvictableIdleTimeMillis);
        dataMap.put("MaxEvictableIdleTimeMillis", this.maxEvictableIdleTimeMillis);

        dataMap.put("LogDifferentThread", isLogDifferentThread());
        dataMap.put("RecycleErrorCount", getRecycleErrorCount());
        dataMap.put("PreparedStatementOpenCount", getPreparedStatementCount());
        dataMap.put("PreparedStatementClosedCount", getClosedPreparedStatementCount());

        dataMap.put("UseUnfairLock", isUseUnfairLock());
        dataMap.put("InitGlobalVariants", isInitGlobalVariants());
        dataMap.put("InitVariants", isInitVariants());
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

    public Map<String, Object> getWallStatMap() {
        WallProviderStatValue wallStatValue = getWallStatValue(false);

        if (wallStatValue != null) {
            return wallStatValue.toMap();
        }

        return null;
    }

    public WallProviderStatValue getWallStatValue(boolean reset) {
        for (Filter filter : this.filters) {
            if (filter instanceof WallFilter) {
                WallFilter wallFilter = (WallFilter) filter;
                return wallFilter.getProvider().getStatValue(reset);
            }
        }

        return null;
    }

    public Lock getLock() {
        return lock;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        for (Filter filter : this.filters) {
            if (filter.isWrapperFor(iface)) {
                return true;
            }
        }

        if (this.statLogger != null
                && (this.statLogger.getClass() == iface || DruidDataSourceStatLogger.class == iface)) {
            return true;
        }

        return super.isWrapperFor(iface);
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) {
        for (Filter filter : this.filters) {
            if (filter.isWrapperFor(iface)) {
                return (T) filter;
            }
        }

        if (this.statLogger != null
                && (this.statLogger.getClass() == iface || DruidDataSourceStatLogger.class == iface)) {
            return (T) statLogger;
        }

        return super.unwrap(iface);
    }

    public boolean isLogDifferentThread() {
        return logDifferentThread;
    }

    public void setLogDifferentThread(boolean logDifferentThread) {
        this.logDifferentThread = logDifferentThread;
    }

    public DruidPooledConnection tryGetConnection() throws SQLException {
        if (poolingCount == 0) {
            return null;
        }
        return getConnection();
    }

    @Override
    public int fill() throws SQLException {
        return this.fill(this.maxActive);
    }

    @Override
    public int fill(int toCount) throws SQLException {
        if (closed) {
            throw new DataSourceClosedException("dataSource already closed at " + new Date(closeTimeMillis));
        }

        if (toCount < 0) {
            throw new IllegalArgumentException("toCount can't not be less than zero");
        }

        init();

        if (toCount > this.maxActive) {
            toCount = this.maxActive;
        }

        int fillCount = 0;
        for (; ; ) {
            try {
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                connectErrorCountUpdater.incrementAndGet(this);
                throw new SQLException("interrupt", e);
            }

            boolean fillable = this.isFillable(toCount);

            lock.unlock();

            if (!fillable) {
                break;
            }

            DruidConnectionHolder holder;
            try {
                PhysicalConnectionInfo pyConnInfo = createPhysicalConnection();
                holder = new DruidConnectionHolder(this, pyConnInfo);
            } catch (SQLException e) {
                LOG.error("fill connection error, url: " + sanitizedUrl(this.jdbcUrl), e);
                connectErrorCountUpdater.incrementAndGet(this);
                throw e;
            }

            try {
                lock.lockInterruptibly();
            } catch (InterruptedException e) {
                connectErrorCountUpdater.incrementAndGet(this);
                throw new SQLException("interrupt", e);
            }

            boolean result;
            try {
                if (!this.isFillable(toCount)) {
                    JdbcUtils.close(holder.getConnection());
                    LOG.info("fill connections skip.");
                    break;
                }
                result = this.putLast(holder, System.currentTimeMillis());
                fillCount++;
            } finally {
                lock.unlock();
            }

            if (!result) {
                JdbcUtils.close(holder.getConnection());
                LOG.info("connection fill failed.");
            }
        }

        if (LOG.isInfoEnabled()) {
            LOG.info("fill " + fillCount + " connections");
        }

        return fillCount;
    }

    static String sanitizedUrl(String url) {
        if (url == null) {
            return null;
        }
        for (String pwdKeyNamesInMysql : new String[]{
            "password=", "password1=", "password2=", "password3=",
            "trustCertificateKeyStorePassword=",
            "clientCertificateKeyStorePassword=",
        }) {
            if (url.contains(pwdKeyNamesInMysql)) {
                url = url.replaceAll("([?&;]" + pwdKeyNamesInMysql + ")[^&#;]*(.*)", "$1<masked>$2");
            }
        }
        return url;
    }

    private boolean isFillable(int toCount) {
        int currentCount = this.poolingCount + this.activeCount;
        return currentCount < toCount && currentCount < this.maxActive;
    }

    public boolean isFull() {
        lock.lock();
        try {
            return this.poolingCount + this.activeCount >= this.maxActive;
        } finally {
            lock.unlock();
        }
    }

    private void emptySignal() {
        emptySignal(1);
    }

    private void emptySignal(int fillCount) {
        if (createScheduler == null) {
            if (activeCount + poolingCount >= maxActive) {
                return;
            }
            empty.signal();
            return;
        }

        for (int i = 0; i < fillCount; i++) {
            if (activeCount + poolingCount + createTaskCount >= maxActive) {
                return;
            }

            if (createTaskCount >= maxCreateTaskCount) {
                return;
            }

            submitCreateTask(false);
        }
    }

    @Override
    public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception {
        if (server != null) {
            try {
                if (server.isRegistered(name)) {
                    server.unregisterMBean(name);
                }
            } catch (Exception ex) {
                LOG.warn("DruidDataSource preRegister error", ex);
            }
        }
        return name;
    }

    @Override
    public void postRegister(Boolean registrationDone) {
    }

    @Override
    public void preDeregister() throws Exception {
    }

    @Override
    public void postDeregister() {
    }

    public boolean isClosed() {
        return this.closed;
    }

    public boolean isCheckExecuteTime() {
        return checkExecuteTime;
    }

    public void setCheckExecuteTime(boolean checkExecuteTime) {
        this.checkExecuteTime = checkExecuteTime;
    }

    public boolean isLoadSpifilterSkip() {
        return loadSpifilterSkip;
    }

    public void setLoadSpifilterSkip(boolean loadSpifilterSkip) {
        this.loadSpifilterSkip = loadSpifilterSkip;
    }

    public void forEach(Connection conn) {
    }
}
