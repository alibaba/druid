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
package com.alibaba.druid.mock;

import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import com.alibaba.druid.util.jdbc.ConnectionBase;

public class MockConnection extends ConnectionBase implements Connection {

    // private final static Log LOG = LogFactory.getLog(MockConnection.class);

    private boolean         closed               = false;

    private MockDriver      driver;
    private int             savepointIdSeed      = 0;
    private List<Savepoint> savepoints           = new ArrayList<Savepoint>();

    private long            id;

    private final long      createdTimeMillis    = System.currentTimeMillis();
    private long            lastActiveTimeMillis = System.currentTimeMillis();

    private SQLException    error;

    private String          lastSql;

    public MockConnection(){
        this(null, null, null);
    }

    public MockConnection(MockDriver driver, String url, Properties connectProperties){
        super(url, connectProperties);

        this.driver = driver;

        if (driver != null) {
            this.id = driver.generateConnectionId();
        }
    }

    public String getLastSql() {
        return lastSql;
    }

    public void setLastSql(String lastSql) {
        this.lastSql = lastSql;
    }

    public SQLException getError() {
        return error;
    }

    public void setError(SQLException error) {
        this.error = error;
    }

    public List<Savepoint> getSavepoints() {
        return savepoints;
    }

    public long getLastActiveTimeMillis() {
        return lastActiveTimeMillis;
    }

    public void setLastActiveTimeMillis(long lastActiveTimeMillis) {
        this.lastActiveTimeMillis = lastActiveTimeMillis;
    }

    public long getCreatedTimeMillis() {
        return createdTimeMillis;
    }

    public long getId() {
        return id;
    }

    public MockDriver getDriver() {
        return driver;
    }

    public void setDriver(MockDriver driver) {
        this.driver = driver;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface.isInstance(this)) {
            return (T) this;
        }

        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface.isInstance(this);
    }

    @Override
    public Statement createStatement() throws SQLException {
        checkState();

        return createMockStatement();
    }

    private MockStatement createMockStatement() {
        if (driver != null) {
            return driver.createMockStatement(this);
        }
        return new MockStatement(this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        checkState();

        return createMockPreparedStatement(sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        checkState();

        return createMockCallableStatement(sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        checkState();

        return sql;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        checkState();

        super.setAutoCommit(autoCommit);
    }

    @Override
    public void commit() throws SQLException {
        checkState();
    }

    @Override
    public void rollback() throws SQLException {
        checkState();

        this.savepoints.clear();
    }

    @Override
    public void close() throws SQLException {
        if (!closed) {
            closed = true;
            if (driver != null) {
                driver.afterConnectionClose(this);
            }
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        return null;
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        checkState();

        MockStatement stmt = createMockStatement();

        stmt.setResultSetType(resultSetType);
        stmt.setResultSetConcurrency(resultSetConcurrency);

        return stmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
                                                                                                      throws SQLException {
        checkState();

        MockPreparedStatement stmt = createMockPreparedStatement(sql);

        stmt.setResultSetType(resultSetType);
        stmt.setResultSetConcurrency(resultSetConcurrency);

        return stmt;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        checkState();

        MockCallableStatement stmt = createMockCallableStatement(sql);

        stmt.setResultSetType(resultSetType);
        stmt.setResultSetConcurrency(resultSetConcurrency);

        return stmt;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        return null;
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {

    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        checkState();

        MockSavepoint savepoint = new MockSavepoint();
        savepoint.setSavepointId(this.savepointIdSeed++);
        this.savepoints.add(savepoint);

        return savepoint;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        checkState();

        MockSavepoint savepoint = new MockSavepoint();
        savepoint.setSavepointId(this.savepointIdSeed++);
        savepoint.setSavepointName(name);
        this.savepoints.add(savepoint);

        return savepoint;
    }

    public void checkState() throws SQLException {
        if (error != null) {
            throw error;
        }

        if (closed) {
            throw new MockConnectionClosedException();
        }
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        checkState();

        int index = this.savepoints.indexOf(savepoint);
        if (index == -1) {
            throw new SQLException("savepoint not contained");
        }
        for (int i = savepoints.size() - 1; i >= index; --i) {
            savepoints.remove(i);
        }
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        checkState();

        if (savepoint == null) {
            throw new SQLException("argument is null");
        }

        int index = this.savepoints.indexOf(savepoint);
        if (index == -1) {
            throw new SQLException("savepoint not contained");
        }
        savepoints.remove(savepoint);
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                                                                                                           throws SQLException {
        checkState();

        MockStatement stmt = createMockStatement();

        stmt.setResultSetType(resultSetType);
        stmt.setResultSetConcurrency(resultSetConcurrency);
        stmt.setResultSetHoldability(resultSetHoldability);

        return stmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        checkState();

        MockPreparedStatement stmt = createMockPreparedStatement(sql);

        stmt.setResultSetType(resultSetType);
        stmt.setResultSetConcurrency(resultSetConcurrency);
        stmt.setResultSetHoldability(resultSetHoldability);

        return stmt;
    }

    private MockPreparedStatement createMockPreparedStatement(String sql) {
        if (driver != null) {
            return driver.createMockPreparedStatement(this, sql);
        }
        return new MockPreparedStatement(this, sql);
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        checkState();

        MockCallableStatement stmt = createMockCallableStatement(sql);

        stmt.setResultSetType(resultSetType);
        stmt.setResultSetConcurrency(resultSetConcurrency);
        stmt.setResultSetHoldability(resultSetHoldability);

        return stmt;
    }

    private MockCallableStatement createMockCallableStatement(String sql) {
        if (driver != null) {
            return driver.createMockCallableStatement(this, sql);
        }
        return new MockCallableStatement(this, sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        checkState();

        return createMockPreparedStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        checkState();

        return createMockPreparedStatement(sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        checkState();

        return createMockPreparedStatement(sql);
    }

    @Override
    public Clob createClob() throws SQLException {
        if (driver != null) {
            return driver.createClob(this);
        }

        return new MockClob();
    }

    @Override
    public Blob createBlob() throws SQLException {
        if (driver != null) {
            return driver.createBlob(this);
        }

        return new MockBlob();
    }

    @Override
    public NClob createNClob() throws SQLException {
        if (driver != null) {
            return driver.createNClob(this);
        }

        return new MockNClob();
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        if (driver != null) {
            return driver.createSQLXML(this);
        }

        return new MockSQLXML();
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        return false;
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {

    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {

    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        return null;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        return new Properties();
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        return new MockArray();
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        return new MockStruct();
    }

    public void setSchema(String schema) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public String getSchema() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public void abort(Executor executor) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public int getNetworkTimeout() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        checkState();
        super.setReadOnly(readOnly);
    }

    public void handleSleep() {
        if (getConnectProperties() != null) {
            Object propertyValue = getConnectProperties().get("executeSleep");

            if (propertyValue != null) {
                long millis = Long.parseLong(propertyValue.toString());
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    // skip
                }
            }
        }
    }
}
