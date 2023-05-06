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
package com.alibaba.druid.proxy.jdbc;

import com.alibaba.druid.filter.FilterChainImpl;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class CallableStatementProxyImpl extends PreparedStatementProxyImpl implements CallableStatementProxy {
    private final CallableStatement statement;

    public CallableStatementProxyImpl(ConnectionProxy connection, CallableStatement statement, String sql, long id) {
        super(connection, statement, sql, id);
        this.statement = statement;
    }

    public CallableStatement getRawObject() {
        return this.statement;
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_registerOutParameter(this, parameterIndex, sqlType);
        recycleFilterChain(chain);
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_registerOutParameter(this, parameterIndex, sqlType, scale);
        recycleFilterChain(chain);
    }

    @Override
    public boolean wasNull() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean wasNull = chain.callableStatement_wasNull(this);
        recycleFilterChain(chain);
        return wasNull;
    }

    @Override
    public String getString(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        String value = chain.callableStatement_getString(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean getBoolean(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.callableStatement_getBoolean(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public byte getByte(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        byte value = chain.callableStatement_getByte(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public short getShort(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        short value = chain.callableStatement_getShort(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getInt(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.callableStatement_getInt(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public long getLong(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        long value = chain.callableStatement_getLong(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public float getFloat(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        float value = chain.callableStatement_getFloat(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public double getDouble(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        double value = chain.callableStatement_getDouble(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        FilterChainImpl chain = createChain();
        BigDecimal decimal = chain.callableStatement_getBigDecimal(this, parameterIndex, scale);
        recycleFilterChain(chain);
        return decimal;
    }

    @Override
    public byte[] getBytes(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        byte[] bytes = chain.callableStatement_getBytes(this, parameterIndex);
        recycleFilterChain(chain);
        return bytes;
    }

    @Override
    public java.sql.Date getDate(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Date value = chain.callableStatement_getDate(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public java.sql.Time getTime(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Time value = chain.callableStatement_getTime(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public java.sql.Timestamp getTimestamp(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Timestamp value = chain.callableStatement_getTimestamp(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Object getObject(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Object value = chain.callableStatement_getObject(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        BigDecimal decimal = chain.callableStatement_getBigDecimal(this, parameterIndex);
        recycleFilterChain(chain);
        return decimal;
    }

    @Override
    public Object getObject(int parameterIndex, java.util.Map<String, Class<?>> map) throws SQLException {
        FilterChainImpl chain = createChain();
        Object value = chain.callableStatement_getObject(this, parameterIndex, map);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Ref getRef(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Ref value = chain.callableStatement_getRef(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Blob getBlob(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Blob value = chain.callableStatement_getBlob(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Clob getClob(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Clob value = chain.callableStatement_getClob(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Array getArray(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Array value = chain.callableStatement_getArray(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public java.sql.Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        FilterChainImpl chain = createChain();
        Date date = chain.callableStatement_getDate(this, parameterIndex, cal);
        recycleFilterChain(chain);
        return date;
    }

    @Override
    public java.sql.Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        FilterChainImpl chain = createChain();
        Time time = chain.callableStatement_getTime(this, parameterIndex, cal);
        recycleFilterChain(chain);
        return time;
    }

    @Override
    public java.sql.Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        FilterChainImpl chain = createChain();
        Timestamp timestamp = chain.callableStatement_getTimestamp(this, parameterIndex, cal);
        recycleFilterChain(chain);
        return timestamp;
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_registerOutParameter(this, parameterIndex, sqlType, typeName);
        recycleFilterChain(chain);
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_registerOutParameter(this, parameterName, sqlType);
        recycleFilterChain(chain);
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_registerOutParameter(this, parameterName, sqlType, scale);
        recycleFilterChain(chain);
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_registerOutParameter(this, parameterName, sqlType, typeName);
        recycleFilterChain(chain);
    }

    @Override
    public java.net.URL getURL(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        URL url = chain.callableStatement_getURL(this, parameterIndex);
        recycleFilterChain(chain);
        return url;
    }

    @Override
    public void setURL(String parameterName, java.net.URL val) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setURL(this, parameterName, val);
        recycleFilterChain(chain);
    }

    @Override
    public void setNull(String parameterName, int sqlType) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setNull(this, parameterName, sqlType);
        recycleFilterChain(chain);
    }

    @Override
    public void setBoolean(String parameterName, boolean x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setBoolean(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setByte(String parameterName, byte x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setByte(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setShort(String parameterName, short x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setShort(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setInt(String parameterName, int x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setInt(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setLong(String parameterName, long x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setLong(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setFloat(String parameterName, float x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setFloat(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setDouble(String parameterName, double x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setDouble(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setBigDecimal(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setString(String parameterName, String x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setString(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setBytes(String parameterName, byte[] x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setBytes(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setDate(String parameterName, java.sql.Date x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setDate(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setTime(String parameterName, java.sql.Time x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setTime(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setTimestamp(String parameterName, java.sql.Timestamp x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setTimestamp(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setAsciiStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setAsciiStream(this, parameterName, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setBinaryStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setBinaryStream(this, parameterName, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setObject(this, parameterName, x, targetSqlType, scale);
        recycleFilterChain(chain);
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setObject(this, parameterName, x, targetSqlType);
        recycleFilterChain(chain);
    }

    @Override
    public void setObject(String parameterName, Object x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setObject(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setCharacterStream(String parameterName, java.io.Reader reader, int length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setCharacterStream(this, parameterName, reader, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setDate(String parameterName, java.sql.Date x, Calendar cal) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setDate(this, parameterName, x, cal);
        recycleFilterChain(chain);
    }

    @Override
    public void setTime(String parameterName, java.sql.Time x, Calendar cal) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setTime(this, parameterName, x, cal);
        recycleFilterChain(chain);
    }

    @Override
    public void setTimestamp(String parameterName, java.sql.Timestamp x, Calendar cal) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setTimestamp(this, parameterName, x, cal);
        recycleFilterChain(chain);
    }

    @Override
    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setNull(this, parameterName, sqlType, typeName);
        recycleFilterChain(chain);
    }

    @Override
    public String getString(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        String value = chain.callableStatement_getString(this, parameterName);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean getBoolean(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.callableStatement_getBoolean(this, parameterName);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public byte getByte(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        byte value = chain.callableStatement_getByte(this, parameterName);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public short getShort(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        short value = chain.callableStatement_getShort(this, parameterName);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getInt(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.callableStatement_getInt(this, parameterName);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public long getLong(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        long value = chain.callableStatement_getLong(this, parameterName);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public float getFloat(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        float value = chain.callableStatement_getFloat(this, parameterName);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public double getDouble(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        double value = chain.callableStatement_getDouble(this, parameterName);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public byte[] getBytes(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        byte[] value = chain.callableStatement_getBytes(this, parameterName);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public java.sql.Date getDate(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        Date date = chain.callableStatement_getDate(this, parameterName);
        recycleFilterChain(chain);
        return date;
    }

    @Override
    public java.sql.Time getTime(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        Time time = chain.callableStatement_getTime(this, parameterName);
        recycleFilterChain(chain);
        return time;
    }

    @Override
    public java.sql.Timestamp getTimestamp(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        Timestamp timestamp = chain.callableStatement_getTimestamp(this, parameterName);
        recycleFilterChain(chain);
        return timestamp;
    }

    @Override
    public Object getObject(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        Object value = chain.callableStatement_getObject(this, parameterName);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        BigDecimal decimal = chain.callableStatement_getBigDecimal(this, parameterName);
        recycleFilterChain(chain);
        return decimal;
    }

    @Override
    public Object getObject(String parameterName, java.util.Map<String, Class<?>> map) throws SQLException {
        FilterChainImpl chain = createChain();
        Object value = chain.callableStatement_getObject(this, parameterName, map);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Ref getRef(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        Ref value = chain.callableStatement_getRef(this, parameterName);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Blob getBlob(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        Blob value = chain.callableStatement_getBlob(this, parameterName);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Clob getClob(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        Clob clob = chain.callableStatement_getClob(this, parameterName);
        recycleFilterChain(chain);
        return clob;
    }

    @Override
    public Array getArray(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        Array array = chain.callableStatement_getArray(this, parameterName);
        recycleFilterChain(chain);
        return array;
    }

    @Override
    public java.sql.Date getDate(String parameterName, Calendar cal) throws SQLException {
        FilterChainImpl chain = createChain();
        Date date = chain.callableStatement_getDate(this, parameterName, cal);
        recycleFilterChain(chain);
        return date;
    }

    @Override
    public java.sql.Time getTime(String parameterName, Calendar cal) throws SQLException {
        FilterChainImpl chain = createChain();
        Time time = chain.callableStatement_getTime(this, parameterName, cal);
        recycleFilterChain(chain);
        return time;
    }

    @Override
    public java.sql.Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        FilterChainImpl chain = createChain();
        Timestamp timestamp = chain.callableStatement_getTimestamp(this, parameterName, cal);
        recycleFilterChain(chain);
        return timestamp;
    }

    @Override
    public java.net.URL getURL(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        URL url = chain.callableStatement_getURL(this, parameterName);
        recycleFilterChain(chain);
        return url;
    }

    @Override
    public RowId getRowId(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        RowId rowId = chain.callableStatement_getRowId(this, parameterIndex);
        recycleFilterChain(chain);
        return rowId;
    }

    @Override
    public RowId getRowId(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        RowId rowId = chain.callableStatement_getRowId(this, parameterName);
        recycleFilterChain(chain);
        return rowId;
    }

    @Override
    public void setRowId(String parameterName, RowId x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setRowId(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setNString(String parameterName, String value) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setNString(this, parameterName, value);
        recycleFilterChain(chain);
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setNCharacterStream(this, parameterName, value, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setNClob(String parameterName, NClob value) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setNClob(this, parameterName, value);
        recycleFilterChain(chain);
    }

    @Override
    public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setClob(this, parameterName, reader, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setBlob(this, parameterName, inputStream, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setNClob(this, parameterName, reader, length);
        recycleFilterChain(chain);
    }

    @Override
    public NClob getNClob(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        NClob value = chain.callableStatement_getNClob(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public NClob getNClob(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        NClob value = chain.callableStatement_getNClob(this, parameterName);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setSQLXML(this, parameterName, xmlObject);
        recycleFilterChain(chain);
    }

    @Override
    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        SQLXML value = chain.callableStatement_getSQLXML(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public SQLXML getSQLXML(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        SQLXML value = chain.callableStatement_getSQLXML(this, parameterName);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public String getNString(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        String value = chain.callableStatement_getNString(this, parameterIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public String getNString(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        String value = chain.callableStatement_getNString(this, parameterName);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public java.io.Reader getNCharacterStream(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Reader reader = chain.callableStatement_getNCharacterStream(this, parameterIndex);
        recycleFilterChain(chain);
        return reader;
    }

    @Override
    public java.io.Reader getNCharacterStream(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        Reader reader = chain.callableStatement_getNCharacterStream(this, parameterName);
        recycleFilterChain(chain);
        return reader;
    }

    @Override
    public java.io.Reader getCharacterStream(int parameterIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Reader reader = chain.callableStatement_getCharacterStream(this, parameterIndex);
        recycleFilterChain(chain);
        return reader;
    }

    @Override
    public java.io.Reader getCharacterStream(String parameterName) throws SQLException {
        FilterChainImpl chain = createChain();
        Reader reader = chain.callableStatement_getCharacterStream(this, parameterName);
        recycleFilterChain(chain);
        return reader;
    }

    @Override
    public void setBlob(String parameterName, Blob x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setBlob(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setClob(String parameterName, Clob x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setClob(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setAsciiStream(String parameterName, java.io.InputStream x, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setAsciiStream(this, parameterName, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setBinaryStream(String parameterName, java.io.InputStream x, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setBinaryStream(this, parameterName, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setCharacterStream(String parameterName, java.io.Reader reader, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setCharacterStream(this, parameterName, reader, length);
        recycleFilterChain(chain);
    }

    @Override
    public void setAsciiStream(String parameterName, java.io.InputStream x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setAsciiStream(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setBinaryStream(String parameterName, java.io.InputStream x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setBinaryStream(this, parameterName, x);
        recycleFilterChain(chain);
    }

    @Override
    public void setCharacterStream(String parameterName, java.io.Reader reader) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setCharacterStream(this, parameterName, reader);
        recycleFilterChain(chain);
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setNCharacterStream(this, parameterName, value);
        recycleFilterChain(chain);
    }

    @Override
    public void setClob(String parameterName, Reader reader) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setClob(this, parameterName, reader);
        recycleFilterChain(chain);
    }

    @Override
    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setBlob(this, parameterName, inputStream);
        recycleFilterChain(chain);
    }

    @Override
    public void setNClob(String parameterName, Reader reader) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.callableStatement_setNClob(this, parameterName, reader);
        recycleFilterChain(chain);
    }

    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        return statement.getObject(parameterIndex, type);
    }

    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        return statement.getObject(parameterName, type);
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == PreparedStatement.class || iface == CallableStatement.class) {
            return (T) statement;
        }

        return super.unwrap(iface);
    }
}
