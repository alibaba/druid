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
package com.alibaba.druid.mock;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.Statement;

public class MockStatement implements Statement {

    public final static String ERROR_SQL = "THROW ERROR";

    protected boolean          closed    = false;

    private Connection         connection;
    protected MockConnection   mockConnection;
    private int                maxFieldSize;
    private int                maxRows;
    private int                queryTimeout;
    private boolean            escapeProcessing;
    private SQLWarning         warnings;
    private String             cursorName;
    private int                updateCount;
    private int                fetchDirection;
    private int                fetchSize;

    private int                resultSetType;
    private int                resultSetConcurrency;
    private int                resultSetHoldability;

    public MockStatement(Connection connection){
        super();
        this.connection = connection;

        if (connection instanceof MockConnection) {
            mockConnection = (MockConnection) connection;
        }
    }
    
    protected void checkOpen() throws SQLException, MockConnectionClosedException {
        if (closed) {
            throw new SQLException();
        }
        
        if (this.mockConnection != null && this.mockConnection.isClosed()) {
            throw new MockConnectionClosedException();
        }
    }

    public int getResultSetType() throws SQLException {
        checkOpen();

        return resultSetType;
    }

    public void setResultSetType(int resultType) {
        this.resultSetType = resultType;
    }

    public void setResultSetConcurrency(int resultSetConcurrency) {
        this.resultSetConcurrency = resultSetConcurrency;
    }

    public MockConnection getMockConnection() {
        return mockConnection;
    }

    public void setFakeConnection(MockConnection fakeConnection) {
        this.mockConnection = fakeConnection;
        this.connection = fakeConnection;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == null) {
            return null;
        }

        if (iface.isInstance(this)) {
            return (T) this;
        }

        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface == null) {
            return false;
        }

        if (iface.isInstance(this)) {
            return true;
        }

        return false;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        checkOpen();

        if (mockConnection != null && mockConnection.getDriver() != null) {
            return mockConnection.getDriver().executeQuery(this, sql);
        }

        return new MockResultSet(this);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        checkOpen();

        return 0;
    }

    @Override
    public void close() throws SQLException {
        this.closed = true;
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        checkOpen();

        return maxFieldSize;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        checkOpen();

        this.maxFieldSize = max;
    }

    @Override
    public int getMaxRows() throws SQLException {
        checkOpen();

        return maxRows;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        checkOpen();

        this.maxRows = max;
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        checkOpen();
        this.escapeProcessing = enable;
    }

    public boolean isEscapeProcessing() {
        return escapeProcessing;
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        checkOpen();
        return queryTimeout;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        checkOpen();
        this.queryTimeout = seconds;
    }

    @Override
    public void cancel() throws SQLException {
        checkOpen();
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        checkOpen();

        return warnings;
    }

    @Override
    public void clearWarnings() throws SQLException {
        checkOpen();

        warnings = null;
    }

    public void setWarning(SQLWarning warning) {
        this.warnings = warning;
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        checkOpen();

        cursorName = name;
    }

    public String getCursorName() {
        return cursorName;
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        checkOpen();

        if (ERROR_SQL.equals(sql)) {
            throw new SQLException();
        }

        return false;
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        checkOpen();

        return new MockResultSet(this);
    }

    @Override
    public int getUpdateCount() throws SQLException {
        checkOpen();

        return updateCount;
    }

    public void setUpdateCount(int updateCount) {
        this.updateCount = updateCount;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        checkOpen();

        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        checkOpen();

        this.fetchDirection = direction;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        checkOpen();
        return fetchDirection;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        checkOpen();
        this.fetchSize = rows;
    }

    @Override
    public int getFetchSize() throws SQLException {
        checkOpen();
        return fetchSize;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        checkOpen();
        return resultSetConcurrency;
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        checkOpen();
    }

    @Override
    public void clearBatch() throws SQLException {
        checkOpen();
    }

    @Override
    public int[] executeBatch() throws SQLException {
        checkOpen();
        return new int[0];
    }

    @Override
    public Connection getConnection() throws SQLException {
        checkOpen();
        return connection;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        checkOpen();
        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        checkOpen();
        return new MockResultSet(this);
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        checkOpen();
        return 0;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        checkOpen();
        return 0;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        checkOpen();
        return 0;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        checkOpen();
        return false;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        checkOpen();
        return false;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        checkOpen();
        return false;
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        checkOpen();
        return resultSetHoldability;
    }

    public void setResultSetHoldability(int resultSetHoldability) {
        this.resultSetHoldability = resultSetHoldability;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        checkOpen();
    }

    @Override
    public boolean isPoolable() throws SQLException {
        checkOpen();
        return false;
    }

    public void closeOnCompletion() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public boolean isCloseOnCompletion() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

}
