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
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.stat.JdbcSqlStat;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class ResultSetProxyImpl extends WrapperProxyImpl implements ResultSetProxy {

    private final ResultSet       resultSet;
    private final StatementProxy  statement;
    private final String          sql;

    protected int                 cursorIndex          = 0;
    protected int                 fetchRowCount        = 0;
    protected long                constructNano;
    protected final JdbcSqlStat   sqlStat;
    private int                   closeCount           = 0;

    private long                  readStringLength     = 0;
    private long                  readBytesLength      = 0;

    private int                   openInputStreamCount = 0;
    private int                   openReaderCount      = 0;

    private Map<Integer, Integer> logicColumnMap       = null;
    private Map<Integer, Integer> physicalColumnMap    = null;
    private List<Integer>         hiddenColumns        = null;

    private FilterChainImpl       filterChain          = null;

    public ResultSetProxyImpl(StatementProxy statement, ResultSet resultSet, long id, String sql){
        super(resultSet, id);
        this.statement = statement;
        this.resultSet = resultSet;
        this.sql = sql;
        sqlStat = this.statement.getSqlStat();
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

    public JdbcSqlStat getSqlStat() {
        return sqlStat;
    }

    public ResultSet getResultSetRaw() {
        return resultSet;
    }

    public StatementProxy getStatementProxy() {
        return this.statement;
    }

    public FilterChainImpl createChain() {
        FilterChainImpl chain = this.filterChain;
        if (chain == null) {
            chain = new FilterChainImpl(this.statement.getConnectionProxy().getDirectDataSource());
        } else {
            this.filterChain = null;
        }

        return chain;
    }

    public void recycleFilterChain(FilterChainImpl chain) {
        chain.reset();
        this.filterChain = chain;
    }

    @Override
    public boolean absolute(int row) throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSet_absolute(this, row);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public void afterLast() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_afterLast(this);
        recycleFilterChain(chain);
    }

    @Override
    public void beforeFirst() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_beforeFirst(this);
        recycleFilterChain(chain);
    }

    @Override
    public void cancelRowUpdates() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_cancelRowUpdates(this);
        recycleFilterChain(chain);
    }

    @Override
    public void clearWarnings() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_clearWarnings(this);
        recycleFilterChain(chain);
    }

    @Override
    public void close() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_close(this);
        closeCount++;
        recycleFilterChain(chain);
    }

    @Override
    public void deleteRow() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_deleteRow(this);
        recycleFilterChain(chain);
    }

    @Override
    public int findColumn(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.resultSet_findColumn(this, columnLabel);
        recycleFilterChain(chain);

        return value;
    }

    @Override
    public boolean first() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSet_first(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Array getArray(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Array value = chain.resultSet_getArray(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Array getArray(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        Array value = chain.resultSet_getArray(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public InputStream getAsciiStream(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        InputStream value = chain.resultSet_getAsciiStream(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public InputStream getAsciiStream(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        InputStream value = chain.resultSet_getAsciiStream(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        BigDecimal value = chain.resultSet_getBigDecimal(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        BigDecimal value = chain.resultSet_getBigDecimal(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException {
        FilterChainImpl chain = createChain();
        BigDecimal value = chain.resultSet_getBigDecimal(this, columnIndex, scale);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException {
        FilterChainImpl chain = createChain();
        BigDecimal value = chain.resultSet_getBigDecimal(this, columnLabel, scale);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public InputStream getBinaryStream(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        InputStream value = chain.resultSet_getBinaryStream(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public InputStream getBinaryStream(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        InputStream value = chain.resultSet_getBinaryStream(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Blob getBlob(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Blob value = chain.resultSet_getBlob(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Blob getBlob(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        Blob value = chain.resultSet_getBlob(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean getBoolean(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSet_getBoolean(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean getBoolean(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSet_getBoolean(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public byte getByte(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        byte value = chain.resultSet_getByte(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public byte getByte(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        byte value = chain.resultSet_getByte(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public byte[] getBytes(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        byte[] value = chain.resultSet_getBytes(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public byte[] getBytes(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        byte[] value = chain.resultSet_getBytes(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Reader getCharacterStream(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Reader value = chain.resultSet_getCharacterStream(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Reader getCharacterStream(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        Reader value = chain.resultSet_getCharacterStream(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Clob getClob(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Clob value = chain.resultSet_getClob(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Clob getClob(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        Clob value = chain.resultSet_getClob(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getConcurrency() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.resultSet_getConcurrency(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public String getCursorName() throws SQLException {
        FilterChainImpl chain = createChain();
        String value = chain.resultSet_getCursorName(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Date getDate(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Date value = chain.resultSet_getDate(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Date getDate(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        Date value = chain.resultSet_getDate(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Date getDate(int columnIndex, Calendar cal) throws SQLException {
        FilterChainImpl chain = createChain();
        Date value = chain.resultSet_getDate(this, columnIndex, cal);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Date getDate(String columnLabel, Calendar cal) throws SQLException {
        FilterChainImpl chain = createChain();
        Date value = chain.resultSet_getDate(this, columnLabel, cal);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public double getDouble(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        double value = chain.resultSet_getDouble(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public double getDouble(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        double value = chain.resultSet_getDouble(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.resultSet_getFetchDirection(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getFetchSize() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.resultSet_getFetchSize(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public float getFloat(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        float value = chain.resultSet_getFloat(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public float getFloat(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        float value = chain.resultSet_getFloat(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getHoldability() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.resultSet_getHoldability(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getInt(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.resultSet_getInt(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getInt(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.resultSet_getInt(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public long getLong(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        long value = chain.resultSet_getLong(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public long getLong(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        long value = chain.resultSet_getLong(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        FilterChainImpl chain = createChain();
        ResultSetMetaData value = chain.resultSet_getMetaData(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Reader getNCharacterStream(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Reader value = chain.resultSet_getNCharacterStream(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Reader getNCharacterStream(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        Reader value = chain.resultSet_getNCharacterStream(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public NClob getNClob(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        NClob value = chain.resultSet_getNClob(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public NClob getNClob(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        NClob value = chain.resultSet_getNClob(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public String getNString(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        String value = chain.resultSet_getNString(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public String getNString(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        String value = chain.resultSet_getNString(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Object getObject(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Object value = chain.resultSet_getObject(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Object getObject(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        Object value = chain.resultSet_getObject(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException {
        FilterChainImpl chain = createChain();
        Object value = chain.resultSet_getObject(this, columnIndex, map);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException {
        FilterChainImpl chain = createChain();
        Object value = chain.resultSet_getObject(this, columnLabel, map);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Ref getRef(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Ref value = chain.resultSet_getRef(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Ref getRef(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        Ref value = chain.resultSet_getRef(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getRow() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.resultSet_getRow(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public RowId getRowId(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        RowId value = chain.resultSet_getRowId(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public RowId getRowId(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        RowId value = chain.resultSet_getRowId(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public SQLXML getSQLXML(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        SQLXML value = chain.resultSet_getSQLXML(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public SQLXML getSQLXML(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        SQLXML value = chain.resultSet_getSQLXML(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public short getShort(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        short value = chain.resultSet_getShort(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public short getShort(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        short value = chain.resultSet_getShort(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Statement getStatement() throws SQLException {
        FilterChainImpl chain = createChain();
        Statement stmt = chain.resultSet_getStatement(this);
        recycleFilterChain(chain);
        return stmt;
    }

    @Override
    public String getString(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        String value = chain.resultSet_getString(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public String getString(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        String value = chain.resultSet_getString(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Time getTime(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Time value = chain.resultSet_getTime(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Time getTime(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        Time value = chain.resultSet_getTime(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Time getTime(int columnIndex, Calendar cal) throws SQLException {
        FilterChainImpl chain = createChain();
        Time value = chain.resultSet_getTime(this, columnIndex, cal);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Time getTime(String columnLabel, Calendar cal) throws SQLException {
        FilterChainImpl chain = createChain();
        Time value = chain.resultSet_getTime(this, columnLabel, cal);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Timestamp getTimestamp(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        Timestamp value = chain.resultSet_getTimestamp(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Timestamp getTimestamp(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        Timestamp value = chain.resultSet_getTimestamp(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException {
        FilterChainImpl chain = createChain();
        Timestamp value = chain.resultSet_getTimestamp(this, columnIndex, cal);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException {
        FilterChainImpl chain = createChain();
        Timestamp value = chain.resultSet_getTimestamp(this, columnLabel, cal);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public int getType() throws SQLException {
        FilterChainImpl chain = createChain();
        int value = chain.resultSet_getType(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public URL getURL(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        URL value = chain.resultSet_getURL(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public URL getURL(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        URL value = chain.resultSet_getURL(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public InputStream getUnicodeStream(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        InputStream value = chain.resultSet_getUnicodeStream(this, columnIndex);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public InputStream getUnicodeStream(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        InputStream value = chain.resultSet_getUnicodeStream(this, columnLabel);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        FilterChainImpl chain = createChain();
        SQLWarning value = chain.resultSet_getWarnings(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public void insertRow() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_insertRow(this);
        recycleFilterChain(chain);
    }

    @Override
    public boolean isAfterLast() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSet_isAfterLast(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isBeforeFirst() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSet_isBeforeFirst(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isClosed() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSet_isClosed(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isFirst() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSet_isFirst(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean isLast() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSet_isLast(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean last() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSet_last(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public void moveToCurrentRow() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_moveToCurrentRow(this);
        recycleFilterChain(chain);
    }

    @Override
    public void moveToInsertRow() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_moveToInsertRow(this);
        recycleFilterChain(chain);
    }

    @Override
    public boolean next() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean moreRows = chain.resultSet_next(this);

        if (moreRows) {
            cursorIndex++;
            if (cursorIndex > fetchRowCount) {
                fetchRowCount = cursorIndex;
            }
        }

        recycleFilterChain(chain);
        return moreRows;
    }

    @Override
    public boolean previous() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean moreRows = chain.resultSet_previous(this);

        if (moreRows) {
            cursorIndex--;
        }

        recycleFilterChain(chain);
        return moreRows;
    }

    @Override
    public void refreshRow() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_refreshRow(this);
        recycleFilterChain(chain);
    }

    @Override
    public boolean relative(int rows) throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSet_relative(this, rows);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean rowDeleted() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSet_rowDeleted(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean rowInserted() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSet_rowInserted(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public boolean rowUpdated() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean value = chain.resultSet_rowUpdated(this);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_setFetchDirection(this, direction);
        recycleFilterChain(chain);
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_setFetchSize(this, rows);
        recycleFilterChain(chain);
    }

    @Override
    public void updateArray(int columnIndex, Array x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateArray(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateArray(String columnLabel, Array x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateArray(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateAsciiStream(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateAsciiStream(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateAsciiStream(this, columnIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateAsciiStream(this, columnLabel, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateAsciiStream(this, columnIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateAsciiStream(this, columnLabel, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateBigDecimal(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateBigDecimal(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateBinaryStream(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateBinaryStream(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateBinaryStream(this, columnIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateBinaryStream(this, columnLabel, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateBinaryStream(this, columnIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateBinaryStream(this, columnLabel, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateBlob(int columnIndex, Blob x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateBlob(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateBlob(String columnLabel, Blob x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateBlob(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateBlob(int columnIndex, InputStream x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateBlob(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateBlob(String columnLabel, InputStream x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateBlob(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateBlob(int columnIndex, InputStream x, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateBlob(this, columnIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateBlob(String columnLabel, InputStream x, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateBlob(this, columnLabel, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateBoolean(int columnIndex, boolean x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateBoolean(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateBoolean(String columnLabel, boolean x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateBoolean(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateByte(int columnIndex, byte x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateByte(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateByte(String columnLabel, byte x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateByte(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateBytes(int columnIndex, byte[] x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateBytes(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateBytes(String columnLabel, byte[] x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateBytes(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateCharacterStream(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateCharacterStream(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateCharacterStream(this, columnIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader x, int length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateCharacterStream(this, columnLabel, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateCharacterStream(this, columnIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateCharacterStream(String columnLabel, Reader x, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateCharacterStream(this, columnLabel, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateClob(int columnIndex, Clob x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateClob(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateClob(String columnLabel, Clob x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateClob(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateClob(int columnIndex, Reader x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateClob(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateClob(String columnLabel, Reader x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateClob(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateClob(int columnIndex, Reader x, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateClob(this, columnIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateClob(String columnLabel, Reader x, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateClob(this, columnLabel, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateDate(int columnIndex, Date x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateDate(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateDate(String columnLabel, Date x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateDate(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateDouble(int columnIndex, double x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateDouble(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateDouble(String columnLabel, double x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateDouble(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateFloat(int columnIndex, float x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateFloat(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateFloat(String columnLabel, float x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateFloat(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateInt(int columnIndex, int x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateInt(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateInt(String columnLabel, int x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateInt(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateLong(int columnIndex, long x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateLong(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateLong(String columnLabel, long x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateLong(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateNCharacterStream(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateNCharacterStream(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateNCharacterStream(this, columnIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateNCharacterStream(String columnLabel, Reader x, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateNCharacterStream(this, columnLabel, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateNClob(int columnIndex, NClob x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateNClob(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateNClob(String columnLabel, NClob x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateNClob(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateNClob(int columnIndex, Reader x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateNClob(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateNClob(String columnLabel, Reader x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateNClob(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateNClob(int columnIndex, Reader x, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateNClob(this, columnIndex, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateNClob(String columnLabel, Reader x, long length) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateNClob(this, columnLabel, x, length);
        recycleFilterChain(chain);
    }

    @Override
    public void updateNString(int columnIndex, String x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateNString(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateNString(String columnLabel, String x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateNString(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateNull(int columnIndex) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateNull(this, columnIndex);
        recycleFilterChain(chain);
    }

    @Override
    public void updateNull(String columnLabel) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateNull(this, columnLabel);
        recycleFilterChain(chain);
    }

    @Override
    public void updateObject(int columnIndex, Object x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateObject(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateObject(String columnLabel, Object x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateObject(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateObject(this, columnIndex, x, scaleOrLength);
        recycleFilterChain(chain);
    }

    @Override
    public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateObject(this, columnLabel, x, scaleOrLength);
        recycleFilterChain(chain);
    }

    @Override
    public void updateRef(int columnIndex, Ref x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateRef(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateRef(String columnLabel, Ref x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateRef(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateRow() throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateRow(this);
        recycleFilterChain(chain);
    }

    @Override
    public void updateRowId(int columnIndex, RowId x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateRowId(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateRowId(String columnLabel, RowId x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateRowId(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateSQLXML(int columnIndex, SQLXML x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateSQLXML(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateSQLXML(String columnLabel, SQLXML x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateSQLXML(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateShort(int columnIndex, short x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateShort(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateShort(String columnLabel, short x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateShort(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateString(int columnIndex, String x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateString(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateString(String columnLabel, String x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateString(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateTime(int columnIndex, Time x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateTime(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateTime(String columnLabel, Time x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateTime(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateTimestamp(this, columnIndex, x);
        recycleFilterChain(chain);
    }

    @Override
    public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException {
        FilterChainImpl chain = createChain();
        chain.resultSet_updateTimestamp(this, columnLabel, x);
        recycleFilterChain(chain);
    }

    @Override
    public boolean wasNull() throws SQLException {
        FilterChainImpl chain = createChain();
        boolean result = chain.resultSet_wasNull(this);

        recycleFilterChain(chain);
        return result;
    }

    @Override
    public <T> T getObject(int columnIndex, Class<T> type) throws SQLException {
        FilterChainImpl chain = createChain();
        T value = chain.resultSet_getObject(this, columnIndex, type);
        recycleFilterChain(chain);
        return value;
    }

    @Override
    public <T> T getObject(String columnLabel, Class<T> type) throws SQLException {
        FilterChainImpl chain = createChain();
        T value = chain.resultSet_getObject(this, columnLabel, type);
        recycleFilterChain(chain);
        return value;
    }

    public int getCloseCount() {
        return closeCount;
    }

    @Override
    public void addReadStringLength(int length) {
        this.readStringLength += length;
    }

    @Override
    public long getReadStringLength() {
        return readStringLength;
    }

    @Override
    public void addReadBytesLength(int length) {
        this.readBytesLength += length;
    }

    @Override
    public long getReadBytesLength() {
        return readBytesLength;
    }

    @Override
    public void incrementOpenInputStreamCount() {
        openInputStreamCount++;
    }

    @Override
    public int getOpenInputStreamCount() {
        return openInputStreamCount;
    }

    @Override
    public void incrementOpenReaderCount() {
        openReaderCount++;
    }

    @Override
    public int getOpenReaderCount() {
        return openReaderCount;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == ResultSetProxy.class || iface == ResultSetProxyImpl.class) {
            return (T) this;
        }

        return super.unwrap(iface);
    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface == ResultSetProxy.class || iface == ResultSetProxyImpl.class) {
            return true;
        }

        return super.isWrapperFor(iface);
    }

    @Override
    public int getPhysicalColumn(int logicColumn) {
        if (logicColumnMap == null) {
            return logicColumn;
        }
        return logicColumnMap.get(logicColumn);
    }

    @Override
    public int getLogicColumn(int physicalColumn) {
        if (physicalColumnMap == null) {
            return physicalColumn;
        }
        return physicalColumnMap.get(physicalColumn);
    }

    @Override
    public int getHiddenColumnCount() {
        if (hiddenColumns == null) {
            return 0;
        }
        return hiddenColumns.size();
    }

    @Override
    public List<Integer> getHiddenColumns() {
        return this.hiddenColumns;
    }

    @Override
    public void setLogicColumnMap(Map<Integer, Integer> logicColumnMap) {
        this.logicColumnMap = logicColumnMap;
    }

    @Override
    public void setPhysicalColumnMap(Map<Integer, Integer> physicalColumnMap) {
        this.physicalColumnMap = physicalColumnMap;
    }

    @Override
    public void setHiddenColumns(List<Integer> hiddenColumns) {
        this.hiddenColumns = hiddenColumns;
    }

}
