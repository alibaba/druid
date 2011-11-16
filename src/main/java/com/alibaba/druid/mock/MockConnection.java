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
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

public class MockConnection implements Connection {

    // private final static Log LOG = LogFactory.getLog(MockConnection.class);

    private boolean    autoCommit           = false;
    private boolean    closed               = false;
    private boolean    readOnly             = false;
    private String     catalog              = null;
    private int        transactionIsolation;
    private SQLWarning warning;
    private int        holdability;

    private MockDriver driver;

    private Properties connectProperties;

    private long       id;

    private final long createdTimeMillis    = System.currentTimeMillis();
    private long       lastActiveTimeMillis = System.currentTimeMillis();

    public MockConnection(){
        this(null);
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

    public MockConnection(MockDriver driver){
        this(driver, new Properties());
    }

    public MockConnection(MockDriver driver, Properties connectProperties){
        this.driver = driver;
        this.connectProperties = connectProperties;

        if (driver != null) {
            this.id = driver.generateConnectionId();
        }
    }

    public long getId() {
        return id;
    }

    public MockDriver getDriver() {
        return driver;
    }

    public Properties getConnectProperties() {
        return connectProperties;
    }

    public void setDriver(MockDriver driver) {
        this.driver = driver;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == MockConnection.class) {
            return (T) this;
        }

        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return iface == MockConnection.class;
    }

    @Override
    public Statement createStatement() throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }

        return new MockStatement(this);
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }

        return new MockPreparedStatement(this, sql);
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }

        return new MockCallableStatement(this, sql);
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }

        return sql;
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }

        this.autoCommit = autoCommit;
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        return autoCommit;
    }

    @Override
    public void commit() throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }

    }

    @Override
    public void rollback() throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }

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
    public void setReadOnly(boolean readOnly) throws SQLException {
        this.readOnly = readOnly;
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        return readOnly;
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        this.catalog = catalog;
    }

    @Override
    public String getCatalog() throws SQLException {
        return catalog;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        this.transactionIsolation = level;
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        return transactionIsolation;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return warning;
    }

    @Override
    public void clearWarnings() throws SQLException {
        warning = null;
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency) throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }

        MockStatement stmt = new MockStatement(this);

        stmt.setResultSetType(resultSetType);
        stmt.setResultSetConcurrency(resultSetConcurrency);

        return stmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
                                                                                                      throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }

        MockPreparedStatement stmt = new MockPreparedStatement(this, sql);

        stmt.setResultSetType(resultSetType);
        stmt.setResultSetConcurrency(resultSetConcurrency);

        return stmt;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }

        MockCallableStatement stmt = new MockCallableStatement(this, sql);

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
    public void setHoldability(int holdability) throws SQLException {
        this.holdability = holdability;
    }

    @Override
    public int getHoldability() throws SQLException {
        return holdability;
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }
        return null;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }
        return null;
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }
    }

    @Override
    public Statement createStatement(int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                                                                                                           throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }

        MockStatement stmt = new MockStatement(this);

        stmt.setResultSetType(resultSetType);
        stmt.setResultSetConcurrency(resultSetConcurrency);
        stmt.setResultSetHoldability(resultSetHoldability);

        return stmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }

        MockPreparedStatement stmt = new MockPreparedStatement(this, sql);

        stmt.setResultSetType(resultSetType);
        stmt.setResultSetConcurrency(resultSetConcurrency);
        stmt.setResultSetHoldability(resultSetHoldability);

        return stmt;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }

        MockCallableStatement stmt = new MockCallableStatement(this, sql);

        stmt.setResultSetType(resultSetType);
        stmt.setResultSetConcurrency(resultSetConcurrency);
        stmt.setResultSetHoldability(resultSetHoldability);

        return stmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }

        return new MockPreparedStatement(this, sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }

        return new MockPreparedStatement(this, sql);
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        if (closed) {
            throw new MockConnectionClosedException();
        }

        return new MockPreparedStatement(this, sql);
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

}
