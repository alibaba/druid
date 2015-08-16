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
package com.alibaba.druid.pool;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
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
import java.util.Calendar;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public final class DruidPooledResultSet extends PoolableWrapper implements ResultSet {

    private final ResultSet         rs;
    private final DruidPooledStatement stmt;
    private boolean                 closed        = false;

    protected int                   cursorIndex   = 0;
    protected int                   fetchRowCount = 0;

    public DruidPooledResultSet(DruidPooledStatement stmt, ResultSet rs){
        super(rs);
        this.stmt = stmt;
        this.rs = rs;
    }

    protected SQLException checkException(Throwable error) throws SQLException {
        return stmt.checkException(error);
    }

    public DruidPooledStatement getPoolableStatement() {
        return stmt;
    }

    public ResultSet getRawResultSet() {
        return rs;
    }

    @Override
    public boolean next() throws SQLException {
        try {
            boolean moreRows = rs.next();

            if (moreRows) {
                cursorIndex++;
                if (cursorIndex > fetchRowCount) {
                    fetchRowCount = cursorIndex;
                }
            }
            return moreRows;
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void close() throws SQLException {
        try {
            this.closed = true;
            rs.close();
            
            stmt.recordFetchRowCount(fetchRowCount);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }
    
    public int getFetchRowCount() {
        return fetchRowCount;
    }

    @Override
    public boolean wasNull() throws SQLException {
        try {
            return rs.wasNull();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        try {
            return rs.getString(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        try {
            return rs.getBoolean(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        try {
            return rs.getByte(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        try {
            return rs.getShort(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        try {
            return rs.getInt(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        try {
            return rs.getLong(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        try {
            return rs.getFloat(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        try {
            return rs.getDouble(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    @Deprecated
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        try {
            return rs.getBigDecimal(columnIndex, scale);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        try {
            return rs.getBytes(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Date getDate(int columnIndex) throws SQLException {
        try {
            return rs.getDate(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Time getTime(int columnIndex) throws SQLException {
        try {
            return rs.getTime(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Timestamp getTimestamp(int columnIndex) throws SQLException {
        try {
            return rs.getTimestamp(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.io.InputStream getAsciiStream(int columnIndex) throws SQLException {
        try {
            return rs.getAsciiStream(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    @Deprecated
    public java.io.InputStream getUnicodeStream(int columnIndex) throws SQLException {
        try {
            return rs.getUnicodeStream(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.io.InputStream getBinaryStream(int columnIndex) throws SQLException {
        try {
            return rs.getBinaryStream(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        try {
            return rs.getString(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        try {
            return rs.getBoolean(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        try {
            return rs.getByte(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        try {
            return rs.getShort(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        try {
            return rs.getInt(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        try {
            return rs.getLong(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        try {
            return rs.getFloat(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        try {
            return rs.getDouble(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    @Deprecated
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        try {
            return rs.getBigDecimal(columnLabel, scale);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        try {
            return rs.getBytes(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Date getDate(String columnLabel) throws SQLException {
        try {
            return rs.getDate(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Time getTime(String columnLabel) throws SQLException {
        try {
            return rs.getTime(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Timestamp getTimestamp(String columnLabel) throws SQLException {
        try {
            return rs.getTimestamp(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.io.InputStream getAsciiStream(String columnLabel) throws SQLException {
        try {
            return rs.getAsciiStream(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    @Deprecated
    public java.io.InputStream getUnicodeStream(String columnLabel) throws SQLException {
        try {
            return rs.getUnicodeStream(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.io.InputStream getBinaryStream(String columnLabel) throws SQLException {
        try {
            return rs.getBinaryStream(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        try {
            return rs.getWarnings();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void clearWarnings() throws SQLException {
        try {
            rs.clearWarnings();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public String getCursorName() throws SQLException {
        try {
            return rs.getCursorName();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        try {
            return rs.getMetaData();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        try {
            return rs.getObject(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        try {
            return rs.getObject(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        try {
            return rs.findColumn(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.io.Reader getCharacterStream(int columnIndex) throws SQLException {
        try {
            return rs.getCharacterStream(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.io.Reader getCharacterStream(String columnLabel) throws SQLException {
        try {
            return rs.getCharacterStream(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        try {
            return rs.getBigDecimal(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        try {
            return rs.getBigDecimal(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        try {
            return rs.isBeforeFirst();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        try {
            return rs.isAfterLast();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean isFirst() throws SQLException {
        try {
            return rs.isFirst();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean isLast() throws SQLException {
        try {
            return rs.isLast();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void beforeFirst() throws SQLException {
        try {
            rs.beforeFirst();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void afterLast() throws SQLException {
        try {
            rs.afterLast();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean first() throws SQLException {
        try {
            return rs.first();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean last() throws SQLException {
        try {
            return rs.last();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public int getRow() throws SQLException {
        try {
            return rs.getRow();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        try {
            return rs.absolute(row);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        try {
            return rs.relative(rows);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean previous() throws SQLException {
        try {
            boolean moreRows = rs.previous();

            if (moreRows) {
                cursorIndex--;
            }

            return moreRows;
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        try {
            rs.setFetchDirection(direction);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public int getFetchDirection() throws SQLException {
        try {
            return rs.getFetchDirection();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        try {
            rs.setFetchSize(rows);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public int getFetchSize() throws SQLException {
        try {
            return rs.getFetchSize();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public int getType() throws SQLException {
        try {
            return rs.getType();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public int getConcurrency() throws SQLException {
        try {
            return rs.getConcurrency();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        try {
            return rs.rowUpdated();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean rowInserted() throws SQLException {
        try {
            return rs.rowInserted();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        try {
            return rs.rowDeleted();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        try {
            rs.updateNull(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        try {
            rs.updateBoolean(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        try {
            rs.updateByte(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        try {
            rs.updateShort(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        try {
            rs.updateInt(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        try {
            rs.updateLong(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        try {
            rs.updateFloat(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        try {
            rs.updateDouble(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        try {
            rs.updateBigDecimal(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        try {
            rs.updateString(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateBytes(int columnIndex, byte x[]) throws SQLException {
        try {
            rs.updateBytes(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateDate(int columnIndex, java.sql.Date x) throws SQLException {
        try {
            rs.updateDate(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateTime(int columnIndex, java.sql.Time x) throws SQLException {
        try {
            rs.updateTime(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateTimestamp(int columnIndex, java.sql.Timestamp x) throws SQLException {
        try {
            rs.updateTimestamp(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateAsciiStream(int columnIndex, java.io.InputStream x, int length) throws SQLException {
        try {
            rs.updateAsciiStream(columnIndex, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateBinaryStream(int columnIndex, java.io.InputStream x, int length) throws SQLException {
        try {
            rs.updateBinaryStream(columnIndex, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x, int length) throws SQLException {
        try {
            rs.updateCharacterStream(columnIndex, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        try {
            rs.updateObject(columnIndex, x, scaleOrLength);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        try {
            rs.updateObject(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        try {
            rs.updateNull(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        try {
            rs.updateBoolean(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        try {
            rs.updateByte(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        try {
            rs.updateShort(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        try {
            rs.updateInt(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        try {
            rs.updateLong(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        try {
            rs.updateFloat(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        try {
            rs.updateDouble(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        try {
            rs.updateBigDecimal(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        try {
            rs.updateString(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateBytes(String columnLabel, byte x[]) throws SQLException {
        try {
            rs.updateBytes(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateDate(String columnLabel, java.sql.Date x) throws SQLException {
        try {
            rs.updateDate(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateTime(String columnLabel, java.sql.Time x) throws SQLException {
        try {
            rs.updateTime(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateTimestamp(String columnLabel, java.sql.Timestamp x) throws SQLException {
        try {
            rs.updateTimestamp(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateAsciiStream(String columnLabel, java.io.InputStream x, int length) throws SQLException {
        try {
            rs.updateAsciiStream(columnLabel, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateBinaryStream(String columnLabel, java.io.InputStream x, int length) throws SQLException {
        try {
            rs.updateBinaryStream(columnLabel, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader, int length) throws SQLException {
        try {
            rs.updateCharacterStream(columnLabel, reader, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        try {
            rs.updateObject(columnLabel, x, scaleOrLength);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        try {
            rs.updateObject(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void insertRow() throws SQLException {
        try {
            rs.insertRow();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateRow() throws SQLException {
        try {
            rs.updateRow();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void deleteRow() throws SQLException {
        try {
            rs.deleteRow();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void refreshRow() throws SQLException {
        try {
            rs.refreshRow();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        try {
            rs.cancelRowUpdates();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        try {
            rs.moveToInsertRow();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        try {
            rs.moveToCurrentRow();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Statement getStatement() {
        return stmt;
    }

    @Override
    public Object getObject(int columnIndex, java.util.Map<String, Class<?>> map) throws SQLException {
        try {
            return rs.getObject(columnIndex, map);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        try {
            return rs.getRef(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        try {
            return rs.getBlob(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        try {
            return rs.getClob(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        try {
            return rs.getArray(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Object getObject(String columnLabel, java.util.Map<String, Class<?>> map) throws SQLException {
        try {
            return rs.getObject(columnLabel, map);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        try {
            return rs.getRef(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        try {
            return rs.getBlob(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        try {
            return rs.getClob(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        try {
            return rs.getArray(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Date getDate(int columnIndex, Calendar cal) throws SQLException {
        try {
            return rs.getDate(columnIndex, cal);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Date getDate(String columnLabel, Calendar cal) throws SQLException {
        try {
            return rs.getDate(columnLabel, cal);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Time getTime(int columnIndex, Calendar cal) throws SQLException {
        try {
            return rs.getTime(columnIndex, cal);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Time getTime(String columnLabel, Calendar cal) throws SQLException {
        try {
            return rs.getTime(columnLabel, cal);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        try {
            return rs.getTimestamp(columnIndex, cal);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.sql.Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        try {
            return rs.getTimestamp(columnLabel, cal);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.net.URL getURL(int columnIndex) throws SQLException {
        try {
            return rs.getURL(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.net.URL getURL(String columnLabel) throws SQLException {
        try {
            return rs.getURL(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateRef(int columnIndex, java.sql.Ref x) throws SQLException {
        try {
            rs.updateRef(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateRef(String columnLabel, java.sql.Ref x) throws SQLException {
        try {
            rs.updateRef(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateBlob(int columnIndex, java.sql.Blob x) throws SQLException {
        try {
            rs.updateBlob(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateBlob(String columnLabel, java.sql.Blob x) throws SQLException {
        try {
            rs.updateBlob(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateClob(int columnIndex, java.sql.Clob x) throws SQLException {
        try {
            rs.updateClob(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateClob(String columnLabel, java.sql.Clob x) throws SQLException {
        try {
            rs.updateClob(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateArray(int columnIndex, java.sql.Array x) throws SQLException {
        try {
            rs.updateArray(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateArray(String columnLabel, java.sql.Array x) throws SQLException {
        try {
            rs.updateArray(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        try {
            return rs.getRowId(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        try {
            return rs.getRowId(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        try {
            rs.updateRowId(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        try {
            rs.updateRowId(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public int getHoldability() throws SQLException {
        try {
            return rs.getHoldability();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean isClosed() throws SQLException {
        return this.closed;
    }

    @Override
    public void updateNString(int columnIndex, String nString) throws SQLException {
        try {
            rs.updateNString(columnIndex, nString);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateNString(String columnLabel, String nString) throws SQLException {
        try {
            rs.updateNString(columnLabel, nString);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateNClob(int columnIndex, NClob nClob) throws SQLException {
        try {
            rs.updateNClob(columnIndex, nClob);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateNClob(String columnLabel, NClob nClob) throws SQLException {
        try {
            rs.updateNClob(columnLabel, nClob);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        try {
            return rs.getNClob(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        try {
            return rs.getNClob(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        try {
            return rs.getSQLXML(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        try {
            return rs.getSQLXML(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException {
        try {
            rs.updateSQLXML(columnIndex, xmlObject);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException {
        try {
            rs.updateSQLXML(columnLabel, xmlObject);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        try {
            return rs.getNString(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        try {
            return rs.getNString(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.io.Reader getNCharacterStream(int columnIndex) throws SQLException {
        try {
            return rs.getNCharacterStream(columnIndex);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public java.io.Reader getNCharacterStream(String columnLabel) throws SQLException {
        try {
            return rs.getNCharacterStream(columnLabel);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateNCharacterStream(int columnIndex, java.io.Reader x, long length) throws SQLException {
        try {
            rs.updateNCharacterStream(columnIndex, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateNCharacterStream(String columnLabel, java.io.Reader reader, long length) throws SQLException {
        try {
            rs.updateNCharacterStream(columnLabel, reader, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateAsciiStream(int columnIndex, java.io.InputStream x, long length) throws SQLException {
        try {
            rs.updateAsciiStream(columnIndex, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateBinaryStream(int columnIndex, java.io.InputStream x, long length) throws SQLException {
        try {
            rs.updateBinaryStream(columnIndex, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x, long length) throws SQLException {
        try {
            rs.updateCharacterStream(columnIndex, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateAsciiStream(String columnLabel, java.io.InputStream x, long length) throws SQLException {
        try {
            rs.updateAsciiStream(columnLabel, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateBinaryStream(String columnLabel, java.io.InputStream x, long length) throws SQLException {
        try {
            rs.updateBinaryStream(columnLabel, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader, long length) throws SQLException {
        try {
            rs.updateCharacterStream(columnLabel, reader, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException {
        try {
            rs.updateBlob(columnIndex, inputStream, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException {
        try {
            rs.updateBlob(columnLabel, inputStream, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateClob(int columnIndex, Reader reader, long length) throws SQLException {
        try {
            rs.updateClob(columnIndex, reader, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateClob(String columnLabel, Reader reader, long length) throws SQLException {
        try {
            rs.updateClob(columnLabel, reader, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException {
        try {
            rs.updateNClob(columnIndex, reader, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException {
        try {
            rs.updateNClob(columnLabel, reader, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateNCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
        try {
            rs.updateNCharacterStream(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateNCharacterStream(String columnLabel, java.io.Reader reader) throws SQLException {
        try {
            rs.updateNCharacterStream(columnLabel, reader);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateAsciiStream(int columnIndex, java.io.InputStream x) throws SQLException {
        try {
            rs.updateAsciiStream(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateBinaryStream(int columnIndex, java.io.InputStream x) throws SQLException {
        try {
            rs.updateBinaryStream(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateCharacterStream(int columnIndex, java.io.Reader x) throws SQLException {
        try {
            rs.updateCharacterStream(columnIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateAsciiStream(String columnLabel, java.io.InputStream x) throws SQLException {
        try {
            rs.updateAsciiStream(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateBinaryStream(String columnLabel, java.io.InputStream x) throws SQLException {
        try {
            rs.updateBinaryStream(columnLabel, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateCharacterStream(String columnLabel, java.io.Reader reader) throws SQLException {
        try {
            rs.updateCharacterStream(columnLabel, reader);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException {
        try {
            rs.updateBlob(columnIndex, inputStream);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException {
        try {
            rs.updateBlob(columnLabel, inputStream);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateClob(int columnIndex, Reader reader) throws SQLException {
        try {
            rs.updateClob(columnIndex, reader);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateClob(String columnLabel, Reader reader) throws SQLException {
        try {
            rs.updateClob(columnLabel, reader);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateNClob(int columnIndex, Reader reader) throws SQLException {
        try {
            rs.updateNClob(columnIndex, reader);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void updateNClob(String columnLabel, Reader reader) throws SQLException {
        try {
            rs.updateNClob(columnLabel, reader);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
