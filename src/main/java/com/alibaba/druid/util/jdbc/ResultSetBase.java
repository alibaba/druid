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
package com.alibaba.druid.util.jdbc;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;

public abstract class ResultSetBase implements ResultSet {

    protected boolean           closed         = false;
    protected boolean           wasNull        = false;
    private SQLWarning          warning;
    private String              cursorName;
    private int                 fetchSize      = 0;
    private int                 fetchDirection = 0;

    protected Statement         statement;
    protected ResultSetMetaData metaData;

    public ResultSetBase(Statement statement){
        super();
        this.statement = statement;
    }

    @Override
    public boolean isClosed() throws SQLException {
        return closed;
    }

    @Override
    public void updateNString(int columnIndex, String x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateNString(String columnLabel, String x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateNClob(int columnIndex, NClob x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateNClob(String columnLabel, NClob x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        return (NClob) getObject(columnIndex);
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        return (NClob) getObject(columnLabel);
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        return (SQLXML) getObject(columnIndex);
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return (SQLXML) getObject(columnLabel);
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        return (String) getObject(columnIndex);
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        return (String) getObject(columnLabel);
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return (Reader) getObject(columnIndex);
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return (Reader) getObject(columnLabel);
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader x, long length) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader x, long length) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateBlob(int columnIndex, InputStream x, long length) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateBlob(String columnLabel, InputStream x, long length) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateClob(int columnIndex, Reader x, long length) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateClob(String columnLabel, Reader x, long length) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateNClob(int columnIndex, Reader x, long length) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateNClob(String columnLabel, Reader x, long length) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateBlob(int columnIndex, InputStream x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateBlob(String columnLabel, InputStream x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateClob(int columnIndex, Reader x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateClob(String columnLabel, Reader x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateNClob(int columnIndex, Reader x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateNClob(String columnLabel, Reader x) throws SQLException {
        updateObject(columnLabel, x);
    }

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == null) {
            return null;
        }

        if (iface.isInstance(this)) {
            return (T) this;
        }

        return null;
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface == null) {
            return false;
        }

        return iface.isInstance(this);
    }

    @Override
    public void close() throws SQLException {
        this.closed = true;
    }

    @Override
    public boolean wasNull() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return wasNull;
    }

    public Object getObjectInternal(int columnIndex) throws SQLException {
        if (this.getMetaData() != null) {
            String columnName = this.getMetaData().getColumnName(columnIndex);
            return getObject(columnName);
        }

        return null;
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        Object obj = getObjectInternal(columnIndex);

        wasNull = (obj == null);

        return obj;
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return getObject(findColumn(columnLabel));
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        return Integer.parseInt(columnLabel);
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return (Reader) getObject(columnIndex);
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return (Reader) getObject(columnLabel);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return (BigDecimal) getObject(columnIndex);
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return getBigDecimal(findColumn(columnLabel));
    }

    @Override
    public void clearWarnings() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        warning = null;
    }

    public void setWarning(SQLWarning warning) {
        this.warning = warning;
    }

    @Override
    public String getCursorName() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return cursorName;
    }

    public void setCursorName(String cursorName) {
        this.cursorName = cursorName;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return warning;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        this.fetchDirection = direction;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return fetchDirection;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        this.fetchSize = rows;
    }

    @Override
    public int getFetchSize() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return fetchSize;
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return false;
    }

    @Override
    public boolean rowInserted() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return false;
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return false;
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        updateObject(columnIndex, null);
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        updateObject(columnLabel, null);
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader reader, int length) throws SQLException {
        updateObject(columnLabel, reader);
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        updateObject(findColumn(columnLabel), x);
    }

    @Override
    public void insertRow() throws SQLException {
        if (closed) {
            throw new SQLException("resultSet closed");
        }
    }

    @Override
    public void updateRow() throws SQLException {
        if (closed) {
            throw new SQLException("resultSet closed");
        }
    }

    @Override
    public void deleteRow() throws SQLException {
        if (closed) {
            throw new SQLException("resultSet closed");
        }
    }

    @Override
    public void refreshRow() throws SQLException {
        if (closed) {
            throw new SQLException("resultSet closed");
        }
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        if (closed) {
            throw new SQLException("resultSet closed");
        }
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        if (closed) {
            throw new SQLException("resultSet closed");
        }
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        if (closed) {
            throw new SQLException("resultSet closed");
        }
    }

    @Override
    public Statement getStatement() throws SQLException {
        if (closed) {
            throw new SQLException("resultSet closed");
        }
        return statement;
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return getObject(columnIndex);
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        return (Ref) getObject(columnIndex);
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        return (Blob) getObject(columnIndex);
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        return (Clob) getObject(columnIndex);
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        return (Array) getObject(columnIndex);
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return getObject(columnLabel);
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        return (Ref) getObject(columnLabel);
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        return (Blob) getObject(columnLabel);
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        return (Clob) getObject(columnLabel);
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        return (Array) getObject(columnLabel);
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return (Date) getObject(columnIndex);
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return (Date) getObject(columnLabel);
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return (Time) getObject(columnIndex);
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return (Time) getObject(columnLabel);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return (Timestamp) getObject(columnIndex);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return (Timestamp) getObject(columnLabel);
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        return (URL) getObject(columnIndex);
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        return (URL) getObject(columnLabel);
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        return (RowId) getObject(columnIndex);
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        return (RowId) getObject(columnLabel);
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        updateObject(columnIndex, x);
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        updateObject(columnLabel, x);
    }

    @Override
    public int getHoldability() throws SQLException {
        if (closed) {
            throw new SQLException("resultSet closed");
        }

        return 0;
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

        return (Boolean) obj;
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
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        return (InputStream) getObject(columnIndex);
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return (InputStream) getObject(columnIndex);
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        return (InputStream) getObject(columnIndex);
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        return getString(findColumn(columnLabel));
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return getBoolean(findColumn(columnLabel));
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return getByte(findColumn(columnLabel));
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return getShort(findColumn(columnLabel));
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return getInt(findColumn(columnLabel));
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return getLong(findColumn(columnLabel));
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return getFloat(findColumn(columnLabel));
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return getDouble(findColumn(columnLabel));
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return getBigDecimal(findColumn(columnLabel), scale);
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        return getBytes(findColumn(columnLabel));
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return getDate(findColumn(columnLabel));
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return getTime(findColumn(columnLabel));
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return getTimestamp(findColumn(columnLabel));
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return getAsciiStream(findColumn(columnLabel));
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return getUnicodeStream(findColumn(columnLabel));
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return getBinaryStream(findColumn(columnLabel));
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return false;
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        if (closed) {
            throw new SQLException();
        }
        return false;
    }

    @Override
    public boolean isFirst() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return false;
    }

    @Override
    public boolean isLast() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return false;
    }

    @Override
    public void beforeFirst() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

    }

    @Override
    public void afterLast() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

    }

    @Override
    public boolean first() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return false;
    }

    @Override
    public boolean last() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return false;
    }

    @Override
    public int getRow() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return 0;
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return false;
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return false;
    }

    @Override
    public int getType() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return 0;
    }

    @Override
    public int getConcurrency() throws SQLException {
        if (closed) {
            throw new SQLException();
        }

        return 0;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        if (closed) {
            throw new SQLException("resultSet closed");
        }

        return metaData;
    }
}
