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
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Calendar;

import com.alibaba.druid.pool.PreparedStatementPool.MethodType;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.OracleUtils;

/**
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidPooledPreparedStatement extends DruidPooledStatement implements PreparedStatement {

    private final static Log              LOG = LogFactory.getLog(DruidPooledPreparedStatement.class);

    private final PreparedStatementHolder holder;
    private final PreparedStatement       stmt;
    private final String                  sql;

    private int                           defaultMaxFieldSize;
    private int                           defaultMaxRows;
    private int                           defaultQueryTimeout;
    private int                           defaultFetchDirection;
    private int                           defaultFetchSize;
    private int                           currentMaxFieldSize;
    private int                           currentMaxRows;
    private int                           currentQueryTimeout;
    private int                           currentFetchDirection;
    private int                           currentFetchSize;
    
    private boolean pooled = false;

    public DruidPooledPreparedStatement(DruidPooledConnection conn, PreparedStatementHolder holder) throws SQLException{
        super(conn, holder.statement);
        this.stmt = holder.statement;
        this.holder = holder;
        this.sql = holder.key.sql;

        pooled = conn.getConnectionHolder().isPoolPreparedStatements();
        // Remember the defaults

        if (pooled) {
            try {
                defaultMaxFieldSize = stmt.getMaxFieldSize();
            } catch (SQLException e) {
                LOG.error("getMaxFieldSize error", e);
            }

            try {
                defaultMaxRows = stmt.getMaxRows();
            } catch (SQLException e) {
                LOG.error("getMaxRows error", e);
            }

            try {
                defaultQueryTimeout = stmt.getQueryTimeout();
            } catch (SQLException e) {
                LOG.error("getMaxRows error", e);
            }

            try {
                defaultFetchDirection = stmt.getFetchDirection();
            } catch (SQLException e) {
                LOG.error("getFetchDirection error", e);
            }

            try {
                defaultFetchSize = stmt.getFetchSize();
            } catch (SQLException e) {
                LOG.error("getFetchSize error", e);
            }
        }

        currentMaxFieldSize = defaultMaxFieldSize;
        currentMaxRows = defaultMaxRows;
        currentQueryTimeout = defaultQueryTimeout;
        currentFetchDirection = defaultFetchDirection;
        currentFetchSize = defaultFetchSize;
    }

    public PreparedStatementHolder getPreparedStatementHolder() {
        return holder;
    }

    public int getHitCount() {
        return holder.getHitCount();
    }

    public void setFetchSize(int rows) throws SQLException {
        currentFetchSize = rows;
        super.setFetchSize(rows);
    }

    public void setFetchDirection(int direction) throws SQLException {
        currentFetchDirection = direction;
        super.setFetchDirection(direction);
    }

    public void setMaxFieldSize(int max) throws SQLException {
        currentMaxFieldSize = max;
        super.setMaxFieldSize(max);
    }

    public void setMaxRows(int max) throws SQLException {
        currentMaxRows = max;
        super.setMaxRows(max);
    }

    public void setQueryTimeout(int seconds) throws SQLException {
        currentQueryTimeout = seconds;
        super.setQueryTimeout(seconds);
    }

    public String getSql() {
        return sql;
    }

    public PreparedStatementKey getKey() {
        return holder.key;
    }

    public PreparedStatement getRawPreparedStatement() {
        return stmt;
    }

    public PreparedStatement getRawStatement() {
        return stmt;
    }

    @Override
    public void close() throws SQLException {
        if (isClosed()) {
            return;
        }

        boolean connectionClosed = this.conn.isClosed();
        // Reset the defaults
        if (pooled && !connectionClosed) {
            try {
                if (defaultMaxFieldSize != currentMaxFieldSize) {
                    stmt.setMaxFieldSize(defaultMaxFieldSize);
                    currentMaxFieldSize = defaultMaxFieldSize;
                }
                if (defaultMaxRows != currentMaxRows) {
                    stmt.setMaxRows(defaultMaxRows);
                    currentMaxRows = defaultMaxRows;
                }
                if (defaultQueryTimeout != currentQueryTimeout) {
                    stmt.setQueryTimeout(defaultQueryTimeout);
                    currentQueryTimeout = defaultQueryTimeout;
                }
                if (defaultFetchDirection != currentFetchDirection) {
                    stmt.setFetchDirection(defaultFetchDirection);
                    currentFetchDirection = defaultFetchDirection;
                }
                if (defaultFetchSize != currentFetchSize) {
                    stmt.setFetchSize(defaultFetchSize);
                    currentFetchSize = defaultFetchSize;
                }
            } catch (Exception e) {
                this.conn.handleException(e, null);
            }
        }

        conn.closePoolableStatement(this);
    }
    
    public boolean isPooled() {
        return pooled;
    }

    void closeInternal() throws SQLException {
        super.close();
    }

    void setClosed(boolean value) {
        this.closed = value;
    }

    @Override
    public ResultSet executeQuery() throws SQLException {
        checkOpen();

        incrementExecuteQueryCount();
        transactionRecord(sql);

        oracleSetRowPrefetch();

        conn.beforeExecute();
        try {
            ResultSet rs = stmt.executeQuery();

            if (rs == null) {
                return null;
            }

            DruidPooledResultSet poolableResultSet = new DruidPooledResultSet(this, rs);
            addResultSetTrace(poolableResultSet);

            return poolableResultSet;
        } catch (Throwable t) {
            errorCheck(t);

            throw checkException(t);
        } finally {
            conn.afterExecute();
        }
    }

    @Override
    public int executeUpdate() throws SQLException {
        checkOpen();

        incrementExecuteUpdateCount();
        transactionRecord(sql);

        conn.beforeExecute();
        try {
            return stmt.executeUpdate();
        } catch (Throwable t) {
            errorCheck(t);

            throw checkException(t);
        } finally {
            conn.afterExecute();
        }
    }

    @Override
    public void setNull(int parameterIndex, int sqlType) throws SQLException {
        checkOpen();

        try {
            stmt.setNull(parameterIndex, sqlType);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setBoolean(int parameterIndex, boolean x) throws SQLException {
        checkOpen();

        try {
            stmt.setBoolean(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setByte(int parameterIndex, byte x) throws SQLException {
        checkOpen();

        try {
            stmt.setByte(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setShort(int parameterIndex, short x) throws SQLException {
        checkOpen();

        try {
            stmt.setShort(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setInt(int parameterIndex, int x) throws SQLException {
        checkOpen();

        try {
            stmt.setInt(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setLong(int parameterIndex, long x) throws SQLException {
        checkOpen();

        try {
            stmt.setLong(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setFloat(int parameterIndex, float x) throws SQLException {
        checkOpen();

        try {
            stmt.setFloat(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setDouble(int parameterIndex, double x) throws SQLException {
        checkOpen();

        try {
            stmt.setDouble(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setBigDecimal(int parameterIndex, BigDecimal x) throws SQLException {
        checkOpen();

        try {
            stmt.setBigDecimal(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setString(int parameterIndex, String x) throws SQLException {
        checkOpen();

        try {
            stmt.setString(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setBytes(int parameterIndex, byte[] x) throws SQLException {
        checkOpen();

        try {
            stmt.setBytes(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setDate(int parameterIndex, Date x) throws SQLException {
        checkOpen();

        try {
            stmt.setDate(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setTime(int parameterIndex, Time x) throws SQLException {
        checkOpen();

        try {
            stmt.setTime(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x) throws SQLException {
        checkOpen();

        try {
            stmt.setTimestamp(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException {
        checkOpen();

        try {
            stmt.setAsciiStream(parameterIndex, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    @Deprecated
    public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException {
        checkOpen();

        try {
            stmt.setUnicodeStream(parameterIndex, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException {
        checkOpen();

        try {
            stmt.setBinaryStream(parameterIndex, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void clearParameters() throws SQLException {
        checkOpen();

        try {
            stmt.clearParameters();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType) throws SQLException {
        checkOpen();

        try {
            stmt.setObject(parameterIndex, x, targetSqlType);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setObject(int parameterIndex, Object x) throws SQLException {
        checkOpen();

        try {
            stmt.setObject(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public boolean execute() throws SQLException {
        checkOpen();

        incrementExecuteCount();
        transactionRecord(sql);

        oracleSetRowPrefetch();

        conn.beforeExecute();
        try {
            return stmt.execute();
        } catch (Throwable t) {
            errorCheck(t);

            throw checkException(t);
        } finally {
            conn.afterExecute();
        }
    }

    protected void oracleSetRowPrefetch() throws SQLException {
        if (!conn.isOracle()) {
            return;
        }

        if (holder.getHitCount() == 0) {
            return;
        }

        int fetchRowPeak = holder.getFetchRowPeak();

        if (fetchRowPeak < 0) {
            return;
        }

        if (holder.getDefaultRowPrefetch() == -1) {
            int defaultRowPretch = OracleUtils.getRowPrefetch(this);
            if (defaultRowPretch != holder.getDefaultRowPrefetch()) {
                holder.setDefaultRowPrefetch(defaultRowPretch);
                holder.setRowPrefetch(defaultRowPretch);
            }
        }

        int rowPrefetch;

        if (fetchRowPeak <= 1) {
            rowPrefetch = 2;
        } else if (fetchRowPeak > holder.getDefaultRowPrefetch()) {
            rowPrefetch = holder.getDefaultRowPrefetch();
        } else {
            rowPrefetch = fetchRowPeak + 1;
        }

        if (rowPrefetch != holder.getRowPrefetch()) {
            OracleUtils.setRowPrefetch(this, rowPrefetch);
            holder.setRowPrefetch(rowPrefetch);
        }
    }

    @Override
    public void addBatch() throws SQLException {
        checkOpen();

        try {
            stmt.addBatch();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    public int[] executeBatch() throws SQLException {
        checkOpen();

        incrementExecuteBatchCount();
        transactionRecord(sql);

        conn.beforeExecute();
        try {
            return stmt.executeBatch();
        } catch (Throwable t) {
            errorCheck(t);

            throw checkException(t);
        } finally {
            conn.afterExecute();
        }
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException {
        checkOpen();

        try {
            stmt.setCharacterStream(parameterIndex, reader, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setRef(int parameterIndex, Ref x) throws SQLException {
        checkOpen();

        try {
            stmt.setRef(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setBlob(int parameterIndex, Blob x) throws SQLException {
        checkOpen();

        try {
            stmt.setBlob(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setClob(int parameterIndex, Clob x) throws SQLException {
        checkOpen();

        try {
            stmt.setClob(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setArray(int parameterIndex, Array x) throws SQLException {
        checkOpen();

        try {
            stmt.setArray(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public ResultSetMetaData getMetaData() throws SQLException {
        checkOpen();

        if (!conn.holder.isUnderlyingAutoCommit()) {
            conn.createTransactionInfo();
        }

        try {
            return stmt.getMetaData();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException {
        checkOpen();

        try {
            stmt.setDate(parameterIndex, x, cal);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException {
        checkOpen();

        try {
            stmt.setTime(parameterIndex, x, cal);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException {
        checkOpen();

        try {
            stmt.setTimestamp(parameterIndex, x, cal);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setNull(int parameterIndex, int sqlType, String typeName) throws SQLException {
        checkOpen();

        try {
            stmt.setNull(parameterIndex, sqlType, typeName);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setURL(int parameterIndex, URL x) throws SQLException {
        checkOpen();

        try {
            stmt.setURL(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public ParameterMetaData getParameterMetaData() throws SQLException {
        checkOpen();

        if (!conn.holder.isUnderlyingAutoCommit()) {
            conn.createTransactionInfo();
        }

        try {
            return stmt.getParameterMetaData();
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setRowId(int parameterIndex, RowId x) throws SQLException {
        checkOpen();

        try {
            stmt.setRowId(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setNString(int parameterIndex, String value) throws SQLException {
        checkOpen();

        try {
            stmt.setNString(parameterIndex, value);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException {
        checkOpen();

        try {
            stmt.setNCharacterStream(parameterIndex, value, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setNClob(int parameterIndex, NClob value) throws SQLException {
        checkOpen();

        try {
            stmt.setNClob(parameterIndex, value);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setClob(int parameterIndex, Reader reader, long length) throws SQLException {
        checkOpen();

        try {
            stmt.setClob(parameterIndex, reader, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException {
        checkOpen();

        try {
            stmt.setBlob(parameterIndex, inputStream, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException {
        checkOpen();

        try {
            stmt.setNClob(parameterIndex, reader, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException {
        checkOpen();

        try {
            stmt.setSQLXML(parameterIndex, xmlObject);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        checkOpen();

        try {
            stmt.setObject(parameterIndex, x, targetSqlType, scaleOrLength);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException {
        checkOpen();

        try {
            stmt.setAsciiStream(parameterIndex, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException {
        checkOpen();

        try {
            stmt.setBinaryStream(parameterIndex, x, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException {
        checkOpen();

        try {
            stmt.setCharacterStream(parameterIndex, reader, length);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException {
        checkOpen();

        try {
            stmt.setAsciiStream(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException {
        checkOpen();

        try {
            stmt.setBinaryStream(parameterIndex, x);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException {
        checkOpen();

        try {
            stmt.setCharacterStream(parameterIndex, reader);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException {
        checkOpen();

        try {
            stmt.setNCharacterStream(parameterIndex, value);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setClob(int parameterIndex, Reader reader) throws SQLException {
        checkOpen();

        try {
            stmt.setClob(parameterIndex, reader);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException {
        checkOpen();

        try {
            stmt.setBlob(parameterIndex, inputStream);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    @Override
    public void setNClob(int parameterIndex, Reader reader) throws SQLException {
        checkOpen();

        try {
            stmt.setNClob(parameterIndex, reader);
        } catch (Throwable t) {
            throw checkException(t);
        }
    }

    public static class PreparedStatementKey {

        protected final String     sql;
        protected final String     catalog;

        protected final MethodType methodType;

        public final int           resultSetType;
        public final int           resultSetConcurrency;
        public final int           resultSetHoldability;
        public final int           autoGeneratedKeys;
        private final int[]        columnIndexes;
        private final String[]     columnNames;

        public PreparedStatementKey(String sql, String catalog, MethodType methodType) throws SQLException{
            this(sql, catalog, methodType, 0, 0, 0, 0, null, null);
        }

        public PreparedStatementKey(String sql, String catalog, MethodType methodType, int resultSetType,
                                    int resultSetConcurrency) throws SQLException{
            this(sql, catalog, methodType, resultSetType, resultSetConcurrency, 0, 0, null, null);
        }

        public PreparedStatementKey(String sql, String catalog, MethodType methodType, int resultSetType,
                                    int resultSetConcurrency, int resultSetHoldability) throws SQLException{
            this(sql, catalog, methodType, resultSetType, resultSetConcurrency, resultSetHoldability, 0, null, null);
        }

        public PreparedStatementKey(String sql, String catalog, MethodType methodType, int autoGeneratedKeys)
                                                                                                             throws SQLException{
            this(sql, catalog, methodType, 0, 0, 0, autoGeneratedKeys, null, null);
        }

        public PreparedStatementKey(String sql, String catalog, MethodType methodType, int[] columnIndexes)
                                                                                                           throws SQLException{
            this(sql, catalog, methodType, 0, 0, 0, 0, columnIndexes, null);
        }

        public PreparedStatementKey(String sql, String catalog, MethodType methodType, String[] columnNames)
                                                                                                            throws SQLException{
            this(sql, catalog, methodType, 0, 0, 0, 0, null, columnNames);
        }

        public PreparedStatementKey(String sql, String catalog, MethodType methodType, int resultSetType,
                                    int resultSetConcurrency, int resultSetHoldability, int autoGeneratedKeys,
                                    int[] columnIndexes, String[] columnNames) throws SQLException{
            if (sql == null) {
                throw new SQLException("sql is null");
            }

            this.sql = sql;
            this.catalog = catalog;
            this.methodType = methodType;
            this.resultSetType = resultSetType;
            this.resultSetConcurrency = resultSetConcurrency;
            this.resultSetHoldability = resultSetHoldability;
            this.autoGeneratedKeys = autoGeneratedKeys;
            this.columnIndexes = columnIndexes;
            this.columnNames = columnNames;
        }

        public int getResultSetType() {
            return resultSetType;
        }

        public int getResultSetConcurrency() {
            return resultSetConcurrency;
        }

        public int getResultSetHoldability() {
            return resultSetHoldability;
        }

        public boolean equals(Object object) {
            PreparedStatementKey that = (PreparedStatementKey) object;

            if (!this.sql.equals(that.sql)) {
                return false;
            }

            if (this.catalog == null) {
                if (that.catalog != null) {
                    return false;
                }
            } else {
                if (!this.catalog.equals(that.catalog)) {
                    return false;
                }
            }

            if (this.methodType != that.methodType) {
                return false;
            }

            if (this.resultSetType != that.resultSetType) {
                return false;
            }

            if (this.resultSetConcurrency != that.resultSetConcurrency) {
                return false;
            }

            if (this.resultSetHoldability != that.resultSetHoldability) {
                return false;
            }

            if (this.autoGeneratedKeys != that.autoGeneratedKeys) {
                return false;
            }

            if (!Arrays.equals(columnIndexes, that.columnIndexes)) {
                return false;
            }

            if (!Arrays.equals(columnNames, that.columnNames)) {
                return false;
            }

            return true;
        }

        public int hashCode() {
            final int prime = 31;
            int result = 1;

            result = prime * result + ((sql == null) ? 0 : sql.hashCode());
            result = prime * result + ((catalog == null) ? 0 : catalog.hashCode());
            result = prime * result + ((methodType == null) ? 0 : methodType.hashCode());

            result = prime * result + resultSetConcurrency;
            result = prime * result + resultSetHoldability;
            result = prime * result + resultSetType;

            result = prime * result + autoGeneratedKeys;

            result = prime * result + Arrays.hashCode(columnIndexes);
            result = prime * result + Arrays.hashCode(columnNames);

            return result;
        }

        public String getSql() {
            return sql;
        }

    }

    @Override
    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        if (iface == PreparedStatementHolder.class) {
            return true;
        }

        return super.isWrapperFor(iface);
    }

    @SuppressWarnings("unchecked")
    public <T> T unwrap(Class<T> iface) throws SQLException {
        if (iface == PreparedStatementHolder.class) {
            return (T) holder;
        }
        return super.unwrap(iface);
    }
}
