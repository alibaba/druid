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
package com.alibaba.druid.wall;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.druid.DruidRuntimeException;
import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import com.alibaba.druid.wall.spi.PGWallProvider;
import com.alibaba.druid.wall.spi.SQLServerWallProvider;
import com.alibaba.druid.wall.violation.SyntaxErrorViolation;

public class WallFilter extends FilterAdapter implements WallFilterMBean {

    private final static Log LOG            = LogFactory.getLog(WallFilter.class);

    private boolean          inited         = false;

    private WallProvider     provider;

    private String           dbType;

    private WallConfig       config;

    private volatile boolean logViolation   = false;
    private volatile boolean throwException = true;

    // stats
    private final AtomicLong violationCount = new AtomicLong();

    @Override
    public void init(DataSourceProxy dataSource) {
        if (this.dbType == null || this.dbType.trim().length() == 0) {
            if (dataSource != null && dataSource.getDbType() != null) {
                this.dbType = dataSource.getDbType();
            } else {
                this.dbType = JdbcUtils.getDbType(dataSource.getRawJdbcUrl(), "");
            }
        }

        if (dbType == null) {
            dbType = JdbcUtils.getDbType(dataSource.getUrl(), null);
        }

        if (JdbcUtils.MYSQL.equals(dbType) || JdbcUtils.H2.equals(dbType)) {
            if (config == null) {
                config = new WallConfig(MySqlWallProvider.DEFAULT_CONFIG_DIR);
            }

            provider = new MySqlWallProvider(config);
        } else if (JdbcUtils.ORACLE.equals(dbType) || JdbcUtils.ALI_ORACLE.equals(dbType)) {
            if (config == null) {
                config = new WallConfig(OracleWallProvider.DEFAULT_CONFIG_DIR);
            }

            provider = new OracleWallProvider(config);
        } else if (JdbcUtils.SQL_SERVER.equals(dbType) || JdbcUtils.JTDS.equals(dbType)) {
            if (config == null) {
                config = new WallConfig(SQLServerWallProvider.DEFAULT_CONFIG_DIR);
            }

            provider = new SQLServerWallProvider(config);
        } else if (JdbcUtils.POSTGRESQL.equals(dbType)) {
            if (config == null) {
                config = new WallConfig(PGWallProvider.DEFAULT_CONFIG_DIR);
            }

            provider = new PGWallProvider(config);
        } else {
            throw new IllegalStateException("dbType not support : " + dbType + ", url " + dataSource.getUrl());
        }

        this.inited = true;
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public boolean isLogViolation() {
        return logViolation;
    }

    public void setLogViolation(boolean logViolation) {
        this.logViolation = logViolation;
    }

    public boolean isThrowException() {
        return throwException;
    }

    public void setThrowException(boolean throwException) {
        this.throwException = throwException;
    }

    public void clearProviderCache() {
        if (provider != null) {
            provider.clearCache();
        }
    }

    public Set<String> getProviderWhiteList() {
        if (provider == null) {
            return Collections.emptySet();
        }

        return provider.getWhiteList();
    }

    public WallProvider getProvider() {
        return provider;
    }

    public WallConfig getConfig() {
        return config;
    }

    public void setConfig(WallConfig config) {
        this.config = config;
    }

    public boolean isInited() {
        return inited;
    }

    public void checkInit() {
        if (inited) {
            throw new DruidRuntimeException("wall filter is inited");
        }
    }

    @Override
    public void statement_addBatch(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        String dbType = statement.getConnectionProxy().getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            chain.statement_addBatch(statement, sql);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection, String sql)
                                                                                                                        throws SQLException {
        String dbType = connection.getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            return chain.connection_prepareStatement(connection, sql);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int autoGeneratedKeys) throws SQLException {
        String dbType = connection.getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            return chain.connection_prepareStatement(connection, sql, autoGeneratedKeys);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int resultSetType, int resultSetConcurrency)
                                                                                                                      throws SQLException {
        String dbType = connection.getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            return chain.connection_prepareStatement(connection, sql, resultSetType, resultSetConcurrency);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int resultSetType, int resultSetConcurrency,
                                                              int resultSetHoldability) throws SQLException {
        String dbType = connection.getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            return chain.connection_prepareStatement(connection, sql, resultSetType, resultSetConcurrency,
                                                     resultSetHoldability);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int[] columnIndexes) throws SQLException {
        String dbType = connection.getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            return chain.connection_prepareStatement(connection, sql, columnIndexes);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, String[] columnNames) throws SQLException {
        String dbType = connection.getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            return chain.connection_prepareStatement(connection, sql, columnNames);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql)
                                                                                                                   throws SQLException {
        String dbType = connection.getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            return chain.connection_prepareCall(connection, sql);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql,
                                                         int resultSetType, int resultSetConcurrency)
                                                                                                     throws SQLException {
        String dbType = connection.getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            return chain.connection_prepareCall(connection, sql, resultSetType, resultSetConcurrency);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql,
                                                         int resultSetType, int resultSetConcurrency,
                                                         int resultSetHoldability) throws SQLException {
        String dbType = connection.getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            return chain.connection_prepareCall(connection, sql, resultSetType, resultSetConcurrency,
                                                resultSetHoldability);
        } finally {
            WallContext.clearContext();
        }
    }

    // //////////////

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        String dbType = statement.getConnectionProxy().getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            return chain.statement_execute(statement, sql);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, int autoGeneratedKeys)
                                                                                                                    throws SQLException {
        String dbType = statement.getConnectionProxy().getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            return chain.statement_execute(statement, sql, autoGeneratedKeys);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, int columnIndexes[])
                                                                                                                  throws SQLException {
        String dbType = statement.getConnectionProxy().getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            return chain.statement_execute(statement, sql, columnIndexes);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, String columnNames[])
                                                                                                                   throws SQLException {
        String dbType = statement.getConnectionProxy().getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            return chain.statement_execute(statement, sql, columnNames);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public ResultSetProxy statement_executeQuery(FilterChain chain, StatementProxy statement, String sql)
                                                                                                         throws SQLException {
        String dbType = statement.getConnectionProxy().getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            return chain.statement_executeQuery(statement, sql);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        String dbType = statement.getConnectionProxy().getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            return chain.statement_executeUpdate(statement, sql);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, int autoGeneratedKeys)
                                                                                                                      throws SQLException {
        String dbType = statement.getConnectionProxy().getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            return chain.statement_executeUpdate(statement, sql, autoGeneratedKeys);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, int columnIndexes[])
                                                                                                                    throws SQLException {
        String dbType = statement.getConnectionProxy().getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            return chain.statement_executeUpdate(statement, sql, columnIndexes);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, String columnNames[])
                                                                                                                     throws SQLException {
        String dbType = statement.getConnectionProxy().getDirectDataSource().getDbType();
        WallContext.createIfNotExists(dbType);
        try {
            sql = check(sql);
            return chain.statement_executeUpdate(statement, sql, columnNames);
        } finally {
            WallContext.clearContext();
        }
    }

    public String check(String sql) throws SQLException {
        WallCheckResult checkResult = provider.check(sql);
        List<Violation> violations = checkResult.getViolations();

        if (violations.size() > 0) {
            violationCount.incrementAndGet();

            Violation firstViolation = violations.get(0);
            if (isLogViolation()) {
                LOG.error("sql injection violation, " + firstViolation.getMessage() + " : " + sql);
            }

            if (throwException) {
                if (violations.get(0) instanceof SyntaxErrorViolation) {
                    SyntaxErrorViolation violation = (SyntaxErrorViolation) violations.get(0);
                    throw new SQLException("sql injection violation, " + firstViolation.getMessage() + " : " + sql,
                                           violation.getException());
                } else {
                    throw new SQLException("sql injection violation, " + firstViolation.getMessage() + " : " + sql);
                }
            }
        }

        return sql;
    }

    @Override
    public boolean isWrapperFor(FilterChain chain, Wrapper wrapper, Class<?> iface) throws SQLException {
        if (config.isDoPrivilegedAllow() && WallProvider.ispPivileged()) {
            return chain.isWrapperFor(wrapper, iface);
        }

        if (!this.provider.getConfig().isWrapAllow()) {
            return false;
        }
        return chain.isWrapperFor(wrapper, iface);
    }

    @Override
    public <T> T unwrap(FilterChain chain, Wrapper wrapper, Class<T> iface) throws SQLException {
        if (config.isDoPrivilegedAllow() && WallProvider.ispPivileged()) {
            return chain.unwrap(wrapper, iface);
        }

        if (!this.provider.getConfig().isWrapAllow()) {
            violationCount.incrementAndGet();
            return null;
        }

        return chain.unwrap(wrapper, iface);
    }

    @Override
    public DatabaseMetaData connection_getMetaData(FilterChain chain, ConnectionProxy connection) throws SQLException {
        if (config.isDoPrivilegedAllow() && WallProvider.ispPivileged()) {
            return chain.connection_getMetaData(connection);
        }

        if (!this.provider.getConfig().isMetadataAllow()) {
            violationCount.incrementAndGet();
            if (isLogViolation()) {
                LOG.error("not support method : Connection.getMetdataData");
            }

            if (throwException) {
                throw new WallSQLException("not support method : Connection.getMetdataData");
            } else {

            }
        }

        return chain.connection_getMetaData(connection);
    }

    public long getViolationCount() {
        return this.violationCount.get();
    }

    public void resetViolationCount() {
        this.violationCount.set(0);
    }

    public void clearWhiteList() {
        this.provider.clearCache();
    }

    public boolean checkValid(String sql) {
        return provider.checkValid(sql);
    }
}
