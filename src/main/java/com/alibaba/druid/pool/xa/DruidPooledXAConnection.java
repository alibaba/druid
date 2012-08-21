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
