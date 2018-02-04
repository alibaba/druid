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
package com.alibaba.druid.pool;

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
import java.sql.ResultSet;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;
import java.util.Calendar;

import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidPooledCallableStatement extends DruidPooledPreparedStatement implements CallableStatement {

    private CallableStatement stmt;

    public DruidPooledCallableStatement(DruidPooledConnection conn, PreparedStatementHolder holder) throws SQLException{
        super(conn, holder);
        this.stmt = (CallableStatement) holder.statement;
    }

    public CallableStatement getCallableStatementRaw() {
        return stmt;
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        try {
            stmt.registerOutParameter(parameterIndex, sqlType);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        try {
            stmt.registerOutParameter(parameterIndex, sqlType, scale);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean wasNull() throws SQLException {
        try {
            return stmt.wasNull();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public String getString(int parameterIndex) throws SQLException {
        try {
            return stmt.getString(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean getBoolean(int parameterIndex) throws SQLException {
        try {
            return stmt.getBoolean(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public byte getByte(int parameterIndex) throws SQLException {
        try {
            return stmt.getByte(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public short getShort(int parameterIndex) throws SQLException {
        try {
            return stmt.getShort(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public int getInt(int parameterIndex) throws SQLException {
        try {
            return stmt.getInt(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public long getLong(int parameterIndex) throws SQLException {
        try {
            return stmt.getLong(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public float getFloat(int parameterIndex) throws SQLException {
        try {
            return stmt.getFloat(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public double getDouble(int parameterIndex) throws SQLException {
        try {
            return stmt.getDouble(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    @Deprecated
    public BigDecimal getBigDecimal(int parameterIndex, int scale) throws SQLException {
        try {
            return stmt.getBigDecimal(parameterIndex, scale);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public byte[] getBytes(int parameterIndex) throws SQLException {
        try {
            return stmt.getBytes(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Date getDate(int parameterIndex) throws SQLException {
        try {
            return stmt.getDate(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Time getTime(int parameterIndex) throws SQLException {
        try {
            return stmt.getTime(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Timestamp getTimestamp(int parameterIndex) throws SQLException {
        try {
            return stmt.getTimestamp(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Object getObject(int parameterIndex) throws SQLException {
        try {
            Object obj = stmt.getObject(parameterIndex);
            return wrapObject(obj);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    private Object wrapObject(Object obj) {
        if (obj instanceof ResultSet) {
            ResultSet rs = (ResultSet) obj;
            
            DruidPooledResultSet poolableResultSet = new DruidPooledResultSet(this, rs);
            addResultSetTrace(poolableResultSet);
            
            obj = poolableResultSet;
        }
        
        return obj;
    }

    @Override
    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        try {
            return stmt.getBigDecimal(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Object getObject(int parameterIndex, java.util.Map<String, Class<?>> map) throws SQLException {
        try {
            Object obj = stmt.getObject(parameterIndex, map);
            return wrapObject(obj);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Ref getRef(int parameterIndex) throws SQLException {
        try {
            return stmt.getRef(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Blob getBlob(int parameterIndex) throws SQLException {
        try {
            return stmt.getBlob(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Clob getClob(int parameterIndex) throws SQLException {
        try {
            return stmt.getClob(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Array getArray(int parameterIndex) throws SQLException {
        try {
            return stmt.getArray(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        try {
            return stmt.getDate(parameterIndex, cal);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        try {
            return stmt.getTime(parameterIndex, cal);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        try {
            return stmt.getTimestamp(parameterIndex, cal);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
        try {
            stmt.registerOutParameter(parameterIndex, sqlType, typeName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        try {
            stmt.registerOutParameter(parameterName, sqlType);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        try {
            stmt.registerOutParameter(parameterName, sqlType, scale);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        try {
            stmt.registerOutParameter(parameterName, sqlType, typeName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.net.URL getURL(int parameterIndex) throws SQLException {
        try {
            return stmt.getURL(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setURL(String parameterName, java.net.URL val) throws SQLException {
        try {
            stmt.setURL(parameterName, val);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setNull(String parameterName, int sqlType) throws SQLException {
        try {
            stmt.setNull(parameterName, sqlType);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setBoolean(String parameterName, boolean x) throws SQLException {
        try {
            stmt.setBoolean(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setByte(String parameterName, byte x) throws SQLException {
        try {
            stmt.setByte(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setShort(String parameterName, short x) throws SQLException {
        try {
            stmt.setShort(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setInt(String parameterName, int x) throws SQLException {
        try {
            stmt.setInt(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setLong(String parameterName, long x) throws SQLException {
        try {
            stmt.setLong(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setFloat(String parameterName, float x) throws SQLException {
        try {
            stmt.setFloat(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setDouble(String parameterName, double x) throws SQLException {
        try {
            stmt.setDouble(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        try {
            stmt.setBigDecimal(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setString(String parameterName, String x) throws SQLException {
        try {
            stmt.setString(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setBytes(String parameterName, byte x[]) throws SQLException {
        try {
            stmt.setBytes(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setDate(String parameterName, java.sql.Date x) throws SQLException {
        try {
            stmt.setDate(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setTime(String parameterName, java.sql.Time x) throws SQLException {
        try {
            stmt.setTime(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setTimestamp(String parameterName, java.sql.Timestamp x) throws SQLException {
        try {
            stmt.setTimestamp(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setAsciiStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
        try {
            stmt.setAsciiStream(parameterName, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setBinaryStream(String parameterName, java.io.InputStream x, int length) throws SQLException {
        try {
            stmt.setBinaryStream(parameterName, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        try {
            stmt.setObject(parameterName, x, targetSqlType, scale);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        try {
            stmt.setObject(parameterName, x, targetSqlType);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setObject(String parameterName, Object x) throws SQLException {
        try {
            stmt.setObject(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setCharacterStream(String parameterName, java.io.Reader reader, int length) throws SQLException {
        try {
            stmt.setCharacterStream(parameterName, reader, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setDate(String parameterName, java.sql.Date x, Calendar cal) throws SQLException {
        try {
            stmt.setDate(parameterName, x, cal);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setTime(String parameterName, java.sql.Time x, Calendar cal) throws SQLException {
        try {
            stmt.setTime(parameterName, x, cal);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setTimestamp(String parameterName, java.sql.Timestamp x, Calendar cal) throws SQLException {
        try {
            stmt.setTimestamp(parameterName, x, cal);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        try {
            stmt.setNull(parameterName, sqlType, typeName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public String getString(String parameterName) throws SQLException {
        try {
            return stmt.getString(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean getBoolean(String parameterName) throws SQLException {
        try {
            return stmt.getBoolean(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public byte getByte(String parameterName) throws SQLException {
        try {
            return stmt.getByte(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public short getShort(String parameterName) throws SQLException {
        try {
            return stmt.getShort(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public int getInt(String parameterName) throws SQLException {
        try {
            return stmt.getInt(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public long getLong(String parameterName) throws SQLException {
        try {
            return stmt.getLong(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public float getFloat(String parameterName) throws SQLException {
        try {
            return stmt.getFloat(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public double getDouble(String parameterName) throws SQLException {
        try {
            return stmt.getDouble(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public byte[] getBytes(String parameterName) throws SQLException {
        try {
            return stmt.getBytes(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Date getDate(String parameterName) throws SQLException {
        try {
            return stmt.getDate(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Time getTime(String parameterName) throws SQLException {
        try {
            return stmt.getTime(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Timestamp getTimestamp(String parameterName) throws SQLException {
        try {
            return stmt.getTimestamp(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Object getObject(String parameterName) throws SQLException {
        try {
            Object obj = stmt.getObject(parameterName);
            return wrapObject(obj);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        try {
            return stmt.getBigDecimal(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Object getObject(String parameterName, java.util.Map<String, Class<?>> map) throws SQLException {
        try {
            Object obj = stmt.getObject(parameterName, map);
            return wrapObject(obj);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Ref getRef(String parameterName) throws SQLException {
        try {
            return stmt.getRef(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Blob getBlob(String parameterName) throws SQLException {
        try {
            return stmt.getBlob(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Clob getClob(String parameterName) throws SQLException {
        try {
            return stmt.getClob(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Array getArray(String parameterName) throws SQLException {
        try {
            return stmt.getArray(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Date getDate(String parameterName, Calendar cal) throws SQLException {
        try {
            return stmt.getDate(parameterName, cal);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Time getTime(String parameterName, Calendar cal) throws SQLException {
        try {
            return stmt.getTime(parameterName, cal);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        try {
            return stmt.getTimestamp(parameterName, cal);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.net.URL getURL(String parameterName) throws SQLException {
        try {
            return stmt.getURL(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public RowId getRowId(int parameterIndex) throws SQLException {
        try {
            return stmt.getRowId(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public RowId getRowId(String parameterName) throws SQLException {
        try {
            return stmt.getRowId(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setRowId(String parameterName, RowId x) throws SQLException {
        try {
            stmt.setRowId(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setNString(String parameterName, String value) throws SQLException {
        try {
            stmt.setNString(parameterName, value);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader value, long length) throws SQLException {
        try {
            stmt.setNCharacterStream(parameterName, value, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setNClob(String parameterName, NClob value) throws SQLException {
        try {
            stmt.setNClob(parameterName, value);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setClob(String parameterName, Reader reader, long length) throws SQLException {
        try {
            stmt.setClob(parameterName, reader, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setBlob(String parameterName, InputStream inputStream, long length) throws SQLException {
        try {
            stmt.setBlob(parameterName, inputStream, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setNClob(String parameterName, Reader reader, long length) throws SQLException {
        try {
            stmt.setNClob(parameterName, reader, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public NClob getNClob(int parameterIndex) throws SQLException {
        try {
            return stmt.getNClob(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public NClob getNClob(String parameterName) throws SQLException {
        try {
            return stmt.getNClob(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setSQLXML(String parameterName, SQLXML xmlObject) throws SQLException {
        try {
            stmt.setSQLXML(parameterName, xmlObject);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        try {
            return stmt.getSQLXML(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public SQLXML getSQLXML(String parameterName) throws SQLException {
        try {
            return stmt.getSQLXML(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public String getNString(int parameterIndex) throws SQLException {
        try {
            return stmt.getNString(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public String getNString(String parameterName) throws SQLException {
        try {
            return stmt.getNString(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.io.Reader getNCharacterStream(int parameterIndex) throws SQLException {
        try {
            return stmt.getNCharacterStream(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.io.Reader getNCharacterStream(String parameterName) throws SQLException {
        try {
            return stmt.getNCharacterStream(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.io.Reader getCharacterStream(int parameterIndex) throws SQLException {
        try {
            return stmt.getCharacterStream(parameterIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.io.Reader getCharacterStream(String parameterName) throws SQLException {
        try {
            return stmt.getCharacterStream(parameterName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setBlob(String parameterName, Blob x) throws SQLException {
        try {
            stmt.setBlob(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setClob(String parameterName, Clob x) throws SQLException {
        try {
            stmt.setClob(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setAsciiStream(String parameterName, java.io.InputStream x, long length) throws SQLException {
        try {
            stmt.setAsciiStream(parameterName, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setBinaryStream(String parameterName, java.io.InputStream x, long length) throws SQLException {
        try {
            stmt.setBinaryStream(parameterName, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setCharacterStream(String parameterName, java.io.Reader reader, long length) throws SQLException {
        try {
            stmt.setCharacterStream(parameterName, reader, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setAsciiStream(String parameterName, java.io.InputStream x) throws SQLException {
        try {
            stmt.setAsciiStream(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setBinaryStream(String parameterName, java.io.InputStream x) throws SQLException {
        try {
            stmt.setBinaryStream(parameterName, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setCharacterStream(String parameterName, java.io.Reader reader) throws SQLException {
        try {
            stmt.setCharacterStream(parameterName, reader);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader value) throws SQLException {
        try {
            stmt.setNCharacterStream(parameterName, value);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setClob(String parameterName, Reader reader) throws SQLException {
        try {
            stmt.setClob(parameterName, reader);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setBlob(String parameterName, InputStream inputStream) throws SQLException {
        try {
            stmt.setBlob(parameterName, inputStream);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setNClob(String parameterName, Reader reader) throws SQLException {
        try {
            stmt.setNClob(parameterName, reader);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == CallableStatement.class || iface == PreparedStatement.class) {
            if (stmt instanceof CallableStatementProxy) {
                return stmt.unwrap(iface);
            }
            return (T) stmt;
        }
        
        return super.unwrap(iface);
    }
}
