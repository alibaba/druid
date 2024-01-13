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

import com.alibaba.druid.Constants;
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
import org.jctools.queues.MpscBlockingConsumerArrayQueue;
import org.springframework.util.CollectionUtils;

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
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import static com.alibaba.druid.pool.DruidConnectionHolder.ACTIVE;
import static com.alibaba.druid.pool.DruidConnectionHolder.IDLE;
import static com.alibaba.druid.pool.DruidConnectionHolder.PROCESSING;
import static com.alibaba.druid.util.Utils.getBoolean;

/**
 * @author ljw [ljw2083@alibaba-inc.com]
 * @author wenshao [szujobs@hotmail.com]
 * @author zrlw [zrlw@sina.com]
 */
public class DruidDataSource extends DruidAbstractDataSource
        implements DruidDataSourceMBean, ManagedDataSource, Referenceable, Closeable, Cloneable, ConnectionPoolDataSource, MBeanRegistration {
    private static final Log LOG = LogFactory.getLog(DruidDataSource.class);
    private static final long serialVersionUID = 1L;
    // stats
    private volatile Throwable discardErrorLast;
    private volatile AtomicLong connectCount = new AtomicLong(0);
    private volatile AtomicLong closeCount = new AtomicLong(0);

    private volatile AtomicLong recycleCount = new AtomicLong(0);
    private volatile AtomicLong removeAbandonedCount = new AtomicLong(0);
    private volatile AtomicLong notEmptyWaitCount = new AtomicLong(0);
    private volatile AtomicLong notEmptyWaitNanos = new AtomicLong(0);
    private volatile AtomicInteger keepAliveCheckCount = new AtomicInteger(0);
    private volatile AtomicInteger activePeak = new AtomicInteger(0);
    private volatile long activePeakTime;
    private volatile AtomicInteger poolingPeak = new AtomicInteger(0);
    private long poolingPeakTime;
    private volatile Throwable keepAliveCheckErrorLast;
    // store
    private volatile Map<Object, DruidConnectionHolder> connections = new ConcurrentHashMap<>();
    private volatile Map<DruidPooledConnection, Object> activeConnections = new ConcurrentHashMap<>();
    private volatile AtomicInteger poolingCount = new AtomicInteger(0);
    private volatile AtomicInteger activeCount = new AtomicInteger(0);
    private volatile AtomicLong discardCount = new AtomicLong(0);
    private volatile AtomicInteger notEmptyWaitThreadCount = new AtomicInteger(0);
    private volatile AtomicInteger notEmptyWaitThreadPeak = new AtomicInteger(0);

    private CreateConnectionThread createConnectionThread;
    private LogStatsThread logStatsThread;

    private volatile boolean enable = true;

    private boolean resetStatEnable = true;

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

    // has updater
    private volatile long recycleErrorCount;
    protected static final AtomicLongFieldUpdater<DruidDataSource> recycleErrorCountUpdater
            = AtomicLongFieldUpdater.newUpdater(DruidDataSource.class, "recycleErrorCount");

    private volatile long connectErrorCount;
    protected static final AtomicLongFieldUpdater<DruidDataSource> connectErrorCountUpdater
            = AtomicLongFieldUpdater.newUpdater(DruidDataSource.class, "connectErrorCount");

    private volatile long resetCount;
    protected static final AtomicLongFieldUpdater<DruidDataSource> resetCountUpdater
            = AtomicLongFieldUpdater.newUpdater(DruidDataSource.class, "resetCount");

    private volatile long discardErrorCount;
    protected static final AtomicLongFieldUpdater<DruidDataSource> discardErrorCountUpdater
            = AtomicLongFieldUpdater.newUpdater(DruidDataSource.class, "discardErrorCount");

    private volatile int keepAliveCheckErrorCount;
    protected static final AtomicIntegerFieldUpdater<DruidDataSource> keepAliveCheckErrorCountUpdater
            = AtomicIntegerFieldUpdater.newUpdater(DruidDataSource.class, "keepAliveCheckErrorCount");

    private volatile MpscBlockingConsumerArrayQueue<DruidConnectionRequest> requestQueue;
    private volatile long lastGetConnectionTime;

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
        {
            String property = properties.getProperty("druid.name");
            if (property != null) {
                this.setName(property);
            }
        }
        {
            String property = properties.getProperty("druid.url");
            if (property != null) {
                this.setUrl(property);
            }
        }
        {
            String property = properties.getProperty("druid.username");
            if (property != null) {
                this.setUsername(property);
            }
        }
        {
            String property = properties.getProperty("druid.password");
            if (property != null) {
                this.setPassword(property);
            }
        }
        {
            Boolean value = getBoolean(properties, "druid.testWhileIdle");
            if (value != null) {
                this.testWhileIdle = value;
            }
        }
        {
            Boolean value = getBoolean(properties, "druid.testOnBorrow");
            if (value != null) {
                this.testOnBorrow = value;
            }
        }
        {
            String property = properties.getProperty("druid.validationQuery");
            if (property != null && property.length() > 0) {
                this.setValidationQuery(property);
            }
        }
        {
            Boolean value = getBoolean(properties, "druid.useGlobalDataSourceStat");
            if (value != null) {
                this.setUseGlobalDataSourceStat(value);
            }
        }
        {
            Boolean value = getBoolean(properties, "druid.useGloalDataSourceStat"); // compatible for early versions
            if (value != null) {
                this.setUseGlobalDataSourceStat(value);
            }
        }
        {
            Boolean value = getBoolean(properties, "druid.asyncInit"); // compatible for early versions
            if (value != null) {
                this.setAsyncInit(value);
            }
        }
        {
            String property = properties.getProperty("druid.filters");

            if (property != null && property.length() > 0) {
                try {
                    this.setFilters(property);
                } catch (SQLException e) {
                    LOG.error("setFilters error", e);
                }
            }
        }
        {
            String property = properties.getProperty(Constants.DRUID_TIME_BETWEEN_LOG_STATS_MILLIS);
            if (property != null && property.length() > 0) {
                try {
                    long value = Long.parseLong(property);
                    this.setTimeBetweenLogStatsMillis(value);
                } catch (NumberFormatException e) {
                    LOG.error("illegal property '" + Constants.DRUID_TIME_BETWEEN_LOG_STATS_MILLIS + "'", e);
                }
            }
        }
        {
            String property = properties.getProperty(Constants.DRUID_STAT_SQL_MAX_SIZE);
            if (property != null && property.length() > 0) {
                try {
                    int value = Integer.parseInt(property);
                    if (dataSourceStat != null) {
                        dataSourceStat.setMaxSqlSize(value);
                    }
                } catch (NumberFormatException e) {
                    LOG.error("illegal property '" + Constants.DRUID_STAT_SQL_MAX_SIZE + "'", e);
                }
            }
        }
        {
            Boolean value = getBoolean(properties, "druid.clearFiltersEnable");
            if (value != null) {
                this.setClearFiltersEnable(value);
            }
        }
        {
            Boolean value = getBoolean(properties, "druid.resetStatEnable");
            if (value != null) {
                this.setResetStatEnable(value);
            }
        }
        {
            String property = properties.getProperty("druid.notFullTimeoutRetryCount");
            if (property != null && property.length() > 0) {
                try {
                    int value = Integer.parseInt(property);
                    this.setNotFullTimeoutRetryCount(value);
                } catch (NumberFormatException e) {
                    LOG.error("illegal property 'druid.notFullTimeoutRetryCount'", e);
                }
            }
        }
        {
            String property = properties.getProperty("druid.timeBetweenEvictionRunsMillis");
            if (property != null && property.length() > 0) {
                try {
                    long value = Long.parseLong(property);
                    this.setTimeBetweenEvictionRunsMillis(value);
                } catch (NumberFormatException e) {
                    LOG.error("illegal property 'druid.timeBetweenEvictionRunsMillis'", e);
                }
            }
        }
        {
            String property = properties.getProperty("druid.maxWaitThreadCount");
            if (property != null && property.length() > 0) {
                try {
                    int value = Integer.parseInt(property);
                    this.setMaxWaitThreadCount(value);
                } catch (NumberFormatException e) {
                    LOG.error("illegal property 'druid.maxWaitThreadCount'", e);
                }
            }
        }
        {
            String property = properties.getProperty("druid.maxWait");
            if (property != null && property.length() > 0) {
                try {
                    int value = Integer.parseInt(property);
                    this.setMaxWait(value);
                } catch (NumberFormatException e) {
                    LOG.error("illegal property 'druid.maxWait'", e);
                }
            }
        }
        {
            Boolean value = getBoolean(properties, "druid.failFast");
            if (value != null) {
                this.setFailFast(value);
            }
        }
        {
            String property = properties.getProperty("druid.phyTimeoutMillis");
            if (property != null && property.length() > 0) {
                try {
                    long value = Long.parseLong(property);
                    this.setPhyTimeoutMillis(value);
                } catch (NumberFormatException e) {
                    LOG.error("illegal property 'druid.phyTimeoutMillis'", e);
                }
            }
        }
        {
            String property = properties.getProperty("druid.phyMaxUseCount");
            if (property != null && property.length() > 0) {
                try {
                    long value = Long.parseLong(property);
                    this.setPhyMaxUseCount(value);
                } catch (NumberFormatException e) {
                    LOG.error("illegal property 'druid.phyMaxUseCount'", e);
                }
            }
        }
        {
            String property = properties.getProperty("druid.minEvictableIdleTimeMillis");
            if (property != null && property.length() > 0) {
                try {
                    long value = Long.parseLong(property);
                    this.setMinEvictableIdleTimeMillis(value);
                } catch (NumberFormatException e) {
                    LOG.error("illegal property 'druid.minEvictableIdleTimeMillis'", e);
                }
            }
        }
        {
            String property = properties.getProperty("druid.maxEvictableIdleTimeMillis");
            if (property != null && property.length() > 0) {
                try {
                    long value = Long.parseLong(property);
                    this.setMaxEvictableIdleTimeMillis(value);
                } catch (NumberFormatException e) {
                    LOG.error("illegal property 'druid.maxEvictableIdleTimeMillis'", e);
                }
            }
        }
        {
            Boolean value = getBoolean(properties, "druid.keepAlive");
            if (value != null) {
                this.setKeepAlive(value);
            }
        }
        {
            String property = properties.getProperty("druid.keepAliveBetweenTimeMillis");
            if (property != null && property.length() > 0) {
                try {
                    long value = Long.parseLong(property);
                    this.setKeepAliveBetweenTimeMillis(value);
                } catch (NumberFormatException e) {
                    LOG.error("illegal property 'druid.keepAliveBetweenTimeMillis'", e);
                }
            }
        }
        {
            Boolean value = getBoolean(properties, "druid.poolPreparedStatements");
            if (value != null) {
                this.setPoolPreparedStatements0(value);
            }
        }
        {
            Boolean value = getBoolean(properties, "druid.initVariants");
            if (value != null) {
                this.setInitVariants(value);
            }
        }
        {
            Boolean value = getBoolean(properties, "druid.initGlobalVariants");
            if (value != null) {
                this.setInitGlobalVariants(value);
            }
        }
        {
            Boolean value = getBoolean(properties, "druid.useUnfairLock");
            if (value != null) {
                this.setUseUnfairLock(value);
            }
        }
        {
            String property = properties.getProperty("druid.driverClassName");
            if (property != null) {
                this.setDriverClassName(property);
            }
        }
        {
            String property = properties.getProperty("druid.initialSize");
            if (property != null && property.length() > 0) {
                try {
                    int value = Integer.parseInt(property);
                    this.setInitialSize(value);
                } catch (NumberFormatException e) {
                    LOG.error("illegal property 'druid.initialSize'", e);
                }
            }
        }
        {
            String property = properties.getProperty("druid.minIdle");
            if (property != null && property.length() > 0) {
                try {
                    int value = Integer.parseInt(property);
                    this.setMinIdle(value);
                } catch (NumberFormatException e) {
                    LOG.error("illegal property 'druid.minIdle'", e);
                }
            }
        }
        {
            String property = properties.getProperty("druid.maxActive");
            if (property != null && property.length() > 0) {
                try {
                    int value = Integer.parseInt(property);
                    this.setMaxActive(value);
                } catch (NumberFormatException e) {
                    LOG.error("illegal property 'druid.maxActive'", e);
                }
            }
        }
        {
            Boolean value = getBoolean(properties, "druid.killWhenSocketReadTimeout");
            if (value != null) {
                setKillWhenSocketReadTimeout(value);
            }
        }
        {
            String property = properties.getProperty("druid.connectProperties");
            if (property != null) {
                this.setConnectionProperties(property);
            }
        }
        {
            String property = properties.getProperty("druid.maxPoolPreparedStatementPerConnectionSize");
            if (property != null && property.length() > 0) {
                try {
                    int value = Integer.parseInt(property);
                    this.setMaxPoolPreparedStatementPerConnectionSize(value);
                } catch (NumberFormatException e) {
                    LOG.error("illegal property 'druid.maxPoolPreparedStatementPerConnectionSize'", e);
                }
            }
        }
        {
            String property = properties.getProperty("druid.initConnectionSqls");
            if (property != null && property.length() > 0) {
                try {
                    StringTokenizer tokenizer = new StringTokenizer(property, ";");
                    setConnectionInitSqls(Collections.list(tokenizer));
                } catch (NumberFormatException e) {
                    LOG.error("illegal property 'druid.initConnectionSqls'", e);
                }
            }
        }
        {
            String property = System.getProperty("druid.load.spifilter.skip");
            if (property != null && !"false".equals(property)) {
                loadSpifilterSkip = true;
            }
        }
        {
            String property = System.getProperty("druid.checkExecuteTime");
            if (property != null && !"false".equals(property)) {
                checkExecuteTime = true;
            }
        }
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
        return discardCount.get();
    }

    public void restart() throws SQLException {
        this.restart(null);
    }

    public void restart(Properties properties) throws SQLException {
        if (activeCount.get() > 0) {
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
    }

    public void resetStat() {
        if (!isResetStatEnable()) {
            return;
        }

        connectCount.set(0);
        closeCount.set(0);
        discardCount.set(0);
        recycleCount.set(0);
        createCountUpdater.set(this, 0);
        destroyCountUpdater.set(this, 0);
        removeAbandonedCount.set(0);
        notEmptyWaitCount.set(0);
        notEmptyWaitNanos.set(0);

        activeCount.set(0);
        activePeak.set(0);
        activePeakTime = 0;
        poolingPeak.set(0);
        createTimespan = 0;
        lastError = null;
        lastErrorTimeMillis = 0;
        lastCreateError = null;
        lastCreateErrorTimeMillis = 0;

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
        return resetCountUpdater.get(this);
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
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
            Iterator<DruidConnectionHolder> it = connections.values().iterator();
            DruidConnectionHolder connection;
            while (it.hasNext()) {
                connection = it.next();
                if (connection.getActiveFlag().compareAndSet(IDLE, PROCESSING)) {
                    try {
                        for (PreparedStatementHolder holder : connection.getStatementPool().getMap().values()) {
                            closePreapredStatement(holder);
                        }
                        connection.getStatementPool().getMap().clear();
                    } finally {
                        connection.getActiveFlag().set(IDLE);
                    }
                }
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

        lock.writeLock().lock();
        try {
            Iterator<Map.Entry<Object, DruidConnectionHolder>> it = connections.entrySet().iterator();
            DruidConnectionHolder connection;
            int cnt = 0;
            while (it.hasNext()) {
                connection = it.next().getValue();
                if (cnt < maxActive) {
                    cnt++;
                    continue;
                }
                if (connection.getActiveFlag().compareAndSet(IDLE, PROCESSING)) {
                    try {
                        // remove the current priors to pollingCount decrement.
                        it.remove();
                        poolingCount.decrementAndGet();
                        JdbcUtils.close(connection.getConnection());
                    } finally {
                        connection.getActiveFlag().set(IDLE);
                    }
                }
            }
            this.maxActive = maxActive;
        } finally {
            lock.writeLock().unlock();
        }

        try {
            int newCapacity = Math.max(maxActive, 16);
            if (requestQueue.capacity() != newCapacity) {
                MpscBlockingConsumerArrayQueue<DruidConnectionRequest> oldQueue = requestQueue;
                requestQueue = new MpscBlockingConsumerArrayQueue<>(newCapacity);
                DruidConnectionRequest req;
                while ((req = oldQueue.poll(0, TimeUnit.MILLISECONDS)) != null) {
                    if (!requestQueue.offer(req)) {
                        req.setStopping(true);
                        req.getDoneLatch().countDown();
                    }
                }
                tryNoticeCreateConnectionThread(oldQueue);
            }
        } catch (InterruptedException e) {
            LOG.debug("{dataSource-" + this.getID() + "} interrupt", e);
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
                Object value = this.connectProperties.get(entry.getKey());
                Object entryValue = entry.getValue();
                if (value == null && entryValue != null) {
                    equals = false;
                    break;
                }

                if (!value.equals(entry.getValue())) {
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

        final ReentrantReadWriteLock lock = this.lock;
        try {
            lock.writeLock().lockInterruptibly();
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
                DruidDataSource.connectionIdSeedUpdater.addAndGet(this, delta);
                DruidDataSource.statementIdSeedUpdater.addAndGet(this, delta);
                DruidDataSource.resultSetIdSeedUpdater.addAndGet(this, delta);
                DruidDataSource.transactionIdSeedUpdater.addAndGet(this, delta);
            }

            if (connectTimeout == 0) {
                connectTimeout = DEFAULT_TIME_CONNECT_TIMEOUT_MILLIS;
            }

            if (socketTimeout == 0) {
                socketTimeout = DEFAULT_TIME_SOCKET_TIMEOUT_MILLIS;
            }

            if (this.jdbcUrl != null) {
                this.jdbcUrl = this.jdbcUrl.trim();
                initFromWrapDriverUrl();
                initFromUrlOrProperties();
            }

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

            requestQueue = new MpscBlockingConsumerArrayQueue<>(Math.max(maxActive, 16));

            createAndLogThread();
            createAndStartCreatorThread();

            init = true;

            initedTime = new Date();
            registerMbean();

            if (keepAlive) {
                tryNoticeCreateConnectionThread(requestQueue);
            }
        } catch (SQLException e) {
            LOG.error("{dataSource-" + this.getID() + "} init error", e);
            throw e;
        } catch (RuntimeException e) {
            LOG.error("{dataSource-" + this.getID() + "} init error", e);
            throw e;
        } catch (Error e) {
            LOG.error("{dataSource-" + this.getID() + "} init error", e);
            throw e;

        } finally {
            inited = true;
            lock.writeLock().unlock();

            if (createConnectionThread != null) {
                try {
                    createConnectionThread.getInitedLatch().await();
                } catch (InterruptedException e) {
                    LOG.debug("{dataSource-" + this.getID() + "} initialize connections error", e);
                    throw new SQLException("interrupt", e);
                }
            }

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

    private void initFromUrlOrProperties() {
        if (isMysqlOrMariaDBUrl(jdbcUrl)) {
            if (jdbcUrl.indexOf("connectTimeout=") != -1 || jdbcUrl.indexOf("socketTimeout=") != -1) {
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

    private void createAndLogThread() {
        if (this.timeBetweenLogStatsMillis <= 0) {
            return;
        }

        String threadName = "Druid-ConnectionPool-Log-" + System.identityHashCode(this);
        logStatsThread = new LogStatsThread(threadName);
        logStatsThread.start();

        this.resetStatEnable = false;
    }

    @Deprecated
    protected void createAndStartDestroyThread() {
        // keep only for unit tests.
    }

    protected void createAndStartCreatorThread() {
        String threadName = "Druid-ConnectionPool-Create-" + System.identityHashCode(this);
        createConnectionThread = new CreateConnectionThread(threadName);
        createConnectionThread.start();
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

                activeConnections.put(poolableConnection, PRESENT);
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

        activeCount.decrementAndGet();
        discardCount.incrementAndGet();

        if (connections.size() < minIdle) {
            tryNoticeCreateConnectionThread(requestQueue);
            emptySignalCalled = true;
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

        if (holder.discard) {
            return emptySignalCalled;
        }

        if (holder.active) {
            activeCount.decrementAndGet();
            holder.active = false;
            discardCount.incrementAndGet();
            holder.discard = true;
        }

        connections.remove(Thread.currentThread());
        if (connections.size() < minIdle) {
            emptySignalCalled = true;
            tryNoticeCreateConnectionThread(requestQueue);
        }
        return emptySignalCalled;
    }

    private DruidPooledConnection getConnectionInternal(long maxWaitMillis) throws SQLException {
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
        long expiredTime = startTime + maxWaitMillis;
        Thread currentThread = Thread.currentThread();
        lastGetConnectionTime = startTime;
        while (true) {
            try {
                if (maxWaitThreadCount > 0
                        && notEmptyWaitThreadCount.get() > maxWaitThreadCount) {
                    connectErrorCountUpdater.incrementAndGet(this);
                    throw new SQLException("maxWaitThreadCount " + maxWaitThreadCount + ", current wait Thread count "
                            + lock.getQueueLength());
                }

                if (onFatalError
                        && onFatalErrorMaxActive > 0
                        && activeCount.get() >= onFatalErrorMaxActive) {
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

                connectCount.incrementAndGet();
                if (maxWaitMillis > 0) {
                    if (System.currentTimeMillis() < expiredTime) {
                        holder = getThreadConnection(currentThread, startTime, expiredTime);
                    } else {
                        holder = null;
                        break;
                    }
                } else {
                    holder = getThreadConnection(currentThread, startTime, 0);
                }

                if (holder != null && holder.discard) {
                    // decrease activeCount and reset active as getThreadConnection has increased activeCount and set active to true.
                    activeCount.decrementAndGet();
                    holder.active = false;
                    holder = null;
                    if (maxWaitMillis > 0 && System.currentTimeMillis() >= expiredTime) {
                        break;
                    }
                    continue;
                }
            } catch (InterruptedException e) {
                connectErrorCountUpdater.incrementAndGet(this);
                throw new SQLException(e.getMessage(), e);
            } catch (SQLException e) {
                connectErrorCountUpdater.incrementAndGet(this);
                throw e;
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
            final ReentrantReadWriteLock lock = this.lock;
            try {
                lock.writeLock().lockInterruptibly();
            } catch (InterruptedException e) {
                throw new GetConnectionTimeoutException("interrupt", e);
            }
            try {
                activeCount = this.activeCount.get();
                maxActive = this.maxActive;
                creatingCount = creatingCountUpdater.get(this);
                createStartNanos = createStartNanosUpdater.get(this);
                createErrorCount = createErrorCountUpdater.get(this);
                createError = this.createError;
            } finally {
                lock.writeLock().unlock();
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

        DruidPooledConnection poolalbeConnection = new DruidPooledConnection(holder);
        return poolalbeConnection;
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
            activeConnections.remove(conn);
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
        ReentrantReadWriteLock fatalErrorCountLock = hasHolderDataSource ? holder.getDataSource().lock : conn.lock;
        try {
            fatalErrorCountLock.writeLock().lockInterruptibly();
        } catch (InterruptedException e) {
            throw new SQLException("interrupt", e);
        }
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
            fatalErrorCountLock.writeLock().unlock();
        }

        boolean emptySignalCalled = false;
        if (requireDiscard) {
            if (!CollectionUtils.isEmpty(holder.statementTrace)) {
                try {
                    holder.lock.writeLock().lockInterruptibly();
                } catch (InterruptedException e) {
                    throw new SQLException("interrupt", e);
                }
                try {
                    for (Statement stmt : holder.statementTrace) {
                        JdbcUtils.close(stmt);
                    }
                } finally {
                    holder.lock.writeLock().unlock();
                }
            }

            // decrease activeCount first to make sure the following emptySignal should be called successfully.
            emptySignalCalled = this.discardConnection(holder);
        }

        // holder.
        LOG.error("{conn-" + holder.getConnectionId() + "} discard", error);

        if (!emptySignalCalled && onFatalError && hasHolderDataSource) {
            tryNoticeCreateConnectionThread(requestQueue);
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
            if (pooledConnection.traceEnable) {
                oldInfo = activeConnections.remove(pooledConnection);
                pooledConnection.traceEnable = false;
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
                final ReentrantReadWriteLock lock = pooledConnection.lock;
                try {
                    lock.writeLock().lockInterruptibly();
                } catch (InterruptedException e) {
                    throw new SQLException("interrupt", e);
                }
                try {
                    holder.reset();
                } finally {
                    lock.writeLock().unlock();
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
                if (holder.active) {
                    activeCount.decrementAndGet();
                    holder.active = false;
                }
                closeCount.incrementAndGet();
                return;
            }

            if (testOnReturn) {
                boolean validated = testConnectionInternal(holder, physicalConnection);
                if (!validated) {
                    JdbcUtils.close(physicalConnection);

                    destroyCountUpdater.incrementAndGet(this);

                    if (holder.active) {
                        activeCount.decrementAndGet();
                        holder.active = false;
                    }
                    closeCount.incrementAndGet();
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

            closeCount.incrementAndGet();

            result = putLast(holder, currentTimeMillis, false);
            recycleCount.incrementAndGet();

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
        return recycleErrorCountUpdater.get(this);
    }

    public void clearStatementCache() throws SQLException {
        try {
            lock.writeLock().lockInterruptibly();
        } catch (InterruptedException e) {
            throw new SQLException("interrupt", e);
        }
        try {
            DruidConnectionHolder conn;
            Iterator<DruidConnectionHolder> it = connections.values().iterator();
            while (it.hasNext()) {
                conn = it.next();
                if (conn.getActiveFlag().compareAndSet(IDLE, PROCESSING)) {
                    try {
                        if (conn.statementPool != null) {
                            conn.statementPool.clear();
                        }
                    } finally {
                        conn.getActiveFlag().set(IDLE);
                    }
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * close datasource
     */
    public void close() {
        if (LOG.isInfoEnabled()) {
            LOG.info("{dataSource-" + this.getID() + "} closing ...");
        }

        if (this.closed) {
            return;
        }

        if (!this.inited) {
            return;
        }

        try {
            lock.writeLock().lockInterruptibly();
        } catch (InterruptedException e) {
            LOG.debug("{dataSource-" + this.getID() + "} close operation interrupted. ", e);
            return;
        }

        try {
            this.closing = true;

            if (logStatsThread != null) {
                logStatsThread.interrupt();
            }

            // send signal only once to avoid dead locking.
            if (createConnectionThread != null
                    && createConnectionThread.isAlive()
                    && !createConnectionThread.isExiting()) {
                createConnectionThread.interrupt();
            }

            if (connections != null) {
                DruidConnectionHolder connHolder;
                Iterator<DruidConnectionHolder> it = connections.values().iterator();
                while (it.hasNext()) {
                    connHolder = it.next();
                    if (!connHolder.getActiveFlag().compareAndSet(IDLE, PROCESSING)) {
                        continue;
                    }
                    // remove the current priors to pollingCount decrement.
                    it.remove();
                    poolingCount.decrementAndGet();
                    for (PreparedStatementHolder stmtHolder : connHolder.getStatementPool().getMap().values()) {
                        connHolder.getStatementPool().closeRemovedStatement(stmtHolder);
                    }
                    connHolder.getStatementPool().getMap().clear();

                    Connection physicalConnection = connHolder.getConnection();
                    if (physicalConnection != null) {
                        try {
                            physicalConnection.close();
                        } catch (Exception ex) {
                            LOG.warn("close connection error", ex);
                        }
                    }
                    destroyCountUpdater.incrementAndGet(this);
                }
            }
            unregisterMbean();

            enable = false;

            this.closed = true;
            this.closeTimeMillis = System.currentTimeMillis();

            disableException = new DataSourceDisableException();

            for (Filter filter : filters) {
                filter.destroy();
            }
        } finally {
            this.closing = false;
            lock.writeLock().unlock();
        }

        while (createConnectionThread != null
                && createConnectionThread.isAlive()
                && !createConnectionThread.isExiting()) {
            createConnectionThread.interrupt();
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

    boolean putLast(DruidConnectionHolder holder, long lastActiveTimeMillis, boolean isNewConnection) throws InterruptedException {
        if (isNewConnection || holder.getActiveFlag().compareAndSet(ACTIVE, PROCESSING)) {
            holder.lastActiveTimeMillis = lastActiveTimeMillis;

            if (holder.active) {
                activeCount.decrementAndGet();
                holder.active = false;
            }

            if (isNewConnection) {
                try {
                    lock.writeLock().lockInterruptibly();
                } catch (InterruptedException e) {
                    LOG.debug("{dataSource-" + this.getID() + "} put connection to pool failed. ", e);
                    return false;
                }
                try {
                    if (this.closing || this.closed) {
                        return false;
                    }

                    if (connections.size() >= maxActive) {
                        return false;
                    }

                    // increase poolingCount priors to put operation.
                    int count = poolingCount.incrementAndGet();
                    int peak = poolingPeak.get();
                    if (count > peak) {
                        poolingPeak.compareAndSet(peak, count);
                        poolingPeakTime = System.currentTimeMillis();
                    }

                    connections.put(new Object(), holder);
                } finally {
                    lock.writeLock().unlock();
                }
            } else {
                int count = poolingCount.incrementAndGet();
                int peak = poolingPeak.get();
                if (count > peak) {
                    poolingPeak.compareAndSet(peak, count);
                    poolingPeakTime = System.currentTimeMillis();
                }
                // reset active flag after poolingCount increment.
                holder.getActiveFlag().set(IDLE);
            }
        }
        return true;
    }

    private void incActiveCountAndDecPoolingCount(DruidConnectionHolder last) {
        int count = activeCount.incrementAndGet();
        poolingCount.decrementAndGet();
        int peak = activePeak.get();
        if (count > peak) {
            if (activePeak.compareAndSet(peak, count)) {
                activePeakTime = System.currentTimeMillis();
            }
        }
        last.active = true;
    }

    private DruidConnectionHolder getThreadConnection(Thread currentThread, long startTime, long expiredTime)
            throws InterruptedException, SQLException {
        DruidConnectionHolder last = connections.get(currentThread);
        if (last != null) {
            if (last.getActiveFlag().compareAndSet(IDLE, ACTIVE)) {
                incActiveCountAndDecPoolingCount(last);
                long waitNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() - startTime);
                last.setLastNotEmptyWaitNanos(waitNanos);
                return last;
            }
            last = null;
        }

        int count = notEmptyWaitThreadCount.incrementAndGet();
        int peak = notEmptyWaitThreadPeak.get();
        if (count > peak) {
            notEmptyWaitThreadPeak.compareAndSet(peak, count);
        }

        try {
            DruidConnectionRequest req = new DruidConnectionRequest(currentThread);
            if (expiredTime != 0) {
                req.setExpiredTime(expiredTime);
            }

            // offer connection request to createConnectionThread.
            boolean addOk = false;
            do {
                if (addOk = requestQueue.offer(req)) {
                    break;
                }
                Thread.sleep(1);
            } while (enable && !req.isStopping()
                    && (expiredTime == 0 || System.currentTimeMillis() < expiredTime));

            if (addOk) {
                if (expiredTime == 0) {
                    req.getDoneLatch().await();
                    last = connections.get(currentThread);
                } else {
                    long waitMillis = expiredTime - System.currentTimeMillis();
                    if (waitMillis > 0 && req.getDoneLatch().await(waitMillis, TimeUnit.MILLISECONDS)) {
                        last = connections.get(currentThread);
                    }
                }
            }

            long waitNanos = TimeUnit.MILLISECONDS.toNanos(System.currentTimeMillis() - startTime);
            if (last != null) {
                if (last.getActiveFlag().compareAndSet(currentThread, ACTIVE)) {
                    incActiveCountAndDecPoolingCount(last);
                    last.setLastNotEmptyWaitNanos(waitNanos);
                } else {
                    last = null;
                }
            }

            notEmptyWaitCount.incrementAndGet();
            long currentVal = notEmptyWaitNanos.get();
            while (!notEmptyWaitNanos.compareAndSet(currentVal, currentVal + waitNanos)) {
                currentVal = notEmptyWaitNanos.get();
            }

            if (!enable) {
                connectErrorCountUpdater.incrementAndGet(this);

                if (disableException != null) {
                    throw disableException;
                }

                throw new DataSourceDisableException();
            }
        } finally {
            notEmptyWaitThreadCount.decrementAndGet();
        }

        return last;
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
       return createCountUpdater.get(this);
    }

    public long getDestroyCount() {
        return destroyCountUpdater.get(this);
    }

    public long getConnectCount() {
        return connectCount.get();
    }

    public long getCloseCount() {
        return closeCount.get();
    }

    public long getConnectErrorCount() {
        return connectErrorCountUpdater.get(this);
    }

    @Override
    public int getPoolingCount() {
        return poolingCount.get();
    }

    public int getPoolingPeak() {
        return poolingPeak.get();
    }

    public Date getPoolingPeakTime() {
        if (poolingPeakTime <= 0) {
            return null;
        }

        return new Date(poolingPeakTime);
    }

    public long getRecycleCount() {
        return recycleCount.get();
    }

    public int getActiveCount() {
        return activeCount.get();
    }

    public void logStats() throws SQLException {
        final DruidDataSourceStatLogger statLogger = this.statLogger;
        if (statLogger == null) {
            return;
        }

        DruidDataSourceStatValue statValue = getStatValueAndReset();

        statLogger.log(statValue);
    }

    public DruidDataSourceStatValue getStatValueAndReset() {
        DruidDataSourceStatValue value = new DruidDataSourceStatValue();

        try {
            lock.writeLock().lockInterruptibly();
        } catch (InterruptedException e) {
            LOG.debug("{dataSource-" + this.getID() + "} interrupted. ", e);
            return null;
        }
        try {
            value.setPoolingCount(this.poolingCount.get());
            value.setPoolingPeak(this.poolingPeak.get());
            value.setPoolingPeakTime(this.poolingPeakTime);

            value.setActiveCount(this.activeCount.get());
            value.setActivePeak(this.activePeak.get());
            value.setActivePeakTime(this.activePeakTime);

            value.setConnectCount(this.connectCount.get());
            value.setCloseCount(this.closeCount.get());
            value.setWaitThreadCount(notEmptyWaitThreadCount.get());
            value.setNotEmptyWaitCount(this.notEmptyWaitCount.get());
            value.setNotEmptyWaitNanos(this.notEmptyWaitNanos.get());
            value.setKeepAliveCheckCount(this.keepAliveCheckCount.get());

            // reset
            this.poolingPeak.set(0);
            this.poolingPeakTime = 0;
            this.activePeak.set(0);
            this.activePeakTime = 0;
            this.connectCount.set(0);
            this.closeCount.set(0);
            this.keepAliveCheckCount.set(0);

            this.notEmptyWaitCount.set(0);
            this.notEmptyWaitNanos.set(0);
        } finally {
            lock.writeLock().unlock();
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
        return removeAbandonedCount.get();
    }

    protected boolean put(PhysicalConnectionInfo physicalConnectionInfo, DruidConnectionRequest connReq) {
        DruidConnectionHolder holder = null;
        try {
            holder = new DruidConnectionHolder(DruidDataSource.this, physicalConnectionInfo);
        } catch (SQLException ex) {
            LOG.error("create connection holder error", ex);
            return false;
        }

        return put(holder, connReq);
    }

    private boolean put(DruidConnectionHolder holder, DruidConnectionRequest connReq) {
        try {
            lock.writeLock().lockInterruptibly();
        } catch (InterruptedException e) {
            LOG.debug("{dataSource-" + this.getID() + "} put connection to pool failed. ", e);
            return false;
        }
        try {
            if (this.closing || this.closed) {
                return false;
            }

            if (connections.size() >= maxActive) {
                return false;
            }

            // increase poolingCount priors to put operation.
            int count = poolingCount.incrementAndGet();
            int peak = poolingPeak.get();
            if (count > peak) {
                poolingPeak.compareAndSet(peak, count);
                poolingPeakTime = System.currentTimeMillis();
            }

            if (connReq == null) {
                connections.put(new Object(), holder);
            } else {
                // set the candidate connection active flag to connection request thread.
                Thread connReqThread = connReq.getThread();
                holder.getActiveFlag().set(connReqThread);
                connections.put(connReqThread, holder);
                connReq.getDoneLatch().countDown();
            }
        } finally {
            lock.writeLock().unlock();
        }
        return true;
    }

    public class CreateConnectionThread extends Thread {
        private final long emptyWaitTimes = Math.max(timeBetweenEvictionRunsMillis, 1000);
        private long nextDestroyTaskTime = System.currentTimeMillis() + emptyWaitTimes;
        private boolean initTask = true;
        private final CountDownLatch initedLatch = new CountDownLatch(1);
        private boolean exiting;

        public CreateConnectionThread(String name) {
            super(name);
            this.setDaemon(true);
        }

        public CountDownLatch getInitedLatch() {
            return initedLatch;
        }

        public boolean isExiting() {
            return exiting;
        }

        public void run() {
            final ReentrantReadWriteLock lock = DruidDataSource.this.lock;

            // init connections
            try {
                if (initTask && initialSize > 0) {
                    while (!closing && !closed
                            && poolingCount.get() < initialSize) {
                        try {
                            PhysicalConnectionInfo pyConnectInfo = createPhysicalConnection();
                            try {
                                lock.writeLock().lockInterruptibly();
                            } catch (InterruptedException e) {
                                LOG.debug("{dataSource-" + DruidDataSource.this.getID() + "} interrupted.", e);
                                break;
                            }
                            try {
                                if (poolingCount.get() < initialSize) {
                                    DruidConnectionHolder holder = new DruidConnectionHolder(
                                            DruidDataSource.this,
                                            pyConnectInfo);
                                    // increase pollingCount priors to put operation.
                                    poolingCount.incrementAndGet();
                                    connections.put(new Object(), holder);
                                } else {
                                    JdbcUtils.close(pyConnectInfo.getPhysicalConnection());
                                }
                            } finally {
                                lock.writeLock().unlock();
                            }
                        } catch (SQLException ex) {
                            LOG.error("init datasource error, url: " + DruidDataSource.this.getUrl(), ex);
                            if (initExceptionThrow) {
                                break;
                            } else {
                                try {
                                    Thread.sleep(3000);
                                } catch (InterruptedException e) {
                                    LOG.debug("{dataSource-" + DruidDataSource.this.getID() + "} interrupted. ", e);
                                    break;
                                }
                            }
                        }
                    }

                    if (poolingCount.get() < initialSize) {
                        doExit(null);
                        return;
                    }

                    int count = poolingCount.get();
                    if (count > 0) {
                        poolingPeak.set(count);
                        poolingPeakTime = System.currentTimeMillis();
                    }
                    initTask = false;
                }
            } finally {
                initedLatch.countDown();
            }

            long lastDiscardCount = 0;
            int errorCount = 0;
            boolean emptyWait;
            int allCount;
            Long expiredTime;
            DruidConnectionRequest connReq = null;
            while (!closing && !closed) {
                long discardCount = DruidDataSource.this.discardCount.get();
                boolean discardChanged = discardCount - lastDiscardCount > 0;
                lastDiscardCount = discardCount;

                emptyWait = true;
                if (createError != null && poolingCount.get() == 0 && !discardChanged) {
                    emptyWait = false;
                }

                allCount = connections.size();
                if (emptyWait && keepAlive && allCount < minIdle) {
                    emptyWait = false;
                }

                if (emptyWait && isFailContinuous()) {
                    emptyWait = false;
                }

                if (emptyWait && connReq == null) {
                    try {
                        // LockSupport.parkNanos will be called by the poll method.
                        connReq = requestQueue.poll(emptyWaitTimes, TimeUnit.MILLISECONDS);
                    } catch (InterruptedException e) {
                        LOG.debug("{dataSource-" + DruidDataSource.this.getID() + "} interrupted. ", e);
                        break;
                    }
                }

                if (connReq != null
                        && (expiredTime = connReq.getExpiredTime()) != null
                        && expiredTime <= System.currentTimeMillis()) {
                    // request expired.
                    connReq = null;
                }

                if (connReq != null) {
                    Thread connReqThread = connReq.getThread();
                    if (connReqThread == this) {
                        // notice received.
                        connReq = null;
                    } else {
                        // connection request.
                        DruidConnectionHolder candidate = connections.get(connReqThread);
                        if (candidate == null) {
                            // search the first connection that could be used.
                            Object thread;
                            DruidConnectionHolder connection;
                            Map.Entry<Object, DruidConnectionHolder> entry;
                            Iterator<Map.Entry<Object, DruidConnectionHolder>> it = connections.entrySet().iterator();
                            it = connections.entrySet().iterator();
                            while (it.hasNext()) {
                                entry = it.next();
                                connection = entry.getValue();
                                if (connection.getActiveFlag().compareAndSet(IDLE, connReqThread)) {
                                    it.remove();
                                    candidate = connection;
                                    break;
                                } else {
                                    thread = entry.getKey();
                                    if (thread instanceof Thread && !((Thread) thread).isAlive()) {
                                        // take back the connection from dead thread.
                                        it.remove();
                                        candidate = connection;
                                        if (candidate.getActiveFlag().get() == ACTIVE) {
                                            if (candidate.active) {
                                                activeCount.decrementAndGet();
                                                candidate.active = false;
                                            }
                                            poolingCount.incrementAndGet();
                                        }
                                        candidate.getActiveFlag().set(connReqThread);
                                        break;
                                    }
                                }
                            }
                            if (candidate == null) {
                                emptyWait = false;
                            } else {
                                connections.put(connReqThread, candidate);
                                connReq.getDoneLatch().countDown();
                                connReq = null;
                            }
                        } else {
                            if (candidate.getActiveFlag().compareAndSet(IDLE, connReqThread)) {
                                // take back the connection which had been processed by shrink.
                                connReq.getDoneLatch().countDown();
                                connReq = null;
                            } else if (candidate.getActiveFlag().compareAndSet(ACTIVE, PROCESSING)) {
                                // take back the active connection.
                                if (candidate.active) {
                                    activeCount.decrementAndGet();
                                    candidate.active = false;
                                }

                                if (candidate.discard) {
                                    connections.remove(candidate);
                                    continue;
                                } else {
                                    poolingCount.incrementAndGet();

                                    // set the candidate connection active flag to connection request thread..
                                    candidate.getActiveFlag().set(connReqThread);
                                    connReq.getDoneLatch().countDown();
                                    connReq = null;
                                }
                            } else {
                                // busy waiting as active flag might be set to PROCESSING temporarily.
                                continue;
                            }
                        }
                    }
                }

                // when active and pool connections reach maxActive limit, or other must waiting conditions.
                if (emptyWait || allCount >= maxActive) {
                    if (connReq == null && System.currentTimeMillis() > nextDestroyTaskTime) {
                        try {
                            destroyTask();
                        } catch (Throwable t) {
                            LOG.warn("destroy task failure.", t);
                        }
                        nextDestroyTaskTime = System.currentTimeMillis() + emptyWaitTimes;
                    }
                    continue;
                }

                PhysicalConnectionInfo connection = null;
                try {
                    connection = createPhysicalConnection();
                } catch (SQLException | RuntimeException e) {
                    LOG.error("create connection Exception, url: " + sanitizedUrl(jdbcUrl)
                            + (e instanceof SQLException ? ", errorCode " + ((SQLException) e).getErrorCode()
                                    + ", state " + ((SQLException) e).getSQLState() : ""), e);

                    if (initTask) {
                        LOG.warn("initialize connection failure, the dataSource will be closed, please check configuration!", e);
                        break;
                    }

                    errorCount++;
                    if (errorCount > connectionErrorRetryAttempts && timeBetweenConnectErrorMillis > 0) {
                        // fail over retry attempts
                        setFailContinuous(true);

                        if (breakAfterAcquireFailure || closing || closed) {
                            break;
                        }

                        if (failFast) {
                            if (connReq != null) {
                                connReq.setStopping(true);
                                connReq.getDoneLatch().countDown();
                                connReq = null;
                            }
                            try {
                                setMpscQueueStoppingNotice();
                            } catch (InterruptedException ex) {
                                LOG.debug("{dataSource-" + DruidDataSource.this.getID() + "} interrupted. ", ex);
                                break;
                            }
                        }

                        try {
                            Thread.sleep(timeBetweenConnectErrorMillis);
                        } catch (InterruptedException interruptEx) {
                            LOG.debug("{dataSource-" + DruidDataSource.this.getID() + "} interrupted. ", interruptEx);
                            break;
                        }
                    }
                } catch (Error e) {
                    LOG.error("create connection Error", e);
                    break;
                }

                if (connection == null) {
                    continue;
                }
                initTask = false;

                boolean result = put(connection, connReq);
                if (!result) {
                    if (connReq != null) {
                        connReq.setStopping(true);
                        connReq.getDoneLatch().countDown();
                    }
                    JdbcUtils.close(connection.getPhysicalConnection());
                    LOG.debug("put physical connection to pool failed.");
                }
                connReq = null;

                // reset errorCount
                errorCount = 0;
            }

            doExit(connReq);
        }

        private void doExit(DruidConnectionRequest connReq) {
            exiting = true;
            // close data source priors to send stopping notice.
            if (!closing && !closed) {
                DruidDataSource.this.close();
            }
            if (connReq != null) {
                connReq.setStopping(true);
                connReq.getDoneLatch().countDown();
            }
            try {
                setMpscQueueStoppingNotice();
            } catch (InterruptedException e) {
                // do nothing.
            }
        }

        private void setMpscQueueStoppingNotice() throws InterruptedException {
            DruidConnectionRequest req;
            // poll at no waiting mode.
            while ((req = requestQueue.poll(0, TimeUnit.MILLISECONDS)) != null) {
                req.setStopping(true);
                req.getDoneLatch().countDown();
            }
        }

        private void destroyTask() {
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

        Iterator<DruidPooledConnection> iter = activeConnections.keySet().iterator();
        while (iter.hasNext()) {
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

        if (abandonedList.size() > 0) {
            for (DruidPooledConnection pooledConnection : abandonedList) {
                final ReentrantReadWriteLock lock = pooledConnection.lock;
                try {
                    lock.readLock().lockInterruptibly();
                } catch (InterruptedException e) {
                    LOG.debug("{dataSource-" + DruidDataSource.this.getID() + "} interrupted. ", e);
                    return removeCount;
                }
                try {
                    if (pooledConnection.isDisable()) {
                        continue;
                    }
                } finally {
                    lock.readLock().unlock();
                }

                JdbcUtils.close(pooledConnection);
                pooledConnection.abandond();
                removeAbandonedCount.incrementAndGet();
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
        if (poolingCount.get() == 0) {
            return;
        }

        if (checkTime && lastGetConnectionTime > System.currentTimeMillis() - Math.min(minEvictableIdleTimeMillis, 100)) {
            // do not shrink the pool if busy using。
            return;
        }

        boolean needFill = false;
        int evictCount = 0;
        int keepAliveCount = 0;
        int fatalErrorIncrement = 0;

        // keep lock for handleFatalError method.
        final ReentrantReadWriteLock lock = this.lock;
        try {
            lock.writeLock().lockInterruptibly();
        } catch (InterruptedException e) {
            return;
        }

        try {
            fatalErrorIncrement = fatalErrorCount - fatalErrorCountLastShrink;
            fatalErrorCountLastShrink = fatalErrorCount;
        } finally {
            lock.writeLock().unlock();
        }

        if (!inited) {
            return;
        }

        DruidConnectionHolder[] evictConnections = new DruidConnectionHolder[maxActive];
        DruidConnectionHolder[] keepAliveConnections = new DruidConnectionHolder[maxActive];

        final int checkCount = poolingCount.get() - minIdle;
        final long currentTimeMillis = System.currentTimeMillis();
        Iterator<Map.Entry<Object, DruidConnectionHolder>> it = connections.entrySet().iterator();
        Map.Entry<Object, DruidConnectionHolder> entry;
        DruidConnectionHolder connection;
        int i = -1;
        while (it.hasNext()) {
            i++;
            entry = it.next();
            connection = entry.getValue();
            if (connection.getActiveFlag().compareAndSet(IDLE, PROCESSING)) {
                try {
                    if ((onFatalError || fatalErrorIncrement > 0)
                            && (lastFatalErrorTimeMillis > connection.connectTimeMillis)) {
                        // remove the current priors to pollingCount decrement.
                        it.remove();
                        poolingCount.decrementAndGet();
                        keepAliveConnections[keepAliveCount++] = connection;
                        continue;
                    }

                    if (checkTime) {
                        if (phyTimeoutMillis > 0) {
                            long phyConnectTimeMillis = currentTimeMillis - connection.connectTimeMillis;
                            if (phyConnectTimeMillis > phyTimeoutMillis) {
                                // remove the current priors to pollingCount decrement.
                                it.remove();
                                poolingCount.decrementAndGet();
                                evictConnections[evictCount++] = connection;
                                continue;
                            }
                        }

                        long idleMillis = currentTimeMillis - connection.lastActiveTimeMillis;

                        if (idleMillis >= minEvictableIdleTimeMillis) {
                            if (i < checkCount) {
                                // remove the current priors to pollingCount decrement.
                                it.remove();
                                poolingCount.decrementAndGet();
                                evictConnections[evictCount++] = connection;
                                continue;
                            } else if (idleMillis > maxEvictableIdleTimeMillis) {
                                // remove the current priors to pollingCount decrement.
                                it.remove();
                                poolingCount.decrementAndGet();
                                evictConnections[evictCount++] = connection;
                                continue;
                            }
                        }

                        if (keepAlive && idleMillis >= keepAliveBetweenTimeMillis
                                && currentTimeMillis - connection.lastKeepTimeMillis >= keepAliveBetweenTimeMillis) {
                            // remove the current priors to pollingCount decrement.
                            it.remove();
                            poolingCount.decrementAndGet();
                            keepAliveConnections[keepAliveCount++] = connection;
                        }
                    } else {
                        if (i < checkCount) {
                            // remove the current priors to pollingCount decrement.
                            it.remove();
                            poolingCount.decrementAndGet();
                            evictConnections[evictCount++] = connection;
                        } else {
                            break;
                        }
                    }
                } finally {
                    connection.getActiveFlag().set(IDLE);
                }
            }
        }

        int currentVal = keepAliveCheckCount.get();
        while (!keepAliveCheckCount.compareAndSet(currentVal, currentVal + keepAliveCount)) {
            currentVal = keepAliveCheckCount.get();
        }

        if (keepAlive && connections.size() < minIdle) {
            needFill = true;
        }

        DruidConnectionHolder holder;
        Connection sqlConnection;
        if (evictCount > 0) {
            for (i = 0; i < evictCount; ++i) {
                holder = evictConnections[i];
                sqlConnection = holder.getConnection();
                JdbcUtils.close(sqlConnection);
                destroyCountUpdater.incrementAndGet(this);
            }
        }

        if (keepAliveCount > 0) {
            // keep order
            for (i = keepAliveCount - 1; i >= 0; --i) {
                holder = keepAliveConnections[i];
                sqlConnection = holder.getConnection();
                holder.incrementKeepAliveCheckCount();

                boolean validate = false;
                try {
                    this.validateConnection(sqlConnection);
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
                    boolean putOk = put(holder, null);
                    if (!putOk) {
                        discard = true;
                    }
                }

                if (discard) {
                    try {
                        sqlConnection.close();
                    } catch (Throwable error) {
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

                    holder.discard = true;
                    discardCount.incrementAndGet();

                    if (connections.size() < minIdle) {
                        needFill = true;
                    }
                }
            }
            this.getDataSourceStat().addKeepAliveCheckCount(keepAliveCount);
        }

        if (needFill || fatalErrorIncrement > 0) {
            tryNoticeCreateConnectionThread(requestQueue);
        }
    }

    public Throwable getDiscardErrorLast() {
        return discardErrorLast;
    }

    public Throwable getKeepAliveCheckErrorLast() {
        return keepAliveCheckErrorLast;
    }

    public long getNotEmptyWaitCount() {
        return notEmptyWaitCount.get();
    }

    public int getNotEmptyWaitThreadCount() {
        return notEmptyWaitThreadCount.get();
    }

    public int getNotEmptyWaitThreadPeak() {
        return notEmptyWaitThreadPeak.get();
    }

    public long getNotEmptyWaitMillis() {
        return notEmptyWaitNanos.get() / (1000 * 1000);
    }

    public long getNotEmptyWaitNanos() {
        return notEmptyWaitNanos.get();
    }

    public int getLockQueueLength() {
        return lock.getQueueLength();
    }

    public int getActivePeak() {
        return activePeak.get();
    }

    public Date getActivePeakTime() {
        if (activePeakTime <= 0) {
            return null;
        }

        return new Date(activePeakTime);
    }

    public String dump() {
        return this.toString();
    }

    public long getErrorCount() {
        return errorCountUpdater.get(this);
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();
        lock.writeLock().lock();
        try {
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
            DruidConnectionHolder conn;
            int i = 0;
            Iterator<Map.Entry<Object, DruidConnectionHolder>> it = connections.entrySet().iterator();
            while (it.hasNext()) {
                i++;
                conn = it.next().getValue();
                if (i != 1) {
                    buf.append(",");
                }
                buf.append("\n\t\t");
                buf.append(conn.toString());
            }
            buf.append("\n\t]");

            buf.append("\n}");

            if (this.isPoolPreparedStatements()) {
                buf.append("\n\n[");
                i = 0;
                it = connections.entrySet().iterator();
                while (it.hasNext()) {
                    i++;
                    conn = it.next().getValue();
                    if (i != 1) {
                        buf.append(",");
                    }
                    buf.append("\n\t{\n\tID:");
                    buf.append(System.identityHashCode(conn.getConnection()));
                    PreparedStatementPool pool = conn.getStatementPool();

                    buf.append(", \n\tpoolStatements:[");

                    int entryIndex = 0;
                    try {
                        for (Map.Entry<PreparedStatementKey, PreparedStatementHolder> entry
                                : pool.getMap().entrySet()) {
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
                buf.append("\n]");
            }
        } finally {
            lock.writeLock().unlock();
        }
        return buf.toString();
    }

    public List<Map<String, Object>> getPoolingConnectionInfo() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        lock.writeLock().lock();
        try {
            DruidConnectionHolder connHolder;
            Connection conn;
            Iterator<Map.Entry<Object, DruidConnectionHolder>> it = connections.entrySet().iterator();
            while (it.hasNext()) {
                connHolder = it.next().getValue();
                conn = connHolder.getConnection();

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
            lock.writeLock().unlock();
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

        lock.readLock().lock();
        try {
            poolingCount = this.poolingCount.get();
            poolingPeak = this.poolingPeak.get();
            poolingPeakTime = this.getPoolingPeakTime();

            activeCount = this.activeCount.get();
            activePeak = this.activePeak.get();
            activePeakTime = this.getActivePeakTime();

            connectCount = this.connectCount.get();
            closeCount = this.closeCount.get();
        } finally {
            lock.readLock().unlock();
        }
        Map<String, Object> dataMap = new LinkedHashMap<String, Object>();

        dataMap.put("Identity", System.identityHashCode(this));
        dataMap.put("Name", this.getName());
        dataMap.put("DbType", this.dbTypeName);
        dataMap.put("DriverClassName", this.getDriverClassName());

        dataMap.put("URL", this.getUrl());
        dataMap.put("UserName", this.getUsername());
        dataMap.put("FilterClassNames", this.getFilterClassNames());

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

    public ReentrantReadWriteLock getLock() {
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
        if (poolingCount.get() == 0) {
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
            boolean fillable = this.isFillable(toCount);
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

            boolean result = false;

            try {
                result = this.putLast(holder, System.currentTimeMillis(), true);
                fillCount++;
            } catch (InterruptedException e) {
                connectErrorCountUpdater.incrementAndGet(this);
                throw new SQLException("interrupt", e);
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
        lock.readLock().lock();
        try {
            int currentCount = connections.size();
            if (currentCount >= toCount || currentCount >= this.maxActive) {
                return false;
            } else {
                return true;
            }
        } finally {
            lock.readLock().unlock();
        }
    }

    public boolean isFull() {
        lock.readLock().lock();
        try {
            return connections.size() >= this.maxActive;
        } finally {
            lock.readLock().unlock();
        }
    }

    private boolean tryNoticeCreateConnectionThread(MpscBlockingConsumerArrayQueue<DruidConnectionRequest> queue) {
        DruidConnectionRequest req = new DruidConnectionRequest(createConnectionThread);
        return queue.offer(req);
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

    public void forEach(Connection conn) {
    }

    public static final class DruidConnectionRequest {
        private final Thread thread;
        private final CountDownLatch doneLatch = new CountDownLatch(1);
        private DruidConnectionHolder druidConnectionHolder;
        private Long expiredTime;
        private boolean stopping;

        public DruidConnectionRequest(Thread thread) {
            this.thread = thread;
        }

        public Thread getThread() {
            return thread;
        }

        public CountDownLatch getDoneLatch() {
            return doneLatch;
        }

        public DruidConnectionHolder getDruidConnectionHolder() {
            return druidConnectionHolder;
        }

        public void setDruidConnectionHolder(DruidConnectionHolder druidConnectionHolder) {
            this.druidConnectionHolder = druidConnectionHolder;
        }

        public Long getExpiredTime() {
            return expiredTime;
        }

        public void setExpiredTime(Long expiredTime) {
            this.expiredTime = expiredTime;
        }

        public boolean isStopping() {
            return stopping;
        }

        public void setStopping(boolean stopping) {
            this.stopping = stopping;
        }
    }

    @Override
    public Set<DruidPooledConnection> getActiveConnections() {
         return new HashSet<>(activeConnections.keySet());
    }
}
