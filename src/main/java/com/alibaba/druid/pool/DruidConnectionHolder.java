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

import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcConstants;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.Utils;

import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author wenshao<szujobs@hotmail.com>
 */
public final class DruidConnectionHolder {

    private final static Log                    LOG                      = LogFactory.getLog(DruidConnectionHolder.class);

    private final DruidAbstractDataSource       dataSource;
    private final Connection                    conn;
    private final List<ConnectionEventListener> connectionEventListeners = new CopyOnWriteArrayList<ConnectionEventListener>();
    private final List<StatementEventListener>  statementEventListeners  = new CopyOnWriteArrayList<StatementEventListener>();
    private final long                          connectTimeMillis;
    private transient long                      lastActiveTimeMillis;
    private long                                useCount                 = 0;

    private PreparedStatementPool               statementPool;

    private final List<Statement>               statementTrace           = new ArrayList<Statement>(2);

    private final boolean                       defaultReadOnly;
    private final int                           defaultHoldability;
    private final int                           defaultTransactionIsolation;

    private final boolean                       defaultAutoCommit;

    private boolean                             underlyingReadOnly;
    private int                                 underlyingHoldability;
    private int                                 underlyingTransactionIsolation;
    private boolean                             underlyingAutoCommit;
    private boolean                             discard                  = false;
    
    public static boolean                       holdabilityUnsupported   = false;

    public DruidConnectionHolder(DruidAbstractDataSource dataSource, Connection conn) throws SQLException{

        this.dataSource = dataSource;
        this.conn = conn;
        this.connectTimeMillis = System.currentTimeMillis();
        this.lastActiveTimeMillis = connectTimeMillis;

        this.underlyingAutoCommit = conn.getAutoCommit();

        {
            boolean initUnderlyHoldability = !holdabilityUnsupported;
            if (JdbcConstants.SYBASE.equals(dataSource.getDbType()) //
                || JdbcConstants.DB2.equals(dataSource.getDbType()) //
            ) {
                initUnderlyHoldability = false;
            }
            if (initUnderlyHoldability) {
                try {
                    this.underlyingHoldability = conn.getHoldability();
                } catch (UnsupportedOperationException e) {
                    holdabilityUnsupported = true;
                    LOG.warn("getHoldability unsupported", e);
                } catch (SQLFeatureNotSupportedException e) {
                    holdabilityUnsupported = true;
                    LOG.warn("getHoldability unsupported", e);
                } catch (SQLException e) {
                    // bug fixed for hive jdbc-driver
                    if ("Method not supported".equals(e.getMessage())) {
                        holdabilityUnsupported = true;
                    }
                    LOG.warn("getHoldability error", e);
                }
            }
        }

        this.underlyingReadOnly = conn.isReadOnly();
        try {
            this.underlyingTransactionIsolation = conn.getTransactionIsolation();
        } catch (SQLException e) {
            // compartible for alibaba corba
            if (!"com.mysql.jdbc.exceptions.jdbc4.MySQLSyntaxErrorException".equals(e.getClass().getName())) { 
                throw e;
            }
        }

        this.defaultHoldability = underlyingHoldability;
        this.defaultTransactionIsolation = underlyingTransactionIsolation;
        this.defaultAutoCommit = underlyingAutoCommit;
        this.defaultReadOnly = underlyingReadOnly;
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

    public void addTrace(DruidPooledStatement stmt) {
        statementTrace.add(stmt);
    }

    public void removeTrace(DruidPooledStatement stmt) {
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

    public PreparedStatementPool getStatementPoolDirect() {
        return statementPool;
    }

    public void clearStatementCache() {
        if (this.statementPool == null) {
            return;
        }
        this.statementPool.clear();
    }

    public DruidAbstractDataSource getDataSource() {
        return dataSource;
    }

    public boolean isPoolPreparedStatements() {
        return dataSource.isPoolPreparedStatements();
    }

    public Connection getConnection() {
        return conn;
    }

    public long getTimeMillis() {
        return connectTimeMillis;
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
            underlyingReadOnly = defaultReadOnly;
        }

        if (underlyingHoldability != defaultHoldability) {
            conn.setHoldability(defaultHoldability);
            underlyingHoldability = defaultHoldability;
        }

        if (underlyingTransactionIsolation != defaultTransactionIsolation) {
            conn.setTransactionIsolation(defaultTransactionIsolation);
            underlyingTransactionIsolation = defaultTransactionIsolation;
        }

        if (underlyingAutoCommit != defaultAutoCommit) {
            conn.setAutoCommit(defaultAutoCommit);
            underlyingAutoCommit = defaultAutoCommit;
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

    public boolean isDiscard() {
        return discard;
    }

    public void setDiscard(boolean discard) {
        this.discard = discard;
    }

    public String toString() {
        StringBuilder buf = new StringBuilder();

        buf.append("{ID:");
        buf.append(System.identityHashCode(conn));
        buf.append(", ConnectTime:\"");
        buf.append(Utils.toString(new Date(this.connectTimeMillis)));

        buf.append("\", UseCount:");
        buf.append(useCount);

        if (lastActiveTimeMillis > 0) {
            buf.append(", LastActiveTime:\"");
            buf.append(Utils.toString(new Date(this.lastActiveTimeMillis)));
            buf.append("\"");
        }

        if (statementPool != null && statementPool.getMap().size() > 0) {
            buf.append("\", CachedStatementCount:");
            buf.append(statementPool.getMap().size());
        }

        buf.append("}");

        return buf.toString();
    }

}
