/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetMetaDataProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.ServletPathMatcher;
import com.alibaba.druid.util.StringUtils;
import com.alibaba.druid.wall.WallConfig.TenantCallBack;
import com.alibaba.druid.wall.WallConfig.TenantCallBack.StatementType;
import com.alibaba.druid.wall.spi.DB2WallProvider;
import com.alibaba.druid.wall.spi.MySqlWallProvider;
import com.alibaba.druid.wall.spi.OracleWallProvider;
import com.alibaba.druid.wall.spi.PGWallProvider;
import com.alibaba.druid.wall.spi.SQLServerWallProvider;
import com.alibaba.druid.wall.violation.SyntaxErrorViolation;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Wrapper;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static com.alibaba.druid.util.Utils.getBoolean;

public class WallFilter extends FilterAdapter implements WallFilterMBean {

    private final static Log   LOG            = LogFactory.getLog(WallFilter.class);

    private boolean            inited         = false;

    private WallProvider       provider;

    private String             dbType;

    private WallConfig         config;

    private volatile boolean   logViolation   = false;
    private volatile boolean   throwException = true;

    public final static String ATTR_SQL_STAT  = "wall.sqlStat";

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
    }

    @Override
    public synchronized void init(DataSourceProxy dataSource) {

        if (null == dataSource) {
            LOG.error("dataSource should not be null");
            return;
        }

        if (this.dbType == null || this.dbType.trim().length() == 0) {
            if (dataSource.getDbType() != null) {
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

        provider.setName(dataSource.getName());

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
        } catch (SQLException ex) {
            incrementExecuteErrorCount();
            throw ex;
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
        } catch (SQLException ex) {
            incrementExecuteErrorCount();
            throw ex;
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
        } catch (SQLException ex) {
            incrementExecuteErrorCount();
            throw ex;
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
        } catch (SQLException ex) {
            incrementExecuteErrorCount();
            throw ex;
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
            for (int count : updateCounts) {
                updateCount += count;
            }

            if (sqlStat != null) {
                provider.addUpdateCount(sqlStat, updateCount);
            }

            return updateCounts;
        } catch (SQLException ex) {
            incrementExecuteErrorCount();
            throw ex;
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
            ResultSetProxy resultSetProxy = chain.statement_executeQuery(statement, sql);
            preprocessResultSet(resultSetProxy);
            return resultSetProxy;
        } catch (SQLException ex) {
            incrementExecuteErrorCount();
            throw ex;
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
        } catch (SQLException ex) {
            incrementExecuteErrorCount();
            throw ex;
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
        } catch (SQLException ex) {
            incrementExecuteErrorCount();
            throw ex;
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
        } catch (SQLException ex) {
            incrementExecuteErrorCount();
            throw ex;
        } finally {
            WallContext.clearContext();
        }
    }

    public String getDbType(StatementProxy statement) {
        return statement.getConnectionProxy().getDirectDataSource().getDbType();
    }

    private WallContext createWallContext(StatementProxy statement) {
        String dbType = getDbType(statement);
        return WallContext.create(dbType);
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
        } catch (SQLException ex) {
            incrementExecuteErrorCount();
            throw ex;
        } finally {
            WallContext.clearContext();
        }
    }

    @Override
    public boolean preparedStatement_execute(FilterChain chain, PreparedStatementProxy statement) throws SQLException {
        try {
            boolean firstResult = chain.preparedStatement_execute(statement);

            if (!firstResult) {
                WallSqlStat sqlStat = (WallSqlStat) statement.getAttribute(ATTR_SQL_STAT);
                int updateCount = statement.getUpdateCount();
                if (sqlStat != null) {
                    provider.addUpdateCount(sqlStat, updateCount);
                }
            }

            return firstResult;
        } catch (SQLException ex) {
            incrementExecuteErrorCount(statement);
            throw ex;
        }
    }

    @Override
    public ResultSetProxy preparedStatement_executeQuery(FilterChain chain, PreparedStatementProxy statement)
                                                                                                             throws SQLException {
        try {
            ResultSetProxy resultSetProxy = chain.preparedStatement_executeQuery(statement);
            preprocessResultSet(resultSetProxy);
            return resultSetProxy;
        } catch (SQLException ex) {
            incrementExecuteErrorCount(statement);
            throw ex;
        }
    }

    @Override
    public int preparedStatement_executeUpdate(FilterChain chain, PreparedStatementProxy statement) throws SQLException {
        try {
            int updateCount = chain.preparedStatement_executeUpdate(statement);
            WallSqlStat sqlStat = (WallSqlStat) statement.getAttribute(ATTR_SQL_STAT);
            if (sqlStat != null) {
                provider.addUpdateCount(sqlStat, updateCount);
            }
            return updateCount;
        } catch (SQLException ex) {
            incrementExecuteErrorCount(statement);
            throw ex;
        }
    }

    @Override
    public ResultSetProxy statement_getResultSet(FilterChain chain, StatementProxy statement) throws SQLException {
        ResultSetProxy resultSetProxy = chain.statement_getResultSet(statement);
        preprocessResultSet(resultSetProxy);
        return resultSetProxy;
    }

    @Override
    public ResultSetProxy statement_getGeneratedKeys(FilterChain chain, StatementProxy statement) throws SQLException {
        ResultSetProxy resultSetProxy = chain.statement_getGeneratedKeys(statement);
        preprocessResultSet(resultSetProxy);
        return resultSetProxy;
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

        if (updateCount > 0) {
            provider.addUpdateCount(sqlStat, updateCount);
        }
    }

    public void incrementExecuteErrorCount(PreparedStatementProxy statement) {
        WallSqlStat sqlStat = (WallSqlStat) statement.getAttribute(ATTR_SQL_STAT);
        if (sqlStat != null) {
            sqlStat.incrementAndGetExecuteErrorCount();
        }
    }

    public void incrementExecuteErrorCount() {
        WallContext context = WallContext.current();
        if (context == null) {
            return;
        }

        WallSqlStat sqlStat = context.getSqlStat();
        if (sqlStat == null) {
            return;
        }

        sqlStat.incrementAndGetExecuteErrorCount();
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

        return checkResult.getSql();
    }

    @Override
    public boolean isWrapperFor(FilterChain chain, Wrapper wrapper, Class<?> iface) throws SQLException {
        if (config.isDoPrivilegedAllow() && WallProvider.ispPrivileged()) {
            return chain.isWrapperFor(wrapper, iface);
        }

        return this.provider.getConfig().isWrapAllow() && chain.isWrapperFor(wrapper, iface);
    }

    @Override
    public <T> T unwrap(FilterChain chain, Wrapper wrapper, Class<T> iface) throws SQLException {
        if (config.isDoPrivilegedAllow() && WallProvider.ispPrivileged()) {
            return chain.unwrap(wrapper, iface);
        }

        if (!this.provider.getConfig().isWrapAllow()) {
            return null;
        }

        return chain.unwrap(wrapper, iface);
    }

    @Override
    public DatabaseMetaData connection_getMetaData(FilterChain chain, ConnectionProxy connection) throws SQLException {
        if (config.isDoPrivilegedAllow() && WallProvider.ispPrivileged()) {
            return chain.connection_getMetaData(connection);
        }

        if (!this.provider.getConfig().isMetadataAllow()) {
            if (isLogViolation()) {
                LOG.error("not support method : Connection.getMetaData");
            }

            if (throwException) {
                throw new WallSQLException("not support method : Connection.getMetaData");
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

    // ////////////////

    @Override
    public int resultSet_findColumn(FilterChain chain, ResultSetProxy resultSet, String columnLabel)
                                                                                                    throws SQLException {
        int physicalColumn = chain.resultSet_findColumn(resultSet, columnLabel);
        return resultSet.getLogicColumn(physicalColumn);
    }

    @Override
    public Array resultSet_getArray(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        return chain.resultSet_getArray(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public InputStream resultSet_getAsciiStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex)
                                                                                                             throws SQLException {
        return chain.resultSet_getAsciiStream(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public BigDecimal resultSet_getBigDecimal(FilterChain chain, ResultSetProxy resultSet, int columnIndex)
                                                                                                           throws SQLException {
        return chain.resultSet_getBigDecimal(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public BigDecimal resultSet_getBigDecimal(FilterChain chain, ResultSetProxy resultSet, int columnIndex, int scale)
                                                                                                                      throws SQLException {
        return chain.resultSet_getBigDecimal(resultSet, resultSet.getPhysicalColumn(columnIndex), scale);
    }

    @Override
    public java.io.InputStream resultSet_getBinaryStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex)
                                                                                                                      throws SQLException {
        return chain.resultSet_getBinaryStream(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public Blob resultSet_getBlob(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        return chain.resultSet_getBlob(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public boolean resultSet_getBoolean(FilterChain chain, ResultSetProxy resultSet, int columnIndex)
                                                                                                     throws SQLException {
        return chain.resultSet_getBoolean(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public byte resultSet_getByte(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        return chain.resultSet_getByte(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public byte[] resultSet_getBytes(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        return chain.resultSet_getBytes(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public java.io.Reader resultSet_getCharacterStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex)
                                                                                                                    throws SQLException {
        return chain.resultSet_getCharacterStream(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public Clob resultSet_getClob(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        return chain.resultSet_getClob(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public java.sql.Date resultSet_getDate(FilterChain chain, ResultSetProxy resultSet, int columnIndex)
                                                                                                        throws SQLException {
        return chain.resultSet_getDate(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public java.sql.Date resultSet_getDate(FilterChain chain, ResultSetProxy resultSet, int columnIndex, Calendar cal)
                                                                                                                      throws SQLException {
        return chain.resultSet_getDate(resultSet, resultSet.getPhysicalColumn(columnIndex), cal);
    }

    @Override
    public double resultSet_getDouble(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        return chain.resultSet_getDouble(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public float resultSet_getFloat(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        return chain.resultSet_getFloat(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public int resultSet_getInt(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        return chain.resultSet_getInt(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public long resultSet_getLong(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        return chain.resultSet_getLong(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public java.io.Reader resultSet_getNCharacterStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex)
                                                                                                                     throws SQLException {
        return chain.resultSet_getNCharacterStream(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public NClob resultSet_getNClob(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        return chain.resultSet_getNClob(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public String resultSet_getNString(FilterChain chain, ResultSetProxy resultSet, int columnIndex)
                                                                                                    throws SQLException {
        return chain.resultSet_getNString(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        return chain.resultSet_getObject(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                      java.util.Map<String, Class<?>> map) throws SQLException {
        return chain.resultSet_getObject(resultSet, resultSet.getPhysicalColumn(columnIndex), map);
    }

    @Override
    public Ref resultSet_getRef(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        return chain.resultSet_getRef(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public RowId resultSet_getRowId(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        return chain.resultSet_getRowId(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public SQLXML resultSet_getSQLXML(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        return chain.resultSet_getSQLXML(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public short resultSet_getShort(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        return chain.resultSet_getShort(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public String resultSet_getString(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        return chain.resultSet_getString(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public java.sql.Time resultSet_getTime(FilterChain chain, ResultSetProxy resultSet, int columnIndex)
                                                                                                        throws SQLException {
        return chain.resultSet_getTime(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public java.sql.Time resultSet_getTime(FilterChain chain, ResultSetProxy resultSet, int columnIndex, Calendar cal)
                                                                                                                      throws SQLException {
        return chain.resultSet_getTime(resultSet, resultSet.getPhysicalColumn(columnIndex), cal);
    }

    @Override
    public java.sql.Timestamp resultSet_getTimestamp(FilterChain chain, ResultSetProxy resultSet, int columnIndex)
                                                                                                                  throws SQLException {
        return chain.resultSet_getTimestamp(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public java.sql.Timestamp resultSet_getTimestamp(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                                     Calendar cal) throws SQLException {
        return chain.resultSet_getTimestamp(resultSet, resultSet.getPhysicalColumn(columnIndex), cal);
    }

    @Override
    public java.net.URL resultSet_getURL(FilterChain chain, ResultSetProxy resultSet, int columnIndex)
                                                                                                      throws SQLException {
        return chain.resultSet_getURL(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public java.io.InputStream resultSet_getUnicodeStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex)
                                                                                                                       throws SQLException {
        return chain.resultSet_getUnicodeStream(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public void resultSet_updateArray(FilterChain chain, ResultSetProxy resultSet, int columnIndex, java.sql.Array x)
                                                                                                                     throws SQLException {
        chain.resultSet_updateArray(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateAsciiStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                            java.io.InputStream x) throws SQLException {
        chain.resultSet_updateAsciiStream(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateAsciiStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                            java.io.InputStream x, int length) throws SQLException {
        chain.resultSet_updateAsciiStream(resultSet, resultSet.getPhysicalColumn(columnIndex), x, length);
    }

    @Override
    public void resultSet_updateAsciiStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                            java.io.InputStream x, long length) throws SQLException {
        chain.resultSet_updateAsciiStream(resultSet, resultSet.getPhysicalColumn(columnIndex), x, length);
    }

    @Override
    public void resultSet_updateBigDecimal(FilterChain chain, ResultSetProxy resultSet, int columnIndex, BigDecimal x)
                                                                                                                      throws SQLException {
        chain.resultSet_updateBigDecimal(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateBinaryStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                             java.io.InputStream x) throws SQLException {
        chain.resultSet_updateBinaryStream(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateBinaryStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                             java.io.InputStream x, int length) throws SQLException {
        chain.resultSet_updateBinaryStream(resultSet, resultSet.getPhysicalColumn(columnIndex), x, length);
    }

    @Override
    public void resultSet_updateBinaryStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                             java.io.InputStream x, long length) throws SQLException {
        chain.resultSet_updateBinaryStream(resultSet, resultSet.getPhysicalColumn(columnIndex), x, length);
    }

    @Override
    public void resultSet_updateBlob(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                     InputStream inputStream) throws SQLException {
        chain.resultSet_updateBlob(resultSet, resultSet.getPhysicalColumn(columnIndex), inputStream);
    }

    @Override
    public void resultSet_updateBlob(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                     InputStream inputStream, long length) throws SQLException {
        chain.resultSet_updateBlob(resultSet, resultSet.getPhysicalColumn(columnIndex), inputStream, length);
    }

    @Override
    public void resultSet_updateBlob(FilterChain chain, ResultSetProxy resultSet, int columnIndex, java.sql.Blob x)
                                                                                                                   throws SQLException {
        chain.resultSet_updateBlob(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateBoolean(FilterChain chain, ResultSetProxy resultSet, int columnIndex, boolean x)
                                                                                                                throws SQLException {
        chain.resultSet_updateBoolean(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateByte(FilterChain chain, ResultSetProxy resultSet, int columnIndex, byte x)
                                                                                                          throws SQLException {
        chain.resultSet_updateByte(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateBytes(FilterChain chain, ResultSetProxy resultSet, int columnIndex, byte[] x)
                                                                                                             throws SQLException {
        chain.resultSet_updateBytes(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateCharacterStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                                java.io.Reader x) throws SQLException {
        chain.resultSet_updateCharacterStream(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateCharacterStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                                java.io.Reader x, int length) throws SQLException {
        chain.resultSet_updateCharacterStream(resultSet, resultSet.getPhysicalColumn(columnIndex), x, length);
    }

    @Override
    public void resultSet_updateCharacterStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                                java.io.Reader x, long length) throws SQLException {
        chain.resultSet_updateCharacterStream(resultSet, resultSet.getPhysicalColumn(columnIndex), x, length);
    }

    @Override
    public void resultSet_updateClob(FilterChain chain, ResultSetProxy resultSet, int columnIndex, java.sql.Clob x)
                                                                                                                   throws SQLException {
        chain.resultSet_updateClob(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateClob(FilterChain chain, ResultSetProxy resultSet, int columnIndex, Reader reader)
                                                                                                                 throws SQLException {
        chain.resultSet_updateClob(resultSet, resultSet.getPhysicalColumn(columnIndex), reader);
    }

    @Override
    public void resultSet_updateClob(FilterChain chain, ResultSetProxy resultSet, int columnIndex, Reader reader,
                                     long length) throws SQLException {
        chain.resultSet_updateClob(resultSet, resultSet.getPhysicalColumn(columnIndex), reader, length);
    }

    @Override
    public void resultSet_updateDate(FilterChain chain, ResultSetProxy resultSet, int columnIndex, java.sql.Date x)
                                                                                                                   throws SQLException {
        chain.resultSet_updateDate(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateDouble(FilterChain chain, ResultSetProxy resultSet, int columnIndex, double x)
                                                                                                              throws SQLException {
        chain.resultSet_updateDouble(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateFloat(FilterChain chain, ResultSetProxy resultSet, int columnIndex, float x)
                                                                                                            throws SQLException {
        chain.resultSet_updateFloat(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateInt(FilterChain chain, ResultSetProxy resultSet, int columnIndex, int x)
                                                                                                        throws SQLException {
        chain.resultSet_updateInt(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateLong(FilterChain chain, ResultSetProxy resultSet, int columnIndex, long x)
                                                                                                          throws SQLException {
        chain.resultSet_updateLong(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateNCharacterStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                                 java.io.Reader x) throws SQLException {
        chain.resultSet_updateNCharacterStream(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateNCharacterStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                                 java.io.Reader x, long length) throws SQLException {
        chain.resultSet_updateNCharacterStream(resultSet, resultSet.getPhysicalColumn(columnIndex), x, length);
    }

    @Override
    public void resultSet_updateNClob(FilterChain chain, ResultSetProxy resultSet, int columnIndex, NClob nClob)
                                                                                                                throws SQLException {
        chain.resultSet_updateNClob(resultSet, resultSet.getPhysicalColumn(columnIndex), nClob);
    }

    @Override
    public void resultSet_updateNClob(FilterChain chain, ResultSetProxy resultSet, int columnIndex, Reader reader)
                                                                                                                  throws SQLException {
        chain.resultSet_updateNClob(resultSet, resultSet.getPhysicalColumn(columnIndex), reader);
    }

    @Override
    public void resultSet_updateNClob(FilterChain chain, ResultSetProxy resultSet, int columnIndex, Reader reader,
                                      long length) throws SQLException {
        chain.resultSet_updateNClob(resultSet, resultSet.getPhysicalColumn(columnIndex), reader, length);
    }

    @Override
    public void resultSet_updateNString(FilterChain chain, ResultSetProxy resultSet, int columnIndex, String nString)
                                                                                                                     throws SQLException {
        chain.resultSet_updateNString(resultSet, resultSet.getPhysicalColumn(columnIndex), nString);
    }

    @Override
    public void resultSet_updateNull(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        chain.resultSet_updateNull(resultSet, resultSet.getPhysicalColumn(columnIndex));
    }

    @Override
    public void resultSet_updateObject(FilterChain chain, ResultSetProxy resultSet, int columnIndex, Object x)
                                                                                                              throws SQLException {
        chain.resultSet_updateObject(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateObject(FilterChain chain, ResultSetProxy resultSet, int columnIndex, Object x,
                                       int scaleOrLength) throws SQLException {
        chain.resultSet_updateObject(resultSet, resultSet.getPhysicalColumn(columnIndex), x, scaleOrLength);
    }

    @Override
    public void resultSet_updateRef(FilterChain chain, ResultSetProxy resultSet, int columnIndex, java.sql.Ref x)
                                                                                                                 throws SQLException {
        chain.resultSet_updateRef(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateRowId(FilterChain chain, ResultSetProxy resultSet, int columnIndex, RowId x)
                                                                                                            throws SQLException {
        chain.resultSet_updateRowId(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateShort(FilterChain chain, ResultSetProxy resultSet, int columnIndex, short x)
                                                                                                            throws SQLException {
        chain.resultSet_updateShort(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateSQLXML(FilterChain chain, ResultSetProxy resultSet, int columnIndex, SQLXML xmlObject)
                                                                                                                      throws SQLException {
        chain.resultSet_updateSQLXML(resultSet, resultSet.getPhysicalColumn(columnIndex), xmlObject);
    }

    @Override
    public void resultSet_updateString(FilterChain chain, ResultSetProxy resultSet, int columnIndex, String x)
                                                                                                              throws SQLException {
        chain.resultSet_updateString(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateTime(FilterChain chain, ResultSetProxy resultSet, int columnIndex, java.sql.Time x)
                                                                                                                   throws SQLException {
        chain.resultSet_updateTime(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public void resultSet_updateTimestamp(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                          java.sql.Timestamp x) throws SQLException {
        chain.resultSet_updateTimestamp(resultSet, resultSet.getPhysicalColumn(columnIndex), x);
    }

    @Override
    public boolean resultSet_next(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        boolean hasNext = chain.resultSet_next(resultSet);
        TenantCallBack callback = provider.getConfig().getTenantCallBack();
        if (callback != null && hasNext) {
            List<Integer> tenantColumns = tenantColumnsLocal.get();
            if (tenantColumns != null && tenantColumns.size() > 0) {
                for (Integer columnIndex : tenantColumns) {
                    Object value = resultSet.getResultSetRaw().getObject(columnIndex);
                    callback.filterResultsetTenantColumn(value);
                }
            }
        }
        return hasNext;
    }

    @Override
    public int resultSetMetaData_getColumnCount(FilterChain chain, ResultSetMetaDataProxy metaData) throws SQLException {
        int count = chain.resultSetMetaData_getColumnCount(metaData);
        return count - metaData.getResultSetProxy().getHiddenColumnCount();
    }

    @Override
    public boolean resultSetMetaData_isAutoIncrement(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                    throws SQLException {
        return chain.resultSetMetaData_isAutoIncrement(metaData, metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public boolean resultSetMetaData_isCaseSensitive(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                    throws SQLException {
        return chain.resultSetMetaData_isCaseSensitive(metaData, metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public boolean resultSetMetaData_isSearchable(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                 throws SQLException {
        return chain.resultSetMetaData_isSearchable(metaData, metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public boolean resultSetMetaData_isCurrency(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                               throws SQLException {
        return chain.resultSetMetaData_isCurrency(metaData, metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public int resultSetMetaData_isNullable(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                           throws SQLException {
        return chain.resultSetMetaData_isNullable(metaData, metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public boolean resultSetMetaData_isSigned(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                             throws SQLException {
        return chain.resultSetMetaData_isSigned(metaData, metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public int resultSetMetaData_getColumnDisplaySize(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                     throws SQLException {
        return chain.resultSetMetaData_getColumnDisplaySize(metaData,
                                                            metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public String resultSetMetaData_getColumnLabel(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                  throws SQLException {
        return chain.resultSetMetaData_getColumnLabel(metaData, metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public String resultSetMetaData_getColumnName(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                 throws SQLException {
        return chain.resultSetMetaData_getColumnName(metaData, metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public String resultSetMetaData_getSchemaName(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                 throws SQLException {
        return chain.resultSetMetaData_getSchemaName(metaData, metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public int resultSetMetaData_getPrecision(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                             throws SQLException {
        return chain.resultSetMetaData_getPrecision(metaData, metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public int resultSetMetaData_getScale(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                         throws SQLException {
        return chain.resultSetMetaData_getScale(metaData, metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public String resultSetMetaData_getTableName(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                throws SQLException {
        return chain.resultSetMetaData_getTableName(metaData, metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public String resultSetMetaData_getCatalogName(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                  throws SQLException {
        return chain.resultSetMetaData_getCatalogName(metaData, metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public int resultSetMetaData_getColumnType(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                              throws SQLException {
        return chain.resultSetMetaData_getColumnType(metaData, metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public String resultSetMetaData_getColumnTypeName(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                     throws SQLException {
        return chain.resultSetMetaData_getColumnTypeName(metaData,
                                                         metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public boolean resultSetMetaData_isReadOnly(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                               throws SQLException {
        return chain.resultSetMetaData_isReadOnly(metaData, metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public boolean resultSetMetaData_isWritable(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                               throws SQLException {
        return chain.resultSetMetaData_isWritable(metaData, metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public boolean resultSetMetaData_isDefinitelyWritable(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                         throws SQLException {
        return chain.resultSetMetaData_isDefinitelyWritable(metaData,
                                                            metaData.getResultSetProxy().getPhysicalColumn(column));
    }

    @Override
    public String resultSetMetaData_getColumnClassName(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                      throws SQLException {
        return chain.resultSetMetaData_getColumnClassName(metaData,
                                                          metaData.getResultSetProxy().getPhysicalColumn(column));
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

    private static final ThreadLocal<List<Integer>> tenantColumnsLocal = new ThreadLocal<List<Integer>>();

    private void preprocessResultSet(ResultSetProxy resultSet) throws SQLException {
        if (resultSet == null) {
            return;
        }

        ResultSetMetaData metaData = resultSet.getResultSetRaw().getMetaData();
        if (metaData == null) {
            return;
        }

        TenantCallBack tenantCallBack = provider.getConfig().getTenantCallBack();
        String tenantTablePattern = provider.getConfig().getTenantTablePattern();
        if (tenantCallBack == null && (tenantTablePattern == null || tenantTablePattern.length() == 0)) {
            return;
        }

        Map<Integer, Integer> logicColumnMap = new HashMap<Integer, Integer>();
        Map<Integer, Integer> physicalColumnMap = new HashMap<Integer, Integer>();
        List<Integer> hiddenColumns = new ArrayList<Integer>();
        List<Integer> tenantColumns = new ArrayList<Integer>();
        for (int physicalColumn = 1, logicColumn = 1; physicalColumn <= metaData.getColumnCount(); physicalColumn++) {
            boolean isHidden = false;
            String tableName = metaData.getTableName(physicalColumn);

            String hiddenColumn = null;
            String tenantColumn = null;
            if (tenantCallBack != null) {
                tenantColumn = tenantCallBack.getTenantColumn(StatementType.SELECT, tableName);
                hiddenColumn = tenantCallBack.getHiddenColumn(tableName);
            }

            if (StringUtils.isEmpty(hiddenColumn) || StringUtils.isEmpty(tenantColumn)) {
                if (tableName == null || ServletPathMatcher.getInstance().matches(tenantTablePattern, tableName)) {
                    if (StringUtils.isEmpty(hiddenColumn)) {
                        hiddenColumn = provider.getConfig().getTenantColumn();
                    }

                    if (StringUtils.isEmpty(tenantColumn)) {
                        tenantColumn = provider.getConfig().getTenantColumn();
                    }
                }
            }

            if (!StringUtils.isEmpty(hiddenColumn)) {
                String columnName = metaData.getColumnName(physicalColumn);
                if (null != hiddenColumn && hiddenColumn.equalsIgnoreCase(columnName)) {
                    hiddenColumns.add(physicalColumn);
                    isHidden = true;
                }
            }
            if (!isHidden) {
                logicColumnMap.put(logicColumn, physicalColumn);
                physicalColumnMap.put(physicalColumn, logicColumn);
                logicColumn++;
            }

            if (!StringUtils.isEmpty(tenantColumn)
                && null != tenantColumn && tenantColumn.equalsIgnoreCase(metaData.getColumnName(physicalColumn))) {
                tenantColumns.add(physicalColumn);
            }
        }

        if (hiddenColumns.size() > 0) {
            resultSet.setLogicColumnMap(logicColumnMap);
            resultSet.setPhysicalColumnMap(physicalColumnMap);
            resultSet.setHiddenColumns(hiddenColumns);
        }
        tenantColumnsLocal.set(tenantColumns);
    }
}
