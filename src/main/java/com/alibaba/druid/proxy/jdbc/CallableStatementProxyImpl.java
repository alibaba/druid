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

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;
import java.util.Calendar;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class CallableStatementProxyImpl extends PreparedStatementProxyImpl implements CallableStatementProxy {

    private final CallableStatement statement;

    public CallableStatementProxyImpl(ConnectionProxy connection, CallableStatement statement, String sql, long id){
        super(connection, statement, sql, id);
        this.statement = statement;
    }

    public CallableStatement getRawObject() {
        return this.statement;
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        createChain().callableStatement_registerOutParameter(this, parameterIndex, sqlType);
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        createChain().callableStatement_registerOutParameter(this, parameterIndex, sqlType, scale);
    }

    @Override
    public boolean wasNull() throws SQLException {
        return createChain().callableStatement_wasNull(this);
    }

    @Override
    public String getString(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getString(this, parameterIndex);
    }

    @Override
    public boolean getBoolean(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getBoolean(this, parameterIndex);
    }

    @Override
    public byte getByte(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getByte(this, parameterIndex);
    }

    @Override
    public short getShort(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getShort(this, parameterIndex);
    }

    @Override
    public int getInt(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getInt(this, parameterIndex);
    }

    @Override
    public long getLong(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getLong(this, parameterIndex);
    }

    @Override
    public float getFloat(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getFloat(this, parameterIndex);
    }

    @Override
    public double getDouble(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getDouble(this, parameterIndex);
    }

    @Override
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        return createChain().callableStatement_getBigDecimal(this, parameterIndex, scale);
    }

    @Override
    public byte[] getBytes(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getBytes(this, parameterIndex);
    }

    @Override
    public java.sql.Date getDate(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getDate(this, parameterIndex);
    }

    @Override
    public java.sql.Time getTime(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getTime(this, parameterIndex);
    }

    @Override
    public java.sql.Timestamp getTimestamp(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getTimestamp(this, parameterIndex);
    }

    @Override
    public Object getObject(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getObject(this, parameterIndex);
    }

    @Override
    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getBigDecimal(this, parameterIndex);
    }

    @Override
    public Object getObject(int parameterIndex, java.util.Map<String, Class<?>> map) throws SQLException {
        return createChain().callableStatement_getObject(this, parameterIndex, map);
    }

    @Override
    public Ref getRef(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getRef(this, parameterIndex);
    }

    @Override
    public Blob getBlob(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getBlob(this, parameterIndex);
    }

    @Override
    public Clob getClob(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getClob(this, parameterIndex);
    }

    @Override
    public Array getArray(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getArray(this, parameterIndex);
    }

    @Override
    public java.sql.Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        return createChain().callableStatement_getDate(this, parameterIndex, cal);
    }

    @Override
    public java.sql.Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        return createChain().callableStatement_getTime(this, parameterIndex, cal);
    }

    @Override
    public java.sql.Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        return createChain().callableStatement_getTimestamp(this, parameterIndex, cal);
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
        createChain().callableStatement_registerOutParameter(this, parameterIndex, sqlType, typeName);
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        createChain().callableStatement_registerOutParameter(this, parameterName, sqlType);
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        createChain().callableStatement_registerOutParameter(this, parameterName, sqlType, scale);
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        createChain().callableStatement_registerOutParameter(this, parameterName, sqlType, typeName);
    }

    @Override
    public java.net.URL getURL(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getURL(this, parameterIndex);
    }

    @Override
    public void setURL(String parameterName, java.net.URL val) throws SQLException {
        createChain().callableStatement_setURL(this, parameterName, val);
    }

    @Override
    public void setNull(String parameterName, int sqlType) throws SQLException {
        createChain().callableStatement_setNull(this, parameterName, sqlType);
    }

    @Override
    public void setBoolean(String parameterName, boolean x) throws SQLException {
        createChain().callableStatement_setBoolean(this, parameterName, x);
    }

    @Override
    public void setByte(String parameterName, byte x) throws SQLException {
        createChain().callableStatement_setByte(this, parameterName, x);
    }

    @Override
    public void setShort(String parameterName, short x) throws SQLException {
        createChain().callableStatement_setShort(this, parameterName, x);
    }

    @Override
    public void setInt(String parameterName, int x) throws SQLException {
        createChain().callableStatement_setInt(this, parameterName, x);
    }

    @Override
    public void setLong(String parameterName, long x) throws SQLException {
        createChain().callableStatement_setLong(this, parameterName, x);
    }

    @Override
    public void setFloat(String parameterName, float x) throws SQLException {
        createChain().callableStatement_setFloat(this, parameterName, x);
    }

    @Override
    public void setDouble(String parameterName, double x) throws SQLException {
        createChain().callableStatement_setDouble(this, parameterName, x);
    }

    @Override
    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        createChain().callableStatement_setBigDecimal(this, parameterName, x);
    }

    @Override
    public void setString(String parameterName, String x) throws SQLException {
        createChain().callableStatement_setString(this, parameterName, x);
    }

    @Override
    public void setBytes(String parameterName, byte[] x) throws SQLException {
        createChain().callableStatement_setBytes(this, parameterName, x);
    }

    @Override
    public void setDate(String parameterName, java.sql.Date x) throws SQLException {
        createChain().callableStatement_setDate(this, parameterName, x);
    }

    @Override
    public void setTime(String parameterName, java.sql.Time x) throws SQLException {
        createChain().callableStatement_setTime(this, parameterName, x);
    }

    @Override
    public void setTimestamp(String parameterName, java.sql.Timestamp x) throws SQLException {
        createChain().callableStatement_setTimestamp(this, parameterName, x);
    }

    @Override
    public void setAsciiStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
        createChain().callableStatement_setAsciiStream(this, parameterName, x, length);
    }

    @Override
    public void setBinaryStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
        createChain().callableStatement_setBinaryStream(this, parameterName, x, length);
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        createChain().callableStatement_setObject(this, parameterName, x, targetSqlType, scale);
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        createChain().callableStatement_setObject(this, parameterName, x, targetSqlType);
    }

    @Override
    public void setObject(String parameterName, Object x) throws SQLException {
        createChain().callableStatement_setObject(this, parameterName, x);
    }

    @Override
    public void setCharacterStream(String parameterName, java.io.Reader reader, int length) throws SQLException {
        createChain().callableStatement_setCharacterStream(this, parameterName, reader, length);
    }

    @Override
    public void setDate(String parameterName, java.sql.Date x, Calendar cal) throws SQLException {
        createChain().callableStatement_setDate(this, parameterName, x, cal);
    }

    @Override
    public void setTime(String parameterName, java.sql.Time x, Calendar cal) throws SQLException {
        createChain().callableStatement_setTime(this, parameterName, x, cal);
    }

    @Override
    public void setTimestamp(String parameterName, java.sql.Timestamp x, Calendar cal) throws SQLException {
        createChain().callableStatement_setTimestamp(this, parameterName, x, cal);
    }

    @Override
    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        createChain().callableStatement_setNull(this, parameterName, sqlType, typeName);
    }

    @Override
    public String getString(String parameterName) throws SQLException {
        return createChain().callableStatement_getString(this, parameterName);
    }

    @Override
    public boolean getBoolean(String parameterName) throws SQLException {
        return createChain().callableStatement_getBoolean(this, parameterName);
    }

    @Override
    public byte getByte(String parameterName) throws SQLException {
        return createChain().callableStatement_getByte(this, parameterName);
    }

    @Override
    public short getShort(String parameterName) throws SQLException {
        return createChain().callableStatement_getShort(this, parameterName);
    }

    @Override
    public int getInt(String parameterName) throws SQLException {
        return createChain().callableStatement_getInt(this, parameterName);
    }

    @Override
    public long getLong(String parameterName) throws SQLException {
        return createChain().callableStatement_getLong(this, parameterName);
    }

    @Override
    public float getFloat(String parameterName) throws SQLException {
        return createChain().callableStatement_getFloat(this, parameterName);
    }

    @Override
    public double getDouble(String parameterName) throws SQLException {
        return createChain().callableStatement_getDouble(this, parameterName);
    }

    @Override
    public byte[] getBytes(String parameterName) throws SQLException {
        return createChain().callableStatement_getBytes(this, parameterName);
    }

    @Override
    public java.sql.Date getDate(String parameterName) throws SQLException {
        return createChain().callableStatement_getDate(this, parameterName);
    }

    @Override
    public java.sql.Time getTime(String parameterName) throws SQLException {
        return createChain().callableStatement_getTime(this, parameterName);
    }

    @Override
    public java.sql.Timestamp getTimestamp(String parameterName) throws SQLException {
        return createChain().callableStatement_getTimestamp(this, parameterName);
    }

    @Override
    public Object getObject(String parameterName) throws SQLException {
        return createChain().callableStatement_getObject(this, parameterName);
    }

    @Override
    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        return createChain().callableStatement_getBigDecimal(this, parameterName);
    }

    @Override
    public Object getObject(String parameterName, java.util.Map<String, Class<?>> map) throws SQLException {
        return createChain().callableStatement_getObject(this, parameterName, map);
    }

    @Override
    public Ref getRef(String parameterName) throws SQLException {
        return createChain().callableStatement_getRef(this, parameterName);
    }

    @Override
    public Blob getBlob(String parameterName) throws SQLException {
        return createChain().callableStatement_getBlob(this, parameterName);
    }

    @Override
    public Clob getClob(String parameterName) throws SQLException {
        return createChain().callableStatement_getClob(this, parameterName);
    }

    @Override
    public Array getArray(String parameterName) throws SQLException {
        return createChain().callableStatement_getArray(this, parameterName);
    }

    @Override
    public java.sql.Date getDate(String parameterName, Calendar cal) throws SQLException {
        return createChain().callableStatement_getDate(this, parameterName, cal);
    }

    @Override
    public java.sql.Time getTime(String parameterName, Calendar cal) throws SQLException {
        return createChain().callableStatement_getTime(this, parameterName, cal);
    }

    @Override
    public java.sql.Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        return createChain().callableStatement_getTimestamp(this, parameterName, cal);
    }

    @Override
    public java.net.URL getURL(String parameterName) throws SQLException {
        return createChain().callableStatement_getURL(this, parameterName);
    }

    @Override
    public RowId getRowId(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getRowId(this, parameterIndex);
    }

    @Override
    public RowId getRowId(String parameterName) throws SQLException {
        return createChain().callableStatement_getRowId(this, parameterName);
    }

    @Override
    public void setRowId(String parameterName, RowId x) throws SQLException {
        createChain().callableStatement_setRowId(this, parameterName, x);
    }

    @Override
    public void setNString(String parameterName, String value) throws SQLException {
        createChain().callableStatement_setNString(this, parameterName, value);
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
        createChain().callableStatement_setNCharacterStream(this, parameterName, value, length);
    }

    @Override
    public void setNClob(String parameterName, NClob value) throws SQLException {
        createChain().callableStatement_setNClob(this, parameterName, value);
    }

    @Override
    public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        createChain().callableStatement_setClob(this, parameterName, reader, length);
    }

    @Override
    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        createChain().callableStatement_setBlob(this, parameterName, inputStream, length);
    }

    @Override
    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        createChain().callableStatement_setNClob(this, parameterName, reader, length);
    }

    @Override
    public NClob getNClob(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getNClob(this, parameterIndex);
    }

    @Override
    public NClob getNClob(String parameterName) throws SQLException {
        return createChain().callableStatement_getNClob(this, parameterName);
    }

    @Override
    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        createChain().callableStatement_setSQLXML(this, parameterName, xmlObject);
    }

    @Override
    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getSQLXML(this, parameterIndex);
    }

    @Override
    public SQLXML getSQLXML(String parameterName) throws SQLException {
        return createChain().callableStatement_getSQLXML(this, parameterName);
    }

    @Override
    public String getNString(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getNString(this, parameterIndex);
    }

    @Override
    public String getNString(String parameterName) throws SQLException {
        return createChain().callableStatement_getNString(this, parameterName);
    }

    @Override
    public java.io.Reader getNCharacterStream(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getNCharacterStream(this, parameterIndex);
    }

    @Override
    public java.io.Reader getNCharacterStream(String parameterName) throws SQLException {
        return createChain().callableStatement_getNCharacterStream(this, parameterName);
    }

    @Override
    public java.io.Reader getCharacterStream(int parameterIndex) throws SQLException {
        return createChain().callableStatement_getCharacterStream(this, parameterIndex);
    }

    @Override
    public java.io.Reader getCharacterStream(String parameterName) throws SQLException {
        return createChain().callableStatement_getCharacterStream(this, parameterName);
    }

    @Override
    public void setBlob(String parameterName, Blob x) throws SQLException {
        createChain().callableStatement_setBlob(this, parameterName, x);
    }

    @Override
    public void setClob(String parameterName, Clob x) throws SQLException {
        createChain().callableStatement_setClob(this, parameterName, x);
    }

    @Override
    public void setAsciiStream(String parameterName, java.io.InputStream x, long length) throws SQLException {
        createChain().callableStatement_setAsciiStream(this, parameterName, x, length);
    }

    @Override
    public void setBinaryStream(String parameterName, java.io.InputStream x, long length) throws SQLException {
        createChain().callableStatement_setBinaryStream(this, parameterName, x, length);
    }

    @Override
    public void setCharacterStream(String parameterName, java.io.Reader reader, long length) throws SQLException {
        createChain().callableStatement_setCharacterStream(this, parameterName, reader, length);
    }

    @Override
    public void setAsciiStream(String parameterName, java.io.InputStream x) throws SQLException {
        createChain().callableStatement_setAsciiStream(this, parameterName, x);
    }

    @Override
    public void setBinaryStream(String parameterName, java.io.InputStream x) throws SQLException {
        createChain().callableStatement_setBinaryStream(this, parameterName, x);
    }

    @Override
    public void setCharacterStream(String parameterName, java.io.Reader reader) throws SQLException {
        createChain().callableStatement_setCharacterStream(this, parameterName, reader);
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
        createChain().callableStatement_setNCharacterStream(this, parameterName, value);
    }

    @Override
    public void setClob(String parameterName, Reader reader) throws SQLException {
        createChain().callableStatement_setClob(this, parameterName, reader);
    }

    @Override
    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        createChain().callableStatement_setBlob(this, parameterName, inputStream);
    }

    @Override
    public void setNClob(String parameterName, Reader reader) throws SQLException {
        createChain().callableStatement_setNClob(this, parameterName, reader);
    }

    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == PreparedStatement.class || iface == CallableStatement.class) {
            return (T) statement;
        }
        
        return super.unwrap(iface);
    }
}
