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
package com.alibaba.druid.filter.stat;

import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import com.alibaba.druid.VERSION;
import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.ClobProxy;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.JdbcParameter;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementExecuteType;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.sql.visitor.ParameterizedOutputVisitorUtils;
import com.alibaba.druid.stat.JdbcConnectionStat;
import com.alibaba.druid.stat.JdbcDataSourceStat;
import com.alibaba.druid.stat.JdbcResultSetStat;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.stat.JdbcStatContext;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.stat.JdbcStatementStat;
import com.alibaba.druid.support.json.JSONWriter;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.support.profile.Profiler;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class StatFilter extends FilterEventAdapter implements StatFilterMBean {

    private final static Log          LOG                        = LogFactory.getLog(StatFilter.class);

    private static final String       SYS_PROP_LOG_SLOW_SQL      = "druid.stat.logSlowSql";
    private static final String       SYS_PROP_SLOW_SQL_MILLIS   = "druid.stat.slowSqlMillis";
    private static final String       SYS_PROP_MERGE_SQL         = "druid.stat.mergeSql";

    public final static String        ATTR_NAME_CONNECTION_STAT  = "stat.conn";
    public final static String        ATTR_TRANSACTION           = "stat.tx";

    private final Lock                lock                       = new ReentrantLock();

    // protected JdbcDataSourceStat dataSourceStat;

    @Deprecated
    protected final JdbcStatementStat statementStat              = JdbcStatManager.getInstance().getStatementStat();

    @Deprecated
    protected final JdbcResultSetStat resultSetStat              = JdbcStatManager.getInstance().getResultSetStat();

    private boolean                   connectionStackTraceEnable = false;

    // 3 seconds is slow sql
    protected long                    slowSqlMillis              = 3 * 1000;

    protected boolean                 logSlowSql                 = false;

    private String                    dbType;

    private boolean                   mergeSql                   = false;

    public StatFilter(){
    }

    public String getDbType() {
        return dbType;
    }

    public void setDbType(String dbType) {
        this.dbType = dbType;
    }

    public long getSlowSqlMillis() {
        return slowSqlMillis;
    }

    public void setSlowSqlMillis(long slowSqlMillis) {
        this.slowSqlMillis = slowSqlMillis;
    }

    public boolean isLogSlowSql() {
        return logSlowSql;
    }

    public void setLogSlowSql(boolean logSlowSql) {
        this.logSlowSql = logSlowSql;
    }

    public boolean isConnectionStackTraceEnable() {
        return connectionStackTraceEnable;
    }

    public void setConnectionStackTraceEnable(boolean connectionStackTraceEnable) {
        this.connectionStackTraceEnable = connectionStackTraceEnable;
    }

    public boolean isMergeSql() {
        return mergeSql;
    }

    public void setMergeSql(boolean mergeSql) {
        this.mergeSql = mergeSql;
    }

    @Deprecated
    public String mergeSql(String sql) {
        return this.mergeSql(sql, dbType);
    }

    public String mergeSql(String sql, String dbType) {
        if (!mergeSql) {
            return sql;
        }

        try {
            sql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        } catch (Exception e) {
            LOG.error("merge sql error, dbType " + dbType + ", druid-" + VERSION.getVersionNumber() + ", sql : " + sql, e);
        }

        return sql;
    }

    @Override
    public void init(DataSourceProxy dataSource) {
        lock.lock();
        try {
            if (this.dbType == null || this.dbType.trim().length() == 0) {
                this.dbType = dataSource.getDbType();
            }

            configFromProperties(dataSource.getConnectProperties());
            configFromProperties(System.getProperties());
        } finally {
            lock.unlock();
        }
    }

    public void configFromProperties(Properties properties) {
        if (properties == null) {
            return;
        }

        {
            String property = properties.getProperty(SYS_PROP_MERGE_SQL);
            if ("true".equals(property)) {
                this.mergeSql = true;
            } else if ("false".equals(property)) {
                this.mergeSql = false;
            }
        }

        {
            String property = properties.getProperty(SYS_PROP_SLOW_SQL_MILLIS);
            if (property != null && property.trim().length() > 0) {
                property = property.trim();
                try {
                    this.slowSqlMillis = Long.parseLong(property);
                } catch (Exception e) {
                    LOG.error("property 'druid.stat.slowSqlMillis' format error");
                }
            }
        }

        {
            String property = properties.getProperty(SYS_PROP_LOG_SLOW_SQL);
            if ("true".equals(property)) {
                this.logSlowSql = true;
            } else if ("false".equals(property)) {
                this.logSlowSql = false;
            }
        }
    }

    public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
        ConnectionProxy connection = null;

        long startNano = System.nanoTime();
        long startTime = System.currentTimeMillis();

        long nanoSpan;
        long nowTime = System.currentTimeMillis();

        JdbcDataSourceStat dataSourceStat = chain.getDataSource().getDataSourceStat();
        dataSourceStat.getConnectionStat().beforeConnect();
        try {
            connection = chain.connection_connect(info);
            nanoSpan = System.nanoTime() - startNano;
        } catch (SQLException ex) {
            dataSourceStat.getConnectionStat().connectError(ex);
            throw ex;
        }
        dataSourceStat.getConnectionStat().afterConnected(nanoSpan);

        if (connection != null) {
            JdbcConnectionStat.Entry statEntry = getConnectionInfo(connection);

            dataSourceStat.getConnections().put(connection.getId(), statEntry);

            statEntry.setConnectTime(new Date(startTime));
            statEntry.setConnectTimespanNano(nanoSpan);
            statEntry.setEstablishNano(System.nanoTime());
            statEntry.setEstablishTime(nowTime);
            statEntry.setConnectStackTrace(new Exception());

            dataSourceStat.getConnectionStat().setActiveCount(dataSourceStat.getConnections().size());
        }

        return connection;
    }

    @Override
    public void connection_close(FilterChain chain, ConnectionProxy connection) throws SQLException {
        if (connection.getCloseCount() == 0) {
            long nowNano = System.nanoTime();

            JdbcDataSourceStat dataSourceStat = chain.getDataSource().getDataSourceStat();
            dataSourceStat.getConnectionStat().incrementConnectionCloseCount();

            JdbcConnectionStat.Entry connectionInfo = getConnectionInfo(connection);

            long aliveNanoSpan = nowNano - connectionInfo.getEstablishNano();

            JdbcConnectionStat.Entry existsConnection = dataSourceStat.getConnections().remove(connection.getId());
            if (existsConnection != null) {
                dataSourceStat.getConnectionStat().afterClose(aliveNanoSpan);
            }
        }

        chain.connection_close(connection);
        // duplicate close, C3P0等连接池，在某些情况下会关闭连接多次。
    }

    @Override
    public void connection_commit(FilterChain chain, ConnectionProxy connection) throws SQLException {
        chain.connection_commit(connection);

        JdbcDataSourceStat dataSourceStat = chain.getDataSource().getDataSourceStat();
        dataSourceStat.getConnectionStat().incrementConnectionCommitCount();
    }

    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection) throws SQLException {
        chain.connection_rollback(connection);

        JdbcDataSourceStat dataSourceStat = chain.getDataSource().getDataSourceStat();
        dataSourceStat.getConnectionStat().incrementConnectionRollbackCount();
    }

    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection, Savepoint savepoint)
                                                                                                       throws SQLException {
        chain.connection_rollback(connection, savepoint);

        JdbcDataSourceStat dataSourceStat = connection.getDirectDataSource().getDataSourceStat();
        dataSourceStat.getConnectionStat().incrementConnectionRollbackCount();
    }

    @Override
    public void statementCreateAfter(StatementProxy statement) {
        JdbcDataSourceStat dataSourceStat = statement.getConnectionProxy().getDirectDataSource().getDataSourceStat();
        dataSourceStat.getStatementStat().incrementCreateCounter();

        super.statementCreateAfter(statement);
    }

    @Override
    public void statementPrepareCallAfter(CallableStatementProxy statement) {
        JdbcDataSourceStat dataSourceStat = statement.getConnectionProxy().getDirectDataSource().getDataSourceStat();
        dataSourceStat.getStatementStat().incrementPrepareCallCount();

        JdbcSqlStat sqlStat = createSqlStat(statement, statement.getSql());
        statement.setSqlStat(sqlStat);
    }

    @Override
    public void statementPrepareAfter(PreparedStatementProxy statement) {
        JdbcDataSourceStat dataSourceStat = statement.getConnectionProxy().getDirectDataSource().getDataSourceStat();
        dataSourceStat.getStatementStat().incrementPrepareCounter();
        JdbcSqlStat sqlStat = createSqlStat(statement, statement.getSql());
        statement.setSqlStat(sqlStat);
    }

    @Override
    public void statement_close(FilterChain chain, StatementProxy statement) throws SQLException {
        chain.statement_close(statement);

        JdbcDataSourceStat dataSourceStat = chain.getDataSource().getDataSourceStat();
        dataSourceStat.getStatementStat().incrementStatementCloseCounter();
        JdbcStatContext context = JdbcStatManager.getInstance().getStatContext();
        if (context != null) {
            context.setName(null);
            context.setFile(null);
            context.setSql(null);
        }
    }

    @Override
    protected void statementExecuteUpdateBefore(StatementProxy statement, String sql) {
        internalBeforeStatementExecute(statement, sql);
    }

    @Override
    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {
        internalAfterStatementExecute(statement, false, updateCount);
    }

    @Override
    protected void statementExecuteQueryBefore(StatementProxy statement, String sql) {
        internalBeforeStatementExecute(statement, sql);
    }

    @Override
    protected void statementExecuteQueryAfter(StatementProxy statement, String sql, ResultSetProxy resultSet) {
        internalAfterStatementExecute(statement, true);
    }

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
        internalBeforeStatementExecute(statement, sql);
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean firstResult) {
        internalAfterStatementExecute(statement, firstResult);
    }

    @Override
    protected void statementExecuteBatchBefore(StatementProxy statement) {
        final String sql = statement.getBatchSql();

        final int batchSize = statement.getBatchSqlList().size();
        JdbcSqlStat sqlStat = statement.getSqlStat();
        if (sqlStat == null || sqlStat.isRemoved()) {
            sqlStat = createSqlStat(statement, sql);
            statement.setSqlStat(sqlStat);
        }

        if (sqlStat != null) {
            sqlStat.addExecuteBatchCount(batchSize);
        }

        internalBeforeStatementExecute(statement, sql);

    }

    @Override
    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        internalAfterStatementExecute(statement, false, result);

    }

    private final void internalBeforeStatementExecute(StatementProxy statement, String sql) {
        JdbcDataSourceStat dataSourceStat = statement.getConnectionProxy().getDirectDataSource().getDataSourceStat();
        dataSourceStat.getStatementStat().beforeExecute();

        final ConnectionProxy connection = statement.getConnectionProxy();
        final JdbcConnectionStat.Entry connectionCounter = getConnectionInfo(connection);

        statement.setLastExecuteStartNano();

        connectionCounter.setLastSql(sql);

        if (connectionStackTraceEnable) {
            connectionCounter.setLastStatementStatckTrace(new Exception());
        }

        // //////////SQL

        JdbcSqlStat sqlStat = statement.getSqlStat();
        if (sqlStat == null || sqlStat.isRemoved()) {
            sqlStat = createSqlStat(statement, sql);
            statement.setSqlStat(sqlStat);
        }

        JdbcStatContext statContext = JdbcStatManager.getInstance().getStatContext();
        if (statContext != null) {
            sqlStat.setName(statContext.getName());
            sqlStat.setFile(statContext.getFile());
        }

        boolean inTransaction = false;
        try {
            inTransaction = !statement.getConnectionProxy().getAutoCommit();
        } catch (SQLException e) {
            LOG.error("getAutoCommit error", e);
        }

        if (sqlStat != null) {
            sqlStat.setExecuteLastStartTime(System.currentTimeMillis());
            sqlStat.incrementRunningCount();

            if (inTransaction) {
                sqlStat.incrementInTransactionCount();
            }
        }

        StatFilterContext.getInstance().executeBefore(sql, inTransaction);

        String mergedSql;
        if (sqlStat != null) {
            mergedSql = sqlStat.getSql();
        } else {
            mergedSql = sql;
        }
        Profiler.enter(mergedSql, Profiler.PROFILE_TYPE_SQL);
    }

    private final void internalAfterStatementExecute(StatementProxy statement, boolean firstResult,
                                                     int... updateCountArray) {
        final long nowNano = System.nanoTime();
        final long nanos = nowNano - statement.getLastExecuteStartNano();

        JdbcDataSourceStat dataSourceStat = statement.getConnectionProxy().getDirectDataSource().getDataSourceStat();
        dataSourceStat.getStatementStat().afterExecute(nanos);

        final JdbcSqlStat sqlStat = statement.getSqlStat();

        if (sqlStat != null) {
            sqlStat.incrementExecuteSuccessCount();

            sqlStat.decrementRunningCount();
            sqlStat.addExecuteTime(statement.getLastExecuteType(), firstResult, nanos);
            statement.setLastExecuteTimeNano(nanos);
            if ((!firstResult) && statement.getLastExecuteType() == StatementExecuteType.Execute) {
                try {
                    int updateCount = statement.getUpdateCount();
                    sqlStat.addUpdateCount(updateCount);
                } catch (SQLException e) {
                    LOG.error("getUpdateCount error", e);
                }
            } else {
                for (int updateCount : updateCountArray) {
                    sqlStat.addUpdateCount(updateCount);
                    sqlStat.addFetchRowCount(0);
                    StatFilterContext.getInstance().addUpdateCount(updateCount);
                }
            }

            long millis = nanos / (1000 * 1000);
            if (millis >= slowSqlMillis) {
                String slowParameters = buildSlowParameters(statement);
                sqlStat.setLastSlowParameters(slowParameters);

                String lastExecSql = statement.getLastExecuteSql();
                if (logSlowSql) {
                    LOG.error("slow sql " + millis + " millis. " + lastExecSql + "" + slowParameters);
                }

                handleSlowSql(statement);
            }
        }

        String sql = statement.getLastExecuteSql();
        StatFilterContext.getInstance().executeAfter(sql, nanos, null);

        Profiler.release(nanos);
    }

    protected void handleSlowSql(StatementProxy statementProxy) {

    }

    @Override
    protected void statement_executeErrorAfter(StatementProxy statement, String sql, Throwable error) {

        ConnectionProxy connection = statement.getConnectionProxy();
        JdbcConnectionStat.Entry connectionCounter = getConnectionInfo(connection);

        long nanos = System.nanoTime() - statement.getLastExecuteStartNano();

        JdbcDataSourceStat dataSourceStat = statement.getConnectionProxy().getDirectDataSource().getDataSourceStat();
        dataSourceStat.getStatementStat().error(error);
        dataSourceStat.getStatementStat().afterExecute(nanos);

        connectionCounter.error(error);

        // SQL
        JdbcSqlStat sqlStat = statement.getSqlStat();

        if (sqlStat != null) {
            sqlStat.decrementExecutingCount();
            sqlStat.error(error);
            sqlStat.addExecuteTime(statement.getLastExecuteType(), statement.isFirstResultSet(), nanos);
            statement.setLastExecuteTimeNano(nanos);
        }

        StatFilterContext.getInstance().executeAfter(sql, nanos, error);
        Profiler.release(nanos);
    }

    protected String buildSlowParameters(StatementProxy statement) {
        JSONWriter out = new JSONWriter();

        out.writeArrayStart();
        for (int i = 0, parametersSize = statement.getParametersSize(); i < parametersSize; ++i) {
            JdbcParameter parameter = statement.getParameter(i);
            if (i != 0) {
                out.writeComma();
            }
            if (parameter == null) {
                continue;
            }

            Object value = parameter.getValue();
            if (value == null) {
                out.writeNull();
            } else if (value instanceof String) {
                String text = (String) value;
                if (text.length() > 100) {
                    out.writeString(text.substring(0, 97) + "...");
                } else {
                    out.writeString(text);
                }
            } else if (value instanceof Number) {
                out.writeObject(value);
            } else if (value instanceof java.util.Date) {
                out.writeObject(value);
            } else if (value instanceof Boolean) {
                out.writeObject(value);
            } else if (value instanceof InputStream) {
                out.writeString("<InputStream>");
            } else if (value instanceof NClob) {
                out.writeString("<NClob>");
            } else if (value instanceof Clob) {
                out.writeString("<Clob>");
            } else if (value instanceof Blob) {
                out.writeString("<Blob>");
            } else {
                out.writeString('<' + value.getClass().getName() + '>');
            }
        }
        out.writeArrayEnd();

        return out.toString();
    }

    @Override
    protected void resultSetOpenAfter(ResultSetProxy resultSet) {
        JdbcDataSourceStat dataSourceStat = resultSet.getStatementProxy().getConnectionProxy().getDirectDataSource().getDataSourceStat();
        dataSourceStat.getResultSetStat().beforeOpen();

        resultSet.setConstructNano();

        StatFilterContext.getInstance().resultSet_open();
    }

    @Override
    public void resultSet_close(FilterChain chain, ResultSetProxy resultSet) throws SQLException {

        long nanos = System.nanoTime() - resultSet.getConstructNano();

        int fetchRowCount = resultSet.getFetchRowCount();

        JdbcDataSourceStat dataSourceStat = chain.getDataSource().getDataSourceStat();
        dataSourceStat.getResultSetStat().afterClose(nanos);
        dataSourceStat.getResultSetStat().addFetchRowCount(fetchRowCount);
        dataSourceStat.getResultSetStat().incrementCloseCounter();

        StatFilterContext.getInstance().addFetchRowCount(fetchRowCount);

        String sql = resultSet.getSql();
        if (sql != null) {
            JdbcSqlStat sqlStat = resultSet.getSqlStat();
            if (sqlStat != null && resultSet.getCloseCount() == 0) {
                sqlStat.addFetchRowCount(fetchRowCount);
                long stmtExecuteNano = resultSet.getStatementProxy().getLastExecuteTimeNano();
                sqlStat.addResultSetHoldTimeNano(stmtExecuteNano, nanos);
                if (resultSet.getReadStringLength() > 0) {
                    sqlStat.addStringReadLength(resultSet.getReadStringLength());
                }
                if (resultSet.getReadBytesLength() > 0) {
                    sqlStat.addReadBytesLength(resultSet.getReadBytesLength());
                }
                if (resultSet.getOpenInputStreamCount() > 0) {
                    sqlStat.addInputStreamOpenCount(resultSet.getOpenInputStreamCount());
                }
                if (resultSet.getOpenReaderCount() > 0) {
                    sqlStat.addReaderOpenCount(resultSet.getOpenReaderCount());
                }
            }
        }

        chain.resultSet_close(resultSet);

        StatFilterContext.getInstance().resultSet_close(nanos);
    }

    public JdbcConnectionStat.Entry getConnectionInfo(ConnectionProxy connection) {
        JdbcConnectionStat.Entry counter = (JdbcConnectionStat.Entry) connection.getAttribute(ATTR_NAME_CONNECTION_STAT);

        if (counter == null) {
            String dataSourceName = connection.getDirectDataSource().getName();
            connection.putAttribute(ATTR_NAME_CONNECTION_STAT,
                                           new JdbcConnectionStat.Entry(dataSourceName, connection.getId()));
            counter = (JdbcConnectionStat.Entry) connection.getAttribute(ATTR_NAME_CONNECTION_STAT);
        }

        return counter;
    }

    public JdbcSqlStat createSqlStat(StatementProxy statement, String sql) {
        DataSourceProxy dataSource = statement.getConnectionProxy().getDirectDataSource();
        JdbcDataSourceStat dataSourceStat = dataSource.getDataSourceStat();

        JdbcStatContext context = JdbcStatManager.getInstance().getStatContext();
        String contextSql = context != null ? context.getSql() : null;
        if (contextSql != null && contextSql.length() > 0) {
            return dataSourceStat.createSqlStat(contextSql);
        } else {
            String dbType = this.dbType;

            if (dbType == null) {
                dbType = dataSource.getDbType();
            }

            sql = mergeSql(sql, dbType);
            return dataSourceStat.createSqlStat(sql);
        }
    }

    public static StatFilter getStatFilter(DataSourceProxy dataSource) {
        for (Filter filter : dataSource.getProxyFilters()) {
            if (filter instanceof StatFilter) {
                return (StatFilter) filter;
            }
        }

        return null;
    }

    @Override
    public void dataSource_releaseConnection(FilterChain chain, DruidPooledConnection conn) throws SQLException {
        chain.dataSource_recycle(conn);

        long nanos = System.nanoTime() - conn.getConnectedTimeNano();

        long millis = nanos / (1000L * 1000L);

        JdbcDataSourceStat dataSourceStat = chain.getDataSource().getDataSourceStat();
        dataSourceStat.getConnectionHoldHistogram().record(millis);

        StatFilterContext.getInstance().pool_connection_close(nanos);
    }

    @Override
    public DruidPooledConnection dataSource_getConnection(FilterChain chain, DruidDataSource dataSource,
                                                          long maxWaitMillis) throws SQLException {
        DruidPooledConnection conn = chain.dataSource_connect(dataSource, maxWaitMillis);

        if (conn != null) {
            conn.setConnectedTimeNano();

            StatFilterContext.getInstance().pool_connection_open();
        }

        return conn;
    }

    @Override
    public Clob resultSet_getClob(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        Clob clob = chain.resultSet_getClob(resultSet, columnIndex);

        if (clob != null) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat(), resultSet, (ClobProxy) clob);
        }

        return clob;
    }

    @Override
    public Clob resultSet_getClob(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException {
        Clob clob = chain.resultSet_getClob(resultSet, columnLabel);

        if (clob != null) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat(), resultSet, (ClobProxy) clob);
        }

        return clob;
    }

    @Override
    public Blob callableStatement_getBlob(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                  throws SQLException {
        Blob blob = chain.callableStatement_getBlob(statement, parameterIndex);

        if (blob != null) {
            blobOpenAfter(chain.getDataSource().getDataSourceStat(), statement, blob);
        }

        return blob;
    }

    @Override
    public Blob callableStatement_getBlob(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                    throws SQLException {
        Blob blob = chain.callableStatement_getBlob(statement, parameterName);

        if (blob != null) {
            blobOpenAfter(chain.getDataSource().getDataSourceStat(), statement, blob);
        }

        return blob;
    }

    @Override
    public Blob resultSet_getBlob(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        Blob blob = chain.resultSet_getBlob(result, columnIndex);

        if (blob != null) {
            blobOpenAfter(chain.getDataSource().getDataSourceStat(), result, blob);
        }

        return blob;
    }

    @Override
    public Blob resultSet_getBlob(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        Blob blob = chain.resultSet_getBlob(result, columnLabel);

        if (blob != null) {
            blobOpenAfter(chain.getDataSource().getDataSourceStat(), result, blob);
        }

        return blob;
    }

    @Override
    public Clob callableStatement_getClob(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                  throws SQLException {
        Clob clob = chain.callableStatement_getClob(statement, parameterIndex);

        if (clob != null) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat(), statement, (ClobProxy) clob);
        }

        return clob;
    }

    @Override
    public Clob callableStatement_getClob(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                    throws SQLException {
        Clob clob = chain.callableStatement_getClob(statement, parameterName);

        if (clob != null) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat(), statement, (ClobProxy) clob);
        }

        return clob;
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        Object obj = chain.resultSet_getObject(result, columnIndex);

        if (obj instanceof Clob) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat(), result, (ClobProxy) obj);
        } else if (obj instanceof Blob) {
            blobOpenAfter(chain.getDataSource().getDataSourceStat(), result, (Blob) obj);
        } else if (obj instanceof String) {
            result.addReadStringLength(((String) obj).length());
        }

        return obj;
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, int columnIndex,
                                      java.util.Map<String, Class<?>> map) throws SQLException {
        Object obj = chain.resultSet_getObject(result, columnIndex, map);

        if (obj instanceof Clob) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat(), result, (ClobProxy) obj);
        } else if (obj instanceof Blob) {
            blobOpenAfter(chain.getDataSource().getDataSourceStat(), result, (Blob) obj);
        } else if (obj instanceof String) {
            result.addReadStringLength(((String) obj).length());
        }

        return obj;
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        Object obj = chain.resultSet_getObject(result, columnLabel);

        if (obj instanceof Clob) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat(), result, (ClobProxy) obj);
        } else if (obj instanceof Blob) {
            blobOpenAfter(chain.getDataSource().getDataSourceStat(), result, (Blob) obj);
        } else if (obj instanceof String) {
            result.addReadStringLength(((String) obj).length());
        }

        return obj;
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, String columnLabel,
                                      java.util.Map<String, Class<?>> map) throws SQLException {
        Object obj = chain.resultSet_getObject(result, columnLabel, map);

        if (obj instanceof Clob) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat(), result, (ClobProxy) obj);
        } else if (obj instanceof Blob) {
            blobOpenAfter(chain.getDataSource().getDataSourceStat(), result, (Blob) obj);
        } else if (obj instanceof String) {
            result.addReadStringLength(((String) obj).length());
        }

        return obj;
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                      throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterIndex);

        if (obj instanceof Clob) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat(), statement, (ClobProxy) obj);
        } else if (obj instanceof Blob) {
            blobOpenAfter(chain.getDataSource().getDataSourceStat(), statement, (Blob) obj);
        }

        return obj;
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, int parameterIndex,
                                              java.util.Map<String, Class<?>> map) throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterIndex, map);

        if (obj instanceof Clob) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat(), statement, (ClobProxy) obj);
        } else if (obj instanceof Blob) {
            blobOpenAfter(chain.getDataSource().getDataSourceStat(), statement, (Blob) obj);
        }

        return obj;
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                        throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterName);

        if (obj instanceof Clob) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat(), statement, (ClobProxy) obj);
        } else if (obj instanceof Blob) {
            blobOpenAfter(chain.getDataSource().getDataSourceStat(), statement, (Blob) obj);
        }

        return obj;
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement,
                                              String parameterName, java.util.Map<String, Class<?>> map)
                                                                                                        throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterName, map);

        if (obj instanceof Clob) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat(), statement, (ClobProxy) obj);
        } else if (obj instanceof Blob) {
            blobOpenAfter(chain.getDataSource().getDataSourceStat(), statement, (Blob) obj);
        }

        return obj;
    }

    private void blobOpenAfter(JdbcDataSourceStat dataSourceStat, ResultSetProxy rs, Blob blob) {
        blobOpenAfter(dataSourceStat, rs.getStatementProxy(), blob);
    }

    private void clobOpenAfter(JdbcDataSourceStat dataSourceStat, ResultSetProxy rs, ClobProxy clob) {
        clobOpenAfter(dataSourceStat, rs.getStatementProxy(), clob);
    }

    private void blobOpenAfter(JdbcDataSourceStat dataSourceStat, StatementProxy stmt, Blob blob) {
        dataSourceStat.incrementBlobOpenCount();

        if (stmt != null) {
            JdbcSqlStat sqlStat = stmt.getSqlStat();
            if (sqlStat != null) {
                sqlStat.incrementBlobOpenCount();
            }
        }

        StatFilterContext.getInstance().blob_open();
    }

    private void clobOpenAfter(JdbcDataSourceStat dataSourceStat, StatementProxy stmt, ClobProxy clob) {
        dataSourceStat.incrementClobOpenCount();

        if (stmt != null) {
            JdbcSqlStat sqlStat = stmt.getSqlStat();
            if (sqlStat != null) {
                sqlStat.incrementClobOpenCount();
            }
        }

        StatFilterContext.getInstance().clob_open();
    }

    @Override
    public String resultSet_getString(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        String value = chain.resultSet_getString(result, columnIndex);

        if (value != null) {
            result.addReadStringLength(value.length());
        }

        return value;
    }

    @Override
    public String resultSet_getString(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        String value = chain.resultSet_getString(result, columnLabel);

        if (value != null) {
            result.addReadStringLength(value.length());
        }

        return value;
    }

    @Override
    public byte[] resultSet_getBytes(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        byte[] value = chain.resultSet_getBytes(result, columnIndex);

        if (value != null) {
            result.addReadBytesLength(value.length);
        }

        return value;
    }

    @Override
    public byte[] resultSet_getBytes(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        byte[] value = chain.resultSet_getBytes(result, columnLabel);

        if (value != null) {
            result.addReadBytesLength(value.length);
        }

        return value;
    }

    @Override
    public InputStream resultSet_getBinaryStream(FilterChain chain, ResultSetProxy result, int columnIndex)
                                                                                                           throws SQLException {
        InputStream input = chain.resultSet_getBinaryStream(result, columnIndex);

        if (input != null) {
            result.incrementOpenInputStreamCount();
        }

        return input;
    }

    @Override
    public InputStream resultSet_getBinaryStream(FilterChain chain, ResultSetProxy result, String columnLabel)
                                                                                                              throws SQLException {
        InputStream input = chain.resultSet_getBinaryStream(result, columnLabel);

        if (input != null) {
            result.incrementOpenInputStreamCount();
        }

        return input;
    }

    @Override
    public InputStream resultSet_getAsciiStream(FilterChain chain, ResultSetProxy result, int columnIndex)
                                                                                                          throws SQLException {
        InputStream input = chain.resultSet_getAsciiStream(result, columnIndex);

        if (input != null) {
            result.incrementOpenInputStreamCount();
        }

        return input;
    }

    @Override
    public InputStream resultSet_getAsciiStream(FilterChain chain, ResultSetProxy result, String columnLabel)
                                                                                                             throws SQLException {
        InputStream input = chain.resultSet_getAsciiStream(result, columnLabel);

        if (input != null) {
            result.incrementOpenInputStreamCount();
        }

        return input;
    }

    @Override
    public Reader resultSet_getCharacterStream(FilterChain chain, ResultSetProxy result, int columnIndex)
                                                                                                         throws SQLException {
        Reader reader = chain.resultSet_getCharacterStream(result, columnIndex);

        if (reader != null) {
            result.incrementOpenReaderCount();
        }

        return reader;
    }

    @Override
    public Reader resultSet_getCharacterStream(FilterChain chain, ResultSetProxy result, String columnLabel)
                                                                                                            throws SQLException {
        Reader reader = chain.resultSet_getCharacterStream(result, columnLabel);

        if (reader != null) {
            result.incrementOpenReaderCount();
        }

        return reader;
    }
}
