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

import java.io.PrintWriter;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicIntegerFieldUpdater;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicLongFieldUpdater;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import javax.management.JMException;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.sql.DataSource;

import com.alibaba.druid.DruidRuntimeException;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.filter.FilterManager;
import com.alibaba.druid.pool.vendor.NullExceptionSorter;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.TransactionInfo;
import com.alibaba.druid.stat.JdbcDataSourceStat;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.DruidPasswordCallback;
import com.alibaba.druid.util.Histogram;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.MySqlUtils;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.druid.util.Utils;

/**
 * @author wenshao [szujobs@hotmail.com]
 * @author ljw [ljw2083@alibaba-inc.com]
 */
public abstract class DruidAbstractDataSource extends WrapperAdapter implements DruidAbstractDataSourceMBean, DataSource, DataSourceProxy, Serializable {
    private static final long                          serialVersionUID                          = 1L;
    private final static Log                           LOG                                       = LogFactory.getLog(DruidAbstractDataSource.class);

    public final static int                            DEFAULT_INITIAL_SIZE                      = 0;
    public final static int                            DEFAULT_MAX_ACTIVE_SIZE                   = 8;
    public final static int                            DEFAULT_MAX_IDLE                          = 8;
    public final static int                            DEFAULT_MIN_IDLE                          = 0;
    public final static int                            DEFAULT_MAX_WAIT                          = -1;
    public final static String                         DEFAULT_VALIDATION_QUERY                  = null;                                                //
    public final static boolean                        DEFAULT_TEST_ON_BORROW                    = false;
    public final static boolean                        DEFAULT_TEST_ON_RETURN                    = false;
    public final static boolean                        DEFAULT_WHILE_IDLE                        = true;
    public static final long                           DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = 60 * 1000L;
    public static final long                           DEFAULT_TIME_BETWEEN_CONNECT_ERROR_MILLIS = 500;
    public static final int                            DEFAULT_NUM_TESTS_PER_EVICTION_RUN        = 3;

    public static final long                           DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS    = 1000L * 60L * 30L;
    public static final long                           DEFAULT_MAX_EVICTABLE_IDLE_TIME_MILLIS    = 1000L * 60L * 60L * 7;
    public static final long                           DEFAULT_PHY_TIMEOUT_MILLIS                = -1;

    protected volatile boolean                         defaultAutoCommit                         = true;
    protected volatile Boolean                         defaultReadOnly;
    protected volatile Integer                         defaultTransactionIsolation;
    protected volatile String                          defaultCatalog                            = null;

    protected String                                   name;

    protected volatile String                          username;
    protected volatile String                          password;
    protected volatile String                          jdbcUrl;
    protected volatile String                          driverClass;
    protected volatile ClassLoader                     driverClassLoader;
    protected volatile Properties                      connectProperties                         = new Properties();

    protected volatile PasswordCallback                passwordCallback;
    protected volatile NameCallback                    userCallback;

    protected volatile int                             initialSize                               = DEFAULT_INITIAL_SIZE;
    protected volatile int                             maxActive                                 = DEFAULT_MAX_ACTIVE_SIZE;
    protected volatile int                             minIdle                                   = DEFAULT_MIN_IDLE;
    protected volatile int                             maxIdle                                   = DEFAULT_MAX_IDLE;
    protected volatile long                            maxWait                                   = DEFAULT_MAX_WAIT;
    protected int                                      notFullTimeoutRetryCount                  = 0;

    protected volatile String                          validationQuery                           = DEFAULT_VALIDATION_QUERY;
    protected volatile int                             validationQueryTimeout                    = -1;
    protected volatile boolean                         testOnBorrow                              = DEFAULT_TEST_ON_BORROW;
    protected volatile boolean                         testOnReturn                              = DEFAULT_TEST_ON_RETURN;
    protected volatile boolean                         testWhileIdle                             = DEFAULT_WHILE_IDLE;
    protected volatile boolean                         poolPreparedStatements                    = false;
    protected volatile boolean                         sharePreparedStatements                   = false;
    protected volatile int                             maxPoolPreparedStatementPerConnectionSize = 10;

    protected volatile boolean                         inited                                    = false;
    protected volatile boolean                         initExceptionThrow                        = true;

    protected PrintWriter                              logWriter                                 = new PrintWriter(System.out);

    protected List<Filter>                             filters                                   = new CopyOnWriteArrayList<Filter>();
    private boolean                                    clearFiltersEnable                        = true;
    protected volatile ExceptionSorter                 exceptionSorter                           = null;

    protected Driver                                   driver;

    protected volatile int                             queryTimeout;
    protected volatile int                             transactionQueryTimeout;

    protected long                                     createTimespan;

    protected volatile int                             maxWaitThreadCount                        = -1;
    protected volatile boolean                         accessToUnderlyingConnectionAllowed       = true;

    protected volatile long                            timeBetweenEvictionRunsMillis             = DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;
    protected volatile int                             numTestsPerEvictionRun                    = DEFAULT_NUM_TESTS_PER_EVICTION_RUN;
    protected volatile long                            minEvictableIdleTimeMillis                = DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;
    protected volatile long                            maxEvictableIdleTimeMillis                = DEFAULT_MAX_EVICTABLE_IDLE_TIME_MILLIS;
    protected volatile long                            keepAliveBetweenTimeMillis                = DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS * 2;
    protected volatile long                            phyTimeoutMillis                          = DEFAULT_PHY_TIMEOUT_MILLIS;
    protected volatile long                            phyMaxUseCount                            = -1;

    protected volatile boolean                         removeAbandoned;
    protected volatile long                            removeAbandonedTimeoutMillis              = 300 * 1000;
    protected volatile boolean                         logAbandoned;

    protected volatile int                             maxOpenPreparedStatements                 = -1;

    protected volatile List<String>                    connectionInitSqls;

    protected volatile String                          dbType;

    protected volatile long                            timeBetweenConnectErrorMillis             = DEFAULT_TIME_BETWEEN_CONNECT_ERROR_MILLIS;

    protected volatile ValidConnectionChecker          validConnectionChecker                    = null;

    protected final Map<DruidPooledConnection, Object> activeConnections                         = new IdentityHashMap<DruidPooledConnection, Object>();
    protected final static Object                      PRESENT                                   = new Object();

    protected long                                     id;

    protected int                                      connectionErrorRetryAttempts              = 1;
    protected boolean                                  breakAfterAcquireFailure                  = false;
    protected long                                     transactionThresholdMillis                = 0L;

    protected final Date                               createdTime                               = new Date();
    protected Date                                     initedTime;
    protected volatile long                            errorCount                                = 0L;
    protected volatile long                            dupCloseCount                             = 0L;
    protected volatile long                            startTransactionCount                     = 0L;
    protected volatile long                            commitCount                               = 0L;
    protected volatile long                            rollbackCount                             = 0L;
    protected volatile long                            cachedPreparedStatementHitCount           = 0L;
    protected volatile long                            preparedStatementCount                    = 0L;
    protected volatile long                            closedPreparedStatementCount              = 0L;
    protected volatile long                            cachedPreparedStatementCount              = 0L;
    protected volatile long                            cachedPreparedStatementDeleteCount        = 0L;
    protected volatile long                            cachedPreparedStatementMissCount          = 0L;

    final static AtomicLongFieldUpdater<DruidAbstractDataSource> errorCountUpdater                         = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "errorCount");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource> dupCloseCountUpdater                      = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "dupCloseCount");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource> startTransactionCountUpdater              = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "startTransactionCount");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource> commitCountUpdater                        = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "commitCount");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource> rollbackCountUpdater                      = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "rollbackCount");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource> cachedPreparedStatementHitCountUpdater    = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "cachedPreparedStatementHitCount");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource> preparedStatementCountUpdater             = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "preparedStatementCount");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource> closedPreparedStatementCountUpdater       = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "closedPreparedStatementCount");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource> cachedPreparedStatementCountUpdater       = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "cachedPreparedStatementCount");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource> cachedPreparedStatementDeleteCountUpdater = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "cachedPreparedStatementDeleteCount");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource> cachedPreparedStatementMissCountUpdater   = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "cachedPreparedStatementMissCount");


    protected final Histogram                          transactionHistogram                      = new Histogram(1,
                                                                                                                 10,
                                                                                                                 100,
                                                                                                                 1000,
                                                                                                                 10 * 1000,
                                                                                                                 100 * 1000);

    private boolean                                    dupCloseLogEnable                         = false;

    private ObjectName                                 objectName;

    protected volatile long                            executeCount                              = 0L;
    protected volatile long                            executeQueryCount                         = 0L;
    protected volatile long                            executeUpdateCount                        = 0L;
    protected volatile long                            executeBatchCount                         = 0L;

    final static AtomicLongFieldUpdater<DruidAbstractDataSource> executeQueryCountUpdater = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "executeQueryCount");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource> executeUpdateCountUpdater = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "executeUpdateCount");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource> executeBatchCountUpdater = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "executeBatchCount");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource> executeCountUpdater = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "executeCount");

    protected volatile Throwable                       createError;
    protected volatile Throwable                       lastError;
    protected volatile long                            lastErrorTimeMillis;
    protected volatile Throwable                       lastCreateError;
    protected volatile long                            lastCreateErrorTimeMillis;
    protected volatile long                            lastCreateStartTimeMillis;

    protected boolean                                  isOracle                                  = false;
    protected boolean                                  isMySql                                   = false;
    protected boolean                                  useOracleImplicitCache                    = true;

    protected ReentrantLock                            lock;
    protected Condition                                notEmpty;
    protected Condition                                empty;

    protected ReentrantLock                            activeConnectionLock                      = new ReentrantLock();

    protected volatile int                             createErrorCount                          = 0;
    protected volatile int                             creatingCount                             = 0;
    protected volatile int                             directCreateCount                         = 0;
    protected volatile long                            createCount                               = 0L;
    protected volatile long                            destroyCount                              = 0L;
    protected volatile long                            createStartNanos                          = 0L;

    final static AtomicIntegerFieldUpdater<DruidAbstractDataSource> createErrorCountUpdater      = AtomicIntegerFieldUpdater.newUpdater(DruidAbstractDataSource.class, "createErrorCount");
    final static AtomicIntegerFieldUpdater<DruidAbstractDataSource> creatingCountUpdater         = AtomicIntegerFieldUpdater.newUpdater(DruidAbstractDataSource.class, "creatingCount");
    final static AtomicIntegerFieldUpdater<DruidAbstractDataSource> directCreateCountUpdater     = AtomicIntegerFieldUpdater.newUpdater(DruidAbstractDataSource.class, "directCreateCount");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource>    createCountUpdater           = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "createCount");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource>    destroyCountUpdater          = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "destroyCount");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource> createStartNanosUpdater         = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "createStartNanos");

    private Boolean                                    useUnfairLock                             = null;
    private boolean                                    useLocalSessionState                      = true;

    protected long                                     timeBetweenLogStatsMillis;
    protected DruidDataSourceStatLogger                statLogger                                = new DruidDataSourceStatLoggerImpl();
    
    private boolean                                    asyncCloseConnectionEnable                = false;
    protected int                                      maxCreateTaskCount                        = 3;
    protected boolean                                  failFast                                  = false;
    protected volatile int                             failContinuous                            = 0;
    protected volatile long                            failContinuousTimeMillis                  = 0L;
    protected ScheduledExecutorService                 destroyScheduler;
    protected ScheduledExecutorService                 createScheduler;

    final static AtomicLongFieldUpdater<DruidAbstractDataSource> failContinuousTimeMillisUpdater = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "failContinuousTimeMillis");
    final static AtomicIntegerFieldUpdater<DruidAbstractDataSource> failContinuousUpdater        = AtomicIntegerFieldUpdater.newUpdater(DruidAbstractDataSource.class, "failContinuous");

    protected boolean                                  initVariants                              = false;
    protected boolean                                  initGlobalVariants                        = false;
    protected volatile boolean                         onFatalError                              = false;
    protected volatile int                             onFatalErrorMaxActive                     = 0;
    protected volatile int                             fatalErrorCount                           = 0;
    protected volatile int                             fatalErrorCountLastShrink                 = 0;
    protected volatile long                            lastFatalErrorTimeMillis                  = 0;
    protected volatile String                          lastFatalErrorSql                         = null;
    protected volatile Throwable                       lastFatalError                            = null;

    public DruidAbstractDataSource(boolean lockFair){
        lock = new ReentrantLock(lockFair);

        notEmpty = lock.newCondition();
        empty = lock.newCondition();
    }

    public boolean isUseLocalSessionState() {
        return useLocalSessionState;
    }

    public void setUseLocalSessionState(boolean useLocalSessionState) {
        this.useLocalSessionState = useLocalSessionState;
    }

    public DruidDataSourceStatLogger getStatLogger() {
        return statLogger;
    }

    public void setStatLoggerClassName(String className) {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
            DruidDataSourceStatLogger statLogger = (DruidDataSourceStatLogger) clazz.newInstance();
            this.setStatLogger(statLogger);
        } catch (Exception e) {
            throw new IllegalArgumentException(className, e);
        }
    }

    public void setStatLogger(DruidDataSourceStatLogger statLogger) {
        this.statLogger = statLogger;
    }

    public long getTimeBetweenLogStatsMillis() {
        return timeBetweenLogStatsMillis;
    }

    public void setTimeBetweenLogStatsMillis(long timeBetweenLogStatsMillis) {
        this.timeBetweenLogStatsMillis = timeBetweenLogStatsMillis;
    }

    public boolean isOracle() {
        return isOracle;
    }

    public void setOracle(boolean isOracle) {
        if (inited) {
            throw new IllegalStateException();
        }
        this.isOracle = isOracle;
    }

    public boolean isUseUnfairLock() {
        return !lock.isFair();
    }

    public void setUseUnfairLock(boolean useUnfairLock) {
        if (lock.isFair() == !useUnfairLock && this.useUnfairLock != null) {
            return;
        }

        if (!this.inited) {
            final ReentrantLock lock = this.lock;
            lock.lock();
            try {
                if (!this.inited) {
                    this.lock = new ReentrantLock(!useUnfairLock);
                    this.notEmpty = this.lock.newCondition();
                    this.empty = this.lock.newCondition();

                    this.useUnfairLock = useUnfairLock;
                }
            } finally {
                lock.unlock();
            }
        }
    }

    public boolean isUseOracleImplicitCache() {
        return useOracleImplicitCache;
    }

    public void setUseOracleImplicitCache(boolean useOracleImplicitCache) {
        if (this.useOracleImplicitCache != useOracleImplicitCache) {
            this.useOracleImplicitCache = useOracleImplicitCache;
            boolean isOracleDriver10 = isOracle() && this.driver != null && this.driver.getMajorVersion() == 10;

            if (isOracleDriver10 && useOracleImplicitCache) {
                this.getConnectProperties().setProperty("oracle.jdbc.FreeMemoryOnEnterImplicitCache", "true");
            } else {
                this.getConnectProperties().remove("oracle.jdbc.FreeMemoryOnEnterImplicitCache");
            }
        }
    }

    public Throwable getLastCreateError() {
        return lastCreateError;
    }

    public Throwable getLastError() {
        return this.lastError;
    }

    public long getLastErrorTimeMillis() {
        return lastErrorTimeMillis;
    }

    public Date getLastErrorTime() {
        if (lastErrorTimeMillis <= 0) {
            return null;
        }

        return new Date(lastErrorTimeMillis);
    }

    public long getLastCreateErrorTimeMillis() {
        return lastCreateErrorTimeMillis;
    }

    public Date getLastCreateErrorTime() {
        if (lastCreateErrorTimeMillis <= 0) {
            return null;
        }

        return new Date(lastCreateErrorTimeMillis);
    }

    public int getTransactionQueryTimeout() {
        if (transactionQueryTimeout <= 0) {
            return queryTimeout;
        }

        return transactionQueryTimeout;
    }

    public void setTransactionQueryTimeout(int transactionQueryTimeout) {
        this.transactionQueryTimeout = transactionQueryTimeout;
    }

    public long getExecuteCount() {
        return executeCount + executeQueryCount + executeUpdateCount + executeBatchCount;
    }

    public long getExecuteUpdateCount() {
        return executeUpdateCount;
    }

    public long getExecuteQueryCount() {
        return executeQueryCount;
    }

    public long getExecuteBatchCount() {
        return executeBatchCount;
    }

    public long getAndResetExecuteCount() {
        return executeCountUpdater.getAndSet(this, 0)
                + executeQueryCountUpdater.getAndSet(this, 0)
                + executeUpdateCountUpdater.getAndSet(this, 0)
                + executeBatchCountUpdater.getAndSet(this, 0);
    }

    public long getExecuteCount2() {
        return executeCount;
    }

    public void incrementExecuteCount() {
        this.executeCountUpdater.incrementAndGet(this);
    }

    public void incrementExecuteUpdateCount() {
        this.executeUpdateCount++;
    }

    public void incrementExecuteQueryCount() {
        this.executeQueryCount++;
    }

    public void incrementExecuteBatchCount() {
        this.executeBatchCount++;
    }

    public boolean isDupCloseLogEnable() {
        return dupCloseLogEnable;
    }

    public void setDupCloseLogEnable(boolean dupCloseLogEnable) {
        this.dupCloseLogEnable = dupCloseLogEnable;
    }

    public ObjectName getObjectName() {
        return objectName;
    }

    public void setObjectName(ObjectName objectName) {
        this.objectName = objectName;
    }

    public Histogram getTransactionHistogram() {
        return transactionHistogram;
    }

    public void incrementCachedPreparedStatementCount() {
        cachedPreparedStatementCountUpdater.incrementAndGet(this);
    }

    public void decrementCachedPreparedStatementCount() {
        cachedPreparedStatementCountUpdater.decrementAndGet(this);
    }

    public void incrementCachedPreparedStatementDeleteCount() {
        cachedPreparedStatementDeleteCountUpdater.incrementAndGet(this);
    }

    public void incrementCachedPreparedStatementMissCount() {
        cachedPreparedStatementMissCountUpdater.incrementAndGet(this);
    }

    public long getCachedPreparedStatementMissCount() {
        return cachedPreparedStatementMissCount;
    }

    public long getCachedPreparedStatementAccessCount() {
        return cachedPreparedStatementMissCount + cachedPreparedStatementHitCount;
    }

    public long getCachedPreparedStatementDeleteCount() {
        return cachedPreparedStatementDeleteCount;
    }

    public long getCachedPreparedStatementCount() {
        return cachedPreparedStatementCount;
    }

    public void incrementClosedPreparedStatementCount() {
        closedPreparedStatementCountUpdater.incrementAndGet(this);
    }

    public long getClosedPreparedStatementCount() {
        return closedPreparedStatementCount;
    }

    public void incrementPreparedStatementCount() {
        preparedStatementCountUpdater.incrementAndGet(this);
    }

    public long getPreparedStatementCount() {
        return preparedStatementCount;
    }

    public void incrementCachedPreparedStatementHitCount() {
        cachedPreparedStatementHitCountUpdater.incrementAndGet(this);
    }

    public long getCachedPreparedStatementHitCount() {
        return cachedPreparedStatementHitCount;
    }

    public long getTransactionThresholdMillis() {
        return transactionThresholdMillis;
    }

    public void setTransactionThresholdMillis(long transactionThresholdMillis) {
        this.transactionThresholdMillis = transactionThresholdMillis;
    }

    public abstract void logTransaction(TransactionInfo info);

    public long[] getTransactionHistogramValues() {
        return transactionHistogram.toArray();
    }

    public long[] getTransactionHistogramRanges() {
        return transactionHistogram.getRanges();
    }

    public long getCommitCount() {
        return commitCount;
    }

    public void incrementCommitCount() {
        commitCountUpdater.incrementAndGet(this);
    }

    public long getRollbackCount() {
        return rollbackCount;
    }

    public void incrementRollbackCount() {
        rollbackCountUpdater.incrementAndGet(this);
    }

    public long getStartTransactionCount() {
        return startTransactionCount;
    }

    public void incrementStartTransactionCount() {
        startTransactionCountUpdater.incrementAndGet(this);
    }

    public boolean isBreakAfterAcquireFailure() {
        return breakAfterAcquireFailure;
    }

    public void setBreakAfterAcquireFailure(boolean breakAfterAcquireFailure) {
        this.breakAfterAcquireFailure = breakAfterAcquireFailure;
    }

    public int getConnectionErrorRetryAttempts() {
        return connectionErrorRetryAttempts;
    }

    public void setConnectionErrorRetryAttempts(int connectionErrorRetryAttempts) {
        this.connectionErrorRetryAttempts = connectionErrorRetryAttempts;
    }

    public long getDupCloseCount() {
        return dupCloseCount;
    }

    public int getMaxPoolPreparedStatementPerConnectionSize() {
        return maxPoolPreparedStatementPerConnectionSize;
    }

    public void setMaxPoolPreparedStatementPerConnectionSize(int maxPoolPreparedStatementPerConnectionSize) {
        if (maxPoolPreparedStatementPerConnectionSize > 0) {
            this.poolPreparedStatements = true;
        } else {
            this.poolPreparedStatements = false;
        }

        this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
    }

    public boolean isSharePreparedStatements() {
        return sharePreparedStatements;
    }

    public void setSharePreparedStatements(boolean sharePreparedStatements) {
        this.sharePreparedStatements = sharePreparedStatements;
    }

    public void incrementDupCloseCount() {
        dupCloseCountUpdater.incrementAndGet(this);
    }

    public ValidConnectionChecker getValidConnectionChecker() {
        return validConnectionChecker;
    }

    public void setValidConnectionChecker(ValidConnectionChecker validConnectionChecker) {
        this.validConnectionChecker = validConnectionChecker;
    }

    public String getValidConnectionCheckerClassName() {
        if (validConnectionChecker == null) {
            return null;
        }

        return validConnectionChecker.getClass().getName();
    }

    public void setValidConnectionCheckerClassName(String validConnectionCheckerClass) throws Exception {
        Class<?> clazz = Utils.loadClass(validConnectionCheckerClass);
        ValidConnectionChecker validConnectionChecker = null;
        if (clazz != null) {
            validConnectionChecker = (ValidConnectionChecker) clazz.newInstance();
            this.validConnectionChecker = validConnectionChecker;
        } else {
            LOG.error("load validConnectionCheckerClass error : " + validConnectionCheckerClass);
        }
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public void addConnectionProperty(String name, String value) {
        if (StringUtils.equals(connectProperties.getProperty(name), value)) {
            return;
        }

        if (inited) {
            throw new UnsupportedOperationException();
        }

        connectProperties.put(name, value);
    }

    public Collection<String> getConnectionInitSqls() {
        Collection<String> result = connectionInitSqls;
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    public void setConnectionInitSqls(Collection<? extends Object> connectionInitSqls) {
        if ((connectionInitSqls != null) && (connectionInitSqls.size() > 0)) {
            ArrayList<String> newVal = null;
            for (Object o : connectionInitSqls) {
                if (o == null) {
                    continue;
                }

                String s = o.toString();
                s = s.trim();
                if (s.length() == 0) {
                    continue;
                }

                if (newVal == null) {
                    newVal = new ArrayList<String>();
                }
                newVal.add(s);
            }
            this.connectionInitSqls = newVal;
        } else {
            this.connectionInitSqls = null;
        }
    }

    public long getTimeBetweenConnectErrorMillis() {
        return timeBetweenConnectErrorMillis;
    }

    public void setTimeBetweenConnectErrorMillis(long timeBetweenConnectErrorMillis) {
        this.timeBetweenConnectErrorMillis = timeBetweenConnectErrorMillis;
    }

    public int getMaxOpenPreparedStatements() {
        return maxPoolPreparedStatementPerConnectionSize;
    }

    public void setMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
        this.setMaxPoolPreparedStatementPerConnectionSize(maxOpenPreparedStatements);
    }

    public boolean isLogAbandoned() {
        return logAbandoned;
    }

    public void setLogAbandoned(boolean logAbandoned) {
        this.logAbandoned = logAbandoned;
    }

    public int getRemoveAbandonedTimeout() {
        return (int) (removeAbandonedTimeoutMillis / 1000);
    }

    public void setRemoveAbandonedTimeout(int removeAbandonedTimeout) {
        this.removeAbandonedTimeoutMillis = (long) removeAbandonedTimeout * 1000;
    }

    public void setRemoveAbandonedTimeoutMillis(long removeAbandonedTimeoutMillis) {
        this.removeAbandonedTimeoutMillis = removeAbandonedTimeoutMillis;
    }

    public long getRemoveAbandonedTimeoutMillis() {
        return removeAbandonedTimeoutMillis;
    }

    public boolean isRemoveAbandoned() {
        return removeAbandoned;
    }

    public void setRemoveAbandoned(boolean removeAbandoned) {
        this.removeAbandoned = removeAbandoned;
    }

    public long getMinEvictableIdleTimeMillis() {
        return minEvictableIdleTimeMillis;
    }

    public void setMinEvictableIdleTimeMillis(long minEvictableIdleTimeMillis) {
        if (minEvictableIdleTimeMillis < 1000 * 30) {
            LOG.error("minEvictableIdleTimeMillis should be greater than 30000");
        }
        
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
    }

    public long getKeepAliveBetweenTimeMillis() {
        return keepAliveBetweenTimeMillis;
    }

    public void setKeepAliveBetweenTimeMillis(long keepAliveBetweenTimeMillis) {
        if (keepAliveBetweenTimeMillis < 1000 * 30) {
            LOG.error("keepAliveBetweenTimeMillis should be greater than 30000");
        }

        this.keepAliveBetweenTimeMillis = keepAliveBetweenTimeMillis;
    }

    public long getMaxEvictableIdleTimeMillis() {
        return maxEvictableIdleTimeMillis;
    }
    

    public void setMaxEvictableIdleTimeMillis(long maxEvictableIdleTimeMillis) {
        if (maxEvictableIdleTimeMillis < 1000 * 30) {
            LOG.error("maxEvictableIdleTimeMillis should be greater than 30000");
        }
        
        if (inited && maxEvictableIdleTimeMillis < minEvictableIdleTimeMillis) {
            throw new IllegalArgumentException("maxEvictableIdleTimeMillis must be grater than minEvictableIdleTimeMillis");
        }
        
        this.maxEvictableIdleTimeMillis = maxEvictableIdleTimeMillis;
    }

    public long getPhyTimeoutMillis() {
        return phyTimeoutMillis;
    }

    public void setPhyTimeoutMillis(long phyTimeoutMillis) {
        this.phyTimeoutMillis = phyTimeoutMillis;
    }

    public long getPhyMaxUseCount() {
        return phyMaxUseCount;
    }

    public void setPhyMaxUseCount(long phyMaxUseCount) {
        this.phyMaxUseCount = phyMaxUseCount;
    }

    public int getNumTestsPerEvictionRun() {
        return numTestsPerEvictionRun;
    }

    /**
     * @param numTestsPerEvictionRun
     */
    @Deprecated
    public void setNumTestsPerEvictionRun(int numTestsPerEvictionRun) {
        this.numTestsPerEvictionRun = numTestsPerEvictionRun;
    }

    public long getTimeBetweenEvictionRunsMillis() {
        return timeBetweenEvictionRunsMillis;
    }

    public void setTimeBetweenEvictionRunsMillis(long timeBetweenEvictionRunsMillis) {
        this.timeBetweenEvictionRunsMillis = timeBetweenEvictionRunsMillis;
    }

    public int getMaxWaitThreadCount() {
        return maxWaitThreadCount;
    }

    public void setMaxWaitThreadCount(int maxWaithThreadCount) {
        this.maxWaitThreadCount = maxWaithThreadCount;
    }

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
    }

    public int getValidationQueryTimeout() {
        return validationQueryTimeout;
    }

    public void setValidationQueryTimeout(int validationQueryTimeout) {
        if (validationQueryTimeout < 0 && JdbcConstants.SQL_SERVER.equals(dbType)) {
            LOG.error("validationQueryTimeout should be >= 0");
        }
        this.validationQueryTimeout = validationQueryTimeout;
    }

    public boolean isAccessToUnderlyingConnectionAllowed() {
        return accessToUnderlyingConnectionAllowed;
    }

    public void setAccessToUnderlyingConnectionAllowed(boolean accessToUnderlyingConnectionAllowed) {
        this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
    }

    public boolean isTestOnBorrow() {
        return testOnBorrow;
    }

    public void setTestOnBorrow(boolean testOnBorrow) {
        this.testOnBorrow = testOnBorrow;
    }

    public boolean isTestOnReturn() {
        return testOnReturn;
    }

    public void setTestOnReturn(boolean testOnReturn) {
        this.testOnReturn = testOnReturn;
    }

    public boolean isTestWhileIdle() {
        return testWhileIdle;
    }

    public void setTestWhileIdle(boolean testWhileIdle) {
        this.testWhileIdle = testWhileIdle;
    }

    public boolean isDefaultAutoCommit() {
        return defaultAutoCommit;
    }

    public void setDefaultAutoCommit(boolean defaultAutoCommit) {
        this.defaultAutoCommit = defaultAutoCommit;
    }

    public Boolean getDefaultReadOnly() {
        return defaultReadOnly;
    }

    public void setDefaultReadOnly(Boolean defaultReadOnly) {
        this.defaultReadOnly = defaultReadOnly;
    }

    public Integer getDefaultTransactionIsolation() {
        return defaultTransactionIsolation;
    }

    public void setDefaultTransactionIsolation(Integer defaultTransactionIsolation) {
        this.defaultTransactionIsolation = defaultTransactionIsolation;
    }

    public String getDefaultCatalog() {
        return defaultCatalog;
    }

    public void setDefaultCatalog(String defaultCatalog) {
        this.defaultCatalog = defaultCatalog;
    }

    public PasswordCallback getPasswordCallback() {
        return passwordCallback;
    }

    public void setPasswordCallback(PasswordCallback passwordCallback) {
        this.passwordCallback = passwordCallback;
    }

    public void setPasswordCallbackClassName(String passwordCallbackClassName) throws Exception {
        Class<?> clazz = Utils.loadClass(passwordCallbackClassName);
        if (clazz != null) {
            this.passwordCallback = (PasswordCallback) clazz.newInstance();
        } else {
            LOG.error("load passwordCallback error : " + passwordCallbackClassName);
            this.passwordCallback = null;
        }
    }

    public NameCallback getUserCallback() {
        return userCallback;
    }

    public void setUserCallback(NameCallback userCallback) {
        this.userCallback = userCallback;
    }

    public boolean isInitVariants() {
        return initVariants;
    }

    public void setInitVariants(boolean initVariants) {
        this.initVariants = initVariants;
    }

    public boolean isInitGlobalVariants() {
        return initGlobalVariants;
    }

    public void setInitGlobalVariants(boolean initGlobalVariants) {
        this.initGlobalVariants = initGlobalVariants;
    }

    /**
     * Retrieves the number of seconds the driver will wait for a <code>Statement</code> object to execute. If the limit
     * is exceeded, a <code>SQLException</code> is thrown.
     * 
     * @return the current query timeout limit in seconds; zero means there is no limit
     * <code>Statement</code>
     * @see #setQueryTimeout
     */
    public int getQueryTimeout() {
        return queryTimeout;
    }

    /**
     * Sets the number of seconds the driver will wait for a <code>Statement</code> object to execute to the given
     * number of seconds. If the limit is exceeded, an <code>SQLException</code> is thrown. A JDBC driver must apply
     * this limit to the <code>execute</code>, <code>executeQuery</code> and <code>executeUpdate</code> methods. JDBC
     * driver implementations may also apply this limit to <code>ResultSet</code> methods (consult your driver vendor
     * documentation for details).
     * 
     * @param seconds the new query timeout limit in seconds; zero means there is no limit
     * @see #getQueryTimeout
     */
    public void setQueryTimeout(int seconds) {
        this.queryTimeout = seconds;
    }

    public String getName() {
        if (name != null) {
            return name;
        }
        return "DataSource-" + System.identityHashCode(this);
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public abstract void setPoolPreparedStatements(boolean value);

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWaitMillis) {
        if (maxWaitMillis == this.maxWait) {
            return;
        }

        if (maxWaitMillis > 0 && useUnfairLock == null && !this.inited) {
            final ReentrantLock lock = this.lock;
            lock.lock();
            try {
                if ((!this.inited) && (!lock.isFair())) {
                    this.lock = new ReentrantLock(true);
                    this.notEmpty = this.lock.newCondition();
                    this.empty = this.lock.newCondition();
                }
            } finally {
                lock.unlock();
            }
        }

        if (inited) {
            LOG.error("maxWait changed : " + this.maxWait + " -> " + maxWaitMillis);
        }

        this.maxWait = maxWaitMillis;
    }
    
    public int getNotFullTimeoutRetryCount() {
        return notFullTimeoutRetryCount;
    }

    
    public void setNotFullTimeoutRetryCount(int notFullTimeoutRetryCount) {
        this.notFullTimeoutRetryCount = notFullTimeoutRetryCount;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int value) {
        if (value == this.minIdle) {
            return;
        }

        if (inited && value > this.maxActive) {
            throw new IllegalArgumentException("minIdle greater than maxActive, " + maxActive + " < " + this.minIdle);
        }
        
        if (minIdle < 0) {
            throw new IllegalArgumentException("minIdle must > 0");
        }

        this.minIdle = value;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    @Deprecated
    public void setMaxIdle(int maxIdle) {
        LOG.error("maxIdle is deprecated");

        this.maxIdle = maxIdle;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
        if (this.initialSize == initialSize) {
            return;
        }

        if (inited) {
            throw new UnsupportedOperationException();
        }

        this.initialSize = initialSize;
    }

    public long getCreateErrorCount() {
        return createErrorCount;
    }

    public int getMaxActive() {
        return maxActive;
    }

    public abstract void setMaxActive(int maxActive);

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        if (StringUtils.equals(this.username, username)) {
            return;
        }

        if (inited) {
            throw new UnsupportedOperationException();
        }

        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (StringUtils.equals(this.password, password)) {
            return;
        }

        if (inited) {
            LOG.info("password changed");
        }

        this.password = password;
    }

    public Properties getConnectProperties() {
        return connectProperties;
    }

    public abstract void setConnectProperties(Properties properties);

    public void setConnectionProperties(String connectionProperties) {
        if (connectionProperties == null || connectionProperties.trim().length() == 0) {
            setConnectProperties(null);
            return;
        }

        String[] entries = connectionProperties.split(";");
        Properties properties = new Properties();
        for (int i = 0; i < entries.length; i++) {
            String entry = entries[i];
            if (entry.length() > 0) {
                int index = entry.indexOf('=');
                if (index > 0) {
                    String name = entry.substring(0, index);
                    String value = entry.substring(index + 1);
                    properties.setProperty(name, value);
                } else {
                    // no value is empty string which is how java.util.Properties works
                    properties.setProperty(entry, "");
                }
            }
        }

        setConnectProperties(properties);
    }

    public String getUrl() {
        return jdbcUrl;
    }

    public String getRawJdbcUrl() {
        return jdbcUrl;
    }

    public void setUrl(String jdbcUrl) {
        if (StringUtils.equals(this.jdbcUrl, jdbcUrl)) {
            return;
        }

        if (inited) {
            throw new UnsupportedOperationException();
        }

        if (jdbcUrl != null) {
            jdbcUrl = jdbcUrl.trim();
        }

        this.jdbcUrl = jdbcUrl;

        // if (jdbcUrl.startsWith(ConfigFilter.URL_PREFIX)) {
        // this.filters.add(new ConfigFilter());
        // }
    }

    public String getDriverClassName() {
        return driverClass;
    }

    public void setDriverClassName(String driverClass) {
        if (driverClass != null && driverClass.length() > 256) {
            throw new IllegalArgumentException("driverClassName length > 256.");
        }

        if (JdbcConstants.ORACLE_DRIVER2.equalsIgnoreCase(driverClass)) {
            driverClass = "oracle.jdbc.OracleDriver";
            LOG.warn("oracle.jdbc.driver.OracleDriver is deprecated.Having use oracle.jdbc.OracleDriver.");
        }

        if (inited) {
            if (StringUtils.equals(this.driverClass, driverClass)) {
                return;
            }
            
            throw new UnsupportedOperationException();
        }

        this.driverClass = driverClass;
    }

    public ClassLoader getDriverClassLoader() {
        return driverClassLoader;
    }

    public void setDriverClassLoader(ClassLoader driverClassLoader) {
        this.driverClassLoader = driverClassLoader;
    }

    @Override
    public PrintWriter getLogWriter() {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.logWriter = out;
    }

    @Override
    public void setLoginTimeout(int seconds) {
        DriverManager.setLoginTimeout(seconds);
    }

    @Override
    public int getLoginTimeout() {
        return DriverManager.getLoginTimeout();
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public int getDriverMajorVersion() {
        if (this.driver == null) {
            return -1;
        }

        return this.driver.getMajorVersion();
    }

    public int getDriverMinorVersion() {
        if (this.driver == null) {
            return -1;
        }

        return this.driver.getMinorVersion();
    }

    public ExceptionSorter getExceptionSorter() {
        return exceptionSorter;
    }

    public String getExceptionSorterClassName() {
        if (exceptionSorter == null) {
            return null;
        }

        return exceptionSorter.getClass().getName();
    }

    public void setExceptionSorter(ExceptionSorter exceptionSoter) {
        this.exceptionSorter = exceptionSoter;
    }

    // 兼容JBOSS
    public void setExceptionSorterClassName(String exceptionSorter) throws Exception {
        this.setExceptionSorter(exceptionSorter);
    }

    public void setExceptionSorter(String exceptionSorter) throws SQLException {
        if (exceptionSorter == null) {
            this.exceptionSorter = NullExceptionSorter.getInstance();
            return;
        }

        exceptionSorter = exceptionSorter.trim();
        if (exceptionSorter.length() == 0) {
            this.exceptionSorter = NullExceptionSorter.getInstance();
            return;
        }

        Class<?> clazz = Utils.loadClass(exceptionSorter);
        if (clazz == null) {
            LOG.error("load exceptionSorter error : " + exceptionSorter);
        } else {
            try {
                this.exceptionSorter = (ExceptionSorter) clazz.newInstance();
            } catch (Exception ex) {
                throw new SQLException("create exceptionSorter error", ex);
            }
        }
    }

    @Override
    public List<Filter> getProxyFilters() {
        return filters;
    }

    public void setProxyFilters(List<Filter> filters) {
        if (filters != null) {
            this.filters.addAll(filters);
        }
    }

    public String[] getFilterClasses() {
        List<Filter> filterConfigList = getProxyFilters();

        List<String> classes = new ArrayList<String>();
        for (Filter filter : filterConfigList) {
            classes.add(filter.getClass().getName());
        }

        return classes.toArray(new String[classes.size()]);
    }

    public void setFilters(String filters) throws SQLException {
        if (filters != null && filters.startsWith("!")) {
            filters = filters.substring(1);
            this.clearFilters();
        }
        this.addFilters(filters);
    }

    public void addFilters(String filters) throws SQLException {
        if (filters == null || filters.length() == 0) {
            return;
        }

        String[] filterArray = filters.split("\\,");

        for (String item : filterArray) {
            FilterManager.loadFilter(this.filters, item.trim());
        }
    }

    public void clearFilters() {
        if (!isClearFiltersEnable()) {
            return;
        }
        this.filters.clear();
    }

    public void validateConnection(Connection conn) throws SQLException {
        String query = getValidationQuery();
        if (conn.isClosed()) {
            throw new SQLException("validateConnection: connection closed");
        }

        if (validConnectionChecker != null) {
            boolean result = true;
            Exception error = null;
            try {
                result = validConnectionChecker.isValidConnection(conn, validationQuery, validationQueryTimeout);

                if (result && onFatalError) {
                    lock.lock();
                    try {
                        if (onFatalError) {
                            onFatalError = false;
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            } catch (SQLException ex) {
                throw ex;
            } catch (Exception ex) {
                error = ex;
            }
            
            if (!result) {
                SQLException sqlError = error != null ? //
                    new SQLException("validateConnection false", error) //
                    : new SQLException("validateConnection false");
                throw sqlError;
            }
            return;
        }

        if (null != query) {
            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt = conn.createStatement();
                if (getValidationQueryTimeout() > 0) {
                    stmt.setQueryTimeout(getValidationQueryTimeout());
                }
                rs = stmt.executeQuery(query);
                if (!rs.next()) {
                    throw new SQLException("validationQuery didn't return a row");
                }

                if (onFatalError) {
                    lock.lock();
                    try {
                        if (onFatalError) {
                            onFatalError = false;
                        }
                    }
                    finally {
                        lock.unlock();
                    }
                }
            } finally {
                JdbcUtils.close(rs);
                JdbcUtils.close(stmt);
            }
        }
    }

    /**
     * @deprecated
     */
    protected boolean testConnectionInternal(Connection conn) {
        return testConnectionInternal(null, conn);
    }

    protected boolean testConnectionInternal(DruidConnectionHolder holder, Connection conn) {
        String sqlFile = JdbcSqlStat.getContextSqlFile();
        String sqlName = JdbcSqlStat.getContextSqlName();

        if (sqlFile != null) {
            JdbcSqlStat.setContextSqlFile(null);
        }
        if (sqlName != null) {
            JdbcSqlStat.setContextSqlName(null);
        }
        try {
            if (validConnectionChecker != null) {
                boolean valid = validConnectionChecker.isValidConnection(conn, validationQuery, validationQueryTimeout);
                long currentTimeMillis = System.currentTimeMillis();
                if (holder != null) {
                    holder.lastValidTimeMillis = currentTimeMillis;
                    holder.lastExecTimeMillis = currentTimeMillis;
                }

                if (valid && isMySql) { // unexcepted branch
                    long lastPacketReceivedTimeMs = MySqlUtils.getLastPacketReceivedTimeMs(conn);
                    if (lastPacketReceivedTimeMs > 0) {
                        long mysqlIdleMillis = currentTimeMillis - lastPacketReceivedTimeMs;
                        if (lastPacketReceivedTimeMs > 0 //
                                && mysqlIdleMillis >= timeBetweenEvictionRunsMillis) {
                            discardConnection(holder);
                            String errorMsg = "discard long time none received connection. "
                                    + ", jdbcUrl : " + jdbcUrl
                                    + ", jdbcUrl : " + jdbcUrl
                                    + ", lastPacketReceivedIdleMillis : " + mysqlIdleMillis;
                            LOG.error(errorMsg);
                            return false;
                        }
                    }
                }

                if (valid && onFatalError) {
                    lock.lock();
                    try {
                        if (onFatalError) {
                            onFatalError = false;
                        }
                    } finally {
                        lock.unlock();
                    }
                }

                return valid;
            }

            if (conn.isClosed()) {
                return false;
            }

            if (null == validationQuery) {
                return true;
            }

            Statement stmt = null;
            ResultSet rset = null;
            try {
                stmt = conn.createStatement();
                if (getValidationQueryTimeout() > 0) {
                    stmt.setQueryTimeout(validationQueryTimeout);
                }
                rset = stmt.executeQuery(validationQuery);
                if (!rset.next()) {
                    return false;
                }
            } finally {
                JdbcUtils.close(rset);
                JdbcUtils.close(stmt);
            }

            if (onFatalError) {
                lock.lock();
                try {
                    if (onFatalError) {
                        onFatalError = false;
                    }
                } finally {
                    lock.unlock();
                }
            }

            return true;
        } catch (Throwable ex) {
            // skip
            return false;
        } finally {
            if (sqlFile != null) {
                JdbcSqlStat.setContextSqlFile(sqlFile);
            }
            if (sqlName != null) {
                JdbcSqlStat.setContextSqlName(sqlName);
            }
        }
    }

    public Set<DruidPooledConnection> getActiveConnections() {
        activeConnectionLock.lock();
        try {
            return new HashSet<DruidPooledConnection>(this.activeConnections.keySet());
        } finally {
            activeConnectionLock.unlock();
        }
    }

    public List<String> getActiveConnectionStackTrace() {
        List<String> list = new ArrayList<String>();

        for (DruidPooledConnection conn : this.getActiveConnections()) {
            list.add(Utils.toString(conn.getConnectStackTrace()));
        }

        return list;
    }

    public long getCreateTimespanNano() {
        return createTimespan;
    }

    public long getCreateTimespanMillis() {
        return createTimespan / (1000 * 1000);
    }

    @Override
    public Driver getRawDriver() {
        return driver;
    }

    public boolean isClearFiltersEnable() {
        return clearFiltersEnable;
    }

    public void setClearFiltersEnable(boolean clearFiltersEnable) {
        this.clearFiltersEnable = clearFiltersEnable;
    }

    protected volatile long connectionIdSeed  = 10000L;
    protected volatile long statementIdSeed   = 20000L;
    protected volatile long resultSetIdSeed   = 50000L;
    protected volatile long transactionIdSeed = 60000L;
    protected volatile long metaDataIdSeed    = 80000L;

    final static AtomicLongFieldUpdater<DruidAbstractDataSource> connectionIdSeedUpdater  = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "connectionIdSeed");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource> statementIdSeedUpdater   = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "statementIdSeed");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource> resultSetIdSeedUpdater   = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "resultSetIdSeed");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource> transactionIdSeedUpdater = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "transactionIdSeed");
    final static AtomicLongFieldUpdater<DruidAbstractDataSource> metaDataIdSeedUpdater    = AtomicLongFieldUpdater.newUpdater(DruidAbstractDataSource.class, "metaDataIdSeed");

    public long createConnectionId() {
        return connectionIdSeedUpdater.incrementAndGet(this);
    }

    public long createStatementId() {
        return statementIdSeedUpdater.getAndIncrement(this);
    }

    public long createMetaDataId() {
        return metaDataIdSeedUpdater.getAndIncrement(this);
    }

    public long createResultSetId() {
        return resultSetIdSeedUpdater.getAndIncrement(this);
    }

    @Override
    public long createTransactionId() {
        return transactionIdSeedUpdater.getAndIncrement(this);
    }

    void initStatement(DruidPooledConnection conn, Statement stmt) throws SQLException {
        boolean transaction = !conn.getConnectionHolder().underlyingAutoCommit;

        int queryTimeout = transaction ? getTransactionQueryTimeout() : getQueryTimeout();

        if (queryTimeout > 0) {
            stmt.setQueryTimeout(queryTimeout);
        }
    }

    public void handleConnectionException(DruidPooledConnection conn, Throwable t) throws SQLException {
        handleConnectionException(conn, t, null);
    }

    public abstract void handleConnectionException(DruidPooledConnection conn, Throwable t, String sql) throws SQLException;

    protected abstract void recycle(DruidPooledConnection pooledConnection) throws SQLException;

    public Connection createPhysicalConnection(String url, Properties info) throws SQLException {
        Connection conn;
        if (getProxyFilters().size() == 0) {
            conn = getDriver().connect(url, info);
        } else {
            conn = new FilterChainImpl(this).connection_connect(info);
        }

        createCountUpdater.incrementAndGet(this);

        return conn;
    }

    public PhysicalConnectionInfo createPhysicalConnection() throws SQLException {
        String url = this.getUrl();
        Properties connectProperties = getConnectProperties();

        String user;
        if (getUserCallback() != null) {
            user = getUserCallback().getName();
        } else {
            user = getUsername();
        }

        String password = getPassword();
        PasswordCallback passwordCallback = getPasswordCallback();

        if (passwordCallback != null) {
            if (passwordCallback instanceof DruidPasswordCallback) {
                DruidPasswordCallback druidPasswordCallback = (DruidPasswordCallback) passwordCallback;

                druidPasswordCallback.setUrl(url);
                druidPasswordCallback.setProperties(connectProperties);
            }

            char[] chars = passwordCallback.getPassword();
            if (chars != null) {
                password = new String(chars);
            }
        }

        Properties physicalConnectProperties = new Properties();
        if (connectProperties != null) {
            physicalConnectProperties.putAll(connectProperties);
        }

        if (user != null && user.length() != 0) {
            physicalConnectProperties.put("user", user);
        }

        if (password != null && password.length() != 0) {
            physicalConnectProperties.put("password", password);
        }

        Connection conn = null;

        long connectStartNanos = System.nanoTime();
        long connectedNanos, initedNanos, validatedNanos;

        Map<String, Object> variables = initVariants
                ? new HashMap<String, Object>()
                : null;
        Map<String, Object> globalVariables = initGlobalVariants
                ? new HashMap<String, Object>()
                : null;

        createStartNanosUpdater.set(this, connectStartNanos);
        creatingCountUpdater.incrementAndGet(this);
        try {
            conn = createPhysicalConnection(url, physicalConnectProperties);
            connectedNanos = System.nanoTime();

            if (conn == null) {
                throw new SQLException("connect error, url " + url + ", driverClass " + this.driverClass);
            }

            initPhysicalConnection(conn, variables, globalVariables);
            initedNanos = System.nanoTime();

            validateConnection(conn);
            validatedNanos = System.nanoTime();

            setFailContinuous(false);
            setCreateError(null);
        } catch (SQLException ex) {
            setCreateError(ex);
            JdbcUtils.close(conn);
            throw ex;
        } catch (RuntimeException ex) {
            setCreateError(ex);
            JdbcUtils.close(conn);
            throw ex;
        } catch (Error ex) {
            createErrorCountUpdater.incrementAndGet(this);
            setCreateError(ex);
            JdbcUtils.close(conn);
            throw ex;
        } finally {
            long nano = System.nanoTime() - connectStartNanos;
            createTimespan += nano;
            creatingCountUpdater.decrementAndGet(this);
        }

        return new PhysicalConnectionInfo(conn, connectStartNanos, connectedNanos, initedNanos, validatedNanos, variables, globalVariables);
    }

    protected void setCreateError(Throwable ex) {
        if (ex == null) {
            lock.lock();
            try {
                if (createError != null) {
                    createError = null;
                }
            } finally {
                lock.unlock();
            }
            return;
        }
        
        createErrorCountUpdater.incrementAndGet(this);
        long now = System.currentTimeMillis();
        lock.lock();
        try {
            createError = ex;
            lastCreateError = ex;
            lastCreateErrorTimeMillis = now;
        } finally {
            lock.unlock();
        }
    }

    public boolean isFailContinuous() {
        return failContinuousUpdater.get(this) == 1;
    }

    protected void setFailContinuous(boolean fail) {
        if (fail) {
            failContinuousTimeMillisUpdater.set(this, System.currentTimeMillis());
        } else {
            failContinuousTimeMillisUpdater.set(this, 0L);
        }

        boolean currentState = failContinuousUpdater.get(this) == 1;
        if (currentState == fail) {
            return;
        }

        if (fail) {
            failContinuousUpdater.set(this, 1);
            if (LOG.isInfoEnabled()) {
                LOG.info("{dataSource-" + this.getID() + "} failContinuous is true");
            }
        } else {
            failContinuousUpdater.set(this, 0);
            if (LOG.isInfoEnabled()) {
                LOG.info("{dataSource-" + this.getID() + "} failContinuous is false");
            }
        }
    }

    public void initPhysicalConnection(Connection conn) throws SQLException {
        initPhysicalConnection(conn, null, null);
    }

    public void initPhysicalConnection(Connection conn, Map<String, Object> variables, Map<String, Object> globalVariables) throws SQLException {
        if (conn.getAutoCommit() != defaultAutoCommit) {
            conn.setAutoCommit(defaultAutoCommit);
        }

        if (defaultReadOnly != null) {
            if (conn.isReadOnly() != defaultReadOnly) {
                conn.setReadOnly(defaultReadOnly);
            }
        }

        if (getDefaultTransactionIsolation() != null) {
            if (conn.getTransactionIsolation() != getDefaultTransactionIsolation().intValue()) {
                conn.setTransactionIsolation(getDefaultTransactionIsolation());
            }
        }

        if (getDefaultCatalog() != null && getDefaultCatalog().length() != 0) {
            conn.setCatalog(getDefaultCatalog());
        }

        Collection<String> initSqls = getConnectionInitSqls();
        if (initSqls.size() == 0
                && variables == null
                && globalVariables == null) {
            return;
        }

        Statement stmt = null;
        try {
            stmt = conn.createStatement();

            for (String sql : initSqls) {
                if (sql == null) {
                    continue;
                }

                stmt.execute(sql);
            }

            if (JdbcConstants.MYSQL.equals(dbType)
                ||JdbcConstants.ALIYUN_ADS.equals(dbType)) {
                if (variables != null) {
                    ResultSet rs = null;
                    try {
                        rs = stmt.executeQuery("show variables");
                        while (rs.next()) {
                            String name = rs.getString(1);
                            Object value = rs.getObject(2);
                            variables.put(name, value);
                        }
                    } finally {
                        JdbcUtils.close(rs);
                    }
                }

                if (globalVariables != null) {
                    ResultSet rs = null;
                    try {
                        rs = stmt.executeQuery("show global variables");
                        while (rs.next()) {
                            String name = rs.getString(1);
                            Object value = rs.getObject(2);
                            globalVariables.put(name, value);
                        }
                    } finally {
                        JdbcUtils.close(rs);
                    }
                }
            }
        } finally {
            JdbcUtils.close(stmt);
        }
    }

    public abstract int getActivePeak();

    public CompositeDataSupport getCompositeData() throws JMException {
        JdbcDataSourceStat stat = this.getDataSourceStat();

        Map<String, Object> map = new HashMap<String, Object>();

        map.put("ID", getID());
        map.put("URL", this.getUrl());
        map.put("Name", this.getName());
        map.put("FilterClasses", getFilterClasses());
        map.put("CreatedTime", getCreatedTime());

        map.put("RawDriverClassName", getDriverClassName());
        map.put("RawUrl", getUrl());
        map.put("RawDriverMajorVersion", getRawDriverMajorVersion());
        map.put("RawDriverMinorVersion", getRawDriverMinorVersion());
        map.put("Properties", getProperties());

        // 0 - 4
        map.put("ConnectionActiveCount", (long) getActiveCount());
        map.put("ConnectionActiveCountMax", getActivePeak());
        map.put("ConnectionCloseCount", getCloseCount());
        map.put("ConnectionCommitCount", getCommitCount());
        map.put("ConnectionRollbackCount", getRollbackCount());

        // 5 - 9
        map.put("ConnectionConnectLastTime", stat.getConnectionStat().getConnectLastTime());
        map.put("ConnectionConnectErrorCount", this.getCreateCount());
        if (createError != null) {
            map.put("ConnectionConnectErrorLastTime", getLastCreateErrorTime());
            map.put("ConnectionConnectErrorLastMessage", createError.getMessage());
            map.put("ConnectionConnectErrorLastStackTrace", Utils.getStackTrace(createError));
        } else {
            map.put("ConnectionConnectErrorLastTime", null);
            map.put("ConnectionConnectErrorLastMessage", null);
            map.put("ConnectionConnectErrorLastStackTrace", null);
        }

        // 10 - 14
        map.put("StatementCreateCount", stat.getStatementStat().getCreateCount());
        map.put("StatementPrepareCount", stat.getStatementStat().getPrepareCount());
        map.put("StatementPreCallCount", stat.getStatementStat().getPrepareCallCount());
        map.put("StatementExecuteCount", stat.getStatementStat().getExecuteCount());
        map.put("StatementRunningCount", stat.getStatementStat().getRunningCount());

        // 15 - 19
        map.put("StatementConcurrentMax", stat.getStatementStat().getConcurrentMax());
        map.put("StatementCloseCount", stat.getStatementStat().getCloseCount());
        map.put("StatementErrorCount", stat.getStatementStat().getErrorCount());
        map.put("StatementLastErrorTime", null);
        map.put("StatementLastErrorMessage", null);

        // 20 - 24
        map.put("StatementLastErrorStackTrace", null);
        map.put("StatementExecuteMillisTotal", stat.getStatementStat().getMillisTotal());
        map.put("StatementExecuteLastTime", stat.getStatementStat().getExecuteLastTime());
        map.put("ConnectionConnectingCount", stat.getConnectionStat().getConnectingCount());
        map.put("ResultSetCloseCount", stat.getResultSetStat().getCloseCount());

        // 25 - 29
        map.put("ResultSetOpenCount", stat.getResultSetStat().getOpenCount());
        map.put("ResultSetOpenningCount", stat.getResultSetStat().getOpeningCount());
        map.put("ResultSetOpenningMax", stat.getResultSetStat().getOpeningMax());
        map.put("ResultSetFetchRowCount", stat.getResultSetStat().getFetchRowCount());
        map.put("ResultSetLastOpenTime", stat.getResultSetStat().getLastOpenTime());

        // 30 - 34
        map.put("ResultSetErrorCount", stat.getResultSetStat().getErrorCount());
        map.put("ResultSetOpenningMillisTotal", stat.getResultSetStat().getAliveMillisTotal());
        map.put("ResultSetLastErrorTime", stat.getResultSetStat().getLastErrorTime());
        map.put("ResultSetLastErrorMessage", null);
        map.put("ResultSetLastErrorStackTrace", null);

        // 35 - 39
        map.put("ConnectionConnectCount", this.getConnectCount());
        if (createError != null) {
            map.put("ConnectionErrorLastMessage", createError.getMessage());
            map.put("ConnectionErrorLastStackTrace", Utils.getStackTrace(createError));
        } else {
            map.put("ConnectionErrorLastMessage", null);
            map.put("ConnectionErrorLastStackTrace", null);
        }
        map.put("ConnectionConnectMillisTotal", stat.getConnectionStat().getConnectMillis());
        map.put("ConnectionConnectingCountMax", stat.getConnectionStat().getConnectingMax());

        // 40 - 44
        map.put("ConnectionConnectMillisMax", stat.getConnectionStat().getConnectMillisMax());
        map.put("ConnectionErrorLastTime", stat.getConnectionStat().getErrorLastTime());
        map.put("ConnectionAliveMillisMax", stat.getConnectionConnectAliveMillisMax());
        map.put("ConnectionAliveMillisMin", stat.getConnectionConnectAliveMillisMin());

        map.put("ConnectionHistogram", stat.getConnectionHistogramValues());
        map.put("StatementHistogram", stat.getStatementStat().getHistogramValues());

        return new CompositeDataSupport(JdbcStatManager.getDataSourceCompositeType(), map);
    }

    public long getID() {
        return this.id;
    }

    public Date getCreatedTime() {
        return createdTime;
    }

    public abstract int getRawDriverMajorVersion();

    public abstract int getRawDriverMinorVersion();

    public abstract String getProperties();

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        throw new SQLFeatureNotSupportedException();
    }

    public void closePreapredStatement(PreparedStatementHolder stmtHolder) {
        if (stmtHolder == null) {
            return;
        }
        closedPreparedStatementCountUpdater.incrementAndGet(this);
        decrementCachedPreparedStatementCount();
        incrementCachedPreparedStatementDeleteCount();

        JdbcUtils.close(stmtHolder.statement);
    }

    protected void cloneTo(DruidAbstractDataSource to) {
        to.defaultAutoCommit = this.defaultAutoCommit;
        to.defaultReadOnly = this.defaultReadOnly;
        to.defaultTransactionIsolation = this.defaultTransactionIsolation;
        to.defaultCatalog = this.defaultCatalog;
        to.name = this.name;
        to.username = this.username;
        to.password = this.password;
        to.jdbcUrl = this.jdbcUrl;
        to.driverClass = this.driverClass;
        to.connectProperties = this.connectProperties;
        to.passwordCallback = this.passwordCallback;
        to.userCallback = this.userCallback;
        to.initialSize = this.initialSize;
        to.maxActive = this.maxActive;
        to.minIdle = this.minIdle;
        to.maxIdle = this.maxIdle;
        to.maxWait = this.maxWait;
        to.validationQuery = this.validationQuery;
        to.validationQueryTimeout = this.validationQueryTimeout;
        to.testOnBorrow = this.testOnBorrow;
        to.testOnReturn = this.testOnReturn;
        to.testWhileIdle = this.testWhileIdle;
        to.poolPreparedStatements = this.poolPreparedStatements;
        to.sharePreparedStatements = this.sharePreparedStatements;
        to.maxPoolPreparedStatementPerConnectionSize = this.maxPoolPreparedStatementPerConnectionSize;
        to.logWriter = this.logWriter;
        if (this.filters != null) {
            to.filters = new ArrayList<Filter>(this.filters);
        }
        to.exceptionSorter = this.exceptionSorter;
        to.driver = this.driver;
        to.queryTimeout = this.queryTimeout;
        to.transactionQueryTimeout = this.transactionQueryTimeout;
        to.accessToUnderlyingConnectionAllowed = this.accessToUnderlyingConnectionAllowed;
        to.timeBetweenEvictionRunsMillis = this.timeBetweenEvictionRunsMillis;
        to.numTestsPerEvictionRun = this.numTestsPerEvictionRun;
        to.minEvictableIdleTimeMillis = this.minEvictableIdleTimeMillis;
        to.removeAbandoned = this.removeAbandoned;
        to.removeAbandonedTimeoutMillis = this.removeAbandonedTimeoutMillis;
        to.logAbandoned = this.logAbandoned;
        to.maxOpenPreparedStatements = this.maxOpenPreparedStatements;
        if (connectionInitSqls != null) {
            to.connectionInitSqls = new ArrayList<String>(this.connectionInitSqls);
        }
        to.dbType = this.dbType;
        to.timeBetweenConnectErrorMillis = this.timeBetweenConnectErrorMillis;
        to.validConnectionChecker = this.validConnectionChecker;
        to.connectionErrorRetryAttempts = this.connectionErrorRetryAttempts;
        to.breakAfterAcquireFailure = this.breakAfterAcquireFailure;
        to.transactionThresholdMillis = this.transactionThresholdMillis;
        to.dupCloseLogEnable = this.dupCloseLogEnable;
        to.isOracle = this.isOracle;
        to.useOracleImplicitCache = this.useOracleImplicitCache;
        to.asyncCloseConnectionEnable = this.asyncCloseConnectionEnable;
        to.createScheduler = this.createScheduler;
        to.destroyScheduler = this.destroyScheduler;
    }
    
    public abstract void discardConnection(Connection realConnection);

    public void discardConnection(DruidConnectionHolder holder) {
        discardConnection(holder.getConnection());
    }

    public boolean isAsyncCloseConnectionEnable() {
        if (isRemoveAbandoned()) {
            return true;
        }
        return asyncCloseConnectionEnable;
    }

    public void setAsyncCloseConnectionEnable(boolean asyncCloseConnectionEnable) {
        this.asyncCloseConnectionEnable = asyncCloseConnectionEnable;
    }

    public ScheduledExecutorService getCreateScheduler() {
        return createScheduler;
    }
    
    public void setCreateScheduler(ScheduledExecutorService createScheduler) {
        if (isInited()) {
            throw new DruidRuntimeException("dataSource inited.");
        }
        this.createScheduler = createScheduler;
    }

    public ScheduledExecutorService getDestroyScheduler() {
        return destroyScheduler;
    }

    
    public void setDestroyScheduler(ScheduledExecutorService destroyScheduler) {
        if (isInited()) {
            throw new DruidRuntimeException("dataSource inited.");
        }
        this.destroyScheduler = destroyScheduler;
    }

    public boolean isInited() {
        return this.inited;
    }

    
    public int getMaxCreateTaskCount() {
        return maxCreateTaskCount;
    }

    
    public void setMaxCreateTaskCount(int maxCreateTaskCount) {
        if (maxCreateTaskCount < 1) {
            throw new IllegalArgumentException();
        }
        
        this.maxCreateTaskCount = maxCreateTaskCount;
    }
    
    public boolean isFailFast() {
        return failFast;
    }
    
    public void setFailFast(boolean failFast) {
        this.failFast = failFast;
    }

    public int getOnFatalErrorMaxActive() {
        return onFatalErrorMaxActive;
    }

    public void setOnFatalErrorMaxActive(int onFatalErrorMaxActive) {
        this.onFatalErrorMaxActive = onFatalErrorMaxActive;
    }

    public boolean isOnFatalError() {
        return onFatalError;
    }

    /**
     * @since 1.1.11
     */
    public boolean isInitExceptionThrow() {
        return initExceptionThrow;
    }

    /**
     * @since 1.1.11
     */
    public void setInitExceptionThrow(boolean initExceptionThrow) {
        this.initExceptionThrow = initExceptionThrow;
    }

    public static class PhysicalConnectionInfo {
        private Connection connection;
        private long connectStartNanos;
        private long connectedNanos;
        private long initedNanos;
        private long validatedNanos;
        private Map<String, Object> vairiables;
        private Map<String, Object> globalVairiables;

        long createTaskId;

        public PhysicalConnectionInfo(Connection connection //
                , long connectStartNanos //
                , long connectedNanos //
                , long initedNanos //
                , long validatedNanos) {
            this(connection, connectStartNanos, connectedNanos, initedNanos,validatedNanos, null, null);
        }
        
        public PhysicalConnectionInfo(Connection connection //
                                      , long connectStartNanos //
                                      , long connectedNanos //
                                      , long initedNanos //
                                      , long validatedNanos
                                      , Map<String, Object> vairiables
                                      , Map<String, Object> globalVairiables) {
            this.connection = connection;
            
            this.connectStartNanos = connectStartNanos;
            this.connectedNanos = connectedNanos;
            this.initedNanos = initedNanos;
            this.validatedNanos = validatedNanos;
            this.vairiables = vairiables;
            this.globalVairiables = globalVairiables;
        }
        
        public Connection getPhysicalConnection() {
            return connection;
        }

        public long getConnectStartNanos() {
            return connectStartNanos;
        }
        
        public long getConnectedNanos() {
            return connectedNanos;
        }
        
        public long getInitedNanos() {
            return initedNanos;
        }

        public long getValidatedNanos() {
            return validatedNanos;
        }
        
        public long getConnectNanoSpan() {
            return connectedNanos - connectStartNanos;
        }

        public Map<String, Object> getVairiables() {
            return vairiables;
        }

        public Map<String, Object> getGlobalVairiables() {
            return globalVairiables;
        }
    }
}
