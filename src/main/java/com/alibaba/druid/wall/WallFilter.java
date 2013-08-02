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

import static com.alibaba.druid.util.IOUtils.getBoolean;

import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.sql.Wrapper;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.Set;

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
import com.alibaba.druid.wall.spi.DB2WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import com.alibaba.druid.wall.spi.PGWallProvider;
import com.alibaba.druid.wall.spi.SQLServerWallProvider;
import com.alibaba.druid.wall.violation.SyntaxErrorViolation;

public class WallFilter extends FilterAdapter implements WallFilterMBean {

    private final static Log   LOG                       = LogFactory.getLog(WallFilter.class);

    private boolean            inited                    = false;

    private WallProvider       provider;

    private String             dbType;

    private WallConfig         config;

    private volatile boolean   logViolation              = false;
    private volatile boolean   throwException            = true;

    private boolean            autoRegisterToGlobalTimer = false;

    public final static String ATTR_SQL_STAT             = "wall.sqlStat";

    public WallFilter(){
        configFromProperties(System.getProperties());
    }

    @Override
    public void configFromProperties(Properties properties) {
        {
            Boolean value = getBoolean(properties, "druid.wall.logViolation");
            if (value != null) {
                this.logViolation = value;
            }
        }
        {
            Boolean value = getBoolean(properties, "druid.wall.throwException");
            if (value != null) {
                this.throwException = value;
            }
        }
        {
            Boolean value = getBoolean(properties, "druid.wall.autoRegisterToGlobalTimer");
            if (value != null) {
                this.autoRegisterToGlobalTimer = value;
            }
        }
    }

    @Override
    public synchronized void init(DataSourceProxy dataSource) {
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

        if (JdbcUtils.MYSQL.equals(dbType) || //
            JdbcUtils.MARIADB.equals(dbType) || //
            JdbcUtils.H2.equals(dbType)) {
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
        } else if (JdbcUtils.DB2.equals(dbType)) {
            if (config == null) {
                config = new WallConfig(DB2WallProvider.DEFAULT_CONFIG_DIR);
            }

            provider = new DB2WallProvider(config);
        } else {
            throw new IllegalStateException("dbType not support : " + dbType + ", url " + dataSource.getUrl());
        }

        if (autoRegisterToGlobalTimer) {
            WallProviderStatTimer timer = WallProviderStatTimer.getInstance();
            if (timer == null) {
                timer = new WallProviderStatTimer();
                timer.start();
            }
            timer.register(provider);
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

    @Override
    public void statement_addBatch(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        createWallContext(statement);
        try {
            sql = check(sql);
            chain.statement_addBatch(statement, sql);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public void preparedStatement_addBatch(FilterChain chain, PreparedStatementProxy statement) throws SQLException {
        chain.preparedStatement_addBatch(statement);
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection, String sql)
                                                                                                                        throws SQLException {
        String dbType = connection.getDirectDataSource().getDbType();
        WallContext.create(dbType);
        try {
            sql = check(sql);
            PreparedStatementProxy stmt = chain.connection_prepareStatement(connection, sql);
            setSqlStatAttribute(stmt);
            return stmt;
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int autoGeneratedKeys) throws SQLException {
        String dbType = connection.getDirectDataSource().getDbType();
        WallContext.create(dbType);
        try {
            sql = check(sql);
            PreparedStatementProxy stmt = chain.connection_prepareStatement(connection, sql, autoGeneratedKeys);
            setSqlStatAttribute(stmt);
            return stmt;
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int resultSetType, int resultSetConcurrency)
                                                                                                                      throws SQLException {
        String dbType = connection.getDirectDataSource().getDbType();
        WallContext.create(dbType);
        try {
            sql = check(sql);
            PreparedStatementProxy stmt = chain.connection_prepareStatement(connection, sql, resultSetType,
                                                                            resultSetConcurrency);
            setSqlStatAttribute(stmt);
            return stmt;
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int resultSetType, int resultSetConcurrency,
                                                              int resultSetHoldability) throws SQLException {
        String dbType = connection.getDirectDataSource().getDbType();
        WallContext.create(dbType);
        try {
            sql = check(sql);
            PreparedStatementProxy stmt = chain.connection_prepareStatement(connection, sql, resultSetType,
                                                                            resultSetConcurrency, resultSetHoldability);
            setSqlStatAttribute(stmt);
            return stmt;
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int[] columnIndexes) throws SQLException {
        String dbType = connection.getDirectDataSource().getDbType();
        WallContext.create(dbType);
        try {
            sql = check(sql);
            PreparedStatementProxy stmt = chain.connection_prepareStatement(connection, sql, columnIndexes);
            setSqlStatAttribute(stmt);
            return stmt;
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, String[] columnNames) throws SQLException {
        String dbType = connection.getDirectDataSource().getDbType();
        WallContext.create(dbType);
        try {
            sql = check(sql);
            PreparedStatementProxy stmt = chain.connection_prepareStatement(connection, sql, columnNames);
            setSqlStatAttribute(stmt);
            return stmt;
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql)
                                                                                                                   throws SQLException {
        String dbType = connection.getDirectDataSource().getDbType();
        WallContext.create(dbType);
        try {
            sql = check(sql);
            CallableStatementProxy stmt = chain.connection_prepareCall(connection, sql);
            setSqlStatAttribute(stmt);
            return stmt;
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql,
                                                         int resultSetType, int resultSetConcurrency)
                                                                                                     throws SQLException {
        String dbType = connection.getDirectDataSource().getDbType();
        WallContext.create(dbType);
        try {
            sql = check(sql);
            CallableStatementProxy stmt = chain.connection_prepareCall(connection, sql, resultSetType,
                                                                       resultSetConcurrency);
            setSqlStatAttribute(stmt);
            return stmt;
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql,
                                                         int resultSetType, int resultSetConcurrency,
                                                         int resultSetHoldability) throws SQLException {
        String dbType = connection.getDirectDataSource().getDbType();
        WallContext.create(dbType);
        try {
            sql = check(sql);
            CallableStatementProxy stmt = chain.connection_prepareCall(connection, sql, resultSetType,
                                                                       resultSetConcurrency, resultSetHoldability);
            setSqlStatAttribute(stmt);
            return stmt;
        } finally {
            WallContext.clearContext();
        }
    }

    // //////////////

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        WallContext originalContext = WallContext.current();
        try {
            createWallContext(statement);
            sql = check(sql);
            boolean firstResult = chain.statement_execute(statement, sql);
            if (!firstResult) {
                int updateCount = statement.getUpdateCount();
                statExecuteUpdate(updateCount);
            } else {
                setSqlStatAttribute(statement);
            }
            return firstResult;
        } finally {
            if (originalContext != null) {
                WallContext.setContext(originalContext);
            }
        }
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, int autoGeneratedKeys)
                                                                                                                    throws SQLException {
        createWallContext(statement);
        try {
            sql = check(sql);
            boolean firstResult = chain.statement_execute(statement, sql, autoGeneratedKeys);
            if (!firstResult) {
                int updateCount = statement.getUpdateCount();
                statExecuteUpdate(updateCount);
            } else {
                setSqlStatAttribute(statement);
            }
            return firstResult;
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, int columnIndexes[])
                                                                                                                  throws SQLException {
        createWallContext(statement);
        try {
            sql = check(sql);
            boolean firstResult = chain.statement_execute(statement, sql, columnIndexes);
            if (!firstResult) {
                int updateCount = statement.getUpdateCount();
                statExecuteUpdate(updateCount);
            } else {
                setSqlStatAttribute(statement);
            }
            return firstResult;
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, String columnNames[])
                                                                                                                   throws SQLException {
        createWallContext(statement);
        try {
            sql = check(sql);
            boolean firstResult = chain.statement_execute(statement, sql, columnNames);
            if (!firstResult) {
                int updateCount = statement.getUpdateCount();
                statExecuteUpdate(updateCount);
            } else {
                setSqlStatAttribute(statement);
            }
            return firstResult;
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public int[] statement_executeBatch(FilterChain chain, StatementProxy statement) throws SQLException {
        WallSqlStat sqlStat = (WallSqlStat) statement.getAttribute(ATTR_SQL_STAT);
        try {
            int[] updateCounts = chain.statement_executeBatch(statement);
            int updateCount = 0;
            for (int i = 0; i < updateCounts.length; ++i) {
                updateCount += updateCounts[i];
            }

            if (sqlStat != null) {
                provider.addUpdateCount(sqlStat, updateCount);
            }

            return updateCounts;
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public ResultSetProxy statement_executeQuery(FilterChain chain, StatementProxy statement, String sql)
                                                                                                         throws SQLException {
        createWallContext(statement);
        try {
            sql = check(sql);
            return chain.statement_executeQuery(statement, sql);
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        createWallContext(statement);
        try {
            sql = check(sql);
            int updateCount = chain.statement_executeUpdate(statement, sql);
            statExecuteUpdate(updateCount);
            return updateCount;
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, int autoGeneratedKeys)
                                                                                                                      throws SQLException {
        createWallContext(statement);
        try {
            sql = check(sql);
            int updateCount = chain.statement_executeUpdate(statement, sql, autoGeneratedKeys);
            statExecuteUpdate(updateCount);
            return updateCount;
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, int columnIndexes[])
                                                                                                                    throws SQLException {
        createWallContext(statement);
        try {
            sql = check(sql);
            int updateCount = chain.statement_executeUpdate(statement, sql, columnIndexes);
            statExecuteUpdate(updateCount);
            return updateCount;
        } finally {
            WallContext.clearContext();
        }
    }

    public String getDbType(StatementProxy statement) {
        return statement.getConnectionProxy().getDirectDataSource().getDbType();
    }

    private WallContext createWallContext(StatementProxy statement) {
        String dbType = getDbType(statement);
        WallContext context = WallContext.create(dbType);
        return context;
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, String columnNames[])
                                                                                                                     throws SQLException {
        createWallContext(statement);
        try {
            sql = check(sql);
            int updateCount = chain.statement_executeUpdate(statement, sql, columnNames);
            statExecuteUpdate(updateCount);
            return updateCount;
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public boolean preparedStatement_execute(FilterChain chain, PreparedStatementProxy statement) throws SQLException {
        boolean firstResult = chain.preparedStatement_execute(statement);

        if (!firstResult) {
            WallSqlStat sqlStat = (WallSqlStat) statement.getAttribute(ATTR_SQL_STAT);
            int updateCount = statement.getUpdateCount();
            if (sqlStat != null) {
                provider.addUpdateCount(sqlStat, updateCount);
            }
        }

        return firstResult;
    }

    @Override
    public ResultSetProxy preparedStatement_executeQuery(FilterChain chain, PreparedStatementProxy statement)
                                                                                                             throws SQLException {
        return chain.preparedStatement_executeQuery(statement);
    }

    @Override
    public int preparedStatement_executeUpdate(FilterChain chain, PreparedStatementProxy statement) throws SQLException {
        int updateCount = chain.preparedStatement_executeUpdate(statement);
        WallSqlStat sqlStat = (WallSqlStat) statement.getAttribute(ATTR_SQL_STAT);
        if (sqlStat != null) {
            provider.addUpdateCount(sqlStat, updateCount);
        }
        return updateCount;
    }

    public void setSqlStatAttribute(StatementProxy stmt) {
        WallContext context = WallContext.current();
        if (context == null) {
            return;
        }

        WallSqlStat sqlStat = context.getSqlStat();
        if (sqlStat == null) {
            return;
        }

        stmt.putAttribute(ATTR_SQL_STAT, sqlStat);
    }

    public void statExecuteUpdate(int updateCount) {
        WallContext context = WallContext.current();
        if (context == null) {
            return;
        }

        WallSqlStat sqlStat = context.getSqlStat();
        if (sqlStat == null) {
            return;
        }

        if (updateCount > 0 && sqlStat != null) {
            provider.addUpdateCount(sqlStat, updateCount);
        }
    }

    public String check(String sql) throws SQLException {
        WallCheckResult checkResult = provider.check(sql);
        List<Violation> violations = checkResult.getViolations();

        if (violations.size() > 0) {
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

        String resultSql = checkResult.getSql();
        return resultSql;
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

    @Override
    public void resultSet_close(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        chain.resultSet_close(resultSet);
        int fetchRowCount = resultSet.getFetchRowCount();

        WallSqlStat sqlStat = (WallSqlStat) resultSet.getStatementProxy().getAttribute(ATTR_SQL_STAT);
        if (sqlStat == null) {
            return;
        }

        provider.addFetchRowCount(sqlStat, fetchRowCount);
    }

    public long getViolationCount() {
        return this.provider.getViolationCount();
    }

    public void resetViolationCount() {
        this.provider.reset();
    }

    public void clearWhiteList() {
        this.provider.clearCache();
    }

    public boolean checkValid(String sql) {
        return provider.checkValid(sql);
    }
}
