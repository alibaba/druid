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
package com.alibaba.druid.pool.xa;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.ConnectionEventListener;
import javax.sql.StatementEventListener;
import javax.sql.XAConnection;
import javax.transaction.xa.XAResource;

import com.alibaba.druid.pool.DruidPooledConnection;

public class DruidPooledXAConnection implements XAConnection {

    private DruidPooledConnection pooledConnection;
    private XAConnection          xaConnection;

    public DruidPooledXAConnection(DruidPooledConnection pooledConnection, XAConnection xaConnection){
        this.pooledConnection = pooledConnection;
        this.xaConnection = xaConnection;

    }

    @Override
    public Connection getConnection() throws SQLException {
        return pooledConnection;
    }

    @Override
    public void close() throws SQLException {
        pooledConnection.close();
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {
        pooledConnection.addConnectionEventListener(listener);
    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {
        pooledConnection.removeConnectionEventListener(listener);
    }

    @Override
    public void addStatementEventListener(StatementEventListener listener) {
        pooledConnection.addStatementEventListener(listener);
    }

    @Override
    public void removeStatementEventListener(StatementEventListener listener) {
        pooledConnection.removeStatementEventListener(listener);
    }

    @Override
    public XAResource getXAResource() throws SQLException {
        return xaConnection.getXAResource();
    }

}
