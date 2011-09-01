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
    private long                                lastCheckTimeMillis      = 0;
    private long                                useCount                 = 0;

    private final boolean                       poolPreparedStatements;
    private final PreparedStatementPool         statementPool;

    private final List<Statement>               statementTrace           = new ArrayList<Statement>();

    public ConnectionHolder(DruidAbstractDataSource dataSource, Connection conn){
        this.dataSource = dataSource;
        this.conn = conn;
        this.poolPreparedStatements = dataSource.isPoolPreparedStatements();
        this.connecttimeMillis = System.currentTimeMillis();
        this.lastActiveTimeMillis = connecttimeMillis;

        if (this.poolPreparedStatements) {
            statementPool = new PreparedStatementPool();
        } else {
            statementPool = null;
        }
    }

    public long getLastCheckTimeMillis() {
        return lastCheckTimeMillis;
    }

    public void setLastCheckTimeMillis(long lastCheckTimeMillis) {
        this.lastCheckTimeMillis = lastCheckTimeMillis;
    }

    public long getLastActiveTimeMillis() {
        return lastActiveTimeMillis;
    }

    public void setLastActiveTimeMillis(long lastActiveMillis) {
        this.lastActiveTimeMillis = lastActiveMillis;
    }

    public void addTrace(Statement stmt) {
        statementTrace.add(stmt);
    }

    public void removeTrace(Statement stmt) {
        statementTrace.remove(stmt);
    }

    public List<ConnectionEventListener> getConnectionEventListeners() {
        return connectionEventListeners;
    }

    public List<StatementEventListener> getStatementEventListeners() {
        return statementEventListeners;
    }

    public PreparedStatementPool getStatementPool() {
        return statementPool;
    }

    public DruidAbstractDataSource getDataSource() {
        return dataSource;
    }

    public boolean isPoolPreparedStatements() {
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

    public void reset() {
        connectionEventListeners.clear();
        statementEventListeners.clear();

        for (Object item : statementTrace.toArray()) {
            Statement stmt = (Statement) item;
            JdbcUtils.close(stmt);
        }
        statementTrace.clear();
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
        
        buf.append("}");
        
        return buf.toString();
    }
}
