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
package com.alibaba.druid.sharding;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.Statement;
import java.util.List;

import org.h2.util.StringUtils;

import com.alibaba.druid.pool.WrapperAdapter;
import com.alibaba.druid.sharding.sql.ShardingVisitor;
import com.alibaba.druid.sql.SQLUtils;
import com.alibaba.druid.sql.ast.SQLStatement;
import com.alibaba.druid.sql.ast.statement.SQLInsertStatement;
import com.alibaba.druid.sql.ast.statement.SQLSelectStatement;
import com.alibaba.druid.util.JdbcUtils;

public class ShardingStatement extends WrapperAdapter implements Statement {

    protected final ShardingConnection conn;
    private final long                 id;
    protected Integer                  resultSetType;
    protected Integer                  resultSetConcurrency;
    protected Integer                  resultSetHoldability;

    private Statement                  stmt;
    protected Integer                  updateCount;

    private Integer                    maxFieldSize;
    private Integer                    maxRows;
    private Boolean                    escapeProcessing;
    private Integer                    queryTimeout;
    private String                     cursorName;
    private Integer                    fetchDirection;
    private Integer                    fetchSize;

    protected void initStatement(Statement stmt) throws SQLException {
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

    private Statement createRealStatement(Connection conn) throws SQLException {
        Statement stmt;
        if (resultSetType != null && resultSetConcurrency != null && resultSetHoldability != null) {
            stmt = conn.createStatement(resultSetType, resultSetConcurrency, resultSetHoldability);
        } else if (resultSetType != null && resultSetConcurrency != null) {
            stmt = conn.createStatement(resultSetType, resultSetConcurrency);
        } else {
            stmt = conn.createStatement();
        }

        initStatement(stmt);

        return stmt;
    }

    public ShardingStatement(ShardingConnection conn, long id){
        this.conn = conn;
        this.id = id;
    }

    public ShardingStatement(ShardingConnection conn, long id, int resultSetType, int resultSetConcurrency){
        this.conn = conn;
        this.id = id;
        this.resultSetType = resultSetType;
        this.resultSetConcurrency = resultSetConcurrency;
    }

    public ShardingStatement(ShardingConnection conn, long id, int resultSetType, int resultSetConcurrency,
                             int resultSetHoldability){
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
        ShardingDataSource dataSource = conn.getDataSource();
        ShardingVisitor visitor = dataSource.createShardingVisitor();
        SQLStatement sqlStmt = dataSource.parseStatement(sql);

        sqlStmt.accept(visitor);

        List<SQLStatement> resultSqlStmtList = visitor.getResult();

        if (resultSqlStmtList.size() > 1) {
            throw new SQLException("executeQuery not support multi-statement");
        }

        SQLStatement resultSqlStmt = resultSqlStmtList.get(0);

        String resultSql = SQLUtils.toSQLString(resultSqlStmt, dataSource.getDbType());

        String database = (String) resultSqlStmt.getAttribute(ShardingVisitor.ATTR_DB);

        if (conn.getRealConnection() != null && StringUtils.equals(database, conn.getDatabase())) {
            if (stmt == null) {
                stmt = createRealStatement(conn.getRealConnection());
            }
        } else {
            conn.closeRealConnection();
            if (stmt != null) {
                JdbcUtils.close(stmt);
                stmt = null;
            }
            conn.createRealConnectionByDb(database);
            stmt = createRealStatement(conn.getRealConnection());
        }
        return stmt.executeQuery(resultSql);
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
        ShardingDataSource dataSource = conn.getDataSource();
        ShardingVisitor visitor = dataSource.createShardingVisitor();
        SQLStatement sqlStmt = dataSource.parseStatement(sql);

        sqlStmt.accept(visitor);

        List<SQLStatement> resultSqlStmtList = visitor.getResult();

        boolean isSelelct = sqlStmt instanceof SQLSelectStatement;

        if (resultSqlStmtList.size() > 1 && isSelelct) {
            throw new SQLException("select not support multi-statement");
        }

        for (SQLStatement resultSqlStmt : resultSqlStmtList) {
            String resultSql = SQLUtils.toSQLString(resultSqlStmt, dataSource.getDbType());

            String database = (String) resultSqlStmt.getAttribute(ShardingVisitor.ATTR_DB);

            if (conn.getRealConnection() != null && StringUtils.equals(database, conn.getDatabase())) {
                if (stmt == null) {
                    stmt = createRealStatement(conn.getRealConnection());
                }
            } else {
                conn.closeRealConnection();
                if (stmt != null) {
                    JdbcUtils.close(stmt);
                    stmt = null;
                }
                conn.createRealConnectionByDb(database);
                stmt = createRealStatement(conn.getRealConnection());
            }
            stmt.execute(resultSql);

            if (!isSelelct) {
                if (updateCount == null) {
                    updateCount = stmt.getUpdateCount();
                } else {
                    updateCount = updateCount.intValue() + stmt.getUpdateCount();
                }
            }
        }

        return isSelelct;
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
        if (updateCount != null) {
            return updateCount.intValue();
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
        throw new UnsupportedOperationException();
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
    public ShardingConnection getConnection() throws SQLException {
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
    public int executeUpdate(String sql) throws SQLException {
        ShardingDataSource dataSource = conn.getDataSource();
        ShardingVisitor visitor = dataSource.createShardingVisitor();
        SQLStatement sqlStmt = dataSource.parseStatement(sql);

        sqlStmt.accept(visitor);

        List<SQLStatement> resultSqlStmtList = visitor.getResult();

        int updateCount = 0;

        for (SQLStatement resultSqlStmt : resultSqlStmtList) {
            String resultSql = SQLUtils.toSQLString(resultSqlStmt, dataSource.getDbType());

            String database = (String) resultSqlStmt.getAttribute(ShardingVisitor.ATTR_DB);

            if (conn.getRealConnection() != null && StringUtils.equals(database, conn.getDatabase())) {
                if (stmt == null) {
                    stmt = createRealStatement(conn.getRealConnection());
                }
            } else {
                conn.closeRealConnection();
                if (stmt != null) {
                    JdbcUtils.close(stmt);
                    stmt = null;
                }
                conn.createRealConnectionByDb(database);
                stmt = createRealStatement(conn.getRealConnection());
            }
            updateCount += stmt.executeUpdate(resultSql);
        }

        return updateCount;
    }

    @Override
    public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException {
        ShardingDataSource dataSource = conn.getDataSource();
        ShardingVisitor visitor = dataSource.createShardingVisitor();
        SQLStatement sqlStmt = dataSource.parseStatement(sql);

        sqlStmt.accept(visitor);

        List<SQLStatement> resultSqlStmtList = visitor.getResult();

        if (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS && resultSqlStmtList.size() > 1
            && sqlStmt instanceof SQLInsertStatement) {
            throw new SQLException("RETURN_GENERATED_KEYS not support multi-statement");
        }

        int updateCount = 0;

        for (SQLStatement resultSqlStmt : resultSqlStmtList) {
            String resultSql = SQLUtils.toSQLString(resultSqlStmt, dataSource.getDbType());

            String database = (String) resultSqlStmt.getAttribute(ShardingVisitor.ATTR_DB);

            if (conn.getRealConnection() != null && StringUtils.equals(database, conn.getDatabase())) {
                if (stmt == null) {
                    stmt = createRealStatement(conn.getRealConnection());
                }
            } else {
                conn.closeRealConnection();
                if (stmt != null) {
                    JdbcUtils.close(stmt);
                    stmt = null;
                }
                conn.createRealConnectionByDb(database);
                stmt = createRealStatement(conn.getRealConnection());
            }
            updateCount += stmt.executeUpdate(resultSql, autoGeneratedKeys);
        }

        return updateCount;
    }

    @Override
    public int executeUpdate(String sql, int[] columnIndexes) throws SQLException {
        ShardingDataSource dataSource = conn.getDataSource();
        ShardingVisitor visitor = dataSource.createShardingVisitor();
        SQLStatement sqlStmt = dataSource.parseStatement(sql);

        sqlStmt.accept(visitor);

        List<SQLStatement> resultSqlStmtList = visitor.getResult();

        int updateCount = 0;

        for (SQLStatement resultSqlStmt : resultSqlStmtList) {
            String resultSql = SQLUtils.toSQLString(resultSqlStmt, dataSource.getDbType());

            String database = (String) resultSqlStmt.getAttribute(ShardingVisitor.ATTR_DB);

            if (conn.getRealConnection() != null && StringUtils.equals(database, conn.getDatabase())) {
                if (stmt == null) {
                    stmt = createRealStatement(conn.getRealConnection());
                }
            } else {
                conn.closeRealConnection();
                if (stmt != null) {
                    JdbcUtils.close(stmt);
                    stmt = null;
                }
                conn.createRealConnectionByDb(database);
                stmt = createRealStatement(conn.getRealConnection());
            }
            updateCount += stmt.executeUpdate(resultSql, columnIndexes);
        }

        return updateCount;
    }

    @Override
    public int executeUpdate(String sql, String[] columnNames) throws SQLException {
        ShardingDataSource dataSource = conn.getDataSource();
        ShardingVisitor visitor = dataSource.createShardingVisitor();
        SQLStatement sqlStmt = dataSource.parseStatement(sql);

        sqlStmt.accept(visitor);

        List<SQLStatement> resultSqlStmtList = visitor.getResult();

        int updateCount = 0;

        for (SQLStatement resultSqlStmt : resultSqlStmtList) {
            String resultSql = SQLUtils.toSQLString(resultSqlStmt, dataSource.getDbType());

            String database = (String) resultSqlStmt.getAttribute(ShardingVisitor.ATTR_DB);

            if (conn.getRealConnection() != null && StringUtils.equals(database, conn.getDatabase())) {
                if (stmt == null) {
                    stmt = createRealStatement(conn.getRealConnection());
                }
            } else {
                conn.closeRealConnection();
                if (stmt != null) {
                    JdbcUtils.close(stmt);
                    stmt = null;
                }
                conn.createRealConnectionByDb(database);
                stmt = createRealStatement(conn.getRealConnection());
            }
            updateCount += stmt.executeUpdate(resultSql, columnNames);
        }

        return updateCount;
    }

    @Override
    public boolean execute(String sql, int autoGeneratedKeys) throws SQLException {
        ShardingDataSource dataSource = conn.getDataSource();
        ShardingVisitor visitor = dataSource.createShardingVisitor();
        SQLStatement sqlStmt = dataSource.parseStatement(sql);

        sqlStmt.accept(visitor);

        List<SQLStatement> resultSqlStmtList = visitor.getResult();

        boolean isSelelct = sqlStmt instanceof SQLSelectStatement;

        if (resultSqlStmtList.size() > 1) {
            if (autoGeneratedKeys == Statement.RETURN_GENERATED_KEYS && sqlStmt instanceof SQLInsertStatement) {
                throw new SQLException("RETURN_GENERATED_KEYS not support multi-statement");
            } else if (isSelelct) {
                throw new SQLException("select not support multi-statement");
            }
        }

        for (SQLStatement resultSqlStmt : resultSqlStmtList) {
            String resultSql = SQLUtils.toSQLString(resultSqlStmt, dataSource.getDbType());

            String database = (String) resultSqlStmt.getAttribute(ShardingVisitor.ATTR_DB);

            if (conn.getRealConnection() != null && StringUtils.equals(database, conn.getDatabase())) {
                if (stmt == null) {
                    stmt = createRealStatement(conn.getRealConnection());
                }
            } else {
                conn.closeRealConnection();
                if (stmt != null) {
                    JdbcUtils.close(stmt);
                    stmt = null;
                }
                conn.createRealConnectionByDb(database);
                stmt = createRealStatement(conn.getRealConnection());
            }
            stmt.execute(resultSql, autoGeneratedKeys);

            if (!isSelelct) {
                if (updateCount == null) {
                    updateCount = stmt.getUpdateCount();
                } else {
                    updateCount = updateCount.intValue() + stmt.getUpdateCount();
                }
            }
        }

        return isSelelct;
    }

    @Override
    public boolean execute(String sql, int[] columnIndexes) throws SQLException {
        ShardingDataSource dataSource = conn.getDataSource();
        ShardingVisitor visitor = dataSource.createShardingVisitor();
        SQLStatement sqlStmt = dataSource.parseStatement(sql);

        sqlStmt.accept(visitor);

        List<SQLStatement> resultSqlStmtList = visitor.getResult();

        boolean isSelelct = sqlStmt instanceof SQLSelectStatement;

        if (resultSqlStmtList.size() > 1 && isSelelct) {
            throw new SQLException("select not support multi-statement");
        }

        for (SQLStatement resultSqlStmt : resultSqlStmtList) {
            String resultSql = SQLUtils.toSQLString(resultSqlStmt, dataSource.getDbType());

            String database = (String) resultSqlStmt.getAttribute(ShardingVisitor.ATTR_DB);

            if (conn.getRealConnection() != null && StringUtils.equals(database, conn.getDatabase())) {
                if (stmt == null) {
                    stmt = createRealStatement(conn.getRealConnection());
                }
            } else {
                conn.closeRealConnection();
                if (stmt != null) {
                    JdbcUtils.close(stmt);
                    stmt = null;
                }
                conn.createRealConnectionByDb(database);
                stmt = createRealStatement(conn.getRealConnection());
            }
            stmt.execute(resultSql, columnIndexes);

            if (!isSelelct) {
                if (updateCount == null) {
                    updateCount = stmt.getUpdateCount();
                } else {
                    updateCount = updateCount.intValue() + stmt.getUpdateCount();
                }
            }
        }

        return isSelelct;
    }

    @Override
    public boolean execute(String sql, String[] columnNames) throws SQLException {
        ShardingDataSource dataSource = conn.getDataSource();
        ShardingVisitor visitor = dataSource.createShardingVisitor();
        SQLStatement sqlStmt = dataSource.parseStatement(sql);

        sqlStmt.accept(visitor);

        List<SQLStatement> resultSqlStmtList = visitor.getResult();

        boolean isSelelct = sqlStmt instanceof SQLSelectStatement;

        if (resultSqlStmtList.size() > 1 && isSelelct) {
            throw new SQLException("select not support multi-statement");
        }

        for (SQLStatement resultSqlStmt : resultSqlStmtList) {
            String resultSql = SQLUtils.toSQLString(resultSqlStmt, dataSource.getDbType());

            String database = (String) resultSqlStmt.getAttribute(ShardingVisitor.ATTR_DB);

            if (conn.getRealConnection() != null && StringUtils.equals(database, conn.getDatabase())) {
                if (stmt == null) {
                    stmt = createRealStatement(conn.getRealConnection());
                }
            } else {
                conn.closeRealConnection();
                if (stmt != null) {
                    JdbcUtils.close(stmt);
                    stmt = null;
                }
                conn.createRealConnectionByDb(database);
                stmt = createRealStatement(conn.getRealConnection());
            }
            stmt.execute(resultSql, columnNames);

            if (!isSelelct) {
                if (updateCount == null) {
                    updateCount = stmt.getUpdateCount();
                } else {
                    updateCount = updateCount.intValue() + stmt.getUpdateCount();
                }
            }
        }

        return isSelelct;
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

    public void closeOnCompletion() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }

    public boolean isCloseOnCompletion() throws SQLException {
        throw new SQLFeatureNotSupportedException();
    }
}
