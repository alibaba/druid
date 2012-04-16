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
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.JMException;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.TabularData;

import com.alibaba.druid.filter.Filter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterEventAdapter;
import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
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

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class StatFilter extends FilterEventAdapter implements StatFilterMBean {

    private final static Log          LOG                        = LogFactory.getLog(StatFilter.class);

    public final static String        ATTR_UPDATE_COUNT          = "stat.updteCount";
    public final static String        ATTR_TRANSACTION           = "stat.tx";

    protected JdbcDataSourceStat      dataSourceStat;

    @Deprecated
    protected final JdbcStatementStat statementStat              = JdbcStatManager.getInstance().getStatementStat();

    @Deprecated
    protected final JdbcResultSetStat resultSetStat              = JdbcStatManager.getInstance().getResultSetStat();

    private boolean                   connectionStackTraceEnable = false;

    protected DataSourceProxy         dataSource;

    protected final AtomicLong        resetCount                 = new AtomicLong();

    // 3 seconds is slow sql
    protected long                    slowSqlMillis              = 3 * 1000;

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

    public boolean isConnectionStackTraceEnable() {
        return connectionStackTraceEnable;
    }

    public void setConnectionStackTraceEnable(boolean connectionStackTraceEnable) {
        this.connectionStackTraceEnable = connectionStackTraceEnable;
    }

    public JdbcDataSourceStat getDataSourceStat() {
        return this.dataSourceStat;
    }

    public void reset() {
        dataSourceStat.reset();

        resetCount.incrementAndGet();
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

    public String mergeSql(String sql) {
        if (!mergeSql) {
            return sql;
        }

        try {
            sql = ParameterizedOutputVisitorUtils.parameterize(sql, dbType);
        } catch (Exception e) {
            LOG.error("merge sql error", e);
        }

        return sql;
    }

    @Override
    public synchronized void init(DataSourceProxy dataSource) {
        this.dataSource = dataSource;

        this.dataSourceStat = dataSource.getDataSourceStat();

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
    }

    @Override
    public synchronized void destory() {
        if (dataSource == null) {
            return;
        }

        dataSource = null;
    }

    public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
        ConnectionProxy connection = null;
        {
            long startNano = System.nanoTime();
            long startTime = System.currentTimeMillis();

            long nanoSpan;
            long nowTime = System.currentTimeMillis();

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
        long nowNano = System.nanoTime();

        dataSourceStat.getConnectionStat().incrementConnectionCloseCount();

        JdbcConnectionStat.Entry connectionInfo = getConnectionInfo(connection);

        long aliveNanoSpan = nowNano - connectionInfo.getEstablishNano();

        JdbcConnectionStat.Entry existsConnection = dataSourceStat.getConnections().remove(connection.getId());
        if (existsConnection != null) {
            dataSourceStat.getConnectionStat().afterClose(aliveNanoSpan);
        }

        chain.connection_close(connection);
        // duplicate close, C3P0等连接池，在某些情况下会关闭连接多次。
    }

    @Override
    public void connection_commit(FilterChain chain, ConnectionProxy connection) throws SQLException {
        chain.connection_commit(connection);

        dataSourceStat.getConnectionStat().incrementConnectionCommitCount();
    }

    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection) throws SQLException {
        chain.connection_rollback(connection);

        dataSourceStat.getConnectionStat().incrementConnectionRollbackCount();

        dataSourceStat.getConnectionStat().incrementConnectionRollbackCount();
    }

    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection, Savepoint savepoint)
                                                                                                       throws SQLException {
        chain.connection_rollback(connection, savepoint);

        dataSourceStat.getConnectionStat().incrementConnectionRollbackCount();
    }

    @Override
    public void statementCreateAfter(StatementProxy statement) {
        dataSourceStat.getStatementStat().incrementCreateCounter();

        super.statementCreateAfter(statement);
    }

    @Override
    public void statementPrepareCallAfter(CallableStatementProxy statement) {
        dataSourceStat.getStatementStat().incrementPrepareCallCount();

        JdbcSqlStat sqlStat = createSqlStat(statement, statement.getSql());
        statement.setSqlStat(sqlStat);
    }

    @Override
    public void statementPrepareAfter(PreparedStatementProxy statement) {
        dataSourceStat.getStatementStat().incrementPrepareCounter();
        JdbcSqlStat sqlStat = createSqlStat(statement, statement.getSql());
        statement.setSqlStat(sqlStat);
    }

    @Override
    public void statement_close(FilterChain chain, StatementProxy statement) throws SQLException {
        chain.statement_close(statement);

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
        internalAfterStatementExecute(statement, updateCount);
    }

    @Override
    protected void statementExecuteQueryBefore(StatementProxy statement, String sql) {
        internalBeforeStatementExecute(statement, sql);
    }

    @Override
    protected void statementExecuteQueryAfter(StatementProxy statement, String sql, ResultSetProxy resultSet) {
        internalAfterStatementExecute(statement);
    }

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
        internalBeforeStatementExecute(statement, sql);
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {
        internalAfterStatementExecute(statement);
    }

    @Override
    protected void statementExecuteBatchBefore(StatementProxy statement) {
        final String sql = statement.getBatchSql();

        final int batchSize = statement.getBatchSqlList().size();
        JdbcSqlStat sqlStat = statement.getSqlStat();
        if (sqlStat == null) {
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
        internalAfterStatementExecute(statement, result);

    }

    private final void internalBeforeStatementExecute(StatementProxy statement, String sql) {

        dataSourceStat.getStatementStat().beforeExecute();

        final JdbcStatementStat.Entry statementStat = getStatementInfo(statement);
        final ConnectionProxy connection = statement.getConnectionProxy();
        final JdbcConnectionStat.Entry connectionCounter = getConnectionInfo(connection);

        statementStat.setLastExecuteStartNano(System.nanoTime());
        statementStat.setLastExecuteSql(sql);

        connectionCounter.setLastSql(sql);

        if (connectionStackTraceEnable) {
            connectionCounter.setLastStatementStatckTrace(new Exception());
        }

        // //////////SQL

        JdbcSqlStat sqlStat = statement.getSqlStat();
        if (sqlStat == null) {
            sqlStat = createSqlStat(statement, sql);
            statement.setSqlStat(sqlStat);
        }

        JdbcStatContext statContext = JdbcStatManager.getInstance().getStatContext();
        if (statContext != null) {
            sqlStat.setName(statContext.getName());
            sqlStat.setFile(statContext.getFile());
        }

        if (sqlStat != null) {
            sqlStat.setExecuteLastStartTime(System.currentTimeMillis());
            sqlStat.incrementRunningCount();

            try {
                boolean inTransaction = !statement.getConnectionProxy().getAutoCommit();
                if (inTransaction) {
                    sqlStat.incrementInTransactionCount();
                }
            } catch (SQLException e) {
                LOG.error("getAutoCommit error", e);
            }
        }
    }

    private final void internalAfterStatementExecute(StatementProxy statement, int... updateCountArray) {

        final JdbcStatementStat.Entry entry = getStatementInfo(statement);

        long nowNano = System.nanoTime();

        long nanoSpan = nowNano - entry.getLastExecuteStartNano();

        dataSourceStat.getStatementStat().afterExecute(nanoSpan);

        // // SQL
        final JdbcSqlStat sqlStat = statement.getSqlStat();

        if (sqlStat != null) {
            sqlStat.incrementExecuteSuccessCount();
            for (int updateCount : updateCountArray) {
                sqlStat.addUpdateCount(updateCount);
            }

            sqlStat.decrementRunningCount();
            sqlStat.addExecuteTime(statement.getLastExecuteType(), nanoSpan);
            statement.setLastExecuteTimeNano(nanoSpan);
            if ((!statement.isFirstResultSet()) && statement.getLastExecuteType() == StatementExecuteType.Execute) {
            	try {
					int updateCount = statement.getUpdateCount();
					sqlStat.addUpdateCount(updateCount);
				} catch (SQLException e) {
					LOG.error("getUpdateCount error", e);
				}
            }

            long millis = nanoSpan / (1000 * 1000);
            if (millis >= slowSqlMillis) {
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
                sqlStat.setLastSlowParameters(buf.toString());
            }
        }
    }

    @Override
    protected void statement_executeErrorAfter(StatementProxy statement, String sql, Throwable error) {

        JdbcStatementStat.Entry counter = getStatementInfo(statement);
        ConnectionProxy connection = statement.getConnectionProxy();
        JdbcConnectionStat.Entry connectionCounter = getConnectionInfo(connection);

        long nanoSpan = System.nanoTime() - counter.getLastExecuteStartNano();

        dataSourceStat.getStatementStat().error(error);
        dataSourceStat.getStatementStat().afterExecute(nanoSpan);

        connectionCounter.error(error);

        // SQL
        JdbcSqlStat sqlStat = statement.getSqlStat();

        if (sqlStat != null) {
            sqlStat.error(error);
            sqlStat.addExecuteTime(statement.getLastExecuteType(), nanoSpan);
            statement.setLastExecuteTimeNano(nanoSpan);
        }

        super.statement_executeErrorAfter(statement, sql, error);
    }

    @Override
    protected void resultSetOpenAfter(ResultSetProxy resultSet) {
        dataSourceStat.getResultSetStat().beforeOpen();

        resultSet.setConstructNano();
    }

    @Override
    public void resultSet_close(FilterChain chain, ResultSetProxy resultSet) throws SQLException {

        long nanoSpan = System.nanoTime() - resultSet.getConstructNano();

        int fetchCount = resultSet.getFetchRowCount();

        dataSourceStat.getResultSetStat().afterClose(nanoSpan);

        dataSourceStat.getResultSetStat().addFetchRowCount(fetchCount);

        dataSourceStat.getResultSetStat().incrementCloseCounter();

        String sql = resultSet.getSql();
        if (sql != null) {
            JdbcSqlStat sqlStat = resultSet.getSqlStat();
            if (sqlStat != null) {
                sqlStat.addFetchRowCount(fetchCount);
                long stmtExecuteNano = resultSet.getStatementProxy().getLastExecuteTimeNano();
                sqlStat.addResultSetHoldTimeNano(stmtExecuteNano, nanoSpan);
            }
        }

        chain.resultSet_close(resultSet);
    }

    public final static String ATTR_NAME_CONNECTION_STAT = "stat.conn";
    public final static String ATTR_NAME_STATEMENT_STAT  = "stat.stmt";

    public JdbcConnectionStat.Entry getConnectionInfo(ConnectionProxy connection) {
        JdbcConnectionStat.Entry counter = (JdbcConnectionStat.Entry) connection.getAttributes().get(ATTR_NAME_CONNECTION_STAT);

        if (counter == null) {
            connection.getAttributes().put(ATTR_NAME_CONNECTION_STAT,
                                           new JdbcConnectionStat.Entry(this.dataSource.getName(), connection.getId()));
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

    @Override
    public long getConnectionActiveCount() {
        return dataSourceStat.getConnections().size();
    }

    @Override
    public long getConnectionCloseCount() {
        return dataSourceStat.getConnectionStat().getCloseCount();
    }

    @Override
    public long getConnectionCommitCount() {
        return dataSourceStat.getConnectionStat().getCommitCount();
    }

    @Override
    public long getConnectionConnectCount() {
        return dataSourceStat.getConnectionStat().getConnectCount();
    }

    @Override
    public long getConnectionConnectMillis() {
        return dataSourceStat.getConnectionStat().getConnectMillis();
    }

    @Override
    public long getConnectionConnectingMax() {
        return dataSourceStat.getConnectionStat().getConnectingMax();
    }

    @Override
    public long getConnectionRollbackCount() {
        return dataSourceStat.getConnectionStat().getConnectMillis();
    }

    @Override
    public long getConnectionConnectAliveMillis() {
        return dataSourceStat.getConnectionConnectAliveMillis();
    }

    @Override
    public long getConnectionConnectErrorCount() {
        return dataSourceStat.getConnectionStat().getConnectErrorCount();
    }

    @Override
    public Date getConnectionConnectLastTime() {
        return dataSourceStat.getConnectionStat().getConnectLastTime();
    }

    @Override
    public long getStatementCloseCount() {
        return dataSourceStat.getStatementStat().getCloseCount();
    }

    @Override
    public long getStatementCreateCount() {
        return dataSourceStat.getStatementStat().getCreateCount();
    }

    @Override
    public long getStatementExecuteMillisTotal() {
        return dataSourceStat.getStatementStat().getExecuteMillisTotal();
    }

    @Override
    public Date getStatementExecuteErrorLastTime() {
        return dataSourceStat.getStatementStat().getLastErrorTime();
    }

    @Override
    public Date getStatementExecuteLastTime() {
        return dataSourceStat.getStatementStat().getExecuteLastTime();
    }

    @Override
    public long getStatementPrepareCallCount() {
        return dataSourceStat.getStatementStat().getPrepareCallCount();
    }

    @Override
    public long getStatementPrepareCount() {
        return dataSourceStat.getStatementStat().getPrepareCount();
    }

    @Override
    public long getStatementExecuteErrorCount() {
        return dataSourceStat.getStatementStat().getErrorCount();
    }

    @Override
    public long getStatementExecuteSuccessCount() {
        return dataSourceStat.getStatementStat().getExecuteSuccessCount();
    }

    @Override
    public long getResultSetHoldMillisTotal() {
        return dataSourceStat.getResultSetStat().getHoldMillisTotal();
    }

    @Override
    public long getResultSetFetchRowCount() {
        return dataSourceStat.getResultSetStat().getFetchRowCount();
    }

    @Override
    public long getResultSetOpenCount() {
        return dataSourceStat.getResultSetStat().getOpenCount();
    }

    @Override
    public long getResultSetCloseCount() {
        return dataSourceStat.getResultSetStat().getCloseCount();
    }

    @Override
    public String getConnectionUrl() {
        return dataSource.getUrl();
    }

    public JdbcSqlStat createSqlStat(StatementProxy statement, String sql) {
        JdbcStatContext context = JdbcStatManager.getInstance().getStatContext();
    	String contextSql = context != null ? context.getSql() : null;
    	if (contextSql != null && contextSql.length() > 0) {
    		return dataSourceStat.createSqlStat(contextSql);
    	} else {
    		sql = mergeSql(sql);
    		return dataSourceStat.createSqlStat(sql);
    	}
    }

    public JdbcSqlStat getSqlCounter(String sql) {
    	
    	sql = mergeSql(sql); //mergeSql
        return getSqlStat(sql);
    }

    public JdbcSqlStat getSqlStat(String sql) {
    	
    	sql = mergeSql(sql);
        return dataSourceStat.getSqlStat(sql);
    }

    @Override
    public TabularData getSqlList() throws JMException {
        return dataSourceStat.getSqlList();
    }

    public static StatFilter getStatFilter(DataSourceProxy dataSource) {
        for (Filter filter : dataSource.getProxyFilters()) {
            if (filter instanceof StatFilter) {
                return (StatFilter) filter;
            }
        }

        return null;
    }

    public JdbcSqlStat getSqlStat(long id) {
        return dataSourceStat.getSqlStat(id);
    }

    @Override
    public CompositeData getStatementExecuteLastError() throws JMException {
        return dataSourceStat.getStatementStat().getLastError();
    }

    public final ConcurrentMap<Long, JdbcConnectionStat.Entry> getConnections() {
        return dataSourceStat.getConnections();
    }

    @Override
    public TabularData getConnectionList() throws JMException {
        return dataSourceStat.getConnectionList();
    }

    public static enum Feature {

        ;

        private Feature(){
            mask = (1 << ordinal());
        }

        private final int mask;

        public final int getMask() {
            return mask;
        }

        public static boolean isEnabled(int features, Feature feature) {
            return (features & feature.getMask()) != 0;
        }

        public static int config(int features, Feature feature, boolean state) {
            if (state) {
                features |= feature.getMask();
            } else {
                features &= ~feature.getMask();
            }

            return features;
        }
    }
}
