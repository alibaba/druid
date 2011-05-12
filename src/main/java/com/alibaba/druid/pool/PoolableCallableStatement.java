/*
 * Copyright 2011 Alibaba Group.
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
package com.alibaba.druid.pool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.util.Calendar;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class PoolableCallableStatement extends PoolablePreparedStatement implements CallableStatement {

    private CallableStatement stmt;

    public PoolableCallableStatement(PoolableConnection conn, CallableStatement stmt, PreparedStatementKey key){
        super(conn, stmt, key);
        this.stmt = stmt;
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        stmt.registerOutParameter(parameterIndex, sqlType);
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        stmt.registerOutParameter(parameterIndex, sqlType, scale);
    }

    @Override
    public boolean wasNull() throws SQLException {
        return stmt.wasNull();
    }

    @Override
    public String getString(int parameterIndex) throws SQLException {
        return stmt.getString(parameterIndex);
    }

    @Override
    public boolean getBoolean(int parameterIndex) throws SQLException {
        return stmt.getBoolean(parameterIndex);
    }

    @Override
    public byte getByte(int parameterIndex) throws SQLException {
        return stmt.getByte(parameterIndex);
    }

    @Override
    public short getShort(int parameterIndex) throws SQLException {
        return stmt.getShort(parameterIndex);
    }

    @Override
    public int getInt(int parameterIndex) throws SQLException {
        return stmt.getInt(parameterIndex);
    }

    @Override
    public long getLong(int parameterIndex) throws SQLException {
        return stmt.getLong(parameterIndex);
    }

    @Override
    public float getFloat(int parameterIndex) throws SQLException {
        return stmt.getFloat(parameterIndex);
    }

    @Override
    public double getDouble(int parameterIndex) throws SQLException {
        return stmt.getDouble(parameterIndex);
    }

    @Override
    @Deprecated
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        return stmt.getBigDecimal(parameterIndex, scale);
    }

    @Override
    public byte[] getBytes(int parameterIndex) throws SQLException {
        return stmt.getBytes(parameterIndex);
    }

    @Override
    public java.sql.Date getDate(int parameterIndex) throws SQLException {
        return stmt.getDate(parameterIndex);
    }

    @Override
    public java.sql.Time getTime(int parameterIndex) throws SQLException {
        return stmt.getTime(parameterIndex);
    }

    @Override
    public java.sql.Timestamp getTimestamp(int parameterIndex) throws SQLException {
        return stmt.getTimestamp(parameterIndex);
    }

    @Override
    public Object getObject(int parameterIndex) throws SQLException {
        return stmt.getObject(parameterIndex);
    }

    @Override
    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        return stmt.getBigDecimal(parameterIndex);
    }

    @Override
    public Object getObject(int parameterIndex, java.util.Map<String, Class<?>> map) throws SQLException {
        return stmt.getObject(parameterIndex, map);
    }

    @Override
    public Ref getRef(int parameterIndex) throws SQLException {
        return stmt.getRef(parameterIndex);
    }

    @Override
    public Blob getBlob(int parameterIndex) throws SQLException {
        return stmt.getBlob(parameterIndex);
    }

    @Override
    public Clob getClob(int parameterIndex) throws SQLException {
        return stmt.getClob(parameterIndex);
    }

    @Override
    public Array getArray(int parameterIndex) throws SQLException {
        return stmt.getArray(parameterIndex);
    }

    @Override
    public java.sql.Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        return stmt.getDate(parameterIndex, cal);
    }

    @Override
    public java.sql.Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        return stmt.getTime(parameterIndex, cal);
    }

    @Override
    public java.sql.Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        return stmt.getTimestamp(parameterIndex, cal);
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
        stmt.registerOutParameter(parameterIndex, sqlType, typeName);
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        stmt.registerOutParameter(parameterName, sqlType);
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        stmt.registerOutParameter(parameterName, sqlType, scale);
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        stmt.registerOutParameter(parameterName, sqlType, typeName);
    }

    @Override
    public java.net.URL getURL(int parameterIndex) throws SQLException {
        return stmt.getURL(parameterIndex);
    }

    @Override
    public void setURL(String parameterName, java.net.URL val) throws SQLException {
        stmt.setURL(parameterName, val);
    }

    @Override
    public void setNull(String parameterName, int sqlType) throws SQLException {
        stmt.setNull(parameterName, sqlType);
    }

    @Override
    public void setBoolean(String parameterName, boolean x) throws SQLException {
        stmt.setBoolean(parameterName, x);
    }

    @Override
    public void setByte(String parameterName, byte x) throws SQLException {
        stmt.setByte(parameterName, x);
    }

    @Override
    public void setShort(String parameterName, short x) throws SQLException {
        stmt.setShort(parameterName, x);
    }

    @Override
    public void setInt(String parameterName, int x) throws SQLException {
        stmt.setInt(parameterName, x);
    }

    @Override
    public void setLong(String parameterName, long x) throws SQLException {
        stmt.setLong(parameterName, x);
    }

    @Override
    public void setFloat(String parameterName, float x) throws SQLException {
        stmt.setFloat(parameterName, x);
    }

    @Override
    public void setDouble(String parameterName, double x) throws SQLException {
        stmt.setDouble(parameterName, x);
    }

    @Override
    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        stmt.setBigDecimal(parameterName, x);
    }

    @Override
    public void setString(String parameterName, String x) throws SQLException {
        stmt.setString(parameterName, x);
    }

    @Override
    public void setBytes(String parameterName, byte x[]) throws SQLException {
        stmt.setBytes(parameterName, x);
    }

    @Override
    public void setDate(String parameterName, java.sql.Date x) throws SQLException {
        stmt.setDate(parameterName, x);
    }

    @Override
    public void setTime(String parameterName, java.sql.Time x) throws SQLException {
        stmt.setTime(parameterName, x);
    }

    @Override
    public void setTimestamp(String parameterName, java.sql.Timestamp x) throws SQLException {
        stmt.setTimestamp(parameterName, x);
    }

    @Override
    public void setAsciiStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
        stmt.setAsciiStream(parameterName, x, length);
    }

    @Override
    public void setBinaryStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
        stmt.setBinaryStream(parameterName, x, length);
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        stmt.setObject(parameterName, x, targetSqlType, scale);
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        stmt.setObject(parameterName, x, targetSqlType);
    }

    @Override
    public void setObject(String parameterName, Object x) throws SQLException {
        stmt.setObject(parameterName, x);
    }

    @Override
    public void setCharacterStream(String parameterName, java.io.Reader reader, int length) throws SQLException {
        stmt.setCharacterStream(parameterName, reader, length);
    }

    @Override
    public void setDate(String parameterName, java.sql.Date x, Calendar cal) throws SQLException {
        stmt.setDate(parameterName, x, cal);
    }

    @Override
    public void setTime(String parameterName, java.sql.Time x, Calendar cal) throws SQLException {
        stmt.setTime(parameterName, x, cal);
    }

    @Override
    public void setTimestamp(String parameterName, java.sql.Timestamp x, Calendar cal) throws SQLException {
        stmt.setTimestamp(parameterName, x, cal);
    }

    @Override
    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        stmt.setNull(parameterName, sqlType, typeName);
    }

    @Override
    public String getString(String parameterName) throws SQLException {
        return stmt.getString(parameterName);
    }

    @Override
    public boolean getBoolean(String parameterName) throws SQLException {
        return stmt.getBoolean(parameterName);
    }

    @Override
    public byte getByte(String parameterName) throws SQLException {
        return stmt.getByte(parameterName);
    }

    @Override
    public short getShort(String parameterName) throws SQLException {
        return stmt.getShort(parameterName);
    }

    @Override
    public int getInt(String parameterName) throws SQLException {
        return stmt.getInt(parameterName);
    }

    @Override
    public long getLong(String parameterName) throws SQLException {
        return stmt.getLong(parameterName);
    }

    @Override
    public float getFloat(String parameterName) throws SQLException {
        return stmt.getFloat(parameterName);
    }

    @Override
    public double getDouble(String parameterName) throws SQLException {
        return stmt.getDouble(parameterName);
    }

    @Override
    public byte[] getBytes(String parameterName) throws SQLException {
        return stmt.getBytes(parameterName);
    }

    @Override
    public java.sql.Date getDate(String parameterName) throws SQLException {
        return stmt.getDate(parameterName);
    }

    @Override
    public java.sql.Time getTime(String parameterName) throws SQLException {
        return stmt.getTime(parameterName);
    }

    @Override
    public java.sql.Timestamp getTimestamp(String parameterName) throws SQLException {
        return stmt.getTimestamp(parameterName);
    }

    @Override
    public Object getObject(String parameterName) throws SQLException {
        return stmt.getObject(parameterName);
    }

    @Override
    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        return stmt.getBigDecimal(parameterName);
    }

    @Override
    public Object getObject(String parameterName, java.util.Map<String, Class<?>> map) throws SQLException {
        return stmt.getObject(parameterName, map);
    }

    @Override
    public Ref getRef(String parameterName) throws SQLException {
        return stmt.getRef(parameterName);
    }

    @Override
    public Blob getBlob(String parameterName) throws SQLException {
        return stmt.getBlob(parameterName);
    }

    @Override
    public Clob getClob(String parameterName) throws SQLException {
        return stmt.getClob(parameterName);
    }

    @Override
    public Array getArray(String parameterName) throws SQLException {
        return stmt.getArray(parameterName);
    }

    @Override
    public java.sql.Date getDate(String parameterName, Calendar cal) throws SQLException {
        return stmt.getDate(parameterName, cal);
    }

    @Override
    public java.sql.Time getTime(String parameterName, Calendar cal) throws SQLException {
        return stmt.getTime(parameterName, cal);
    }

    @Override
    public java.sql.Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        return stmt.getTimestamp(parameterName, cal);
    }

    @Override
    public java.net.URL getURL(String parameterName) throws SQLException {
        return stmt.getURL(parameterName);
    }

    @Override
    public RowId getRowId(int parameterIndex) throws SQLException {
        return stmt.getRowId(parameterIndex);
    }

    @Override
    public RowId getRowId(String parameterName) throws SQLException {
        return stmt.getRowId(parameterName);
    }

    @Override
    public void setRowId(String parameterName, RowId x) throws SQLException {
        stmt.setRowId(parameterName, x);
    }

    @Override
    public void setNString(String parameterName, String value) throws SQLException {
        stmt.setNString(parameterName, value);
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
        stmt.setNCharacterStream(parameterName, value, length);
    }

    @Override
    public void setNClob(String parameterName, NClob value) throws SQLException {
        stmt.setNClob(parameterName, value);
    }

    @Override
    public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        stmt.setClob(parameterName, reader, length);
    }

    @Override
    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        stmt.setBlob(parameterName, inputStream, length);
    }

    @Override
    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        stmt.setNClob(parameterName, reader, length);
    }

    @Override
    public NClob getNClob(int parameterIndex) throws SQLException {
        return stmt.getNClob(parameterIndex);
    }

    @Override
    public NClob getNClob(String parameterName) throws SQLException {
        return stmt.getNClob(parameterName);
    }

    @Override
    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        stmt.setSQLXML(parameterName, xmlObject);
    }

    @Override
    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        return stmt.getSQLXML(parameterIndex);
    }

    @Override
    public SQLXML getSQLXML(String parameterName) throws SQLException {
        return stmt.getSQLXML(parameterName);
    }

    @Override
    public String getNString(int parameterIndex) throws SQLException {
        return stmt.getNString(parameterIndex);
    }

    @Override
    public String getNString(String parameterName) throws SQLException {
        return stmt.getNString(parameterName);
    }

    @Override
    public java.io.Reader getNCharacterStream(int parameterIndex) throws SQLException {
        return stmt.getNCharacterStream(parameterIndex);
    }

    @Override
    public java.io.Reader getNCharacterStream(String parameterName) throws SQLException {
        return stmt.getNCharacterStream(parameterName);
    }

    @Override
    public java.io.Reader getCharacterStream(int parameterIndex) throws SQLException {
        return stmt.getCharacterStream(parameterIndex);
    }

    @Override
    public java.io.Reader getCharacterStream(String parameterName) throws SQLException {
        return stmt.getCharacterStream(parameterName);
    }

    @Override
    public void setBlob(String parameterName, Blob x) throws SQLException {
        stmt.setBlob(parameterName, x);
    }

    @Override
    public void setClob(String parameterName, Clob x) throws SQLException {
        stmt.setClob(parameterName, x);
    }

    @Override
    public void setAsciiStream(String parameterName, java.io.InputStream x, long length) throws SQLException {
        stmt.setAsciiStream(parameterName, x, length);
    }

    @Override
    public void setBinaryStream(String parameterName, java.io.InputStream x, long length) throws SQLException {
        stmt.setBinaryStream(parameterName, x, length);
    }

    @Override
    public void setCharacterStream(String parameterName, java.io.Reader reader, long length) throws SQLException {
        stmt.setCharacterStream(parameterName, reader, length);
    }

    @Override
    public void setAsciiStream(String parameterName, java.io.InputStream x) throws SQLException {
        stmt.setAsciiStream(parameterName, x);
    }

    @Override
    public void setBinaryStream(String parameterName, java.io.InputStream x) throws SQLException {
        stmt.setBinaryStream(parameterName, x);
    }

    @Override
    public void setCharacterStream(String parameterName, java.io.Reader reader) throws SQLException {
        stmt.setCharacterStream(parameterName, reader);
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
        stmt.setNCharacterStream(parameterName, value);
    }

    @Override
    public void setClob(String parameterName, Reader reader) throws SQLException {
        stmt.setClob(parameterName, reader);
    }

    @Override
    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        stmt.setBlob(parameterName, inputStream);
    }

    @Override
    public void setNClob(String parameterName, Reader reader) throws SQLException {
        stmt.setNClob(parameterName, reader);
    }

}
