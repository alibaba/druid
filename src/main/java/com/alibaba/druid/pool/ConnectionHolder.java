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
package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;

import com.alibaba.druid.util.JdbcUtils;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public final class ConnectionHolder {

    private final DruidAbstractDataSource       dataSource;
    private final Connection                    conn;
    private final List<ConnectionEventListener> connectionEventListeners = new CopyOnWriteArrayList<ConnectionEventListener>();
    private final List<StatementEventListener>  statementEventListeners  = new CopyOnWriteArrayList<StatementEventListener>();
    private final long                          connecttimeMillis;
    private long                                lastActiveTimeMillis;
    private long                                useCount                 = 0;

    private PreparedStatementPool               statementPool;

    private final List<Statement>               statementTrace           = new ArrayList<Statement>();

    private final boolean                       defaultReadOnly;
    private final int                           defaultHoldability;
    private final int                           defaultTransactionIsolation;

    private final boolean                       defaultAutoCommit;

    private boolean                             underlyingReadOnly;
    private int                                 underlyingHoldability;
    private int                                 underlyingTransactionIsolation;
    private boolean                             underlyingAutoCommit;

    public ConnectionHolder(DruidAbstractDataSource dataSource, Connection conn) throws SQLException{
        this.dataSource = dataSource;
        this.conn = conn;
        this.connecttimeMillis = System.currentTimeMillis();
        this.lastActiveTimeMillis = connecttimeMillis;

        this.defaultReadOnly = conn.isReadOnly();
        this.defaultHoldability = conn.getHoldability();
        this.defaultTransactionIsolation = conn.getTransactionIsolation();
        this.defaultAutoCommit = conn.getAutoCommit();

        this.underlyingAutoCommit = defaultAutoCommit;
        this.underlyingHoldability = defaultHoldability;
        this.underlyingTransactionIsolation = defaultTransactionIsolation;
        this.underlyingTransactionIsolation = defaultTransactionIsolation;

        statementPool = null;
    }

    public boolean isUnderlyingReadOnly() {
        return underlyingReadOnly;
    }

    public void setUnderlyingReadOnly(boolean underlyingReadOnly) {
        this.underlyingReadOnly = underlyingReadOnly;
    }

    public int getUnderlyingHoldability() {
        return underlyingHoldability;
    }

    public void setUnderlyingHoldability(int underlyingHoldability) {
        this.underlyingHoldability = underlyingHoldability;
    }

    public int getUnderlyingTransactionIsolation() {
        return underlyingTransactionIsolation;
    }

    public void setUnderlyingTransactionIsolation(int underlyingTransactionIsolation) {
        this.underlyingTransactionIsolation = underlyingTransactionIsolation;
    }

    public boolean isUnderlyingAutoCommit() {
        return underlyingAutoCommit;
    }

    public void setUnderlyingAutoCommit(boolean underlyingAutoCommit) {
        this.underlyingAutoCommit = underlyingAutoCommit;
    }

    public long getLastActiveTimeMillis() {
        return lastActiveTimeMillis;
    }

    public void setLastActiveTimeMillis(long lastActiveMillis) {
        this.lastActiveTimeMillis = lastActiveMillis;
    }

    public void addTrace(PoolableStatement stmt) {
        statementTrace.add(stmt);
    }

    public void removeTrace(PoolableStatement stmt) {
        statementTrace.remove(stmt);
    }

    public List<ConnectionEventListener> getConnectionEventListeners() {
        return connectionEventListeners;
    }

    public List<StatementEventListener> getStatementEventListeners() {
        return statementEventListeners;
    }

    public PreparedStatementPool getStatementPool() {
        if (statementPool == null) {
            statementPool = new PreparedStatementPool(this);
        }
        return statementPool;
    }

    public DruidAbstractDataSource getDataSource() {
        return dataSource;
    }

    public boolean isPoolPreparedStatements() {
        boolean poolPreparedStatements = dataSource.isPoolPreparedStatements();
        if (poolPreparedStatements) {
            boolean transaction = !isUnderlyingAutoCommit();
            if (transaction && !dataSource.isSharePreparedStatements()) {
                poolPreparedStatements = false;
            }
        }

        return poolPreparedStatements;
    }

    public Connection getConnection() {
        return conn;
    }

    public long getTimeMillis() {
        return connecttimeMillis;
    }

    public long getUseCount() {
        return useCount;
    }

    public void incrementUseCount() {
        useCount++;
    }

    public void reset() throws SQLException {
        // reset default settings
        if (underlyingReadOnly != defaultReadOnly) {
            conn.setReadOnly(defaultReadOnly);
        }

        if (underlyingHoldability != defaultHoldability) {
            conn.setHoldability(defaultHoldability);
        }

        if (underlyingTransactionIsolation != defaultTransactionIsolation) {
            conn.setTransactionIsolation(defaultTransactionIsolation);
        }

        if (underlyingAutoCommit != defaultAutoCommit) {
            conn.setAutoCommit(defaultAutoCommit);
        }

        connectionEventListeners.clear();
        statementEventListeners.clear();

        for (Object item : statementTrace.toArray()) {
            Statement stmt = (Statement) item;
            JdbcUtils.close(stmt);
        }
        statementTrace.clear();

        conn.clearWarnings();
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append("{ID:");
        buf.append(System.identityHashCode(conn));
        buf.append(", ConnectTime:\"");
        buf.append(JdbcUtils.toString(new Date(this.connecttimeMillis)));

        buf.append("\", UseCount:");
        buf.append(useCount);

        if (lastActiveTimeMillis > 0) {
            buf.append(", LastActiveTime:\"");
            buf.append(JdbcUtils.toString(new Date(this.lastActiveTimeMillis)));
            buf.append("\"");
        }

        PreparedStatementPool statmentPool = this.getStatementPool();
        if (statmentPool != null && statmentPool.getMap().size() > 0) {
            buf.append("\", CachedStatementCount:");
            buf.append(this.getStatementPool().getMap().size());
        }

        buf.append("}");

        return buf.toString();
    }

}
