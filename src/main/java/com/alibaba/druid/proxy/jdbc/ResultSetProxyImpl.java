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
package com.alibaba.druid.proxy.jdbc;

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

import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.filter.FilterChainImpl;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public class ResultSetProxyImpl extends WrapperProxyImpl implements ResultSetProxy {

    private final ResultSet      resultSet;
    private final StatementProxy statement;
    private final String         sql;

    protected int                cursorIndex   = 0;
    protected int                fetchRowCount = 0;
    protected long               constructNano;

    public ResultSetProxyImpl(StatementProxy statement, ResultSet resultSet, long id, String sql){
        super(resultSet, id);
        this.statement = statement;
        this.resultSet = resultSet;
        this.sql = sql;
    }

    public long getConstructNano() {
        return constructNano;
    }

    public void setConstructNano(long constructNano) {
        this.constructNano = constructNano;
    }
    
    public void setConstructNano() {
        if (this.constructNano <= 0) {
            this.constructNano = System.nanoTime();
        }
    }

    public int getCursorIndex() {
        return cursorIndex;
    }

    public int getFetchRowCount() {
        return fetchRowCount;
    }

    public String getSql() {
        return sql;
    }

    public ResultSet getResultSetRaw() {
        return resultSet;
    }

    public StatementProxy getStatementProxy() {
        return this.statement;
    }

    public FilterChain createChain() {
        return new FilterChainImpl(this.statement.getConnectionProxy().getDirectDataSource());
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        return createChain().resultSet_absolute(this, row);
    }

    @Override
    public void afterLast() throws SQLException {
        createChain().resultSet_afterLast(this);
    }

    @Override
    public void beforeFirst() throws SQLException {
        createChain().resultSet_beforeFirst(this);
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        createChain().resultSet_cancelRowUpdates(this);
    }

    @Override
    public void clearWarnings() throws SQLException {
        createChain().resultSet_clearWarnings(this);
    }

    @Override
    public void close() throws SQLException {
        createChain().resultSet_close(this);
    }

    @Override
    public void deleteRow() throws SQLException {
        createChain().resultSet_deleteRow(this);
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        return createChain().resultSet_findColumn(this, columnLabel);
    }

    @Override
    public boolean first() throws SQLException {
        return createChain().resultSet_first(this);
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        return createChain().resultSet_getArray(this, columnIndex);
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        return createChain().resultSet_getArray(this, columnLabel);
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        return createChain().resultSet_getAsciiStream(this, columnIndex);
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        return createChain().resultSet_getAsciiStream(this, columnLabel);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        return createChain().resultSet_getBigDecimal(this, columnIndex);
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        return createChain().resultSet_getBigDecimal(this, columnLabel);
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        return createChain().resultSet_getBigDecimal(this, columnIndex, scale);
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        return createChain().resultSet_getBigDecimal(this, columnLabel, scale);
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        return createChain().resultSet_getBinaryStream(this, columnIndex);
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        return createChain().resultSet_getBinaryStream(this, columnLabel);
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        return createChain().resultSet_getBlob(this, columnIndex);
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        return createChain().resultSet_getBlob(this, columnLabel);
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        return createChain().resultSet_getBoolean(this, columnIndex);
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        return createChain().resultSet_getBoolean(this, columnLabel);
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        return createChain().resultSet_getByte(this, columnIndex);
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        return createChain().resultSet_getByte(this, columnLabel);
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        return createChain().resultSet_getBytes(this, columnIndex);
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        return createChain().resultSet_getBytes(this, columnLabel);
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        return createChain().resultSet_getCharacterStream(this, columnIndex);
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        return createChain().resultSet_getCharacterStream(this, columnLabel);
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        return createChain().resultSet_getClob(this, columnIndex);
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        return createChain().resultSet_getClob(this, columnLabel);
    }

    @Override
    public int getConcurrency() throws SQLException {
        return createChain().resultSet_getConcurrency(this);
    }

    @Override
    public String getCursorName() throws SQLException {
        return createChain().resultSet_getCursorName(this);
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        return createChain().resultSet_getDate(this, columnIndex);
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        return createChain().resultSet_getDate(this, columnLabel);
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        return createChain().resultSet_getDate(this, columnIndex, cal);
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        return createChain().resultSet_getDate(this, columnLabel, cal);
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        return createChain().resultSet_getDouble(this, columnIndex);
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        return createChain().resultSet_getDouble(this, columnLabel);
    }

    @Override
    public int getFetchDirection() throws SQLException {
        return createChain().resultSet_getFetchDirection(this);
    }

    @Override
    public int getFetchSize() throws SQLException {
        return createChain().resultSet_getFetchSize(this);
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        return createChain().resultSet_getFloat(this, columnIndex);
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        return createChain().resultSet_getFloat(this, columnLabel);
    }

    @Override
    public int getHoldability() throws SQLException {
        return createChain().resultSet_getHoldability(this);
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        return createChain().resultSet_getInt(this, columnIndex);
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        return createChain().resultSet_getInt(this, columnLabel);
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        return createChain().resultSet_getLong(this, columnIndex);
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        return createChain().resultSet_getLong(this, columnLabel);
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        return createChain().resultSet_getMetaData(this);
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        return createChain().resultSet_getNCharacterStream(this, columnIndex);
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        return createChain().resultSet_getNCharacterStream(this, columnLabel);
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        return createChain().resultSet_getNClob(this, columnIndex);
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        return createChain().resultSet_getNClob(this, columnLabel);
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        return createChain().resultSet_getNString(this, columnIndex);
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        return createChain().resultSet_getNString(this, columnLabel);
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        return createChain().resultSet_getObject(this, columnIndex);
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        return createChain().resultSet_getObject(this, columnLabel);
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        return createChain().resultSet_getObject(this, columnIndex, map);
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        return createChain().resultSet_getObject(this, columnLabel, map);
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        return createChain().resultSet_getRef(this, columnIndex);
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        return createChain().resultSet_getRef(this, columnLabel);
    }

    @Override
    public int getRow() throws SQLException {
        return createChain().resultSet_getRow(this);
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        return createChain().resultSet_getRowId(this, columnIndex);
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        return createChain().resultSet_getRowId(this, columnLabel);
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        return createChain().resultSet_getSQLXML(this, columnIndex);
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        return createChain().resultSet_getSQLXML(this, columnLabel);
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        return createChain().resultSet_getShort(this, columnIndex);
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        return createChain().resultSet_getShort(this, columnLabel);
    }

    @Override
    public Statement getStatement() throws SQLException {
        return createChain().resultSet_getStatement(this);
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        return createChain().resultSet_getString(this, columnIndex);
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        return createChain().resultSet_getString(this, columnLabel);
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        return createChain().resultSet_getTime(this, columnIndex);
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        return createChain().resultSet_getTime(this, columnLabel);
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        return createChain().resultSet_getTime(this, columnIndex, cal);
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        return createChain().resultSet_getTime(this, columnLabel, cal);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        return createChain().resultSet_getTimestamp(this, columnIndex);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        return createChain().resultSet_getTimestamp(this, columnLabel);
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        return createChain().resultSet_getTimestamp(this, columnIndex, cal);
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        return createChain().resultSet_getTimestamp(this, columnLabel, cal);
    }

    @Override
    public int getType() throws SQLException {
        return createChain().resultSet_getType(this);
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        return createChain().resultSet_getURL(this, columnIndex);
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        return createChain().resultSet_getURL(this, columnLabel);
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        return createChain().resultSet_getUnicodeStream(this, columnIndex);
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        return createChain().resultSet_getUnicodeStream(this, columnLabel);
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        return createChain().resultSet_getWarnings(this);
    }

    @Override
    public void insertRow() throws SQLException {
        createChain().resultSet_insertRow(this);
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        return createChain().resultSet_isAfterLast(this);
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        return createChain().resultSet_isBeforeFirst(this);
    }

    @Override
    public boolean isClosed() throws SQLException {
        return createChain().resultSet_isClosed(this);
    }

    @Override
    public boolean isFirst() throws SQLException {
        return createChain().resultSet_isFirst(this);
    }

    @Override
    public boolean isLast() throws SQLException {
        return createChain().resultSet_isLast(this);
    }

    @Override
    public boolean last() throws SQLException {
        return createChain().resultSet_last(this);
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        createChain().resultSet_moveToCurrentRow(this);
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        createChain().resultSet_moveToInsertRow(this);
    }

    @Override
    public boolean next() throws SQLException {
        boolean moreRows = createChain().resultSet_next(this);

        if (moreRows) {
            cursorIndex++;
            if (cursorIndex > fetchRowCount) {
                fetchRowCount = cursorIndex;
            }
        }
        return moreRows;
    }

    @Override
    public boolean previous() throws SQLException {
        boolean moreRows = createChain().resultSet_previous(this);

        if (moreRows) {
            cursorIndex--;
        }

        return moreRows;
    }

    @Override
    public void refreshRow() throws SQLException {
        createChain().resultSet_refreshRow(this);
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        return createChain().resultSet_relative(this, rows);
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        return createChain().resultSet_rowDeleted(this);
    }

    @Override
    public boolean rowInserted() throws SQLException {
        return createChain().resultSet_rowInserted(this);
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        return createChain().resultSet_rowUpdated(this);
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        createChain().resultSet_setFetchDirection(this, direction);
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        createChain().resultSet_setFetchSize(this, rows);
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        createChain().resultSet_updateArray(this, columnIndex, x);
    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
        createChain().resultSet_updateArray(this, columnLabel, x);
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        createChain().resultSet_updateAsciiStream(this, columnIndex, x);
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        createChain().resultSet_updateAsciiStream(this, columnLabel, x);
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        createChain().resultSet_updateAsciiStream(this, columnIndex, x, length);
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        createChain().resultSet_updateAsciiStream(this, columnLabel, x, length);
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        createChain().resultSet_updateAsciiStream(this, columnIndex, x, length);
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        createChain().resultSet_updateAsciiStream(this, columnLabel, x, length);
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        createChain().resultSet_updateBigDecimal(this, columnIndex, x);
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        createChain().resultSet_updateBigDecimal(this, columnLabel, x);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        createChain().resultSet_updateBinaryStream(this, columnIndex, x);
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        createChain().resultSet_updateBinaryStream(this, columnLabel, x);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        createChain().resultSet_updateBinaryStream(this, columnIndex, x, length);
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        createChain().resultSet_updateBinaryStream(this, columnLabel, x, length);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        createChain().resultSet_updateBinaryStream(this, columnIndex, x, length);
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        createChain().resultSet_updateBinaryStream(this, columnLabel, x, length);
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        createChain().resultSet_updateBlob(this, columnIndex, x);
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        createChain().resultSet_updateBlob(this, columnLabel, x);
    }

    @Override
    public void updateBlob(int columnIndex, InputStream x) throws SQLException {
        createChain().resultSet_updateBlob(this, columnIndex, x);
    }

    @Override
    public void updateBlob(String columnLabel, InputStream x) throws SQLException {
        createChain().resultSet_updateBlob(this, columnLabel, x);
    }

    @Override
    public void updateBlob(int columnIndex, InputStream x, long length) throws SQLException {
        createChain().resultSet_updateBlob(this, columnIndex, x, length);
    }

    @Override
    public void updateBlob(String columnLabel, InputStream x, long length) throws SQLException {
        createChain().resultSet_updateBlob(this, columnLabel, x, length);
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        createChain().resultSet_updateBoolean(this, columnIndex, x);
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        createChain().resultSet_updateBoolean(this, columnLabel, x);
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        createChain().resultSet_updateByte(this, columnIndex, x);
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        createChain().resultSet_updateByte(this, columnLabel, x);
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        createChain().resultSet_updateBytes(this, columnIndex, x);
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        createChain().resultSet_updateBytes(this, columnLabel, x);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        createChain().resultSet_updateCharacterStream(this, columnIndex, x);
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader x) throws SQLException {
        createChain().resultSet_updateCharacterStream(this, columnLabel, x);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        createChain().resultSet_updateCharacterStream(this, columnIndex, x, length);
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader x, int length) throws SQLException {
        createChain().resultSet_updateCharacterStream(this, columnLabel, x, length);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        createChain().resultSet_updateCharacterStream(this, columnIndex, x, length);
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader x, long length) throws SQLException {
        createChain().resultSet_updateCharacterStream(this, columnLabel, x, length);
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        createChain().resultSet_updateClob(this, columnIndex, x);
    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        createChain().resultSet_updateClob(this, columnLabel, x);
    }

    @Override
    public void updateClob(int columnIndex, Reader x) throws SQLException {
        createChain().resultSet_updateClob(this, columnIndex, x);
    }

    @Override
    public void updateClob(String columnLabel, Reader x) throws SQLException {
        createChain().resultSet_updateClob(this, columnLabel, x);
    }

    @Override
    public void updateClob(int columnIndex, Reader x, long length) throws SQLException {
        createChain().resultSet_updateClob(this, columnIndex, x, length);
    }

    @Override
    public void updateClob(String columnLabel, Reader x, long length) throws SQLException {
        createChain().resultSet_updateClob(this, columnLabel, x, length);
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        createChain().resultSet_updateDate(this, columnIndex, x);
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        createChain().resultSet_updateDate(this, columnLabel, x);
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        createChain().resultSet_updateDouble(this, columnIndex, x);
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        createChain().resultSet_updateDouble(this, columnLabel, x);
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        createChain().resultSet_updateFloat(this, columnIndex, x);
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        createChain().resultSet_updateFloat(this, columnLabel, x);
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        createChain().resultSet_updateInt(this, columnIndex, x);
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        createChain().resultSet_updateInt(this, columnLabel, x);
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        createChain().resultSet_updateLong(this, columnIndex, x);
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        createChain().resultSet_updateLong(this, columnLabel, x);
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        createChain().resultSet_updateNCharacterStream(this, columnIndex, x);
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader x) throws SQLException {
        createChain().resultSet_updateNCharacterStream(this, columnLabel, x);
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        createChain().resultSet_updateNCharacterStream(this, columnIndex, x, length);
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader x, long length) throws SQLException {
        createChain().resultSet_updateNCharacterStream(this, columnLabel, x, length);
    }

    @Override
    public void updateNClob(int columnIndex, NClob x) throws SQLException {
        createChain().resultSet_updateNClob(this, columnIndex, x);
    }

    @Override
    public void updateNClob(String columnLabel, NClob x) throws SQLException {
        createChain().resultSet_updateNClob(this, columnLabel, x);
    }

    @Override
    public void updateNClob(int columnIndex, Reader x) throws SQLException {
        createChain().resultSet_updateNClob(this, columnIndex, x);
    }

    @Override
    public void updateNClob(String columnLabel, Reader x) throws SQLException {
        createChain().resultSet_updateNClob(this, columnLabel, x);
    }

    @Override
    public void updateNClob(int columnIndex, Reader x, long length) throws SQLException {
        createChain().resultSet_updateNClob(this, columnIndex, x, length);
    }

    @Override
    public void updateNClob(String columnLabel, Reader x, long length) throws SQLException {
        createChain().resultSet_updateNClob(this, columnLabel, x, length);
    }

    @Override
    public void updateNString(int columnIndex, String x) throws SQLException {
        createChain().resultSet_updateNString(this, columnIndex, x);
    }

    @Override
    public void updateNString(String columnLabel, String x) throws SQLException {
        createChain().resultSet_updateNString(this, columnLabel, x);
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        createChain().resultSet_updateNull(this, columnIndex);
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        createChain().resultSet_updateNull(this, columnLabel);
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        createChain().resultSet_updateObject(this, columnIndex, x);
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        createChain().resultSet_updateObject(this, columnLabel, x);
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        createChain().resultSet_updateObject(this, columnIndex, x, scaleOrLength);
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        createChain().resultSet_updateObject(this, columnLabel, x, scaleOrLength);
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        createChain().resultSet_updateRef(this, columnIndex, x);
    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        createChain().resultSet_updateRef(this, columnLabel, x);
    }

    @Override
    public void updateRow() throws SQLException {
        createChain().resultSet_updateRow(this);
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        createChain().resultSet_updateRowId(this, columnIndex, x);
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        createChain().resultSet_updateRowId(this, columnLabel, x);
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML x) throws SQLException {
        createChain().resultSet_updateSQLXML(this, columnIndex, x);
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML x) throws SQLException {
        createChain().resultSet_updateSQLXML(this, columnLabel, x);
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        createChain().resultSet_updateShort(this, columnIndex, x);
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        createChain().resultSet_updateShort(this, columnLabel, x);
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        createChain().resultSet_updateString(this, columnIndex, x);
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        createChain().resultSet_updateString(this, columnLabel, x);
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        createChain().resultSet_updateTime(this, columnIndex, x);
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        createChain().resultSet_updateTime(this, columnLabel, x);
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        createChain().resultSet_updateTimestamp(this, columnIndex, x);
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        createChain().resultSet_updateTimestamp(this, columnLabel, x);
    }

    @Override
    public boolean wasNull() throws SQLException {
        return createChain().resultSet_wasNull(this);
    }

    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == ResultSet.class) {
            return (T) resultSet;
        }
        
        return super.unwrap(iface);
    }
}
