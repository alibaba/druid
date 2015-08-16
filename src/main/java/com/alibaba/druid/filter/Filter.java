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
import java.sql.Date;
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
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Wrapper;
import java.util.Calendar;
import java.util.Properties;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public interface Filter extends Wrapper {

    void init(DataSourceProxy dataSource);

    void destroy();

    void configFromProperties(Properties properties);

    boolean isWrapperFor(java.lang.Class<?> iface);

    <T> T unwrap(java.lang.Class<T> iface);

    ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException;

    StatementProxy connection_createStatement(FilterChain chain, ConnectionProxy connection) throws SQLException;

    PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection, String sql)
                                                                                                                 throws SQLException;

    CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql)
                                                                                                            throws SQLException;

    String connection_nativeSQL(FilterChain chain, ConnectionProxy connection, String sql) throws SQLException;

    void connection_setAutoCommit(FilterChain chain, ConnectionProxy connection, boolean autoCommit)
                                                                                                    throws SQLException;

    boolean connection_getAutoCommit(FilterChain chain, ConnectionProxy connection) throws SQLException;

    void connection_commit(FilterChain chain, ConnectionProxy connection) throws SQLException;

    void connection_rollback(FilterChain chain, ConnectionProxy connection) throws SQLException;

    void connection_close(FilterChain chain, ConnectionProxy connection) throws SQLException;

    boolean connection_isClosed(FilterChain chain, ConnectionProxy connection) throws SQLException;

    DatabaseMetaData connection_getMetaData(FilterChain chain, ConnectionProxy connection) throws SQLException;

    void connection_setReadOnly(FilterChain chain, ConnectionProxy connection, boolean readOnly) throws SQLException;

    boolean connection_isReadOnly(FilterChain chain, ConnectionProxy connection) throws SQLException;

    void connection_setCatalog(FilterChain chain, ConnectionProxy connection, String catalog) throws SQLException;

    String connection_getCatalog(FilterChain chain, ConnectionProxy connection) throws SQLException;

    void connection_setTransactionIsolation(FilterChain chain, ConnectionProxy connection, int level)
                                                                                                     throws SQLException;

    int connection_getTransactionIsolation(FilterChain chain, ConnectionProxy connection) throws SQLException;

    SQLWarning connection_getWarnings(FilterChain chain, ConnectionProxy connection) throws SQLException;

    void connection_clearWarnings(FilterChain chain, ConnectionProxy connection) throws SQLException;

    StatementProxy connection_createStatement(FilterChain chain, ConnectionProxy connection, int resultSetType,
                                              int resultSetConcurrency) throws SQLException;

    PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection, String sql,
                                                       int resultSetType, int resultSetConcurrency) throws SQLException;

    CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql,
                                                  int resultSetType, int resultSetConcurrency) throws SQLException;

    java.util.Map<String, Class<?>> connection_getTypeMap(FilterChain chain, ConnectionProxy connection)
                                                                                                        throws SQLException;

    void connection_setTypeMap(FilterChain chain, ConnectionProxy connection, java.util.Map<String, Class<?>> map)
                                                                                                                  throws SQLException;

    void connection_setHoldability(FilterChain chain, ConnectionProxy connection, int holdability) throws SQLException;

    int connection_getHoldability(FilterChain chain, ConnectionProxy connection) throws SQLException;

    Savepoint connection_setSavepoint(FilterChain chain, ConnectionProxy connection) throws SQLException;

    Savepoint connection_setSavepoint(FilterChain chain, ConnectionProxy connection, String name) throws SQLException;

    void connection_rollback(FilterChain chain, ConnectionProxy connection, Savepoint savepoint) throws SQLException;

    void connection_releaseSavepoint(FilterChain chain, ConnectionProxy connection, Savepoint savepoint)
                                                                                                        throws SQLException;

    StatementProxy connection_createStatement(FilterChain chain, ConnectionProxy connection, int resultSetType,
                                              int resultSetConcurrency, int resultSetHoldability) throws SQLException;

    PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection, String sql,
                                                       int resultSetType, int resultSetConcurrency,
                                                       int resultSetHoldability) throws SQLException;

    CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql,
                                                  int resultSetType, int resultSetConcurrency, int resultSetHoldability)
                                                                                                                        throws SQLException;

    PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection, String sql,
                                                       int autoGeneratedKeys) throws SQLException;

    PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection, String sql,
                                                       int columnIndexes[]) throws SQLException;

    PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection, String sql,
                                                       String columnNames[]) throws SQLException;

    Clob connection_createClob(FilterChain chain, ConnectionProxy connection) throws SQLException;

    Blob connection_createBlob(FilterChain chain, ConnectionProxy connection) throws SQLException;

    NClob connection_createNClob(FilterChain chain, ConnectionProxy connection) throws SQLException;

    SQLXML connection_createSQLXML(FilterChain chain, ConnectionProxy connection) throws SQLException;

    boolean connection_isValid(FilterChain chain, ConnectionProxy connection, int timeout) throws SQLException;

    void connection_setClientInfo(FilterChain chain, ConnectionProxy connection, String name, String value)
                                                                                                           throws SQLClientInfoException;

    void connection_setClientInfo(FilterChain chain, ConnectionProxy connection, Properties properties)
                                                                                                       throws SQLClientInfoException;

    String connection_getClientInfo(FilterChain chain, ConnectionProxy connection, String name) throws SQLException;

    Properties connection_getClientInfo(FilterChain chain, ConnectionProxy connection) throws SQLException;

    Array connection_createArrayOf(FilterChain chain, ConnectionProxy connection, String typeName, Object[] elements)
                                                                                                                     throws SQLException;

    Struct connection_createStruct(FilterChain chain, ConnectionProxy connection, String typeName, Object[] attributes)
                                                                                                                       throws SQLException;

    // ///////////////
    boolean resultSet_next(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    void resultSet_close(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_wasNull(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    String resultSet_getString(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    boolean resultSet_getBoolean(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    byte resultSet_getByte(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    short resultSet_getShort(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    int resultSet_getInt(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    long resultSet_getLong(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    float resultSet_getFloat(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    double resultSet_getDouble(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    BigDecimal resultSet_getBigDecimal(FilterChain chain, ResultSetProxy resultSet, int columnIndex, int scale)
                                                                                                               throws SQLException;

    byte[] resultSet_getBytes(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    Date resultSet_getDate(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    Time resultSet_getTime(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    Timestamp resultSet_getTimestamp(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    java.io.InputStream resultSet_getAsciiStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex)
                                                                                                              throws SQLException;

    java.io.InputStream resultSet_getUnicodeStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex)
                                                                                                                throws SQLException;

    java.io.InputStream resultSet_getBinaryStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex)
                                                                                                               throws SQLException;

    String resultSet_getString(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    boolean resultSet_getBoolean(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    byte resultSet_getByte(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    short resultSet_getShort(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    int resultSet_getInt(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    long resultSet_getLong(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    float resultSet_getFloat(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    double resultSet_getDouble(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    BigDecimal resultSet_getBigDecimal(FilterChain chain, ResultSetProxy resultSet, String columnLabel, int scale)
                                                                                                                  throws SQLException;

    byte[] resultSet_getBytes(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    java.sql.Date resultSet_getDate(FilterChain chain, ResultSetProxy resultSet, String columnLabel)
                                                                                                    throws SQLException;

    java.sql.Time resultSet_getTime(FilterChain chain, ResultSetProxy resultSet, String columnLabel)
                                                                                                    throws SQLException;

    java.sql.Timestamp resultSet_getTimestamp(FilterChain chain, ResultSetProxy resultSet, String columnLabel)
                                                                                                              throws SQLException;

    java.io.InputStream resultSet_getAsciiStream(FilterChain chain, ResultSetProxy resultSet, String columnLabel)
                                                                                                                 throws SQLException;

    java.io.InputStream resultSet_getUnicodeStream(FilterChain chain, ResultSetProxy resultSet, String columnLabel)
                                                                                                                   throws SQLException;

    java.io.InputStream resultSet_getBinaryStream(FilterChain chain, ResultSetProxy resultSet, String columnLabel)
                                                                                                                  throws SQLException;

    SQLWarning resultSet_getWarnings(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    void resultSet_clearWarnings(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    String resultSet_getCursorName(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    ResultSetMetaData resultSet_getMetaData(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    Object resultSet_getObject(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    Object resultSet_getObject(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    int resultSet_findColumn(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    java.io.Reader resultSet_getCharacterStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex)
                                                                                                             throws SQLException;

    java.io.Reader resultSet_getCharacterStream(FilterChain chain, ResultSetProxy resultSet, String columnLabel)
                                                                                                                throws SQLException;

    BigDecimal resultSet_getBigDecimal(FilterChain chain, ResultSetProxy resultSet, int columnIndex)
                                                                                                    throws SQLException;

    BigDecimal resultSet_getBigDecimal(FilterChain chain, ResultSetProxy resultSet, String columnLabel)
                                                                                                       throws SQLException;

    boolean resultSet_isBeforeFirst(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_isAfterLast(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_isFirst(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_isLast(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    void resultSet_beforeFirst(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    void resultSet_afterLast(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_first(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_last(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    int resultSet_getRow(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_absolute(FilterChain chain, ResultSetProxy resultSet, int row) throws SQLException;

    boolean resultSet_relative(FilterChain chain, ResultSetProxy resultSet, int rows) throws SQLException;

    boolean resultSet_previous(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    void resultSet_setFetchDirection(FilterChain chain, ResultSetProxy resultSet, int direction) throws SQLException;

    int resultSet_getFetchDirection(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    void resultSet_setFetchSize(FilterChain chain, ResultSetProxy resultSet, int rows) throws SQLException;

    int resultSet_getFetchSize(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    int resultSet_getType(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    int resultSet_getConcurrency(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_rowUpdated(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_rowInserted(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_rowDeleted(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    void resultSet_updateNull(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    void resultSet_updateBoolean(FilterChain chain, ResultSetProxy resultSet, int columnIndex, boolean x)
                                                                                                         throws SQLException;

    void resultSet_updateByte(FilterChain chain, ResultSetProxy resultSet, int columnIndex, byte x) throws SQLException;

    void resultSet_updateShort(FilterChain chain, ResultSetProxy resultSet, int columnIndex, short x)
                                                                                                     throws SQLException;

    void resultSet_updateInt(FilterChain chain, ResultSetProxy resultSet, int columnIndex, int x) throws SQLException;

    void resultSet_updateLong(FilterChain chain, ResultSetProxy resultSet, int columnIndex, long x) throws SQLException;

    void resultSet_updateFloat(FilterChain chain, ResultSetProxy resultSet, int columnIndex, float x)
                                                                                                     throws SQLException;

    void resultSet_updateDouble(FilterChain chain, ResultSetProxy resultSet, int columnIndex, double x)
                                                                                                       throws SQLException;

    void resultSet_updateBigDecimal(FilterChain chain, ResultSetProxy resultSet, int columnIndex, BigDecimal x)
                                                                                                               throws SQLException;

    void resultSet_updateString(FilterChain chain, ResultSetProxy resultSet, int columnIndex, String x)
                                                                                                       throws SQLException;

    void resultSet_updateBytes(FilterChain chain, ResultSetProxy resultSet, int columnIndex, byte x[])
                                                                                                      throws SQLException;

    void resultSet_updateDate(FilterChain chain, ResultSetProxy resultSet, int columnIndex, java.sql.Date x)
                                                                                                            throws SQLException;

    void resultSet_updateTime(FilterChain chain, ResultSetProxy resultSet, int columnIndex, java.sql.Time x)
                                                                                                            throws SQLException;

    void resultSet_updateTimestamp(FilterChain chain, ResultSetProxy resultSet, int columnIndex, java.sql.Timestamp x)
                                                                                                                      throws SQLException;

    void resultSet_updateAsciiStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                     java.io.InputStream x, int length) throws SQLException;

    void resultSet_updateBinaryStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                      java.io.InputStream x, int length) throws SQLException;

    void resultSet_updateCharacterStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                         java.io.Reader x, int length) throws SQLException;

    void resultSet_updateObject(FilterChain chain, ResultSetProxy resultSet, int columnIndex, Object x,
                                int scaleOrLength) throws SQLException;

    void resultSet_updateObject(FilterChain chain, ResultSetProxy resultSet, int columnIndex, Object x)
                                                                                                       throws SQLException;

    void resultSet_updateNull(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    void resultSet_updateBoolean(FilterChain chain, ResultSetProxy resultSet, String columnLabel, boolean x)
                                                                                                            throws SQLException;

    void resultSet_updateByte(FilterChain chain, ResultSetProxy resultSet, String columnLabel, byte x)
                                                                                                      throws SQLException;

    void resultSet_updateShort(FilterChain chain, ResultSetProxy resultSet, String columnLabel, short x)
                                                                                                        throws SQLException;

    void resultSet_updateInt(FilterChain chain, ResultSetProxy resultSet, String columnLabel, int x)
                                                                                                    throws SQLException;

    void resultSet_updateLong(FilterChain chain, ResultSetProxy resultSet, String columnLabel, long x)
                                                                                                      throws SQLException;

    void resultSet_updateFloat(FilterChain chain, ResultSetProxy resultSet, String columnLabel, float x)
                                                                                                        throws SQLException;

    void resultSet_updateDouble(FilterChain chain, ResultSetProxy resultSet, String columnLabel, double x)
                                                                                                          throws SQLException;

    void resultSet_updateBigDecimal(FilterChain chain, ResultSetProxy resultSet, String columnLabel, BigDecimal x)
                                                                                                                  throws SQLException;

    void resultSet_updateString(FilterChain chain, ResultSetProxy resultSet, String columnLabel, String x)
                                                                                                          throws SQLException;

    void resultSet_updateBytes(FilterChain chain, ResultSetProxy resultSet, String columnLabel, byte x[])
                                                                                                         throws SQLException;

    void resultSet_updateDate(FilterChain chain, ResultSetProxy resultSet, String columnLabel, java.sql.Date x)
                                                                                                               throws SQLException;

    void resultSet_updateTime(FilterChain chain, ResultSetProxy resultSet, String columnLabel, java.sql.Time x)
                                                                                                               throws SQLException;

    void resultSet_updateTimestamp(FilterChain chain, ResultSetProxy resultSet, String columnLabel, java.sql.Timestamp x)
                                                                                                                         throws SQLException;

    void resultSet_updateAsciiStream(FilterChain chain, ResultSetProxy resultSet, String columnLabel,
                                     java.io.InputStream x, int length) throws SQLException;

    void resultSet_updateBinaryStream(FilterChain chain, ResultSetProxy resultSet, String columnLabel,
                                      java.io.InputStream x, int length) throws SQLException;

    void resultSet_updateCharacterStream(FilterChain chain, ResultSetProxy resultSet, String columnLabel,
                                         java.io.Reader reader, int length) throws SQLException;

    void resultSet_updateObject(FilterChain chain, ResultSetProxy resultSet, String columnLabel, Object x,
                                int scaleOrLength) throws SQLException;

    void resultSet_updateObject(FilterChain chain, ResultSetProxy resultSet, String columnLabel, Object x)
                                                                                                          throws SQLException;

    void resultSet_insertRow(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    void resultSet_updateRow(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    void resultSet_deleteRow(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    void resultSet_refreshRow(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    void resultSet_cancelRowUpdates(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    void resultSet_moveToInsertRow(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    void resultSet_moveToCurrentRow(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    Statement resultSet_getStatement(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    Object resultSet_getObject(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                               java.util.Map<String, Class<?>> map) throws SQLException;

    Ref resultSet_getRef(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    Blob resultSet_getBlob(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    Clob resultSet_getClob(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    Array resultSet_getArray(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    Object resultSet_getObject(FilterChain chain, ResultSetProxy resultSet, String columnLabel,
                               java.util.Map<String, Class<?>> map) throws SQLException;

    Ref resultSet_getRef(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    Blob resultSet_getBlob(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    Clob resultSet_getClob(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    Array resultSet_getArray(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    java.sql.Date resultSet_getDate(FilterChain chain, ResultSetProxy resultSet, int columnIndex, Calendar cal)
                                                                                                               throws SQLException;

    java.sql.Date resultSet_getDate(FilterChain chain, ResultSetProxy resultSet, String columnLabel, Calendar cal)
                                                                                                                  throws SQLException;

    java.sql.Time resultSet_getTime(FilterChain chain, ResultSetProxy resultSet, int columnIndex, Calendar cal)
                                                                                                               throws SQLException;

    java.sql.Time resultSet_getTime(FilterChain chain, ResultSetProxy resultSet, String columnLabel, Calendar cal)
                                                                                                                  throws SQLException;

    java.sql.Timestamp resultSet_getTimestamp(FilterChain chain, ResultSetProxy resultSet, int columnIndex, Calendar cal)
                                                                                                                         throws SQLException;

    java.sql.Timestamp resultSet_getTimestamp(FilterChain chain, ResultSetProxy resultSet, String columnLabel,
                                              Calendar cal) throws SQLException;

    java.net.URL resultSet_getURL(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    java.net.URL resultSet_getURL(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    void resultSet_updateRef(FilterChain chain, ResultSetProxy resultSet, int columnIndex, java.sql.Ref x)
                                                                                                          throws SQLException;

    void resultSet_updateRef(FilterChain chain, ResultSetProxy resultSet, String columnLabel, java.sql.Ref x)
                                                                                                             throws SQLException;

    void resultSet_updateBlob(FilterChain chain, ResultSetProxy resultSet, int columnIndex, java.sql.Blob x)
                                                                                                            throws SQLException;

    void resultSet_updateBlob(FilterChain chain, ResultSetProxy resultSet, String columnLabel, java.sql.Blob x)
                                                                                                               throws SQLException;

    void resultSet_updateClob(FilterChain chain, ResultSetProxy resultSet, int columnIndex, java.sql.Clob x)
                                                                                                            throws SQLException;

    void resultSet_updateClob(FilterChain chain, ResultSetProxy resultSet, String columnLabel, java.sql.Clob x)
                                                                                                               throws SQLException;

    void resultSet_updateArray(FilterChain chain, ResultSetProxy resultSet, int columnIndex, java.sql.Array x)
                                                                                                              throws SQLException;

    void resultSet_updateArray(FilterChain chain, ResultSetProxy resultSet, String columnLabel, java.sql.Array x)
                                                                                                                 throws SQLException;

    RowId resultSet_getRowId(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    RowId resultSet_getRowId(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    void resultSet_updateRowId(FilterChain chain, ResultSetProxy resultSet, int columnIndex, RowId x)
                                                                                                     throws SQLException;

    void resultSet_updateRowId(FilterChain chain, ResultSetProxy resultSet, String columnLabel, RowId x)
                                                                                                        throws SQLException;

    int resultSet_getHoldability(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    boolean resultSet_isClosed(FilterChain chain, ResultSetProxy resultSet) throws SQLException;

    void resultSet_updateNString(FilterChain chain, ResultSetProxy resultSet, int columnIndex, String nString)
                                                                                                              throws SQLException;

    void resultSet_updateNString(FilterChain chain, ResultSetProxy resultSet, String columnLabel, String nString)
                                                                                                                 throws SQLException;

    void resultSet_updateNClob(FilterChain chain, ResultSetProxy resultSet, int columnIndex, NClob nClob)
                                                                                                         throws SQLException;

    void resultSet_updateNClob(FilterChain chain, ResultSetProxy resultSet, String columnLabel, NClob nClob)
                                                                                                            throws SQLException;

    NClob resultSet_getNClob(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    NClob resultSet_getNClob(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    SQLXML resultSet_getSQLXML(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    SQLXML resultSet_getSQLXML(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    void resultSet_updateSQLXML(FilterChain chain, ResultSetProxy resultSet, int columnIndex, SQLXML xmlObject)
                                                                                                               throws SQLException;

    void resultSet_updateSQLXML(FilterChain chain, ResultSetProxy resultSet, String columnLabel, SQLXML xmlObject)
                                                                                                                  throws SQLException;

    String resultSet_getNString(FilterChain chain, ResultSetProxy resultSet, int columnIndex) throws SQLException;

    String resultSet_getNString(FilterChain chain, ResultSetProxy resultSet, String columnLabel) throws SQLException;

    java.io.Reader resultSet_getNCharacterStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex)
                                                                                                              throws SQLException;

    java.io.Reader resultSet_getNCharacterStream(FilterChain chain, ResultSetProxy resultSet, String columnLabel)
                                                                                                                 throws SQLException;

    void resultSet_updateNCharacterStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                          java.io.Reader x, long length) throws SQLException;

    void resultSet_updateNCharacterStream(FilterChain chain, ResultSetProxy resultSet, String columnLabel,
                                          java.io.Reader reader, long length) throws SQLException;

    void resultSet_updateAsciiStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                     java.io.InputStream x, long length) throws SQLException;

    void resultSet_updateBinaryStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                      java.io.InputStream x, long length) throws SQLException;

    void resultSet_updateCharacterStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                         java.io.Reader x, long length) throws SQLException;

    void resultSet_updateAsciiStream(FilterChain chain, ResultSetProxy resultSet, String columnLabel,
                                     java.io.InputStream x, long length) throws SQLException;

    void resultSet_updateBinaryStream(FilterChain chain, ResultSetProxy resultSet, String columnLabel,
                                      java.io.InputStream x, long length) throws SQLException;

    void resultSet_updateCharacterStream(FilterChain chain, ResultSetProxy resultSet, String columnLabel,
                                         Reader reader, long length) throws SQLException;

    void resultSet_updateBlob(FilterChain chain, ResultSetProxy resultSet, int columnIndex, InputStream inputStream,
                              long length) throws SQLException;

    void resultSet_updateBlob(FilterChain chain, ResultSetProxy resultSet, String columnLabel, InputStream inputStream,
                              long length) throws SQLException;

    void resultSet_updateClob(FilterChain chain, ResultSetProxy resultSet, int columnIndex, Reader reader, long length)
                                                                                                                       throws SQLException;

    void resultSet_updateClob(FilterChain chain, ResultSetProxy resultSet, String columnLabel, Reader reader,
                              long length) throws SQLException;

    void resultSet_updateNClob(FilterChain chain, ResultSetProxy resultSet, int columnIndex, Reader reader, long length)
                                                                                                                        throws SQLException;

    void resultSet_updateNClob(FilterChain chain, ResultSetProxy resultSet, String columnLabel, Reader reader,
                               long length) throws SQLException;

    void resultSet_updateNCharacterStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex, java.io.Reader x)
                                                                                                                         throws SQLException;

    void resultSet_updateNCharacterStream(FilterChain chain, ResultSetProxy resultSet, String columnLabel,
                                          java.io.Reader reader) throws SQLException;

    void resultSet_updateAsciiStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex, java.io.InputStream x)
                                                                                                                         throws SQLException;

    void resultSet_updateBinaryStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex,
                                      java.io.InputStream x) throws SQLException;

    void resultSet_updateCharacterStream(FilterChain chain, ResultSetProxy resultSet, int columnIndex, java.io.Reader x)
                                                                                                                        throws SQLException;

    void resultSet_updateAsciiStream(FilterChain chain, ResultSetProxy resultSet, String columnLabel,
                                     java.io.InputStream x) throws SQLException;

    void resultSet_updateBinaryStream(FilterChain chain, ResultSetProxy resultSet, String columnLabel,
                                      java.io.InputStream x) throws SQLException;

    void resultSet_updateCharacterStream(FilterChain chain, ResultSetProxy resultSet, String columnLabel,
                                         java.io.Reader reader) throws SQLException;

    void resultSet_updateBlob(FilterChain chain, ResultSetProxy resultSet, int columnIndex, InputStream inputStream)
                                                                                                                    throws SQLException;

    void resultSet_updateBlob(FilterChain chain, ResultSetProxy resultSet, String columnLabel, InputStream inputStream)
                                                                                                                       throws SQLException;

    void resultSet_updateClob(FilterChain chain, ResultSetProxy resultSet, int columnIndex, Reader reader)
                                                                                                          throws SQLException;

    void resultSet_updateClob(FilterChain chain, ResultSetProxy resultSet, String columnLabel, Reader reader)
                                                                                                             throws SQLException;

    void resultSet_updateNClob(FilterChain chain, ResultSetProxy resultSet, int columnIndex, Reader reader)
                                                                                                           throws SQLException;

    void resultSet_updateNClob(FilterChain chain, ResultSetProxy resultSet, String columnLabel, Reader reader)
                                                                                                              throws SQLException;

    // / statement

    ResultSetProxy statement_executeQuery(FilterChain chain, StatementProxy statement, String sql) throws SQLException;

    int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql) throws SQLException;

    void statement_close(FilterChain chain, StatementProxy statement) throws SQLException;

    int statement_getMaxFieldSize(FilterChain chain, StatementProxy statement) throws SQLException;

    void statement_setMaxFieldSize(FilterChain chain, StatementProxy statement, int max) throws SQLException;

    int statement_getMaxRows(FilterChain chain, StatementProxy statement) throws SQLException;

    void statement_setMaxRows(FilterChain chain, StatementProxy statement, int max) throws SQLException;

    void statement_setEscapeProcessing(FilterChain chain, StatementProxy statement, boolean enable) throws SQLException;

    int statement_getQueryTimeout(FilterChain chain, StatementProxy statement) throws SQLException;

    void statement_setQueryTimeout(FilterChain chain, StatementProxy statement, int seconds) throws SQLException;

    void statement_cancel(FilterChain chain, StatementProxy statement) throws SQLException;

    SQLWarning statement_getWarnings(FilterChain chain, StatementProxy statement) throws SQLException;

    void statement_clearWarnings(FilterChain chain, StatementProxy statement) throws SQLException;

    void statement_setCursorName(FilterChain chain, StatementProxy statement, String name) throws SQLException;

    boolean statement_execute(FilterChain chain, StatementProxy statement, String sql) throws SQLException;

    ResultSetProxy statement_getResultSet(FilterChain chain, StatementProxy statement) throws SQLException;

    int statement_getUpdateCount(FilterChain chain, StatementProxy statement) throws SQLException;

    boolean statement_getMoreResults(FilterChain chain, StatementProxy statement) throws SQLException;

    void statement_setFetchDirection(FilterChain chain, StatementProxy statement, int direction) throws SQLException;

    int statement_getFetchDirection(FilterChain chain, StatementProxy statement) throws SQLException;

    void statement_setFetchSize(FilterChain chain, StatementProxy statement, int rows) throws SQLException;

    int statement_getFetchSize(FilterChain chain, StatementProxy statement) throws SQLException;

    int statement_getResultSetConcurrency(FilterChain chain, StatementProxy statement) throws SQLException;

    int statement_getResultSetType(FilterChain chain, StatementProxy statement) throws SQLException;

    void statement_addBatch(FilterChain chain, StatementProxy statement, String sql) throws SQLException;

    void statement_clearBatch(FilterChain chain, StatementProxy statement) throws SQLException;

    int[] statement_executeBatch(FilterChain chain, StatementProxy statement) throws SQLException;

    Connection statement_getConnection(FilterChain chain, StatementProxy statement) throws SQLException;

    boolean statement_getMoreResults(FilterChain chain, StatementProxy statement, int current) throws SQLException;

    ResultSetProxy statement_getGeneratedKeys(FilterChain chain, StatementProxy statement) throws SQLException;

    int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, int autoGeneratedKeys)
                                                                                                               throws SQLException;

    int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, int columnIndexes[])
                                                                                                             throws SQLException;

    int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, String columnNames[])
                                                                                                              throws SQLException;

    boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, int autoGeneratedKeys)
                                                                                                             throws SQLException;

    boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, int columnIndexes[])
                                                                                                           throws SQLException;

    boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, String columnNames[])
                                                                                                            throws SQLException;

    int statement_getResultSetHoldability(FilterChain chain, StatementProxy statement) throws SQLException;

    boolean statement_isClosed(FilterChain chain, StatementProxy statement) throws SQLException;

    void statement_setPoolable(FilterChain chain, StatementProxy statement, boolean poolable) throws SQLException;

    boolean statement_isPoolable(FilterChain chain, StatementProxy statement) throws SQLException;

    // ///

    ResultSetProxy preparedStatement_executeQuery(FilterChain chain, PreparedStatementProxy statement)
                                                                                                      throws SQLException;

    int preparedStatement_executeUpdate(FilterChain chain, PreparedStatementProxy statement) throws SQLException;

    void preparedStatement_setNull(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, int sqlType)
                                                                                                                        throws SQLException;

    void preparedStatement_setBoolean(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, boolean x)
                                                                                                                         throws SQLException;

    void preparedStatement_setByte(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, byte x)
                                                                                                                   throws SQLException;

    void preparedStatement_setShort(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, short x)
                                                                                                                     throws SQLException;

    void preparedStatement_setInt(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, int x)
                                                                                                                 throws SQLException;

    void preparedStatement_setLong(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, long x)
                                                                                                                   throws SQLException;

    void preparedStatement_setFloat(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, float x)
                                                                                                                     throws SQLException;

    void preparedStatement_setDouble(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, double x)
                                                                                                                       throws SQLException;

    void preparedStatement_setBigDecimal(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                         BigDecimal x) throws SQLException;

    void preparedStatement_setString(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, String x)
                                                                                                                       throws SQLException;

    void preparedStatement_setBytes(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, byte x[])
                                                                                                                      throws SQLException;

    void preparedStatement_setDate(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                   java.sql.Date x) throws SQLException;

    void preparedStatement_setTime(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                   java.sql.Time x) throws SQLException;

    void preparedStatement_setTimestamp(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                        java.sql.Timestamp x) throws SQLException;

    void preparedStatement_setAsciiStream(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          java.io.InputStream x, int length) throws SQLException;

    void preparedStatement_setUnicodeStream(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                            java.io.InputStream x, int length) throws SQLException;

    void preparedStatement_setBinaryStream(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           java.io.InputStream x, int length) throws SQLException;

    void preparedStatement_clearParameters(FilterChain chain, PreparedStatementProxy statement) throws SQLException;

    void preparedStatement_setObject(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, Object x,
                                     int targetSqlType) throws SQLException;

    void preparedStatement_setObject(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, Object x)
                                                                                                                       throws SQLException;

    boolean preparedStatement_execute(FilterChain chain, PreparedStatementProxy statement) throws SQLException;

    void preparedStatement_addBatch(FilterChain chain, PreparedStatementProxy statement) throws SQLException;

    void preparedStatement_setCharacterStream(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                              java.io.Reader reader, int length) throws SQLException;

    void preparedStatement_setRef(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, Ref x)
                                                                                                                 throws SQLException;

    void preparedStatement_setBlob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, Blob x)
                                                                                                                   throws SQLException;

    void preparedStatement_setClob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, Clob x)
                                                                                                                   throws SQLException;

    void preparedStatement_setArray(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, Array x)
                                                                                                                     throws SQLException;

    ResultSetMetaData preparedStatement_getMetaData(FilterChain chain, PreparedStatementProxy statement)
                                                                                                        throws SQLException;

    void preparedStatement_setDate(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                   java.sql.Date x, Calendar cal) throws SQLException;

    void preparedStatement_setTime(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                   java.sql.Time x, Calendar cal) throws SQLException;

    void preparedStatement_setTimestamp(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                        java.sql.Timestamp x, Calendar cal) throws SQLException;

    void preparedStatement_setNull(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                   int sqlType, String typeName) throws SQLException;

    void preparedStatement_setURL(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                  java.net.URL x) throws SQLException;

    ParameterMetaData preparedStatement_getParameterMetaData(FilterChain chain, PreparedStatementProxy statement)
                                                                                                                 throws SQLException;

    void preparedStatement_setRowId(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, RowId x)
                                                                                                                     throws SQLException;

    void preparedStatement_setNString(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                      String value) throws SQLException;

    void preparedStatement_setNCharacterStream(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                               Reader value, long length) throws SQLException;

    void preparedStatement_setNClob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, NClob value)
                                                                                                                         throws SQLException;

    void preparedStatement_setClob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                   Reader reader, long length) throws SQLException;

    void preparedStatement_setBlob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                   InputStream inputStream, long length) throws SQLException;

    void preparedStatement_setNClob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                    Reader reader, long length) throws SQLException;

    void preparedStatement_setSQLXML(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                     SQLXML xmlObject) throws SQLException;

    void preparedStatement_setObject(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, Object x,
                                     int targetSqlType, int scaleOrLength) throws SQLException;

    void preparedStatement_setAsciiStream(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          java.io.InputStream x, long length) throws SQLException;

    void preparedStatement_setBinaryStream(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           java.io.InputStream x, long length) throws SQLException;

    void preparedStatement_setCharacterStream(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                              java.io.Reader reader, long length) throws SQLException;

    void preparedStatement_setAsciiStream(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          java.io.InputStream x) throws SQLException;

    void preparedStatement_setBinaryStream(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           java.io.InputStream x) throws SQLException;

    void preparedStatement_setCharacterStream(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                              java.io.Reader reader) throws SQLException;

    void preparedStatement_setNCharacterStream(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                               Reader value) throws SQLException;

    void preparedStatement_setClob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                   Reader reader) throws SQLException;

    void preparedStatement_setBlob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                   InputStream inputStream) throws SQLException;

    void preparedStatement_setNClob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                    Reader reader) throws SQLException;

    // ///////////////////////////////////

    void callableStatement_registerOutParameter(FilterChain chain, CallableStatementProxy statement,
                                                int parameterIndex, int sqlType) throws SQLException;

    void callableStatement_registerOutParameter(FilterChain chain, CallableStatementProxy statement,
                                                int parameterIndex, int sqlType, int scale) throws SQLException;

    boolean callableStatement_wasNull(FilterChain chain, CallableStatementProxy statement) throws SQLException;

    String callableStatement_getString(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                               throws SQLException;

    boolean callableStatement_getBoolean(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                 throws SQLException;

    byte callableStatement_getByte(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                           throws SQLException;

    short callableStatement_getShort(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                             throws SQLException;

    int callableStatement_getInt(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                         throws SQLException;

    long callableStatement_getLong(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                           throws SQLException;

    float callableStatement_getFloat(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                             throws SQLException;

    double callableStatement_getDouble(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                               throws SQLException;

    BigDecimal callableStatement_getBigDecimal(FilterChain chain, CallableStatementProxy statement, int parameterIndex,
                                               int scale) throws SQLException;

    byte[] callableStatement_getBytes(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                              throws SQLException;

    java.sql.Date callableStatement_getDate(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                    throws SQLException;

    java.sql.Time callableStatement_getTime(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                    throws SQLException;

    java.sql.Timestamp callableStatement_getTimestamp(FilterChain chain, CallableStatementProxy statement,
                                                      int parameterIndex) throws SQLException;

    Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                               throws SQLException;

    BigDecimal callableStatement_getBigDecimal(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                       throws SQLException;

    Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, int parameterIndex,
                                       java.util.Map<String, Class<?>> map) throws SQLException;

    Ref callableStatement_getRef(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                         throws SQLException;

    Blob callableStatement_getBlob(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                           throws SQLException;

    Clob callableStatement_getClob(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                           throws SQLException;

    Array callableStatement_getArray(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                             throws SQLException;

    java.sql.Date callableStatement_getDate(FilterChain chain, CallableStatementProxy statement, int parameterIndex,
                                            Calendar cal) throws SQLException;

    java.sql.Time callableStatement_getTime(FilterChain chain, CallableStatementProxy statement, int parameterIndex,
                                            Calendar cal) throws SQLException;

    java.sql.Timestamp callableStatement_getTimestamp(FilterChain chain, CallableStatementProxy statement,
                                                      int parameterIndex, Calendar cal) throws SQLException;

    void callableStatement_registerOutParameter(FilterChain chain, CallableStatementProxy statement,
                                                int parameterIndex, int sqlType, String typeName) throws SQLException;

    void callableStatement_registerOutParameter(FilterChain chain, CallableStatementProxy statement,
                                                String parameterName, int sqlType) throws SQLException;

    void callableStatement_registerOutParameter(FilterChain chain, CallableStatementProxy statement,
                                                String parameterName, int sqlType, int scale) throws SQLException;

    void callableStatement_registerOutParameter(FilterChain chain, CallableStatementProxy statement,
                                                String parameterName, int sqlType, String typeName) throws SQLException;

    java.net.URL callableStatement_getURL(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                  throws SQLException;

    void callableStatement_setURL(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                  java.net.URL val) throws SQLException;

    void callableStatement_setNull(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                   int sqlType) throws SQLException;

    void callableStatement_setBoolean(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                      boolean x) throws SQLException;

    void callableStatement_setByte(FilterChain chain, CallableStatementProxy statement, String parameterName, byte x)
                                                                                                                     throws SQLException;

    void callableStatement_setShort(FilterChain chain, CallableStatementProxy statement, String parameterName, short x)
                                                                                                                       throws SQLException;

    void callableStatement_setInt(FilterChain chain, CallableStatementProxy statement, String parameterName, int x)
                                                                                                                   throws SQLException;

    void callableStatement_setLong(FilterChain chain, CallableStatementProxy statement, String parameterName, long x)
                                                                                                                     throws SQLException;

    void callableStatement_setFloat(FilterChain chain, CallableStatementProxy statement, String parameterName, float x)
                                                                                                                       throws SQLException;

    void callableStatement_setDouble(FilterChain chain, CallableStatementProxy statement, String parameterName, double x)
                                                                                                                         throws SQLException;

    void callableStatement_setBigDecimal(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                         BigDecimal x) throws SQLException;

    void callableStatement_setString(FilterChain chain, CallableStatementProxy statement, String parameterName, String x)
                                                                                                                         throws SQLException;

    void callableStatement_setBytes(FilterChain chain, CallableStatementProxy statement, String parameterName, byte x[])
                                                                                                                        throws SQLException;

    void callableStatement_setDate(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                   java.sql.Date x) throws SQLException;

    void callableStatement_setTime(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                   java.sql.Time x) throws SQLException;

    void callableStatement_setTimestamp(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                        java.sql.Timestamp x) throws SQLException;

    void callableStatement_setAsciiStream(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                          java.io.InputStream x, int length) throws SQLException;

    void callableStatement_setBinaryStream(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                           java.io.InputStream x, int length) throws SQLException;

    void callableStatement_setObject(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                     Object x, int targetSqlType, int scale) throws SQLException;

    void callableStatement_setObject(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                     Object x, int targetSqlType) throws SQLException;

    void callableStatement_setObject(FilterChain chain, CallableStatementProxy statement, String parameterName, Object x)
                                                                                                                         throws SQLException;

    void callableStatement_setCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                              String parameterName, java.io.Reader reader, int length)
                                                                                                      throws SQLException;

    void callableStatement_setDate(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                   java.sql.Date x, Calendar cal) throws SQLException;

    void callableStatement_setTime(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                   java.sql.Time x, Calendar cal) throws SQLException;

    void callableStatement_setTimestamp(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                        java.sql.Timestamp x, Calendar cal) throws SQLException;

    void callableStatement_setNull(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                   int sqlType, String typeName) throws SQLException;

    String callableStatement_getString(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                 throws SQLException;

    boolean callableStatement_getBoolean(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                   throws SQLException;

    byte callableStatement_getByte(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                             throws SQLException;

    short callableStatement_getShort(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                               throws SQLException;

    int callableStatement_getInt(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                           throws SQLException;

    long callableStatement_getLong(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                             throws SQLException;

    float callableStatement_getFloat(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                               throws SQLException;

    double callableStatement_getDouble(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                 throws SQLException;

    byte[] callableStatement_getBytes(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                throws SQLException;

    java.sql.Date callableStatement_getDate(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                      throws SQLException;

    java.sql.Time callableStatement_getTime(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                      throws SQLException;

    java.sql.Timestamp callableStatement_getTimestamp(FilterChain chain, CallableStatementProxy statement,
                                                      String parameterName) throws SQLException;

    Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                 throws SQLException;

    BigDecimal callableStatement_getBigDecimal(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                         throws SQLException;

    Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                       java.util.Map<String, Class<?>> map) throws SQLException;

    Ref callableStatement_getRef(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                           throws SQLException;

    Blob callableStatement_getBlob(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                             throws SQLException;

    Clob callableStatement_getClob(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                             throws SQLException;

    Array callableStatement_getArray(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                               throws SQLException;

    java.sql.Date callableStatement_getDate(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                            Calendar cal) throws SQLException;

    java.sql.Time callableStatement_getTime(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                            Calendar cal) throws SQLException;

    java.sql.Timestamp callableStatement_getTimestamp(FilterChain chain, CallableStatementProxy statement,
                                                      String parameterName, Calendar cal) throws SQLException;

    java.net.URL callableStatement_getURL(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                    throws SQLException;

    RowId callableStatement_getRowId(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                             throws SQLException;

    RowId callableStatement_getRowId(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                               throws SQLException;

    void callableStatement_setRowId(FilterChain chain, CallableStatementProxy statement, String parameterName, RowId x)
                                                                                                                       throws SQLException;

    void callableStatement_setNString(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                      String value) throws SQLException;

    void callableStatement_setNCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                               String parameterName, Reader value, long length) throws SQLException;

    void callableStatement_setNClob(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                    NClob value) throws SQLException;

    void callableStatement_setClob(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                   Reader reader, long length) throws SQLException;

    void callableStatement_setBlob(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                   InputStream inputStream, long length) throws SQLException;

    void callableStatement_setNClob(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                    Reader reader, long length) throws SQLException;

    NClob callableStatement_getNClob(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                             throws SQLException;

    NClob callableStatement_getNClob(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                               throws SQLException;

    void callableStatement_setSQLXML(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                     SQLXML xmlObject) throws SQLException;

    SQLXML callableStatement_getSQLXML(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                               throws SQLException;

    SQLXML callableStatement_getSQLXML(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                 throws SQLException;

    String callableStatement_getNString(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                throws SQLException;

    String callableStatement_getNString(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                  throws SQLException;

    java.io.Reader callableStatement_getNCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                                         int parameterIndex) throws SQLException;

    java.io.Reader callableStatement_getNCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                                         String parameterName) throws SQLException;

    java.io.Reader callableStatement_getCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                                        int parameterIndex) throws SQLException;

    java.io.Reader callableStatement_getCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                                        String parameterName) throws SQLException;

    void callableStatement_setBlob(FilterChain chain, CallableStatementProxy statement, String parameterName, Blob x)
                                                                                                                     throws SQLException;

    void callableStatement_setClob(FilterChain chain, CallableStatementProxy statement, String parameterName, Clob x)
                                                                                                                     throws SQLException;

    void callableStatement_setAsciiStream(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                          java.io.InputStream x, long length) throws SQLException;

    void callableStatement_setBinaryStream(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                           java.io.InputStream x, long length) throws SQLException;

    void callableStatement_setCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                              String parameterName, java.io.Reader reader, long length)
                                                                                                       throws SQLException;

    void callableStatement_setAsciiStream(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                          java.io.InputStream x) throws SQLException;

    void callableStatement_setBinaryStream(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                           java.io.InputStream x) throws SQLException;

    void callableStatement_setCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                              String parameterName, java.io.Reader reader) throws SQLException;

    void callableStatement_setNCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                               String parameterName, Reader value) throws SQLException;

    void callableStatement_setClob(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                   Reader reader) throws SQLException;

    void callableStatement_setBlob(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                   InputStream inputStream) throws SQLException;

    void callableStatement_setNClob(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                    Reader reader) throws SQLException;

    <T> T unwrap(FilterChain chain, Wrapper wrapper, java.lang.Class<T> iface) throws java.sql.SQLException;

    boolean isWrapperFor(FilterChain chain, Wrapper wrapper, java.lang.Class<?> iface) throws java.sql.SQLException;

    void clob_free(FilterChain chain, ClobProxy wrapper) throws SQLException;

    InputStream clob_getAsciiStream(FilterChain chain, ClobProxy wrapper) throws SQLException;

    Reader clob_getCharacterStream(FilterChain chain, ClobProxy wrapper) throws SQLException;

    Reader clob_getCharacterStream(FilterChain chain, ClobProxy wrapper, long pos, long length) throws SQLException;

    String clob_getSubString(FilterChain chain, ClobProxy wrapper, long pos, int length) throws SQLException;

    long clob_length(FilterChain chain, ClobProxy wrapper) throws SQLException;

    long clob_position(FilterChain chain, ClobProxy wrapper, String searchstr, long start) throws SQLException;

    long clob_position(FilterChain chain, ClobProxy wrapper, Clob searchstr, long start) throws SQLException;

    OutputStream clob_setAsciiStream(FilterChain chain, ClobProxy wrapper, long pos) throws SQLException;

    Writer clob_setCharacterStream(FilterChain chain, ClobProxy wrapper, long pos) throws SQLException;

    int clob_setString(FilterChain chain, ClobProxy wrapper, long pos, String str) throws SQLException;

    int clob_setString(FilterChain chain, ClobProxy wrapper, long pos, String str, int offset, int len)
                                                                                                       throws SQLException;

    void clob_truncate(FilterChain chain, ClobProxy wrapper, long len) throws SQLException;

    void dataSource_releaseConnection(FilterChain chain, DruidPooledConnection connection) throws SQLException;

    DruidPooledConnection dataSource_getConnection(FilterChain chain, DruidDataSource dataSource, long maxWaitMillis)
                                                                                                                     throws SQLException;

    // /////////////////
    int resultSetMetaData_getColumnCount(FilterChain chain, ResultSetMetaDataProxy metaData) throws SQLException;

    boolean resultSetMetaData_isAutoIncrement(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                             throws SQLException;

    boolean resultSetMetaData_isCaseSensitive(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                             throws SQLException;

    boolean resultSetMetaData_isSearchable(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                          throws SQLException;

    boolean resultSetMetaData_isCurrency(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                        throws SQLException;

    int resultSetMetaData_isNullable(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                    throws SQLException;

    boolean resultSetMetaData_isSigned(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                      throws SQLException;

    int resultSetMetaData_getColumnDisplaySize(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                              throws SQLException;

    String resultSetMetaData_getColumnLabel(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                           throws SQLException;

    String resultSetMetaData_getColumnName(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                          throws SQLException;

    String resultSetMetaData_getSchemaName(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                          throws SQLException;

    int resultSetMetaData_getPrecision(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                      throws SQLException;

    int resultSetMetaData_getScale(FilterChain chain, ResultSetMetaDataProxy metaData, int column) throws SQLException;

    String resultSetMetaData_getTableName(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                         throws SQLException;

    String resultSetMetaData_getCatalogName(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                           throws SQLException;

    int resultSetMetaData_getColumnType(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                       throws SQLException;

    String resultSetMetaData_getColumnTypeName(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                              throws SQLException;

    boolean resultSetMetaData_isReadOnly(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                        throws SQLException;

    boolean resultSetMetaData_isWritable(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                        throws SQLException;

    boolean resultSetMetaData_isDefinitelyWritable(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                  throws SQLException;

    String resultSetMetaData_getColumnClassName(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                               throws SQLException;

}
