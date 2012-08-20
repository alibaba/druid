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
