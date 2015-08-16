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
package com.alibaba.druid.proxy.jdbc;

import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.filter.stat.StatFilter;

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
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class ConnectionProxyImpl extends WrapperProxyImpl implements ConnectionProxy {

    private final Connection      connection;

    private final DataSourceProxy dataSource;

    private final Properties      properties;

    private final long            connectedTime;

    private TransactionInfo       transactionInfo;

    private int                   closeCount;

    private FilterChainImpl       filterChain = null;

    public ConnectionProxyImpl(DataSourceProxy dataSource, Connection connection, Properties properties, long id){
        super(connection, id);
        this.dataSource = dataSource;
        this.connection = connection;
        this.properties = properties;
        this.connectedTime = System.currentTimeMillis();
    }

    public Date getConnectedTime() {
        return new Date(connectedTime);
    }

    public Properties getProperties() {
        return properties;
    }

    public Connection getConnectionRaw() {
        return connection;
    }

    public Connection getRawObject() {
        return connection;
    }

    public DataSourceProxy getDirectDataSource() {
        return this.dataSource;
    }

    public FilterChainImpl createChain() {
        FilterChainImpl chain = this.filterChain;
        if (chain == null) {
            chain = new FilterChainImpl(dataSource);
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
    public void clearWarnings() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.connection_clearWarnings(this);
        recycleFilterChain(chain);
    }

    @Override
    public void close() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.connection_close(this);
        closeCount++;
        recycleFilterChain(chain);
    }

    @Override
    public void commit() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.connection_commit(this);

        if (transactionInfo != null) {
            transactionInfo.setEndTimeMillis();
        }
        recycleFilterChain(chain);
    }

    @Override
    public Array createArrayOf(String typeName, Object[] elements) throws SQLException {
        FilterChainImpl chain = createChain();
        Array value = chain.connection_createArrayOf(this, typeName, elements);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Blob createBlob() throws SQLException {
        FilterChainImpl chain = createChain();
        Blob value = chain.connection_createBlob(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Clob createClob() throws SQLException {
        FilterChainImpl chain = createChain();
        Clob value = chain.connection_createClob(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public NClob createNClob() throws SQLException {
        FilterChainImpl chain = createChain();
        NClob value = chain.connection_createNClob(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public SQLXML createSQLXML() throws SQLException {
        FilterChainImpl chain = createChain();
        SQLXML value = chain.connection_createSQLXML(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Statement createStatement() throws SQLException {
        FilterChainImpl chain = createChain();
        Statement stmt = chain.connection_createStatement(this);
        recycleFilterChain(chain);
        return stmt;
    }

    @Override
    public Statement createStatement(int resultSetType, //
                                     int resultSetConcurrency //
    ) throws SQLException {
        FilterChainImpl chain = createChain();
        Statement stmt = chain.connection_createStatement(this, resultSetType, resultSetConcurrency);
        recycleFilterChain(chain);
        return stmt;
    }

    @Override
    public Statement createStatement(int resultSetType, //
                                     int resultSetConcurrency, //
                                     int resultSetHoldability //
    ) throws SQLException {
        FilterChainImpl chain = createChain();
        Statement stmt = chain.connection_createStatement(this, resultSetType, resultSetConcurrency,
                                                          resultSetHoldability);
        recycleFilterChain(chain);
        return stmt;
    }

    @Override
    public Struct createStruct(String typeName, Object[] attributes) throws SQLException {
        FilterChainImpl chain = createChain();
        Struct value = chain.connection_createStruct(this, typeName, attributes);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean getAutoCommit() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.connection_getAutoCommit(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public String getCatalog() throws SQLException {
        FilterChainImpl chain = createChain();
        String value = chain.connection_getCatalog(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Properties getClientInfo() throws SQLException {
        FilterChainImpl chain = createChain();
        Properties value = chain.connection_getClientInfo(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public String getClientInfo(String name) throws SQLException {
        FilterChainImpl chain = createChain();
        String value = chain.connection_getClientInfo(this, name);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getHoldability() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.connection_getHoldability(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public DatabaseMetaData getMetaData() throws SQLException {
        FilterChainImpl chain = createChain();
        DatabaseMetaData value = chain.connection_getMetaData(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getTransactionIsolation() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.connection_getTransactionIsolation(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Map<String, Class<?>> getTypeMap() throws SQLException {
        FilterChainImpl chain = createChain();
        Map<String, Class<?>> value = chain.connection_getTypeMap(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        FilterChainImpl chain = createChain();
        SQLWarning value = chain.connection_getWarnings(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isClosed() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.connection_isClosed(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isReadOnly() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.connection_isReadOnly(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isValid(int timeout) throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.connection_isValid(this, timeout);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public String nativeSQL(String sql) throws SQLException {
        FilterChainImpl chain = createChain();
        String value = chain.connection_nativeSQL(this, sql);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public CallableStatement prepareCall(String sql) throws SQLException {
        FilterChainImpl chain = createChain();
        CallableStatement stmt = chain.connection_prepareCall(this, sql);
        recycleFilterChain(chain);
        return stmt;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency) throws SQLException {
        FilterChainImpl chain = createChain();
        CallableStatement stmt = chain.connection_prepareCall(this, sql, resultSetType, resultSetConcurrency);
        recycleFilterChain(chain);
        return stmt;
    }

    @Override
    public CallableStatement prepareCall(String sql, int resultSetType, int resultSetConcurrency,
                                         int resultSetHoldability) throws SQLException {
        FilterChainImpl chain = createChain();
        CallableStatement stmt = chain.connection_prepareCall(this, sql, resultSetType, resultSetConcurrency,
                                                    resultSetHoldability);
        recycleFilterChain(chain);
        return stmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql) throws SQLException {
        FilterChainImpl chain = createChain();
        PreparedStatement stmt = chain.connection_prepareStatement(this, sql);
        recycleFilterChain(chain);
        return stmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int autoGeneratedKeys) throws SQLException {
        FilterChainImpl chain = createChain();
        PreparedStatement stmt = chain.connection_prepareStatement(this, sql, autoGeneratedKeys);
        recycleFilterChain(chain);
        return stmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int[] columnIndexes) throws SQLException {
        FilterChainImpl chain = createChain();
        PreparedStatement stmt = chain.connection_prepareStatement(this, sql, columnIndexes);
        recycleFilterChain(chain);
        return stmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, String[] columnNames) throws SQLException {
        FilterChainImpl chain = createChain();
        PreparedStatement stmt = chain.connection_prepareStatement(this, sql, columnNames);
        recycleFilterChain(chain);
        return stmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency)
                                                                                                      throws SQLException {
        FilterChainImpl chain = createChain();
        PreparedStatement stmt = chain.connection_prepareStatement(this, sql, resultSetType, resultSetConcurrency);
        recycleFilterChain(chain);
        return stmt;
    }

    @Override
    public PreparedStatement prepareStatement(String sql, int resultSetType, int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException {
        FilterChainImpl chain = createChain();
        PreparedStatement stmt = chain.connection_prepareStatement(this, sql, resultSetType, resultSetConcurrency,
                                                         resultSetHoldability);
        recycleFilterChain(chain);
        return stmt;
    }

    @Override
    public void releaseSavepoint(Savepoint savepoint) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.connection_releaseSavepoint(this, savepoint);
        recycleFilterChain(chain);
    }

    @Override
    public void rollback() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.connection_rollback(this);
        recycleFilterChain(chain);
        if (transactionInfo != null) {
            transactionInfo.setEndTimeMillis();
        }
    }

    @Override
    public void rollback(Savepoint savepoint) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.connection_rollback(this, savepoint);
        recycleFilterChain(chain);
        if (transactionInfo != null) {
            transactionInfo.setEndTimeMillis();
        }
    }

    @Override
    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (!autoCommit) {
            if (transactionInfo == null) {
                long transactionId = this.dataSource.createTransactionId();
                transactionInfo = new TransactionInfo(transactionId);
                this.putAttribute(StatFilter.ATTR_TRANSACTION, transactionInfo); // compatible for druid 0.1.18
            }
        } else {
            transactionInfo = null;
        }

        FilterChainImpl chain = createChain();
        chain.connection_setAutoCommit(this, autoCommit);
        recycleFilterChain(chain);
    }

    @Override
    public void setCatalog(String catalog) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.connection_setCatalog(this, catalog);
        recycleFilterChain(chain);
    }

    @Override
    public void setClientInfo(Properties properties) throws SQLClientInfoException {
        FilterChainImpl chain = createChain();
        chain.connection_setClientInfo(this, properties);
        recycleFilterChain(chain);
    }

    @Override
    public void setClientInfo(String name, String value) throws SQLClientInfoException {
        FilterChainImpl chain = createChain();
        chain.connection_setClientInfo(this, name, value);
        recycleFilterChain(chain);
    }

    @Override
    public void setHoldability(int holdability) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.connection_setHoldability(this, holdability);
        recycleFilterChain(chain);
    }

    @Override
    public void setReadOnly(boolean readOnly) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.connection_setReadOnly(this, readOnly);
        recycleFilterChain(chain);
    }

    @Override
    public Savepoint setSavepoint() throws SQLException {
        FilterChainImpl chain = createChain();
        Savepoint savepoint = chain.connection_setSavepoint(this);
        recycleFilterChain(chain);
        return savepoint;
    }

    @Override
    public Savepoint setSavepoint(String name) throws SQLException {
        FilterChainImpl chain = createChain();
        Savepoint savepoint = chain.connection_setSavepoint(this, name);
        recycleFilterChain(chain);
        return savepoint;
    }

    @Override
    public void setTransactionIsolation(int level) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.connection_setTransactionIsolation(this, level);
        recycleFilterChain(chain);
    }

    @Override
    public void setTypeMap(Map<String, Class<?>> map) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.connection_setTypeMap(this, map);
        recycleFilterChain(chain);
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

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == this.getClass() || iface == ConnectionProxy.class) {
            return (T) this;
        }

        return super.unwrap(iface);
    }

    @Override
    public TransactionInfo getTransactionInfo() {
        return transactionInfo;
    }

    @Override
    public int getCloseCount() {
        return closeCount;
    }
}
