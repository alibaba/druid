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

import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.ClobProxy;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public interface FilterChain {
    
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
    public boolean resultSet_next(ResultSetProxy resultSet) throws SQLException;

    public void resultSet_close(ResultSetProxy resultSet) throws SQLException;

    public boolean resultSet_wasNull(ResultSetProxy resultSet) throws SQLException;

    public String resultSet_getString(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public boolean resultSet_getBoolean(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public byte resultSet_getByte(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public short resultSet_getShort(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public int resultSet_getInt(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public long resultSet_getLong(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public float resultSet_getFloat(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public double resultSet_getDouble(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public BigDecimal resultSet_getBigDecimal(ResultSetProxy resultSet, int columnIndex, int scale) throws SQLException;

    public byte[] resultSet_getBytes(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public java.sql.Date resultSet_getDate(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public java.sql.Time resultSet_getTime(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public java.sql.Timestamp resultSet_getTimestamp(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public java.io.InputStream resultSet_getAsciiStream(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public java.io.InputStream resultSet_getUnicodeStream(ResultSetProxy resultSet, int columnIndex)
                                                                                                    throws SQLException;

    public java.io.InputStream resultSet_getBinaryStream(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public String resultSet_getString(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public boolean resultSet_getBoolean(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public byte resultSet_getByte(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public short resultSet_getShort(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public int resultSet_getInt(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public long resultSet_getLong(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public float resultSet_getFloat(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public double resultSet_getDouble(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public BigDecimal resultSet_getBigDecimal(ResultSetProxy resultSet, String columnLabel, int scale)
                                                                                                      throws SQLException;

    public byte[] resultSet_getBytes(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public java.sql.Date resultSet_getDate(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public java.sql.Time resultSet_getTime(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public java.sql.Timestamp resultSet_getTimestamp(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public java.io.InputStream resultSet_getAsciiStream(ResultSetProxy resultSet, String columnLabel)
                                                                                                     throws SQLException;

    public java.io.InputStream resultSet_getUnicodeStream(ResultSetProxy resultSet, String columnLabel)
                                                                                                       throws SQLException;

    public java.io.InputStream resultSet_getBinaryStream(ResultSetProxy resultSet, String columnLabel)
                                                                                                      throws SQLException;

    public SQLWarning resultSet_getWarnings(ResultSetProxy resultSet) throws SQLException;

    public void resultSet_clearWarnings(ResultSetProxy resultSet) throws SQLException;

    public String resultSet_getCursorName(ResultSetProxy resultSet) throws SQLException;

    public ResultSetMetaData resultSet_getMetaData(ResultSetProxy resultSet) throws SQLException;

    public Object resultSet_getObject(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public Object resultSet_getObject(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public int resultSet_findColumn(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public java.io.Reader resultSet_getCharacterStream(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public java.io.Reader resultSet_getCharacterStream(ResultSetProxy resultSet, String columnLabel)
                                                                                                    throws SQLException;

    public BigDecimal resultSet_getBigDecimal(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public BigDecimal resultSet_getBigDecimal(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public boolean resultSet_isBeforeFirst(ResultSetProxy resultSet) throws SQLException;

    public boolean resultSet_isAfterLast(ResultSetProxy resultSet) throws SQLException;

    public boolean resultSet_isFirst(ResultSetProxy resultSet) throws SQLException;

    public boolean resultSet_isLast(ResultSetProxy resultSet) throws SQLException;

    public void resultSet_beforeFirst(ResultSetProxy resultSet) throws SQLException;

    public void resultSet_afterLast(ResultSetProxy resultSet) throws SQLException;

    public boolean resultSet_first(ResultSetProxy resultSet) throws SQLException;

    public boolean resultSet_last(ResultSetProxy resultSet) throws SQLException;

    public int resultSet_getRow(ResultSetProxy resultSet) throws SQLException;

    public boolean resultSet_absolute(ResultSetProxy resultSet, int row) throws SQLException;

    public boolean resultSet_relative(ResultSetProxy resultSet, int rows) throws SQLException;

    public boolean resultSet_previous(ResultSetProxy resultSet) throws SQLException;

    public void resultSet_setFetchDirection(ResultSetProxy resultSet, int direction) throws SQLException;

    public int resultSet_getFetchDirection(ResultSetProxy resultSet) throws SQLException;

    public void resultSet_setFetchSize(ResultSetProxy resultSet, int rows) throws SQLException;

    public int resultSet_getFetchSize(ResultSetProxy resultSet) throws SQLException;

    public int resultSet_getType(ResultSetProxy resultSet) throws SQLException;

    public int resultSet_getConcurrency(ResultSetProxy resultSet) throws SQLException;

    public boolean resultSet_rowUpdated(ResultSetProxy resultSet) throws SQLException;

    public boolean resultSet_rowInserted(ResultSetProxy resultSet) throws SQLException;

    public boolean resultSet_rowDeleted(ResultSetProxy resultSet) throws SQLException;

    public void resultSet_updateNull(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public void resultSet_updateBoolean(ResultSetProxy resultSet, int columnIndex, boolean x) throws SQLException;

    public void resultSet_updateByte(ResultSetProxy resultSet, int columnIndex, byte x) throws SQLException;

    public void resultSet_updateShort(ResultSetProxy resultSet, int columnIndex, short x) throws SQLException;

    public void resultSet_updateInt(ResultSetProxy resultSet, int columnIndex, int x) throws SQLException;

    public void resultSet_updateLong(ResultSetProxy resultSet, int columnIndex, long x) throws SQLException;

    public void resultSet_updateFloat(ResultSetProxy resultSet, int columnIndex, float x) throws SQLException;

    public void resultSet_updateDouble(ResultSetProxy resultSet, int columnIndex, double x) throws SQLException;

    public void resultSet_updateBigDecimal(ResultSetProxy resultSet, int columnIndex, BigDecimal x) throws SQLException;

    public void resultSet_updateString(ResultSetProxy resultSet, int columnIndex, String x) throws SQLException;

    public void resultSet_updateBytes(ResultSetProxy resultSet, int columnIndex, byte x[]) throws SQLException;

    public void resultSet_updateDate(ResultSetProxy resultSet, int columnIndex, java.sql.Date x) throws SQLException;

    public void resultSet_updateTime(ResultSetProxy resultSet, int columnIndex, java.sql.Time x) throws SQLException;

    public void resultSet_updateTimestamp(ResultSetProxy resultSet, int columnIndex, java.sql.Timestamp x)
                                                                                                          throws SQLException;

    public void resultSet_updateAsciiStream(ResultSetProxy resultSet, int columnIndex, java.io.InputStream x, int length)
                                                                                                                         throws SQLException;

    public void resultSet_updateBinaryStream(ResultSetProxy resultSet, int columnIndex, java.io.InputStream x,
                                             int length) throws SQLException;

    public void resultSet_updateCharacterStream(ResultSetProxy resultSet, int columnIndex, java.io.Reader x, int length)
                                                                                                                        throws SQLException;

    public void resultSet_updateObject(ResultSetProxy resultSet, int columnIndex, Object x, int scaleOrLength)
                                                                                                              throws SQLException;

    public void resultSet_updateObject(ResultSetProxy resultSet, int columnIndex, Object x) throws SQLException;

    public void resultSet_updateNull(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public void resultSet_updateBoolean(ResultSetProxy resultSet, String columnLabel, boolean x) throws SQLException;

    public void resultSet_updateByte(ResultSetProxy resultSet, String columnLabel, byte x) throws SQLException;

    public void resultSet_updateShort(ResultSetProxy resultSet, String columnLabel, short x) throws SQLException;

    public void resultSet_updateInt(ResultSetProxy resultSet, String columnLabel, int x) throws SQLException;

    public void resultSet_updateLong(ResultSetProxy resultSet, String columnLabel, long x) throws SQLException;

    public void resultSet_updateFloat(ResultSetProxy resultSet, String columnLabel, float x) throws SQLException;

    public void resultSet_updateDouble(ResultSetProxy resultSet, String columnLabel, double x) throws SQLException;

    public void resultSet_updateBigDecimal(ResultSetProxy resultSet, String columnLabel, BigDecimal x)
                                                                                                      throws SQLException;

    public void resultSet_updateString(ResultSetProxy resultSet, String columnLabel, String x) throws SQLException;

    public void resultSet_updateBytes(ResultSetProxy resultSet, String columnLabel, byte x[]) throws SQLException;

    public void resultSet_updateDate(ResultSetProxy resultSet, String columnLabel, java.sql.Date x) throws SQLException;

    public void resultSet_updateTime(ResultSetProxy resultSet, String columnLabel, java.sql.Time x) throws SQLException;

    public void resultSet_updateTimestamp(ResultSetProxy resultSet, String columnLabel, java.sql.Timestamp x)
                                                                                                             throws SQLException;

    public void resultSet_updateAsciiStream(ResultSetProxy resultSet, String columnLabel, java.io.InputStream x,
                                            int length) throws SQLException;

    public void resultSet_updateBinaryStream(ResultSetProxy resultSet, String columnLabel, java.io.InputStream x,
                                             int length) throws SQLException;

    public void resultSet_updateCharacterStream(ResultSetProxy resultSet, String columnLabel, java.io.Reader reader,
                                                int length) throws SQLException;

    public void resultSet_updateObject(ResultSetProxy resultSet, String columnLabel, Object x, int scaleOrLength)
                                                                                                                 throws SQLException;

    public void resultSet_updateObject(ResultSetProxy resultSet, String columnLabel, Object x) throws SQLException;

    public void resultSet_insertRow(ResultSetProxy resultSet) throws SQLException;

    public void resultSet_updateRow(ResultSetProxy resultSet) throws SQLException;

    public void resultSet_deleteRow(ResultSetProxy resultSet) throws SQLException;

    public void resultSet_refreshRow(ResultSetProxy resultSet) throws SQLException;

    public void resultSet_cancelRowUpdates(ResultSetProxy resultSet) throws SQLException;

    public void resultSet_moveToInsertRow(ResultSetProxy resultSet) throws SQLException;

    public void resultSet_moveToCurrentRow(ResultSetProxy resultSet) throws SQLException;

    public Statement resultSet_getStatement(ResultSetProxy resultSet) throws SQLException;

    public Object resultSet_getObject(ResultSetProxy resultSet, int columnIndex, java.util.Map<String, Class<?>> map)
                                                                                                                     throws SQLException;

    public Ref resultSet_getRef(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public Blob resultSet_getBlob(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public Clob resultSet_getClob(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public Array resultSet_getArray(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public Object resultSet_getObject(ResultSetProxy resultSet, String columnLabel, java.util.Map<String, Class<?>> map)
                                                                                                                        throws SQLException;

    public Ref resultSet_getRef(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public Blob resultSet_getBlob(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public Clob resultSet_getClob(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public Array resultSet_getArray(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public java.sql.Date resultSet_getDate(ResultSetProxy resultSet, int columnIndex, Calendar cal) throws SQLException;

    public java.sql.Date resultSet_getDate(ResultSetProxy resultSet, String columnLabel, Calendar cal)
                                                                                                      throws SQLException;

    public java.sql.Time resultSet_getTime(ResultSetProxy resultSet, int columnIndex, Calendar cal) throws SQLException;

    public java.sql.Time resultSet_getTime(ResultSetProxy resultSet, String columnLabel, Calendar cal)
                                                                                                      throws SQLException;

    public java.sql.Timestamp resultSet_getTimestamp(ResultSetProxy resultSet, int columnIndex, Calendar cal)
                                                                                                             throws SQLException;

    public java.sql.Timestamp resultSet_getTimestamp(ResultSetProxy resultSet, String columnLabel, Calendar cal)
                                                                                                                throws SQLException;

    public java.net.URL resultSet_getURL(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public java.net.URL resultSet_getURL(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public void resultSet_updateRef(ResultSetProxy resultSet, int columnIndex, java.sql.Ref x) throws SQLException;

    public void resultSet_updateRef(ResultSetProxy resultSet, String columnLabel, java.sql.Ref x) throws SQLException;

    public void resultSet_updateBlob(ResultSetProxy resultSet, int columnIndex, java.sql.Blob x) throws SQLException;

    public void resultSet_updateBlob(ResultSetProxy resultSet, String columnLabel, java.sql.Blob x) throws SQLException;

    public void resultSet_updateClob(ResultSetProxy resultSet, int columnIndex, java.sql.Clob x) throws SQLException;

    public void resultSet_updateClob(ResultSetProxy resultSet, String columnLabel, java.sql.Clob x) throws SQLException;

    public void resultSet_updateArray(ResultSetProxy resultSet, int columnIndex, java.sql.Array x) throws SQLException;

    public void resultSet_updateArray(ResultSetProxy resultSet, String columnLabel, java.sql.Array x)
                                                                                                     throws SQLException;

    public RowId resultSet_getRowId(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public RowId resultSet_getRowId(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public void resultSet_updateRowId(ResultSetProxy resultSet, int columnIndex, RowId x) throws SQLException;

    public void resultSet_updateRowId(ResultSetProxy resultSet, String columnLabel, RowId x) throws SQLException;

    public int resultSet_getHoldability(ResultSetProxy resultSet) throws SQLException;

    public boolean resultSet_isClosed(ResultSetProxy resultSet) throws SQLException;

    public void resultSet_updateNString(ResultSetProxy resultSet, int columnIndex, String nString) throws SQLException;

    public void resultSet_updateNString(ResultSetProxy resultSet, String columnLabel, String nString)
                                                                                                     throws SQLException;

    public void resultSet_updateNClob(ResultSetProxy resultSet, int columnIndex, NClob nClob) throws SQLException;

    public void resultSet_updateNClob(ResultSetProxy resultSet, String columnLabel, NClob nClob) throws SQLException;

    public NClob resultSet_getNClob(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public NClob resultSet_getNClob(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public SQLXML resultSet_getSQLXML(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public SQLXML resultSet_getSQLXML(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public void resultSet_updateSQLXML(ResultSetProxy resultSet, int columnIndex, SQLXML xmlObject) throws SQLException;

    public void resultSet_updateSQLXML(ResultSetProxy resultSet, String columnLabel, SQLXML xmlObject)
                                                                                                      throws SQLException;

    public String resultSet_getNString(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public String resultSet_getNString(ResultSetProxy resultSet, String columnLabel) throws SQLException;

    public java.io.Reader resultSet_getNCharacterStream(ResultSetProxy resultSet, int columnIndex) throws SQLException;

    public java.io.Reader resultSet_getNCharacterStream(ResultSetProxy resultSet, String columnLabel)
                                                                                                     throws SQLException;

    public void resultSet_updateNCharacterStream(ResultSetProxy resultSet, int columnIndex, java.io.Reader x,
                                                 long length) throws SQLException;

    public void resultSet_updateNCharacterStream(ResultSetProxy resultSet, String columnLabel, java.io.Reader reader,
                                                 long length) throws SQLException;

    public void resultSet_updateAsciiStream(ResultSetProxy resultSet, int columnIndex, java.io.InputStream x,
                                            long length) throws SQLException;

    public void resultSet_updateBinaryStream(ResultSetProxy resultSet, int columnIndex, java.io.InputStream x,
                                             long length) throws SQLException;

    public void resultSet_updateCharacterStream(ResultSetProxy resultSet, int columnIndex, java.io.Reader x, long length)
                                                                                                                         throws SQLException;

    public void resultSet_updateAsciiStream(ResultSetProxy resultSet, String columnLabel, java.io.InputStream x,
                                            long length) throws SQLException;

    public void resultSet_updateBinaryStream(ResultSetProxy resultSet, String columnLabel, java.io.InputStream x,
                                             long length) throws SQLException;

    public void resultSet_updateCharacterStream(ResultSetProxy resultSet, String columnLabel, java.io.Reader reader,
                                                long length) throws SQLException;

    public void resultSet_updateBlob(ResultSetProxy resultSet, int columnIndex, InputStream inputStream, long length)
                                                                                                                     throws SQLException;

    public void resultSet_updateBlob(ResultSetProxy resultSet, String columnLabel, InputStream inputStream, long length)
                                                                                                                        throws SQLException;

    public void resultSet_updateClob(ResultSetProxy resultSet, int columnIndex, Reader reader, long length)
                                                                                                           throws SQLException;

    public void resultSet_updateClob(ResultSetProxy resultSet, String columnLabel, Reader reader, long length)
                                                                                                              throws SQLException;

    public void resultSet_updateNClob(ResultSetProxy resultSet, int columnIndex, Reader reader, long length)
                                                                                                            throws SQLException;

    public void resultSet_updateNClob(ResultSetProxy resultSet, String columnLabel, Reader reader, long length)
                                                                                                               throws SQLException;

    public void resultSet_updateNCharacterStream(ResultSetProxy resultSet, int columnIndex, java.io.Reader x)
                                                                                                             throws SQLException;

    public void resultSet_updateNCharacterStream(ResultSetProxy resultSet, String columnLabel, java.io.Reader reader)
                                                                                                                     throws SQLException;

    public void resultSet_updateAsciiStream(ResultSetProxy resultSet, int columnIndex, java.io.InputStream x)
                                                                                                             throws SQLException;

    public void resultSet_updateBinaryStream(ResultSetProxy resultSet, int columnIndex, java.io.InputStream x)
                                                                                                              throws SQLException;

    public void resultSet_updateCharacterStream(ResultSetProxy resultSet, int columnIndex, java.io.Reader x)
                                                                                                            throws SQLException;

    public void resultSet_updateAsciiStream(ResultSetProxy resultSet, String columnLabel, java.io.InputStream x)
                                                                                                                throws SQLException;

    public void resultSet_updateBinaryStream(ResultSetProxy resultSet, String columnLabel, java.io.InputStream x)
                                                                                                                 throws SQLException;

    public void resultSet_updateCharacterStream(ResultSetProxy resultSet, String columnLabel, java.io.Reader reader)
                                                                                                                    throws SQLException;

    public void resultSet_updateBlob(ResultSetProxy resultSet, int columnIndex, InputStream inputStream)
                                                                                                        throws SQLException;

    public void resultSet_updateBlob(ResultSetProxy resultSet, String columnLabel, InputStream inputStream)
                                                                                                           throws SQLException;

    public void resultSet_updateClob(ResultSetProxy resultSet, int columnIndex, Reader reader) throws SQLException;

    public void resultSet_updateClob(ResultSetProxy resultSet, String columnLabel, Reader reader) throws SQLException;

    public void resultSet_updateNClob(ResultSetProxy resultSet, int columnIndex, Reader reader) throws SQLException;

    public void resultSet_updateNClob(ResultSetProxy resultSet, String columnLabel, Reader reader) throws SQLException;

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

    public ResultSetProxy preparedStatement_executeQuery(PreparedStatementProxy statement) throws SQLException;

    public int preparedStatement_executeUpdate(PreparedStatementProxy statement) throws SQLException;

    public void preparedStatement_setNull(PreparedStatementProxy statement, int parameterIndex, int sqlType)
                                                                                                            throws SQLException;

    public void preparedStatement_setBoolean(PreparedStatementProxy statement, int parameterIndex, boolean x)
                                                                                                             throws SQLException;

    public void preparedStatement_setByte(PreparedStatementProxy statement, int parameterIndex, byte x)
                                                                                                       throws SQLException;

    public void preparedStatement_setShort(PreparedStatementProxy statement, int parameterIndex, short x)
                                                                                                         throws SQLException;

    public void preparedStatement_setInt(PreparedStatementProxy statement, int parameterIndex, int x)
                                                                                                     throws SQLException;

    public void preparedStatement_setLong(PreparedStatementProxy statement, int parameterIndex, long x)
                                                                                                       throws SQLException;

    public void preparedStatement_setFloat(PreparedStatementProxy statement, int parameterIndex, float x)
                                                                                                         throws SQLException;

    public void preparedStatement_setDouble(PreparedStatementProxy statement, int parameterIndex, double x)
                                                                                                           throws SQLException;

    public void preparedStatement_setBigDecimal(PreparedStatementProxy statement, int parameterIndex, BigDecimal x)
                                                                                                                   throws SQLException;

    public void preparedStatement_setString(PreparedStatementProxy statement, int parameterIndex, String x)
                                                                                                           throws SQLException;

    public void preparedStatement_setBytes(PreparedStatementProxy statement, int parameterIndex, byte x[])
                                                                                                          throws SQLException;

    public void preparedStatement_setDate(PreparedStatementProxy statement, int parameterIndex, java.sql.Date x)
                                                                                                                throws SQLException;

    public void preparedStatement_setTime(PreparedStatementProxy statement, int parameterIndex, java.sql.Time x)
                                                                                                                throws SQLException;

    public void preparedStatement_setTimestamp(PreparedStatementProxy statement, int parameterIndex,
                                               java.sql.Timestamp x) throws SQLException;

    public void preparedStatement_setAsciiStream(PreparedStatementProxy statement, int parameterIndex,
                                                 java.io.InputStream x, int length) throws SQLException;

    public void preparedStatement_setUnicodeStream(PreparedStatementProxy statement, int parameterIndex,
                                                   java.io.InputStream x, int length) throws SQLException;

    public void preparedStatement_setBinaryStream(PreparedStatementProxy statement, int parameterIndex,
                                                  java.io.InputStream x, int length) throws SQLException;

    public void preparedStatement_clearParameters(PreparedStatementProxy statement) throws SQLException;

    public void preparedStatement_setObject(PreparedStatementProxy statement, int parameterIndex, Object x,
                                            int targetSqlType) throws SQLException;

    public void preparedStatement_setObject(PreparedStatementProxy statement, int parameterIndex, Object x)
                                                                                                           throws SQLException;

    public boolean preparedStatement_execute(PreparedStatementProxy statement) throws SQLException;

    public void preparedStatement_addBatch(PreparedStatementProxy statement) throws SQLException;

    public void preparedStatement_setCharacterStream(PreparedStatementProxy statement, int parameterIndex,
                                                     java.io.Reader reader, int length) throws SQLException;

    public void preparedStatement_setRef(PreparedStatementProxy statement, int parameterIndex, Ref x)
                                                                                                     throws SQLException;

    public void preparedStatement_setBlob(PreparedStatementProxy statement, int parameterIndex, Blob x)
                                                                                                       throws SQLException;

    public void preparedStatement_setClob(PreparedStatementProxy statement, int parameterIndex, Clob x)
                                                                                                       throws SQLException;

    public void preparedStatement_setArray(PreparedStatementProxy statement, int parameterIndex, Array x)
                                                                                                         throws SQLException;

    public ResultSetMetaData preparedStatement_getMetaData(PreparedStatementProxy statement) throws SQLException;

    public void preparedStatement_setDate(PreparedStatementProxy statement, int parameterIndex, java.sql.Date x,
                                          Calendar cal) throws SQLException;

    public void preparedStatement_setTime(PreparedStatementProxy statement, int parameterIndex, java.sql.Time x,
                                          Calendar cal) throws SQLException;

    public void preparedStatement_setTimestamp(PreparedStatementProxy statement, int parameterIndex,
                                               java.sql.Timestamp x, Calendar cal) throws SQLException;

    public void preparedStatement_setNull(PreparedStatementProxy statement, int parameterIndex, int sqlType,
                                          String typeName) throws SQLException;

    public void preparedStatement_setURL(PreparedStatementProxy statement, int parameterIndex, java.net.URL x)
                                                                                                              throws SQLException;

    public ParameterMetaData preparedStatement_getParameterMetaData(PreparedStatementProxy statement)
                                                                                                     throws SQLException;

    public void preparedStatement_setRowId(PreparedStatementProxy statement, int parameterIndex, RowId x)
                                                                                                         throws SQLException;

    public void preparedStatement_setNString(PreparedStatementProxy statement, int parameterIndex, String value)
                                                                                                                throws SQLException;

    public void preparedStatement_setNCharacterStream(PreparedStatementProxy statement, int parameterIndex,
                                                      Reader value, long length) throws SQLException;

    public void preparedStatement_setNClob(PreparedStatementProxy statement, int parameterIndex, NClob value)
                                                                                                             throws SQLException;

    public void preparedStatement_setClob(PreparedStatementProxy statement, int parameterIndex, Reader reader,
                                          long length) throws SQLException;

    public void preparedStatement_setBlob(PreparedStatementProxy statement, int parameterIndex,
                                          InputStream inputStream, long length) throws SQLException;

    public void preparedStatement_setNClob(PreparedStatementProxy statement, int parameterIndex, Reader reader,
                                           long length) throws SQLException;

    public void preparedStatement_setSQLXML(PreparedStatementProxy statement, int parameterIndex, SQLXML xmlObject)
                                                                                                                   throws SQLException;

    public void preparedStatement_setObject(PreparedStatementProxy statement, int parameterIndex, Object x,
                                            int targetSqlType, int scaleOrLength) throws SQLException;

    public void preparedStatement_setAsciiStream(PreparedStatementProxy statement, int parameterIndex,
                                                 java.io.InputStream x, long length) throws SQLException;

    public void preparedStatement_setBinaryStream(PreparedStatementProxy statement, int parameterIndex,
                                                  java.io.InputStream x, long length) throws SQLException;

    public void preparedStatement_setCharacterStream(PreparedStatementProxy statement, int parameterIndex,
                                                     java.io.Reader reader, long length) throws SQLException;

    public void preparedStatement_setAsciiStream(PreparedStatementProxy statement, int parameterIndex,
                                                 java.io.InputStream x) throws SQLException;

    public void preparedStatement_setBinaryStream(PreparedStatementProxy statement, int parameterIndex,
                                                  java.io.InputStream x) throws SQLException;

    public void preparedStatement_setCharacterStream(PreparedStatementProxy statement, int parameterIndex,
                                                     java.io.Reader reader) throws SQLException;

    public void preparedStatement_setNCharacterStream(PreparedStatementProxy statement, int parameterIndex, Reader value)
                                                                                                                         throws SQLException;

    public void preparedStatement_setClob(PreparedStatementProxy statement, int parameterIndex, Reader reader)
                                                                                                              throws SQLException;

    public void preparedStatement_setBlob(PreparedStatementProxy statement, int parameterIndex, InputStream inputStream)
                                                                                                                        throws SQLException;

    public void preparedStatement_setNClob(PreparedStatementProxy statement, int parameterIndex, Reader reader)
                                                                                                               throws SQLException;

    // /////////////////////////////

    public void callableStatement_registerOutParameter(CallableStatementProxy statement, int parameterIndex, int sqlType)
                                                                                                                         throws SQLException;

    public void callableStatement_registerOutParameter(CallableStatementProxy statement, int parameterIndex,
                                                       int sqlType, int scale) throws SQLException;

    public boolean callableStatement_wasNull(CallableStatementProxy statement) throws SQLException;

    public String callableStatement_getString(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    public boolean callableStatement_getBoolean(CallableStatementProxy statement, int parameterIndex)
                                                                                                     throws SQLException;

    public byte callableStatement_getByte(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    public short callableStatement_getShort(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    public int callableStatement_getInt(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    public long callableStatement_getLong(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    public float callableStatement_getFloat(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    public double callableStatement_getDouble(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    public BigDecimal callableStatement_getBigDecimal(CallableStatementProxy statement, int parameterIndex, int scale)
                                                                                                                      throws SQLException;

    public byte[] callableStatement_getBytes(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    public java.sql.Date callableStatement_getDate(CallableStatementProxy statement, int parameterIndex)
                                                                                                        throws SQLException;

    public java.sql.Time callableStatement_getTime(CallableStatementProxy statement, int parameterIndex)
                                                                                                        throws SQLException;

    public java.sql.Timestamp callableStatement_getTimestamp(CallableStatementProxy statement, int parameterIndex)
                                                                                                                  throws SQLException;

    public Object callableStatement_getObject(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    public BigDecimal callableStatement_getBigDecimal(CallableStatementProxy statement, int parameterIndex)
                                                                                                           throws SQLException;

    public Object callableStatement_getObject(CallableStatementProxy statement, int parameterIndex,
                                              java.util.Map<String, Class<?>> map) throws SQLException;

    public Ref callableStatement_getRef(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    public Blob callableStatement_getBlob(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    public Clob callableStatement_getClob(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    public Array callableStatement_getArray(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    public java.sql.Date callableStatement_getDate(CallableStatementProxy statement, int parameterIndex, Calendar cal)
                                                                                                                      throws SQLException;

    public java.sql.Time callableStatement_getTime(CallableStatementProxy statement, int parameterIndex, Calendar cal)
                                                                                                                      throws SQLException;

    public java.sql.Timestamp callableStatement_getTimestamp(CallableStatementProxy statement, int parameterIndex,
                                                             Calendar cal) throws SQLException;

    public void callableStatement_registerOutParameter(CallableStatementProxy statement, int parameterIndex,
                                                       int sqlType, String typeName) throws SQLException;

    public void callableStatement_registerOutParameter(CallableStatementProxy statement, String parameterName,
                                                       int sqlType) throws SQLException;

    public void callableStatement_registerOutParameter(CallableStatementProxy statement, String parameterName,
                                                       int sqlType, int scale) throws SQLException;

    public void callableStatement_registerOutParameter(CallableStatementProxy statement, String parameterName,
                                                       int sqlType, String typeName) throws SQLException;

    public java.net.URL callableStatement_getURL(CallableStatementProxy statement, int parameterIndex)
                                                                                                      throws SQLException;

    public void callableStatement_setURL(CallableStatementProxy statement, String parameterName, java.net.URL val)
                                                                                                                  throws SQLException;

    public void callableStatement_setNull(CallableStatementProxy statement, String parameterName, int sqlType)
                                                                                                              throws SQLException;

    public void callableStatement_setBoolean(CallableStatementProxy statement, String parameterName, boolean x)
                                                                                                               throws SQLException;

    public void callableStatement_setByte(CallableStatementProxy statement, String parameterName, byte x)
                                                                                                         throws SQLException;

    public void callableStatement_setShort(CallableStatementProxy statement, String parameterName, short x)
                                                                                                           throws SQLException;

    public void callableStatement_setInt(CallableStatementProxy statement, String parameterName, int x)
                                                                                                       throws SQLException;

    public void callableStatement_setLong(CallableStatementProxy statement, String parameterName, long x)
                                                                                                         throws SQLException;

    public void callableStatement_setFloat(CallableStatementProxy statement, String parameterName, float x)
                                                                                                           throws SQLException;

    public void callableStatement_setDouble(CallableStatementProxy statement, String parameterName, double x)
                                                                                                             throws SQLException;

    public void callableStatement_setBigDecimal(CallableStatementProxy statement, String parameterName, BigDecimal x)
                                                                                                                     throws SQLException;

    public void callableStatement_setString(CallableStatementProxy statement, String parameterName, String x)
                                                                                                             throws SQLException;

    public void callableStatement_setBytes(CallableStatementProxy statement, String parameterName, byte x[])
                                                                                                            throws SQLException;

    public void callableStatement_setDate(CallableStatementProxy statement, String parameterName, java.sql.Date x)
                                                                                                                  throws SQLException;

    public void callableStatement_setTime(CallableStatementProxy statement, String parameterName, java.sql.Time x)
                                                                                                                  throws SQLException;

    public void callableStatement_setTimestamp(CallableStatementProxy statement, String parameterName,
                                               java.sql.Timestamp x) throws SQLException;

    public void callableStatement_setAsciiStream(CallableStatementProxy statement, String parameterName,
                                                 java.io.InputStream x, int length) throws SQLException;

    public void callableStatement_setBinaryStream(CallableStatementProxy statement, String parameterName,
                                                  java.io.InputStream x, int length) throws SQLException;

    public void callableStatement_setObject(CallableStatementProxy statement, String parameterName, Object x,
                                            int targetSqlType, int scale) throws SQLException;

    public void callableStatement_setObject(CallableStatementProxy statement, String parameterName, Object x,
                                            int targetSqlType) throws SQLException;

    public void callableStatement_setObject(CallableStatementProxy statement, String parameterName, Object x)
                                                                                                             throws SQLException;

    public void callableStatement_setCharacterStream(CallableStatementProxy statement, String parameterName,
                                                     java.io.Reader reader, int length) throws SQLException;

    public void callableStatement_setDate(CallableStatementProxy statement, String parameterName, java.sql.Date x,
                                          Calendar cal) throws SQLException;

    public void callableStatement_setTime(CallableStatementProxy statement, String parameterName, java.sql.Time x,
                                          Calendar cal) throws SQLException;

    public void callableStatement_setTimestamp(CallableStatementProxy statement, String parameterName,
                                               java.sql.Timestamp x, Calendar cal) throws SQLException;

    public void callableStatement_setNull(CallableStatementProxy statement, String parameterName, int sqlType,
                                          String typeName) throws SQLException;

    public String callableStatement_getString(CallableStatementProxy statement, String parameterName)
                                                                                                     throws SQLException;

    public boolean callableStatement_getBoolean(CallableStatementProxy statement, String parameterName)
                                                                                                       throws SQLException;

    public byte callableStatement_getByte(CallableStatementProxy statement, String parameterName) throws SQLException;

    public short callableStatement_getShort(CallableStatementProxy statement, String parameterName) throws SQLException;

    public int callableStatement_getInt(CallableStatementProxy statement, String parameterName) throws SQLException;

    public long callableStatement_getLong(CallableStatementProxy statement, String parameterName) throws SQLException;

    public float callableStatement_getFloat(CallableStatementProxy statement, String parameterName) throws SQLException;

    public double callableStatement_getDouble(CallableStatementProxy statement, String parameterName)
                                                                                                     throws SQLException;

    public byte[] callableStatement_getBytes(CallableStatementProxy statement, String parameterName)
                                                                                                    throws SQLException;

    public java.sql.Date callableStatement_getDate(CallableStatementProxy statement, String parameterName)
                                                                                                          throws SQLException;

    public java.sql.Time callableStatement_getTime(CallableStatementProxy statement, String parameterName)
                                                                                                          throws SQLException;

    public java.sql.Timestamp callableStatement_getTimestamp(CallableStatementProxy statement, String parameterName)
                                                                                                                    throws SQLException;

    public Object callableStatement_getObject(CallableStatementProxy statement, String parameterName)
                                                                                                     throws SQLException;

    public BigDecimal callableStatement_getBigDecimal(CallableStatementProxy statement, String parameterName)
                                                                                                             throws SQLException;

    public Object callableStatement_getObject(CallableStatementProxy statement, String parameterName,
                                              java.util.Map<String, Class<?>> map) throws SQLException;

    public Ref callableStatement_getRef(CallableStatementProxy statement, String parameterName) throws SQLException;

    public Blob callableStatement_getBlob(CallableStatementProxy statement, String parameterName) throws SQLException;

    public Clob callableStatement_getClob(CallableStatementProxy statement, String parameterName) throws SQLException;

    public Array callableStatement_getArray(CallableStatementProxy statement, String parameterName) throws SQLException;

    public java.sql.Date callableStatement_getDate(CallableStatementProxy statement, String parameterName, Calendar cal)
                                                                                                                        throws SQLException;

    public java.sql.Time callableStatement_getTime(CallableStatementProxy statement, String parameterName, Calendar cal)
                                                                                                                        throws SQLException;

    public java.sql.Timestamp callableStatement_getTimestamp(CallableStatementProxy statement, String parameterName,
                                                             Calendar cal) throws SQLException;

    public java.net.URL callableStatement_getURL(CallableStatementProxy statement, String parameterName)
                                                                                                        throws SQLException;

    public RowId callableStatement_getRowId(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    public RowId callableStatement_getRowId(CallableStatementProxy statement, String parameterName) throws SQLException;

    public void callableStatement_setRowId(CallableStatementProxy statement, String parameterName, RowId x)
                                                                                                           throws SQLException;

    public void callableStatement_setNString(CallableStatementProxy statement, String parameterName, String value)
                                                                                                                  throws SQLException;

    public void callableStatement_setNCharacterStream(CallableStatementProxy statement, String parameterName,
                                                      Reader value, long length) throws SQLException;

    public void callableStatement_setNClob(CallableStatementProxy statement, String parameterName, NClob value)
                                                                                                               throws SQLException;

    public void callableStatement_setClob(CallableStatementProxy statement, String parameterName, Reader reader,
                                          long length) throws SQLException;

    public void callableStatement_setBlob(CallableStatementProxy statement, String parameterName,
                                          InputStream inputStream, long length) throws SQLException;

    public void callableStatement_setNClob(CallableStatementProxy statement, String parameterName, Reader reader,
                                           long length) throws SQLException;

    public NClob callableStatement_getNClob(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    public NClob callableStatement_getNClob(CallableStatementProxy statement, String parameterName) throws SQLException;

    public void callableStatement_setSQLXML(CallableStatementProxy statement, String parameterName, SQLXML xmlObject)
                                                                                                                     throws SQLException;

    public SQLXML callableStatement_getSQLXML(CallableStatementProxy statement, int parameterIndex) throws SQLException;

    public SQLXML callableStatement_getSQLXML(CallableStatementProxy statement, String parameterName)
                                                                                                     throws SQLException;

    public String callableStatement_getNString(CallableStatementProxy statement, int parameterIndex)
                                                                                                    throws SQLException;

    public String callableStatement_getNString(CallableStatementProxy statement, String parameterName)
                                                                                                      throws SQLException;

    public java.io.Reader callableStatement_getNCharacterStream(CallableStatementProxy statement, int parameterIndex)
                                                                                                                     throws SQLException;

    public java.io.Reader callableStatement_getNCharacterStream(CallableStatementProxy statement, String parameterName)
                                                                                                                       throws SQLException;

    public java.io.Reader callableStatement_getCharacterStream(CallableStatementProxy statement, int parameterIndex)
                                                                                                                    throws SQLException;

    public java.io.Reader callableStatement_getCharacterStream(CallableStatementProxy statement, String parameterName)
                                                                                                                      throws SQLException;

    public void callableStatement_setBlob(CallableStatementProxy statement, String parameterName, Blob x)
                                                                                                         throws SQLException;

    public void callableStatement_setClob(CallableStatementProxy statement, String parameterName, Clob x)
                                                                                                         throws SQLException;

    public void callableStatement_setAsciiStream(CallableStatementProxy statement, String parameterName,
                                                 java.io.InputStream x, long length) throws SQLException;

    public void callableStatement_setBinaryStream(CallableStatementProxy statement, String parameterName,
                                                  java.io.InputStream x, long length) throws SQLException;

    public void callableStatement_setCharacterStream(CallableStatementProxy statement, String parameterName,
                                                     java.io.Reader reader, long length) throws SQLException;

    public void callableStatement_setAsciiStream(CallableStatementProxy statement, String parameterName,
                                                 java.io.InputStream x) throws SQLException;

    public void callableStatement_setBinaryStream(CallableStatementProxy statement, String parameterName,
                                                  java.io.InputStream x) throws SQLException;

    public void callableStatement_setCharacterStream(CallableStatementProxy statement, String parameterName,
                                                     java.io.Reader reader) throws SQLException;

    public void callableStatement_setNCharacterStream(CallableStatementProxy statement, String parameterName,
                                                      Reader value) throws SQLException;

    public void callableStatement_setClob(CallableStatementProxy statement, String parameterName, Reader reader)
                                                                                                                throws SQLException;

    public void callableStatement_setBlob(CallableStatementProxy statement, String parameterName,
                                          InputStream inputStream) throws SQLException;

    public void callableStatement_setNClob(CallableStatementProxy statement, String parameterName, Reader reader)
                                                                                                                 throws SQLException;

    public void clob_free(ClobProxy wrapper) throws SQLException;

    public InputStream clob_getAsciiStream(ClobProxy wrapper) throws SQLException;

    public Reader clob_getCharacterStream(ClobProxy wrapper) throws SQLException;

    public Reader clob_getCharacterStream(ClobProxy wrapper, long pos, long length) throws SQLException;

    public String clob_getSubString(ClobProxy wrapper, long pos, int length) throws SQLException;

    public long clob_length(ClobProxy wrapper) throws SQLException;

    public long clob_position(ClobProxy wrapper, String searchstr, long start) throws SQLException;

    public long clob_position(ClobProxy wrapper, Clob searchstr, long start) throws SQLException;

    public OutputStream clob_setAsciiStream(ClobProxy wrapper, long pos) throws SQLException;

    public Writer clob_setCharacterStream(ClobProxy wrapper, long pos) throws SQLException;

    public int clob_setString(ClobProxy wrapper, long pos, String str) throws SQLException;

    public int clob_setString(ClobProxy wrapper, long pos, String str, int offset, int len) throws SQLException;

    public void clob_truncate(ClobProxy wrapper, long len) throws SQLException;

    // ////
}
