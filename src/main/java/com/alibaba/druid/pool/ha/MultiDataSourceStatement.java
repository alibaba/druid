package com.alibaba.druid.pool.ha;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Statement;

import com.alibaba.druid.pool.WrapperAdapter;

public class MultiDataSourceStatement extends WrapperAdapter implements Statement {

    private final MultiDataSourceConnection conn;
    private final long                      id;
    private Integer                         resultSetType;
    private Integer                         resultSetConcurrency;
    private Integer                         resultSetHoldability;
    private Statement                       stmt;

    private Integer                         maxFieldSize;
    private Integer                         maxRows;
    private Boolean                         escapeProcessing;
    private Integer                         queryTimeout;
    private String                          cursorName;
    private Integer                         fetchDirection;
    private Integer                         fetchSize;

    void checkStatement(String sql) throws SQLException {
        conn.checkConnection(sql);

        if (resultSetType != null && resultSetConcurrency != null && resultSetHoldability != null) {
            stmt = conn.getConnectionRaw().createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        } else if (resultSetType != null && resultSetConcurrency != null) {
            stmt = conn.getConnectionRaw().createStatement(resultSetType, resultSetConcurrency);
        } else {
            stmt = conn.getConnectionRaw().createStatement();
        }

        if (maxFieldSize != null) {
            stmt.setMaxFieldSize(maxFieldSize);
        }
        if (maxRows != null) {
            stmt.setMaxRows(maxRows);
        }
        if (escapeProcessing != null) {
            stmt.setEscapeProcessing(escapeProcessing);
        }
        if (queryTimeout != null) {
            stmt.setQueryTimeout(queryTimeout);
        }
        if (cursorName != null) {
            stmt.setCursorName(cursorName);
        }
        if (fetchDirection != null) {
            stmt.setFetchDirection(fetchDirection);
        }
        if (fetchSize != null) {
            stmt.setFetchSize(fetchSize);
        }
    }

    public MultiDataSourceStatement(MultiDataSourceConnection conn, long id){
        super();
        this.conn = conn;
        this.id = id;
    }

    public MultiDataSourceStatement(MultiDataSourceConnection conn, long id, int resultSetType, int resultSetConcurrency){
        super();
        this.conn = conn;
        this.id = id;
        this.resultSetType = resultSetType;
        this.resultSetConcurrency = resultSetConcurrency;
    }

    public MultiDataSourceStatement(MultiDataSourceConnection conn, long id, int resultSetType,
                                    int resultSetConcurrency, int resultSetHoldability){
        super();
        this.conn = conn;
        this.id = id;
        this.resultSetType = resultSetType;
        this.resultSetConcurrency = resultSetConcurrency;
        this.resultSetHoldability = resultSetHoldability;
    }

    public long getId() {
        return id;
    }

    @Override
    public ResultSet executeQuery(String sql) throws SQLException {
        checkStatement(sql);

        return stmt.executeQuery(sql);
    }

    @Override
    public int executeUpdate(String sql) throws SQLException {
        checkStatement(sql);
        return stmt.executeUpdate(sql);
    }

    @Override
    public void close() throws SQLException {
        if (stmt != null) {
            stmt.close();
        }
    }

    @Override
    public int getMaxFieldSize() throws SQLException {
        if (stmt != null) {
            return stmt.getMaxFieldSize();
        }

        if (maxFieldSize == null) {
            return 0;
        }

        return maxFieldSize;
    }

    @Override
    public void setMaxFieldSize(int max) throws SQLException {
        if (stmt != null) {
            stmt.setMaxFieldSize(max);
            return;
        }

        this.maxFieldSize = max;
    }

    @Override
    public int getMaxRows() throws SQLException {
        if (stmt != null) {
            return stmt.getMaxRows();
        }

        if (maxRows == null) {
            return 0;
        }

        return maxRows;
    }

    @Override
    public void setMaxRows(int max) throws SQLException {
        if (stmt != null) {
            stmt.setMaxRows(max);
            return;
        }

        this.maxRows = max;
    }

    @Override
    public void setEscapeProcessing(boolean enable) throws SQLException {
        if (stmt != null) {
            stmt.setEscapeProcessing(enable);
            return;
        }

        this.escapeProcessing = enable;
    }

    @Override
    public int getQueryTimeout() throws SQLException {
        if (stmt != null) {
            return stmt.getQueryTimeout();
        }

        if (queryTimeout == null) {
            return 0;
        }

        return queryTimeout;
    }

    @Override
    public void setQueryTimeout(int seconds) throws SQLException {
        if (stmt != null) {
            stmt.setQueryTimeout(seconds);
            return;
        }

        this.queryTimeout = seconds;
    }

    @Override
    public void cancel() throws SQLException {
        if (stmt != null) {
            stmt.cancel();
        }
    }

    @Override
    public SQLWarning getWarnings() throws SQLException {
        if (stmt != null) {
            return stmt.getWarnings();
        }

        return null;
    }

    @Override
    public void clearWarnings() throws SQLException {
        if (stmt != null) {
            stmt.clearWarnings();
        }
    }

    @Override
    public void setCursorName(String name) throws SQLException {
        if (stmt != null) {
            stmt.setCursorName(name);
            return;
        }

        this.cursorName = name;
    }

    @Override
    public boolean execute(String sql) throws SQLException {
        checkStatement(sql);
        return stmt.execute(sql);
    }

    @Override
    public ResultSet getResultSet() throws SQLException {
        if (stmt != null) {
            return stmt.getResultSet();
        }

        return null;
    }

    @Override
    public int getUpdateCount() throws SQLException {
        if (stmt != null) {
            return stmt.getUpdateCount();
        }

        return 0;
    }

    @Override
    public boolean getMoreResults() throws SQLException {
        if (stmt != null) {
            return stmt.getMoreResults();
        }

        return false;
    }

    @Override
    public void setFetchDirection(int direction) throws SQLException {
        if (stmt != null) {
            stmt.setFetchDirection(direction);
            return;
        }

        this.fetchDirection = direction;
    }

    @Override
    public int getFetchDirection() throws SQLException {
        if (stmt != null) {
            return stmt.getFetchDirection();
        }

        return this.fetchDirection;
    }

    @Override
    public void setFetchSize(int rows) throws SQLException {
        if (stmt != null) {
            stmt.setFetchSize(rows);
            return;
        }

        this.fetchSize = rows;
    }

    @Override
    public int getFetchSize() throws SQLException {
        if (stmt != null) {
            return stmt.getFetchSize();
        }

        return this.fetchSize;
    }

    @Override
    public int getResultSetConcurrency() throws SQLException {
        if (stmt != null) {
            return stmt.getResultSetConcurrency();
        }

        if (resultSetConcurrency != null) {
            return resultSetConcurrency;
        }

        return 0;
    }

    @Override
    public int getResultSetType() throws SQLException {
        if (stmt != null) {
            return stmt.getResultSetType();
        }

        return resultSetType;
    }

    @Override
    public void addBatch(String sql) throws SQLException {
        checkStatement(sql);

        stmt.addBatch(sql);
    }

    @Override
    public void clearBatch() throws SQLException {
        if (stmt != null) {
            stmt.clearBatch();
        }
    }

    @Override
    public int[] executeBatch() throws SQLException {
        if (stmt != null) {
            return stmt.executeBatch();
        }
        return new int[0];
    }

    @Override
    public MultiDataSourceConnection getConnection() throws SQLException {
        return conn;
    }

    @Override
    public boolean getMoreResults(int current) throws SQLException {
        if (stmt != null) {
            return stmt.getMoreResults(current);
        }

        return false;
    }

    @Override
    public ResultSet getGeneratedKeys() throws SQLException {
        if (stmt != null) {
            return stmt.getGeneratedKeys();
        }

        return null;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        checkStatement(sql);

        return stmt.executeUpdate(sql, autoGeneratedKeys);
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        checkStatement(sql);
        return stmt.executeUpdate(sql, columnIndexes);
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        checkStatement(sql);
        return stmt.executeUpdate(sql, columnNames);
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        checkStatement(sql);
        return stmt.execute(sql, autoGeneratedKeys);
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        checkStatement(sql);
        return stmt.execute(sql, columnIndexes);
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        checkStatement(sql);
        return stmt.execute(sql, columnNames);
    }

    @Override
    public int getResultSetHoldability() throws SQLException {
        if (stmt != null) {
            return stmt.getResultSetHoldability();
        }

        if (resultSetHoldability != null) {
            return resultSetHoldability;
        }

        return 0;
    }

    @Override
    public boolean isClosed() throws SQLException {
        if (stmt != null) {
            return stmt.isClosed();
        }
        return false;
    }

    @Override
    public void setPoolable(boolean poolable) throws SQLException {
        throw new SQLException("not supported");
    }

    @Override
    public boolean isPoolable() throws SQLException {
        return false;
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

        if (stmt != null) {
            return stmt.unwrap(iface);
        }

        return null;
    }

    public Boolean isEscapeProcessing() {
        return escapeProcessing;
    }

    public String getCursorName() {
        return cursorName;
    }
}
