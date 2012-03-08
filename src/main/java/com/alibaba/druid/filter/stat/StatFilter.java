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
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
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

    private final static Log           LOG                        = LogFactory.getLog(StatFilter.class);

    public final static String         ATTR_SQL                   = "stat.sql";
    public final static String         ATTR_UPDATE_COUNT          = "stat.updteCount";

    protected JdbcDataSourceStat       dataSourceStat;

    protected final JdbcConnectionStat connectStat                = JdbcStatManager.getInstance().getConnectionstat();
    protected final JdbcStatementStat  statementStat              = JdbcStatManager.getInstance().getStatementStat();
    protected final JdbcResultSetStat  resultSetStat              = JdbcStatManager.getInstance().getResultSetStat();

    private boolean                    connectionStackTraceEnable = false;

    protected DataSourceProxy          dataSource;

    protected final AtomicLong         resetCount                 = new AtomicLong();

    protected int                      maxSqlStatCount            = 1000 * 100;

    public StatFilter(){
        String property = System.getProperty("druid.stat.maxSqlStatCount");
        if (property != null && property.length() > 0) {
            try {
                maxSqlStatCount = Integer.parseInt(property);
            } catch (Exception e) {

            }
        }
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

    public int getMaxSqlStatCount() {
        return maxSqlStatCount;
    }

    public void setMaxSqlStatCount(int maxSqlStatCount) {
        this.maxSqlStatCount = maxSqlStatCount;
    }

    public void reset() {
        dataSourceStat.reset();

        resetCount.incrementAndGet();
    }

    public long getResetCount() {
        return resetCount.get();
    }

    @Override
    public void init(DataSourceProxy dataSource) {
        this.dataSource = dataSource;

        ConcurrentMap<String, JdbcDataSourceStat> dataSourceStats = JdbcStatManager.getInstance().getDataSources();

        String url = dataSource.getUrl();
        JdbcDataSourceStat stat = dataSourceStats.get(url);
        if (stat == null) {
            dataSourceStats.putIfAbsent(url, new JdbcDataSourceStat(dataSource.getName(), url));
            stat = dataSourceStats.get(url);
        }
        this.dataSourceStat = stat;
    }

    public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
        ConnectionProxy connection = null;
        {
            long startNano = System.nanoTime();
            long startTime = System.currentTimeMillis();

            long nanoSpan;
            long nowTime = System.currentTimeMillis();

            connectStat.beforeConnect();
            dataSourceStat.getConnectionStat().beforeConnect();
            try {
                connection = super.connection_connect(chain, info);
                nanoSpan = System.nanoTime() - startNano;
            } catch (SQLException ex) {
                connectStat.connectError(ex);
                dataSourceStat.getConnectionStat().connectError(ex);
                throw ex;
            }
            connectStat.afterConnected(nanoSpan);
            dataSourceStat.getConnectionStat().afterConnected(nanoSpan);

            if (connection != null) {
                JdbcConnectionStat.Entry statEntry = getConnectionInfo(connection);

                dataSourceStat.getConnections().put(connection.getId(), statEntry);

                statEntry.setConnectTime(new Date(startTime));
                statEntry.setConnectTimespanNano(nanoSpan);
                statEntry.setEstablishNano(System.nanoTime());
                statEntry.setEstablishTime(nowTime);
                statEntry.setConnectStackTrace(new Exception());

                connectStat.setActiveCount(dataSourceStat.getConnections().size());
                dataSourceStat.getConnectionStat().setActiveCount(dataSourceStat.getConnections().size());
            }
        }
        return connection;
    }

    @Override
    public void connection_close(FilterChain chain, ConnectionProxy connection) throws SQLException {
        long nowNano = System.nanoTime();

        connectStat.incrementConnectionCloseCount();
        dataSourceStat.getConnectionStat().incrementConnectionCloseCount();

        JdbcConnectionStat.Entry connectionInfo = getConnectionInfo(connection);

        long aliveNanoSpan = nowNano - connectionInfo.getEstablishNano();

        JdbcConnectionStat.Entry existsConnection = dataSourceStat.getConnections().remove(connection.getId());
        if (existsConnection != null) {
            connectStat.afterClose(aliveNanoSpan);
            dataSourceStat.getConnectionStat().afterClose(aliveNanoSpan);
        }

        super.connection_close(chain, connection);
        // duplicate close, C3P0等连接池，在某些情况下会关闭连接多次。
    }

    @Override
    public void connection_commit(FilterChain chain, ConnectionProxy connection) throws SQLException {
        super.connection_commit(chain, connection);

        connectStat.incrementConnectionCommitCount();
        dataSourceStat.getConnectionStat().incrementConnectionCommitCount();
    }


    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection) throws SQLException {
        super.connection_rollback(chain, connection);

        connectStat.incrementConnectionRollbackCount();
        dataSourceStat.getConnectionStat().incrementConnectionRollbackCount();

        dataSourceStat.getConnectionStat().incrementConnectionRollbackCount();
    }

    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection, Savepoint savepoint)
                                                                                                       throws SQLException {
        super.connection_rollback(chain, connection, savepoint);

        connectStat.incrementConnectionRollbackCount();
        dataSourceStat.getConnectionStat().incrementConnectionRollbackCount();
    }

    @Override
    public void statementCreateAfter(StatementProxy statement) {
        statementStat.incrementCreateCounter();
        dataSourceStat.getStatementStat().incrementCreateCounter();

        super.statementCreateAfter(statement);
    }

    @Override
    public void statementPrepareCallAfter(CallableStatementProxy statement) {
        statementStat.incrementPrepareCallCount();
        dataSourceStat.getStatementStat().incrementPrepareCallCount();

        JdbcSqlStat sqlStat = createSqlStat(statement, statement.getSql());
        statement.getAttributes().put(ATTR_SQL, sqlStat);
        ;

        // super.statementPrepareCallAfter(statement);
    }

    @Override
    public void statementPrepareAfter(PreparedStatementProxy statement) {
        statementStat.incrementPrepareCounter();
        dataSourceStat.getStatementStat().incrementPrepareCounter();
        JdbcSqlStat sqlStat = createSqlStat(statement, statement.getSql());
        statement.getAttributes().put(ATTR_SQL, sqlStat);

        // super.statementPrepareAfter(statement);
    }

    @Override
    public void statement_close(FilterChain chain, StatementProxy statement) throws SQLException {
        super.statement_close(chain, statement);

        statementStat.incrementStatementCloseCounter();
        dataSourceStat.getStatementStat().incrementStatementCloseCounter();
    }

    @Override
    protected void statementExecuteUpdateBefore(StatementProxy statement, String sql) {
        internalBeforeStatementExecute(statement, sql);

        // super.statementExecuteUpdateBefore(statement, sql);
    }

    @Override
    protected void statementExecuteUpdateAfter(StatementProxy statement, String sql, int updateCount) {
        internalAfterStatementExecute(statement, updateCount);

        // super.statementExecuteUpdateAfter(statement, sql, updateCount);
    }

    @Override
    protected void statementExecuteQueryBefore(StatementProxy statement, String sql) {
        internalBeforeStatementExecute(statement, sql);

        // super.statementExecuteQueryBefore(statement, sql);
    }

    @Override
    protected void statementExecuteQueryAfter(StatementProxy statement, String sql, ResultSetProxy resultSet) {
        internalAfterStatementExecute(statement);

        // super.statementExecuteQueryAfter(statement, sql, resultSet);
    }

    @Override
    protected void statementExecuteBefore(StatementProxy statement, String sql) {
        internalBeforeStatementExecute(statement, sql);

        // super.statementExecuteBefore(statement, sql);
    }

    @Override
    protected void statementExecuteAfter(StatementProxy statement, String sql, boolean result) {
        internalAfterStatementExecute(statement);

        // super.statementExecuteAfter(statement, sql, result);
    }

    @Override
    protected void statementExecuteBatchBefore(StatementProxy statement) {
        final String sql = statement.getBatchSql();

        final int batchSize = statement.getBatchSqlList().size();
        JdbcSqlStat sqlStat = getSqlStat(statement);
        if (sqlStat == null) {
            sqlStat = createSqlStat(statement, sql);
            statement.getAttributes().put(ATTR_SQL, sqlStat);
        }

        if (sqlStat != null) {
            sqlStat.addExecuteBatchCount(batchSize);
        }

        internalBeforeStatementExecute(statement, sql);

        // super.statementExecuteBatchBefore(statement);
    }

    @Override
    protected void statementExecuteBatchAfter(StatementProxy statement, int[] result) {
        internalAfterStatementExecute(statement, result);

        // super.statementExecuteBatchAfter(statement, result);
    }

    private final void internalBeforeStatementExecute(StatementProxy statement, String sql) {

        statementStat.beforeExecute();
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

        JdbcSqlStat sqlStat = getSqlStat(statement);
        if (sqlStat == null) {
            sqlStat = createSqlStat(statement, sql);
            statement.getAttributes().put(ATTR_SQL, sqlStat);
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

        statement.getAttributes().put(ATTR_UPDATE_COUNT, 0);
    }

    @Override
    public int statement_getUpdateCount(FilterChain chain, StatementProxy statement) throws SQLException {
        int updateCount = chain.statement_getUpdateCount(statement);

        Integer attr = (Integer) statement.getAttributes().get(ATTR_UPDATE_COUNT);
        if (attr == null) {
            statement.getAttributes().put(ATTR_UPDATE_COUNT, updateCount);

            final JdbcSqlStat sqlStat = getSqlStat(statement);
            if (sqlStat != null) {
                sqlStat.addUpdateCount(updateCount);
            }
        }

        return updateCount;
    }

    private final void internalAfterStatementExecute(StatementProxy statement, int... updateCountArray) {

        final JdbcStatementStat.Entry entry = getStatementInfo(statement);

        long nowNano = System.nanoTime();

        long nanoSpan = nowNano - entry.getLastExecuteStartNano();

        statementStat.afterExecute(nanoSpan);
        dataSourceStat.getStatementStat().afterExecute(nanoSpan);

        // // SQL
        final JdbcSqlStat sqlStat = getSqlStat(statement);

        if (sqlStat != null) {
            sqlStat.incrementExecuteSuccessCount();
            for (int updateCount : updateCountArray) {
                sqlStat.addUpdateCount(updateCount);
            }

            sqlStat.decrementExecutingCount();
            sqlStat.addExecuteTime(nanoSpan);
        }

        if (updateCountArray.length == 1) {
            statement.getAttributes().put(ATTR_UPDATE_COUNT, updateCountArray[0]);
        }
    }

    @Override
    protected void statement_executeErrorAfter(StatementProxy statement, String sql, Throwable error) {

        JdbcStatementStat.Entry counter = getStatementInfo(statement);
        ConnectionProxy connection = statement.getConnectionProxy();
        JdbcConnectionStat.Entry connectionCounter = getConnectionInfo(connection);

        long nanoSpan = System.nanoTime() - counter.getLastExecuteStartNano();

        statementStat.error(error);
        dataSourceStat.getStatementStat().error(error);
        statementStat.afterExecute(nanoSpan);
        dataSourceStat.getStatementStat().afterExecute(nanoSpan);

        connectionCounter.error(error);

        // SQL
        JdbcSqlStat sqlStat = getSqlStat(statement);

        if (sqlStat != null) {
            sqlStat.error(error);
            sqlStat.addExecuteTime(nanoSpan);
        }

        super.statement_executeErrorAfter(statement, sql, error);
    }

    @Override
    protected void resultSetOpenAfter(ResultSetProxy resultSet) {
        resultSetStat.beforeOpen();
        dataSourceStat.getResultSetStat().beforeOpen();

        resultSet.setConstructNano();
        // super.resultSetOpenAfter(resultSet);
    }

    @Override
    public void resultSet_close(FilterChain chain, ResultSetProxy resultSet) throws SQLException {

        long nanoSpan = System.nanoTime() - resultSet.getConstructNano();

        int fetchCount = resultSet.getFetchRowCount();

        resultSetStat.afterClose(nanoSpan);
        dataSourceStat.getResultSetStat().afterClose(nanoSpan);

        resultSetStat.addFetchRowCount(fetchCount);
        dataSourceStat.getResultSetStat().addFetchRowCount(fetchCount);

        resultSetStat.incrementCloseCounter();
        dataSourceStat.getResultSetStat().incrementCloseCounter();

        String sql = resultSet.getSql();
        if (sql != null) {
            JdbcSqlStat sqlStat = getSqlCounter(sql);
            if (sqlStat != null) {
                sqlStat.addFetchRowCount(fetchCount);
            }
        }

        super.resultSet_close(chain, resultSet);
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
        final ConcurrentMap<String, JdbcSqlStat> sqlStatMap = dataSourceStat.getSqlStatMap();
        JdbcSqlStat sqlStat = sqlStatMap.get(sql);
        if (sqlStat == null) {
            if (dataSourceStat.getSqlStatMap().size() >= maxSqlStatCount) {
                return null;
            }

            JdbcSqlStat newSqlStat = new JdbcSqlStat(sql);
            if (dataSourceStat.getSqlStatMap().putIfAbsent(sql, newSqlStat) == null) {
                newSqlStat.setId(JdbcStatManager.getInstance().generateSqlId());
                newSqlStat.setDataSource(this.dataSource.getUrl());
            }

            sqlStat = dataSourceStat.getSqlStatMap().get(sql);
        }

        if (sqlStat == null) {
            LOG.error("stat is null");
        }

        return sqlStat;
    }

    public static JdbcSqlStat getSqlStat(StatementProxy statement) {
        if (statement == null) {
            return null;
        }

        return (JdbcSqlStat) statement.getAttributes().get(ATTR_SQL);
    }

    public JdbcSqlStat getSqlCounter(String sql) {
        return dataSourceStat.getSqlStatMap().get(sql);
    }

    public ConcurrentMap<String, JdbcSqlStat> getSqlStatisticMap() {
        return dataSourceStat.getSqlStatMap();
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
