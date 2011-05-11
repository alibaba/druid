/*
 * Copyright 2011 Alibaba Group.
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
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.sql.DataSource;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public abstract class DruidDataAbstractSource implements DataSource, DataSourceProxy, Serializable {

    private static final long   serialVersionUID         = 1L;

    public final static int     DEFAULT_INITIAL_SIZE     = 0;
    public final static int     DEFAULT_MAX_ACTIVE_SIZE  = 8;
    public final static int     DEFAULT_MAX_IDLE         = 8;
    public final static int     DEFAULT_MIN_IDLE         = 0;
    public final static int     DEFAULT_MAX_WAIT         = -1;
    public final static String  DEFAULT_VALIDATION_QUERY = "SELECT 1";
    public final static boolean DEFAULT_TEST_ON_BORROW   = true;
    public final static boolean DEFAULT_TEST_ON_RETURN   = false;
    public final static boolean DEFAULT_WHILE_IDLE       = false;

    private boolean             defaultAutoCommit        = false;
    private Boolean             defaultReadOnly;
    private Integer             defaultTransactionIsolation;
    private String              defaultCatalog           = null;

    protected String            name;

    protected String            user;
    protected String            password;
    protected String            jdbcUrl;
    protected String            driverClass;
    protected Properties        properties               = new Properties();

    protected PasswordCallback  passwordCallback;
    protected NameCallback      userCallback;

    protected ConnectionFactory connectionFactory;

    protected int               initialSize              = DEFAULT_INITIAL_SIZE;
    protected int               maxActive                = DEFAULT_MAX_ACTIVE_SIZE;
    protected int               minIdle                  = DEFAULT_MIN_IDLE;
    protected int               maxIdle                  = DEFAULT_MAX_IDLE;
    protected long              maxWait                  = DEFAULT_MAX_WAIT;

    protected String            validationQuery          = DEFAULT_VALIDATION_QUERY;
    private boolean             testOnBorrow             = DEFAULT_TEST_ON_BORROW;
    private boolean             testOnReturn             = DEFAULT_TEST_ON_RETURN;
    private boolean             testWhileIdle            = DEFAULT_WHILE_IDLE;
    protected boolean           poolPreparedStatements   = false;

    protected boolean           inited                   = false;

    protected PrintWriter       logWriter                = new PrintWriter(System.out);

    private final List<Filter>  filters                  = new ArrayList<Filter>();

    protected Driver            driver;

    protected int               queryTimeout;

    protected long              createErrorCount;

    protected long              createTimespan;

    public String getValidationQuery() {
        return validationQuery;
    }

    public void setValidationQuery(String validationQuery) {
        this.validationQuery = validationQuery;
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
        return name;
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
        return user;
    }

    public void setUsername(String user) {
        if (inited) {
            throw new UnsupportedOperationException();
        }

        this.user = user;
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

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        if (inited) {
            throw new UnsupportedOperationException();
        }

        this.properties = properties;
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
    public PrintWriter getLogWriter() throws SQLException {
        return logWriter;
    }

    @Override
    public void setLogWriter(PrintWriter out) throws SQLException {
        this.logWriter = out;
    }

    @Override
    public void setLoginTimeout(int seconds) throws SQLException {

    }

    @Override
    public int getLoginTimeout() throws SQLException {
        return 0;
    }

    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return false;
    }

    protected void initConnectionFactory() {
        connectionFactory = new DruidPoolConnectionFactory(this);
    }

    public Driver getDriver() {
        return driver;
    }

    @Override
    public List<Filter> getFilters() {
        return filters;
    }

    public long getCreateTimespanNano() {
        return createTimespan;
    }

    public long getCreateTimespanMillis() {
        return createTimespan / (1000 * 1000);
    }

    abstract void incrementCreateCount();

    public static class DruidPoolConnectionFactory implements ConnectionFactory {

        private final String                  url;
        private final Properties              info;
        private final DruidDataAbstractSource dataSource;

        public DruidPoolConnectionFactory(DruidDataAbstractSource dataSource){
            this.dataSource = dataSource;
            this.url = dataSource.getUrl();

            Properties properties = dataSource.getProperties();
            String user;
            if (dataSource.getUserCallback() != null) {
                user = dataSource.getUserCallback().getName();
            } else {
                user = dataSource.getUsername();
            }

            String password;
            if (dataSource.getPasswordCallback() != null) {
                password = new String(dataSource.getPasswordCallback().getPassword());
            } else {
                password = dataSource.getPassword();
            }

            this.info = new Properties(dataSource.getProperties());

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
                if (dataSource.getFilters().size() != 0) {
                    conn = new FilterChainImpl(dataSource).connection_connect(info);
                } else {
                    conn = dataSource.getDriver().connect(url, info);
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
}
