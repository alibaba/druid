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
package com.alibaba.druid.pool.xa;

import java.sql.Connection;
import java.sql.SQLException;

import com.alibaba.druid.pool.ConnectionHolder;
import com.alibaba.druid.pool.PoolableConnection;

/**
 * ManagedConnection is responsible for managing a database connection in a transactional environment (typically called
 * "Container Managed"). A managed connection operates like any other connection when no global transaction (a.k.a. XA
 * transaction or JTA Transaction) is in progress. When a global transaction is active a single physical connection to
 * the database is used by all ManagedConnections accessed in the scope of the transaction. Connection sharing means
 * that all data access during a transaction has a consistent view of the database. When the global transaction is
 * committed or rolled back the enlisted connections are committed or rolled back. Typically upon transaction
 * completion, a connection returns to the auto commit setting in effect before being enlisted in the transaction, but
 * some vendors do not properly implement this. When enlisted in a transaction the setAutoCommit(), commit(),
 * rollback(), and setReadOnly() methods throw a SQLException. This is necessary to assure that the transaction
 * completes as a single unit.
 * 
 * @author Dain Sundstrom
 * @version $Revision$
 */
public class ManagedConnection extends PoolableConnection {

    private final TransactionRegistry transactionRegistry;
    private final boolean             accessToUnderlyingConnectionAllowed;
    private TransactionContext        transactionContext;
    private boolean                   isSharedConnection;

    public ManagedConnection(ConnectionHolder holder, TransactionRegistry transactionRegistry,
                             boolean accessToUnderlyingConnectionAllowed) throws SQLException{
        super(holder);
        this.transactionRegistry = transactionRegistry;
        this.accessToUnderlyingConnectionAllowed = accessToUnderlyingConnectionAllowed;
        updateTransactionStatus();
    }

    protected void checkOpen() throws SQLException {
        super.checkOpen();
        updateTransactionStatus();
    }

    private void updateTransactionStatus() throws SQLException {
        // // if there is a is an active transaction context, assure the transaction context hasn't changed
        // if (transactionContext != null) {
        // if (transactionContext.isActive()) {
        // if (transactionContext != transactionRegistry.getActiveTransactionContext()) {
        // throw new SQLException("Connection can not be used while enlisted in another transaction");
        // }
        // return;
        // } else {
        // // transaction should have been cleared up by TransactionContextListener, but in
        // // rare cases another lister could have registered which uses the connection before
        // // our listener is called. In that rare case, trigger the transaction complete call now
        // transactionComplete();
        // }
        // }
        //
        // // the existing transaction context ended (or we didn't have one), get the active transaction context
        // transactionContext = transactionRegistry.getActiveTransactionContext();
        //
        // // if there is an active transaction context and it already has a shared connection, use it
        // if (transactionContext != null && transactionContext.getSharedConnection() != null) {
        // // A connection for the connection factory has already been enrolled
        // // in the transaction, replace our delegate with the enrolled connection
        //
        // // return current connection to the pool
        // Connection connection = getDelegateInternal();
        // setDelegate(null);
        // if (connection != null) {
        // try {
        // pool.returnObject(connection);
        // } catch (Exception ignored) {
        // // whatever... try to invalidate the connection
        // try {
        // pool.invalidateObject(connection);
        // } catch (Exception ignore) {
        // // no big deal
        // }
        // }
        // }
        //
        // // add a listener to the transaction context
        // transactionContext.addTransactionContextListener(new CompletionListener());
        //
        // // set our delegate to the shared connection
        // setDelegate(transactionContext.getSharedConnection());
        //
        // // remember that we are using a shared connection so it can be cleared after the
        // // transaction completes
        // isSharedConnection = true;
        // } else {
        // // if our delegate is null, create one
        // if (getDelegateInternal() == null) {
        // try {
        // // borrow a new connection from the pool
        // Connection connection = (Connection) pool.borrowObject();
        // setDelegate(connection);
        // } catch (Exception e) {
        // throw (SQLException) new SQLException("Unable to acquire a new connection from the pool").initCause(e);
        // }
        // }
        //
        // // if we have a transaction, out delegate becomes the shared delegate
        // if (transactionContext != null) {
        // // add a listener to the transaction context
        // transactionContext.addTransactionContextListener(new CompletionListener());
        //
        // // register our connection as the shared connection
        // try {
        // transactionContext.setSharedConnection(getDelegateInternal());
        // } catch (SQLException e) {
        // // transaction is hosed
        // transactionContext = null;
        // throw e;
        // }
        // }
        // }
        throw new SQLException("TODO");
    }

    public void close() throws SQLException {
        if (transactionContext == null) {
            super.close();
        }
    }

    /**
     * Delegates to {@link ManagedConnection#transactionComplete()} for transaction completion events.
     */
    protected class CompletionListener implements TransactionContextListener {

        public void afterCompletion(TransactionContext completedContext, boolean commited) {
            if (completedContext == transactionContext) {
                transactionComplete();
            }
        }
    }

    protected void transactionComplete() {
//        transactionContext = null;
//
//        // if we were using a shared connection, clear the reference now that the transaction has completed
//        if (isSharedConnection) {
//            // for now, just set the delegate to null, it will be created later if needed
//            setDelegate(null);
//            isSharedConnection = false;
//        }
//
//        // if this connection was closed during the transaction and there is still a delegate present close it
//        Connection delegate = getDelegateInternal();
//        if (isClosed() && delegate != null) {
//            try {
//                setDelegate(null);
//
//                // don't actually close the connection if in a transaction
//                if (!delegate.isClosed()) {
//                    // don't use super.close() because it calls passivate() which marks the
//                    // the connection as closed without returning it to the pool
//                    delegate.close();
//                }
//            } catch (SQLException ignored) {
//                // not a whole lot we can do here as connection is closed
//                // and this is a transaction callback so there is no
//                // way to report the error
//            } finally {
//                _closed = true;
//            }
//        }
        throw new RuntimeException("TODO");
    }

    //
    // The following methods can't be used while enlisted in a transaction
    //

    public void setAutoCommit(boolean autoCommit) throws SQLException {
        if (transactionContext != null) {
            throw new SQLException("Auto-commit can not be set while enrolled in a transaction");
        }
        super.setAutoCommit(autoCommit);
    }

    public void commit() throws SQLException {
        if (transactionContext != null) {
            throw new SQLException("Commit can not be set while enrolled in a transaction");
        }
        super.commit();
    }

    public void rollback() throws SQLException {
        if (transactionContext != null) {
            throw new SQLException("Commit can not be set while enrolled in a transaction");
        }
        super.rollback();
    }

    public void setReadOnly(boolean readOnly) throws SQLException {
        if (transactionContext != null) {
            throw new SQLException("Read-only can not be set while enrolled in a transaction");
        }
        super.setReadOnly(readOnly);
    }

    //
    // Methods for accessing the delegate connection
    //

    /**
     * If false, getDelegate() and getInnermostDelegate() will return null.
     * 
     * @return if false, getDelegate() and getInnermostDelegate() will return null
     */
    public boolean isAccessToUnderlyingConnectionAllowed() {
        return accessToUnderlyingConnectionAllowed;
    }

    @Deprecated
    public Connection getDelegate() {
        if (isAccessToUnderlyingConnectionAllowed()) {
            return getRawConnection();
        } else {
            return null;
        }
    }

    @Deprecated
    public Connection getInnermostDelegate() {
        if (isAccessToUnderlyingConnectionAllowed()) {
            return getRawConnection();
        } else {
            return null;
        }
    }
}
