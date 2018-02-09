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

import com.alibaba.druid.util.JdbcUtils;

import net.sourceforge.jtds.jdbc.XASupport;

public class JtdsXAConnection implements XAConnection {

    private Connection       connection;

    private final XAResource resource;
    private final int        xaConnectionId;

    public JtdsXAConnection(Connection connection) throws SQLException{
        this.resource = new JtdsXAResource(this, connection);
        this.connection = connection;
        this.xaConnectionId = XASupport.xa_open(connection);
    }

    int getXAConnectionID() {
        return this.xaConnectionId;
    }

    @Override
    public Connection getConnection() throws SQLException {
        return connection;
    }

    @Override
    public void close() throws SQLException {
        try {
            XASupport.xa_close(connection, xaConnectionId);
        } catch (SQLException e) {
            // Ignore close errors
        }
        
        JdbcUtils.close(connection);
    }

    @Override
    public void addConnectionEventListener(ConnectionEventListener listener) {

    }

    @Override
    public void removeConnectionEventListener(ConnectionEventListener listener) {

    }

    @Override
    public void addStatementEventListener(StatementEventListener listener) {

    }

    @Override
    public void removeStatementEventListener(StatementEventListener listener) {

    }

    @Override
    public XAResource getXAResource() throws SQLException {
        return resource;
    }

}
