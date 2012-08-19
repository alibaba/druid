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
package com.alibaba.druid.filter.stat;

import java.io.InputStream;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Date;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;

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
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class StatFilter extends FilterEventAdapter implements StatFilterMBean {

    private final static Log          LOG                        = LogFactory.getLog(StatFilter.class);
    public final static String        ATTR_NAME_CONNECTION_STAT  = "stat.conn";
    public final static String        ATTR_NAME_STATEMENT_STAT   = "stat.stmt";
    public final static String        ATTR_UPDATE_COUNT          = "stat.updteCount";
    public final static String        ATTR_TRANSACTION           = "stat.tx";
    public final static String        ATTR_RESULTSET_CLOSED      = "stat.rs.closed";

    // protected JdbcDataSourceStat dataSourceStat;

    @Deprecated
    protected final JdbcStatementStat statementStat              = JdbcStatManager.getInstance().getStatementStat();

    @Deprecated
    protected final JdbcResultSetStat resultSetStat              = JdbcStatManager.getInstance().getResultSetStat();

    private boolean                   connectionStackTraceEnable = false;

    protected final AtomicLong        resetCount                 = new AtomicLong();

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

    public boolean isConnectionStackTraceEnable() {
        return connectionStackTraceEnable;
    }

    public void setConnectionStackTraceEnable(boolean connectionStackTraceEnable) {
        this.connectionStackTraceEnable = connectionStackTraceEnable;
    }

    public long getResetCount() {
        return resetCount.get();
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
            LOG.error("merge sql error, dbType " + dbType + ", sql : \n" + sql, e);
        }

        return sql;
    }

    @Override
    public synchronized void init(DataSourceProxy dataSource) {
        if (this.dbType == null || this.dbType.trim().length() == 0) {
            this.dbType = dataSource.getDbType();
        }

        initFromProperties(dataSource.getConnectProperties());
        initFromProperties(System.getProperties());
    }

    private void initFromProperties(Properties properties) {
        if (properties == null) {
            return;
        }

        {
            String property = properties.getProperty("druid.stat.mergeSql");
            if ("true".equals(property)) {
                this.mergeSql = true;
            } else if ("false".equals(property)) {
                this.mergeSql = false;
            }
        }

        {
            String property = properties.getProperty("druid.stat.slowSqlMillis");
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
            String property = properties.getProperty("druid.stat.logSlowSql");
            if ("true".equals(property)) {
                this.logSlowSql = true;
            } else if ("false".equals(property)) {
                this.logSlowSql = false;
            }
        }
    }

    public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
        ConnectionProxy connection = null;
        {
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

        final long startNano = System.nanoTime();

        JdbcDataSourceStat dataSourceStat = statement.getConnectionProxy().getDirectDataSource().getDataSourceStat();
        dataSourceStat.getStatementStat().beforeExecute();

        final JdbcStatementStat.Entry statementStat = getStatementInfo(statement);
        final ConnectionProxy connection = statement.getConnectionProxy();
        final JdbcConnectionStat.Entry connectionCounter = getConnectionInfo(connection);

        statementStat.setLastExecuteStartNano(startNano);
        statementStat.setLastExecuteSql(sql);

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
    }

    private final void internalAfterStatementExecute(StatementProxy statement, boolean firstResult,
                                                     int... updateCountArray) {

        final JdbcStatementStat.Entry entry = getStatementInfo(statement);

        final long nowNano = System.nanoTime();
        final long nanoSpan = nowNano - entry.getLastExecuteStartNano();

        JdbcDataSourceStat dataSourceStat = statement.getConnectionProxy().getDirectDataSource().getDataSourceStat();
        dataSourceStat.getStatementStat().afterExecute(nanoSpan);

        final JdbcSqlStat sqlStat = statement.getSqlStat();

        if (sqlStat != null) {
            sqlStat.incrementExecuteSuccessCount();

            sqlStat.decrementRunningCount();
            sqlStat.addExecuteTime(statement.getLastExecuteType(), firstResult, nanoSpan);
            statement.setLastExecuteTimeNano(nanoSpan);
            if ((!statement.isFirstResultSet()) && statement.getLastExecuteType() == StatementExecuteType.Execute) {
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

            long millis = nanoSpan / (1000 * 1000);
            if (millis >= slowSqlMillis) {
                StringBuilder buf = buildSlowParameters(statement);
                sqlStat.setLastSlowParameters(buf.toString());

                if (logSlowSql) {
                    LOG.error("slow sql " + millis + " millis. \n" + statement.getLastExecuteSql() + "\n"
                              + buf.toString());
                }
            }
        }

        String sql = statement.getLastExecuteSql();
        StatFilterContext.getInstance().executeAfter(sql, nanoSpan, null);
    }

    @Override
    protected void statement_executeErrorAfter(StatementProxy statement, String sql, Throwable error) {

        JdbcStatementStat.Entry counter = getStatementInfo(statement);
        ConnectionProxy connection = statement.getConnectionProxy();
        JdbcConnectionStat.Entry connectionCounter = getConnectionInfo(connection);

        long nanoSpan = System.nanoTime() - counter.getLastExecuteStartNano();

        JdbcDataSourceStat dataSourceStat = statement.getConnectionProxy().getDirectDataSource().getDataSourceStat();
        dataSourceStat.getStatementStat().error(error);
        dataSourceStat.getStatementStat().afterExecute(nanoSpan);

        connectionCounter.error(error);

        // SQL
        JdbcSqlStat sqlStat = statement.getSqlStat();

        if (sqlStat != null) {
            sqlStat.error(error);
            sqlStat.addExecuteTime(statement.getLastExecuteType(), statement.isFirstResultSet(), nanoSpan);
            statement.setLastExecuteTimeNano(nanoSpan);
        }

        StatFilterContext.getInstance().executeAfter(sql, nanoSpan, error);
    }

    private StringBuilder buildSlowParameters(StatementProxy statement) {
        StringBuilder buf = new StringBuilder();
        buf.append('[');
        int index = 0;
        for (JdbcParameter parameter : statement.getParameters().values()) {
            if (index != 0) {
                buf.append(',');
            }
            Object value = parameter.getValue();
            if (value == null) {
                buf.append("null");
            } else if (value instanceof String) {
                buf.append('"');
                String text = (String) value;
                if (text.length() > 100) {
                    for (int i = 0; i < 97; ++i) {
                        char ch = text.charAt(i);
                        if (ch == '\'') {
                            buf.append('\\');
                            buf.append(ch);
                        } else {
                            buf.append(ch);
                        }
                    }
                    buf.append("...");
                } else {
                    for (int i = 0; i < text.length(); ++i) {
                        char ch = text.charAt(i);
                        if (ch == '\'') {
                            buf.append('\\');
                            buf.append(ch);
                        } else {
                            buf.append(ch);
                        }
                    }
                }
                buf.append('"');
            } else if (value instanceof Number) {
                buf.append(value.toString());
            } else if (value instanceof java.util.Date) {
                java.util.Date date = (java.util.Date) value;
                buf.append(date.getClass().getSimpleName());
                buf.append('(');
                buf.append(date.getTime());
                buf.append(')');
            } else if (value instanceof Boolean) {
                buf.append(value.toString());
            } else if (value instanceof InputStream) {
                buf.append("<InputStream>");
            } else if (value instanceof Clob) {
                buf.append("<Clob>");
            } else if (value instanceof NClob) {
                buf.append("<NClob>");
            } else if (value instanceof Blob) {
                buf.append("<Blob>");
            } else {
                buf.append('<');
                buf.append(value.getClass().getName());
                buf.append('>');
            }
            index++;
        }
        buf.append(']');
        return buf;
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
            }
        }

        chain.resultSet_close(resultSet);

        StatFilterContext.getInstance().resultSet_close(nanos);
    }

    public JdbcConnectionStat.Entry getConnectionInfo(ConnectionProxy connection) {
        JdbcConnectionStat.Entry counter = (JdbcConnectionStat.Entry) connection.getAttributes().get(ATTR_NAME_CONNECTION_STAT);

        if (counter == null) {
            String dataSourceName = connection.getDirectDataSource().getName();
            connection.getAttributes().put(ATTR_NAME_CONNECTION_STAT,
                                           new JdbcConnectionStat.Entry(dataSourceName, connection.getId()));
            counter = (JdbcConnectionStat.Entry) connection.getAttributes().get(ATTR_NAME_CONNECTION_STAT);
        }

        return counter;
    }

    public JdbcStatementStat.Entry getStatementInfo(StatementProxy statement) {
        JdbcStatementStat.Entry counter = (JdbcStatementStat.Entry) statement.getAttributes().get(ATTR_NAME_STATEMENT_STAT);

        if (counter == null) {
            statement.getAttributes().put(ATTR_NAME_STATEMENT_STAT, new JdbcStatementStat.Entry());
            counter = (JdbcStatementStat.Entry) statement.getAttributes().get(ATTR_NAME_STATEMENT_STAT);
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
            conn.setConnectedTimeNano(System.nanoTime());

            StatFilterContext.getInstance().pool_connection_open();
        }

        return conn;
    }

    @Override
    public Clob resultSet_getClob(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException {
        Clob clob = chain.resultSet_getClob(resultSet, columnIndex);

        if (clob != null) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat());
        }

        return clob;
    }

    @Override
    public Clob resultSet_getClob(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException {
        Clob clob = chain.resultSet_getClob(resultSet, columnLabel);

        if (clob != null) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat());
        }

        return clob;
    }

    @Override
    public Clob callableStatement_getClob(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                  throws SQLException {
        Clob clob = chain.callableStatement_getClob(statement, parameterIndex);

        if (clob != null) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat());
        }

        return clob;
    }

    @Override
    public Clob callableStatement_getClob(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                    throws SQLException {
        Clob clob = chain.callableStatement_getClob(statement, parameterName);

        if (clob != null) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat());
        }

        return clob;
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        Object obj = chain.resultSet_getObject(result, columnIndex);

        if (obj instanceof Clob) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat());
        }

        return obj;
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, int columnIndex,
                                      java.util.Map<String, Class<?>> map) throws SQLException {
        Object obj = chain.resultSet_getObject(result, columnIndex, map);

        if (obj instanceof Clob) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat());
        }

        return obj;
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        Object obj = chain.resultSet_getObject(result, columnLabel);

        if (obj instanceof Clob) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat());
        }

        return obj;
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, String columnLabel,
                                      java.util.Map<String, Class<?>> map) throws SQLException {
        Object obj = chain.resultSet_getObject(result, columnLabel, map);

        if (obj instanceof Clob) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat());
        }

        return obj;
    }
    
    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                      throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterIndex);

        if (obj instanceof Clob) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat());
        }

        return obj;
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, int parameterIndex,
                                              java.util.Map<String, Class<?>> map) throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterIndex, map);

        if (obj instanceof Clob) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat());
        }

        return obj;
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                        throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterName);

        if (obj instanceof Clob) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat());
        }

        return obj;
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement,
                                              String parameterName, java.util.Map<String, Class<?>> map)
                                                                                                        throws SQLException {
        Object obj = chain.callableStatement_getObject(statement, parameterName, map);

        if (obj instanceof Clob) {
            clobOpenAfter(chain.getDataSource().getDataSourceStat());
        }

        return obj;
    }

    private void clobOpenAfter(JdbcDataSourceStat dataSourceStat) {
        dataSourceStat.incrementClobOpenCount();
    }

    @Override
    public void clob_free(FilterChain chain, ClobProxy wrapper) throws SQLException {
        chain.clob_free(wrapper);

        JdbcDataSourceStat dataSourceStat = chain.getDataSource().getDataSourceStat();
        dataSourceStat.incrementClobFreeCount();
    }

}
