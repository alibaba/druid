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

import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import com.alibaba.druid.DbType;
import com.alibaba.druid.VERSION;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.MySqlUtils;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidPooledStatement extends PoolableWrapper implements Statement {

    private final static Log        LOG            = LogFactory.getLog(DruidPooledStatement.class);

    private final Statement         stmt;
    protected DruidPooledConnection conn;
    protected List<ResultSet>       resultSetTrace;
    protected boolean               closed         = false;
    protected int                   fetchRowPeak   = -1;
    protected int                   exceptionCount = 0;

    public DruidPooledStatement(DruidPooledConnection conn, Statement stmt){
        super(stmt);

        this.conn = conn;
        this.stmt = stmt;
    }

    protected void addResultSetTrace(ResultSet resultSet) {
        if (resultSetTrace == null) {
            resultSetTrace = new ArrayList<ResultSet>(1);
        }
        resultSetTrace.add(resultSet);
    }

    protected void recordFetchRowCount(int fetchRowCount) {
        if (fetchRowPeak < fetchRowCount) {
            fetchRowPeak = fetchRowCount;
        }
    }

    public int getFetchRowPeak() {
        return fetchRowPeak;
    }

    protected SQLException checkException(Throwable error) throws SQLException {
        String sql = null;
        if (this instanceof DruidPooledPreparedStatement) {
            sql = ((DruidPooledPreparedStatement) this).getSql();
        }

        handleSocketTimeout(error);

        exceptionCount++;
        return conn.handleException(error, sql);
    }

    protected SQLException checkException(Throwable error, String sql) throws SQLException {
        handleSocketTimeout(error);

        exceptionCount++;
        return conn.handleException(error, sql);
    }

    protected void handleSocketTimeout(Throwable error) throws SQLException {
        if (this.conn == null
                || this.conn.transactionInfo != null
                || this.conn.holder == null) {
            return;
        }

        DruidDataSource dataSource = null;

        final DruidConnectionHolder holder = this.conn.holder;
        if (holder.dataSource instanceof DruidDataSource) {
            dataSource = (DruidDataSource) holder.dataSource;
        }
        if (dataSource == null) {
            return;
        }

        if (!dataSource.killWhenSocketReadTimeout) {
            return;
        }

        SQLException sqlException = null;
        if (error instanceof SQLException) {
            sqlException = (SQLException) error;
        }

        if (sqlException == null) {
            return;
        }


        Throwable cause = error.getCause();
        boolean socketReadTimeout = cause instanceof SocketTimeoutException
                && "Read timed out".equals(cause.getMessage());
        if (!socketReadTimeout) {
            return;
        }

        if (DbType.mysql != DbType.of(dataSource.dbTypeName)) {
            return;
        }

        String killQuery = MySqlUtils.buildKillQuerySql(this.conn.getConnection(), (SQLException) error);

        if (killQuery == null) {
            return;
        }

        DruidPooledConnection killQueryConn = null;
        Statement killQueryStmt = null;


        try {
            killQueryConn = dataSource.getConnection(1000);
            if (killQueryConn == null) {
                return;
            }

            killQueryStmt = killQueryConn.createStatement();
            killQueryStmt.execute(killQuery);

            if (LOG.isDebugEnabled()) {
                LOG.debug(killQuery + " success.");
            }
        } catch (Exception ex) {
            LOG.warn(killQuery + " error.", ex);
        } finally {
            JdbcUtils.close(killQueryStmt);
            JdbcUtils.close(killQueryConn);
        }
    }

    public DruidPooledConnection getPoolableConnection() {
        return conn;
    }

    public Statement getStatement() {
        return stmt;
    }

    protected void checkOpen() throws SQLException {
        if (closed) {
            Throwable disableError = null;
            if (this.conn != null) {
                disableError = this.conn.getDisableError();
            }

            if (disableError != null) {
                throw new SQLException("statement is closed", disableError);
            } else {
                throw new SQLException("statement is closed");
            }
        }
    }

    protected void clearResultSet() {
        if (resultSetTrace == null) {
            return;
        }

        for (ResultSet rs : resultSetTrace) {
            try {
                if (!rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException ex) {
                LOG.error("clearResultSet error", ex);
            }
        }
        resultSetTrace.clear();
    }

    public void incrementExecuteCount() {
        final DruidPooledConnection conn = this.getPoolableConnection();
        if (conn == null) {
            return;
        }

        final DruidConnectionHolder holder = conn.getConnectionHolder();
        if (holder == null) {
            return;
        }

        final DruidAbstractDataSource dataSource = holder.getDataSource();
        if (dataSource == null) {
            return;
        }

        dataSource.incrementExecuteCount();
    }

    public void incrementExecuteBatchCount() {
        final DruidPooledConnection conn = this.getPoolableConnection();
        if (conn == null) {
            return;
        }

        final DruidConnectionHolder holder = conn.getConnectionHolder();
        if (holder == null) {
            return;
        }

        if (holder.getDataSource() == null) {
            return;
        }

        final DruidAbstractDataSource dataSource = holder.getDataSource();
        if (dataSource == null) {
            return;
        }

        dataSource.incrementExecuteBatchCount();
    }

    public void incrementExecuteUpdateCount() {
        final DruidPooledConnection conn = this.getPoolableConnection();
        if (conn == null) {
            return;
        }

        final DruidConnectionHolder holder = conn.getConnectionHolder();
        if (holder == null) {
            return;
        }

        final DruidAbstractDataSource dataSource = holder.getDataSource();
        if (dataSource == null) {
            return;
        }

        dataSource.incrementExecuteUpdateCount();
    }

    public void incrementExecuteQueryCount() {
        final DruidPooledConnection conn = this.getPoolableConnection();
        if (conn == null) {
            return;
        }

        final DruidConnectionHolder holder = conn.getConnectionHolder();
        if (holder == null) {
            return;
        }

        final DruidAbstractDataSource dataSource = holder.getDataSource();
        if (dataSource == null) {
            return;
        }

        dataSource.incrementExecuteQueryCount();
    }

    protected void transactionRecord(String sql) throws SQLException {
        conn.transactionRecord(sql);
    }

    @Override
    public final ResultSet executeQuery(String sql) throws SQLException {
        checkOpen();

        incrementExecuteQueryCount();
        transactionRecord(sql);

        conn.beforeExecute();
        try {
            ResultSet rs = stmt.executeQuery(sql);

            if (rs == null) {
                return rs;
            }

            DruidPooledResultSet poolableResultSet = new DruidPooledResultSet(this, rs);
            addResultSetTrace(poolableResultSet);

            return poolableResultSet;
        } catch (Throwable t) {
            errorCheck(t);

            throw checkException(t, sql);
        } finally {
            conn.afterExecute();
        }
    }

    @Override
    public final int executeUpdate(String sql) throws SQLException {
        checkOpen();

        incrementExecuteUpdateCount();
        transactionRecord(sql);

        conn.beforeExecute();
        try {
            return stmt.executeUpdate(sql);
        } catch (Throwable t) {
            errorCheck(t);

            throw checkException(t, sql);
        } finally {
            conn.afterExecute();
        }
    }

    protected final void errorCheck(Throwable t) {
        String errorClassName = t.getClass().getName();
        if (errorClassName.endsWith(".CommunicationsException")
                && conn.holder != null
                && conn.holder.dataSource.testWhileIdle)
        {
            DruidConnectionHolder holder = conn.holder;
            DruidAbstractDataSource dataSource = holder.dataSource;

            long currentTimeMillis = System.currentTimeMillis();
            long lastActiveTimeMillis = holder.lastActiveTimeMillis;

            if (lastActiveTimeMillis < holder.lastKeepTimeMillis) {
                lastActiveTimeMillis = holder.lastKeepTimeMillis;
            }

            long idleMillis = currentTimeMillis - lastActiveTimeMillis;
            long lastValidIdleMillis = currentTimeMillis - holder.lastActiveTimeMillis;

            String errorMsg = "CommunicationsException, druid version " + VERSION.getVersionNumber()
                    + ", jdbcUrl : " + dataSource.jdbcUrl
                    + ", testWhileIdle " + dataSource.testWhileIdle
                    + ", idle millis " + idleMillis
                    + ", minIdle " + dataSource.minIdle
                    + ", poolingCount " + dataSource.getPoolingCount()
                    + ", timeBetweenEvictionRunsMillis " + dataSource.timeBetweenEvictionRunsMillis
                    + ", lastValidIdleMillis " + lastValidIdleMillis
                    + ", driver " + dataSource.driver.getClass().getName();

            if (dataSource.exceptionSorter != null) {
                errorMsg += ", exceptionSorter " + dataSource.exceptionSorter.getClass().getName();
            }

            LOG.error(errorMsg);
        }
    }

    @Override
    public final int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        checkOpen();

        incrementExecuteUpdateCount();
        transactionRecord(sql);

        conn.beforeExecute();
        try {
            return stmt.executeUpdate(sql, autoGeneratedKeys);
        } catch (Throwable t) {
            errorCheck(t);

            throw checkException(t, sql);
        } finally {
            conn.afterExecute();
        }
    }

    @Override
    public final int executeUpdate(String sql, int columnIndexes[]) throws SQLException {
        checkOpen();

        incrementExecuteUpdateCount();
        transactionRecord(sql);

        conn.beforeExecute();
        try {
            return stmt.executeUpdate(sql, columnIndexes);
        } catch (Throwable t) {
            errorCheck(t);

            throw checkException(t, sql);
        } finally {
            conn.afterExecute();
        }
    }

    @Override
    public final int executeUpdate(String sql, String columnNames[]) throws SQLException {
        checkOpen();

        incrementExecuteUpdateCount();
        transactionRecord(sql);

        conn.beforeExecute();
        try {
            return stmt.executeUpdate(sql, columnNames);
        } catch (Throwable t) {
            errorCheck(t);

            throw checkException(t, sql);
        } finally {
            conn.afterExecute();
        }
    }

    @Override
    public final boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        checkOpen();

        incrementExecuteCount();
        transactionRecord(sql);

        conn.beforeExecute();
        try {
            return stmt.execute(sql, autoGeneratedKeys);
        } catch (Throwable t) {
            errorCheck(t);

            throw checkException(t, sql);
        } finally {
            conn.afterExecute();
        }
    }

    @Override
    public final boolean execute(String sql, int columnIndexes[]) throws SQLException {
        checkOpen();

        incrementExecuteCount();
        transactionRecord(sql);

        conn.beforeExecute();
        try {
            return stmt.execute(sql, columnIndexes);
        } catch (Throwable t) {
            errorCheck(t);

            throw checkException(t, sql);
        } finally {
            conn.afterExecute();
        }
    }

    @Override
    public final boolean execute(String sql, String columnNames[]) throws SQLException {
        checkOpen();

        incrementExecuteCount();
        transactionRecord(sql);

        conn.beforeExecute();
        try {
            return stmt.execute(sql, columnNames);
        } catch (Throwable t) {
            errorCheck(t);

            throw checkException(t, sql);
        } finally {
            conn.afterExecute();
        }
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        checkOpen();

        try {
            return stmt.getMaxFieldSize();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void close() throws SQLException {
        if (this.closed) {
            return;
        }

        clearResultSet();
        if (stmt != null) {
            stmt.close();
        }
        this.closed = true;

        DruidConnectionHolder connHolder = conn.getConnectionHolder();
        if (connHolder != null) {
            connHolder.removeTrace(this);
        }
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        checkOpen();

        try {
            stmt.setMaxFieldSize(max);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int getMaxRows() throws SQLException {
        checkOpen();

        try {
            return stmt.getMaxRows();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        checkOpen();

        try {
            stmt.setMaxRows(max);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final void setEscapeProcessing(boolean enable) throws SQLException {
        checkOpen();

        try {
            stmt.setEscapeProcessing(enable);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int getQueryTimeout() throws SQLException {
        checkOpen();

        try {
            return stmt.getQueryTimeout();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        checkOpen();

        try {
            stmt.setQueryTimeout(seconds);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final void cancel() throws SQLException {
        checkOpen();

        try {
            stmt.cancel();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final SQLWarning getWarnings() throws SQLException {
        checkOpen();

        try {
            return stmt.getWarnings();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final void clearWarnings() throws SQLException {
        checkOpen();

        try {
            stmt.clearWarnings();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final void setCursorName(String name) throws SQLException {
        checkOpen();

        try {
            stmt.setCursorName(name);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final boolean execute(String sql) throws SQLException {
        checkOpen();

        incrementExecuteCount();
        transactionRecord(sql);

        try {
            return stmt.execute(sql);
        } catch (Throwable t) {
            errorCheck(t);

            throw checkException(t, sql);
        }
    }

    @Override
    public final ResultSet getResultSet() throws SQLException {
        checkOpen();

        try {
            ResultSet rs = stmt.getResultSet();
            if (rs == null) {
                return null;
            }

            DruidPooledResultSet poolableResultSet = new DruidPooledResultSet(this, rs);
            addResultSetTrace(poolableResultSet);

            return poolableResultSet;
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int getUpdateCount() throws SQLException {
        checkOpen();

        try {
            return stmt.getUpdateCount();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final boolean getMoreResults() throws SQLException {
        checkOpen();

        try {
            boolean moreResults = stmt.getMoreResults();

            if (resultSetTrace != null && resultSetTrace.size() > 0) {
                ResultSet lastResultSet = resultSetTrace.get(resultSetTrace.size() - 1);
                if (lastResultSet instanceof DruidPooledResultSet) {
                    DruidPooledResultSet pooledResultSet = ((DruidPooledResultSet) lastResultSet);
                    pooledResultSet.closed = true;
                }
            }

            return moreResults;
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        checkOpen();

        try {
            stmt.setFetchDirection(direction);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int getFetchDirection() throws SQLException {
        checkOpen();

        try {
            return stmt.getFetchDirection();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        checkOpen();

        try {
            stmt.setFetchSize(rows);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int getFetchSize() throws SQLException {
        checkOpen();

        try {
            return stmt.getFetchSize();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int getResultSetConcurrency() throws SQLException {
        checkOpen();

        try {
            return stmt.getResultSetConcurrency();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int getResultSetType() throws SQLException {
        checkOpen();

        try {
            return stmt.getResultSetType();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final void addBatch(String sql) throws SQLException {
        checkOpen();

        transactionRecord(sql);

        try {
            stmt.addBatch(sql);
        } catch (Throwable t) {
            throw checkException(t, sql);
        }
    }

    @Override
    public final void clearBatch() throws SQLException {
        if (closed) {
            return;
        }

        try {
            stmt.clearBatch();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public int[] executeBatch() throws SQLException {
        checkOpen();

        incrementExecuteBatchCount();

        conn.beforeExecute();
        try {
            return stmt.executeBatch();
        } catch (Throwable t) {
            errorCheck(t);

            throw checkException(t);
        } finally {
            conn.afterExecute();
        }
    }

    @Override
    public final Connection getConnection() throws SQLException {
        checkOpen();

        return conn;
    }

    @Override
    public final boolean getMoreResults(int current) throws SQLException {
        checkOpen();

        try {
            boolean results = stmt.getMoreResults(current);

            if (resultSetTrace != null && resultSetTrace.size() > 0) {
                ResultSet lastResultSet = resultSetTrace.get(resultSetTrace.size() - 1);
                if (lastResultSet instanceof DruidPooledResultSet) {
                    DruidPooledResultSet pooledResultSet = ((DruidPooledResultSet) lastResultSet);
                    pooledResultSet.closed = true;
                }
            }

            return results;
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final ResultSet getGeneratedKeys() throws SQLException {
        checkOpen();

        try {
            ResultSet rs = stmt.getGeneratedKeys();

            DruidPooledResultSet poolableResultSet = new DruidPooledResultSet(this, rs);

            addResultSetTrace(poolableResultSet);

            return poolableResultSet;
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final int getResultSetHoldability() throws SQLException {
        checkOpen();

        try {
            return stmt.getResultSetHoldability();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public final boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public final void setPoolable(boolean poolable) throws SQLException {
        if (poolable) {
            return;
        }

        throw new SQLException("not support");
    }

    @Override
    public final boolean isPoolable() throws SQLException {
        return false;
    }

    public String toString() {
        return stmt.toString();
    }

    public void closeOnCompletion() throws SQLException {
        stmt.closeOnCompletion();
    }

    public boolean isCloseOnCompletion() throws SQLException {
        return stmt.isCloseOnCompletion();
    }
}
