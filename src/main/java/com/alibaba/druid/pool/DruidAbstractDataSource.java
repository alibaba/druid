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

import java.io.PrintWriter;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.openmbean.CompositeDataSupport;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.sql.DataSource;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.filter.stat.StatFilter;
import com.alibaba.druid.pool.DruidDataSource.ActiveConnectionTraceInfo;
import com.alibaba.druid.pool.vendor.NullExceptionSorter;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.stat.JdbcDataSourceStat;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.util.ConcurrentIdentityHashMap;
import com.alibaba.druid.util.DruidLoaderUtils;
import com.alibaba.druid.util.JdbcUtils;

/**
 * @author wenshao<szujobs@hotmail.com>
 * @author ljw<ljw2083@alibaba-inc.com>
 */
public abstract class DruidAbstractDataSource extends WrapperAdapter implements DruidAbstractDataSourceMBean, DataSource, DataSourceProxy, Serializable {

    private static final long                                                                serialVersionUID                          = 1L;

    public final static int                                                                  DEFAULT_INITIAL_SIZE                      = 0;
    public final static int                                                                  DEFAULT_MAX_ACTIVE_SIZE                   = 8;
    public final static int                                                                  DEFAULT_MAX_IDLE                          = 8;
    public final static int                                                                  DEFAULT_MIN_IDLE                          = 0;
    public final static int                                                                  DEFAULT_MAX_WAIT                          = -1;
    public final static String                                                               DEFAULT_VALIDATION_QUERY                  = null;                                                                          //
    public final static boolean                                                              DEFAULT_TEST_ON_BORROW                    = true;
    public final static boolean                                                              DEFAULT_TEST_ON_RETURN                    = false;
    public final static boolean                                                              DEFAULT_WHILE_IDLE                        = false;
    public static final long                                                                 DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS = -1L;
    public static final long                                                                 DEFAULT_TIME_BETWEEN_CONNECT_ERROR_MILLIS = 30 * 1000;
    public static final int                                                                  DEFAULT_NUM_TESTS_PER_EVICTION_RUN        = 3;

    /**
     * The default value for {@link #getMinEvictableIdleTimeMillis}.
     * 
     * @see #getMinEvictableIdleTimeMillis
     * @see #setMinEvictableIdleTimeMillis
     */
    public static final long                                                                 DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS    = 1000L * 60L * 30L;

    protected boolean                                                                        defaultAutoCommit                         = true;
    protected Boolean                                                                        defaultReadOnly;
    protected Integer                                                                        defaultTransactionIsolation;
    protected String                                                                         defaultCatalog                            = null;

    protected String                                                                         name;

    protected String                                                                         username;
    protected String                                                                         password;
    protected String                                                                         jdbcUrl;
    protected String                                                                         driverClass;
    protected Properties                                                                     connectionProperties                      = new Properties();

    protected PasswordCallback                                                               passwordCallback;
    protected NameCallback                                                                   userCallback;

    protected ConnectionFactory                                                              connectionFactory;

    protected int                                                                            initialSize                               = DEFAULT_INITIAL_SIZE;
    protected int                                                                            maxActive                                 = DEFAULT_MAX_ACTIVE_SIZE;
    protected int                                                                            minIdle                                   = DEFAULT_MIN_IDLE;
    protected int                                                                            maxIdle                                   = DEFAULT_MAX_IDLE;
    protected long                                                                           maxWait                                   = DEFAULT_MAX_WAIT;

    protected String                                                                         validationQuery                           = DEFAULT_VALIDATION_QUERY;
    protected int                                                                            validationQueryTimeout                    = -1;
    private boolean                                                                          testOnBorrow                              = DEFAULT_TEST_ON_BORROW;
    private boolean                                                                          testOnReturn                              = DEFAULT_TEST_ON_RETURN;
    private boolean                                                                          testWhileIdle                             = DEFAULT_WHILE_IDLE;
    protected boolean                                                                        poolPreparedStatements                    = false;
    protected int                                                                            maxPoolPreparedStatementPerConnectionSize = -1;

    protected boolean                                                                        inited                                    = false;

    protected PrintWriter                                                                    logWriter                                 = new PrintWriter(
                                                                                                                                                         System.out);

    protected List<Filter>                                                                   filters                                   = new ArrayList<Filter>();
    protected ExceptionSorter                                                                exceptionSoter                            = null;

    protected Driver                                                                         driver;

    protected int                                                                            queryTimeout;

    protected long                                                                           createErrorCount;

    protected long                                                                           createTimespan;

    protected int                                                                            maxWaitThreadCount                        = -1;

    protected boolean                                                                        accessToUnderlyingConnectionAllowed       = true;

    protected long                                                                           timeBetweenEvictionRunsMillis             = DEFAULT_TIME_BETWEEN_EVICTION_RUNS_MILLIS;

    protected int                                                                            numTestsPerEvictionRun                    = DEFAULT_NUM_TESTS_PER_EVICTION_RUN;

    protected long                                                                           minEvictableIdleTimeMillis                = DEFAULT_MIN_EVICTABLE_IDLE_TIME_MILLIS;

    protected boolean                                                                        removeAbandoned;

    protected long                                                                           removeAbandonedTimeoutMillis              = 300 * 1000;

    protected boolean                                                                        logAbandoned;

    protected int                                                                            maxOpenPreparedStatements                 = -1;

    protected List<String>                                                                   connectionInitSqls;

    protected String                                                                         dbType;

    protected long                                                                           timeBetweenConnectErrorMillis             = DEFAULT_TIME_BETWEEN_CONNECT_ERROR_MILLIS;

    protected ValidConnectionChecker                                                         validConnectionChecker                    = null;

    protected final AtomicLong                                                               errorCount                                = new AtomicLong();
    protected final AtomicLong                                                               dupCloseCount                             = new AtomicLong();

    protected final ConcurrentIdentityHashMap<PoolableConnection, ActiveConnectionTraceInfo> activeConnections                         = new ConcurrentIdentityHashMap<PoolableConnection, ActiveConnectionTraceInfo>();

    protected long                                                                           id;

    protected Date                                                                           createdTime;

    protected int                                                                            connectionErrorRetryAttempts              = 30;

    protected boolean                                                                        breakAfterAcquireFailure                  = false;

    protected final AtomicLong                                                               commitCount                               = new AtomicLong();
    protected final AtomicLong                                                               startTransactionCount                     = new AtomicLong();
    protected final AtomicLong                                                               rollbackCount                             = new AtomicLong();

    public long getCommitCount() {
        return commitCount.get();
    }

    public void incrementCommitCount() {
        commitCount.incrementAndGet();
    }

    public long getRollbackCount() {
        return rollbackCount.get();
    }

    public void incrementRollbackCount() {
        rollbackCount.incrementAndGet();
    }

    public long getStartTransactionCount() {
        return startTransactionCount.get();
    }

    public void incrementStartTransactionCount() {
        startTransactionCount.incrementAndGet();
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
        return dupCloseCount.get();
    }

    public int getMaxPoolPreparedStatementPerConnectionSize() {
        return maxPoolPreparedStatementPerConnectionSize;
    }

    public void setMaxPoolPreparedStatementPerConnectionSize(int maxPoolPreparedStatementPerConnectionSize) {
        this.maxPoolPreparedStatementPerConnectionSize = maxPoolPreparedStatementPerConnectionSize;
    }

    public void incrementDupCloseCount() {
        dupCloseCount.incrementAndGet();
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
        Class<?> clazz = DruidLoaderUtils.loadClass(validConnectionCheckerClass);
        ValidConnectionChecker validConnectionChecker = null;
        if (clazz != null) {
            validConnectionChecker = (ValidConnectionChecker) clazz.newInstance();
        }

        this.validConnectionChecker = validConnectionChecker;
    }

    public String getDbType() {
        return dbType;
    }

    public void addConnectionProperty(String name, String value) {
        if (inited) {
            throw new UnsupportedOperationException();
        }

        connectionProperties.put(name, value);
    }

    public Collection<String> getConnectionInitSqls() {
        Collection<String> result = connectionInitSqls;
        if (result == null) {
            return Collections.emptyList();
        }
        return result;
    }

    public void setConnectionInitSqls(Collection<Object> connectionInitSqls) {
        if ((connectionInitSqls != null) && (connectionInitSqls.size() > 0)) {
            ArrayList<String> newVal = null;
            for (Iterator<Object> iterator = connectionInitSqls.iterator(); iterator.hasNext();) {
                Object o = iterator.next();
                if (o != null) {
                    String s = o.toString();
                    if (s.trim().length() > 0) {
                        if (newVal == null) {
                            newVal = new ArrayList<String>();
                        }
                        newVal.add(s);
                    }
                }
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
        return maxOpenPreparedStatements;
    }

    public void setMaxOpenPreparedStatements(int maxOpenPreparedStatements) {
        this.maxOpenPreparedStatements = maxOpenPreparedStatements;
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
        this.removeAbandonedTimeoutMillis = removeAbandonedTimeout * 1000;
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
        this.minEvictableIdleTimeMillis = minEvictableIdleTimeMillis;
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
        Class<?> clazz = DruidLoaderUtils.loadClass(passwordCallbackClassName);
        if (clazz != null) {
            this.passwordCallback = (PasswordCallback) clazz.newInstance();
        } else {
            this.passwordCallback = null;
        }
    }

    public NameCallback getUserCallback() {
        return userCallback;
    }

    public void setUserCallback(NameCallback userCallback) {
        this.userCallback = userCallback;
    }

    /**
     * Retrieves the number of seconds the driver will wait for a <code>Statement</code> object to execute. If the limit
     * is exceeded, a <code>SQLException</code> is thrown.
     * 
     * @return the current query timeout limit in seconds; zero means there is no limit
     * @exception SQLException if a database access error occurs or this method is called on a closed
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
     * @exception SQLException if a database access error occurs, this method is called on a closed
     * <code>Statement</code> or the condition seconds >= 0 is not satisfied
     * @see #getQueryTimeout
     */
    public void setQueryTimeout(int seconds) {
        this.queryTimeout = seconds;
    }

    public String getName() {
        if (name == null) {
            return "DataSource-" + System.identityHashCode(this);
        }
        return name;
    }

    public String getNameInternal() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPoolPreparedStatements() {
        return poolPreparedStatements;
    }

    public void setPoolPreparedStatements(boolean poolPreparedStatements) {
        if (inited) {
            throw new UnsupportedOperationException();
        }

        this.poolPreparedStatements = poolPreparedStatements;
    }

    public long getMaxWait() {
        return maxWait;
    }

    public void setMaxWait(long maxWait) {
        if (inited) {
            throw new UnsupportedOperationException();
        }

        this.maxWait = maxWait;
    }

    public int getMinIdle() {
        return minIdle;
    }

    public void setMinIdle(int minIdle) {
        if (inited) {
            throw new UnsupportedOperationException();
        }

        this.minIdle = minIdle;
    }

    public int getMaxIdle() {
        return maxIdle;
    }

    public void setMaxIdle(int maxIdle) {
        if (inited) {
            throw new UnsupportedOperationException();
        }

        this.maxIdle = maxIdle;
    }

    public int getInitialSize() {
        return initialSize;
    }

    public void setInitialSize(int initialSize) {
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

    public void setMaxActive(int maxActive) {
        if (inited) {
            throw new UnsupportedOperationException();
        }

        this.maxActive = maxActive;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String user) {
        if (inited) {
            throw new UnsupportedOperationException();
        }

        this.username = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        if (inited) {
            throw new UnsupportedOperationException();
        }

        this.password = password;
    }

    public Properties getConnectProperties() {
        return connectionProperties;
    }

    public void setConnectProperties(Properties connectionProperties) {
        if (inited) {
            throw new UnsupportedOperationException();
        }

        this.connectionProperties = connectionProperties;
    }

    public void setConnectionProperties(String connectionProperties) {
        if (inited) {
            throw new UnsupportedOperationException();
        }

        if (connectionProperties == null) {
            this.connectionProperties.clear();
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
        this.connectionProperties = properties;
    }

    public String getUrl() {
        return jdbcUrl;
    }

    public String getRawJdbcUrl() {
        return jdbcUrl;
    }

    public void setUrl(String jdbcUrl) {
        if (inited) {
            throw new UnsupportedOperationException();
        }

        this.jdbcUrl = jdbcUrl;
    }

    public String getDriverClassName() {
        return driverClass;
    }

    public void setDriverClassName(String driverClass) {
        if (inited) {
            throw new UnsupportedOperationException();
        }

        this.driverClass = driverClass;
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

    protected void initConnectionFactory() throws SQLException {
        connectionFactory = createConnectionFactory();
    }

    protected ConnectionFactory createConnectionFactory() throws SQLException {
        return new DruidPoolConnectionFactory(this);
    }

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public ExceptionSorter getExceptionSoter() {
        return exceptionSoter;
    }

    public void setExceptionSoter(ExceptionSorter exceptionSoter) {
        this.exceptionSoter = exceptionSoter;
    }

    // 兼容JBOSS
    public void setExceptionSorterClassName(String exceptionSorter) throws Exception {
        this.setExceptionSorter(exceptionSorter);
    }

    public void setExceptionSorter(String exceptionSorter) throws Exception {
        if (exceptionSorter == null) {
            this.exceptionSoter = NullExceptionSorter.getInstance();
            return;
        }

        exceptionSorter = exceptionSorter.trim();
        if (exceptionSorter.length() == 0) {
            this.exceptionSoter = NullExceptionSorter.getInstance();
            return;
        }

        Class<?> clazz = DruidLoaderUtils.loadClass(exceptionSorter);
        this.exceptionSoter = (ExceptionSorter) clazz.newInstance();
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
        if (filters == null || filters.length() == 0) {
            return;
        }

        String[] filterArray = filters.split("\\,");

        for (String item : filterArray) {
            DruidLoaderUtils.loadFilter(this.filters, item);
        }
    }

    public void validateConnection(Connection conn) throws SQLException {
        String query = getValidationQuery();
        if (conn.isClosed()) {
            throw new SQLException("validateConnection: connection closed");
        }

        if (validConnectionChecker != null) {
            if (!validConnectionChecker.isValidConnection(conn, validationQuery, validationQueryTimeout)) {
                throw new SQLException("validateConnection false");
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
            } finally {
                JdbcUtils.close(rs);
                JdbcUtils.close(stmt);
            }
        }
    }

    protected boolean testConnectionInternal(Connection conn) {
        try {
            if (validConnectionChecker != null) {
                return validConnectionChecker.isValidConnection(conn, validationQuery, validationQueryTimeout);
            }

            String query = getValidationQuery();
            if (conn.isClosed()) {
                return false;
            }

            if (null == query) {
                return true;
            }

            Statement stmt = null;
            ResultSet rset = null;
            try {
                stmt = conn.createStatement();
                if (getValidationQueryTimeout() > 0) {
                    stmt.setQueryTimeout(getValidationQueryTimeout());
                }
                rset = stmt.executeQuery(query);
                if (!rset.next()) {
                    return false;
                }
            } finally {
                JdbcUtils.close(rset);
                JdbcUtils.close(stmt);
            }

            return true;
        } catch (SQLException ex) {
            // skip
            return false;
        }
    }

    public Set<PoolableConnection> getActiveConnections() {
        return this.activeConnections.keySet();
    }

    void removeActiveConnection(PoolableConnection conn) {
        activeConnections.remove(conn);
    }

    public List<String> getActiveConnectionStackTrace() {
        List<String> list = new ArrayList<String>();
        for (ActiveConnectionTraceInfo traceInfo : this.activeConnections.values()) {
            StringBuilder buf = new StringBuilder();
            for (StackTraceElement item : traceInfo.getStackTrace()) {
                buf.append(item.toString());
                buf.append("\n");
            }
            list.add(buf.toString());
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

    private final AtomicLong connectionIdSeed  = new AtomicLong(10000);
    private final AtomicLong statementIdSeed   = new AtomicLong(20000);
    private final AtomicLong resultSetIdSeed   = new AtomicLong(50000);
    private final AtomicLong transactionIdSeed = new AtomicLong(50000);

    public long createConnectionId() {
        return connectionIdSeed.incrementAndGet();
    }

    public long createStatementId() {
        return statementIdSeed.getAndIncrement();
    }

    public long createResultSetId() {
        return resultSetIdSeed.getAndIncrement();
    }

    @Override
    public long createTransactionId() {
        return transactionIdSeed.getAndIncrement();
    }

    void initStatement(Statement stmt) throws SQLException {
        if (queryTimeout > 0) {
            stmt.setQueryTimeout(queryTimeout);
        }
    }

    public abstract void handleConnectionException(PoolableConnection pooledConnection, Throwable t)
                                                                                                    throws SQLException;

    protected abstract void recycle(PoolableConnection pooledConnection) throws SQLException;

    abstract void incrementCreateCount();

    public static class DruidPoolConnectionFactory implements ConnectionFactory {

        private final String                  url;
        private final Properties              info;
        private final DruidAbstractDataSource dataSource;

        public DruidPoolConnectionFactory(DruidAbstractDataSource dataSource) throws SQLException{
            this.dataSource = dataSource;
            this.url = dataSource.getUrl();

            Properties properties = dataSource.getConnectProperties();
            String user;
            if (dataSource.getUserCallback() != null) {
                user = dataSource.getUserCallback().getName();
            } else {
                user = dataSource.getUsername();
            }

            String password;
            PasswordCallback passwordCallback = dataSource.getPasswordCallback();
            if (passwordCallback != null) {
                try {
                    Method method = passwordCallback.getClass().getMethod("setUrl", String.class);
                    method.invoke(passwordCallback, url);
                } catch (NoSuchMethodException ex) {
                    // skip
                } catch (IllegalAccessException e) {
                    throw new SQLException("passwordCallback Error", e);
                } catch (InvocationTargetException e) {
                    throw new SQLException("passwordCallback Error", e);
                }

                try {
                    Method method = passwordCallback.getClass().getMethod("setProperties", Properties.class);
                    method.invoke(passwordCallback, properties);
                } catch (NoSuchMethodException ex) {
                    // skip
                } catch (IllegalAccessException e) {
                    throw new SQLException("passwordCallback Error", e);
                } catch (InvocationTargetException e) {
                    throw new SQLException("passwordCallback Error", e);
                }

                char[] chars = passwordCallback.getPassword();
                if (chars != null) {
                    password = new String(chars);
                } else {
                    password = null;
                }
            } else {
                password = dataSource.getPassword();
            }

            this.info = new Properties(dataSource.getConnectProperties());

            if (properties != null) {
                info.putAll(properties);
            }

            if ((!info.contains("user")) && user != null) {
                info.put("user", user);
            }

            if ((!info.contains("password")) && password != null) {
                info.put("password", password);
            }
        }

        public String getUrl() {
            return url;
        }

        public Properties getInfo() {
            return info;
        }

        @Override
        public Connection createConnection() throws SQLException {
            Connection conn;

            long startNano = System.nanoTime();

            try {
                if (dataSource.getProxyFilters().size() != 0) {
                    conn = new FilterChainImpl(dataSource).connection_connect(info);
                } else {
                    conn = dataSource.getDriver().connect(url, info);

                    if (conn == null) {
                        throw new SQLException("connect error, url " + url);
                    }
                }

                conn.setAutoCommit(dataSource.isDefaultAutoCommit());
                if (dataSource.getDefaultReadOnly() != null) {
                    conn.setReadOnly(dataSource.getDefaultReadOnly());
                }

                if (dataSource.getDefaultTransactionIsolation() != null) {
                    conn.setTransactionIsolation(dataSource.getDefaultTransactionIsolation());
                }

                if (dataSource.getDefaultCatalog() != null && dataSource.getDefaultCatalog().length() != 0) {
                    conn.setCatalog(dataSource.getDefaultCatalog());
                }

                dataSource.validateConnection(conn);
            } catch (SQLException ex) {
                dataSource.createErrorCount++;
                throw ex;
            } catch (RuntimeException ex) {
                dataSource.createErrorCount++;
                throw ex;
            } catch (Error ex) {
                dataSource.createErrorCount++;
                throw ex;
            } finally {
                long nano = System.nanoTime() - startNano;
                dataSource.createTimespan += nano;
            }

            dataSource.incrementCreateCount();

            return conn;
        }
    }

    public CompositeDataSupport getCompositeData() throws JMException {
        StatFilter statFilter = null;
        JdbcDataSourceStat stat = null;
        for (Filter filter : this.getProxyFilters()) {
            if (filter instanceof StatFilter) {
                statFilter = (StatFilter) filter;
            }
        }
        if (statFilter != null) {
            stat = statFilter.getDataSourceStat();
        }

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

        if (stat != null) {
            // 0 - 4
            map.put("ConnectionActiveCount", stat.getConnectionActiveCount());
            map.put("ConnectionActiveCountMax", stat.getConnectionStat().getActiveMax());
            map.put("ConnectionCloseCount", stat.getConnectionStat().getCloseCount());
            map.put("ConnectionCommitCount", stat.getConnectionStat().getCommitCount());
            map.put("ConnectionRollbackCount", stat.getConnectionStat().getRollbackCount());

            // 5 - 9
            map.put("ConnectionConnectLastTime", stat.getConnectionStat().getConnectLastTime());
            map.put("ConnectionConnectErrorCount", stat.getConnectionStat().getConnectErrorCount());
            Throwable lastConnectionConnectError = stat.getConnectionStat().getConnectErrorLast();
            if (lastConnectionConnectError != null) {
                map.put("ConnectionConnectErrorLastTime", stat.getConnectionStat().getErrorLastTime());
                map.put("ConnectionConnectErrorLastMessage", lastConnectionConnectError.getMessage());
                map.put("ConnectionConnectErrorLastStackTrace", JdbcUtils.getStackTrace(lastConnectionConnectError));
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

            // 15 -24
            map.put("StatementConcurrentMax", stat.getStatementStat().getConcurrentMax());
            map.put("StatementCloseCount", stat.getStatementStat().getCloseCount());
            map.put("StatementErrorCount", stat.getStatementStat().getErrorCount());
            Throwable lastStatementError = stat.getStatementStat().getLastException();
            if (lastStatementError != null) {
                map.put("StatementLastErrorTime", stat.getStatementStat().getLastErrorTime());
                map.put("StatementLastErrorMessage", lastStatementError.getMessage());

                map.put("StatementLastErrorStackTrace", JdbcUtils.getStackTrace(lastStatementError));
            } else {
                map.put("StatementLastErrorTime", null);
                map.put("StatementLastErrorMessage", null);

                map.put("StatementLastErrorStackTrace", null);
            }
            map.put("StatementExecuteMillisTotal", stat.getStatementStat().getMillisTotal());
            map.put("StatementExecuteLastTime", stat.getStatementStat().getExecuteLastTime());
            map.put("ConnectionConnectingCount", stat.getConnectionStat().getConnectingCount());
            map.put("ResultSetCloseCount", stat.getResultSetStat().getCloseCount());

            // 25 - 29
            map.put("ResultSetOpenCount", stat.getResultSetStat().getOpenCount());
            map.put("ResultSetOpenningCount", stat.getResultSetStat().getOpenningCount());
            map.put("ResultSetOpenningMax", stat.getResultSetStat().getOpenningMax());
            map.put("ResultSetFetchRowCount", stat.getResultSetStat().getFetchRowCount());
            map.put("ResultSetLastOpenTime", stat.getResultSetStat().getLastOpenTime());

            // 30 - 34
            map.put("ResultSetErrorCount", stat.getResultSetStat().getErrorCount());
            map.put("ResultSetOpenningMillisTotal", stat.getResultSetStat().getAliveMillisTotal());
            map.put("ResultSetLastErrorTime", stat.getResultSetStat().getLastErrorTime());
            Throwable lastResultSetError = stat.getResultSetStat().getLastError();
            if (lastResultSetError != null) {
                map.put("ResultSetLastErrorMessage", lastResultSetError.getMessage());
                map.put("ResultSetLastErrorStackTrace", JdbcUtils.getStackTrace(lastResultSetError));
            } else {
                map.put("ResultSetLastErrorMessage", null);
                map.put("ResultSetLastErrorStackTrace", null);
            }

            // 35 - 39
            map.put("ConnectionConnectCount", stat.getConnectionStat().getConnectCount());
            Throwable lastConnectionError = stat.getConnectionStat().getErrorLast();
            if (lastConnectionError != null) {
                map.put("ConnectionErrorLastMessage", lastConnectionError.getMessage());
                map.put("ConnectionErrorLastStackTrace", JdbcUtils.getStackTrace(lastConnectionError));
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

            map.put("ConnectionCount_Alive_0_1_Seconds", stat.getConnectionCount_Alive_0_1_Seconds());
            map.put("ConnectionCount_Alive_1_5_Seconds", stat.getConnectionCount_Alive_1_5_Seconds());
            map.put("ConnectionCount_Alive_5_10_Seconds", stat.getConnectionCount_Alive_5_10_Seconds());
            map.put("ConnectionCount_Alive_10_30_Seconds", stat.getConnectionCount_Alive_10_30_Seconds());
            map.put("ConnectionCount_Alive_30_60_Seconds", stat.getConnectionCount_Alive_30_60_Seconds());

            map.put("ConnectionCount_Alive_1_5_Minutes", stat.getConnectionCount_Alive_1_5_Minutes());
            map.put("ConnectionCount_Alive_5_10_Minutes", stat.getConnectionCount_Alive_5_10_Minutes());
            map.put("ConnectionCount_Alive_10_30_Minutes", stat.getConnectionCount_Alive_10_30_Minutes());
            map.put("ConnectionCount_Alive_30_60_Minutes", stat.getConnectionCount_Alive_30_60_Minutes());
            map.put("ConnectionCount_Alive_1_6_Hours", stat.getConnectionCount_Alive_1_6_Hours());

            map.put("ConnectionCount_Alive_6_24_Hours", stat.getConnectionCount_Alive_6_24_Hours());
            map.put("ConnectionCount_Alive_1_7_Day", stat.getConnectionCount_Alive_1_7_day());
            map.put("ConnectionCount_Alive_7_30_Day", stat.getConnectionCount_Alive_7_30_Day());
            map.put("ConnectionCount_Alive_30_90_Day", stat.getConnectionCount_Alive_30_90_Day());
            map.put("ConnectionCount_Alive_90_more_Day", stat.getConnectionCount_Alive_90_more_Day());

            map.put("StatementExecuteCount_0_1_Millis", stat.getStatementStat().getCount_0_1_Millis());
            map.put("StatementExecuteCount_1_2_Millis", stat.getStatementStat().getCount_1_2_Millis());
            map.put("StatementExecuteCount_2_5_Millis", stat.getStatementStat().getCount_2_5_Millis());
            map.put("StatementExecuteCount_5_10_Millis", stat.getStatementStat().getCount_5_10_Millis());
            map.put("StatementExecuteCount_10_20_Millis", stat.getStatementStat().getCount_10_20_Millis());

            map.put("StatementExecuteCount_20_50_Millis", stat.getStatementStat().getCount_20_50_Millis());
            map.put("StatementExecuteCount_50_100_Millis", stat.getStatementStat().getCount_50_100_Millis());
            map.put("StatementExecuteCount_100_200_Millis", stat.getStatementStat().getCount_100_200_Millis());
            map.put("StatementExecuteCount_200_500_Millis", stat.getStatementStat().getCount_200_500_Millis());
            map.put("StatementExecuteCount_500_1000_Millis", stat.getStatementStat().getCount_500_1000_Millis());

            map.put("StatementExecuteCount_1_2_Seconds", stat.getStatementStat().getCount_1_2_Seconds());
            map.put("StatementExecuteCount_2_5_Seconds", stat.getStatementStat().getCount_2_5_Seconds());
            map.put("StatementExecuteCount_5_10_Seconds", stat.getStatementStat().getCount_5_10_Seconds());
            map.put("StatementExecuteCount_10_30_Seconds", stat.getStatementStat().getCount_10_30_Seconds());
            map.put("StatementExecuteCount_30_60_Seconds", stat.getStatementStat().getCount_30_60_Seconds());

            map.put("StatementExecuteCount_1_2_Minutes", stat.getStatementStat().getCount_1_2_minutes());
            map.put("StatementExecuteCount_2_5_Minutes", stat.getStatementStat().getCount_2_5_minutes());
            map.put("StatementExecuteCount_5_10_Minutes", stat.getStatementStat().getCount_5_10_minutes());
            map.put("StatementExecuteCount_10_30_Minutes", stat.getStatementStat().getCount_10_30_minutes());
            map.put("StatementExecuteCount_30_more_Minutes", stat.getStatementStat().getCount_30_more_minutes());
        } else {
            // 0 - 4
            map.put("ConnectionActiveCount", null);
            map.put("ConnectionActiveCountMax", null);
            map.put("ConnectionCloseCount", null);
            map.put("ConnectionCommitCount", null);
            map.put("ConnectionRollbackCount", null);

            // 5 - 9
            map.put("ConnectionConnectLastTime", null);
            map.put("ConnectionConnectErrorCount", null);
            map.put("ConnectionConnectErrorLastTime", null);
            map.put("ConnectionConnectErrorLastMessage", null);
            map.put("ConnectionConnectErrorLastStackTrace", null);

            // 10 - 14
            map.put("StatementCreateCount", null);
            map.put("StatementPrepareCount", null);
            map.put("StatementPreCallCount", null);
            map.put("StatementExecuteCount", null);
            map.put("StatementRunningCount", null);

            // 15 - 19
            map.put("StatementConcurrentMax", null);
            map.put("StatementCloseCount", null);
            map.put("StatementErrorCount", null);
            map.put("StatementLastErrorTime", null);
            map.put("StatementLastErrorMessage", null);

            // 20 - 24
            map.put("StatementLastErrorStackTrace", null);
            map.put("StatementExecuteMillisTotal", null);
            map.put("ConnectionConnectingCount", null);
            map.put("StatementExecuteLastTime", null);
            map.put("ResultSetCloseCount", null);

            // 25 - 29
            map.put("ResultSetOpenCount", null);
            map.put("ResultSetOpenningCount", null);
            map.put("ResultSetOpenningMax", null);
            map.put("ResultSetFetchRowCount", null);
            map.put("ResultSetLastOpenTime", null);

            // 30 - 34
            map.put("ResultSetErrorCount", null);
            map.put("ResultSetOpenningMillisTotal", null);
            map.put("ResultSetLastErrorTime", null);
            map.put("ResultSetLastErrorMessage", null);
            map.put("ResultSetLastErrorStackTrace", null);

            map.put("ConnectionConnectCount", null);
            map.put("ConnectionErrorLastMessage", null);
            map.put("ConnectionErrorLastStackTrace", null);
            map.put("ConnectionConnectMillisTotal", null);
            map.put("ConnectionConnectingCountMax", null);

            map.put("ConnectionConnectMillisMax", null);
            map.put("ConnectionErrorLastTime", null);
            map.put("ConnectionAliveMillisMax", null);
            map.put("ConnectionAliveMillisMin", null);

            map.put("ConnectionCount_Alive_0_1_Seconds", null);
            map.put("ConnectionCount_Alive_1_5_Seconds", null);
            map.put("ConnectionCount_Alive_5_10_Seconds", null);
            map.put("ConnectionCount_Alive_10_30_Seconds", null);
            map.put("ConnectionCount_Alive_30_60_Seconds", null);

            map.put("ConnectionCount_Alive_1_5_Minutes", null);
            map.put("ConnectionCount_Alive_5_10_Minutes", null);
            map.put("ConnectionCount_Alive_10_30_Minutes", null);
            map.put("ConnectionCount_Alive_30_60_Minutes", null);
            map.put("ConnectionCount_Alive_1_6_Hours", null);

            map.put("ConnectionCount_Alive_6_24_Hours", null);
            map.put("ConnectionCount_Alive_1_7_Day", null);
            map.put("ConnectionCount_Alive_7_30_Day", null);
            map.put("ConnectionCount_Alive_30_90_Day", null);
            map.put("ConnectionCount_Alive_90_more_Day", null);

            map.put("StatementExecuteCount_0_1_Millis", null);
            map.put("StatementExecuteCount_1_2_Millis", null);
            map.put("StatementExecuteCount_2_5_Millis", null);
            map.put("StatementExecuteCount_5_10_Millis", null);
            map.put("StatementExecuteCount_10_20_Millis", null);

            map.put("StatementExecuteCount_20_50_Millis", null);
            map.put("StatementExecuteCount_50_100_Millis", null);
            map.put("StatementExecuteCount_100_200_Millis", null);
            map.put("StatementExecuteCount_200_500_Millis", null);
            map.put("StatementExecuteCount_500_1000_Millis", null);

            map.put("StatementExecuteCount_1_2_Seconds", null);
            map.put("StatementExecuteCount_2_5_Seconds", null);
            map.put("StatementExecuteCount_5_10_Seconds", null);
            map.put("StatementExecuteCount_10_30_Seconds", null);
            map.put("StatementExecuteCount_30_60_Seconds", null);

            map.put("StatementExecuteCount_1_2_Minutes", null);
            map.put("StatementExecuteCount_2_5_Minutes", null);
            map.put("StatementExecuteCount_5_10_Minutes", null);
            map.put("StatementExecuteCount_10_30_Minutes", null);
            map.put("StatementExecuteCount_30_more_Minutes", null);
        }

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
}
