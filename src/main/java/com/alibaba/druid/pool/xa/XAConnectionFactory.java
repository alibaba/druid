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

import com.alibaba.druid.pool.ConnectionFactory;

/**
 * XAConnectionFactory is an extension of ConnectionFactory used to create connections
 * in a transaction managed environment.  The XAConnectionFactory opperates like a normal
 * ConnectionFactory except an TransactionRegistry is provided from which the XAResource
 * for a connection can be obtained.  This allows the existing DBCP pool code to work with
 * XAConnections and gives a the ManagedConnection a way to enlist a connection in the
 * the transaction.
 *
 * @author Dain Sundstrom
 * @author Rodney Waldhoff
 * @version $Revision$
 */
public interface XAConnectionFactory extends ConnectionFactory {
    /**
     * Gets the TransactionRegistry for this connection factory which contains a the
     * XAResource for every connection created by this factory.
     *
     * @return the transaction registry for this connection factory
     */
    TransactionRegistry getTransactionRegistry();

    /**
     * Create a new {@link java.sql.Connection} in an implementation specific fashion.
     * </p>
     * An implementation can assume that the caller of this will wrap the connection in
     * a proxy that protects access to the setAutoCommit, commit and rollback when
     * enrolled in a XA transaction.
     *
     * @return a new {@link java.sql.Connection}
     * @throws java.sql.SQLException if a database error occurs creating the connection
     */
    Connection createConnection() throws SQLException;
}

