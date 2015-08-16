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
package com.alibaba.druid.filter;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.Ref;
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
import java.util.Properties;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.ClobProxy;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.DataSourceProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetMetaDataProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public interface FilterChain {

    DataSourceProxy getDataSource();

    int getFilterSize();

    FilterChain cloneChain();

    <T> T unwrap(Wrapper wrapper, java.lang.Class<T> iface) throws java.sql.SQLException;

    boolean isWrapperFor(Wrapper wrapper, java.lang.Class<?> iface) throws java.sql.SQLException;

    ConnectionProxy connection_connect(Properties info) throws SQLException;

    StatementProxy connection_createStatement(ConnectionProxy connection) throws SQLException;

    PreparedStatementProxy connection_prepareStatement(ConnectionProxy connection, String sql) throws SQLException;

    CallableStatementProxy connection_prepareCall(ConnectionProxy connection, String sql) throws SQLException;

    String connection_nativeSQL(ConnectionProxy connection, String sql) throws SQLException;

    void connection_setAutoCommit(ConnectionProxy connection, boolean autoCommit) throws SQLException;

    boolean connection_getAutoCommit(ConnectionProxy connection) throws SQLException;

    void connection_commit(ConnectionProxy connection) throws SQLException;

    void connection_rollback(ConnectionProxy connection) throws SQLException;

    void connection_close(ConnectionProxy connection) throws SQLException;

    boolean connection_isClosed(ConnectionProxy connection) throws SQLException;

    DatabaseMetaData connection_getMetaData(ConnectionProxy connection) throws SQLException;

    void connection_setReadOnly(ConnectionProxy connection, boolean readOnly) throws SQLException;

    boolean connection_isReadOnly(ConnectionProxy connection) throws SQLException;

    void connection_setCatalog(ConnectionProxy connection, String catalog) throws SQLException;

    String connection_getCatalog(ConnectionProxy connection) throws SQLException;

    void connection_setTransactionIsolation(ConnectionProxy connection, int level) throws SQLException;

    int connection_getTransactionIsolation(ConnectionProxy connection) throws SQLException;

    SQLWarning connection_getWarnings(ConnectionProxy connection) throws SQLException;

    void connection_clearWarnings(ConnectionProxy connection) throws SQLException;

    StatementProxy connection_createStatement(ConnectionProxy connection, int resultSetType, int resultSetConcurrency)
                                                                                                                      throws SQLException;

    PreparedStatementProxy connection_prepareStatement(ConnectionProxy connection, String sql, int resultSetType,
                                                       int resultSetConcurrency) throws SQLException;

    CallableStatementProxy connection_prepareCall(ConnectionProxy connection, String sql, int resultSetType,
                                                  int resultSetConcurrency) throws SQLException;

    java.util.Map<String, Class<?>> connection_getTypeMap(ConnectionProxy connection) throws SQLException;

    void connection_setTypeMap(ConnectionProxy connection, java.util.Map<String, Class<?>> map) throws SQLException;

    void connection_setHoldability(ConnectionProxy connection, int holdability) throws SQLException;

    int connection_getHoldability(ConnectionProxy connection) throws SQLException;

    Savepoint connection_setSavepoint(ConnectionProxy connection) throws SQLException;

    Savepoint connection_setSavepoint(ConnectionProxy connection, String name) throws SQLException;

    void connection_rollback(ConnectionProxy connection, Savepoint savepoint) throws SQLException;

    void connection_releaseSavepoint(ConnectionProxy connection, Savepoint savepoint) throws SQLException;

    StatementProxy connection_createStatement(ConnectionProxy connection, int resultSetType, int resultSetConcurrency,
                                              int resultSetHoldability) throws SQLException;

    PreparedStatementProxy connection_prepareStatement(ConnectionProxy connection, String sql, int resultSetType,
                                                       int resultSetConcurrency, int resultSetHoldability)
                                                                                                          throws SQLException;

    CallableStatementProxy connection_prepareCall(ConnectionProxy connection, String sql, int resultSetType,
                                                  int resultSetConcurrency, int resultSetHoldability)
                                                                                                     throws SQLException;

    PreparedStatementProxy connection_prepareStatement(ConnectionProxy connection, String sql, int autoGeneratedKeys)
                                                                                                                     throws SQLException;

    PreparedStatementProxy connection_prepareStatement(ConnectionProxy connection, String sql, int columnIndexes[])
                                                                                                                   throws SQLException;

    PreparedStatementProxy connection_prepareStatement(ConnectionProxy connection, String sql, String columnNames[])
                                                                                                                    throws SQLException;

    Clob connection_createClob(ConnectionProxy connection) throws SQLException;

    Blob connection_createBlob(ConnectionProxy connection) throws SQLException;

    NClob connection_createNClob(ConnectionProxy connection) throws SQLException;

    SQLXML connection_createSQLXML(ConnectionProxy connection) throws SQLException;

    boolean connection_isValid(ConnectionProxy connection, int timeout) throws SQLException;

    void connection_setClientInfo(ConnectionProxy connection, String name, String value) throws SQLClientInfoException;

    void connection_setClientInfo(ConnectionProxy connection, Properties properties) throws SQLClientInfoException;

    String connection_getClientInfo(ConnectionProxy connection, String name) throws SQLException;

    Properties connection_getClientInfo(ConnectionProxy connection) throws SQLException;

    Array connection_createArrayOf(ConnectionProxy connection, String typeName, Object[] elements) throws SQLException;

    Struct connection_createStruct(ConnectionProxy connection, String typeName, Object[] attributes)
                                                                                                    throws SQLException;

    // ---------

    // ///////////////
    boolean resultSet_next(ResultSetProxy resultSet) throws SQLException;

    void resultSet_close(ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_wasNull(ResultSetProxy resultSet) throws SQLException;

    String resultSet_getString(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    boolean resultSet_getBoolean(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    byte resultSet_getByte(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    short resultSet_getShort(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    int resultSet_getInt(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    long resultSet_getLong(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    float resultSet_getFloat(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    double resultSet_getDouble(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    BigDecimal resultSet_getBigDecimal(ResultSetProxy resultSet, int columnIndex, int scale) throws SQLException;

    byte[] resultSet_getBytes(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    java.sql.Date resultSet_getDate(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    java.sql.Time resultSet_getTime(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    java.sql.Timestamp resultSet_getTimestamp(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    java.io.InputStream resultSet_getAsciiStream(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    java.io.InputStream resultSet_getUnicodeStream(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    java.io.InputStream resultSet_getBinaryStream(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    String resultSet_getString(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    boolean resultSet_getBoolean(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    byte resultSet_getByte(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    short resultSet_getShort(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    int resultSet_getInt(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    long resultSet_getLong(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    float resultSet_getFloat(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    double resultSet_getDouble(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    BigDecimal resultSet_getBigDecimal(ResultSetProxy resultSet, String columnLabel, int scale) throws SQLException;

    byte[] resultSet_getBytes(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    java.sql.Date resultSet_getDate(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    java.sql.Time resultSet_getTime(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    java.sql.Timestamp resultSet_getTimestamp(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    java.io.InputStream resultSet_getAsciiStream(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    java.io.InputStream resultSet_getUnicodeStream(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    java.io.InputStream resultSet_getBinaryStream(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    SQLWarning resultSet_getWarnings(ResultSetProxy resultSet) throws SQLException;

    void resultSet_clearWarnings(ResultSetProxy resultSet) throws SQLException;

    String resultSet_getCursorName(ResultSetProxy resultSet) throws SQLException;

    ResultSetMetaData resultSet_getMetaData(ResultSetProxy resultSet) throws SQLException;

    Object resultSet_getObject(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    Object resultSet_getObject(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    int resultSet_findColumn(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    java.io.Reader resultSet_getCharacterStream(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    java.io.Reader resultSet_getCharacterStream(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    BigDecimal resultSet_getBigDecimal(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    BigDecimal resultSet_getBigDecimal(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    boolean resultSet_isBeforeFirst(ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_isAfterLast(ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_isFirst(ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_isLast(ResultSetProxy resultSet) throws SQLException;

    void resultSet_beforeFirst(ResultSetProxy resultSet) throws SQLException;

    void resultSet_afterLast(ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_first(ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_last(ResultSetProxy resultSet) throws SQLException;

    int resultSet_getRow(ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_absolute(ResultSetProxy resultSet, int row) throws SQLException;

    boolean resultSet_relative(ResultSetProxy resultSet, int rows) throws SQLException;

    boolean resultSet_previous(ResultSetProxy resultSet) throws SQLException;

    void resultSet_setFetchDirection(ResultSetProxy resultSet, int direction) throws SQLException;

    int resultSet_getFetchDirection(ResultSetProxy resultSet) throws SQLException;

    void resultSet_setFetchSize(ResultSetProxy resultSet, int rows) throws SQLException;

    int resultSet_getFetchSize(ResultSetProxy resultSet) throws SQLException;

    int resultSet_getType(ResultSetProxy resultSet) throws SQLException;

    int resultSet_getConcurrency(ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_rowUpdated(ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_rowInserted(ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_rowDeleted(ResultSetProxy resultSet) throws SQLException;

    void resultSet_updateNull(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    void resultSet_updateBoolean(ResultSetProxy resultSet, int columnIndex, boolean x) throws SQLException;

    void resultSet_updateByte(ResultSetProxy resultSet, int columnIndex, byte x) throws SQLException;

    void resultSet_updateShort(ResultSetProxy resultSet, int columnIndex, short x) throws SQLException;

    void resultSet_updateInt(ResultSetProxy resultSet, int columnIndex, int x) throws SQLException;

    void resultSet_updateLong(ResultSetProxy resultSet, int columnIndex, long x) throws SQLException;

    void resultSet_updateFloat(ResultSetProxy resultSet, int columnIndex, float x) throws SQLException;

    void resultSet_updateDouble(ResultSetProxy resultSet, int columnIndex, double x) throws SQLException;

    void resultSet_updateBigDecimal(ResultSetProxy resultSet, int columnIndex, BigDecimal x) throws SQLException;

    void resultSet_updateString(ResultSetProxy resultSet, int columnIndex, String x) throws SQLException;

    void resultSet_updateBytes(ResultSetProxy resultSet, int columnIndex, byte x[]) throws SQLException;

    void resultSet_updateDate(ResultSetProxy resultSet, int columnIndex, java.sql.Date x) throws SQLException;

    void resultSet_updateTime(ResultSetProxy resultSet, int columnIndex, java.sql.Time x) throws SQLException;

    void resultSet_updateTimestamp(ResultSetProxy resultSet, int columnIndex, java.sql.Timestamp x) throws SQLException;

    void resultSet_updateAsciiStream(ResultSetProxy resultSet, int columnIndex, java.io.InputStream x, int length)
                                                                                                                  throws SQLException;

    void resultSet_updateBinaryStream(ResultSetProxy resultSet, int columnIndex, java.io.InputStream x, int length)
                                                                                                                   throws SQLException;

    void resultSet_updateCharacterStream(ResultSetProxy resultSet, int columnIndex, java.io.Reader x, int length)
                                                                                                                 throws SQLException;

    void resultSet_updateObject(ResultSetProxy resultSet, int columnIndex, Object x, int scaleOrLength)
                                                                                                       throws SQLException;

    void resultSet_updateObject(ResultSetProxy resultSet, int columnIndex, Object x) throws SQLException;

    void resultSet_updateNull(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    void resultSet_updateBoolean(ResultSetProxy resultSet, String columnLabel, boolean x) throws SQLException;

    void resultSet_updateByte(ResultSetProxy resultSet, String columnLabel, byte x) throws SQLException;

    void resultSet_updateShort(ResultSetProxy resultSet, String columnLabel, short x) throws SQLException;

    void resultSet_updateInt(ResultSetProxy resultSet, String columnLabel, int x) throws SQLException;

    void resultSet_updateLong(ResultSetProxy resultSet, String columnLabel, long x) throws SQLException;

    void resultSet_updateFloat(ResultSetProxy resultSet, String columnLabel, float x) throws SQLException;

    void resultSet_updateDouble(ResultSetProxy resultSet, String columnLabel, double x) throws SQLException;

    void resultSet_updateBigDecimal(ResultSetProxy resultSet, String columnLabel, BigDecimal x) throws SQLException;

    void resultSet_updateString(ResultSetProxy resultSet, String columnLabel, String x) throws SQLException;

    void resultSet_updateBytes(ResultSetProxy resultSet, String columnLabel, byte x[]) throws SQLException;

    void resultSet_updateDate(ResultSetProxy resultSet, String columnLabel, java.sql.Date x) throws SQLException;

    void resultSet_updateTime(ResultSetProxy resultSet, String columnLabel, java.sql.Time x) throws SQLException;

    void resultSet_updateTimestamp(ResultSetProxy resultSet, String columnLabel, java.sql.Timestamp x)
                                                                                                      throws SQLException;

    void resultSet_updateAsciiStream(ResultSetProxy resultSet, String columnLabel, java.io.InputStream x, int length)
                                                                                                                     throws SQLException;

    void resultSet_updateBinaryStream(ResultSetProxy resultSet, String columnLabel, java.io.InputStream x, int length)
                                                                                                                      throws SQLException;

    void resultSet_updateCharacterStream(ResultSetProxy resultSet, String columnLabel, java.io.Reader reader, int length)
                                                                                                                         throws SQLException;

    void resultSet_updateObject(ResultSetProxy resultSet, String columnLabel, Object x, int scaleOrLength)
                                                                                                          throws SQLException;

    void resultSet_updateObject(ResultSetProxy resultSet, String columnLabel, Object x) throws SQLException;

    void resultSet_insertRow(ResultSetProxy resultSet) throws SQLException;

    void resultSet_updateRow(ResultSetProxy resultSet) throws SQLException;

    void resultSet_deleteRow(ResultSetProxy resultSet) throws SQLException;

    void resultSet_refreshRow(ResultSetProxy resultSet) throws SQLException;

    void resultSet_cancelRowUpdates(ResultSetProxy resultSet) throws SQLException;

    void resultSet_moveToInsertRow(ResultSetProxy resultSet) throws SQLException;

    void resultSet_moveToCurrentRow(ResultSetProxy resultSet) throws SQLException;

    Statement resultSet_getStatement(ResultSetProxy resultSet) throws SQLException;

    Object resultSet_getObject(ResultSetProxy resultSet, int columnIndex, java.util.Map<String, Class<?>> map)
                                                                                                              throws SQLException;

    Ref resultSet_getRef(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    Blob resultSet_getBlob(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    Clob resultSet_getClob(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    Array resultSet_getArray(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    Object resultSet_getObject(ResultSetProxy resultSet, String columnLabel, java.util.Map<String, Class<?>> map)
                                                                                                                 throws SQLException;

    Ref resultSet_getRef(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    Blob resultSet_getBlob(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    Clob resultSet_getClob(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    Array resultSet_getArray(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    java.sql.Date resultSet_getDate(ResultSetProxy resultSet, int columnIndex, Calendar cal) throws SQLException;

    java.sql.Date resultSet_getDate(ResultSetProxy resultSet, String columnLabel, Calendar cal) throws SQLException;

    java.sql.Time resultSet_getTime(ResultSetProxy resultSet, int columnIndex, Calendar cal) throws SQLException;

    java.sql.Time resultSet_getTime(ResultSetProxy resultSet, String columnLabel, Calendar cal) throws SQLException;

    java.sql.Timestamp resultSet_getTimestamp(ResultSetProxy resultSet, int columnIndex, Calendar cal)
                                                                                                      throws SQLException;

    java.sql.Timestamp resultSet_getTimestamp(ResultSetProxy resultSet, String columnLabel, Calendar cal)
                                                                                                         throws SQLException;

    java.net.URL resultSet_getURL(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    java.net.URL resultSet_getURL(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    void resultSet_updateRef(ResultSetProxy resultSet, int columnIndex, java.sql.Ref x) throws SQLException;

    void resultSet_updateRef(ResultSetProxy resultSet, String columnLabel, java.sql.Ref x) throws SQLException;

    void resultSet_updateBlob(ResultSetProxy resultSet, int columnIndex, java.sql.Blob x) throws SQLException;

    void resultSet_updateBlob(ResultSetProxy resultSet, String columnLabel, java.sql.Blob x) throws SQLException;

    void resultSet_updateClob(ResultSetProxy resultSet, int columnIndex, java.sql.Clob x) throws SQLException;

    void resultSet_updateClob(ResultSetProxy resultSet, String columnLabel, java.sql.Clob x) throws SQLException;

    void resultSet_updateArray(ResultSetProxy resultSet, int columnIndex, java.sql.Array x) throws SQLException;

    void resultSet_updateArray(ResultSetProxy resultSet, String columnLabel, java.sql.Array x) throws SQLException;

    RowId resultSet_getRowId(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    RowId resultSet_getRowId(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    void resultSet_updateRowId(ResultSetProxy resultSet, int columnIndex, RowId x) throws SQLException;

    void resultSet_updateRowId(ResultSetProxy resultSet, String columnLabel, RowId x) throws SQLException;

    int resultSet_getHoldability(ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_isClosed(ResultSetProxy resultSet) throws SQLException;

    void resultSet_updateNString(ResultSetProxy resultSet, int columnIndex, String nString) throws SQLException;

    void resultSet_updateNString(ResultSetProxy resultSet, String columnLabel, String nString) throws SQLException;

    void resultSet_updateNClob(ResultSetProxy resultSet, int columnIndex, NClob nClob) throws SQLException;

    void resultSet_updateNClob(ResultSetProxy resultSet, String columnLabel, NClob nClob) throws SQLException;

    NClob resultSet_getNClob(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    NClob resultSet_getNClob(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    SQLXML resultSet_getSQLXML(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    SQLXML resultSet_getSQLXML(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    void resultSet_updateSQLXML(ResultSetProxy resultSet, int columnIndex, SQLXML xmlObject) throws SQLException;

    void resultSet_updateSQLXML(ResultSetProxy resultSet, String columnLabel, SQLXML xmlObject) throws SQLException;

    String resultSet_getNString(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    String resultSet_getNString(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    java.io.Reader resultSet_getNCharacterStream(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    java.io.Reader resultSet_getNCharacterStream(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    void resultSet_updateNCharacterStream(ResultSetProxy resultSet, int columnIndex, java.io.Reader x, long length)
                                                                                                                   throws SQLException;

    void resultSet_updateNCharacterStream(ResultSetProxy resultSet, String columnLabel, java.io.Reader reader,
                                          long length) throws SQLException;

    void resultSet_updateAsciiStream(ResultSetProxy resultSet, int columnIndex, java.io.InputStream x, long length)
                                                                                                                   throws SQLException;

    void resultSet_updateBinaryStream(ResultSetProxy resultSet, int columnIndex, java.io.InputStream x, long length)
                                                                                                                    throws SQLException;

    void resultSet_updateCharacterStream(ResultSetProxy resultSet, int columnIndex, java.io.Reader x, long length)
                                                                                                                  throws SQLException;

    void resultSet_updateAsciiStream(ResultSetProxy resultSet, String columnLabel, java.io.InputStream x, long length)
                                                                                                                      throws SQLException;

    void resultSet_updateBinaryStream(ResultSetProxy resultSet, String columnLabel, java.io.InputStream x, long length)
                                                                                                                       throws SQLException;

    void resultSet_updateCharacterStream(ResultSetProxy resultSet, String columnLabel, java.io.Reader reader,
                                         long length) throws SQLException;

    void resultSet_updateBlob(ResultSetProxy resultSet, int columnIndex, InputStream inputStream, long length)
                                                                                                              throws SQLException;

    void resultSet_updateBlob(ResultSetProxy resultSet, String columnLabel, InputStream inputStream, long length)
                                                                                                                 throws SQLException;

    void resultSet_updateClob(ResultSetProxy resultSet, int columnIndex, Reader reader, long length)
                                                                                                    throws SQLException;

    void resultSet_updateClob(ResultSetProxy resultSet, String columnLabel, Reader reader, long length)
                                                                                                       throws SQLException;

    void resultSet_updateNClob(ResultSetProxy resultSet, int columnIndex, Reader reader, long length)
                                                                                                     throws SQLException;

    void resultSet_updateNClob(ResultSetProxy resultSet, String columnLabel, Reader reader, long length)
                                                                                                        throws SQLException;

    void resultSet_updateNCharacterStream(ResultSetProxy resultSet, int columnIndex, java.io.Reader x)
                                                                                                      throws SQLException;

    void resultSet_updateNCharacterStream(ResultSetProxy resultSet, String columnLabel, java.io.Reader reader)
                                                                                                              throws SQLException;

    void resultSet_updateAsciiStream(ResultSetProxy resultSet, int columnIndex, java.io.InputStream x)
                                                                                                      throws SQLException;

    void resultSet_updateBinaryStream(ResultSetProxy resultSet, int columnIndex, java.io.InputStream x)
                                                                                                       throws SQLException;

    void resultSet_updateCharacterStream(ResultSetProxy resultSet, int columnIndex, java.io.Reader x)
                                                                                                     throws SQLException;

    void resultSet_updateAsciiStream(ResultSetProxy resultSet, String columnLabel, java.io.InputStream x)
                                                                                                         throws SQLException;

    void resultSet_updateBinaryStream(ResultSetProxy resultSet, String columnLabel, java.io.InputStream x)
                                                                                                          throws SQLException;

    void resultSet_updateCharacterStream(ResultSetProxy resultSet, String columnLabel, java.io.Reader reader)
                                                                                                             throws SQLException;

    void resultSet_updateBlob(ResultSetProxy resultSet, int columnIndex, InputStream inputStream) throws SQLException;

    void resultSet_updateBlob(ResultSetProxy resultSet, String columnLabel, InputStream inputStream)
                                                                                                    throws SQLException;

    void resultSet_updateClob(ResultSetProxy resultSet, int columnIndex, Reader reader) throws SQLException;

    void resultSet_updateClob(ResultSetProxy resultSet, String columnLabel, Reader reader) throws SQLException;

    void resultSet_updateNClob(ResultSetProxy resultSet, int columnIndex, Reader reader) throws SQLException;

    void resultSet_updateNClob(ResultSetProxy resultSet, String columnLabel, Reader reader) throws SQLException;

    // ////////////////////////////////////

    ResultSetProxy statement_executeQuery(StatementProxy statement, String sql) throws SQLException;

    int statement_executeUpdate(StatementProxy statement, String sql) throws SQLException;

    void statement_close(StatementProxy statement) throws SQLException;

    int statement_getMaxFieldSize(StatementProxy statement) throws SQLException;

    void statement_setMaxFieldSize(StatementProxy statement, int max) throws SQLException;

    int statement_getMaxRows(StatementProxy statement) throws SQLException;

    void statement_setMaxRows(StatementProxy statement, int max) throws SQLException;

    void statement_setEscapeProcessing(StatementProxy statement, boolean enable) throws SQLException;

    int statement_getQueryTimeout(StatementProxy statement) throws SQLException;

    void statement_setQueryTimeout(StatementProxy statement, int seconds) throws SQLException;

    void statement_cancel(StatementProxy statement) throws SQLException;

    SQLWarning statement_getWarnings(StatementProxy statement) throws SQLException;

    void statement_clearWarnings(StatementProxy statement) throws SQLException;

    void statement_setCursorName(StatementProxy statement, String name) throws SQLException;

    boolean statement_execute(StatementProxy statement, String sql) throws SQLException;

    ResultSetProxy statement_getResultSet(StatementProxy statement) throws SQLException;

    int statement_getUpdateCount(StatementProxy statement) throws SQLException;

    boolean statement_getMoreResults(StatementProxy statement) throws SQLException;

    void statement_setFetchDirection(StatementProxy statement, int direction) throws SQLException;

    int statement_getFetchDirection(StatementProxy statement) throws SQLException;

    void statement_setFetchSize(StatementProxy statement, int rows) throws SQLException;

    int statement_getFetchSize(StatementProxy statement) throws SQLException;

    int statement_getResultSetConcurrency(StatementProxy statement) throws SQLException;

    int statement_getResultSetType(StatementProxy statement) throws SQLException;

    void statement_addBatch(StatementProxy statement, String sql) throws SQLException;

    void statement_clearBatch(StatementProxy statement) throws SQLException;

    int[] statement_executeBatch(StatementProxy statement) throws SQLException;

    Connection statement_getConnection(StatementProxy statement) throws SQLException;

    boolean statement_getMoreResults(StatementProxy statement, int current) throws SQLException;

    ResultSetProxy statement_getGeneratedKeys(StatementProxy statement) throws SQLException;

    int statement_executeUpdate(StatementProxy statement, String sql, int autoGeneratedKeys) throws SQLException;

    int statement_executeUpdate(StatementProxy statement, String sql, int columnIndexes[]) throws SQLException;

    int statement_executeUpdate(StatementProxy statement, String sql, String columnNames[]) throws SQLException;

    boolean statement_execute(StatementProxy statement, String sql, int autoGeneratedKeys) throws SQLException;

    boolean statement_execute(StatementProxy statement, String sql, int columnIndexes[]) throws SQLException;

    boolean statement_execute(StatementProxy statement, String sql, String columnNames[]) throws SQLException;

    int statement_getResultSetHoldability(StatementProxy statement) throws SQLException;

    boolean statement_isClosed(StatementProxy statement) throws SQLException;

    void statement_setPoolable(StatementProxy statement, boolean poolable) throws SQLException;

    boolean statement_isPoolable(StatementProxy statement) throws SQLException;

    // ////////////////////

    ResultSetProxy preparedStatement_executeQuery(PreparedStatementProxy statement) throws SQLException;

    int preparedStatement_executeUpdate(PreparedStatementProxy statement) throws SQLException;

    void preparedStatement_setNull(PreparedStatementProxy statement, int parameterIndex, int sqlType)
                                                                                                     throws SQLException;

    void preparedStatement_setBoolean(PreparedStatementProxy statement, int parameterIndex, boolean x)
                                                                                                      throws SQLException;

    void preparedStatement_setByte(PreparedStatementProxy statement, int parameterIndex, byte x) throws SQLException;

    void preparedStatement_setShort(PreparedStatementProxy statement, int parameterIndex, short x) throws SQLException;

    void preparedStatement_setInt(PreparedStatementProxy statement, int parameterIndex, int x) throws SQLException;

    void preparedStatement_setLong(PreparedStatementProxy statement, int parameterIndex, long x) throws SQLException;

    void preparedStatement_setFloat(PreparedStatementProxy statement, int parameterIndex, float x) throws SQLException;

    void preparedStatement_setDouble(PreparedStatementProxy statement, int parameterIndex, double x)
                                                                                                    throws SQLException;

    void preparedStatement_setBigDecimal(PreparedStatementProxy statement, int parameterIndex, BigDecimal x)
                                                                                                            throws SQLException;

    void preparedStatement_setString(PreparedStatementProxy statement, int parameterIndex, String x)
                                                                                                    throws SQLException;

    void preparedStatement_setBytes(PreparedStatementProxy statement, int parameterIndex, byte x[]) throws SQLException;

    void preparedStatement_setDate(PreparedStatementProxy statement, int parameterIndex, java.sql.Date x)
                                                                                                         throws SQLException;

    void preparedStatement_setTime(PreparedStatementProxy statement, int parameterIndex, java.sql.Time x)
                                                                                                         throws SQLException;

    void preparedStatement_setTimestamp(PreparedStatementProxy statement, int parameterIndex, java.sql.Timestamp x)
                                                                                                                   throws SQLException;

    void preparedStatement_setAsciiStream(PreparedStatementProxy statement, int parameterIndex, java.io.InputStream x,
                                          int length) throws SQLException;

    void preparedStatement_setUnicodeStream(PreparedStatementProxy statement, int parameterIndex,
                                            java.io.InputStream x, int length) throws SQLException;

    void preparedStatement_setBinaryStream(PreparedStatementProxy statement, int parameterIndex, java.io.InputStream x,
                                           int length) throws SQLException;

    void preparedStatement_clearParameters(PreparedStatementProxy statement) throws SQLException;

    void preparedStatement_setObject(PreparedStatementProxy statement, int parameterIndex, Object x, int targetSqlType)
                                                                                                                       throws SQLException;

    void preparedStatement_setObject(PreparedStatementProxy statement, int parameterIndex, Object x)
                                                                                                    throws SQLException;

    boolean preparedStatement_execute(PreparedStatementProxy statement) throws SQLException;

    void preparedStatement_addBatch(PreparedStatementProxy statement) throws SQLException;

    void preparedStatement_setCharacterStream(PreparedStatementProxy statement, int parameterIndex,
                                              java.io.Reader reader, int length) throws SQLException;

    void preparedStatement_setRef(PreparedStatementProxy statement, int parameterIndex, Ref x) throws SQLException;

    void preparedStatement_setBlob(PreparedStatementProxy statement, int parameterIndex, Blob x) throws SQLException;

    void preparedStatement_setClob(PreparedStatementProxy statement, int parameterIndex, Clob x) throws SQLException;

    void preparedStatement_setArray(PreparedStatementProxy statement, int parameterIndex, Array x) throws SQLException;

    ResultSetMetaData preparedStatement_getMetaData(PreparedStatementProxy statement) throws SQLException;

    void preparedStatement_setDate(PreparedStatementProxy statement, int parameterIndex, java.sql.Date x, Calendar cal)
                                                                                                                       throws SQLException;

    void preparedStatement_setTime(PreparedStatementProxy statement, int parameterIndex, java.sql.Time x, Calendar cal)
                                                                                                                       throws SQLException;

    void preparedStatement_setTimestamp(PreparedStatementProxy statement, int parameterIndex, java.sql.Timestamp x,
                                        Calendar cal) throws SQLException;

    void preparedStatement_setNull(PreparedStatementProxy statement, int parameterIndex, int sqlType, String typeName)
                                                                                                                      throws SQLException;

    void preparedStatement_setURL(PreparedStatementProxy statement, int parameterIndex, java.net.URL x)
                                                                                                       throws SQLException;

    ParameterMetaData preparedStatement_getParameterMetaData(PreparedStatementProxy statement) throws SQLException;

    void preparedStatement_setRowId(PreparedStatementProxy statement, int parameterIndex, RowId x) throws SQLException;

    void preparedStatement_setNString(PreparedStatementProxy statement, int parameterIndex, String value)
                                                                                                         throws SQLException;

    void preparedStatement_setNCharacterStream(PreparedStatementProxy statement, int parameterIndex, Reader value,
                                               long length) throws SQLException;

    void preparedStatement_setNClob(PreparedStatementProxy statement, int parameterIndex, NClob value)
                                                                                                      throws SQLException;

    void preparedStatement_setClob(PreparedStatementProxy statement, int parameterIndex, Reader reader, long length)
                                                                                                                    throws SQLException;

    void preparedStatement_setBlob(PreparedStatementProxy statement, int parameterIndex, InputStream inputStream,
                                   long length) throws SQLException;

    void preparedStatement_setNClob(PreparedStatementProxy statement, int parameterIndex, Reader reader, long length)
                                                                                                                     throws SQLException;

    void preparedStatement_setSQLXML(PreparedStatementProxy statement, int parameterIndex, SQLXML xmlObject)
                                                                                                            throws SQLException;

    void preparedStatement_setObject(PreparedStatementProxy statement, int parameterIndex, Object x, int targetSqlType,
                                     int scaleOrLength) throws SQLException;

    void preparedStatement_setAsciiStream(PreparedStatementProxy statement, int parameterIndex, java.io.InputStream x,
                                          long length) throws SQLException;

    void preparedStatement_setBinaryStream(PreparedStatementProxy statement, int parameterIndex, java.io.InputStream x,
                                           long length) throws SQLException;

    void preparedStatement_setCharacterStream(PreparedStatementProxy statement, int parameterIndex,
                                              java.io.Reader reader, long length) throws SQLException;

    void preparedStatement_setAsciiStream(PreparedStatementProxy statement, int parameterIndex, java.io.InputStream x)
                                                                                                                      throws SQLException;

    void preparedStatement_setBinaryStream(PreparedStatementProxy statement, int parameterIndex, java.io.InputStream x)
                                                                                                                       throws SQLException;

    void preparedStatement_setCharacterStream(PreparedStatementProxy statement, int parameterIndex,
                                              java.io.Reader reader) throws SQLException;

    void preparedStatement_setNCharacterStream(PreparedStatementProxy statement, int parameterIndex, Reader value)
                                                                                                                  throws SQLException;

    void preparedStatement_setClob(PreparedStatementProxy statement, int parameterIndex, Reader reader)
                                                                                                       throws SQLException;

    void preparedStatement_setBlob(PreparedStatementProxy statement, int parameterIndex, InputStream inputStream)
                                                                                                                 throws SQLException;

    void preparedStatement_setNClob(PreparedStatementProxy statement, int parameterIndex, Reader reader)
                                                                                                        throws SQLException;

    // /////////////////////////////

    void callableStatement_registerOutParameter(CallableStatementProxy statement, int parameterIndex, int sqlType)
                                                                                                                  throws SQLException;

    void callableStatement_registerOutParameter(CallableStatementProxy statement, int parameterIndex, int sqlType,
                                                int scale) throws SQLException;

    boolean callableStatement_wasNull(CallableStatementProxy statement) throws SQLException;

    String callableStatement_getString(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    boolean callableStatement_getBoolean(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    byte callableStatement_getByte(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    short callableStatement_getShort(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    int callableStatement_getInt(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    long callableStatement_getLong(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    float callableStatement_getFloat(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    double callableStatement_getDouble(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    BigDecimal callableStatement_getBigDecimal(CallableStatementProxy statement, int parameterIndex, int scale)
                                                                                                               throws SQLException;

    byte[] callableStatement_getBytes(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    java.sql.Date callableStatement_getDate(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    java.sql.Time callableStatement_getTime(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    java.sql.Timestamp callableStatement_getTimestamp(CallableStatementProxy statement, int parameterIndex)
                                                                                                           throws SQLException;

    Object callableStatement_getObject(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    BigDecimal callableStatement_getBigDecimal(CallableStatementProxy statement, int parameterIndex)
                                                                                                    throws SQLException;

    Object callableStatement_getObject(CallableStatementProxy statement, int parameterIndex,
                                       java.util.Map<String, Class<?>> map) throws SQLException;

    Ref callableStatement_getRef(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    Blob callableStatement_getBlob(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    Clob callableStatement_getClob(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    Array callableStatement_getArray(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    java.sql.Date callableStatement_getDate(CallableStatementProxy statement, int parameterIndex, Calendar cal)
                                                                                                               throws SQLException;

    java.sql.Time callableStatement_getTime(CallableStatementProxy statement, int parameterIndex, Calendar cal)
                                                                                                               throws SQLException;

    java.sql.Timestamp callableStatement_getTimestamp(CallableStatementProxy statement, int parameterIndex, Calendar cal)
                                                                                                                         throws SQLException;

    void callableStatement_registerOutParameter(CallableStatementProxy statement, int parameterIndex, int sqlType,
                                                String typeName) throws SQLException;

    void callableStatement_registerOutParameter(CallableStatementProxy statement, String parameterName, int sqlType)
                                                                                                                    throws SQLException;

    void callableStatement_registerOutParameter(CallableStatementProxy statement, String parameterName, int sqlType,
                                                int scale) throws SQLException;

    void callableStatement_registerOutParameter(CallableStatementProxy statement, String parameterName, int sqlType,
                                                String typeName) throws SQLException;

    java.net.URL callableStatement_getURL(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    void callableStatement_setURL(CallableStatementProxy statement, String parameterName, java.net.URL val)
                                                                                                           throws SQLException;

    void callableStatement_setNull(CallableStatementProxy statement, String parameterName, int sqlType)
                                                                                                       throws SQLException;

    void callableStatement_setBoolean(CallableStatementProxy statement, String parameterName, boolean x)
                                                                                                        throws SQLException;

    void callableStatement_setByte(CallableStatementProxy statement, String parameterName, byte x) throws SQLException;

    void callableStatement_setShort(CallableStatementProxy statement, String parameterName, short x)
                                                                                                    throws SQLException;

    void callableStatement_setInt(CallableStatementProxy statement, String parameterName, int x) throws SQLException;

    void callableStatement_setLong(CallableStatementProxy statement, String parameterName, long x) throws SQLException;

    void callableStatement_setFloat(CallableStatementProxy statement, String parameterName, float x)
                                                                                                    throws SQLException;

    void callableStatement_setDouble(CallableStatementProxy statement, String parameterName, double x)
                                                                                                      throws SQLException;

    void callableStatement_setBigDecimal(CallableStatementProxy statement, String parameterName, BigDecimal x)
                                                                                                              throws SQLException;

    void callableStatement_setString(CallableStatementProxy statement, String parameterName, String x)
                                                                                                      throws SQLException;

    void callableStatement_setBytes(CallableStatementProxy statement, String parameterName, byte x[])
                                                                                                     throws SQLException;

    void callableStatement_setDate(CallableStatementProxy statement, String parameterName, java.sql.Date x)
                                                                                                           throws SQLException;

    void callableStatement_setTime(CallableStatementProxy statement, String parameterName, java.sql.Time x)
                                                                                                           throws SQLException;

    void callableStatement_setTimestamp(CallableStatementProxy statement, String parameterName, java.sql.Timestamp x)
                                                                                                                     throws SQLException;

    void callableStatement_setAsciiStream(CallableStatementProxy statement, String parameterName,
                                          java.io.InputStream x, int length) throws SQLException;

    void callableStatement_setBinaryStream(CallableStatementProxy statement, String parameterName,
                                           java.io.InputStream x, int length) throws SQLException;

    void callableStatement_setObject(CallableStatementProxy statement, String parameterName, Object x,
                                     int targetSqlType, int scale) throws SQLException;

    void callableStatement_setObject(CallableStatementProxy statement, String parameterName, Object x, int targetSqlType)
                                                                                                                         throws SQLException;

    void callableStatement_setObject(CallableStatementProxy statement, String parameterName, Object x)
                                                                                                      throws SQLException;

    void callableStatement_setCharacterStream(CallableStatementProxy statement, String parameterName,
                                              java.io.Reader reader, int length) throws SQLException;

    void callableStatement_setDate(CallableStatementProxy statement, String parameterName, java.sql.Date x, Calendar cal)
                                                                                                                         throws SQLException;

    void callableStatement_setTime(CallableStatementProxy statement, String parameterName, java.sql.Time x, Calendar cal)
                                                                                                                         throws SQLException;

    void callableStatement_setTimestamp(CallableStatementProxy statement, String parameterName, java.sql.Timestamp x,
                                        Calendar cal) throws SQLException;

    void callableStatement_setNull(CallableStatementProxy statement, String parameterName, int sqlType, String typeName)
                                                                                                                        throws SQLException;

    String callableStatement_getString(CallableStatementProxy statement, String parameterName) throws SQLException;

    boolean callableStatement_getBoolean(CallableStatementProxy statement, String parameterName) throws SQLException;

    byte callableStatement_getByte(CallableStatementProxy statement, String parameterName) throws SQLException;

    short callableStatement_getShort(CallableStatementProxy statement, String parameterName) throws SQLException;

    int callableStatement_getInt(CallableStatementProxy statement, String parameterName) throws SQLException;

    long callableStatement_getLong(CallableStatementProxy statement, String parameterName) throws SQLException;

    float callableStatement_getFloat(CallableStatementProxy statement, String parameterName) throws SQLException;

    double callableStatement_getDouble(CallableStatementProxy statement, String parameterName) throws SQLException;

    byte[] callableStatement_getBytes(CallableStatementProxy statement, String parameterName) throws SQLException;

    java.sql.Date callableStatement_getDate(CallableStatementProxy statement, String parameterName) throws SQLException;

    java.sql.Time callableStatement_getTime(CallableStatementProxy statement, String parameterName) throws SQLException;

    java.sql.Timestamp callableStatement_getTimestamp(CallableStatementProxy statement, String parameterName)
                                                                                                             throws SQLException;

    Object callableStatement_getObject(CallableStatementProxy statement, String parameterName) throws SQLException;

    BigDecimal callableStatement_getBigDecimal(CallableStatementProxy statement, String parameterName)
                                                                                                      throws SQLException;

    Object callableStatement_getObject(CallableStatementProxy statement, String parameterName,
                                       java.util.Map<String, Class<?>> map) throws SQLException;

    Ref callableStatement_getRef(CallableStatementProxy statement, String parameterName) throws SQLException;

    Blob callableStatement_getBlob(CallableStatementProxy statement, String parameterName) throws SQLException;

    Clob callableStatement_getClob(CallableStatementProxy statement, String parameterName) throws SQLException;

    Array callableStatement_getArray(CallableStatementProxy statement, String parameterName) throws SQLException;

    java.sql.Date callableStatement_getDate(CallableStatementProxy statement, String parameterName, Calendar cal)
                                                                                                                 throws SQLException;

    java.sql.Time callableStatement_getTime(CallableStatementProxy statement, String parameterName, Calendar cal)
                                                                                                                 throws SQLException;

    java.sql.Timestamp callableStatement_getTimestamp(CallableStatementProxy statement, String parameterName,
                                                      Calendar cal) throws SQLException;

    java.net.URL callableStatement_getURL(CallableStatementProxy statement, String parameterName) throws SQLException;

    RowId callableStatement_getRowId(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    RowId callableStatement_getRowId(CallableStatementProxy statement, String parameterName) throws SQLException;

    void callableStatement_setRowId(CallableStatementProxy statement, String parameterName, RowId x)
                                                                                                    throws SQLException;

    void callableStatement_setNString(CallableStatementProxy statement, String parameterName, String value)
                                                                                                           throws SQLException;

    void callableStatement_setNCharacterStream(CallableStatementProxy statement, String parameterName, Reader value,
                                               long length) throws SQLException;

    void callableStatement_setNClob(CallableStatementProxy statement, String parameterName, NClob value)
                                                                                                        throws SQLException;

    void callableStatement_setClob(CallableStatementProxy statement, String parameterName, Reader reader, long length)
                                                                                                                      throws SQLException;

    void callableStatement_setBlob(CallableStatementProxy statement, String parameterName, InputStream inputStream,
                                   long length) throws SQLException;

    void callableStatement_setNClob(CallableStatementProxy statement, String parameterName, Reader reader, long length)
                                                                                                                       throws SQLException;

    NClob callableStatement_getNClob(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    NClob callableStatement_getNClob(CallableStatementProxy statement, String parameterName) throws SQLException;

    void callableStatement_setSQLXML(CallableStatementProxy statement, String parameterName, SQLXML xmlObject)
                                                                                                              throws SQLException;

    SQLXML callableStatement_getSQLXML(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    SQLXML callableStatement_getSQLXML(CallableStatementProxy statement, String parameterName) throws SQLException;

    String callableStatement_getNString(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    String callableStatement_getNString(CallableStatementProxy statement, String parameterName) throws SQLException;

    java.io.Reader callableStatement_getNCharacterStream(CallableStatementProxy statement, int parameterIndex)
                                                                                                              throws SQLException;

    java.io.Reader callableStatement_getNCharacterStream(CallableStatementProxy statement, String parameterName)
                                                                                                                throws SQLException;

    java.io.Reader callableStatement_getCharacterStream(CallableStatementProxy statement, int parameterIndex)
                                                                                                             throws SQLException;

    java.io.Reader callableStatement_getCharacterStream(CallableStatementProxy statement, String parameterName)
                                                                                                               throws SQLException;

    void callableStatement_setBlob(CallableStatementProxy statement, String parameterName, Blob x) throws SQLException;

    void callableStatement_setClob(CallableStatementProxy statement, String parameterName, Clob x) throws SQLException;

    void callableStatement_setAsciiStream(CallableStatementProxy statement, String parameterName,
                                          java.io.InputStream x, long length) throws SQLException;

    void callableStatement_setBinaryStream(CallableStatementProxy statement, String parameterName,
                                           java.io.InputStream x, long length) throws SQLException;

    void callableStatement_setCharacterStream(CallableStatementProxy statement, String parameterName,
                                              java.io.Reader reader, long length) throws SQLException;

    void callableStatement_setAsciiStream(CallableStatementProxy statement, String parameterName, java.io.InputStream x)
                                                                                                                        throws SQLException;

    void callableStatement_setBinaryStream(CallableStatementProxy statement, String parameterName, java.io.InputStream x)
                                                                                                                         throws SQLException;

    void callableStatement_setCharacterStream(CallableStatementProxy statement, String parameterName,
                                              java.io.Reader reader) throws SQLException;

    void callableStatement_setNCharacterStream(CallableStatementProxy statement, String parameterName, Reader value)
                                                                                                                    throws SQLException;

    void callableStatement_setClob(CallableStatementProxy statement, String parameterName, Reader reader)
                                                                                                         throws SQLException;

    void callableStatement_setBlob(CallableStatementProxy statement, String parameterName, InputStream inputStream)
                                                                                                                   throws SQLException;

    void callableStatement_setNClob(CallableStatementProxy statement, String parameterName, Reader reader)
                                                                                                          throws SQLException;

    void clob_free(ClobProxy wrapper) throws SQLException;

    InputStream clob_getAsciiStream(ClobProxy wrapper) throws SQLException;

    Reader clob_getCharacterStream(ClobProxy wrapper) throws SQLException;

    Reader clob_getCharacterStream(ClobProxy wrapper, long pos, long length) throws SQLException;

    String clob_getSubString(ClobProxy wrapper, long pos, int length) throws SQLException;

    long clob_length(ClobProxy wrapper) throws SQLException;

    long clob_position(ClobProxy wrapper, String searchstr, long start) throws SQLException;

    long clob_position(ClobProxy wrapper, Clob searchstr, long start) throws SQLException;

    OutputStream clob_setAsciiStream(ClobProxy wrapper, long pos) throws SQLException;

    Writer clob_setCharacterStream(ClobProxy wrapper, long pos) throws SQLException;

    int clob_setString(ClobProxy wrapper, long pos, String str) throws SQLException;

    int clob_setString(ClobProxy wrapper, long pos, String str, int offset, int len) throws SQLException;

    void clob_truncate(ClobProxy wrapper, long len) throws SQLException;

    // ////

    void dataSource_recycle(DruidPooledConnection connection) throws SQLException;

    DruidPooledConnection dataSource_connect(DruidDataSource dataSource, long maxWaitMillis) throws SQLException;

    // //////////
    int resultSetMetaData_getColumnCount(ResultSetMetaDataProxy metaData) throws SQLException;

    boolean resultSetMetaData_isAutoIncrement(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    boolean resultSetMetaData_isCaseSensitive(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    boolean resultSetMetaData_isSearchable(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    boolean resultSetMetaData_isCurrency(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    int resultSetMetaData_isNullable(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    boolean resultSetMetaData_isSigned(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    int resultSetMetaData_getColumnDisplaySize(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    String resultSetMetaData_getColumnLabel(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    String resultSetMetaData_getColumnName(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    String resultSetMetaData_getSchemaName(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    int resultSetMetaData_getPrecision(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    int resultSetMetaData_getScale(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    String resultSetMetaData_getTableName(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    String resultSetMetaData_getCatalogName(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    int resultSetMetaData_getColumnType(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    String resultSetMetaData_getColumnTypeName(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    boolean resultSetMetaData_isReadOnly(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    boolean resultSetMetaData_isWritable(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    boolean resultSetMetaData_isDefinitelyWritable(ResultSetMetaDataProxy metaData, int column) throws SQLException;

    String resultSetMetaData_getColumnClassName(ResultSetMetaDataProxy metaData, int column) throws SQLException;
}
