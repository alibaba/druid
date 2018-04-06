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

import javax.management.NotificationBroadcasterSupport;
import java.io.InputStream;
import java.io.Reader;
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
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executor;

/**
 * 提供JdbcFilter的基本实现，使得实现一个JdbcFilter更容易。
 * 
 * @author wenshao [szujobs@hotmail.com]
 */
public abstract class FilterAdapter extends NotificationBroadcasterSupport implements Filter {

    @Override
    public void init(DataSourceProxy dataSource) {
    }

    @Override
    public void destroy() {

    }

    public void configFromProperties(Properties properties) {

    }

    @Override
    public boolean isWrapperFor(Class<?> iface) {
        return iface == this.getClass();
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) {
        if (iface == this.getClass()) {
            return (T) this;
        }
        return null;
    }

    @Override
    public Array callableStatement_getArray(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                    throws SQLException {
        return chain.callableStatement_getArray(statement, parameterIndex);
    }

    @Override
    public Array callableStatement_getArray(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                      throws SQLException {
        return chain.callableStatement_getArray(statement, parameterName);
    }

    @Override
    public BigDecimal callableStatement_getBigDecimal(FilterChain chain, CallableStatementProxy statement,
                                                      int parameterIndex) throws SQLException {
        return chain.callableStatement_getBigDecimal(statement, parameterIndex);
    }

    @Override
    public BigDecimal callableStatement_getBigDecimal(FilterChain chain, CallableStatementProxy statement,
                                                      int parameterIndex, int scale) throws SQLException {
        return chain.callableStatement_getBigDecimal(statement, parameterIndex, scale);
    }

    @Override
    public BigDecimal callableStatement_getBigDecimal(FilterChain chain, CallableStatementProxy statement,
                                                      String parameterName) throws SQLException {
        return chain.callableStatement_getBigDecimal(statement, parameterName);
    }

    @Override
    public Blob callableStatement_getBlob(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                  throws SQLException {
        return chain.callableStatement_getBlob(statement, parameterIndex);
    }

    @Override
    public Blob callableStatement_getBlob(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                    throws SQLException {
        return chain.callableStatement_getBlob(statement, parameterName);
    }

    @Override
    public boolean callableStatement_getBoolean(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                        throws SQLException {
        return chain.callableStatement_getBoolean(statement, parameterIndex);
    }

    @Override
    public boolean callableStatement_getBoolean(FilterChain chain, CallableStatementProxy statement,
                                                String parameterName) throws SQLException {
        return chain.callableStatement_getBoolean(statement, parameterName);
    }

    @Override
    public byte callableStatement_getByte(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                  throws SQLException {
        return chain.callableStatement_getByte(statement, parameterIndex);
    }

    @Override
    public byte callableStatement_getByte(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                    throws SQLException {
        return chain.callableStatement_getByte(statement, parameterName);
    }

    @Override
    public byte[] callableStatement_getBytes(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                     throws SQLException {
        return chain.callableStatement_getBytes(statement, parameterIndex);
    }

    @Override
    public byte[] callableStatement_getBytes(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                       throws SQLException {
        return chain.callableStatement_getBytes(statement, parameterName);
    }

    @Override
    public java.io.Reader callableStatement_getCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                                               int parameterIndex) throws SQLException {
        return chain.callableStatement_getCharacterStream(statement, parameterIndex);
    }

    @Override
    public java.io.Reader callableStatement_getCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                                               String parameterName) throws SQLException {
        return chain.callableStatement_getCharacterStream(statement, parameterName);
    }

    @Override
    public Clob callableStatement_getClob(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                  throws SQLException {
        return chain.callableStatement_getClob(statement, parameterIndex);
    }

    @Override
    public Clob callableStatement_getClob(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                    throws SQLException {
        return chain.callableStatement_getClob(statement, parameterName);
    }

    @Override
    public java.sql.Date callableStatement_getDate(FilterChain chain, CallableStatementProxy statement,
                                                   int parameterIndex) throws SQLException {
        return chain.callableStatement_getDate(statement, parameterIndex);
    }

    @Override
    public java.sql.Date callableStatement_getDate(FilterChain chain, CallableStatementProxy statement,
                                                   int parameterIndex, Calendar cal) throws SQLException {
        return chain.callableStatement_getDate(statement, parameterIndex, cal);
    }

    @Override
    public java.sql.Date callableStatement_getDate(FilterChain chain, CallableStatementProxy statement,
                                                   String parameterName) throws SQLException {
        return chain.callableStatement_getDate(statement, parameterName);
    }

    @Override
    public java.sql.Date callableStatement_getDate(FilterChain chain, CallableStatementProxy statement,
                                                   String parameterName, Calendar cal) throws SQLException {
        return chain.callableStatement_getDate(statement, parameterName, cal);
    }

    @Override
    public double callableStatement_getDouble(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                      throws SQLException {
        return chain.callableStatement_getDouble(statement, parameterIndex);
    }

    @Override
    public double callableStatement_getDouble(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                        throws SQLException {
        return chain.callableStatement_getDouble(statement, parameterName);
    }

    @Override
    public float callableStatement_getFloat(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                    throws SQLException {
        return chain.callableStatement_getFloat(statement, parameterIndex);
    }

    @Override
    public float callableStatement_getFloat(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                      throws SQLException {
        return chain.callableStatement_getFloat(statement, parameterName);
    }

    @Override
    public int callableStatement_getInt(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                throws SQLException {
        return chain.callableStatement_getInt(statement, parameterIndex);
    }

    @Override
    public int callableStatement_getInt(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                  throws SQLException {
        return chain.callableStatement_getInt(statement, parameterName);
    }

    @Override
    public long callableStatement_getLong(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                  throws SQLException {
        return chain.callableStatement_getLong(statement, parameterIndex);
    }

    @Override
    public long callableStatement_getLong(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                    throws SQLException {
        return chain.callableStatement_getLong(statement, parameterName);
    }

    @Override
    public java.io.Reader callableStatement_getNCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                                                int parameterIndex) throws SQLException {
        return chain.callableStatement_getNCharacterStream(statement, parameterIndex);
    }

    @Override
    public java.io.Reader callableStatement_getNCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                                                String parameterName) throws SQLException {
        return chain.callableStatement_getNCharacterStream(statement, parameterName);
    }

    @Override
    public NClob callableStatement_getNClob(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                    throws SQLException {
        return chain.callableStatement_getNClob(statement, parameterIndex);
    }

    @Override
    public NClob callableStatement_getNClob(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                      throws SQLException {
        return chain.callableStatement_getNClob(statement, parameterName);
    }

    @Override
    public String callableStatement_getNString(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                       throws SQLException {
        return chain.callableStatement_getNString(statement, parameterIndex);
    }

    @Override
    public String callableStatement_getNString(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                         throws SQLException {
        return chain.callableStatement_getNString(statement, parameterName);
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                      throws SQLException {
        return chain.callableStatement_getObject(statement, parameterIndex);
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, int parameterIndex,
                                              java.util.Map<String, Class<?>> map) throws SQLException {
        return chain.callableStatement_getObject(statement, parameterIndex, map);
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                        throws SQLException {
        return chain.callableStatement_getObject(statement, parameterName);
    }

    @Override
    public Object callableStatement_getObject(FilterChain chain, CallableStatementProxy statement,
                                              String parameterName, java.util.Map<String, Class<?>> map)
                                                                                                        throws SQLException {
        return chain.callableStatement_getObject(statement, parameterName, map);
    }

    @Override
    public Ref callableStatement_getRef(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                throws SQLException {
        return chain.callableStatement_getRef(statement, parameterIndex);
    }

    @Override
    public Ref callableStatement_getRef(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                  throws SQLException {
        return chain.callableStatement_getRef(statement, parameterName);
    }

    @Override
    public RowId callableStatement_getRowId(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                    throws SQLException {
        return chain.callableStatement_getRowId(statement, parameterIndex);
    }

    @Override
    public RowId callableStatement_getRowId(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                      throws SQLException {
        return chain.callableStatement_getRowId(statement, parameterName);
    }

    @Override
    public short callableStatement_getShort(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                    throws SQLException {
        return chain.callableStatement_getShort(statement, parameterIndex);
    }

    @Override
    public short callableStatement_getShort(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                      throws SQLException {
        return chain.callableStatement_getShort(statement, parameterName);
    }

    @Override
    public SQLXML callableStatement_getSQLXML(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                      throws SQLException {
        return chain.callableStatement_getSQLXML(statement, parameterIndex);
    }

    @Override
    public SQLXML callableStatement_getSQLXML(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                        throws SQLException {
        return chain.callableStatement_getSQLXML(statement, parameterName);
    }

    @Override
    public String callableStatement_getString(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                      throws SQLException {
        return chain.callableStatement_getString(statement, parameterIndex);
    }

    @Override
    public String callableStatement_getString(FilterChain chain, CallableStatementProxy statement, String parameterName)
                                                                                                                        throws SQLException {
        return chain.callableStatement_getString(statement, parameterName);
    }

    @Override
    public java.sql.Time callableStatement_getTime(FilterChain chain, CallableStatementProxy statement,
                                                   int parameterIndex) throws SQLException {
        return chain.callableStatement_getTime(statement, parameterIndex);
    }

    // //////////////////////////////

    @Override
    public java.sql.Time callableStatement_getTime(FilterChain chain, CallableStatementProxy statement,
                                                   int parameterIndex, Calendar cal) throws SQLException {
        return chain.callableStatement_getTime(statement, parameterIndex, cal);
    }

    @Override
    public java.sql.Time callableStatement_getTime(FilterChain chain, CallableStatementProxy statement,
                                                   String parameterName) throws SQLException {
        return chain.callableStatement_getTime(statement, parameterName);
    }

    @Override
    public java.sql.Time callableStatement_getTime(FilterChain chain, CallableStatementProxy statement,
                                                   String parameterName, Calendar cal) throws SQLException {
        return chain.callableStatement_getTime(statement, parameterName, cal);
    }

    @Override
    public java.sql.Timestamp callableStatement_getTimestamp(FilterChain chain, CallableStatementProxy statement,
                                                             int parameterIndex) throws SQLException {
        return chain.callableStatement_getTimestamp(statement, parameterIndex);
    }

    @Override
    public java.sql.Timestamp callableStatement_getTimestamp(FilterChain chain, CallableStatementProxy statement,
                                                             int parameterIndex, Calendar cal) throws SQLException {
        return chain.callableStatement_getTimestamp(statement, parameterIndex, cal);
    }

    @Override
    public java.sql.Timestamp callableStatement_getTimestamp(FilterChain chain, CallableStatementProxy statement,
                                                             String parameterName) throws SQLException {
        return chain.callableStatement_getTimestamp(statement, parameterName);
    }

    @Override
    public java.sql.Timestamp callableStatement_getTimestamp(FilterChain chain, CallableStatementProxy statement,
                                                             String parameterName, Calendar cal) throws SQLException {
        return chain.callableStatement_getTimestamp(statement, parameterName, cal);
    }

    @Override
    public java.net.URL callableStatement_getURL(FilterChain chain, CallableStatementProxy statement, int parameterIndex)
                                                                                                                         throws SQLException {
        return chain.callableStatement_getURL(statement, parameterIndex);
    }

    @Override
    public java.net.URL callableStatement_getURL(FilterChain chain, CallableStatementProxy statement,
                                                 String parameterName) throws SQLException {
        return chain.callableStatement_getURL(statement, parameterName);
    }

    // ///////////////
    @Override
    public void callableStatement_registerOutParameter(FilterChain chain, CallableStatementProxy statement,
                                                       int parameterIndex, int sqlType) throws SQLException {
        chain.callableStatement_registerOutParameter(statement, parameterIndex, sqlType);
    }

    @Override
    public void callableStatement_registerOutParameter(FilterChain chain, CallableStatementProxy statement,
                                                       int parameterIndex, int sqlType, int scale) throws SQLException {
        chain.callableStatement_registerOutParameter(statement, parameterIndex, sqlType, scale);
    }

    @Override
    public void callableStatement_registerOutParameter(FilterChain chain, CallableStatementProxy statement,
                                                       int parameterIndex, int sqlType, String typeName)
                                                                                                        throws SQLException {
        chain.callableStatement_registerOutParameter(statement, parameterIndex, sqlType, typeName);
    }

    @Override
    public void callableStatement_registerOutParameter(FilterChain chain, CallableStatementProxy statement,
                                                       String parameterName, int sqlType) throws SQLException {
        chain.callableStatement_registerOutParameter(statement, parameterName, sqlType);
    }

    @Override
    public void callableStatement_registerOutParameter(FilterChain chain, CallableStatementProxy statement,
                                                       String parameterName, int sqlType, int scale)
                                                                                                    throws SQLException {
        chain.callableStatement_registerOutParameter(statement, parameterName, sqlType, scale);
    }

    @Override
    public void callableStatement_registerOutParameter(FilterChain chain, CallableStatementProxy statement,
                                                       String parameterName, int sqlType, String typeName)
                                                                                                          throws SQLException {
        chain.callableStatement_registerOutParameter(statement, parameterName, sqlType, typeName);
    }

    @Override
    public void callableStatement_setAsciiStream(FilterChain chain, CallableStatementProxy statement,
                                                 String parameterName, java.io.InputStream x) throws SQLException {
        chain.callableStatement_setAsciiStream(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setAsciiStream(FilterChain chain, CallableStatementProxy statement,
                                                 String parameterName, java.io.InputStream x, int length)
                                                                                                         throws SQLException {
        chain.callableStatement_setAsciiStream(statement, parameterName, x, length);
    }

    @Override
    public void callableStatement_setAsciiStream(FilterChain chain, CallableStatementProxy statement,
                                                 String parameterName, java.io.InputStream x, long length)
                                                                                                          throws SQLException {
        chain.callableStatement_setAsciiStream(statement, parameterName, x, length);
    }

    @Override
    public void callableStatement_setBigDecimal(FilterChain chain, CallableStatementProxy statement,
                                                String parameterName, BigDecimal x) throws SQLException {
        chain.callableStatement_setBigDecimal(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setBinaryStream(FilterChain chain, CallableStatementProxy statement,
                                                  String parameterName, java.io.InputStream x) throws SQLException {
        chain.callableStatement_setBinaryStream(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setBinaryStream(FilterChain chain, CallableStatementProxy statement,
                                                  String parameterName, java.io.InputStream x, int length)
                                                                                                          throws SQLException {
        chain.callableStatement_setBinaryStream(statement, parameterName, x, length);
    }

    @Override
    public void callableStatement_setBinaryStream(FilterChain chain, CallableStatementProxy statement,
                                                  String parameterName, java.io.InputStream x, long length)
                                                                                                           throws SQLException {
        chain.callableStatement_setBinaryStream(statement, parameterName, x, length);
    }

    @Override
    public void callableStatement_setBlob(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                          Blob x) throws SQLException {
        chain.callableStatement_setBlob(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setBlob(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                          InputStream inputStream) throws SQLException {
        chain.callableStatement_setBlob(statement, parameterName, inputStream);
    }

    @Override
    public void callableStatement_setBlob(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                          InputStream inputStream, long length) throws SQLException {
        chain.callableStatement_setBlob(statement, parameterName, inputStream, length);
    }

    @Override
    public void callableStatement_setBoolean(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                             boolean x) throws SQLException {
        chain.callableStatement_setBoolean(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setByte(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                          byte x) throws SQLException {
        chain.callableStatement_setByte(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setBytes(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                           byte x[]) throws SQLException {
        chain.callableStatement_setBytes(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                                     String parameterName, java.io.Reader reader) throws SQLException {
        chain.callableStatement_setCharacterStream(statement, parameterName, reader);
    }

    @Override
    public void callableStatement_setCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                                     String parameterName, java.io.Reader reader, int length)
                                                                                                             throws SQLException {
        chain.callableStatement_setCharacterStream(statement, parameterName, reader, length);
    }

    @Override
    public void callableStatement_setCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                                     String parameterName, java.io.Reader reader, long length)
                                                                                                              throws SQLException {
        chain.callableStatement_setCharacterStream(statement, parameterName, reader, length);
    }

    @Override
    public void callableStatement_setClob(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                          Clob x) throws SQLException {
        chain.callableStatement_setClob(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setClob(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                          Reader reader) throws SQLException {
        chain.callableStatement_setClob(statement, parameterName, reader);
    }

    @Override
    public void callableStatement_setClob(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                          Reader reader, long length) throws SQLException {
        chain.callableStatement_setClob(statement, parameterName, reader, length);
    }

    @Override
    public void callableStatement_setDate(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                          java.sql.Date x) throws SQLException {
        chain.callableStatement_setDate(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setDate(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                          java.sql.Date x, Calendar cal) throws SQLException {
        chain.callableStatement_setDate(statement, parameterName, x, cal);
    }

    @Override
    public void callableStatement_setDouble(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                            double x) throws SQLException {
        chain.callableStatement_setDouble(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setFloat(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                           float x) throws SQLException {
        chain.callableStatement_setFloat(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setInt(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                         int x) throws SQLException {
        chain.callableStatement_setInt(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setLong(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                          long x) throws SQLException {
        chain.callableStatement_setLong(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setNCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                                      String parameterName, Reader value) throws SQLException {
        chain.callableStatement_setNCharacterStream(statement, parameterName, value);
    }

    @Override
    public void callableStatement_setNCharacterStream(FilterChain chain, CallableStatementProxy statement,
                                                      String parameterName, Reader value, long length)
                                                                                                      throws SQLException {
        chain.callableStatement_setNCharacterStream(statement, parameterName, value, length);
    }

    @Override
    public void callableStatement_setNClob(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                           NClob value) throws SQLException {
        chain.callableStatement_setNClob(statement, parameterName, value);
    }

    @Override
    public void callableStatement_setNClob(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                           Reader reader) throws SQLException {
        chain.callableStatement_setNClob(statement, parameterName, reader);
    }

    @Override
    public void callableStatement_setNClob(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                           Reader reader, long length) throws SQLException {
        chain.callableStatement_setNClob(statement, parameterName, reader, length);
    }

    @Override
    public void callableStatement_setNString(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                             String value) throws SQLException {
        chain.callableStatement_setNString(statement, parameterName, value);
    }

    @Override
    public void callableStatement_setNull(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                          int sqlType) throws SQLException {
        chain.callableStatement_setNull(statement, parameterName, sqlType);
    }

    @Override
    public void callableStatement_setNull(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                          int sqlType, String typeName) throws SQLException {
        chain.callableStatement_setNull(statement, parameterName, sqlType, typeName);
    }

    @Override
    public void callableStatement_setObject(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                            Object x) throws SQLException {
        chain.callableStatement_setObject(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setObject(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                            Object x, int targetSqlType) throws SQLException {
        chain.callableStatement_setObject(statement, parameterName, x, targetSqlType);
    }

    @Override
    public void callableStatement_setObject(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                            Object x, int targetSqlType, int scale) throws SQLException {
        chain.callableStatement_setObject(statement, parameterName, x, targetSqlType, scale);
    }

    @Override
    public void callableStatement_setRowId(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                           RowId x) throws SQLException {
        chain.callableStatement_setRowId(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setShort(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                           short x) throws SQLException {
        chain.callableStatement_setShort(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setSQLXML(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                            SQLXML xmlObject) throws SQLException {
        chain.callableStatement_setSQLXML(statement, parameterName, xmlObject);
    }

    @Override
    public void callableStatement_setString(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                            String x) throws SQLException {
        chain.callableStatement_setString(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setTime(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                          java.sql.Time x) throws SQLException {
        chain.callableStatement_setTime(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setTime(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                          java.sql.Time x, Calendar cal) throws SQLException {
        chain.callableStatement_setTime(statement, parameterName, x, cal);
    }

    @Override
    public void callableStatement_setTimestamp(FilterChain chain, CallableStatementProxy statement,
                                               String parameterName, java.sql.Timestamp x) throws SQLException {
        chain.callableStatement_setTimestamp(statement, parameterName, x);
    }

    @Override
    public void callableStatement_setTimestamp(FilterChain chain, CallableStatementProxy statement,
                                               String parameterName, java.sql.Timestamp x, Calendar cal)
                                                                                                        throws SQLException {
        chain.callableStatement_setTimestamp(statement, parameterName, x, cal);
    }

    @Override
    public void callableStatement_setURL(FilterChain chain, CallableStatementProxy statement, String parameterName,
                                         java.net.URL val) throws SQLException {
        chain.callableStatement_setURL(statement, parameterName, val);
    }

    @Override
    public boolean callableStatement_wasNull(FilterChain chain, CallableStatementProxy statement) throws SQLException {
        return chain.callableStatement_wasNull(statement);
    }

    @Override
    public void connection_clearWarnings(FilterChain chain, ConnectionProxy connection) throws SQLException {
        chain.connection_clearWarnings(connection);
    }

    @Override
    public void connection_close(FilterChain chain, ConnectionProxy connection) throws SQLException {
        chain.connection_close(connection);
    }

    @Override
    public void connection_commit(FilterChain chain, ConnectionProxy connection) throws SQLException {
        chain.connection_commit(connection);
    }

    @Override
    public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
        return chain.connection_connect(info);
    }

    @Override
    public Array connection_createArrayOf(FilterChain chain, ConnectionProxy connection, String typeName,
                                          Object[] elements) throws SQLException {
        return chain.connection_createArrayOf(connection, typeName, elements);
    }

    @Override
    public Blob connection_createBlob(FilterChain chain, ConnectionProxy connection) throws SQLException {
        return chain.connection_createBlob(connection);
    }

    @Override
    public Clob connection_createClob(FilterChain chain, ConnectionProxy connection) throws SQLException {
        return chain.connection_createClob(connection);
    }

    @Override
    public NClob connection_createNClob(FilterChain chain, ConnectionProxy connection) throws SQLException {
        return chain.connection_createNClob(connection);
    }

    @Override
    public SQLXML connection_createSQLXML(FilterChain chain, ConnectionProxy connection) throws SQLException {
        return chain.connection_createSQLXML(connection);
    }

    @Override
    public StatementProxy connection_createStatement(FilterChain chain, ConnectionProxy connection) throws SQLException {
        return chain.connection_createStatement(connection);
    }

    @Override
    public StatementProxy connection_createStatement(FilterChain chain, ConnectionProxy connection, int resultSetType,
                                                     int resultSetConcurrency) throws SQLException {
        return chain.connection_createStatement(connection, resultSetType, resultSetConcurrency);
    }

    @Override
    public StatementProxy connection_createStatement(FilterChain chain, ConnectionProxy connection, int resultSetType,
                                                     int resultSetConcurrency, int resultSetHoldability)
                                                                                                        throws SQLException {
        return chain.connection_createStatement(connection, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public Struct connection_createStruct(FilterChain chain, ConnectionProxy connection, String typeName,
                                          Object[] attributes) throws SQLException {
        return chain.connection_createStruct(connection, typeName, attributes);
    }

    @Override
    public boolean connection_getAutoCommit(FilterChain chain, ConnectionProxy connection) throws SQLException {
        return chain.connection_getAutoCommit(connection);
    }

    @Override
    public String connection_getCatalog(FilterChain chain, ConnectionProxy connection) throws SQLException {
        return chain.connection_getCatalog(connection);
    }

    @Override
    public Properties connection_getClientInfo(FilterChain chain, ConnectionProxy connection) throws SQLException {
        return chain.connection_getClientInfo(connection);
    }

    @Override
    public String connection_getClientInfo(FilterChain chain, ConnectionProxy connection, String name)
                                                                                                      throws SQLException {
        return chain.connection_getClientInfo(connection, name);
    }

    @Override
    public int connection_getHoldability(FilterChain chain, ConnectionProxy connection) throws SQLException {
        return chain.connection_getHoldability(connection);
    }

    @Override
    public DatabaseMetaData connection_getMetaData(FilterChain chain, ConnectionProxy connection) throws SQLException {
        return chain.connection_getMetaData(connection);
    }

    @Override
    public int connection_getTransactionIsolation(FilterChain chain, ConnectionProxy connection) throws SQLException {
        return chain.connection_getTransactionIsolation(connection);
    }

    @Override
    public Map<String, Class<?>> connection_getTypeMap(FilterChain chain, ConnectionProxy connection)
                                                                                                     throws SQLException {
        return chain.connection_getTypeMap(connection);
    }

    @Override
    public SQLWarning connection_getWarnings(FilterChain chain, ConnectionProxy connection) throws SQLException {
        return chain.connection_getWarnings(connection);
    }

    @Override
    public boolean connection_isClosed(FilterChain chain, ConnectionProxy connection) throws SQLException {
        return chain.connection_isClosed(connection);
    }

    @Override
    public boolean connection_isReadOnly(FilterChain chain, ConnectionProxy connection) throws SQLException {
        return chain.connection_isReadOnly(connection);
    }

    @Override
    public boolean connection_isValid(FilterChain chain, ConnectionProxy connection, int timeout) throws SQLException {
        return chain.connection_isValid(connection, timeout);
    }

    @Override
    public String connection_nativeSQL(FilterChain chain, ConnectionProxy connection, String sql) throws SQLException {
        return chain.connection_nativeSQL(connection, sql);
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql)
                                                                                                                   throws SQLException {
        return chain.connection_prepareCall(connection, sql);
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql,
                                                         int resultSetType, int resultSetConcurrency)
                                                                                                     throws SQLException {
        return chain.connection_prepareCall(connection, sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql,
                                                         int resultSetType, int resultSetConcurrency,
                                                         int resultSetHoldability) throws SQLException {
        return chain.connection_prepareCall(connection, sql, resultSetType, resultSetConcurrency, resultSetHoldability);
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection, String sql)
                                                                                                                        throws SQLException {
        return chain.connection_prepareStatement(connection, sql);
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int autoGeneratedKeys) throws SQLException {
        return chain.connection_prepareStatement(connection, sql, autoGeneratedKeys);
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int resultSetType, int resultSetConcurrency)
                                                                                                                      throws SQLException {
        return chain.connection_prepareStatement(connection, sql, resultSetType, resultSetConcurrency);
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int resultSetType, int resultSetConcurrency,
                                                              int resultSetHoldability) throws SQLException {
        return chain.connection_prepareStatement(connection, sql, resultSetType, resultSetConcurrency,
                                                 resultSetHoldability);
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int[] columnIndexes) throws SQLException {
        return chain.connection_prepareStatement(connection, sql, columnIndexes);
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, String[] columnNames) throws SQLException {
        return chain.connection_prepareStatement(connection, sql, columnNames);
    }

    @Override
    public void connection_releaseSavepoint(FilterChain chain, ConnectionProxy connection, Savepoint savepoint)
                                                                                                               throws SQLException {
        chain.connection_releaseSavepoint(connection, savepoint);
    }

    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection) throws SQLException {
        chain.connection_rollback(connection);
    }

    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection, Savepoint savepoint)
                                                                                                       throws SQLException {
        chain.connection_rollback(connection, savepoint);
    }

    @Override
    public void connection_setAutoCommit(FilterChain chain, ConnectionProxy connection, boolean autoCommit)
                                                                                                           throws SQLException {
        chain.connection_setAutoCommit(connection, autoCommit);
    }

    @Override
    public void connection_setCatalog(FilterChain chain, ConnectionProxy connection, String catalog)
                                                                                                    throws SQLException {
        chain.connection_setCatalog(connection, catalog);
    }

    @Override
    public void connection_setClientInfo(FilterChain chain, ConnectionProxy connection, Properties properties)
                                                                                                              throws SQLClientInfoException {
        chain.connection_setClientInfo(connection, properties);
    }

    @Override
    public void connection_setClientInfo(FilterChain chain, ConnectionProxy connection, String name, String value)
                                                                                                                  throws SQLClientInfoException {
        chain.connection_setClientInfo(connection, name, value);
    }

    @Override
    public void connection_setHoldability(FilterChain chain, ConnectionProxy connection, int holdability)
                                                                                                         throws SQLException {
        chain.connection_setHoldability(connection, holdability);
    }

    @Override
    public void connection_setReadOnly(FilterChain chain, ConnectionProxy connection, boolean readOnly)
                                                                                                       throws SQLException {
        chain.connection_setReadOnly(connection, readOnly);
    }

    @Override
    public Savepoint connection_setSavepoint(FilterChain chain, ConnectionProxy connection) throws SQLException {
        return chain.connection_setSavepoint(connection);
    }

    @Override
    public Savepoint connection_setSavepoint(FilterChain chain, ConnectionProxy connection, String name)
                                                                                                        throws SQLException {
        return chain.connection_setSavepoint(connection, name);
    }

    @Override
    public void connection_setTransactionIsolation(FilterChain chain, ConnectionProxy connection, int level)
                                                                                                            throws SQLException {
        chain.connection_setTransactionIsolation(connection, level);
    }

    @Override
    public void connection_setTypeMap(FilterChain chain, ConnectionProxy connection, Map<String, Class<?>> map)
                                                                                                               throws SQLException {
        chain.connection_setTypeMap(connection, map);
    }

    @Override
    public String connection_getSchema(FilterChain chain, ConnectionProxy connection) throws SQLException {
        return chain.connection_getSchema(connection);
    }

    @Override
    public void connection_setSchema(FilterChain chain, ConnectionProxy connection, String schema) throws SQLException {
        chain.connection_setSchema(connection, schema);
    }

    public void connection_abort(FilterChain chain, ConnectionProxy connection, Executor executor) throws SQLException {
        chain.connection_abort(connection, executor);
    }

    public void connection_setNetworkTimeout(FilterChain chain, ConnectionProxy connection, Executor executor, int milliseconds) throws SQLException {
        chain.connection_setNetworkTimeout(connection, executor, milliseconds);
    }
    public int connection_getNetworkTimeout(FilterChain chain, ConnectionProxy connection) throws SQLException {
        return chain.connection_getNetworkTimeout(connection);
    }

    @Override
    public boolean isWrapperFor(FilterChain chain, Wrapper wrapper, Class<?> iface) throws SQLException {
        return chain.isWrapperFor(wrapper, iface);
    }

    @Override
    public void preparedStatement_addBatch(FilterChain chain, PreparedStatementProxy statement) throws SQLException {
        chain.preparedStatement_addBatch(statement);
    }

    @Override
    public void preparedStatement_clearParameters(FilterChain chain, PreparedStatementProxy statement)
                                                                                                      throws SQLException {
        chain.preparedStatement_clearParameters(statement);
    }

    @Override
    public boolean preparedStatement_execute(FilterChain chain, PreparedStatementProxy statement) throws SQLException {
        return chain.preparedStatement_execute(statement);
    }

    @Override
    public ResultSetProxy preparedStatement_executeQuery(FilterChain chain, PreparedStatementProxy statement)
                                                                                                             throws SQLException {
        return chain.preparedStatement_executeQuery(statement);
    }

    @Override
    public int preparedStatement_executeUpdate(FilterChain chain, PreparedStatementProxy statement) throws SQLException {
        return chain.preparedStatement_executeUpdate(statement);
    }

    @Override
    public ResultSetMetaData preparedStatement_getMetaData(FilterChain chain, PreparedStatementProxy statement)
                                                                                                               throws SQLException {
        return chain.preparedStatement_getMetaData(statement);
    }

    @Override
    public ParameterMetaData preparedStatement_getParameterMetaData(FilterChain chain, PreparedStatementProxy statement)
                                                                                                                        throws SQLException {
        return chain.preparedStatement_getParameterMetaData(statement);
    }

    @Override
    public void preparedStatement_setArray(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           Array x) throws SQLException {
        chain.preparedStatement_setArray(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setAsciiStream(FilterChain chain, PreparedStatementProxy statement,
                                                 int parameterIndex, java.io.InputStream x) throws SQLException {
        chain.preparedStatement_setAsciiStream(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setAsciiStream(FilterChain chain, PreparedStatementProxy statement,
                                                 int parameterIndex, java.io.InputStream x, int length)
                                                                                                       throws SQLException {
        chain.preparedStatement_setAsciiStream(statement, parameterIndex, x, length);
    }

    @Override
    public void preparedStatement_setAsciiStream(FilterChain chain, PreparedStatementProxy statement,
                                                 int parameterIndex, java.io.InputStream x, long length)
                                                                                                        throws SQLException {
        chain.preparedStatement_setAsciiStream(statement, parameterIndex, x, length);
    }

    @Override
    public void preparedStatement_setBigDecimal(FilterChain chain, PreparedStatementProxy statement,
                                                int parameterIndex, BigDecimal x) throws SQLException {
        chain.preparedStatement_setBigDecimal(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setBinaryStream(FilterChain chain, PreparedStatementProxy statement,
                                                  int parameterIndex, java.io.InputStream x) throws SQLException {
        chain.preparedStatement_setBinaryStream(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setBinaryStream(FilterChain chain, PreparedStatementProxy statement,
                                                  int parameterIndex, java.io.InputStream x, int length)
                                                                                                        throws SQLException {
        chain.preparedStatement_setBinaryStream(statement, parameterIndex, x, length);
    }

    @Override
    public void preparedStatement_setBinaryStream(FilterChain chain, PreparedStatementProxy statement,
                                                  int parameterIndex, java.io.InputStream x, long length)
                                                                                                         throws SQLException {
        chain.preparedStatement_setBinaryStream(statement, parameterIndex, x, length);
    }

    @Override
    public void preparedStatement_setBlob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          Blob x) throws SQLException {
        chain.preparedStatement_setBlob(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setBlob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          InputStream inputStream) throws SQLException {
        chain.preparedStatement_setBlob(statement, parameterIndex, inputStream);
    }

    @Override
    public void preparedStatement_setBlob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          InputStream inputStream, long length) throws SQLException {
        chain.preparedStatement_setBlob(statement, parameterIndex, inputStream, length);
    }

    @Override
    public void preparedStatement_setBoolean(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                             boolean x) throws SQLException {
        chain.preparedStatement_setBoolean(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setByte(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          byte x) throws SQLException {
        chain.preparedStatement_setByte(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setBytes(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           byte x[]) throws SQLException {
        chain.preparedStatement_setBytes(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setCharacterStream(FilterChain chain, PreparedStatementProxy statement,
                                                     int parameterIndex, java.io.Reader reader) throws SQLException {
        chain.preparedStatement_setCharacterStream(statement, parameterIndex, reader);
    }

    @Override
    public void preparedStatement_setCharacterStream(FilterChain chain, PreparedStatementProxy statement,
                                                     int parameterIndex, java.io.Reader reader, int length)
                                                                                                           throws SQLException {
        chain.preparedStatement_setCharacterStream(statement, parameterIndex, reader, length);
    }

    @Override
    public void preparedStatement_setCharacterStream(FilterChain chain, PreparedStatementProxy statement,
                                                     int parameterIndex, java.io.Reader reader, long length)
                                                                                                            throws SQLException {
        chain.preparedStatement_setCharacterStream(statement, parameterIndex, reader, length);
    }

    @Override
    public void preparedStatement_setClob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          Clob x) throws SQLException {
        chain.preparedStatement_setClob(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setClob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          Reader reader) throws SQLException {
        chain.preparedStatement_setClob(statement, parameterIndex, reader);
    }

    @Override
    public void preparedStatement_setClob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          Reader reader, long length) throws SQLException {
        chain.preparedStatement_setClob(statement, parameterIndex, reader, length);
    }

    @Override
    public void preparedStatement_setDate(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          java.sql.Date x) throws SQLException {
        chain.preparedStatement_setDate(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setDate(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          java.sql.Date x, Calendar cal) throws SQLException {
        chain.preparedStatement_setDate(statement, parameterIndex, x, cal);
    }

    @Override
    public void preparedStatement_setDouble(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                            double x) throws SQLException {
        chain.preparedStatement_setDouble(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setFloat(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           float x) throws SQLException {
        chain.preparedStatement_setFloat(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setInt(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, int x)
                                                                                                                        throws SQLException {
        chain.preparedStatement_setInt(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setLong(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          long x) throws SQLException {
        chain.preparedStatement_setLong(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setNCharacterStream(FilterChain chain, PreparedStatementProxy statement,
                                                      int parameterIndex, Reader value) throws SQLException {
        chain.preparedStatement_setNCharacterStream(statement, parameterIndex, value);
    }

    @Override
    public void preparedStatement_setNCharacterStream(FilterChain chain, PreparedStatementProxy statement,
                                                      int parameterIndex, Reader value, long length)
                                                                                                    throws SQLException {
        chain.preparedStatement_setNCharacterStream(statement, parameterIndex, value, length);
    }

    @Override
    public void preparedStatement_setNClob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           NClob value) throws SQLException {
        chain.preparedStatement_setNClob(statement, parameterIndex, value);
    }

    @Override
    public void preparedStatement_setNClob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           Reader reader) throws SQLException {
        chain.preparedStatement_setNClob(statement, parameterIndex, reader);
    }

    @Override
    public void preparedStatement_setNClob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           Reader reader, long length) throws SQLException {
        chain.preparedStatement_setNClob(statement, parameterIndex, reader, length);
    }

    @Override
    public void preparedStatement_setNString(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                             String value) throws SQLException {
        chain.preparedStatement_setNString(statement, parameterIndex, value);
    }

    @Override
    public void preparedStatement_setNull(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          int sqlType) throws SQLException {
        chain.preparedStatement_setNull(statement, parameterIndex, sqlType);
    }

    @Override
    public void preparedStatement_setNull(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          int sqlType, String typeName) throws SQLException {
        chain.preparedStatement_setNull(statement, parameterIndex, sqlType, typeName);
    }

    @Override
    public void preparedStatement_setObject(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                            Object x) throws SQLException {
        chain.preparedStatement_setObject(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setObject(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                            Object x, int targetSqlType) throws SQLException {
        chain.preparedStatement_setObject(statement, parameterIndex, x, targetSqlType);
    }

    @Override
    public void preparedStatement_setObject(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                            Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        chain.preparedStatement_setObject(statement, parameterIndex, x, targetSqlType, scaleOrLength);
    }

    @Override
    public void preparedStatement_setRef(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, Ref x)
                                                                                                                        throws SQLException {
        chain.preparedStatement_setRef(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setRowId(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           RowId x) throws SQLException {
        chain.preparedStatement_setRowId(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setShort(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           short x) throws SQLException {
        chain.preparedStatement_setShort(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setSQLXML(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                            SQLXML xmlObject) throws SQLException {
        chain.preparedStatement_setSQLXML(statement, parameterIndex, xmlObject);
    }

    @Override
    public void preparedStatement_setString(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                            String x) throws SQLException {
        chain.preparedStatement_setString(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setTime(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          java.sql.Time x) throws SQLException {
        chain.preparedStatement_setTime(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setTime(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          java.sql.Time x, Calendar cal) throws SQLException {
        chain.preparedStatement_setTime(statement, parameterIndex, x, cal);
    }

    @Override
    public void preparedStatement_setTimestamp(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                               java.sql.Timestamp x) throws SQLException {
        chain.preparedStatement_setTimestamp(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setTimestamp(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                               java.sql.Timestamp x, Calendar cal) throws SQLException {
        chain.preparedStatement_setTimestamp(statement, parameterIndex, x, cal);
    }

    @Override
    public void preparedStatement_setUnicodeStream(FilterChain chain, PreparedStatementProxy statement,
                                                   int parameterIndex, java.io.InputStream x, int length)
                                                                                                         throws SQLException {
        chain.preparedStatement_setUnicodeStream(statement, parameterIndex, x, length);
    }

    @Override
    public void preparedStatement_setURL(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                         java.net.URL x) throws SQLException {
        chain.preparedStatement_setURL(statement, parameterIndex, x);
    }

    @Override
    public boolean resultSet_absolute(FilterChain chain, ResultSetProxy result, int row) throws SQLException {
        return chain.resultSet_absolute(result, row);
    }

    @Override
    public void resultSet_afterLast(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        chain.resultSet_afterLast(resultSet);
    }

    @Override
    public void resultSet_beforeFirst(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        chain.resultSet_beforeFirst(resultSet);
    }

    @Override
    public void resultSet_cancelRowUpdates(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        chain.resultSet_cancelRowUpdates(resultSet);
    }

    @Override
    public void resultSet_clearWarnings(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        chain.resultSet_clearWarnings(resultSet);
    }

    @Override
    public void resultSet_close(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        chain.resultSet_close(resultSet);
    }

    @Override
    public void resultSet_deleteRow(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        chain.resultSet_deleteRow(resultSet);
    }

    @Override
    public int resultSet_findColumn(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return chain.resultSet_findColumn(result, columnLabel);
    }

    @Override
    public boolean resultSet_first(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_first(resultSet);
    }

    @Override
    public Array resultSet_getArray(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getArray(result, columnIndex);
    }

    @Override
    public Array resultSet_getArray(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return chain.resultSet_getArray(result, columnLabel);
    }

    @Override
    public java.io.InputStream resultSet_getAsciiStream(FilterChain chain, ResultSetProxy result, int columnIndex)
                                                                                                                  throws SQLException {
        return chain.resultSet_getAsciiStream(result, columnIndex);
    }

    @Override
    public java.io.InputStream resultSet_getAsciiStream(FilterChain chain, ResultSetProxy result, String columnLabel)
                                                                                                                     throws SQLException {
        return chain.resultSet_getAsciiStream(result, columnLabel);
    }

    @Override
    public BigDecimal resultSet_getBigDecimal(FilterChain chain, ResultSetProxy result, int columnIndex)
                                                                                                        throws SQLException {
        return chain.resultSet_getBigDecimal(result, columnIndex);
    }

    @Override
    public BigDecimal resultSet_getBigDecimal(FilterChain chain, ResultSetProxy result, int columnIndex, int scale)
                                                                                                                   throws SQLException {
        return chain.resultSet_getBigDecimal(result, columnIndex, scale);
    }

    @Override
    public BigDecimal resultSet_getBigDecimal(FilterChain chain, ResultSetProxy result, String columnLabel)
                                                                                                           throws SQLException {
        return chain.resultSet_getBigDecimal(result, columnLabel);
    }

    @Override
    public BigDecimal resultSet_getBigDecimal(FilterChain chain, ResultSetProxy result, String columnLabel, int scale)
                                                                                                                      throws SQLException {
        return chain.resultSet_getBigDecimal(result, columnLabel, scale);
    }

    @Override
    public java.io.InputStream resultSet_getBinaryStream(FilterChain chain, ResultSetProxy result, int columnIndex)
                                                                                                                   throws SQLException {
        return chain.resultSet_getBinaryStream(result, columnIndex);
    }

    @Override
    public java.io.InputStream resultSet_getBinaryStream(FilterChain chain, ResultSetProxy result, String columnLabel)
                                                                                                                      throws SQLException {
        return chain.resultSet_getBinaryStream(result, columnLabel);
    }

    @Override
    public Blob resultSet_getBlob(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getBlob(result, columnIndex);
    }

    @Override
    public Blob resultSet_getBlob(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return chain.resultSet_getBlob(result, columnLabel);
    }

    @Override
    public boolean resultSet_getBoolean(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getBoolean(result, columnIndex);
    }

    @Override
    public boolean resultSet_getBoolean(FilterChain chain, ResultSetProxy result, String columnLabel)
                                                                                                     throws SQLException {
        return chain.resultSet_getBoolean(result, columnLabel);
    }

    @Override
    public byte resultSet_getByte(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getByte(result, columnIndex);
    }

    @Override
    public byte resultSet_getByte(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return chain.resultSet_getByte(result, columnLabel);
    }

    @Override
    public byte[] resultSet_getBytes(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getBytes(result, columnIndex);
    }

    @Override
    public byte[] resultSet_getBytes(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return chain.resultSet_getBytes(result, columnLabel);
    }

    @Override
    public java.io.Reader resultSet_getCharacterStream(FilterChain chain, ResultSetProxy result, int columnIndex)
                                                                                                                 throws SQLException {
        return chain.resultSet_getCharacterStream(result, columnIndex);
    }

    @Override
    public java.io.Reader resultSet_getCharacterStream(FilterChain chain, ResultSetProxy result, String columnLabel)
                                                                                                                    throws SQLException {
        return chain.resultSet_getCharacterStream(result, columnLabel);
    }

    @Override
    public Clob resultSet_getClob(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getClob(result, columnIndex);
    }

    @Override
    public Clob resultSet_getClob(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return chain.resultSet_getClob(result, columnLabel);
    }

    @Override
    public int resultSet_getConcurrency(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_getConcurrency(resultSet);
    }

    @Override
    public String resultSet_getCursorName(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_getCursorName(resultSet);
    }

    @Override
    public java.sql.Date resultSet_getDate(FilterChain chain, ResultSetProxy result, int columnIndex)
                                                                                                     throws SQLException {
        return chain.resultSet_getDate(result, columnIndex);
    }

    @Override
    public java.sql.Date resultSet_getDate(FilterChain chain, ResultSetProxy result, int columnIndex, Calendar cal)
                                                                                                                   throws SQLException {
        return chain.resultSet_getDate(result, columnIndex, cal);
    }

    @Override
    public java.sql.Date resultSet_getDate(FilterChain chain, ResultSetProxy result, String columnLabel)
                                                                                                        throws SQLException {
        return chain.resultSet_getDate(result, columnLabel);
    }

    @Override
    public java.sql.Date resultSet_getDate(FilterChain chain, ResultSetProxy result, String columnLabel, Calendar cal)
                                                                                                                      throws SQLException {
        return chain.resultSet_getDate(result, columnLabel, cal);
    }

    @Override
    public double resultSet_getDouble(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getDouble(result, columnIndex);
    }

    @Override
    public double resultSet_getDouble(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return chain.resultSet_getDouble(result, columnLabel);
    }

    @Override
    public int resultSet_getFetchDirection(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_getFetchDirection(resultSet);
    }

    @Override
    public int resultSet_getFetchSize(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_getFetchSize(resultSet);
    }

    @Override
    public float resultSet_getFloat(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getFloat(result, columnIndex);
    }

    @Override
    public float resultSet_getFloat(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return chain.resultSet_getFloat(result, columnLabel);
    }

    @Override
    public int resultSet_getHoldability(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_getHoldability(resultSet);
    }

    @Override
    public int resultSet_getInt(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getInt(result, columnIndex);
    }

    @Override
    public int resultSet_getInt(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return chain.resultSet_getInt(result, columnLabel);
    }

    @Override
    public long resultSet_getLong(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getLong(result, columnIndex);
    }

    @Override
    public long resultSet_getLong(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return chain.resultSet_getLong(result, columnLabel);
    }

    @Override
    public ResultSetMetaData resultSet_getMetaData(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_getMetaData(resultSet);
    }

    @Override
    public java.io.Reader resultSet_getNCharacterStream(FilterChain chain, ResultSetProxy result, int columnIndex)
                                                                                                                  throws SQLException {
        return chain.resultSet_getNCharacterStream(result, columnIndex);
    }

    @Override
    public java.io.Reader resultSet_getNCharacterStream(FilterChain chain, ResultSetProxy result, String columnLabel)
                                                                                                                     throws SQLException {
        return chain.resultSet_getNCharacterStream(result, columnLabel);
    }

    @Override
    public NClob resultSet_getNClob(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getNClob(result, columnIndex);
    }

    @Override
    public NClob resultSet_getNClob(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return chain.resultSet_getNClob(result, columnLabel);
    }

    @Override
    public String resultSet_getNString(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getNString(result, columnIndex);
    }

    @Override
    public String resultSet_getNString(FilterChain chain, ResultSetProxy result, String columnLabel)
                                                                                                    throws SQLException {
        return chain.resultSet_getNString(result, columnLabel);
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getObject(result, columnIndex);
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, int columnIndex,
                                      java.util.Map<String, Class<?>> map) throws SQLException {
        return chain.resultSet_getObject(result, columnIndex, map);
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return chain.resultSet_getObject(result, columnLabel);
    }

    @Override
    public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, String columnLabel,
                                      java.util.Map<String, Class<?>> map) throws SQLException {
        return chain.resultSet_getObject(result, columnLabel, map);
    }

    @Override
    public Ref resultSet_getRef(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getRef(result, columnIndex);
    }

    @Override
    public Ref resultSet_getRef(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return chain.resultSet_getRef(result, columnLabel);
    }

    @Override
    public int resultSet_getRow(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_getRow(resultSet);
    }

    // ////////////////

    @Override
    public RowId resultSet_getRowId(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getRowId(result, columnIndex);
    }

    @Override
    public RowId resultSet_getRowId(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return chain.resultSet_getRowId(result, columnLabel);
    }

    @Override
    public short resultSet_getShort(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getShort(result, columnIndex);
    }

    @Override
    public short resultSet_getShort(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return chain.resultSet_getShort(result, columnLabel);
    }

    @Override
    public SQLXML resultSet_getSQLXML(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getSQLXML(result, columnIndex);
    }

    @Override
    public SQLXML resultSet_getSQLXML(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return chain.resultSet_getSQLXML(result, columnLabel);
    }

    @Override
    public Statement resultSet_getStatement(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_getStatement(resultSet);
    }

    @Override
    public String resultSet_getString(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getString(result, columnIndex);
    }

    @Override
    public String resultSet_getString(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        return chain.resultSet_getString(result, columnLabel);
    }

    @Override
    public java.sql.Time resultSet_getTime(FilterChain chain, ResultSetProxy result, int columnIndex)
                                                                                                     throws SQLException {
        return chain.resultSet_getTime(result, columnIndex);
    }

    @Override
    public java.sql.Time resultSet_getTime(FilterChain chain, ResultSetProxy result, int columnIndex, Calendar cal)
                                                                                                                   throws SQLException {
        return chain.resultSet_getTime(result, columnIndex, cal);
    }

    @Override
    public java.sql.Time resultSet_getTime(FilterChain chain, ResultSetProxy result, String columnLabel)
                                                                                                        throws SQLException {
        return chain.resultSet_getTime(result, columnLabel);
    }

    @Override
    public java.sql.Time resultSet_getTime(FilterChain chain, ResultSetProxy result, String columnLabel, Calendar cal)
                                                                                                                      throws SQLException {
        return chain.resultSet_getTime(result, columnLabel, cal);
    }

    @Override
    public java.sql.Timestamp resultSet_getTimestamp(FilterChain chain, ResultSetProxy result, int columnIndex)
                                                                                                               throws SQLException {
        return chain.resultSet_getTimestamp(result, columnIndex);
    }

    @Override
    public java.sql.Timestamp resultSet_getTimestamp(FilterChain chain, ResultSetProxy result, int columnIndex,
                                                     Calendar cal) throws SQLException {
        return chain.resultSet_getTimestamp(result, columnIndex, cal);
    }

    @Override
    public java.sql.Timestamp resultSet_getTimestamp(FilterChain chain, ResultSetProxy result, String columnLabel)
                                                                                                                  throws SQLException {
        return chain.resultSet_getTimestamp(result, columnLabel);
    }

    @Override
    public java.sql.Timestamp resultSet_getTimestamp(FilterChain chain, ResultSetProxy result, String columnLabel,
                                                     Calendar cal) throws SQLException {
        return chain.resultSet_getTimestamp(result, columnLabel, cal);
    }

    @Override
    public int resultSet_getType(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_getType(resultSet);
    }

    @Override
    public java.io.InputStream resultSet_getUnicodeStream(FilterChain chain, ResultSetProxy result, int columnIndex)
                                                                                                                    throws SQLException {
        return chain.resultSet_getUnicodeStream(result, columnIndex);
    }

    @Override
    public java.io.InputStream resultSet_getUnicodeStream(FilterChain chain, ResultSetProxy result, String columnLabel)
                                                                                                                       throws SQLException {
        return chain.resultSet_getUnicodeStream(result, columnLabel);
    }

    @Override
    public java.net.URL resultSet_getURL(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        return chain.resultSet_getURL(result, columnIndex);
    }

    @Override
    public java.net.URL resultSet_getURL(FilterChain chain, ResultSetProxy result, String columnLabel)
                                                                                                      throws SQLException {
        return chain.resultSet_getURL(result, columnLabel);
    }

    @Override
    public SQLWarning resultSet_getWarnings(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_getWarnings(resultSet);
    }

    @Override
    public void resultSet_insertRow(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        chain.resultSet_insertRow(resultSet);
    }

    @Override
    public boolean resultSet_isAfterLast(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_isAfterLast(resultSet);
    }

    @Override
    public boolean resultSet_isBeforeFirst(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_isBeforeFirst(resultSet);
    }

    @Override
    public boolean resultSet_isClosed(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_isClosed(resultSet);
    }

    @Override
    public boolean resultSet_isFirst(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_isFirst(resultSet);
    }

    @Override
    public boolean resultSet_isLast(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_isLast(resultSet);
    }

    @Override
    public boolean resultSet_last(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_last(resultSet);
    }

    @Override
    public void resultSet_moveToCurrentRow(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        chain.resultSet_moveToCurrentRow(resultSet);
    }

    @Override
    public void resultSet_moveToInsertRow(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        chain.resultSet_moveToInsertRow(resultSet);
    }

    @Override
    public boolean resultSet_next(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_next(resultSet);
    }

    @Override
    public boolean resultSet_previous(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_previous(resultSet);
    }

    @Override
    public void resultSet_refreshRow(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        chain.resultSet_refreshRow(resultSet);
    }

    @Override
    public boolean resultSet_relative(FilterChain chain, ResultSetProxy result, int rows) throws SQLException {
        return chain.resultSet_relative(result, rows);
    }

    @Override
    public boolean resultSet_rowDeleted(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_rowDeleted(resultSet);
    }

    @Override
    public boolean resultSet_rowInserted(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_rowInserted(resultSet);
    }

    @Override
    public boolean resultSet_rowUpdated(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_rowUpdated(resultSet);
    }

    @Override
    public void resultSet_setFetchDirection(FilterChain chain, ResultSetProxy result, int direction)
                                                                                                    throws SQLException {
        chain.resultSet_setFetchDirection(result, direction);
    }

    @Override
    public void resultSet_setFetchSize(FilterChain chain, ResultSetProxy result, int rows) throws SQLException {
        chain.resultSet_setFetchSize(result, rows);
    }

    @Override
    public void resultSet_updateArray(FilterChain chain, ResultSetProxy result, int columnIndex, java.sql.Array x)
                                                                                                                  throws SQLException {
        chain.resultSet_updateArray(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateArray(FilterChain chain, ResultSetProxy result, String columnLabel, java.sql.Array x)
                                                                                                                     throws SQLException {
        chain.resultSet_updateArray(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateAsciiStream(FilterChain chain, ResultSetProxy result, int columnIndex,
                                            java.io.InputStream x) throws SQLException {
        chain.resultSet_updateAsciiStream(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateAsciiStream(FilterChain chain, ResultSetProxy result, int columnIndex,
                                            java.io.InputStream x, int length) throws SQLException {
        chain.resultSet_updateAsciiStream(result, columnIndex, x, length);
    }

    @Override
    public void resultSet_updateAsciiStream(FilterChain chain, ResultSetProxy result, int columnIndex,
                                            java.io.InputStream x, long length) throws SQLException {
        chain.resultSet_updateAsciiStream(result, columnIndex, x, length);
    }

    @Override
    public void resultSet_updateAsciiStream(FilterChain chain, ResultSetProxy result, String columnLabel,
                                            java.io.InputStream x) throws SQLException {
        chain.resultSet_updateAsciiStream(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateAsciiStream(FilterChain chain, ResultSetProxy result, String columnLabel,
                                            java.io.InputStream x, int length) throws SQLException {
        chain.resultSet_updateAsciiStream(result, columnLabel, x, length);
    }

    @Override
    public void resultSet_updateAsciiStream(FilterChain chain, ResultSetProxy result, String columnLabel,
                                            java.io.InputStream x, long length) throws SQLException {
        chain.resultSet_updateAsciiStream(result, columnLabel, x, length);
    }

    @Override
    public void resultSet_updateBigDecimal(FilterChain chain, ResultSetProxy result, int columnIndex, BigDecimal x)
                                                                                                                   throws SQLException {
        chain.resultSet_updateBigDecimal(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateBigDecimal(FilterChain chain, ResultSetProxy result, String columnLabel, BigDecimal x)
                                                                                                                      throws SQLException {
        chain.resultSet_updateBigDecimal(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateBinaryStream(FilterChain chain, ResultSetProxy result, int columnIndex,
                                             java.io.InputStream x) throws SQLException {
        chain.resultSet_updateBinaryStream(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateBinaryStream(FilterChain chain, ResultSetProxy result, int columnIndex,
                                             java.io.InputStream x, int length) throws SQLException {
        chain.resultSet_updateBinaryStream(result, columnIndex, x, length);
    }

    @Override
    public void resultSet_updateBinaryStream(FilterChain chain, ResultSetProxy result, int columnIndex,
                                             java.io.InputStream x, long length) throws SQLException {
        chain.resultSet_updateBinaryStream(result, columnIndex, x, length);
    }

    @Override
    public void resultSet_updateBinaryStream(FilterChain chain, ResultSetProxy result, String columnLabel,
                                             java.io.InputStream x) throws SQLException {
        chain.resultSet_updateBinaryStream(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateBinaryStream(FilterChain chain, ResultSetProxy result, String columnLabel,
                                             java.io.InputStream x, int length) throws SQLException {
        chain.resultSet_updateBinaryStream(result, columnLabel, x, length);
    }

    @Override
    public void resultSet_updateBinaryStream(FilterChain chain, ResultSetProxy result, String columnLabel,
                                             java.io.InputStream x, long length) throws SQLException {
        chain.resultSet_updateBinaryStream(result, columnLabel, x, length);
    }

    @Override
    public void resultSet_updateBlob(FilterChain chain, ResultSetProxy result, int columnIndex, InputStream inputStream)
                                                                                                                        throws SQLException {
        chain.resultSet_updateBlob(result, columnIndex, inputStream);
    }

    @Override
    public void resultSet_updateBlob(FilterChain chain, ResultSetProxy result, int columnIndex,
                                     InputStream inputStream, long length) throws SQLException {
        chain.resultSet_updateBlob(result, columnIndex, inputStream, length);
    }

    @Override
    public void resultSet_updateBlob(FilterChain chain, ResultSetProxy result, int columnIndex, java.sql.Blob x)
                                                                                                                throws SQLException {
        chain.resultSet_updateBlob(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateBlob(FilterChain chain, ResultSetProxy result, String columnLabel,
                                     InputStream inputStream) throws SQLException {
        chain.resultSet_updateBlob(result, columnLabel, inputStream);
    }

    @Override
    public void resultSet_updateBlob(FilterChain chain, ResultSetProxy result, String columnLabel,
                                     InputStream inputStream, long length) throws SQLException {
        chain.resultSet_updateBlob(result, columnLabel, inputStream, length);
    }

    @Override
    public void resultSet_updateBlob(FilterChain chain, ResultSetProxy result, String columnLabel, java.sql.Blob x)
                                                                                                                   throws SQLException {
        chain.resultSet_updateBlob(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateBoolean(FilterChain chain, ResultSetProxy result, int columnIndex, boolean x)
                                                                                                             throws SQLException {
        chain.resultSet_updateBoolean(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateBoolean(FilterChain chain, ResultSetProxy result, String columnLabel, boolean x)
                                                                                                                throws SQLException {
        chain.resultSet_updateBoolean(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateByte(FilterChain chain, ResultSetProxy result, int columnIndex, byte x)
                                                                                                       throws SQLException {
        chain.resultSet_updateByte(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateByte(FilterChain chain, ResultSetProxy result, String columnLabel, byte x)
                                                                                                          throws SQLException {
        chain.resultSet_updateByte(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateBytes(FilterChain chain, ResultSetProxy result, int columnIndex, byte[] x)
                                                                                                          throws SQLException {
        chain.resultSet_updateBytes(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateBytes(FilterChain chain, ResultSetProxy result, String columnLabel, byte[] x)
                                                                                                             throws SQLException {
        chain.resultSet_updateBytes(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateCharacterStream(FilterChain chain, ResultSetProxy result, int columnIndex,
                                                java.io.Reader x) throws SQLException {
        chain.resultSet_updateCharacterStream(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateCharacterStream(FilterChain chain, ResultSetProxy result, int columnIndex,
                                                java.io.Reader x, int length) throws SQLException {
        chain.resultSet_updateCharacterStream(result, columnIndex, x, length);
    }

    @Override
    public void resultSet_updateCharacterStream(FilterChain chain, ResultSetProxy result, int columnIndex,
                                                java.io.Reader x, long length) throws SQLException {
        chain.resultSet_updateCharacterStream(result, columnIndex, x, length);
    }

    @Override
    public void resultSet_updateCharacterStream(FilterChain chain, ResultSetProxy result, String columnLabel,
                                                java.io.Reader reader) throws SQLException {
        chain.resultSet_updateCharacterStream(result, columnLabel, reader);
    }

    @Override
    public void resultSet_updateCharacterStream(FilterChain chain, ResultSetProxy result, String columnLabel,
                                                java.io.Reader reader, int length) throws SQLException {
        chain.resultSet_updateCharacterStream(result, columnLabel, reader, length);
    }

    @Override
    public void resultSet_updateCharacterStream(FilterChain chain, ResultSetProxy result, String columnLabel,
                                                java.io.Reader reader, long length) throws SQLException {
        chain.resultSet_updateCharacterStream(result, columnLabel, reader, length);
    }

    @Override
    public void resultSet_updateClob(FilterChain chain, ResultSetProxy result, int columnIndex, java.sql.Clob x)
                                                                                                                throws SQLException {
        chain.resultSet_updateClob(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateClob(FilterChain chain, ResultSetProxy result, int columnIndex, Reader reader)
                                                                                                              throws SQLException {
        chain.resultSet_updateClob(result, columnIndex, reader);
    }

    @Override
    public void resultSet_updateClob(FilterChain chain, ResultSetProxy result, int columnIndex, Reader reader,
                                     long length) throws SQLException {
        chain.resultSet_updateClob(result, columnIndex, reader, length);
    }

    @Override
    public void resultSet_updateClob(FilterChain chain, ResultSetProxy result, String columnLabel, java.sql.Clob x)
                                                                                                                   throws SQLException {
        chain.resultSet_updateClob(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateClob(FilterChain chain, ResultSetProxy result, String columnLabel, Reader reader)
                                                                                                                 throws SQLException {
        chain.resultSet_updateClob(result, columnLabel, reader);
    }

    @Override
    public void resultSet_updateClob(FilterChain chain, ResultSetProxy result, String columnLabel, Reader reader,
                                     long length) throws SQLException {
        chain.resultSet_updateClob(result, columnLabel, reader, length);
    }

    @Override
    public void resultSet_updateDate(FilterChain chain, ResultSetProxy result, int columnIndex, java.sql.Date x)
                                                                                                                throws SQLException {
        chain.resultSet_updateDate(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateDate(FilterChain chain, ResultSetProxy result, String columnLabel, java.sql.Date x)
                                                                                                                   throws SQLException {
        chain.resultSet_updateDate(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateDouble(FilterChain chain, ResultSetProxy result, int columnIndex, double x)
                                                                                                           throws SQLException {
        chain.resultSet_updateDouble(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateDouble(FilterChain chain, ResultSetProxy result, String columnLabel, double x)
                                                                                                              throws SQLException {
        chain.resultSet_updateDouble(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateFloat(FilterChain chain, ResultSetProxy result, int columnIndex, float x)
                                                                                                         throws SQLException {
        chain.resultSet_updateFloat(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateFloat(FilterChain chain, ResultSetProxy result, String columnLabel, float x)
                                                                                                            throws SQLException {
        chain.resultSet_updateFloat(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateInt(FilterChain chain, ResultSetProxy result, int columnIndex, int x)
                                                                                                     throws SQLException {
        chain.resultSet_updateInt(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateInt(FilterChain chain, ResultSetProxy result, String columnLabel, int x)
                                                                                                        throws SQLException {
        chain.resultSet_updateInt(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateLong(FilterChain chain, ResultSetProxy result, int columnIndex, long x)
                                                                                                       throws SQLException {
        chain.resultSet_updateLong(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateLong(FilterChain chain, ResultSetProxy result, String columnLabel, long x)
                                                                                                          throws SQLException {
        chain.resultSet_updateLong(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateNCharacterStream(FilterChain chain, ResultSetProxy result, int columnIndex,
                                                 java.io.Reader x) throws SQLException {
        chain.resultSet_updateNCharacterStream(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateNCharacterStream(FilterChain chain, ResultSetProxy result, int columnIndex,
                                                 java.io.Reader x, long length) throws SQLException {
        chain.resultSet_updateNCharacterStream(result, columnIndex, x, length);
    }

    @Override
    public void resultSet_updateNCharacterStream(FilterChain chain, ResultSetProxy result, String columnLabel,
                                                 java.io.Reader reader) throws SQLException {
        chain.resultSet_updateNCharacterStream(result, columnLabel, reader);
    }

    @Override
    public void resultSet_updateNCharacterStream(FilterChain chain, ResultSetProxy result, String columnLabel,
                                                 java.io.Reader reader, long length) throws SQLException {
        chain.resultSet_updateNCharacterStream(result, columnLabel, reader, length);
    }

    @Override
    public void resultSet_updateNClob(FilterChain chain, ResultSetProxy result, int columnIndex, NClob nClob)
                                                                                                             throws SQLException {
        chain.resultSet_updateNClob(result, columnIndex, nClob);
    }

    @Override
    public void resultSet_updateNClob(FilterChain chain, ResultSetProxy result, int columnIndex, Reader reader)
                                                                                                               throws SQLException {
        chain.resultSet_updateNClob(result, columnIndex, reader);
    }

    @Override
    public void resultSet_updateNClob(FilterChain chain, ResultSetProxy result, int columnIndex, Reader reader,
                                      long length) throws SQLException {
        chain.resultSet_updateNClob(result, columnIndex, reader, length);
    }

    @Override
    public void resultSet_updateNClob(FilterChain chain, ResultSetProxy result, String columnLabel, NClob nClob)
                                                                                                                throws SQLException {
        chain.resultSet_updateNClob(result, columnLabel, nClob);
    }

    @Override
    public void resultSet_updateNClob(FilterChain chain, ResultSetProxy result, String columnLabel, Reader reader)
                                                                                                                  throws SQLException {
        chain.resultSet_updateNClob(result, columnLabel, reader);
    }

    @Override
    public void resultSet_updateNClob(FilterChain chain, ResultSetProxy result, String columnLabel, Reader reader,
                                      long length) throws SQLException {
        chain.resultSet_updateNClob(result, columnLabel, reader, length);
    }

    @Override
    public void resultSet_updateNString(FilterChain chain, ResultSetProxy result, int columnIndex, String nString)
                                                                                                                  throws SQLException {
        chain.resultSet_updateNString(result, columnIndex, nString);
    }

    @Override
    public void resultSet_updateNString(FilterChain chain, ResultSetProxy result, String columnLabel, String nString)
                                                                                                                     throws SQLException {
        chain.resultSet_updateNString(result, columnLabel, nString);
    }

    @Override
    public void resultSet_updateNull(FilterChain chain, ResultSetProxy result, int columnIndex) throws SQLException {
        chain.resultSet_updateNull(result, columnIndex);
    }

    @Override
    public void resultSet_updateNull(FilterChain chain, ResultSetProxy result, String columnLabel) throws SQLException {
        chain.resultSet_updateNull(result, columnLabel);
    }

    @Override
    public void resultSet_updateObject(FilterChain chain, ResultSetProxy result, int columnIndex, Object x)
                                                                                                           throws SQLException {
        chain.resultSet_updateObject(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateObject(FilterChain chain, ResultSetProxy result, int columnIndex, Object x,
                                       int scaleOrLength) throws SQLException {
        chain.resultSet_updateObject(result, columnIndex, x, scaleOrLength);
    }

    @Override
    public void resultSet_updateObject(FilterChain chain, ResultSetProxy result, String columnLabel, Object x)
                                                                                                              throws SQLException {
        chain.resultSet_updateObject(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateObject(FilterChain chain, ResultSetProxy result, String columnLabel, Object x,
                                       int scaleOrLength) throws SQLException {
        chain.resultSet_updateObject(result, columnLabel, x, scaleOrLength);
    }

    @Override
    public void resultSet_updateRef(FilterChain chain, ResultSetProxy result, int columnIndex, java.sql.Ref x)
                                                                                                              throws SQLException {
        chain.resultSet_updateRef(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateRef(FilterChain chain, ResultSetProxy result, String columnLabel, java.sql.Ref x)
                                                                                                                 throws SQLException {
        chain.resultSet_updateRef(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateRow(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        chain.resultSet_updateRow(resultSet);
    }

    @Override
    public void resultSet_updateRowId(FilterChain chain, ResultSetProxy result, int columnIndex, RowId x)
                                                                                                         throws SQLException {
        chain.resultSet_updateRowId(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateRowId(FilterChain chain, ResultSetProxy result, String columnLabel, RowId x)
                                                                                                            throws SQLException {
        chain.resultSet_updateRowId(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateShort(FilterChain chain, ResultSetProxy result, int columnIndex, short x)
                                                                                                         throws SQLException {
        chain.resultSet_updateShort(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateShort(FilterChain chain, ResultSetProxy result, String columnLabel, short x)
                                                                                                            throws SQLException {
        chain.resultSet_updateShort(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateSQLXML(FilterChain chain, ResultSetProxy result, int columnIndex, SQLXML xmlObject)
                                                                                                                   throws SQLException {
        chain.resultSet_updateSQLXML(result, columnIndex, xmlObject);
    }

    @Override
    public void resultSet_updateSQLXML(FilterChain chain, ResultSetProxy result, String columnLabel, SQLXML xmlObject)
                                                                                                                      throws SQLException {
        chain.resultSet_updateSQLXML(result, columnLabel, xmlObject);
    }

    @Override
    public void resultSet_updateString(FilterChain chain, ResultSetProxy result, int columnIndex, String x)
                                                                                                           throws SQLException {
        chain.resultSet_updateString(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateString(FilterChain chain, ResultSetProxy result, String columnLabel, String x)
                                                                                                              throws SQLException {
        chain.resultSet_updateString(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateTime(FilterChain chain, ResultSetProxy result, int columnIndex, java.sql.Time x)
                                                                                                                throws SQLException {
        chain.resultSet_updateTime(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateTime(FilterChain chain, ResultSetProxy result, String columnLabel, java.sql.Time x)
                                                                                                                   throws SQLException {
        chain.resultSet_updateTime(result, columnLabel, x);
    }

    @Override
    public void resultSet_updateTimestamp(FilterChain chain, ResultSetProxy result, int columnIndex,
                                          java.sql.Timestamp x) throws SQLException {
        chain.resultSet_updateTimestamp(result, columnIndex, x);
    }

    @Override
    public void resultSet_updateTimestamp(FilterChain chain, ResultSetProxy result, String columnLabel,
                                          java.sql.Timestamp x) throws SQLException {
        chain.resultSet_updateTimestamp(result, columnLabel, x);
    }

    @Override
    public boolean resultSet_wasNull(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        return chain.resultSet_wasNull(resultSet);
    }

    @Override
    public void statement_addBatch(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        chain.statement_addBatch(statement, sql);
    }

    @Override
    public void statement_cancel(FilterChain chain, StatementProxy statement) throws SQLException {
        chain.statement_cancel(statement);
    }

    @Override
    public void statement_clearBatch(FilterChain chain, StatementProxy statement) throws SQLException {
        chain.statement_clearBatch(statement);
    }

    @Override
    public void statement_close(FilterChain chain, StatementProxy statement) throws SQLException {
        chain.statement_close(statement);
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        return chain.statement_execute(statement, sql);
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, int autoGeneratedKeys)
                                                                                                                    throws SQLException {
        return chain.statement_execute(statement, sql, autoGeneratedKeys);
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, int columnIndexes[])
                                                                                                                  throws SQLException {
        return chain.statement_execute(statement, sql, columnIndexes);
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, String columnNames[])
                                                                                                                   throws SQLException {
        return chain.statement_execute(statement, sql, columnNames);
    }

    @Override
    public int[] statement_executeBatch(FilterChain chain, StatementProxy statement) throws SQLException {
        return chain.statement_executeBatch(statement);
    }

    // /////////////////////////////
    @Override
    public ResultSetProxy statement_executeQuery(FilterChain chain, StatementProxy statement, String sql)
                                                                                                         throws SQLException {
        return chain.statement_executeQuery(statement, sql);
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        return chain.statement_executeUpdate(statement, sql);
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, int autoGeneratedKeys)
                                                                                                                      throws SQLException {
        return chain.statement_executeUpdate(statement, sql, autoGeneratedKeys);
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, int columnIndexes[])
                                                                                                                    throws SQLException {
        return chain.statement_executeUpdate(statement, sql, columnIndexes);
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, String columnNames[])
                                                                                                                     throws SQLException {
        return chain.statement_executeUpdate(statement, sql, columnNames);
    }

    @Override
    public Connection statement_getConnection(FilterChain chain, StatementProxy statement) throws SQLException {
        return chain.statement_getConnection(statement);
    }

    @Override
    public int statement_getFetchDirection(FilterChain chain, StatementProxy statement) throws SQLException {
        return chain.statement_getFetchDirection(statement);
    }

    @Override
    public int statement_getFetchSize(FilterChain chain, StatementProxy statement) throws SQLException {
        return chain.statement_getFetchSize(statement);
    }

    @Override
    public ResultSetProxy statement_getGeneratedKeys(FilterChain chain, StatementProxy statement) throws SQLException {
        return chain.statement_getGeneratedKeys(statement);
    }

    @Override
    public int statement_getMaxFieldSize(FilterChain chain, StatementProxy statement) throws SQLException {
        return chain.statement_getMaxFieldSize(statement);
    }

    @Override
    public int statement_getMaxRows(FilterChain chain, StatementProxy statement) throws SQLException {
        return chain.statement_getMaxRows(statement);
    }

    @Override
    public boolean statement_getMoreResults(FilterChain chain, StatementProxy statement) throws SQLException {
        return chain.statement_getMoreResults(statement);
    }

    @Override
    public boolean statement_getMoreResults(FilterChain chain, StatementProxy statement, int current)
                                                                                                     throws SQLException {
        return chain.statement_getMoreResults(statement, current);
    }

    @Override
    public int statement_getQueryTimeout(FilterChain chain, StatementProxy statement) throws SQLException {
        return chain.statement_getQueryTimeout(statement);
    }

    @Override
    public void statement_setQueryTimeout(FilterChain chain, StatementProxy statement, int seconds) throws SQLException {
        chain.statement_setQueryTimeout(statement, seconds);
    }

    @Override
    public ResultSetProxy statement_getResultSet(FilterChain chain, StatementProxy statement) throws SQLException {
        return chain.statement_getResultSet(statement);
    }

    @Override
    public int statement_getResultSetConcurrency(FilterChain chain, StatementProxy statement) throws SQLException {
        return chain.statement_getResultSetConcurrency(statement);
    }

    @Override
    public int statement_getResultSetHoldability(FilterChain chain, StatementProxy statement) throws SQLException {
        return chain.statement_getResultSetHoldability(statement);
    }

    @Override
    public int statement_getResultSetType(FilterChain chain, StatementProxy statement) throws SQLException {
        return chain.statement_getResultSetType(statement);
    }

    @Override
    public int statement_getUpdateCount(FilterChain chain, StatementProxy statement) throws SQLException {
        return chain.statement_getUpdateCount(statement);
    }

    @Override
    public SQLWarning statement_getWarnings(FilterChain chain, StatementProxy statement) throws SQLException {
        return chain.statement_getWarnings(statement);
    }

    @Override
    public void statement_clearWarnings(FilterChain chain, StatementProxy statement) throws SQLException {
        chain.statement_clearWarnings(statement);
    }

    @Override
    public boolean statement_isClosed(FilterChain chain, StatementProxy statement) throws SQLException {
        return chain.statement_isClosed(statement);
    }

    @Override
    public boolean statement_isPoolable(FilterChain chain, StatementProxy statement) throws SQLException {
        return chain.statement_isPoolable(statement);
    }

    @Override
    public void statement_setCursorName(FilterChain chain, StatementProxy statement, String name) throws SQLException {
        chain.statement_setCursorName(statement, name);
    }

    @Override
    public void statement_setEscapeProcessing(FilterChain chain, StatementProxy statement, boolean enable)
                                                                                                          throws SQLException {
        chain.statement_setEscapeProcessing(statement, enable);
    }

    @Override
    public void statement_setFetchDirection(FilterChain chain, StatementProxy statement, int direction)
                                                                                                       throws SQLException {
        chain.statement_setFetchDirection(statement, direction);
    }

    @Override
    public void statement_setFetchSize(FilterChain chain, StatementProxy statement, int rows) throws SQLException {
        chain.statement_setFetchSize(statement, rows);
    }

    @Override
    public void statement_setMaxFieldSize(FilterChain chain, StatementProxy statement, int max) throws SQLException {
        chain.statement_setMaxFieldSize(statement, max);
    }

    @Override
    public void statement_setMaxRows(FilterChain chain, StatementProxy statement, int max) throws SQLException {
        chain.statement_setMaxRows(statement, max);
    }

    @Override
    public void statement_setPoolable(FilterChain chain, StatementProxy statement, boolean poolable)
                                                                                                    throws SQLException {
        chain.statement_setPoolable(statement, poolable);
    }

    @Override
    public <T> T unwrap(FilterChain chain, Wrapper wrapper, Class<T> iface) throws SQLException {
        return chain.unwrap(wrapper, iface);
    }

    @Override
    public long clob_length(FilterChain chain, ClobProxy wrapper) throws SQLException {
        return chain.clob_length(wrapper);
    }

    @Override
    public String clob_getSubString(FilterChain chain, ClobProxy wrapper, long pos, int length) throws SQLException {
        return chain.clob_getSubString(wrapper, pos, length);
    }

    @Override
    public java.io.Reader clob_getCharacterStream(FilterChain chain, ClobProxy wrapper) throws SQLException {
        return chain.clob_getCharacterStream(wrapper);
    }

    @Override
    public java.io.InputStream clob_getAsciiStream(FilterChain chain, ClobProxy wrapper) throws SQLException {
        return chain.clob_getAsciiStream(wrapper);
    }

    @Override
    public long clob_position(FilterChain chain, ClobProxy wrapper, String searchstr, long start) throws SQLException {
        return chain.clob_position(wrapper, searchstr, start);
    }

    @Override
    public long clob_position(FilterChain chain, ClobProxy wrapper, Clob searchstr, long start) throws SQLException {
        return chain.clob_position(wrapper, searchstr, start);
    }

    @Override
    public int clob_setString(FilterChain chain, ClobProxy wrapper, long pos, String str) throws SQLException {
        return chain.clob_setString(wrapper, pos, str);
    }

    @Override
    public int clob_setString(FilterChain chain, ClobProxy wrapper, long pos, String str, int offset, int len)
                                                                                                              throws SQLException {
        return chain.clob_setString(wrapper, pos, str, offset, len);
    }

    @Override
    public java.io.OutputStream clob_setAsciiStream(FilterChain chain, ClobProxy wrapper, long pos) throws SQLException {
        return chain.clob_setAsciiStream(wrapper, pos);
    }

    @Override
    public java.io.Writer clob_setCharacterStream(FilterChain chain, ClobProxy wrapper, long pos) throws SQLException {
        return chain.clob_setCharacterStream(wrapper, pos);
    }

    @Override
    public void clob_truncate(FilterChain chain, ClobProxy wrapper, long len) throws SQLException {
        chain.clob_truncate(wrapper, len);
    }

    @Override
    public void clob_free(FilterChain chain, ClobProxy wrapper) throws SQLException {
        chain.clob_free(wrapper);
    }

    @Override
    public Reader clob_getCharacterStream(FilterChain chain, ClobProxy wrapper, long pos, long length)
                                                                                                      throws SQLException {
        return chain.clob_getCharacterStream(wrapper, pos, length);
    }

    // ///////////////////

    @Override
    public void dataSource_releaseConnection(FilterChain chain, DruidPooledConnection connection) throws SQLException {
        chain.dataSource_recycle(connection);
    }

    @Override
    public DruidPooledConnection dataSource_getConnection(FilterChain chain, DruidDataSource dataSource,
                                                          long maxWaitMillis) throws SQLException {
        return chain.dataSource_connect(dataSource, maxWaitMillis);
    }

    // ///////////////

    @Override
    public int resultSetMetaData_getColumnCount(FilterChain chain, ResultSetMetaDataProxy metaData) throws SQLException {
        return chain.resultSetMetaData_getColumnCount(metaData);
    }

    @Override
    public boolean resultSetMetaData_isAutoIncrement(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                    throws SQLException {
        return chain.resultSetMetaData_isAutoIncrement(metaData, column);
    }

    @Override
    public boolean resultSetMetaData_isCaseSensitive(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                    throws SQLException {
        return chain.resultSetMetaData_isCaseSensitive(metaData, column);
    }

    @Override
    public boolean resultSetMetaData_isSearchable(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                 throws SQLException {
        return chain.resultSetMetaData_isSearchable(metaData, column);
    }

    @Override
    public boolean resultSetMetaData_isCurrency(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                               throws SQLException {
        return chain.resultSetMetaData_isCurrency(metaData, column);
    }

    @Override
    public int resultSetMetaData_isNullable(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                           throws SQLException {
        return chain.resultSetMetaData_isNullable(metaData, column);
    }

    @Override
    public boolean resultSetMetaData_isSigned(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                             throws SQLException {
        return chain.resultSetMetaData_isSigned(metaData, column);
    }

    @Override
    public int resultSetMetaData_getColumnDisplaySize(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                     throws SQLException {
        return chain.resultSetMetaData_getColumnDisplaySize(metaData, column);
    }

    @Override
    public String resultSetMetaData_getColumnLabel(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                  throws SQLException {
        return chain.resultSetMetaData_getColumnLabel(metaData, column);
    }

    @Override
    public String resultSetMetaData_getColumnName(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                 throws SQLException {
        return chain.resultSetMetaData_getColumnName(metaData, column);
    }

    @Override
    public String resultSetMetaData_getSchemaName(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                 throws SQLException {
        return chain.resultSetMetaData_getSchemaName(metaData, column);
    }

    @Override
    public int resultSetMetaData_getPrecision(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                             throws SQLException {
        return chain.resultSetMetaData_getPrecision(metaData, column);
    }

    @Override
    public int resultSetMetaData_getScale(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                         throws SQLException {
        return chain.resultSetMetaData_getScale(metaData, column);
    }

    @Override
    public String resultSetMetaData_getTableName(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                throws SQLException {
        return chain.resultSetMetaData_getTableName(metaData, column);
    }

    @Override
    public String resultSetMetaData_getCatalogName(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                  throws SQLException {
        return chain.resultSetMetaData_getCatalogName(metaData, column);
    }

    @Override
    public int resultSetMetaData_getColumnType(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                              throws SQLException {
        return chain.resultSetMetaData_getColumnType(metaData, column);
    }

    @Override
    public String resultSetMetaData_getColumnTypeName(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                     throws SQLException {
        return chain.resultSetMetaData_getColumnTypeName(metaData, column);
    }

    @Override
    public boolean resultSetMetaData_isReadOnly(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                               throws SQLException {
        return chain.resultSetMetaData_isReadOnly(metaData, column);
    }

    @Override
    public boolean resultSetMetaData_isWritable(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                               throws SQLException {
        return chain.resultSetMetaData_isWritable(metaData, column);
    }

    @Override
    public boolean resultSetMetaData_isDefinitelyWritable(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                         throws SQLException {
        return chain.resultSetMetaData_isDefinitelyWritable(metaData, column);
    }

    @Override
    public String resultSetMetaData_getColumnClassName(FilterChain chain, ResultSetMetaDataProxy metaData, int column)
                                                                                                                      throws SQLException {
        return chain.resultSetMetaData_getColumnClassName(metaData, column);
    }
}
