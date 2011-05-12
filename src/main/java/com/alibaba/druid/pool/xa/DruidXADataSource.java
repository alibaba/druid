/*
 * Copyright 2011 Alibaba Group.
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

import java.sql.SQLException;

import javax.sql.XAConnection;
import javax.sql.XADataSource;
import javax.transaction.TransactionManager;

import org.apache.commons.dbcp.AbandonedConfig;
import org.apache.commons.dbcp.PoolableConnectionFactory;
import org.apache.commons.dbcp.PoolingDataSource;
import org.apache.commons.dbcp.managed.ManagedDataSource;
import org.apache.commons.dbcp.managed.PoolableManagedConnectionFactory;
import org.apache.commons.pool.KeyedObjectPoolFactory;

import com.alibaba.druid.pool.ConnectionFactory;
import com.alibaba.druid.pool.DruidDataSource;

public class DruidXADataSource extends DruidDataSource {

    private static final long            serialVersionUID = 1L;

    /** Transaction Registry */
    private TransactionRegistry          transactionRegistry;
    /** Transaction Manager */
    private transient TransactionManager transactionManager;
    /** XA datasource class name */
    private String                       xaDataSource;
    /** XA datasource instance */
    private XADataSource                 xaDataSourceInstance;

    /**
     * Gets the XADataSource instance used by the XAConnectionFactory.
     * 
     * @return the XADataSource
     */
    public synchronized XADataSource getXaDataSourceInstance() {
        return xaDataSourceInstance;
    }

    /**
     * <p>
     * Sets the XADataSource instance used by the XAConnectionFactory.
     * </p>
     * <p>
     * Note: this method currently has no effect once the pool has been initialized. The pool is initialized the first
     * time one of the following methods is invoked: <code>getConnection, setLogwriter,
     * setLoginTimeout, getLoginTimeout, getLogWriter.</code>
     * </p>
     * 
     * @param xaDataSourceInstance XADataSource instance
     */
    public synchronized void setXaDataSourceInstance(XADataSource xaDataSourceInstance) {
        this.xaDataSourceInstance = xaDataSourceInstance;
        xaDataSource = xaDataSourceInstance == null ? null : xaDataSourceInstance.getClass().getName();
    }

    /**
     * Gets the required transaction manager property.
     * 
     * @return the transaction manager used to enlist connections
     */
    public TransactionManager getTransactionManager() {
        return transactionManager;
    }

    /**
     * Gets the transaction registry.
     * 
     * @return the transaction registry associating XAResources with managed connections
     */
    protected synchronized TransactionRegistry getTransactionRegistry() {
        return transactionRegistry;
    }

    /**
     * Sets the required transaction manager property.
     * 
     * @param transactionManager the transaction manager used to enlist connections
     */
    public void setTransactionManager(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Gets the optional XADataSource class name.
     * 
     * @return the optional XADataSource class name
     */
    public synchronized String getXADataSource() {
        return xaDataSource;
    }

    /**
     * Sets the optional XADataSource class name.
     * 
     * @param xaDataSource the optional XADataSource class name
     */
    public synchronized void setXADataSource(String xaDataSource) {
        this.xaDataSource = xaDataSource;
    }

    protected ConnectionFactory createConnectionFactory() throws SQLException {
        if (transactionManager == null) {
            throw new SQLException("Transaction manager must be set before a connection can be created");
        }

        // If xa data source is not specified a DriverConnectionFactory is created and wrapped with a
        // LocalXAConnectionFactory
        if (xaDataSource == null) {
            ConnectionFactory connectionFactory = super.createConnectionFactory();
            XAConnectionFactory xaConnectionFactory = new LocalXAConnectionFactory(getTransactionManager(),
                                                                                   connectionFactory);
            transactionRegistry = xaConnectionFactory.getTransactionRegistry();
            return xaConnectionFactory;
        }

        // Create the XADataSource instance using the configured class name if it has not been set
        if (xaDataSourceInstance == null) {
            Class xaDataSourceClass = null;
            try {
                xaDataSourceClass = Class.forName(xaDataSource);
            } catch (Throwable t) {
                String message = "Cannot load XA data source class '" + xaDataSource + "'";
                throw (SQLException) new SQLException(message).initCause(t);
            }

            try {
                xaDataSourceInstance = (XADataSource) xaDataSourceClass.newInstance();
            } catch (Throwable t) {
                String message = "Cannot create XA data source of class '" + xaDataSource + "'";
                throw (SQLException) new SQLException(message).initCause(t);
            }
        }

        // finally, create the XAConectionFactory using the XA data source
        XAConnectionFactory xaConnectionFactory = new DataSourceXAConnectionFactory(getTransactionManager(),
                                                                                    xaDataSourceInstance, username,
                                                                                    password);
        transactionRegistry = xaConnectionFactory.getTransactionRegistry();
        return xaConnectionFactory;
    }

    /**
     * Creates the PoolableConnectionFactory and attaches it to the connection pool.
     * 
     * @param driverConnectionFactory JDBC connection factory created by {@link #createConnectionFactory()}
     * @param statementPoolFactory statement pool factory (null if statement pooling is turned off)
     * @param abandonedConfig abandoned connection tracking configuration (null if no tracking)
     * @throws SQLException if an error occurs creating the PoolableConnectionFactory
     */
    protected void createPoolableConnectionFactory(ConnectionFactory driverConnectionFactory,
                                                   KeyedObjectPoolFactory statementPoolFactory,
                                                   AbandonedConfig abandonedConfig) throws SQLException {
        // PoolableConnectionFactory connectionFactory = null;
        // try {
        // connectionFactory = new PoolableManagedConnectionFactory((XAConnectionFactory) driverConnectionFactory,
        // connectionPool, statementPoolFactory,
        // validationQuery, validationQueryTimeout,
        // connectionInitSqls, defaultReadOnly,
        // defaultAutoCommit, defaultTransactionIsolation,
        // defaultCatalog, abandonedConfig);
        // validateConnectionFactory(connectionFactory);
        // } catch (RuntimeException e) {
        // throw e;
        // } catch (Exception e) {
        // throw (SQLException) new SQLException("Cannot create PoolableConnectionFactory (" + e.getMessage() +
        // ")").initCause(e);
        // }
        throw new SQLException("TODO");
    }

}
