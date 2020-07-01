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
package com.alibaba.druid.mock;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class MockCallableStatement extends MockPreparedStatement implements CallableStatement {

    private final List<Object> outParameters = new ArrayList<Object>();
    private boolean            wasNull    = false;

    public MockCallableStatement(MockConnection conn, String sql){
        super(conn, sql);
    }

    public List<Object> getOutParameters() {
        return outParameters;
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType) throws SQLException {
        if (parameterIndex < 1) {
            throw new SQLException();
        }

        if (outParameters.size() >= parameterIndex - 1) {
            outParameters.add(null);
        } else {
            throw new SQLException();
        }
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, int scale) throws SQLException {
        registerOutParameter(parameterIndex, sqlType);
    }

    @Override
    public boolean wasNull() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return wasNull;
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        return (String) getObject(columnIndex);
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        Object obj = getObject(columnIndex);

        if (obj == null) {
            return false;
        }

        return ((Boolean) obj).booleanValue();
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        Number number = (Number) getObject(columnIndex);

        if (number == null) {
            return 0;
        }

        return number.byteValue();
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        Number number = (Number) getObject(columnIndex);

        if (number == null) {
            return 0;
        }

        return number.shortValue();
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        Number number = (Number) getObject(columnIndex);

        if (number == null) {
            return 0;
        }

        return number.intValue();
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        Number number = (Number) getObject(columnIndex);

        if (number == null) {
            return 0;
        }

        return number.longValue();
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        Number number = (Number) getObject(columnIndex);

        if (number == null) {
            return 0;
        }

        return number.floatValue();
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        Number number = (Number) getObject(columnIndex);

        if (number == null) {
            return 0;
        }

        return number.doubleValue();
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return (BigDecimal) getObject(columnIndex);
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        return (byte[]) getObject(columnIndex);
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        return (Date) getObject(columnIndex);
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        return (Time) getObject(columnIndex);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return (Timestamp) getObject(columnIndex);
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        return getString(Integer.parseInt(columnLabel));
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return getBoolean(Integer.parseInt(columnLabel));
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return getByte(Integer.parseInt(columnLabel));
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return getShort(Integer.parseInt(columnLabel));
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return getInt(Integer.parseInt(columnLabel));
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return getLong(Integer.parseInt(columnLabel));
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return getFloat(Integer.parseInt(columnLabel));
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return getDouble(Integer.parseInt(columnLabel));
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        return getBytes(Integer.parseInt(columnLabel));
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return getDate(Integer.parseInt(columnLabel));
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return getTime(Integer.parseInt(columnLabel));
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return getTimestamp(Integer.parseInt(columnLabel));
    }

    @Override
    public Object getObject(int parameterIndex) throws SQLException {
        Object obj = outParameters.get(parameterIndex - 1);

        wasNull = (obj == null);

        return obj;
    }

    @Override
    public BigDecimal getBigDecimal(int parameterIndex) throws SQLException {
        return (BigDecimal) getObject(parameterIndex);
    }

    @Override
    public Object getObject(int parameterIndex, Map<String, Class<?>> map) throws SQLException {
        return getObject(parameterIndex);
    }

    @Override
    public Ref getRef(int parameterIndex) throws SQLException {
        return (Ref) getObject(parameterIndex);
    }

    @Override
    public Blob getBlob(int parameterIndex) throws SQLException {
        return (Blob) getObject(parameterIndex);
    }

    @Override
    public Clob getClob(int parameterIndex) throws SQLException {
        return (Clob) getObject(parameterIndex);
    }

    @Override
    public Array getArray(int parameterIndex) throws SQLException {
        return (Array) getObject(parameterIndex);
    }

    @Override
    public Date getDate(int parameterIndex, Calendar cal) throws SQLException {
        return (Date) getObject(parameterIndex);
    }

    @Override
    public Time getTime(int parameterIndex, Calendar cal) throws SQLException {
        return (Time) getObject(parameterIndex);
    }

    @Override
    public Timestamp getTimestamp(int parameterIndex, Calendar cal) throws SQLException {
        return (Timestamp) getObject(parameterIndex);
    }

    @Override
    public void registerOutParameter(int parameterIndex, int sqlType, String typeName) throws SQLException {
        registerOutParameter(parameterIndex, sqlType);
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType) throws SQLException {
        registerOutParameter(Integer.parseInt(parameterName), sqlType);
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, int scale) throws SQLException {
        registerOutParameter(parameterName, sqlType);
    }

    @Override
    public void registerOutParameter(String parameterName, int sqlType, String typeName) throws SQLException {
        registerOutParameter(parameterName, sqlType);
    }

    @Override
    public URL getURL(int parameterIndex) throws SQLException {
        return (URL) getObject(parameterIndex);
    }

    @Override
    public void setURL(String parameterName, URL x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setNull(String parameterName, int sqlType) throws SQLException {
        setObject(parameterName, null);
    }

    @Override
    public void setBoolean(String parameterName, boolean x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setByte(String parameterName, byte x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setShort(String parameterName, short x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setInt(String parameterName, int x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setLong(String parameterName, long x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setFloat(String parameterName, float x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setDouble(String parameterName, double x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setBigDecimal(String parameterName, BigDecimal x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setString(String parameterName, String x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setBytes(String parameterName, byte[] x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setDate(String parameterName, Date x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setTime(String parameterName, Time x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x, int length) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x, int length) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType, int scale) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setObject(String parameterName, Object x, int targetSqlType) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setObject(String parameterName, Object x) throws SQLException {
        setObject(Integer.parseInt(parameterName), x);
    }

    @Override
    public void setCharacterStream(String parameterName, Reader x, int length) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setDate(String parameterName, Date x, Calendar cal) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setTime(String parameterName, Time x, Calendar cal) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setTimestamp(String parameterName, Timestamp x, Calendar cal) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setNull(String parameterName, int sqlType, String typeName) throws SQLException {
        setObject(parameterName, null);
    }

    @Override
    public Object getObject(String parameterName) throws SQLException {
        return getObject(Integer.parseInt(parameterName));
    }

    @Override
    public BigDecimal getBigDecimal(String parameterName) throws SQLException {
        return getBigDecimal(Integer.parseInt(parameterName));
    }

    @Override
    public Object getObject(String parameterName, Map<String, Class<?>> map) throws SQLException {
        return getObject(parameterName);
    }

    @Override
    public Ref getRef(String parameterName) throws SQLException {
        return (Ref) getObject(parameterName);
    }

    @Override
    public Blob getBlob(String parameterName) throws SQLException {
        return (Blob) getObject(parameterName);
    }

    @Override
    public Clob getClob(String parameterName) throws SQLException {
        return (Clob) getObject(parameterName);
    }

    @Override
    public Array getArray(String parameterName) throws SQLException {
        return (Array) getObject(parameterName);
    }

    @Override
    public Date getDate(String parameterName, Calendar cal) throws SQLException {
        return (Date) getObject(parameterName);
    }

    @Override
    public Time getTime(String parameterName, Calendar cal) throws SQLException {
        return (Time) getObject(parameterName);
    }

    @Override
    public Timestamp getTimestamp(String parameterName, Calendar cal) throws SQLException {
        return (Timestamp) getObject(parameterName);
    }

    @Override
    public URL getURL(String parameterName) throws SQLException {
        return (URL) getObject(parameterName);
    }

    @Override
    public RowId getRowId(int parameterIndex) throws SQLException {
        return (RowId) getObject(parameterIndex);
    }

    @Override
    public RowId getRowId(String parameterName) throws SQLException {
        return (RowId) getObject(parameterName);
    }

    @Override
    public void setRowId(String parameterName, RowId x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setNString(String parameterName, String x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader x, long length) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setNClob(String parameterName, NClob x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setClob(String parameterName, Reader x, long length) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setBlob(String parameterName, InputStream x, long length) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setNClob(String parameterName, Reader x, long length) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public NClob getNClob(int parameterIndex) throws SQLException {
        return (NClob) getObject(parameterIndex);
    }

    @Override
    public NClob getNClob(String parameterName) throws SQLException {
        return (NClob) getObject(parameterName);
    }

    @Override
    public void setSQLXML(String parameterName, SQLXML x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public SQLXML getSQLXML(int parameterIndex) throws SQLException {
        return (SQLXML) getObject(parameterIndex);
    }

    @Override
    public SQLXML getSQLXML(String parameterName) throws SQLException {
        return (SQLXML) getObject(parameterName);
    }

    @Override
    public String getNString(int parameterIndex) throws SQLException {
        return (String) getObject(parameterIndex);
    }

    @Override
    public String getNString(String parameterName) throws SQLException {
        return (String) getObject(parameterName);
    }

    @Override
    public Reader getNCharacterStream(int parameterIndex) throws SQLException {
        return (Reader) getObject(parameterIndex);
    }

    @Override
    public Reader getNCharacterStream(String parameterName) throws SQLException {
        return (Reader) getObject(parameterName);
    }

    @Override
    public Reader getCharacterStream(int parameterIndex) throws SQLException {
        return (Reader) getObject(parameterIndex);
    }

    @Override
    public Reader getCharacterStream(String parameterName) throws SQLException {
        return (Reader) getObject(parameterName);
    }

    @Override
    public void setBlob(String parameterName, Blob x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setClob(String parameterName, Clob x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x, long length) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x, long length) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setCharacterStream(String parameterName, Reader x, long length) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setAsciiStream(String parameterName, InputStream x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setBinaryStream(String parameterName, InputStream x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setCharacterStream(String parameterName, Reader x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setNCharacterStream(String parameterName, Reader x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setClob(String parameterName, Reader x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setBlob(String parameterName, InputStream x) throws SQLException {
        setObject(parameterName, x);
    }

    @Override
    public void setNClob(String parameterName, Reader x) throws SQLException {
        setObject(parameterName, x);
    }

    public <T> T getObject(int parameterIndex, Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public <T> T getObject(String parameterName, Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

}
