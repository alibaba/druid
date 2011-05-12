package com.alibaba.druid.pool.xa;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Map;
import java.util.WeakHashMap;

import javax.transaction.Status;
import javax.transaction.SystemException;
import javax.transaction.Transaction;
import javax.transaction.TransactionManager;
import javax.transaction.xa.XAResource;


/**
 * TransactionRegistry tracks Connections and XAResources in a transacted environment for a single XAConnectionFactory.
 * </p>
 * The TransactionRegistry hides the details of transaction processing from the existing DBCP pooling code, and gives
 * the ManagedConnection a way to enlist connections in a transaction, allowing for the maximal rescue of DBCP.
 *
 * @author Dain Sundstrom
 * @version $Revision$
 */
public class TransactionRegistry {
    private final TransactionManager transactionManager;
    private final Map<Transaction, TransactionContext> caches = new WeakHashMap<Transaction, TransactionContext>();
    private final Map<Connection, XAResource> xaResources = new WeakHashMap<Connection, XAResource>();

    /**
     * Creates a TransactionRegistry for the specified transaction manager.
     * @param transactionManager the transaction manager used to enlist connections
     */
    public TransactionRegistry(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    /**
     * Registers the association between a Connection and a XAResource.  When a connection
     * is enlisted in a transaction, it is actually the XAResource that is given to the transaction
     * manager.
     *
     * @param connection the JDBC connection
     * @param xaResource the XAResource which managed the connection within a transaction
     */
    public synchronized void registerConnection(Connection connection, XAResource xaResource) {
        if (connection == null) throw new NullPointerException("connection is null");
        if (xaResource == null) throw new NullPointerException("xaResource is null");
        xaResources.put(connection, xaResource);
    }

    /**
     * Gets the XAResource registered for the connection.
     * @param connection the connection
     * @return the XAResource registered for the connection; never null
     * @throws SQLException if the connection does not have a registered XAResource
     */
    public synchronized XAResource getXAResource(Connection connection) throws SQLException {
        if (connection == null) throw new NullPointerException("connection is null");
        XAResource xaResource = (XAResource) xaResources.get(connection);
        if (xaResource == null) {
            throw new SQLException("Connection does not have a registered XAResource " + connection);
        }
        return xaResource;
    }

    /**
     * Gets the active TransactionContext or null if not Transaction is active.
     * @return the active TransactionContext or null if not Transaction is active
     * @throws SQLException if an error occurs while fetching the transaction
     */
    public TransactionContext getActiveTransactionContext() throws SQLException {
        Transaction transaction = null;
        try {
            transaction = transactionManager.getTransaction();

            // was there a transaction?
            if (transaction == null) {
                return null;
            }

            // is it active
            int status = transaction.getStatus();
            if (status != Status.STATUS_ACTIVE && status != Status.STATUS_MARKED_ROLLBACK) {
                return null;
            }
        } catch (SystemException e) {
            throw (SQLException) new SQLException("Unable to determine current transaction ").initCause(e);
        }

        // register the the context (or create a new one)
        synchronized (this) {
            TransactionContext cache = (TransactionContext) caches.get(transaction);
            if (cache == null) {
                cache = new TransactionContext(this, transaction);
                caches.put(transaction, cache);
            }
            return cache;
        }
    }

    /**
     * Unregisters a destroyed connection from {@link TransactionRegistry}
     * @param connection
     */
    public synchronized void unregisterConnection(Connection connection) {
        xaResources.remove(connection);
    }
}

