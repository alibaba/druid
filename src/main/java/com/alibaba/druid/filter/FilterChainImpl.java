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
package com.alibaba.druid.filter;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Driver;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Wrapper;
import java.util.Calendar;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxyImpl;
import com.alibaba.druid.proxy.jdbc.ClobProxy;
import com.alibaba.druid.proxy.jdbc.ClobProxyImpl;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.NClobProxy;
import com.alibaba.druid.proxy.jdbc.NClobProxyImpl;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxyImpl;
import com.alibaba.druid.proxy.jdbc.ResultSetMetaDataProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetMetaDataProxyImpl;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxyImpl;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxyImpl;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class FilterChainImpl implements FilterChain {

    protected int                 pos = 0;

    private final DataSourceProxy dataSource;

    private final int             filterSize;

    public FilterChainImpl(DataSourceProxy dataSource){
        this.dataSource = dataSource;
        this.filterSize = getFilters().size();
    }

    public FilterChainImpl(DataSourceProxy dataSource, int pos){
        this.dataSource = dataSource;
        this.pos = pos;
        this.filterSize = getFilters().size();
    }

    public int getFilterSize() {
        return filterSize;
    }

    public int getPos() {
        return pos;
    }

    public void reset() {
        pos = 0;
    }

    @Override
    public FilterChain cloneChain() {
        return new FilterChainImpl(dataSource, pos);
    }

    public DataSourceProxy getDataSource() {
        return dataSource;
    }

    @Override
    public boolean isWrapperFor(Wrapper wrapper, Class<?> iface) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .isWrapperFor(this, wrapper, iface);
        }

        // // if driver is for jdbc 3.0
        if (iface.isInstance(wrapper)) {
            return true;
        }

        return wrapper.isWrapperFor(iface);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Wrapper wrapper, Class<T> iface) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                .unwrap(this, wrapper, iface);
        }

        if (iface == null) {
            return null;
        }

        // if driver is for jdbc 3.0
        if (iface.isAssignableFrom(wrapper.getClass())) {
            return (T) wrapper;
        }

        return wrapper.unwrap(iface);
    }

    public ConnectionProxy connection_connect(Properties info) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_connect(this, info);
        }

        Driver driver = dataSource.getRawDriver();
        String url = dataSource.getRawJdbcUrl();

        Connection nativeConnection = driver.connect(url, info);

        if (nativeConnection == null) {
            return null;
        }

        return new ConnectionProxyImpl(dataSource, nativeConnection, info, dataSource.createConnectionId());
    }

    @Override
    public void connection_clearWarnings(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .connection_clearWarnings(this, connection);
            return;
        }

        connection.getRawObject()
                .clearWarnings();
    }

    @Override
    public void connection_close(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .connection_close(this, connection);
            return;
        }

        connection.getRawObject()
                .close();
        connection.clearAttributes();
    }

    @Override
    public void connection_commit(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .connection_commit(this, connection);
            return;
        }

        connection.getRawObject()
                .commit();
    }

    @Override
    public Array connection_createArrayOf(ConnectionProxy connection, String typeName, Object[] elements)
                                                                                                         throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_createArrayOf(this, connection, typeName, elements);
        }

        return connection.getRawObject()
                .createArrayOf(typeName, elements);

    }

    @Override
    public Blob connection_createBlob(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_createBlob(this, connection);
        }

        return connection.getRawObject()
                .createBlob();
    }

    @Override
    public Clob connection_createClob(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_createClob(this, connection);
        }

        return wrap(connection
                , connection.getRawObject()
                    .createClob()
        );
    }

    @Override
    public NClob connection_createNClob(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_createNClob(this, connection);
        }

        return wrap(connection
                , connection.getRawObject()
                    .createNClob()
        );
    }

    @Override
    public SQLXML connection_createSQLXML(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_createSQLXML(this, connection);
        }

        return connection.getRawObject()
                .createSQLXML();
    }

    @Override
    public StatementProxy connection_createStatement(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_createStatement(this, connection);
        }

        Statement statement = connection.getRawObject()
                .createStatement();

        if (statement == null) {
            return null;
        }

        return new StatementProxyImpl(connection
                , statement
                , dataSource.createStatementId()
        );
    }

    @Override
    public StatementProxy connection_createStatement(
            ConnectionProxy connection,
            int resultSetType,
            int resultSetConcurrency) throws SQLException
    {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_createStatement(this, connection, resultSetType, resultSetConcurrency);
        }

        Statement statement = connection.getRawObject()
                .createStatement(resultSetType, resultSetConcurrency);

        if (statement == null) {
            return null;
        }

        return new StatementProxyImpl(connection, statement, dataSource.createStatementId());
    }

    @Override
    public StatementProxy connection_createStatement(
            ConnectionProxy connection,
            int resultSetType,
            int resultSetConcurrency,
            int resultSetHoldability) throws SQLException
    {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_createStatement(this, connection, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        Statement statement = connection.getRawObject()
                .createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        if (statement == null) {
            return null;
        }
        return new StatementProxyImpl(connection
                , statement
                , dataSource.createStatementId());
    }

    @Override
    public Struct connection_createStruct(
            ConnectionProxy connection,
            String typeName,
            Object[] attributes) throws SQLException
    {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_createStruct(this, connection, typeName, attributes);
        }

        return connection.getRawObject()
                .createStruct(typeName, attributes);
    }

    @Override
    public boolean connection_getAutoCommit(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_getAutoCommit(this, connection);
        }

        return connection.getRawObject()
                .getAutoCommit();
    }

    @Override
    public String connection_getCatalog(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_getCatalog(this, connection);
        }

        return connection.getRawObject()
                .getCatalog();
    }

    @Override
    public Properties connection_getClientInfo(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_getClientInfo(this, connection);
        }

        return connection.getRawObject()
                .getClientInfo();
    }

    @Override
    public String connection_getClientInfo(ConnectionProxy connection, String name) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_getClientInfo(this, connection, name);
        }

        return connection.getRawObject()
                .getClientInfo(name);
    }

    public List<Filter> getFilters() {
        return dataSource.getProxyFilters();
    }

    @Override
    public int connection_getHoldability(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_getHoldability(this, connection);
        }

        return connection.getRawObject()
                .getHoldability();
    }

    @Override
    public DatabaseMetaData connection_getMetaData(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_getMetaData(this, connection);
        }

        return connection.getRawObject()
                .getMetaData();
    }

    @Override
    public int connection_getTransactionIsolation(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_getTransactionIsolation(this, connection);
        }

        return connection.getRawObject()
                .getTransactionIsolation();
    }

    @Override
    public Map<String, Class<?>> connection_getTypeMap(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_getTypeMap(this, connection);
        }

        return connection.getRawObject()
                .getTypeMap();
    }

    @Override
    public SQLWarning connection_getWarnings(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_getWarnings(this, connection);
        }

        return connection.getRawObject()
                .getWarnings();
    }

    @Override
    public boolean connection_isClosed(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_isClosed(this, connection);
        }

        return connection.getRawObject()
                .isClosed();
    }

    @Override
    public boolean connection_isReadOnly(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_isReadOnly(this, connection);
        }

        return connection.getRawObject()
                .isReadOnly();
    }

    @Override
    public boolean connection_isValid(ConnectionProxy connection, int timeout) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_isValid(this, connection, timeout);
        }

        return connection.getRawObject()
                .isValid(timeout);
    }

    @Override
    public String connection_nativeSQL(ConnectionProxy connection, String sql) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_nativeSQL(this, connection, sql);
        }

        return connection.getRawObject()
                .nativeSQL(sql);
    }

    private Filter nextFilter() {
        return getFilters()
                .get(pos++);
    }

    @Override
    public CallableStatementProxy connection_prepareCall(ConnectionProxy connection, String sql) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_prepareCall(this, connection, sql);
        }

        CallableStatement statement = connection.getRawObject()
                .prepareCall(sql);

        if (statement == null) {
            return null;
        }

        return new CallableStatementProxyImpl(connection, statement, sql, dataSource.createStatementId());
    }

    @Override
    public CallableStatementProxy connection_prepareCall(
            ConnectionProxy connection,
            String sql,
            int resultSetType,
            int resultSetConcurrency) throws SQLException
    {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_prepareCall(this, connection, sql, resultSetType, resultSetConcurrency);
        }

        CallableStatement statement = connection.getRawObject()
                .prepareCall(sql, resultSetType, resultSetConcurrency);

        if (statement == null) {
            return null;
        }

        return new CallableStatementProxyImpl(connection, statement, sql, dataSource.createStatementId());
    }

    @Override
    public CallableStatementProxy connection_prepareCall(
            ConnectionProxy connection,
            String sql,
            int resultSetType,
            int resultSetConcurrency,
            int resultSetHoldability) throws SQLException
    {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_prepareCall(this
                            , connection
                            , sql
                            , resultSetType
                            , resultSetConcurrency
                            , resultSetHoldability
                    );
        }

        CallableStatement statement = connection.getRawObject()
                .prepareCall(sql, resultSetType, resultSetConcurrency, resultSetHoldability);

        if (statement == null) {
            return null;
        }

        return new CallableStatementProxyImpl(connection, statement, sql, dataSource.createStatementId());
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(
            ConnectionProxy connection,
            String sql) throws SQLException
    {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_prepareStatement(this, connection, sql);
        }

        PreparedStatement statement = connection.getRawObject()
                .prepareStatement(sql);

        if (statement == null) {
            return null;
        }

        return new PreparedStatementProxyImpl(connection, statement, sql, dataSource.createStatementId());
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(
            ConnectionProxy connection,
            String sql,
            int autoGeneratedKeys) throws SQLException
    {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_prepareStatement(this, connection, sql, autoGeneratedKeys);
        }

        PreparedStatement statement = connection.getRawObject().prepareStatement(sql, autoGeneratedKeys);

        if (statement == null) {
            return null;
        }

        return new PreparedStatementProxyImpl(connection, statement, sql, dataSource.createStatementId());
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(
            ConnectionProxy connection,
            String sql,
            int resultSetType,
            int resultSetConcurrency) throws SQLException
    {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_prepareStatement(this, connection, sql, resultSetType, resultSetConcurrency);
        }

        PreparedStatement statement
                = connection.getRawObject()
                .prepareStatement(sql, resultSetType, resultSetConcurrency);

        if (statement == null) {
            return null;
        }

        return new PreparedStatementProxyImpl(connection, statement, sql, dataSource.createStatementId());
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(
            ConnectionProxy connection,
            String sql,
            int resultSetType,
            int resultSetConcurrency,
            int resultSetHoldability) throws SQLException
    {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_prepareStatement(this, connection, sql, resultSetType, resultSetConcurrency, resultSetHoldability);
        }

        PreparedStatement statement = connection.getRawObject()
                .prepareStatement(sql, resultSetType, resultSetConcurrency, resultSetHoldability);

        if (statement == null) {
            return null;
        }

        return new PreparedStatementProxyImpl(connection
                , statement
                , sql
                , dataSource.createStatementId()
        );
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(
            ConnectionProxy connection,
            String sql,
            int[] columnIndexes) throws SQLException
    {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_prepareStatement(this, connection, sql, columnIndexes);
        }

        PreparedStatement statement = connection.getRawObject()
                .prepareStatement(sql, columnIndexes);

        if (statement == null) {
            return null;
        }

        return new PreparedStatementProxyImpl(connection
                , statement
                , sql
                , dataSource.createStatementId()
        );
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(ConnectionProxy connection, String sql,
                                                              String[] columnNames) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_prepareStatement(this, connection, sql, columnNames);
        }

        PreparedStatement statement = connection.getRawObject()
                .prepareStatement(sql, columnNames);
        if (statement == null) {
            return null;
        }

        return new PreparedStatementProxyImpl(connection, statement, sql, dataSource.createStatementId());
    }

    @Override
    public void connection_releaseSavepoint(ConnectionProxy connection, Savepoint savepoint) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .connection_releaseSavepoint(this, connection, savepoint);
            return;
        }

        connection.getRawObject()
                .releaseSavepoint(savepoint);
    }

    @Override
    public void connection_rollback(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .connection_rollback(this, connection);
            return;
        }

        connection.getRawObject()
                .rollback();
    }

    @Override
    public void connection_rollback(ConnectionProxy connection, Savepoint savepoint) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .connection_rollback(this, connection, savepoint);
            return;
        }

        connection.getRawObject()
                .rollback(savepoint);
    }

    @Override
    public void connection_setAutoCommit(ConnectionProxy connection, boolean autoCommit) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .connection_setAutoCommit(this, connection, autoCommit);
            return;
        }

        connection.getRawObject()
                .setAutoCommit(autoCommit);
    }

    @Override
    public void connection_setCatalog(ConnectionProxy connection, String catalog) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .connection_setCatalog(this, connection, catalog);
            return;
        }

        connection.getRawObject()
                .setCatalog(catalog);
    }

    @Override
    public void connection_setClientInfo(
            ConnectionProxy connection,
            Properties properties) throws SQLClientInfoException
    {
        if (this.pos < filterSize) {
            nextFilter()
                    .connection_setClientInfo(this, connection, properties);
            return;
        }

        connection.getRawObject()
                .setClientInfo(properties);
    }

    @Override
    public void connection_setClientInfo(
            ConnectionProxy connection,
            String name, String value) throws SQLClientInfoException
    {
        if (this.pos < filterSize) {
            nextFilter()
                    .connection_setClientInfo(this, connection, name, value);
            return;
        }

        connection.getRawObject()
                .setClientInfo(name, value);
    }

    @Override
    public void connection_setHoldability(ConnectionProxy connection, int holdability) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .connection_setHoldability(this, connection, holdability);
            return;
        }

        connection.getRawObject()
                .setHoldability(holdability);
    }

    @Override
    public void connection_setReadOnly(ConnectionProxy connection, boolean readOnly) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .connection_setReadOnly(this, connection, readOnly);
            return;
        }

        connection.getRawObject()
                .setReadOnly(readOnly);
    }

    @Override
    public Savepoint connection_setSavepoint(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_setSavepoint(this, connection);
        }

        return connection.getRawObject()
                .setSavepoint();
    }

    @Override
    public Savepoint connection_setSavepoint(ConnectionProxy connection, String name) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_setSavepoint(this, connection, name);
        }

        return connection.getRawObject()
                .setSavepoint(name);
    }

    @Override
    public void connection_setTransactionIsolation(ConnectionProxy connection, int level) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .connection_setTransactionIsolation(this, connection, level);
            return;
        }

        connection.getRawObject()
                .setTransactionIsolation(level);
    }

    @Override
    public void connection_setTypeMap(ConnectionProxy connection, Map<String, Class<?>> map) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .connection_setTypeMap(this, connection, map);
            return;
        }

        connection.getRawObject()
                .setTypeMap(map);
    }

    @Override
    public String connection_getSchema(ConnectionProxy connection) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_getSchema(this, connection);
        }

        return connection.getRawObject()
                .getSchema();
    }

    @Override
    public void connection_setSchema(ConnectionProxy connection, String schema) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .connection_setSchema(this, connection, schema);
            return;
        }

        connection.getRawObject()
                .setSchema(schema);
    }

    public void connection_abort(ConnectionProxy conn, Executor executor) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .connection_abort(this, conn, executor);
            return;
        }

        conn.getRawObject()
                .abort(executor);
    }

    public void connection_setNetworkTimeout(ConnectionProxy conn, Executor executor, int milliseconds) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .connection_setNetworkTimeout(this, conn, executor, milliseconds);
            return;
        }

        conn.getRawObject()
                .setNetworkTimeout(executor, milliseconds);
    }

    public int connection_getNetworkTimeout(ConnectionProxy conn) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .connection_getNetworkTimeout(this, conn);
        }

        return conn.getRawObject().getNetworkTimeout();
    }

    // ///////////////////////////////////////

    @Override
    public boolean resultSet_next(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_next(this, rs);
        }

        return rs
                .getResultSetRaw().next();
    }

    @Override
    public void resultSet_close(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .resultSet_close(this, rs);
            return;
        }

        rs.getResultSetRaw()
                .close();
        rs.clearAttributes();
    }

    @Override
    public boolean resultSet_wasNull(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_wasNull(this, rs);
        }

        return rs.getResultSetRaw().wasNull();
    }

    @Override
    public String resultSet_getString(ResultSetProxy rs, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getString(this, rs, columnIndex);
        }

        return rs.getResultSetRaw()
                .getString(columnIndex);
    }

    @Override
    public boolean resultSet_getBoolean(ResultSetProxy rs, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getBoolean(this, rs, columnIndex);
        }

        return rs.getResultSetRaw()
                .getBoolean(columnIndex);
    }

    @Override
    public byte resultSet_getByte(ResultSetProxy rs, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getByte(this, rs, columnIndex);
        }

        return rs.getResultSetRaw()
                .getByte(columnIndex);
    }

    @Override
    public short resultSet_getShort(ResultSetProxy rs, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getShort(this, rs, columnIndex);
        }

        return rs.getResultSetRaw()
                .getShort(columnIndex);
    }

    @Override
    public int resultSet_getInt(ResultSetProxy rs, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getInt(this, rs, columnIndex);
        }

        return rs.getResultSetRaw()
                .getInt(columnIndex);
    }

    @Override
    public long resultSet_getLong(ResultSetProxy rs, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getLong(this, rs, columnIndex);
        }

        return rs.getResultSetRaw()
                .getLong(columnIndex);
    }

    @Override
    public float resultSet_getFloat(ResultSetProxy resultSet, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getFloat(this, resultSet, columnIndex);
        }

        return resultSet.getResultSetRaw()
                .getFloat(columnIndex);
    }

    @Override
    public double resultSet_getDouble(ResultSetProxy rs, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getDouble(this, rs, columnIndex);
        }

        return rs.getResultSetRaw()
                .getDouble(columnIndex);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BigDecimal resultSet_getBigDecimal(ResultSetProxy rs, int columnIndex, int scale) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getBigDecimal(this, rs, columnIndex, scale);
        }

        return rs.getResultSetRaw()
                .getBigDecimal(columnIndex, scale);
    }

    @Override
    public byte[] resultSet_getBytes(ResultSetProxy rs, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getBytes(this, rs, columnIndex);
        }

        return rs.getResultSetRaw()
                .getBytes(columnIndex);
    }

    @Override
    public java.sql.Date resultSet_getDate(ResultSetProxy rs, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getDate(this, rs, columnIndex);
        }

        return rs.getResultSetRaw()
                .getDate(columnIndex);
    }

    @Override
    public java.sql.Time resultSet_getTime(ResultSetProxy rs, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getTime(this, rs, columnIndex);
        }

        return rs.getResultSetRaw()
                .getTime(columnIndex);
    }

    @Override
    public java.sql.Timestamp resultSet_getTimestamp(ResultSetProxy rs, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getTimestamp(this, rs, columnIndex);
        }

        return rs.getResultSetRaw()
                .getTimestamp(columnIndex);
    }

    @Override
    public java.io.InputStream resultSet_getAsciiStream(ResultSetProxy rs, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getAsciiStream(this, rs, columnIndex);
        }

        return rs.getResultSetRaw()
                .getAsciiStream(columnIndex);
    }

    @SuppressWarnings("deprecation")
    @Override
    public java.io.InputStream resultSet_getUnicodeStream(ResultSetProxy rs, int columnIndex)
                                                                                                    throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getUnicodeStream(this, rs, columnIndex);
        }

        return rs.getResultSetRaw()
                .getUnicodeStream(columnIndex);
    }

    @Override
    public java.io.InputStream resultSet_getBinaryStream(ResultSetProxy rs, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getBinaryStream(this, rs, columnIndex);
        }

        return rs.getResultSetRaw()
                .getBinaryStream(columnIndex);
    }

    @Override
    public String resultSet_getString(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getString(this, rs, columnLabel);
        }
        return rs.getResultSetRaw()
                .getString(columnLabel);
    }

    @Override
    public boolean resultSet_getBoolean(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getBoolean(this, rs, columnLabel);
        }

        return rs.getResultSetRaw()
                .getBoolean(columnLabel);
    }

    @Override
    public byte resultSet_getByte(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getByte(this, rs, columnLabel);
        }

        return rs.getResultSetRaw()
                .getByte(columnLabel);
    }

    @Override
    public short resultSet_getShort(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getShort(this, rs, columnLabel);
        }

        return rs.getResultSetRaw()
                .getShort(columnLabel);
    }

    @Override
    public int resultSet_getInt(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getInt(this, rs, columnLabel);
        }

        return rs.getResultSetRaw()
                .getInt(columnLabel);
    }

    @Override
    public long resultSet_getLong(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getLong(this, rs, columnLabel);
        }

        return rs.getResultSetRaw()
                .getLong(columnLabel);
    }

    @Override
    public float resultSet_getFloat(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getFloat(this, rs, columnLabel);
        }
        return rs.getResultSetRaw()
                .getFloat(columnLabel);
    }

    @Override
    public double resultSet_getDouble(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getDouble(this, rs, columnLabel);
        }
        return rs.getResultSetRaw()
                .getDouble(columnLabel);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BigDecimal resultSet_getBigDecimal(ResultSetProxy rs, String columnLabel, int scale) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getBigDecimal(this, rs, columnLabel, scale);
        }

        return rs.getResultSetRaw()
                .getBigDecimal(columnLabel, scale);
    }

    @Override
    public byte[] resultSet_getBytes(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getBytes(this, rs, columnLabel);
        }
        return rs.getResultSetRaw().getBytes(columnLabel);
    }

    @Override
    public java.sql.Date resultSet_getDate(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getDate(this, rs, columnLabel);
        }

        return rs.getResultSetRaw()
                .getDate(columnLabel);
    }

    @Override
    public java.sql.Time resultSet_getTime(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getTime(this, rs, columnLabel);
        }

        return rs.getResultSetRaw()
                .getTime(columnLabel);
    }

    @Override
    public java.sql.Timestamp resultSet_getTimestamp(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getTimestamp(this, rs, columnLabel);
        }

        return rs.getResultSetRaw()
                .getTimestamp(columnLabel);
    }

    @Override
    public java.io.InputStream resultSet_getAsciiStream(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getAsciiStream(this, rs, columnLabel);
        }

        return rs.getResultSetRaw()
                .getAsciiStream(columnLabel);
    }

    @SuppressWarnings("deprecation")
    @Override
    public java.io.InputStream resultSet_getUnicodeStream(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getUnicodeStream(this, rs, columnLabel);
        }

        return rs.getResultSetRaw()
                .getUnicodeStream(columnLabel);
    }

    @Override
    public java.io.InputStream resultSet_getBinaryStream(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getBinaryStream(this, rs, columnLabel);
        }

        return rs.getResultSetRaw()
                .getBinaryStream(columnLabel);
    }

    @Override
    public SQLWarning resultSet_getWarnings(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getWarnings(this, rs);
        }

        return rs.getResultSetRaw()
                .getWarnings();
    }

    @Override
    public void resultSet_clearWarnings(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .resultSet_clearWarnings(this, rs);
            return;
        }

        rs.getResultSetRaw()
                .clearWarnings();
    }

    @Override
    public String resultSet_getCursorName(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getCursorName(this, rs);
        }

        return rs.getResultSetRaw()
                .getCursorName();
    }

    @Override
    public ResultSetMetaData resultSet_getMetaData(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getMetaData(this, rs);
        }

        ResultSetMetaData metaData = rs.getResultSetRaw()
                .getMetaData();
        if (metaData == null) {
            return null;
        }

        return new ResultSetMetaDataProxyImpl(metaData, dataSource.createMetaDataId(), rs);
    }

    @Override
    public Object resultSet_getObject(ResultSetProxy rs, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getObject(this, rs, columnIndex);
        }

        Object obj = rs.getResultSetRaw().getObject(columnIndex);

        if (obj instanceof ResultSet) {
            StatementProxy statement = rs.getStatementProxy();
            return new ResultSetProxyImpl(statement
                    , (ResultSet) obj
                    , dataSource.createResultSetId()
                    , statement.getLastExecuteSql()
            );
        }

        if (obj instanceof Clob) {
            return wrap(
                    rs.getStatementProxy(), (Clob) obj);
        }

        return obj;
    }

    @Override
    public Object resultSet_getObject(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getObject(this, rs, columnLabel);
        }

        Object obj = rs.getResultSetRaw()
                .getObject(columnLabel);

        if (obj instanceof ResultSet) {
            StatementProxy stmt = rs.getStatementProxy();
            return new ResultSetProxyImpl(stmt
                    , (ResultSet) obj
                    , dataSource.createResultSetId()
                    , stmt.getLastExecuteSql()
            );
        }

        if (obj instanceof Clob) {
            return wrap(rs.getStatementProxy(), (Clob) obj);
        }

        return obj;
    }

    @Override
    public int resultSet_findColumn(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_findColumn(this, rs, columnLabel);
        }
        return rs.getResultSetRaw()
                .findColumn(columnLabel);
    }

    @Override
    public java.io.Reader resultSet_getCharacterStream(ResultSetProxy rs, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getCharacterStream(this, rs, columnIndex);
        }
        return rs.getResultSetRaw()
                .getCharacterStream(columnIndex);
    }

    @Override
    public java.io.Reader resultSet_getCharacterStream(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getCharacterStream(this, rs, columnLabel);
        }

        return rs.getResultSetRaw()
                .getCharacterStream(columnLabel);
    }

    @Override
    public BigDecimal resultSet_getBigDecimal(ResultSetProxy rs, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getBigDecimal(this, rs, columnIndex);
        }

        return rs.getResultSetRaw()
                .getBigDecimal(columnIndex);
    }

    @Override
    public BigDecimal resultSet_getBigDecimal(ResultSetProxy rs, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getBigDecimal(this, rs, columnLabel);
        }

        return rs.getResultSetRaw()
                .getBigDecimal(columnLabel);
    }

    @Override
    public boolean resultSet_isBeforeFirst(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_isBeforeFirst(this, rs);
        }

        return rs.getResultSetRaw()
                .isBeforeFirst();
    }

    @Override
    public boolean resultSet_isAfterLast(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_isAfterLast(this, rs);
        }

        return rs.getResultSetRaw()
                .isAfterLast();
    }

    @Override
    public boolean resultSet_isFirst(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_isFirst(this, rs);
        }

        return rs.getResultSetRaw()
                .isFirst();
    }

    @Override
    public boolean resultSet_isLast(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_isLast(this, rs);
        }
        return rs.getResultSetRaw()
                .isLast();
    }

    @Override
    public void resultSet_beforeFirst(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .resultSet_beforeFirst(this, rs);
            return;
        }
        rs.getResultSetRaw()
                .beforeFirst();
    }

    @Override
    public void resultSet_afterLast(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .resultSet_afterLast(this, rs);
            return;
        }

        rs.getResultSetRaw()
                .afterLast();
    }

    @Override
    public boolean resultSet_first(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_first(this, rs);
        }

        return rs.getResultSetRaw()
                .first();
    }

    @Override
    public boolean resultSet_last(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_last(this, rs);
        }

        return rs.getResultSetRaw()
                .last();
    }

    @Override
    public int resultSet_getRow(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getRow(this, rs);
        }

        return rs.getResultSetRaw()
                .getRow();
    }

    @Override
    public boolean resultSet_absolute(ResultSetProxy rs, int row) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_absolute(this, rs, row);
        }

        return rs.getResultSetRaw()
                .absolute(row);
    }

    @Override
    public boolean resultSet_relative(ResultSetProxy rs, int rows) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_relative(this, rs, rows);
        }
        return rs.getResultSetRaw().relative(rows);
    }

    @Override
    public boolean resultSet_previous(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_previous(this, rs);
        }

        return rs.getResultSetRaw()
                .previous();
    }

    @Override
    public void resultSet_setFetchDirection(ResultSetProxy rs, int direction) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .resultSet_setFetchDirection(this, rs, direction);
            return;
        }

        rs.getResultSetRaw()
                .setFetchDirection(direction);
    }

    @Override
    public int resultSet_getFetchDirection(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getFetchDirection(this, rs);
        }

        return rs.getResultSetRaw()
                .getFetchDirection();
    }

    @Override
    public void resultSet_setFetchSize(ResultSetProxy rs, int rows) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .resultSet_setFetchSize(this, rs, rows);
            return;
        }

        rs.getResultSetRaw()
                .setFetchSize(rows);
    }

    @Override
    public int resultSet_getFetchSize(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getFetchSize(this, rs);
        }

        return rs.getResultSetRaw()
                .getFetchSize();
    }

    @Override
    public int resultSet_getType(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getType(this, rs);
        }

        return rs.getResultSetRaw()
                .getType();
    }

    @Override
    public int resultSet_getConcurrency(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_getConcurrency(this, rs);
        }

        return rs.getResultSetRaw()
                .getConcurrency();
    }

    @Override
    public boolean resultSet_rowUpdated(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_rowUpdated(this, rs);
        }

        return rs.getResultSetRaw()
                .rowUpdated();
    }

    @Override
    public boolean resultSet_rowInserted(ResultSetProxy rs) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_rowInserted(this, rs);
        }

        return rs.getResultSetRaw()
                .rowInserted();
    }

    @Override
    public boolean resultSet_rowDeleted(ResultSetProxy resultSet) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSet_rowDeleted(this, resultSet);
        }

        return resultSet.getResultSetRaw()
                .rowDeleted();
    }

    @Override
    public void resultSet_updateNull(ResultSetProxy rs, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateNull(this, rs, columnIndex);
            return;

        }

        rs.getResultSetRaw()
                .updateNull(columnIndex);
    }

    @Override
    public void resultSet_updateBoolean(ResultSetProxy rs, int columnIndex, boolean x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter()
                    .resultSet_updateBoolean(this, rs, columnIndex, x);
            return;
        }
        rs.getResultSetRaw()
                .updateBoolean(columnIndex, x);
    }

    @Override
    public void resultSet_updateByte(ResultSetProxy resultSet, int columnIndex, byte x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateByte(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateByte(columnIndex, x);
    }

    @Override
    public void resultSet_updateShort(ResultSetProxy resultSet, int columnIndex, short x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateShort(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateShort(columnIndex, x);
    }

    @Override
    public void resultSet_updateInt(ResultSetProxy resultSet, int columnIndex, int x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateInt(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateInt(columnIndex, x);
    }

    @Override
    public void resultSet_updateLong(ResultSetProxy resultSet, int columnIndex, long x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateLong(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateLong(columnIndex, x);
    }

    @Override
    public void resultSet_updateFloat(ResultSetProxy resultSet, int columnIndex, float x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateFloat(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateFloat(columnIndex, x);
    }

    @Override
    public void resultSet_updateDouble(ResultSetProxy resultSet, int columnIndex, double x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateDouble(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateDouble(columnIndex, x);
    }

    @Override
    public void resultSet_updateBigDecimal(ResultSetProxy resultSet, int columnIndex, BigDecimal x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateBigDecimal(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateBigDecimal(columnIndex, x);
    }

    @Override
    public void resultSet_updateString(ResultSetProxy resultSet, int columnIndex, String x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateString(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateString(columnIndex, x);
    }

    @Override
    public void resultSet_updateBytes(ResultSetProxy resultSet, int columnIndex, byte[] x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateBytes(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateBytes(columnIndex, x);
    }

    @Override
    public void resultSet_updateDate(ResultSetProxy resultSet, int columnIndex, java.sql.Date x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateDate(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateDate(columnIndex, x);
    }

    @Override
    public void resultSet_updateTime(ResultSetProxy resultSet, int columnIndex, java.sql.Time x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateTime(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateTime(columnIndex, x);
    }

    @Override
    public void resultSet_updateTimestamp(ResultSetProxy resultSet, int columnIndex, java.sql.Timestamp x)
                                                                                                          throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateTimestamp(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateTimestamp(columnIndex, x);
    }

    @Override
    public void resultSet_updateAsciiStream(ResultSetProxy resultSet, int columnIndex, java.io.InputStream x, int length)
                                                                                                                         throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateAsciiStream(this, resultSet, columnIndex, x, length);
            return;
        }
        resultSet.getResultSetRaw().updateAsciiStream(columnIndex, x, length);
    }

    @Override
    public void resultSet_updateBinaryStream(ResultSetProxy resultSet, int columnIndex, java.io.InputStream x,
                                             int length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateBinaryStream(this, resultSet, columnIndex, x, length);
            return;
        }
        resultSet.getResultSetRaw().updateBinaryStream(columnIndex, x, length);
    }

    @Override
    public void resultSet_updateCharacterStream(ResultSetProxy resultSet, int columnIndex, java.io.Reader x, int length)
                                                                                                                        throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateCharacterStream(this, resultSet, columnIndex, x, length);
            return;
        }
        resultSet.getResultSetRaw().updateCharacterStream(columnIndex, x, length);
    }

    @Override
    public void resultSet_updateObject(ResultSetProxy resultSet, int columnIndex, Object x, int scaleOrLength)
                                                                                                              throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateObject(this, resultSet, columnIndex, x, scaleOrLength);
            return;
        }
        resultSet.getResultSetRaw().updateObject(columnIndex, x, scaleOrLength);
    }

    @Override
    public void resultSet_updateObject(ResultSetProxy resultSet, int columnIndex, Object x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateObject(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateObject(columnIndex, x);
    }

    @Override
    public void resultSet_updateNull(ResultSetProxy resultSet, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateNull(this, resultSet, columnLabel);
            return;
        }
        resultSet.getResultSetRaw().updateNull(columnLabel);
    }

    @Override
    public void resultSet_updateBoolean(ResultSetProxy resultSet, String columnLabel, boolean x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateBoolean(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateBoolean(columnLabel, x);
    }

    @Override
    public void resultSet_updateByte(ResultSetProxy resultSet, String columnLabel, byte x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateByte(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateByte(columnLabel, x);
    }

    @Override
    public void resultSet_updateShort(ResultSetProxy resultSet, String columnLabel, short x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateShort(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateShort(columnLabel, x);
    }

    @Override
    public void resultSet_updateInt(ResultSetProxy resultSet, String columnLabel, int x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateInt(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateInt(columnLabel, x);
    }

    @Override
    public void resultSet_updateLong(ResultSetProxy resultSet, String columnLabel, long x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateLong(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateLong(columnLabel, x);
    }

    @Override
    public void resultSet_updateFloat(ResultSetProxy resultSet, String columnLabel, float x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateFloat(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateFloat(columnLabel, x);
    }

    @Override
    public void resultSet_updateDouble(ResultSetProxy resultSet, String columnLabel, double x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateDouble(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateDouble(columnLabel, x);
    }

    @Override
    public void resultSet_updateBigDecimal(ResultSetProxy resultSet, String columnLabel, BigDecimal x)
                                                                                                      throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateBigDecimal(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateBigDecimal(columnLabel, x);
    }

    @Override
    public void resultSet_updateString(ResultSetProxy resultSet, String columnLabel, String x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateString(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateString(columnLabel, x);
    }

    @Override
    public void resultSet_updateBytes(ResultSetProxy resultSet, String columnLabel, byte x[]) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateBytes(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateBytes(columnLabel, x);
    }

    @Override
    public void resultSet_updateDate(ResultSetProxy resultSet, String columnLabel, java.sql.Date x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateDate(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateDate(columnLabel, x);
    }

    @Override
    public void resultSet_updateTime(ResultSetProxy resultSet, String columnLabel, java.sql.Time x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateTime(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateTime(columnLabel, x);
    }

    @Override
    public void resultSet_updateTimestamp(ResultSetProxy resultSet, String columnLabel, java.sql.Timestamp x)
                                                                                                             throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateTimestamp(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateTimestamp(columnLabel, x);
    }

    @Override
    public void resultSet_updateAsciiStream(ResultSetProxy resultSet, String columnLabel, java.io.InputStream x,
                                            int length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateAsciiStream(this, resultSet, columnLabel, x, length);
            return;
        }
        resultSet.getResultSetRaw().updateAsciiStream(columnLabel, x, length);
    }

    @Override
    public void resultSet_updateBinaryStream(ResultSetProxy resultSet, String columnLabel, java.io.InputStream x,
                                             int length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateBinaryStream(this, resultSet, columnLabel, x, length);
            return;
        }
        resultSet.getResultSetRaw().updateBinaryStream(columnLabel, x, length);
    }

    @Override
    public void resultSet_updateCharacterStream(ResultSetProxy resultSet, String columnLabel, java.io.Reader reader,
                                                int length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateCharacterStream(this, resultSet, columnLabel, reader, length);
            return;
        }
        resultSet.getResultSetRaw().updateCharacterStream(columnLabel, reader, length);
    }

    @Override
    public void resultSet_updateObject(ResultSetProxy resultSet, String columnLabel, Object x, int scaleOrLength)
                                                                                                                 throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateObject(this, resultSet, columnLabel, x, scaleOrLength);
            return;
        }
        resultSet.getResultSetRaw().updateObject(columnLabel, x, scaleOrLength);
    }

    @Override
    public void resultSet_updateObject(ResultSetProxy resultSet, String columnLabel, Object x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateObject(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateObject(columnLabel, x);
    }

    @Override
    public void resultSet_insertRow(ResultSetProxy resultSet) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_insertRow(this, resultSet);
            return;
        }
        resultSet.getResultSetRaw().insertRow();
    }

    @Override
    public void resultSet_updateRow(ResultSetProxy resultSet) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateRow(this, resultSet);
            return;
        }
        resultSet.getResultSetRaw().updateRow();
    }

    @Override
    public void resultSet_deleteRow(ResultSetProxy resultSet) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_deleteRow(this, resultSet);
            return;
        }
        resultSet.getResultSetRaw().deleteRow();
    }

    @Override
    public void resultSet_refreshRow(ResultSetProxy resultSet) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_refreshRow(this, resultSet);
            return;
        }
        resultSet.getResultSetRaw().refreshRow();
    }

    @Override
    public void resultSet_cancelRowUpdates(ResultSetProxy resultSet) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_cancelRowUpdates(this, resultSet);
            return;
        }
        resultSet.getResultSetRaw().cancelRowUpdates();
    }

    @Override
    public void resultSet_moveToInsertRow(ResultSetProxy resultSet) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_moveToInsertRow(this, resultSet);
            return;
        }
        resultSet.getResultSetRaw().moveToInsertRow();
    }

    @Override
    public void resultSet_moveToCurrentRow(ResultSetProxy resultSet) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_moveToCurrentRow(this, resultSet);
            return;
        }
        resultSet.getResultSetRaw().moveToCurrentRow();
    }

    @Override
    public Statement resultSet_getStatement(ResultSetProxy resultSet) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getStatement(this, resultSet);
        }
        return resultSet.getResultSetRaw().getStatement();
    }

    @Override
    public Object resultSet_getObject(ResultSetProxy resultSet, int columnIndex, java.util.Map<String, Class<?>> map)
                                                                                                                     throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getObject(this, resultSet, columnIndex, map);
        }

        Object obj = resultSet.getResultSetRaw().getObject(columnIndex, map);

        if (obj instanceof ResultSet) {
            StatementProxy statement = resultSet.getStatementProxy();
            return new ResultSetProxyImpl(statement, (ResultSet) obj, dataSource.createResultSetId(),
                    statement.getLastExecuteSql());
        }

        if (obj instanceof Clob) {
            return wrap(resultSet.getStatementProxy(), (Clob) obj);
        }

        return obj;
    }

    @Override
    public Ref resultSet_getRef(ResultSetProxy resultSet, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getRef(this, resultSet, columnIndex);
        }
        return resultSet.getResultSetRaw().getRef(columnIndex);
    }

    @Override
    public Blob resultSet_getBlob(ResultSetProxy resultSet, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getBlob(this, resultSet, columnIndex);
        }
        return resultSet.getResultSetRaw().getBlob(columnIndex);
    }

    @Override
    public Clob resultSet_getClob(ResultSetProxy resultSet, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getClob(this, resultSet, columnIndex);
        }

        Clob clob = resultSet.getResultSetRaw().getClob(columnIndex);

        return wrap(resultSet.getStatementProxy(), clob);
    }

    @Override
    public Array resultSet_getArray(ResultSetProxy resultSet, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getArray(this, resultSet, columnIndex);
        }

        Array rawArray = resultSet.getResultSetRaw().getArray(columnIndex);

        return rawArray;
    }

    @Override
    public Object resultSet_getObject(ResultSetProxy resultSet, String columnLabel, java.util.Map<String, Class<?>> map)
                                                                                                                        throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getObject(this, resultSet, columnLabel, map);
        }

        Object obj = resultSet.getResultSetRaw().getObject(columnLabel, map);

        if (obj instanceof ResultSet) {
            StatementProxy statement = resultSet.getStatementProxy();
            return new ResultSetProxyImpl(statement, (ResultSet) obj, dataSource.createResultSetId(),
                    statement.getLastExecuteSql());
        }

        if (obj instanceof Clob) {
            return wrap(resultSet.getStatementProxy(), (Clob) obj);
        }

        return obj;
    }

    @Override
    public Ref resultSet_getRef(ResultSetProxy resultSet, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getRef(this, resultSet, columnLabel);
        }
        return resultSet.getResultSetRaw().getRef(columnLabel);
    }

    @Override
    public Blob resultSet_getBlob(ResultSetProxy resultSet, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getBlob(this, resultSet, columnLabel);
        }
        return resultSet.getResultSetRaw().getBlob(columnLabel);
    }

    @Override
    public Clob resultSet_getClob(ResultSetProxy resultSet, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getClob(this, resultSet, columnLabel);
        }

        Clob clob = resultSet.getResultSetRaw().getClob(columnLabel);

        return wrap(resultSet.getStatementProxy(), clob);
    }

    @Override
    public Array resultSet_getArray(ResultSetProxy resultSet, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getArray(this, resultSet, columnLabel);
        }
        return resultSet.getResultSetRaw().getArray(columnLabel);
    }

    @Override
    public java.sql.Date resultSet_getDate(ResultSetProxy resultSet, int columnIndex, Calendar cal) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getDate(this, resultSet, columnIndex, cal);
        }
        return resultSet.getResultSetRaw().getDate(columnIndex, cal);
    }

    @Override
    public java.sql.Date resultSet_getDate(ResultSetProxy resultSet, String columnLabel, Calendar cal)
                                                                                                      throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getDate(this, resultSet, columnLabel, cal);
        }
        return resultSet.getResultSetRaw().getDate(columnLabel, cal);
    }

    @Override
    public java.sql.Time resultSet_getTime(ResultSetProxy resultSet, int columnIndex, Calendar cal) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getTime(this, resultSet, columnIndex, cal);
        }
        return resultSet.getResultSetRaw().getTime(columnIndex, cal);
    }

    @Override
    public java.sql.Time resultSet_getTime(ResultSetProxy resultSet, String columnLabel, Calendar cal)
                                                                                                      throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getTime(this, resultSet, columnLabel, cal);
        }
        return resultSet.getResultSetRaw().getTime(columnLabel, cal);
    }

    @Override
    public java.sql.Timestamp resultSet_getTimestamp(ResultSetProxy resultSet, int columnIndex, Calendar cal)
                                                                                                             throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getTimestamp(this, resultSet, columnIndex, cal);
        }
        return resultSet.getResultSetRaw().getTimestamp(columnIndex, cal);
    }

    @Override
    public java.sql.Timestamp resultSet_getTimestamp(ResultSetProxy resultSet, String columnLabel, Calendar cal)
                                                                                                                throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getTimestamp(this, resultSet, columnLabel, cal);
        }
        return resultSet.getResultSetRaw().getTimestamp(columnLabel, cal);
    }

    @Override
    public java.net.URL resultSet_getURL(ResultSetProxy resultSet, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getURL(this, resultSet, columnIndex);
        }
        return resultSet.getResultSetRaw().getURL(columnIndex);
    }

    @Override
    public java.net.URL resultSet_getURL(ResultSetProxy resultSet, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getURL(this, resultSet, columnLabel);
        }
        return resultSet.getResultSetRaw().getURL(columnLabel);
    }

    @Override
    public void resultSet_updateRef(ResultSetProxy resultSet, int columnIndex, java.sql.Ref x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateRef(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateRef(columnIndex, x);
    }

    @Override
    public void resultSet_updateRef(ResultSetProxy resultSet, String columnLabel, java.sql.Ref x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateRef(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateRef(columnLabel, x);
    }

    @Override
    public void resultSet_updateBlob(ResultSetProxy resultSet, int columnIndex, Blob x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateBlob(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateBlob(columnIndex, x);
    }

    @Override
    public void resultSet_updateBlob(ResultSetProxy resultSet, String columnLabel, Blob x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateBlob(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateBlob(columnLabel, x);
    }

    @Override
    public void resultSet_updateClob(ResultSetProxy resultSet, int columnIndex, Clob x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateClob(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateClob(columnIndex, x);
    }

    @Override
    public void resultSet_updateClob(ResultSetProxy resultSet, String columnLabel, Clob x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateClob(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateClob(columnLabel, x);
    }

    @Override
    public void resultSet_updateArray(ResultSetProxy resultSet, int columnIndex, java.sql.Array x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateArray(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateArray(columnIndex, x);
    }

    @Override
    public void resultSet_updateArray(ResultSetProxy resultSet, String columnLabel, java.sql.Array x)
                                                                                                     throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateArray(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateArray(columnLabel, x);
    }

    @Override
    public RowId resultSet_getRowId(ResultSetProxy resultSet, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getRowId(this, resultSet, columnIndex);
        }
        return resultSet.getResultSetRaw().getRowId(columnIndex);
    }

    @Override
    public RowId resultSet_getRowId(ResultSetProxy resultSet, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getRowId(this, resultSet, columnLabel);
        }
        return resultSet.getResultSetRaw().getRowId(columnLabel);
    }

    @Override
    public void resultSet_updateRowId(ResultSetProxy resultSet, int columnIndex, RowId x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateRowId(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateRowId(columnIndex, x);
    }

    @Override
    public void resultSet_updateRowId(ResultSetProxy resultSet, String columnLabel, RowId x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateRowId(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateRowId(columnLabel, x);
    }

    @Override
    public int resultSet_getHoldability(ResultSetProxy resultSet) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getHoldability(this, resultSet);
        }
        return resultSet.getResultSetRaw().getHoldability();
    }

    @Override
    public boolean resultSet_isClosed(ResultSetProxy resultSet) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_isClosed(this, resultSet);
        }
        return resultSet.getResultSetRaw().isClosed();
    }

    @Override
    public void resultSet_updateNString(ResultSetProxy resultSet, int columnIndex, String x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateNString(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateNString(columnIndex, x);
    }

    @Override
    public void resultSet_updateNString(ResultSetProxy resultSet, String columnLabel, String nString)
                                                                                                     throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateNString(this, resultSet, columnLabel, nString);
            return;
        }
        resultSet.getResultSetRaw().updateNString(columnLabel, nString);
    }

    @Override
    public void resultSet_updateNClob(ResultSetProxy resultSet, int columnIndex, NClob x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateNClob(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateNClob(columnIndex, x);
    }

    @Override
    public void resultSet_updateNClob(ResultSetProxy resultSet, String columnLabel, NClob x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateNClob(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw()
                .updateNClob(columnLabel, x);
    }

    @Override
    public NClob resultSet_getNClob(ResultSetProxy resultSet, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getNClob(this, resultSet, columnIndex);
        }

        NClob nclob = resultSet.getResultSetRaw().getNClob(columnIndex);

        return wrap(resultSet.getStatementProxy().getConnectionProxy(), nclob);
    }

    @Override
    public NClob resultSet_getNClob(ResultSetProxy resultSet, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getNClob(this, resultSet, columnLabel);
        }

        NClob nclob = resultSet.getResultSetRaw()
                .getNClob(columnLabel);

        return wrap(resultSet.getStatementProxy().getConnectionProxy(), nclob);
    }

    @Override
    public SQLXML resultSet_getSQLXML(ResultSetProxy resultSet, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getSQLXML(this, resultSet, columnIndex);
        }

        return resultSet.getResultSetRaw().getSQLXML(columnIndex);
    }

    @Override
    public SQLXML resultSet_getSQLXML(ResultSetProxy resultSet, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getSQLXML(this, resultSet, columnLabel);
        }
        return resultSet.getResultSetRaw().getSQLXML(columnLabel);
    }

    @Override
    public void resultSet_updateSQLXML(ResultSetProxy resultSet, int columnIndex, SQLXML xmlObject) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateSQLXML(this, resultSet, columnIndex, xmlObject);
            return;
        }
        resultSet.getResultSetRaw().updateSQLXML(columnIndex, xmlObject);
    }

    @Override
    public void resultSet_updateSQLXML(ResultSetProxy resultSet, String columnLabel, SQLXML xmlObject)
                                                                                                      throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateSQLXML(this, resultSet, columnLabel, xmlObject);
            return;
        }
        resultSet.getResultSetRaw().updateSQLXML(columnLabel, xmlObject);
    }

    @Override
    public String resultSet_getNString(ResultSetProxy resultSet, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getNString(this, resultSet, columnIndex);
        }
        return resultSet.getResultSetRaw().getNString(columnIndex);
    }

    @Override
    public String resultSet_getNString(ResultSetProxy resultSet, String columnLabel) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getNString(this, resultSet, columnLabel);
        }
        return resultSet.getResultSetRaw().getNString(columnLabel);
    }

    @Override
    public java.io.Reader resultSet_getNCharacterStream(ResultSetProxy resultSet, int columnIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getNCharacterStream(this, resultSet, columnIndex);
        }
        return resultSet.getResultSetRaw().getNCharacterStream(columnIndex);
    }

    @Override
    public java.io.Reader resultSet_getNCharacterStream(ResultSetProxy resultSet, String columnLabel)
                                                                                                     throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSet_getNCharacterStream(this, resultSet, columnLabel);
        }
        return resultSet.getResultSetRaw().getNCharacterStream(columnLabel);
    }

    @Override
    public void resultSet_updateNCharacterStream(ResultSetProxy resultSet, int columnIndex, java.io.Reader x,
                                                 long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateNCharacterStream(this, resultSet, columnIndex, x, length);
            return;
        }
        resultSet.getResultSetRaw().updateNCharacterStream(columnIndex, x, length);
    }

    @Override
    public void resultSet_updateNCharacterStream(ResultSetProxy resultSet, String columnLabel, java.io.Reader reader,
                                                 long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateNCharacterStream(this, resultSet, columnLabel, reader, length);
            return;
        }
        resultSet.getResultSetRaw().updateNCharacterStream(columnLabel, reader, length);
    }

    @Override
    public void resultSet_updateAsciiStream(ResultSetProxy resultSet, int columnIndex, java.io.InputStream x,
                                            long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateAsciiStream(this, resultSet, columnIndex, x, length);
            return;
        }
        resultSet.getResultSetRaw().updateAsciiStream(columnIndex, x, length);
    }

    @Override
    public void resultSet_updateBinaryStream(ResultSetProxy resultSet, int columnIndex, java.io.InputStream x,
                                             long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateBinaryStream(this, resultSet, columnIndex, x, length);
            return;
        }
        resultSet.getResultSetRaw().updateBinaryStream(columnIndex, x, length);
    }

    @Override
    public void resultSet_updateCharacterStream(ResultSetProxy resultSet, int columnIndex, java.io.Reader x, long length)
                                                                                                                         throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateCharacterStream(this, resultSet, columnIndex, x, length);
            return;
        }
        resultSet.getResultSetRaw().updateCharacterStream(columnIndex, x, length);
    }

    @Override
    public void resultSet_updateAsciiStream(ResultSetProxy resultSet, String columnLabel, java.io.InputStream x,
                                            long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateAsciiStream(this, resultSet, columnLabel, x, length);
            return;
        }
        resultSet.getResultSetRaw().updateAsciiStream(columnLabel, x, length);
    }

    @Override
    public void resultSet_updateBinaryStream(ResultSetProxy resultSet, String columnLabel, java.io.InputStream x,
                                             long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateBinaryStream(this, resultSet, columnLabel, x, length);
            return;
        }
        resultSet.getResultSetRaw().updateBinaryStream(columnLabel, x, length);
    }

    @Override
    public void resultSet_updateCharacterStream(ResultSetProxy resultSet, String columnLabel, java.io.Reader reader,
                                                long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateCharacterStream(this, resultSet, columnLabel, reader, length);
            return;
        }
        resultSet.getResultSetRaw().updateCharacterStream(columnLabel, reader, length);
    }

    @Override
    public void resultSet_updateBlob(ResultSetProxy resultSet, int columnIndex, InputStream inputStream, long length)
                                                                                                                     throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateBlob(this, resultSet, columnIndex, inputStream, length);
            return;
        }
        resultSet.getResultSetRaw().updateBlob(columnIndex, inputStream, length);
    }

    @Override
    public void resultSet_updateBlob(ResultSetProxy resultSet, String columnLabel, InputStream inputStream, long length)
                                                                                                                        throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateBlob(this, resultSet, columnLabel, inputStream, length);
            return;
        }
        resultSet.getResultSetRaw().updateBlob(columnLabel, inputStream, length);
    }

    @Override
    public void resultSet_updateClob(ResultSetProxy resultSet, int columnIndex, Reader reader, long length)
                                                                                                           throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateClob(this, resultSet, columnIndex, reader, length);
            return;
        }
        resultSet.getResultSetRaw().updateClob(columnIndex, reader, length);
    }

    @Override
    public void resultSet_updateClob(ResultSetProxy resultSet, String columnLabel, Reader reader, long length)
                                                                                                              throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateClob(this, resultSet, columnLabel, reader, length);
            return;
        }
        resultSet.getResultSetRaw().updateClob(columnLabel, reader, length);
    }

    @Override
    public void resultSet_updateNClob(ResultSetProxy resultSet, int columnIndex, Reader reader, long length)
                                                                                                            throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateNClob(this, resultSet, columnIndex, reader, length);
            return;
        }
        resultSet.getResultSetRaw().updateNClob(columnIndex, reader, length);
    }

    @Override
    public void resultSet_updateNClob(ResultSetProxy resultSet, String columnLabel, Reader reader, long length)
                                                                                                               throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateNClob(this, resultSet, columnLabel, reader, length);
            return;
        }
        resultSet.getResultSetRaw().updateNClob(columnLabel, reader, length);
    }

    @Override
    public void resultSet_updateNCharacterStream(ResultSetProxy resultSet, int columnIndex, java.io.Reader x)
                                                                                                             throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateNCharacterStream(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateNCharacterStream(columnIndex, x);
    }

    @Override
    public void resultSet_updateNCharacterStream(ResultSetProxy resultSet, String columnLabel, java.io.Reader reader)
                                                                                                                     throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateNCharacterStream(this, resultSet, columnLabel, reader);
            return;
        }
        resultSet.getResultSetRaw().updateNCharacterStream(columnLabel, reader);
    }

    @Override
    public void resultSet_updateAsciiStream(ResultSetProxy resultSet, int columnIndex, java.io.InputStream x)
                                                                                                             throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateAsciiStream(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateAsciiStream(columnIndex, x);
    }

    @Override
    public void resultSet_updateBinaryStream(ResultSetProxy resultSet, int columnIndex, java.io.InputStream x)
                                                                                                              throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateBinaryStream(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateBinaryStream(columnIndex, x);
    }

    @Override
    public void resultSet_updateCharacterStream(ResultSetProxy resultSet, int columnIndex, java.io.Reader x)
                                                                                                            throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateCharacterStream(this, resultSet, columnIndex, x);
            return;
        }
        resultSet.getResultSetRaw().updateCharacterStream(columnIndex, x);
    }

    @Override
    public void resultSet_updateAsciiStream(ResultSetProxy resultSet, String columnLabel, java.io.InputStream x)
                                                                                                                throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateAsciiStream(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateAsciiStream(columnLabel, x);
    }

    @Override
    public void resultSet_updateBinaryStream(ResultSetProxy resultSet, String columnLabel, java.io.InputStream x)
                                                                                                                 throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateBinaryStream(this, resultSet, columnLabel, x);
            return;
        }
        resultSet.getResultSetRaw().updateBinaryStream(columnLabel, x);
    }

    @Override
    public void resultSet_updateCharacterStream(ResultSetProxy resultSet, String columnLabel, java.io.Reader reader)
                                                                                                                    throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateCharacterStream(this, resultSet, columnLabel, reader);
            return;
        }
        resultSet.getResultSetRaw().updateCharacterStream(columnLabel, reader);
    }

    @Override
    public void resultSet_updateBlob(ResultSetProxy resultSet, int columnIndex, InputStream inputStream)
                                                                                                        throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateBlob(this, resultSet, columnIndex, inputStream);
            return;
        }
        resultSet.getResultSetRaw().updateBlob(columnIndex, inputStream);
    }

    @Override
    public void resultSet_updateBlob(ResultSetProxy resultSet, String columnLabel, InputStream inputStream)
                                                                                                           throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateBlob(this, resultSet, columnLabel, inputStream);
            return;
        }
        resultSet.getResultSetRaw().updateBlob(columnLabel, inputStream);
    }

    @Override
    public void resultSet_updateClob(ResultSetProxy resultSet, int columnIndex, Reader reader) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateClob(this, resultSet, columnIndex, reader);
            return;
        }
        resultSet.getResultSetRaw().updateClob(columnIndex, reader);
    }

    @Override
    public void resultSet_updateClob(ResultSetProxy resultSet, String columnLabel, Reader reader) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateClob(this, resultSet, columnLabel, reader);
            return;
        }
        resultSet.getResultSetRaw().updateClob(columnLabel, reader);
    }

    @Override
    public void resultSet_updateNClob(ResultSetProxy resultSet, int columnIndex, Reader reader) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateNClob(this, resultSet, columnIndex, reader);
            return;
        }
        resultSet.getResultSetRaw().updateNClob(columnIndex, reader);
    }

    @Override
    public void resultSet_updateNClob(ResultSetProxy resultSet, String columnLabel, Reader reader) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().resultSet_updateNClob(this, resultSet, columnLabel, reader);
            return;
        }
        resultSet.getResultSetRaw().updateNClob(columnLabel, reader);
    }

    // //////////////////////////////////////// statement
    @Override
    public ResultSetProxy statement_executeQuery(StatementProxy statement, String sql) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_executeQuery(this, statement, sql);
        }

        ResultSet resultSet = statement.getRawObject().executeQuery(sql);

        if (resultSet == null) {
            return null;
        }

        return new ResultSetProxyImpl(statement, resultSet, dataSource.createResultSetId(),
                statement.getLastExecuteSql());
    }

    @Override
    public int statement_executeUpdate(StatementProxy statement, String sql) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_executeUpdate(this, statement, sql);
        }
        return statement.getRawObject().executeUpdate(sql);
    }

    @Override
    public void statement_close(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().statement_close(this, statement);
            return;
        }
        statement.getRawObject().close();
    }

    @Override
    public int statement_getMaxFieldSize(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_getMaxFieldSize(this, statement);
        }
        return statement.getRawObject().getMaxFieldSize();
    }

    @Override
    public void statement_setMaxFieldSize(StatementProxy statement, int max) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().statement_setMaxFieldSize(this, statement, max);
            return;
        }
        statement.getRawObject().setMaxFieldSize(max);
    }

    @Override
    public int statement_getMaxRows(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_getMaxRows(this, statement);
        }
        return statement.getRawObject().getMaxRows();
    }

    @Override
    public void statement_setMaxRows(StatementProxy statement, int max) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().statement_setMaxRows(this, statement, max);
            return;
        }
        statement.getRawObject().setMaxRows(max);
    }

    @Override
    public void statement_setEscapeProcessing(StatementProxy statement, boolean enable) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().statement_setEscapeProcessing(this, statement, enable);
            return;
        }
        statement.getRawObject().setEscapeProcessing(enable);
    }

    @Override
    public int statement_getQueryTimeout(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_getQueryTimeout(this, statement);
        }
        return statement.getRawObject().getQueryTimeout();
    }

    @Override
    public void statement_setQueryTimeout(StatementProxy statement, int seconds) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().statement_setQueryTimeout(this, statement, seconds);
            return;
        }
        statement.getRawObject().setQueryTimeout(seconds);
    }

    @Override
    public void statement_cancel(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().statement_cancel(this, statement);
            return;
        }
        statement.getRawObject().cancel();
    }

    @Override
    public SQLWarning statement_getWarnings(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_getWarnings(this, statement);
        }
        return statement.getRawObject().getWarnings();
    }

    @Override
    public void statement_clearWarnings(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().statement_clearWarnings(this, statement);
            return;
        }
        statement.getRawObject().clearWarnings();
    }

    @Override
    public void statement_setCursorName(StatementProxy statement, String name) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().statement_setCursorName(this, statement, name);
            return;
        }
        statement.getRawObject().setCursorName(name);
    }

    @Override
    public boolean statement_execute(StatementProxy statement, String sql) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_execute(this, statement, sql);
        }
        return statement.getRawObject().execute(sql);
    }

    @Override
    public ResultSetProxy statement_getResultSet(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_getResultSet(this, statement);
        }

        ResultSet resultSet = statement.getRawObject().getResultSet();

        if (resultSet == null) {
            return null;
        }

        return new ResultSetProxyImpl(statement, resultSet, dataSource.createResultSetId(),
                statement.getLastExecuteSql());
    }

    @Override
    public int statement_getUpdateCount(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_getUpdateCount(this, statement);
        }
        return statement.getRawObject().getUpdateCount();
    }

    @Override
    public boolean statement_getMoreResults(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_getMoreResults(this, statement);
        }
        return statement.getRawObject().getMoreResults();
    }

    @Override
    public void statement_setFetchDirection(StatementProxy statement, int direction) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().statement_setFetchDirection(this, statement, direction);
            return;
        }
        statement.getRawObject().setFetchDirection(direction);
    }

    @Override
    public int statement_getFetchDirection(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_getFetchDirection(this, statement);
        }
        return statement.getRawObject().getFetchDirection();
    }

    @Override
    public void statement_setFetchSize(StatementProxy statement, int rows) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().statement_setFetchSize(this, statement, rows);
            return;
        }
        statement.getRawObject().setFetchSize(rows);
    }

    @Override
    public int statement_getFetchSize(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_getFetchSize(this, statement);
        }
        return statement.getRawObject().getFetchSize();
    }

    @Override
    public int statement_getResultSetConcurrency(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_getResultSetConcurrency(this, statement);
        }
        return statement.getRawObject().getResultSetConcurrency();
    }

    @Override
    public int statement_getResultSetType(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_getResultSetType(this, statement);
        }
        return statement.getRawObject().getResultSetType();
    }

    @Override
    public void statement_addBatch(StatementProxy statement, String sql) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().statement_addBatch(this, statement, sql);
            return;
        }
        statement.getRawObject().addBatch(sql);
    }

    @Override
    public void statement_clearBatch(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().statement_clearBatch(this, statement);
            return;
        }
        statement.getRawObject().clearBatch();
    }

    @Override
    public int[] statement_executeBatch(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_executeBatch(this, statement);
        }
        return statement.getRawObject().executeBatch();
    }

    @Override
    public Connection statement_getConnection(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_getConnection(this, statement);
        }
        return statement.getRawObject().getConnection();
    }

    @Override
    public boolean statement_getMoreResults(StatementProxy statement, int current) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_getMoreResults(this, statement, current);
        }
        return statement.getRawObject().getMoreResults(current);
    }

    @Override
    public ResultSetProxy statement_getGeneratedKeys(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_getGeneratedKeys(this, statement);
        }

        ResultSet resultSet = statement.getRawObject().getGeneratedKeys();
        if (resultSet == null) {
            return null;
        }
        return new ResultSetProxyImpl(statement, resultSet, dataSource.createResultSetId(),
                statement.getLastExecuteSql());
    }

    @Override
    public int statement_executeUpdate(StatementProxy statement, String sql, int autoGeneratedKeys) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_executeUpdate(this, statement, sql, autoGeneratedKeys);
        }
        return statement.getRawObject().executeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public int statement_executeUpdate(StatementProxy statement, String sql, int columnIndexes[]) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_executeUpdate(this, statement, sql, columnIndexes);
        }
        return statement.getRawObject().executeUpdate(sql, columnIndexes);
    }

    @Override
    public int statement_executeUpdate(StatementProxy statement, String sql, String columnNames[]) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_executeUpdate(this, statement, sql, columnNames);
        }
        return statement.getRawObject().executeUpdate(sql, columnNames);
    }

    @Override
    public boolean statement_execute(StatementProxy statement, String sql, int autoGeneratedKeys) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_execute(this, statement, sql, autoGeneratedKeys);
        }
        return statement.getRawObject().execute(sql, autoGeneratedKeys);
    }

    @Override
    public boolean statement_execute(StatementProxy statement, String sql, int columnIndexes[]) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_execute(this, statement, sql, columnIndexes);
        }
        return statement.getRawObject().execute(sql, columnIndexes);
    }

    @Override
    public boolean statement_execute(StatementProxy statement, String sql, String columnNames[]) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_execute(this, statement, sql, columnNames);
        }
        return statement.getRawObject().execute(sql, columnNames);
    }

    @Override
    public int statement_getResultSetHoldability(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_getResultSetHoldability(this, statement);
        }
        return statement.getRawObject().getResultSetHoldability();
    }

    @Override
    public boolean statement_isClosed(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_isClosed(this, statement);
        }
        return statement.getRawObject().isClosed();
    }

    @Override
    public void statement_setPoolable(StatementProxy statement, boolean poolable) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().statement_setPoolable(this, statement, poolable);
            return;
        }
        statement.getRawObject().setPoolable(poolable);
    }

    @Override
    public boolean statement_isPoolable(StatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().statement_isPoolable(this, statement);
        }
        return statement.getRawObject().isPoolable();
    }

    // ////////////////

    @Override
    public ResultSetProxy preparedStatement_executeQuery(PreparedStatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().preparedStatement_executeQuery(this, statement);
        }

        ResultSet resultSet = statement.getRawObject().executeQuery();
        if (resultSet == null) {
            return null;
        }
        return new ResultSetProxyImpl(statement, resultSet, dataSource.createResultSetId(),
                statement.getLastExecuteSql());
    }

    @Override
    public int preparedStatement_executeUpdate(PreparedStatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().preparedStatement_executeUpdate(this, statement);
        }
        return statement.getRawObject().executeUpdate();
    }

    @Override
    public void preparedStatement_setNull(PreparedStatementProxy statement, int parameterIndex, int sqlType)
                                                                                                            throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setNull(this, statement, parameterIndex, sqlType);
            return;
        }
        statement.getRawObject().setNull(parameterIndex, sqlType);
    }

    @Override
    public void preparedStatement_setBoolean(PreparedStatementProxy statement, int parameterIndex, boolean x)
                                                                                                             throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setBoolean(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setBoolean(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setByte(PreparedStatementProxy statement, int parameterIndex, byte x)
                                                                                                       throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setByte(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setByte(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setShort(PreparedStatementProxy statement, int parameterIndex, short x)
                                                                                                         throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setShort(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setShort(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setInt(PreparedStatementProxy statement, int parameterIndex, int x)
                                                                                                     throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setInt(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setInt(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setLong(PreparedStatementProxy statement, int parameterIndex, long x)
                                                                                                       throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setLong(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setLong(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setFloat(PreparedStatementProxy statement, int parameterIndex, float x)
                                                                                                         throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setFloat(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setFloat(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setDouble(PreparedStatementProxy statement, int parameterIndex, double x)
                                                                                                           throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setDouble(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setDouble(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setBigDecimal(PreparedStatementProxy statement, int parameterIndex, BigDecimal x)
                                                                                                                   throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setBigDecimal(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setBigDecimal(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setString(PreparedStatementProxy statement, int parameterIndex, String x)
                                                                                                           throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setString(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setString(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setBytes(PreparedStatementProxy statement, int parameterIndex, byte x[])
                                                                                                          throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setBytes(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setBytes(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setDate(PreparedStatementProxy statement, int parameterIndex, java.sql.Date x)
                                                                                                                throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setDate(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setDate(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setTime(PreparedStatementProxy statement, int parameterIndex, java.sql.Time x)
                                                                                                                throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setTime(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setTime(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setTimestamp(PreparedStatementProxy statement, int parameterIndex,
                                               java.sql.Timestamp x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setTimestamp(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setTimestamp(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setAsciiStream(PreparedStatementProxy statement, int parameterIndex,
                                                 java.io.InputStream x, int length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setAsciiStream(this, statement, parameterIndex, x, length);
            return;
        }
        statement.getRawObject().setAsciiStream(parameterIndex, x, length);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void preparedStatement_setUnicodeStream(PreparedStatementProxy statement, int parameterIndex,
                                                   java.io.InputStream x, int length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setUnicodeStream(this, statement, parameterIndex, x, length);
            return;
        }
        statement.getRawObject().setUnicodeStream(parameterIndex, x, length);
    }

    @Override
    public void preparedStatement_setBinaryStream(PreparedStatementProxy statement, int parameterIndex,
                                                  java.io.InputStream x, int length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setBinaryStream(this, statement, parameterIndex, x, length);
            return;
        }
        statement.getRawObject().setBinaryStream(parameterIndex, x, length);
    }

    @Override
    public void preparedStatement_clearParameters(PreparedStatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_clearParameters(this, statement);
            return;
        }
        statement.getRawObject().clearParameters();
    }

    @Override
    public void preparedStatement_setObject(PreparedStatementProxy statement, int parameterIndex, Object x,
                                            int targetSqlType) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setObject(this, statement, parameterIndex, x, targetSqlType);
            return;
        }
        statement.getRawObject().setObject(parameterIndex, x, targetSqlType);
    }

    @Override
    public void preparedStatement_setObject(PreparedStatementProxy statement, int parameterIndex, Object x)
                                                                                                           throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setObject(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setObject(parameterIndex, x);
    }

    @Override
    public boolean preparedStatement_execute(PreparedStatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().preparedStatement_execute(this, statement);
        }
        return statement.getRawObject().execute();
    }

    @Override
    public void preparedStatement_addBatch(PreparedStatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_addBatch(this, statement);
            return;
        }
        statement.getRawObject().addBatch();
    }

    @Override
    public void preparedStatement_setCharacterStream(PreparedStatementProxy statement, int parameterIndex,
                                                     java.io.Reader reader, int length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setCharacterStream(this, statement, parameterIndex, reader, length);
            return;
        }
        statement.getRawObject().setCharacterStream(parameterIndex, reader, length);
    }

    @Override
    public void preparedStatement_setRef(PreparedStatementProxy statement, int parameterIndex, Ref x)
                                                                                                     throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setRef(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setRef(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setBlob(PreparedStatementProxy statement, int parameterIndex, Blob x)
                                                                                                       throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setBlob(this, statement, parameterIndex, x);
            return;
        }

        statement.getRawObject()
                .setBlob(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setClob(PreparedStatementProxy statement, int parameterIndex, Clob x)
                                                                                                       throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setClob(this, statement, parameterIndex, x);
            return;
        }

        if (x instanceof ClobProxy) {
            x = ((ClobProxy) x).getRawClob();
        }

        statement.getRawObject()
                .setClob(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setArray(PreparedStatementProxy statement, int parameterIndex, Array x)
                                                                                                         throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setArray(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setArray(parameterIndex, x);
    }

    @Override
    public ResultSetMetaData preparedStatement_getMetaData(PreparedStatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().preparedStatement_getMetaData(this, statement);
        }
        return statement.getRawObject().getMetaData();
    }

    @Override
    public void preparedStatement_setDate(PreparedStatementProxy statement, int parameterIndex, java.sql.Date x,
                                          Calendar cal) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setDate(this, statement, parameterIndex, x, cal);
            return;
        }
        statement.getRawObject().setDate(parameterIndex, x, cal);
    }

    @Override
    public void preparedStatement_setTime(PreparedStatementProxy statement, int parameterIndex, java.sql.Time x,
                                          Calendar cal) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setTime(this, statement, parameterIndex, x, cal);
            return;
        }
        statement.getRawObject().setTime(parameterIndex, x, cal);
    }

    @Override
    public void preparedStatement_setTimestamp(PreparedStatementProxy statement, int parameterIndex,
                                               java.sql.Timestamp x, Calendar cal) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setTimestamp(this, statement, parameterIndex, x, cal);
            return;
        }
        statement.getRawObject().setTimestamp(parameterIndex, x, cal);
    }

    @Override
    public void preparedStatement_setNull(PreparedStatementProxy statement, int parameterIndex, int sqlType,
                                          String typeName) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setNull(this, statement, parameterIndex, sqlType, typeName);
            return;
        }
        statement.getRawObject().setNull(parameterIndex, sqlType, typeName);
    }

    @Override
    public void preparedStatement_setURL(PreparedStatementProxy statement, int parameterIndex, java.net.URL x)
                                                                                                              throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setURL(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setURL(parameterIndex, x);
    }

    @Override
    public ParameterMetaData preparedStatement_getParameterMetaData(PreparedStatementProxy statement)
                                                                                                     throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().preparedStatement_getParameterMetaData(this, statement);
        }
        return statement.getRawObject().getParameterMetaData();
    }

    @Override
    public void preparedStatement_setRowId(PreparedStatementProxy statement, int parameterIndex, RowId x)
                                                                                                         throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setRowId(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setRowId(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setNString(PreparedStatementProxy statement, int parameterIndex, String value)
                                                                                                                throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setNString(this, statement, parameterIndex, value);
            return;
        }
        statement.getRawObject().setNString(parameterIndex, value);
    }

    @Override
    public void preparedStatement_setNCharacterStream(PreparedStatementProxy statement, int parameterIndex,
                                                      Reader value, long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setNCharacterStream(this, statement, parameterIndex, value, length);
            return;
        }
        statement.getRawObject().setNCharacterStream(parameterIndex, value, length);
    }

    @Override
    public void preparedStatement_setNClob(PreparedStatementProxy statement, int parameterIndex, NClob x)
                                                                                                             throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setNClob(this, statement, parameterIndex, x);
            return;
        }

        if (x instanceof NClobProxy) {
            x = ((NClobProxy) x).getRawNClob();
        }

        statement.getRawObject()
                .setNClob(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setClob(PreparedStatementProxy statement, int parameterIndex, Reader reader,
                                          long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setClob(this, statement, parameterIndex, reader, length);
            return;
        }
        statement.getRawObject().setClob(parameterIndex, reader, length);
    }

    @Override
    public void preparedStatement_setBlob(PreparedStatementProxy statement, int parameterIndex,
                                          InputStream inputStream, long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setBlob(this, statement, parameterIndex, inputStream, length);
            return;
        }
        statement.getRawObject().setBlob(parameterIndex, inputStream, length);
    }

    @Override
    public void preparedStatement_setNClob(PreparedStatementProxy statement, int parameterIndex, Reader reader,
                                           long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setNClob(this, statement, parameterIndex, reader, length);
            return;
        }
        statement.getRawObject().setNClob(parameterIndex, reader, length);
    }

    @Override
    public void preparedStatement_setSQLXML(PreparedStatementProxy statement, int parameterIndex, SQLXML xmlObject)
                                                                                                                   throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setSQLXML(this, statement, parameterIndex, xmlObject);
            return;
        }
        statement.getRawObject().setSQLXML(parameterIndex, xmlObject);
    }

    @Override
    public void preparedStatement_setObject(PreparedStatementProxy statement, int parameterIndex, Object x,
                                            int targetSqlType, int scaleOrLength) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setObject(this, statement, parameterIndex, x, targetSqlType, scaleOrLength);
            return;
        }
        statement.getRawObject().setObject(parameterIndex, x, targetSqlType, scaleOrLength);
    }

    @Override
    public void preparedStatement_setAsciiStream(PreparedStatementProxy statement, int parameterIndex,
                                                 java.io.InputStream x, long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setAsciiStream(this, statement, parameterIndex, x, length);
            return;
        }
        statement.getRawObject().setAsciiStream(parameterIndex, x, length);
    }

    @Override
    public void preparedStatement_setBinaryStream(PreparedStatementProxy statement, int parameterIndex,
                                                  java.io.InputStream x, long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setBinaryStream(this, statement, parameterIndex, x, length);
            return;
        }
        statement.getRawObject().setBinaryStream(parameterIndex, x, length);
    }

    @Override
    public void preparedStatement_setCharacterStream(PreparedStatementProxy statement, int parameterIndex,
                                                     java.io.Reader reader, long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setCharacterStream(this, statement, parameterIndex, reader, length);
            return;
        }
        statement.getRawObject().setCharacterStream(parameterIndex, reader, length);
    }

    @Override
    public void preparedStatement_setAsciiStream(PreparedStatementProxy statement, int parameterIndex,
                                                 java.io.InputStream x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setAsciiStream(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setAsciiStream(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setBinaryStream(PreparedStatementProxy statement, int parameterIndex,
                                                  java.io.InputStream x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setBinaryStream(this, statement, parameterIndex, x);
            return;
        }
        statement.getRawObject().setBinaryStream(parameterIndex, x);
    }

    @Override
    public void preparedStatement_setCharacterStream(PreparedStatementProxy statement, int parameterIndex,
                                                     java.io.Reader reader) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setCharacterStream(this, statement, parameterIndex, reader);
            return;
        }
        statement.getRawObject().setCharacterStream(parameterIndex, reader);
    }

    @Override
    public void preparedStatement_setNCharacterStream(PreparedStatementProxy statement, int parameterIndex, Reader value)
                                                                                                                         throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setNCharacterStream(this, statement, parameterIndex, value);
            return;
        }
        statement.getRawObject().setNCharacterStream(parameterIndex, value);
    }

    @Override
    public void preparedStatement_setClob(PreparedStatementProxy statement, int parameterIndex, Reader reader)
                                                                                                              throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setClob(this, statement, parameterIndex, reader);
            return;
        }
        statement.getRawObject().setClob(parameterIndex, reader);
    }

    @Override
    public void preparedStatement_setBlob(PreparedStatementProxy statement, int parameterIndex, InputStream inputStream)
                                                                                                                        throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setBlob(this, statement, parameterIndex, inputStream);
            return;
        }
        statement.getRawObject().setBlob(parameterIndex, inputStream);
    }

    @Override
    public void preparedStatement_setNClob(PreparedStatementProxy statement, int parameterIndex, Reader reader)
                                                                                                               throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().preparedStatement_setNClob(this, statement, parameterIndex, reader);
            return;
        }
        statement.getRawObject().setNClob(parameterIndex, reader);
    }

    // /////////////////////////////////////

    @Override
    public void callableStatement_registerOutParameter(CallableStatementProxy statement, int parameterIndex, int sqlType)
                                                                                                                         throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_registerOutParameter(this, statement, parameterIndex, sqlType);
            return;
        }
        statement.getRawObject().registerOutParameter(parameterIndex, sqlType);
    }

    @Override
    public void callableStatement_registerOutParameter(CallableStatementProxy statement, int parameterIndex,
                                                       int sqlType, int scale) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_registerOutParameter(this, statement, parameterIndex, sqlType, scale);
            return;
        }
        statement.getRawObject().registerOutParameter(parameterIndex, sqlType, scale);
    }

    @Override
    public boolean callableStatement_wasNull(CallableStatementProxy statement) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_wasNull(this, statement);
        }
        return statement.getRawObject().wasNull();
    }

    @Override
    public String callableStatement_getString(CallableStatementProxy statement, int parameterIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getString(this, statement, parameterIndex);
        }
        return statement.getRawObject().getString(parameterIndex);
    }

    @Override
    public boolean callableStatement_getBoolean(CallableStatementProxy statement, int parameterIndex)
                                                                                                     throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getBoolean(this, statement, parameterIndex);
        }
        return statement.getRawObject().getBoolean(parameterIndex);
    }

    @Override
    public byte callableStatement_getByte(CallableStatementProxy statement, int parameterIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getByte(this, statement, parameterIndex);
        }
        return statement.getRawObject().getByte(parameterIndex);
    }

    @Override
    public short callableStatement_getShort(CallableStatementProxy statement, int parameterIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getShort(this, statement, parameterIndex);
        }
        return statement.getRawObject().getShort(parameterIndex);
    }

    @Override
    public int callableStatement_getInt(CallableStatementProxy statement, int parameterIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getInt(this, statement, parameterIndex);
        }
        return statement.getRawObject().getInt(parameterIndex);
    }

    @Override
    public long callableStatement_getLong(CallableStatementProxy statement, int parameterIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getLong(this, statement, parameterIndex);
        }
        return statement.getRawObject().getLong(parameterIndex);
    }

    @Override
    public float callableStatement_getFloat(CallableStatementProxy statement, int parameterIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getFloat(this, statement, parameterIndex);
        }
        return statement.getRawObject().getFloat(parameterIndex);
    }

    @Override
    public double callableStatement_getDouble(CallableStatementProxy statement, int parameterIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getDouble(this, statement, parameterIndex);
        }
        return statement.getRawObject().getDouble(parameterIndex);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BigDecimal callableStatement_getBigDecimal(CallableStatementProxy statement, int parameterIndex, int scale)
                                                                                                                      throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getBigDecimal(this, statement, parameterIndex, scale);
        }
        return statement.getRawObject().getBigDecimal(parameterIndex, scale);
    }

    @Override
    public byte[] callableStatement_getBytes(CallableStatementProxy statement, int parameterIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getBytes(this, statement, parameterIndex);
        }
        return statement.getRawObject().getBytes(parameterIndex);
    }

    @Override
    public java.sql.Date callableStatement_getDate(CallableStatementProxy statement, int parameterIndex)
                                                                                                        throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getDate(this, statement, parameterIndex);
        }
        return statement.getRawObject().getDate(parameterIndex);
    }

    @Override
    public java.sql.Time callableStatement_getTime(CallableStatementProxy statement, int parameterIndex)
                                                                                                        throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getTime(this, statement, parameterIndex);
        }
        return statement.getRawObject().getTime(parameterIndex);
    }

    @Override
    public java.sql.Timestamp callableStatement_getTimestamp(CallableStatementProxy statement, int parameterIndex)
                                                                                                                  throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getTimestamp(this, statement, parameterIndex);
        }
        return statement.getRawObject().getTimestamp(parameterIndex);
    }

    @Override
    public Object callableStatement_getObject(CallableStatementProxy statement, int parameterIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getObject(this, statement, parameterIndex);
        }

        Object obj = statement.getRawObject().getObject(parameterIndex);

        if (obj instanceof ResultSet) {
            return new ResultSetProxyImpl(statement, (ResultSet) obj, dataSource.createResultSetId(),
                    statement.getLastExecuteSql());
        }

        if (obj instanceof Clob) {
            return wrap(statement, (Clob) obj);
        }

        return obj;
    }

    @Override
    public Object callableStatement_getObject(CallableStatementProxy statement, int parameterIndex,
                                              java.util.Map<String, Class<?>> map) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getObject(this, statement, parameterIndex, map);
        }

        Object obj = statement.getRawObject().getObject(parameterIndex, map);

        if (obj instanceof ResultSet) {
            return new ResultSetProxyImpl(statement, (ResultSet) obj, dataSource.createResultSetId(),
                    statement.getLastExecuteSql());
        }

        if (obj instanceof Clob) {
            return wrap(statement, (Clob) obj);
        }

        return obj;
    }

    @Override
    public Object callableStatement_getObject(CallableStatementProxy statement, String parameterName)
                                                                                                     throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getObject(this, statement, parameterName);
        }

        Object obj = statement.getRawObject().getObject(parameterName);

        if (obj instanceof ResultSet) {
            return new ResultSetProxyImpl(statement, (ResultSet) obj, dataSource.createResultSetId(),
                    statement.getLastExecuteSql());
        }

        if (obj instanceof Clob) {
            return wrap(statement, (Clob) obj);
        }

        return obj;
    }

    @Override
    public Object callableStatement_getObject(CallableStatementProxy statement, String parameterName,
                                              java.util.Map<String, Class<?>> map) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getObject(this, statement, parameterName, map);
        }

        Object obj = statement.getRawObject().getObject(parameterName, map);

        if (obj instanceof ResultSet) {
            return new ResultSetProxyImpl(statement, (ResultSet) obj, dataSource.createResultSetId(),
                    statement.getLastExecuteSql());
        }

        if (obj instanceof Clob) {
            return wrap(statement, (Clob) obj);
        }

        return obj;
    }

    @Override
    public BigDecimal callableStatement_getBigDecimal(CallableStatementProxy statement, int parameterIndex)
                                                                                                           throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getBigDecimal(this, statement, parameterIndex);
        }
        return statement.getRawObject().getBigDecimal(parameterIndex);
    }

    @Override
    public Ref callableStatement_getRef(CallableStatementProxy statement, int parameterIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getRef(this, statement, parameterIndex);
        }
        return statement.getRawObject().getRef(parameterIndex);
    }

    @Override
    public Blob callableStatement_getBlob(CallableStatementProxy statement, int parameterIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getBlob(this, statement, parameterIndex);
        }
        return statement.getRawObject().getBlob(parameterIndex);
    }

    @Override
    public Clob callableStatement_getClob(CallableStatementProxy statement, int parameterIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getClob(this, statement, parameterIndex);
        }

        Clob clob = statement.getRawObject()
                .getClob(parameterIndex);

        return wrap(statement, clob);
    }

    @Override
    public Array callableStatement_getArray(CallableStatementProxy statement, int parameterIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getArray(this, statement, parameterIndex);
        }
        return statement.getRawObject().getArray(parameterIndex);
    }

    @Override
    public java.sql.Date callableStatement_getDate(CallableStatementProxy statement, int parameterIndex, Calendar cal)
                                                                                                                      throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getDate(this, statement, parameterIndex, cal);
        }
        return statement.getRawObject().getDate(parameterIndex, cal);
    }

    @Override
    public java.sql.Time callableStatement_getTime(CallableStatementProxy statement, int parameterIndex, Calendar cal)
                                                                                                                      throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getTime(this, statement, parameterIndex, cal);
        }
        return statement.getRawObject().getTime(parameterIndex, cal);
    }

    @Override
    public java.sql.Timestamp callableStatement_getTimestamp(CallableStatementProxy statement, int parameterIndex,
                                                             Calendar cal) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getTimestamp(this, statement, parameterIndex, cal);
        }
        return statement.getRawObject().getTimestamp(parameterIndex, cal);
    }

    @Override
    public void callableStatement_registerOutParameter(CallableStatementProxy statement, int parameterIndex,
                                                       int sqlType, String typeName) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_registerOutParameter(this, statement, parameterIndex, sqlType, typeName);
            return;
        }
        statement.getRawObject().registerOutParameter(parameterIndex, sqlType, typeName);
    }

    @Override
    public void callableStatement_registerOutParameter(CallableStatementProxy statement, String parameterName,
                                                       int sqlType) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_registerOutParameter(this, statement, parameterName, sqlType);
            return;
        }
        statement.getRawObject().registerOutParameter(parameterName, sqlType);
    }

    @Override
    public void callableStatement_registerOutParameter(CallableStatementProxy statement, String parameterName,
                                                       int sqlType, int scale) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_registerOutParameter(this, statement, parameterName, sqlType, scale);
            return;
        }
        statement.getRawObject().registerOutParameter(parameterName, sqlType, scale);
    }

    @Override
    public void callableStatement_registerOutParameter(CallableStatementProxy statement, String parameterName,
                                                       int sqlType, String typeName) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_registerOutParameter(this, statement, parameterName, sqlType, typeName);
            return;
        }
        statement.getRawObject().registerOutParameter(parameterName, sqlType, typeName);
    }

    @Override
    public java.net.URL callableStatement_getURL(CallableStatementProxy statement, int parameterIndex)
                                                                                                      throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getURL(this, statement, parameterIndex);
        }
        return statement.getRawObject().getURL(parameterIndex);
    }

    @Override
    public void callableStatement_setURL(CallableStatementProxy statement, String parameterName, java.net.URL val)
                                                                                                                  throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setURL(this, statement, parameterName, val);
            return;
        }
        statement.getRawObject().setURL(parameterName, val);
    }

    @Override
    public void callableStatement_setNull(CallableStatementProxy statement, String parameterName, int sqlType)
                                                                                                              throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setNull(this, statement, parameterName, sqlType);
            return;
        }
        statement.getRawObject().setNull(parameterName, sqlType);
    }

    @Override
    public void callableStatement_setBoolean(CallableStatementProxy statement, String parameterName, boolean x)
                                                                                                               throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setBoolean(this, statement, parameterName, x);
            return;
        }
        statement.getRawObject().setBoolean(parameterName, x);
    }

    @Override
    public void callableStatement_setByte(CallableStatementProxy statement, String parameterName, byte x)
                                                                                                         throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setByte(this, statement, parameterName, x);
        }
        statement.getRawObject().setByte(parameterName, x);
    }

    @Override
    public void callableStatement_setShort(CallableStatementProxy statement, String parameterName, short x)
                                                                                                           throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setShort(this, statement, parameterName, x);
            return;
        }
        statement.getRawObject().setShort(parameterName, x);
    }

    @Override
    public void callableStatement_setInt(CallableStatementProxy statement, String parameterName, int x)
                                                                                                       throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setInt(this, statement, parameterName, x);
            return;
        }
        statement.getRawObject().setInt(parameterName, x);
    }

    @Override
    public void callableStatement_setLong(CallableStatementProxy statement, String parameterName, long x)
                                                                                                         throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setLong(this, statement, parameterName, x);
            return;
        }
        statement.getRawObject().setLong(parameterName, x);
    }

    @Override
    public void callableStatement_setFloat(CallableStatementProxy statement, String parameterName, float x)
                                                                                                           throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setFloat(this, statement, parameterName, x);
            return;
        }
        statement.getRawObject().setFloat(parameterName, x);
    }

    @Override
    public void callableStatement_setDouble(CallableStatementProxy statement, String parameterName, double x)
                                                                                                             throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setDouble(this, statement, parameterName, x);
            return;
        }
        statement.getRawObject().setDouble(parameterName, x);
    }

    @Override
    public void callableStatement_setBigDecimal(CallableStatementProxy statement, String parameterName, BigDecimal x)
                                                                                                                     throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setBigDecimal(this, statement, parameterName, x);
            return;
        }
        statement.getRawObject().setBigDecimal(parameterName, x);
    }

    @Override
    public void callableStatement_setString(CallableStatementProxy statement, String parameterName, String x)
                                                                                                             throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setString(this, statement, parameterName, x);
            return;
        }
        statement.getRawObject().setString(parameterName, x);
    }

    @Override
    public void callableStatement_setBytes(CallableStatementProxy statement, String parameterName, byte x[])
                                                                                                            throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setBytes(this, statement, parameterName, x);
            return;
        }
        statement.getRawObject().setBytes(parameterName, x);
    }

    @Override
    public void callableStatement_setDate(CallableStatementProxy statement, String parameterName, java.sql.Date x)
                                                                                                                  throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setDate(this, statement, parameterName, x);
            return;
        }
        statement.getRawObject().setDate(parameterName, x);
    }

    @Override
    public void callableStatement_setTime(CallableStatementProxy statement, String parameterName, java.sql.Time x)
                                                                                                                  throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setTime(this, statement, parameterName, x);
            return;
        }
        statement.getRawObject().setTime(parameterName, x);
    }

    @Override
    public void callableStatement_setTimestamp(CallableStatementProxy statement, String parameterName,
                                               java.sql.Timestamp x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setTimestamp(this, statement, parameterName, x);
            return;
        }
        statement.getRawObject().setTimestamp(parameterName, x);
    }

    @Override
    public void callableStatement_setAsciiStream(CallableStatementProxy statement, String parameterName,
                                                 java.io.InputStream x, int length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setAsciiStream(this, statement, parameterName, x, length);
            return;
        }
        statement.getRawObject().setAsciiStream(parameterName, x, length);
    }

    @Override
    public void callableStatement_setBinaryStream(CallableStatementProxy statement, String parameterName,
                                                  java.io.InputStream x, int length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setBinaryStream(this, statement, parameterName, x, length);
            return;
        }
        statement.getRawObject().setBinaryStream(parameterName, x, length);
    }

    @Override
    public void callableStatement_setObject(CallableStatementProxy statement, String parameterName, Object x,
                                            int targetSqlType, int scale) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setObject(this, statement, parameterName, x, targetSqlType, scale);
            return;
        }
        statement.getRawObject().setObject(parameterName, x, targetSqlType, scale);
    }

    @Override
    public void callableStatement_setObject(CallableStatementProxy statement, String parameterName, Object x,
                                            int targetSqlType) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setObject(this, statement, parameterName, x, targetSqlType);
            return;
        }
        statement.getRawObject().setObject(parameterName, x, targetSqlType);
    }

    @Override
    public void callableStatement_setObject(CallableStatementProxy statement, String parameterName, Object x)
                                                                                                             throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setObject(this, statement, parameterName, x);
            return;
        }
        statement.getRawObject().setObject(parameterName, x);
    }

    @Override
    public void callableStatement_setCharacterStream(CallableStatementProxy statement, String parameterName,
                                                     java.io.Reader reader, int length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setCharacterStream(this, statement, parameterName, reader, length);
            return;
        }
        statement.getRawObject().setCharacterStream(parameterName, reader, length);
    }

    @Override
    public void callableStatement_setDate(CallableStatementProxy statement, String parameterName, java.sql.Date x,
                                          Calendar cal) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setDate(this, statement, parameterName, x, cal);
            return;
        }
        statement.getRawObject().setDate(parameterName, x, cal);
    }

    @Override
    public void callableStatement_setTime(CallableStatementProxy statement, String parameterName, java.sql.Time x,
                                          Calendar cal) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setTime(this, statement, parameterName, x, cal);
            return;
        }
        statement.getRawObject().setTime(parameterName, x, cal);
    }

    @Override
    public void callableStatement_setTimestamp(CallableStatementProxy statement, String parameterName,
                                               java.sql.Timestamp x, Calendar cal) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setTimestamp(this, statement, parameterName, x, cal);
            return;
        }
        statement.getRawObject().setTimestamp(parameterName, x, cal);
    }

    @Override
    public void callableStatement_setNull(CallableStatementProxy statement, String parameterName, int sqlType,
                                          String typeName) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setNull(this, statement, parameterName, sqlType, typeName);
            return;
        }
        statement.getRawObject().setNull(parameterName, sqlType, typeName);
    }

    @Override
    public String callableStatement_getString(CallableStatementProxy statement, String parameterName)
                                                                                                     throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getString(this, statement, parameterName);
        }
        return statement.getRawObject().getString(parameterName);
    }

    @Override
    public boolean callableStatement_getBoolean(CallableStatementProxy statement, String parameterName)
                                                                                                       throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getBoolean(this, statement, parameterName);
        }
        return statement.getRawObject().getBoolean(parameterName);
    }

    @Override
    public byte callableStatement_getByte(CallableStatementProxy statement, String parameterName) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getByte(this, statement, parameterName);
        }
        return statement.getRawObject().getByte(parameterName);
    }

    @Override
    public short callableStatement_getShort(CallableStatementProxy statement, String parameterName) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getShort(this, statement, parameterName);
        }
        return statement.getRawObject().getShort(parameterName);
    }

    @Override
    public int callableStatement_getInt(CallableStatementProxy statement, String parameterName) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getInt(this, statement, parameterName);
        }
        return statement.getRawObject().getInt(parameterName);
    }

    @Override
    public long callableStatement_getLong(CallableStatementProxy statement, String parameterName) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getLong(this, statement, parameterName);
        }
        return statement.getRawObject().getLong(parameterName);
    }

    @Override
    public float callableStatement_getFloat(CallableStatementProxy statement, String parameterName) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getFloat(this, statement, parameterName);
        }
        return statement.getRawObject().getFloat(parameterName);
    }

    @Override
    public double callableStatement_getDouble(CallableStatementProxy statement, String parameterName)
                                                                                                     throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getDouble(this, statement, parameterName);
        }
        return statement.getRawObject().getDouble(parameterName);
    }

    @Override
    public byte[] callableStatement_getBytes(CallableStatementProxy statement, String parameterName)
                                                                                                    throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getBytes(this, statement, parameterName);
        }
        return statement.getRawObject().getBytes(parameterName);
    }

    @Override
    public java.sql.Date callableStatement_getDate(CallableStatementProxy statement, String parameterName)
                                                                                                          throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getDate(this, statement, parameterName);
        }
        return statement.getRawObject().getDate(parameterName);
    }

    @Override
    public java.sql.Time callableStatement_getTime(CallableStatementProxy statement, String parameterName)
                                                                                                          throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getTime(this, statement, parameterName);
        }
        return statement.getRawObject().getTime(parameterName);
    }

    @Override
    public java.sql.Timestamp callableStatement_getTimestamp(CallableStatementProxy statement, String parameterName)
                                                                                                                    throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getTimestamp(this, statement, parameterName);
        }
        return statement.getRawObject().getTimestamp(parameterName);
    }

    @Override
    public BigDecimal callableStatement_getBigDecimal(CallableStatementProxy statement, String parameterName)
                                                                                                             throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getBigDecimal(this, statement, parameterName);
        }
        return statement.getRawObject().getBigDecimal(parameterName);
    }

    @Override
    public Ref callableStatement_getRef(CallableStatementProxy statement, String parameterName) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getRef(this, statement, parameterName);
        }
        return statement.getRawObject().getRef(parameterName);
    }

    @Override
    public Blob callableStatement_getBlob(CallableStatementProxy statement, String parameterName) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getBlob(this, statement, parameterName);
        }
        return statement.getRawObject().getBlob(parameterName);
    }

    @Override
    public Clob callableStatement_getClob(CallableStatementProxy statement, String parameterName) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getClob(this, statement, parameterName);
        }

        Clob clob = statement.getRawObject()
                .getClob(parameterName);

        return wrap(statement, clob);
    }

    @Override
    public Array callableStatement_getArray(CallableStatementProxy statement, String parameterName) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getArray(this, statement, parameterName);
        }
        return statement.getRawObject().getArray(parameterName);
    }

    @Override
    public java.sql.Date callableStatement_getDate(CallableStatementProxy statement, String parameterName, Calendar cal)
                                                                                                                        throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getDate(this, statement, parameterName, cal);
        }
        return statement.getRawObject().getDate(parameterName, cal);
    }

    @Override
    public java.sql.Time callableStatement_getTime(CallableStatementProxy statement, String parameterName, Calendar cal)
                                                                                                                        throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getTime(this, statement, parameterName, cal);
        }
        return statement.getRawObject().getTime(parameterName, cal);
    }

    @Override
    public java.sql.Timestamp callableStatement_getTimestamp(CallableStatementProxy statement, String parameterName,
                                                             Calendar cal) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getTimestamp(this, statement, parameterName, cal);
        }
        return statement.getRawObject().getTimestamp(parameterName, cal);
    }

    @Override
    public java.net.URL callableStatement_getURL(CallableStatementProxy statement, String parameterName)
                                                                                                        throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getURL(this, statement, parameterName);
        }
        return statement.getRawObject().getURL(parameterName);
    }

    @Override
    public RowId callableStatement_getRowId(CallableStatementProxy statement, int parameterIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getRowId(this, statement, parameterIndex);
        }
        return statement.getRawObject().getRowId(parameterIndex);
    }

    @Override
    public RowId callableStatement_getRowId(CallableStatementProxy statement, String parameterName) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getRowId(this, statement, parameterName);
        }
        return statement.getRawObject().getRowId(parameterName);
    }

    @Override
    public void callableStatement_setRowId(CallableStatementProxy statement, String parameterName, RowId x)
                                                                                                           throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setRowId(this, statement, parameterName, x);
            return;
        }
        statement.getRawObject().setRowId(parameterName, x);
    }

    @Override
    public void callableStatement_setNString(CallableStatementProxy statement, String parameterName, String value)
                                                                                                                  throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setNString(this, statement, parameterName, value);
            return;
        }
        statement.getRawObject().setNString(parameterName, value);
    }

    @Override
    public void callableStatement_setNCharacterStream(CallableStatementProxy statement, String parameterName,
                                                      Reader value, long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setNCharacterStream(this, statement, parameterName, value, length);
            return;
        }
        statement.getRawObject().setNCharacterStream(parameterName, value, length);
    }

    @Override
    public void callableStatement_setNClob(CallableStatementProxy statement, String parameterName, NClob x)
                                                                                                               throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setNClob(this, statement, parameterName, x);
            return;
        }

        if (x instanceof NClobProxy) {
            x = ((NClobProxy) x).getRawNClob();
        }

        statement.getRawObject()
                .setNClob(parameterName, x);
    }

    @Override
    public void callableStatement_setClob(CallableStatementProxy statement, String parameterName, Reader reader,
                                          long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setClob(this, statement, parameterName, reader, length);
            return;
        }
        statement.getRawObject().setClob(parameterName, reader, length);
    }

    @Override
    public void callableStatement_setBlob(CallableStatementProxy statement, String parameterName,
                                          InputStream inputStream, long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setBlob(this, statement, parameterName, inputStream, length);
            return;
        }
        statement.getRawObject().setBlob(parameterName, inputStream, length);
    }

    @Override
    public void callableStatement_setNClob(CallableStatementProxy statement, String parameterName, Reader reader,
                                           long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setNClob(this, statement, parameterName, reader, length);
            return;
        }
        statement.getRawObject().setNClob(parameterName, reader, length);
    }

    @Override
    public NClob callableStatement_getNClob(CallableStatementProxy statement, int parameterIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getNClob(this, statement, parameterIndex);
        }

        NClob nclob = statement.getRawObject()
                .getNClob(parameterIndex);

        return wrap(statement.getConnectionProxy(), nclob);
    }

    @Override
    public NClob callableStatement_getNClob(CallableStatementProxy statement, String parameterName) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getNClob(this, statement, parameterName);
        }

        NClob nclob = statement.getRawObject()
                .getNClob(parameterName);

        return wrap(statement.getConnectionProxy(), nclob);
    }

    @Override
    public void callableStatement_setSQLXML(CallableStatementProxy statement, String parameterName, SQLXML xmlObject)
                                                                                                                     throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setSQLXML(this, statement, parameterName, xmlObject);
            return;
        }
        statement.getRawObject().setSQLXML(parameterName, xmlObject);
    }

    @Override
    public SQLXML callableStatement_getSQLXML(CallableStatementProxy statement, int parameterIndex) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getSQLXML(this, statement, parameterIndex);
        }
        return statement.getRawObject().getSQLXML(parameterIndex);
    }

    @Override
    public SQLXML callableStatement_getSQLXML(CallableStatementProxy statement, String parameterName)
                                                                                                     throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getSQLXML(this, statement, parameterName);
        }
        return statement.getRawObject().getSQLXML(parameterName);
    }

    @Override
    public String callableStatement_getNString(CallableStatementProxy statement, int parameterIndex)
                                                                                                    throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getNString(this, statement, parameterIndex);
        }
        return statement.getRawObject().getNString(parameterIndex);
    }

    @Override
    public String callableStatement_getNString(CallableStatementProxy statement, String parameterName)
                                                                                                      throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getNString(this, statement, parameterName);
        }
        return statement.getRawObject().getNString(parameterName);
    }

    @Override
    public java.io.Reader callableStatement_getNCharacterStream(CallableStatementProxy statement, int parameterIndex)
                                                                                                                     throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getNCharacterStream(this, statement, parameterIndex);
        }
        return statement.getRawObject().getNCharacterStream(parameterIndex);
    }

    @Override
    public java.io.Reader callableStatement_getNCharacterStream(CallableStatementProxy statement, String parameterName)
                                                                                                                       throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getNCharacterStream(this, statement, parameterName);
        }
        return statement.getRawObject().getNCharacterStream(parameterName);
    }

    @Override
    public java.io.Reader callableStatement_getCharacterStream(CallableStatementProxy statement, int parameterIndex)
                                                                                                                    throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getCharacterStream(this, statement, parameterIndex);
        }
        return statement.getRawObject().getCharacterStream(parameterIndex);
    }

    @Override
    public java.io.Reader callableStatement_getCharacterStream(CallableStatementProxy statement, String parameterName)
                                                                                                                      throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().callableStatement_getCharacterStream(this, statement, parameterName);
        }
        return statement.getRawObject().getCharacterStream(parameterName);
    }

    @Override
    public void callableStatement_setBlob(CallableStatementProxy statement, String parameterName, Blob x)
                                                                                                         throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setBlob(this, statement, parameterName, x);
            return;
        }
        statement.getRawObject().setBlob(parameterName, x);
    }

    @Override
    public void callableStatement_setClob(CallableStatementProxy statement, String parameterName, Clob x)
                                                                                                         throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setClob(this, statement, parameterName, x);
            return;
        }

        if (x instanceof ClobProxy) {
            x = ((ClobProxy) x).getRawClob();
        }

        statement.getRawObject()
                .setClob(parameterName, x);
    }

    @Override
    public void callableStatement_setAsciiStream(CallableStatementProxy statement, String parameterName,
                                                 java.io.InputStream x, long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setAsciiStream(this, statement, parameterName, x, length);
            return;
        }
        statement.getRawObject().setAsciiStream(parameterName, x, length);
    }

    @Override
    public void callableStatement_setBinaryStream(CallableStatementProxy statement, String parameterName,
                                                  java.io.InputStream x, long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setBinaryStream(this, statement, parameterName, x, length);
            return;
        }
        statement.getRawObject().setBinaryStream(parameterName, x, length);
    }

    @Override
    public void callableStatement_setCharacterStream(CallableStatementProxy statement, String parameterName,
                                                     java.io.Reader reader, long length) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setCharacterStream(this, statement, parameterName, reader, length);
            return;
        }
        statement.getRawObject().setCharacterStream(parameterName, reader, length);
    }

    @Override
    public void callableStatement_setAsciiStream(CallableStatementProxy statement, String parameterName,
                                                 java.io.InputStream x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setAsciiStream(this, statement, parameterName, x);
            return;
        }
        statement.getRawObject().setAsciiStream(parameterName, x);
    }

    @Override
    public void callableStatement_setBinaryStream(CallableStatementProxy statement, String parameterName,
                                                  java.io.InputStream x) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setBinaryStream(this, statement, parameterName, x);
            return;
        }
        statement.getRawObject().setBinaryStream(parameterName, x);
    }

    @Override
    public void callableStatement_setCharacterStream(CallableStatementProxy statement, String parameterName,
                                                     java.io.Reader reader) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setCharacterStream(this, statement, parameterName, reader);
            return;
        }
        statement.getRawObject().setCharacterStream(parameterName, reader);
    }

    @Override
    public void callableStatement_setNCharacterStream(CallableStatementProxy statement, String parameterName,
                                                      Reader value) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setNCharacterStream(this, statement, parameterName, value);
            return;
        }
        statement.getRawObject().setNCharacterStream(parameterName, value);
    }

    @Override
    public void callableStatement_setClob(CallableStatementProxy statement, String parameterName, Reader reader)
                                                                                                                throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setClob(this, statement, parameterName, reader);
            return;
        }
        statement.getRawObject().setClob(parameterName, reader);
    }

    @Override
    public void callableStatement_setBlob(CallableStatementProxy statement, String parameterName,
                                          InputStream inputStream) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setBlob(this, statement, parameterName, inputStream);
            return;
        }
        statement.getRawObject().setBlob(parameterName, inputStream);
    }

    @Override
    public void callableStatement_setNClob(CallableStatementProxy statement, String parameterName, Reader reader)
                                                                                                                 throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().callableStatement_setNClob(this, statement, parameterName, reader);
            return;
        }
        statement.getRawObject().setNClob(parameterName, reader);
    }

    @Override
    public long clob_length(ClobProxy clob) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().clob_length(this, clob);
        }
        return clob.getRawClob().length();
    }

    @Override
    public String clob_getSubString(ClobProxy clob, long pos, int length) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().clob_getSubString(this, clob, pos, length);
        }
        return clob.getRawClob().getSubString(pos, length);
    }

    @Override
    public java.io.Reader clob_getCharacterStream(ClobProxy clob) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().clob_getCharacterStream(this, clob);
        }
        return clob.getRawClob().getCharacterStream();
    }

    @Override
    public java.io.InputStream clob_getAsciiStream(ClobProxy clob) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().clob_getAsciiStream(this, clob);
        }
        return clob.getRawClob().getAsciiStream();
    }

    @Override
    public long clob_position(ClobProxy clob, String searchstr, long start) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().clob_position(this, clob, searchstr, start);
        }
        return clob.getRawClob().position(searchstr, start);
    }

    @Override
    public long clob_position(ClobProxy clob, Clob searchstr, long start) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().clob_position(this, clob, searchstr, start);
        }
        return clob.getRawClob().position(searchstr, start);
    }

    @Override
    public int clob_setString(ClobProxy clob, long pos, String str) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().clob_setString(this, clob, pos, str);
        }
        return clob.getRawClob().setString(pos, str);
    }

    @Override
    public int clob_setString(ClobProxy clob, long pos, String str, int offset, int len) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().clob_setString(this, clob, pos, str, offset, len);
        }
        return clob.getRawClob().setString(pos, str, offset, len);
    }

    @Override
    public java.io.OutputStream clob_setAsciiStream(ClobProxy clob, long pos) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().clob_setAsciiStream(this, clob, pos);
        }
        return clob.getRawClob().setAsciiStream(pos);
    }

    @Override
    public java.io.Writer clob_setCharacterStream(ClobProxy clob, long pos) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().clob_setCharacterStream(this, clob, pos);
        }
        return clob.getRawClob().setCharacterStream(pos);
    }

    @Override
    public void clob_truncate(ClobProxy clob, long len) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().clob_truncate(this, clob, len);
            return;
        }
        clob.getRawClob().truncate(len);
    }

    @Override
    public void clob_free(ClobProxy clob) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().clob_free(this, clob);
            return;
        }
        clob.getRawClob().free();
    }

    @Override
    public Reader clob_getCharacterStream(ClobProxy clob, long pos, long length) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().clob_getCharacterStream(this, clob, pos, length);
        }
        return clob.getRawClob().getCharacterStream(pos, length);
    }

    // ////////////

    public ClobProxy wrap(ConnectionProxy conn, Clob clob) {
        if (clob == null) {
            return null;
        }

        if (clob instanceof NClob) {
            return wrap(conn, (NClob) clob);
        }

        return new ClobProxyImpl(dataSource, conn, clob);
    }

    public NClobProxy wrap(ConnectionProxy conn, NClob clob) {
        if (clob == null) {
            return null;
        }

        return new NClobProxyImpl(dataSource, conn, clob);
    }

    public ClobProxy wrap(StatementProxy stmt, Clob clob) {
        if (clob == null) {
            return null;
        }

        if (clob instanceof NClob) {
            return wrap(stmt, (NClob) clob);
        }

        return new ClobProxyImpl(dataSource, stmt.getConnectionProxy(), clob);
    }

    public NClobProxy wrap(StatementProxy stmt, NClob nclob) {
        if (nclob == null) {
            return null;
        }

        return new NClobProxyImpl(dataSource, stmt.getConnectionProxy(), nclob);
    }

    @Override
    public void dataSource_recycle(DruidPooledConnection connection) throws SQLException {
        if (this.pos < filterSize) {
            nextFilter().dataSource_releaseConnection(this, connection);
            return;
        }

        connection.recycle();
    }

    @Override
    public DruidPooledConnection dataSource_connect(DruidDataSource dataSource, long maxWaitMillis) throws SQLException {
        if (this.pos < filterSize) {
            DruidPooledConnection conn = nextFilter().dataSource_getConnection(this, dataSource, maxWaitMillis);
            return conn;
        }

        return dataSource.getConnectionDirect(maxWaitMillis);
    }

    @Override
    public int resultSetMetaData_getColumnCount(ResultSetMetaDataProxy metaData) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSetMetaData_getColumnCount(this, metaData);
        }

        return metaData.getResultSetMetaDataRaw().getColumnCount();
    }

    @Override
    public boolean resultSetMetaData_isAutoIncrement(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSetMetaData_isAutoIncrement(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw().isAutoIncrement(column);
    }

    @Override
    public boolean resultSetMetaData_isCaseSensitive(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSetMetaData_isCaseSensitive(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw().isCaseSensitive(column);
    }

    @Override
    public boolean resultSetMetaData_isSearchable(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSetMetaData_isSearchable(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw().isSearchable(column);
    }

    @Override
    public boolean resultSetMetaData_isCurrency(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSetMetaData_isCurrency(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw().isCurrency(column);
    }

    @Override
    public int resultSetMetaData_isNullable(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSetMetaData_isNullable(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw().isNullable(column);
    }

    @Override
    public boolean resultSetMetaData_isSigned(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSetMetaData_isSigned(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw().isSigned(column);
    }

    @Override
    public int resultSetMetaData_getColumnDisplaySize(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSetMetaData_getColumnDisplaySize(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw().getColumnDisplaySize(column);
    }

    @Override
    public String resultSetMetaData_getColumnLabel(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSetMetaData_getColumnLabel(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw().getColumnLabel(column);
    }

    @Override
    public String resultSetMetaData_getColumnName(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSetMetaData_getColumnName(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw().getColumnName(column);
    }

    @Override
    public String resultSetMetaData_getSchemaName(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSetMetaData_getSchemaName(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw().getSchemaName(column);
    }

    @Override
    public int resultSetMetaData_getPrecision(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter().resultSetMetaData_getPrecision(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw()
                .getPrecision(column);
    }

    @Override
    public int resultSetMetaData_getScale(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSetMetaData_getScale(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw()
                .getScale(column);
    }

    @Override
    public String resultSetMetaData_getTableName(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSetMetaData_getTableName(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw()
                .getTableName(column);
    }

    @Override
    public String resultSetMetaData_getCatalogName(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSetMetaData_getCatalogName(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw()
                .getCatalogName(column);
    }

    @Override
    public int resultSetMetaData_getColumnType(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSetMetaData_getColumnType(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw()
                .getColumnType(column);
    }

    @Override
    public String resultSetMetaData_getColumnTypeName(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSetMetaData_getColumnTypeName(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw()
                .getColumnTypeName(column);
    }

    @Override
    public boolean resultSetMetaData_isReadOnly(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSetMetaData_isReadOnly(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw()
                .isReadOnly(column);
    }

    @Override
    public boolean resultSetMetaData_isWritable(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSetMetaData_isWritable(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw()
                .isWritable(column);
    }

    @Override
    public boolean resultSetMetaData_isDefinitelyWritable(ResultSetMetaDataProxy metaData, int column)
                                                                                                      throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSetMetaData_isDefinitelyWritable(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw()
                .isDefinitelyWritable(column);
    }

    @Override
    public String resultSetMetaData_getColumnClassName(ResultSetMetaDataProxy metaData, int column) throws SQLException {
        if (this.pos < filterSize) {
            return nextFilter()
                    .resultSetMetaData_getColumnClassName(this, metaData, column);
        }

        return metaData.getResultSetMetaDataRaw()
                .getColumnClassName(column);
    }

}
