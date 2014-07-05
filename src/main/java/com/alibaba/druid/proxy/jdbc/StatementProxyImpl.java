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
package com.alibaba.druid.proxy.jdbc;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.stat.JdbcSqlStat;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class StatementProxyImpl extends WrapperProxyImpl implements StatementProxy {

    private final ConnectionProxy  connection;
    private final Statement        statement;

    protected String               lastExecuteSql;
    protected long                 lastExecuteStartNano;
    protected long                 lastExecuteTimeNano;

    protected JdbcSqlStat          sqlStat;
    protected boolean              firstResultSet;

    protected ArrayList<String>    batchSqlList;

    protected StatementExecuteType lastExecuteType;

    protected Integer     updateCount = null;

    private FilterChainImpl        filterChain = null;

    public StatementProxyImpl(ConnectionProxy connection, Statement statement, long id){
        super(statement, id);
        this.connection = connection;
        this.statement = statement;
    }

    public ConnectionProxy getConnectionProxy() {
        return connection;
    }

    public Statement getRawObject() {
        return this.statement;
    }

    public FilterChainImpl createChain() {
        FilterChainImpl chain = this.filterChain;
        if (chain == null) {
            chain = new FilterChainImpl(this.getConnectionProxy().getDirectDataSource());
        } else {
            this.filterChain = null;
        }

        return chain;
    }

    public void recycleFilterChain(FilterChainImpl chain) {
        chain.reset();
        this.filterChain = chain;
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        if (batchSqlList == null) {
            batchSqlList = new ArrayList<String>();
        }

        createChain().statement_addBatch(this, sql);
        batchSqlList.add(sql);
    }

    @Override
    public void cancel() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_cancel(this);
        recycleFilterChain(chain);
    }

    @Override
    public void clearBatch() throws SQLException {
        if (batchSqlList == null) {
            batchSqlList = new ArrayList<String>();
        }

        FilterChainImpl chain = createChain();
        chain.statement_clearBatch(this);
        recycleFilterChain(chain);
        batchSqlList.clear();
    }

    @Override
    public void clearWarnings() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_clearWarnings(this);
        recycleFilterChain(chain);
    }

    @Override
    public void close() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_close(this);
        recycleFilterChain(chain);
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        updateCount = null;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.Execute;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;

        FilterChainImpl chain = createChain();
        firstResultSet = chain.statement_execute(this, sql);
        recycleFilterChain(chain);
        return firstResultSet;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        updateCount = null;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.Execute;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;

        FilterChainImpl chain = createChain();
        firstResultSet = chain.statement_execute(this, sql, autoGeneratedKeys);
        recycleFilterChain(chain);
        return firstResultSet;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        updateCount = null;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.Execute;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;

        FilterChainImpl chain = createChain();
        firstResultSet = chain.statement_execute(this, sql, columnIndexes);
        recycleFilterChain(chain);
        return firstResultSet;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        updateCount = null;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.Execute;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;

        FilterChainImpl chain = createChain();
        firstResultSet = chain.statement_execute(this, sql, columnNames);
        recycleFilterChain(chain);
        return firstResultSet;
    }

    @Override
    public int[] executeBatch() throws SQLException {
        firstResultSet = false;
        lastExecuteType = StatementExecuteType.ExecuteBatch;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;

        FilterChainImpl chain = createChain();
        int[] updateCounts = chain.statement_executeBatch(this);
        recycleFilterChain(chain);
        if (updateCounts != null && updateCounts.length == 1) {
            updateCount = updateCounts[0];
        }

        return updateCounts;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        firstResultSet = true;
        updateCount = null;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.ExecuteQuery;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;

        FilterChainImpl chain = createChain();
        ResultSet resultSet = chain.statement_executeQuery(this, sql);
        recycleFilterChain(chain);
        return resultSet;
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        firstResultSet = false;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.ExecuteUpdate;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;

        FilterChainImpl chain = createChain();
        updateCount = chain.statement_executeUpdate(this, sql);
        recycleFilterChain(chain);
        return updateCount;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        firstResultSet = false;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.ExecuteUpdate;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;

        FilterChainImpl chain = createChain();
        updateCount = chain.statement_executeUpdate(this, sql, autoGeneratedKeys);
        recycleFilterChain(chain);
        return updateCount;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        firstResultSet = false;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.ExecuteUpdate;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;

        FilterChainImpl chain = createChain();
        updateCount = chain.statement_executeUpdate(this, sql, columnIndexes);
        recycleFilterChain(chain);
        return updateCount;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        firstResultSet = false;
        lastExecuteSql = sql;
        lastExecuteType = StatementExecuteType.ExecuteUpdate;
        lastExecuteStartNano = -1L;
        lastExecuteTimeNano = -1L;

        FilterChainImpl chain = createChain();
        updateCount = chain.statement_executeUpdate(this, sql, columnNames);
        recycleFilterChain(chain);
        return updateCount;
    }

    @Override
    public Connection getConnection() throws SQLException {
        FilterChainImpl chain = createChain();
        Connection conn = chain.statement_getConnection(this);
        recycleFilterChain(chain);
        return conn;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.statement_getFetchDirection(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getFetchSize() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.statement_getFetchSize(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        FilterChainImpl chain = createChain();
        ResultSet value = chain.statement_getGeneratedKeys(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.statement_getMaxFieldSize(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getMaxRows() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.statement_getMaxRows(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.statement_getMoreResults(this);
        updateCount = null;
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        updateCount = null;
        FilterChainImpl chain = createChain();
        boolean value = chain.statement_getMoreResults(this, current);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.statement_getQueryTimeout(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        FilterChainImpl chain = createChain();
        ResultSet value = chain.statement_getResultSet(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.statement_getResultSetConcurrency(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.statement_getResultSetHoldability(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getResultSetType() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.statement_getResultSetType(this);
        recycleFilterChain(chain);
        return value;
    }

    // bug fixed for oracle
    @Override
    public int getUpdateCount() throws SQLException {
        if (updateCount == null) {
            FilterChainImpl chain = createChain();
            updateCount = chain.statement_getUpdateCount(this);
            recycleFilterChain(chain);
        }
        return updateCount;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        FilterChainImpl chain = createChain();
        SQLWarning value = chain.statement_getWarnings(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isClosed() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.statement_isClosed(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isPoolable() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.statement_isPoolable(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_setCursorName(this, name);
        recycleFilterChain(chain);
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_setEscapeProcessing(this, enable);
        recycleFilterChain(chain);
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_setFetchDirection(this, direction);
        recycleFilterChain(chain);
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_setFetchSize(this, rows);
        recycleFilterChain(chain);
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_setMaxFieldSize(this, max);
        recycleFilterChain(chain);
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_setMaxRows(this, max);
        recycleFilterChain(chain);
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_setPoolable(this, poolable);
        recycleFilterChain(chain);
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.statement_setQueryTimeout(this, seconds);
        recycleFilterChain(chain);
    }

    @Override
    public List<String> getBatchSqlList() {
        if (batchSqlList == null) {
            batchSqlList = new ArrayList<String>();
        }

        return batchSqlList;
    }

    @Override
    public String getBatchSql() {
        List<String> sqlList = getBatchSqlList();
        StringBuffer buf = new StringBuffer();
        for (String item : sqlList) {
            if (buf.length() > 0) {
                buf.append("\n;\n");
            }
            buf.append(item);
        }
        return buf.toString();
    }

    public String getLastExecuteSql() {
        return lastExecuteSql;
    }

    public void closeOnCompletion() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public boolean isCloseOnCompletion() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public Map<Integer, JdbcParameter> getParameters() {
        return Collections.emptyMap();
    }

    public JdbcSqlStat getSqlStat() {
        return sqlStat;
    }

    public void setSqlStat(JdbcSqlStat sqlStat) {
        this.sqlStat = sqlStat;
    }

    public long getLastExecuteTimeNano() {
        return lastExecuteTimeNano;
    }

    public void setLastExecuteTimeNano(long lastExecuteTimeNano) {
        this.lastExecuteTimeNano = lastExecuteTimeNano;
    }

    public void setLastExecuteTimeNano() {
        if (this.lastExecuteTimeNano <= 0 && this.lastExecuteStartNano > 0) {
            this.lastExecuteTimeNano = System.nanoTime() - this.lastExecuteStartNano;
        }
    }

    public long getLastExecuteStartNano() {
        return lastExecuteStartNano;
    }

    public void setLastExecuteStartNano(long lastExecuteStartNano) {
        this.lastExecuteStartNano = lastExecuteStartNano;
        this.lastExecuteTimeNano = -1L;
    }

    public void setLastExecuteStartNano() {
        if (lastExecuteStartNano <= 0) {
            setLastExecuteStartNano(System.nanoTime());
        }
    }

    public StatementExecuteType getLastExecuteType() {
        return lastExecuteType;
    }

    public boolean isFirstResultSet() {
        return firstResultSet;
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == StatementProxy.class) {
            return (T) this;
        }

        return super.unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface == StatementProxy.class) {
            return true;
        }

        return super.isWrapperFor(iface);
    }

    @Override
    public int getParametersSize() {
        return 0;
    }

    @Override
    public JdbcParameter getParameter(int i) {
        return null;
    }
}
