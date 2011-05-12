package com.alibaba.druid.pool.xa;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * An implementation of XAConnectionFactory which uses a real XADataSource to obtain connections and XAResources.
 *
 * @author Dain Sundstrom
 * @version $Revision$
 */
public class DataSourceXAConnectionFactory implements XAConnectionFactory {
    protected TransactionRegistry transactionRegistry;
    protected XADataSource xaDataSource;
    protected String username;
    protected String password;

    /**
     * Creates an DataSourceXAConnectionFactory which uses the specified XADataSource to create database
     * connections.  The connections are enlisted into transactions using the specified transaction manager.
     *
     * @param transactionManager the transaction manager in which connections will be enlisted
     * @param xaDataSource the data source from which connections will be retrieved
     */
    public DataSourceXAConnectionFactory(TransactionManager transactionManager, XADataSource xaDataSource) {
        this(transactionManager, xaDataSource, null, null);
    }

    /**
     * Creates an DataSourceXAConnectionFactory which uses the specified XADataSource to create database
     * connections.  The connections are enlisted into transactions using the specified transaction manager.
     *
     * @param transactionManager the transaction manager in which connections will be enlisted
     * @param xaDataSource the data source from which connections will be retrieved
     * @param username the username used for authenticating new connections or null for unauthenticated
     * @param password the password used for authenticating new connections
     */
    public DataSourceXAConnectionFactory(TransactionManager transactionManager, XADataSource xaDataSource, String username, String password) {
        if (transactionManager == null) throw new NullPointerException("transactionManager is null");
        if (xaDataSource == null) throw new NullPointerException("xaDataSource is null");

        this.transactionRegistry = new TransactionRegistry(transactionManager);
        this.xaDataSource = xaDataSource;
        this.username = username;
        this.password = password;
    }

    /**
     * Gets the username used to authenticate new connections.
     * @return the user name or null if unauthenticated connections are used
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username used to authenticate new connections.
     * @param username the username used for authenticating the connection or null for unauthenticated
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Sets the password used to authenticate new connections.
     * @param password the password used for authenticating the connection or null for unauthenticated
     */
    public void setPassword(String password) {
        this.password = password;
    }

    public TransactionRegistry getTransactionRegistry() {
        return transactionRegistry;
    }

    public Connection createConnection() throws SQLException {
        // create a new XAConection
        XAConnection xaConnection;
        if (username == null) {
            xaConnection = xaDataSource.getXAConnection();
        } else {
            xaConnection = xaDataSource.getXAConnection(username, password);
        }

        // get the real connection and XAResource from the connection
        Connection connection = xaConnection.getConnection();
        XAResource xaResource = xaConnection.getXAResource();

        // register the xa resource for the connection
        transactionRegistry.registerConnection(connection, xaResource);

        return connection;
    }
}

