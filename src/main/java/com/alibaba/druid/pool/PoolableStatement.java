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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class PoolableStatement extends PoolableWrapper implements Statement {

    private final Statement         stmt;
    protected PoolableConnection    conn;
    protected final List<ResultSet> resultSetTrace = new ArrayList<ResultSet>();

    public PoolableStatement(PoolableConnection conn, Statement stmt){
        this.conn = conn;
        this.stmt = stmt;
    }

    public PoolableConnection getPoolableConnection() {
        return conn;
    }

    public void setPoolableConnection(PoolableConnection conn) {
        this.conn = conn;
    }

    protected void clearResultSet() {
        for (ResultSet rs : resultSetTrace) {
            try {
                if (!rs.isClosed()) {
                    rs.close();
                }
            } catch (SQLException ex) {
                ex.printStackTrace(); //
            }
        }
        resultSetTrace.clear();
    }

    @Override
    public final ResultSet executeQuery(String sql) throws SQLException {
        ResultSet rs = stmt.executeQuery(sql);

        PoolableResultSet poolableResultSet = new PoolableResultSet(this, rs);
        resultSetTrace.add(poolableResultSet);

        return poolableResultSet;
    }

    @Override
    public final int executeUpdate(String sql) throws SQLException {
        return stmt.executeUpdate(sql);
    }

    @Override
    public void close() throws SQLException {
        clearResultSet();

        stmt.close();

        conn.getConnectionHolder().removeTrace(this);
    }

    @Override
    public final int getMaxFieldSize() throws SQLException {
        return stmt.getMaxFieldSize();
    }

    @Override
    public final void setMaxFieldSize(int max) throws SQLException {
        stmt.setMaxFieldSize(max);
    }

    @Override
    public final int getMaxRows() throws SQLException {
        return stmt.getMaxRows();
    }

    @Override
    public final void setMaxRows(int max) throws SQLException {
        stmt.setMaxRows(max);
    }

    @Override
    public final void setEscapeProcessing(boolean enable) throws SQLException {
        stmt.setEscapeProcessing(enable);
    }

    @Override
    public final int getQueryTimeout() throws SQLException {
        return stmt.getQueryTimeout();
    }

    @Override
    public final void setQueryTimeout(int seconds) throws SQLException {
        stmt.setQueryTimeout(seconds);
    }

    @Override
    public final void cancel() throws SQLException {
        stmt.cancel();
    }

    @Override
    public final SQLWarning getWarnings() throws SQLException {
        return stmt.getWarnings();
    }

    @Override
    public final void clearWarnings() throws SQLException {
        stmt.clearWarnings();
    }

    @Override
    public final void setCursorName(String name) throws SQLException {
        stmt.setCursorName(name);
    }

    @Override
    public final boolean execute(String sql) throws SQLException {
        return stmt.execute(sql);
    }

    @Override
    public final ResultSet getResultSet() throws SQLException {
        ResultSet rs = stmt.getResultSet();

        PoolableResultSet poolableResultSet = new PoolableResultSet(this, rs);
        resultSetTrace.add(poolableResultSet);

        return poolableResultSet;
    }

    @Override
    public final int getUpdateCount() throws SQLException {
        return stmt.getUpdateCount();
    }

    @Override
    public final boolean getMoreResults() throws SQLException {
        return stmt.getMoreResults();
    }

    @Override
    public final void setFetchDirection(int direction) throws SQLException {
        stmt.setFetchDirection(direction);
    }

    @Override
    public final int getFetchDirection() throws SQLException {
        return stmt.getFetchDirection();
    }

    @Override
    public final void setFetchSize(int rows) throws SQLException {
        stmt.setFetchSize(rows);
    }

    @Override
    public final int getFetchSize() throws SQLException {
        return stmt.getFetchSize();
    }

    @Override
    public final int getResultSetConcurrency() throws SQLException {
        return stmt.getResultSetConcurrency();
    }

    @Override
    public final int getResultSetType() throws SQLException {
        return stmt.getResultSetType();
    }

    @Override
    public final void addBatch(String sql) throws SQLException {
        stmt.addBatch(sql);
    }

    @Override
    public final void clearBatch() throws SQLException {
        stmt.clearBatch();
    }

    @Override
    public final int[] executeBatch() throws SQLException {
        return stmt.executeBatch();
    }

    @Override
    public final Connection getConnection() throws SQLException {
        return conn;
    }

    @Override
    public final boolean getMoreResults(int current) throws SQLException {
        return stmt.getMoreResults(current);
    }

    @Override
    public final ResultSet getGeneratedKeys() throws SQLException {
        ResultSet rs = stmt.getGeneratedKeys();

        PoolableResultSet poolableResultSet = new PoolableResultSet(this, rs);

        resultSetTrace.add(poolableResultSet);

        return poolableResultSet;
    }

    @Override
    public final int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        return stmt.executeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public final int executeUpdate(String sql, int columnIndexes[]) throws SQLException {
        return stmt.executeUpdate(sql, columnIndexes);
    }

    @Override
    public final int executeUpdate(String sql, String columnNames[]) throws SQLException {
        return stmt.executeUpdate(sql, columnNames);
    }

    @Override
    public final boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        return stmt.execute(sql, autoGeneratedKeys);
    }

    @Override
    public final boolean execute(String sql, int columnIndexes[]) throws SQLException {
        return stmt.execute(sql, columnIndexes);
    }

    @Override
    public final boolean execute(String sql, String columnNames[]) throws SQLException {
        return stmt.execute(sql, columnNames);
    }

    @Override
    public final int getResultSetHoldability() throws SQLException {
        return stmt.getResultSetHoldability();
    }

    @Override
    public final boolean isClosed() throws SQLException {
        return stmt.isClosed();
    }

    @Override
    public final void setPoolable(boolean poolable) throws SQLException {
        throw new UnsupportedOperationException();
    }

    @Override
    public final boolean isPoolable() throws SQLException {
        return false;
    }

    public String toString() {
        return stmt.toString();
    }
}
