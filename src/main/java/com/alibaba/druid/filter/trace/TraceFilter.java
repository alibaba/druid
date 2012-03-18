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
package com.alibaba.druid.filter.trace;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.SortedMap;
import java.util.TreeMap;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.proxy.jdbc.PreparedStatementProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.stat.JdbcStatContext;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.stat.JdbcTraceManager;

public class TraceFilter extends FilterAdapter implements TraceFilterMBean {


    private static final String TRACE_STMT_COLUMN = "stmt.columnNames";

    private static final String TRACE_STMT_RS_HOLDABILITY = "stmt.resultSetHoldability";

    public final static String ATTR_NAME_RESULT_SET      = "trace.rs";

    public final static String TRACE_CONN_ID             = "conn.id";
    public final static String TRACE_CONN_INFO           = "conn.info";
    public final static String TRACE_CONN_CONNECTED_TIME = "conn.connectedTime";
    public final static String TRACE_STMT_ID             = "stmt.id";
    public final static String TRACE_STMT_SQL            = "stmt.sql";
    public final static String TRACE_STMT_PARAMS         = "stmt.params";
    public final static String TRACE_STMT_UPDATE_COUNT   = "stmt.updateCount";
    public final static String TRACE_STMT_COLUMN_INDEXES = "stmt.columnIndexes";
    public final static String TRACE_STMT_RS_TYPE        = "stmt.resultSetType";
    public static final String TRACE_STMT_RS_CONCURRENCY = "stmt.resultSetConcurrency";
    public final static String TRACE_RS_ID               = "rs.id";
    public final static String TRACE_RS_CURSOR_INDEX     = "rs.cusorIndex";
    

    public TraceFilter(){

    }

    public boolean isTraceConnectionEnable() {
        return this.isTraceEnable();
    }

    public boolean isTraceStatementEnable() {
        return this.isTraceEnable();
    }

    public boolean isTraceResultSetEnable() {
        return this.isTraceEnable();
    }

    public boolean isTraceEnable() {
        JdbcStatContext statContext = JdbcStatManager.getInstance().getStatContext();

        if (statContext == null) {
            return JdbcTraceManager.getInstance().isTraceEnable();
        }

        return statContext.isTraceEnable();
    }

    public static String getAttrNameResultSet() {
        return ATTR_NAME_RESULT_SET;
    }

    @Override
    public ConnectionProxy connection_connect(FilterChain chain, Properties info) throws SQLException {
        if (!isTraceConnectionEnable()) {
            return chain.connection_connect(info);
        }

        long startMillis = System.currentTimeMillis();

        {
            TraceEvent event = new TraceEvent("ConnectionConnectBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, info.clone());
            fireEvent(event);
        }

        try {
            ConnectionProxy conn = chain.connection_connect(info);

            long timespan = System.currentTimeMillis() - startMillis;

            {
                TraceAfterEvent event = new TraceAfterEvent("ConnectionConnectAfter", new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, info.clone());
                event.putContext(TRACE_CONN_ID, conn.getId());
                fireEvent(event);
            }

            return conn;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionConnectError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, info.clone());
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionConnectError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, info.clone());
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public void connection_close(FilterChain chain, ConnectionProxy connection) throws SQLException {
        if (!isTraceConnectionEnable()) {
            chain.connection_close(connection);
            return;
        }

        long startMillis = System.currentTimeMillis();

        {
            TraceEvent event = new TraceEvent("ConnectionCloseBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            fireEvent(event);
        }

        try {
            chain.connection_close(connection);

            long timespan = System.currentTimeMillis() - startMillis;

            {
                TraceAfterEvent event = new TraceAfterEvent("ConnectionCloseAfter", new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                fireEvent(event);
            }
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionCloseError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionCloseError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public void connection_commit(FilterChain chain, ConnectionProxy connection) throws SQLException {
        if (!isTraceConnectionEnable()) {
            chain.connection_commit(connection);
            return;
        }

        long startMillis = System.currentTimeMillis();

        {
            TraceEvent event = new TraceEvent("ConnectionCommitBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            fireEvent(event);
        }

        try {
            chain.connection_commit(connection);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("ConnectionCommitAfter", new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                fireEvent(event);
            }
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionCommitError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionCommitError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection) throws SQLException {
        if (!isTraceConnectionEnable()) {
            chain.connection_rollback(connection);
            return;
        }

        long startMillis = System.currentTimeMillis();

        {
            TraceEvent event = new TraceEvent("ConnectionRollbackBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            fireEvent(event);
        }

        try {
            chain.connection_rollback(connection);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("ConnectionRollbackAfter", new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                fireEvent(event);
            }
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionRollbackError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionRollbackError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public void connection_rollback(FilterChain chain, ConnectionProxy connection, Savepoint savepoint)
                                                                                                       throws SQLException {
        if (!isTraceConnectionEnable()) {
            chain.connection_rollback(connection, savepoint);
            return;
        }

        long startMillis = System.currentTimeMillis();

        {
            TraceEvent event = new TraceEvent("ConnectionRollbackBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            if (savepoint != null) {
                event.putContext("savepointId", savepoint.getSavepointId());
                event.putContext("savepointName", savepoint.getSavepointName());
            }
            fireEvent(event);
        }

        try {
            chain.connection_rollback(connection, savepoint);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("ConnectionRollbackAfter", new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                if (savepoint != null) {
                    event.putContext("savepointId", savepoint.getSavepointId());
                    event.putContext("savepointName", savepoint.getSavepointName());
                }
                fireEvent(event);
            }
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionRollbackError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                if (savepoint != null) {
                    event.putContext("savepointId", savepoint.getSavepointId());
                    event.putContext("savepointName", savepoint.getSavepointName());
                }
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionRollbackError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                if (savepoint != null) {
                    event.putContext("savepointId", savepoint.getSavepointId());
                    event.putContext("savepointName", savepoint.getSavepointName());
                }
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection, String sql)
                                                                                                                        throws SQLException {
        if (!isTraceConnectionEnable()) {
            return chain.connection_prepareStatement(connection, sql);
        }

        long startMillis = System.currentTimeMillis();

        {
            TraceEvent event = new TraceEvent("ConnectionPrepareStatementBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_SQL, sql);
            fireEvent(event);
        }

        try {
            PreparedStatementProxy statement = chain.connection_prepareStatement(connection, sql);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("ConnectionPrepareStatementAfter", new Date(startMillis),
                                                            timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                fireEvent(event);
            }
            return statement;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionPrepareStatementError", new Date(startMillis),
                                                            ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionPrepareStatementError", new Date(startMillis),
                                                            ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int autoGeneratedKeys) throws SQLException {
        if (!isTraceConnectionEnable()) {
            return chain.connection_prepareStatement(connection, sql, autoGeneratedKeys);
        }

        long startMillis = System.currentTimeMillis();

        {
            TraceEvent event = new TraceEvent("ConnectionPrepareStatementBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_SQL, sql);
            event.putContext("stmt.autoGeneratedKeys", autoGeneratedKeys);
            fireEvent(event);
        }

        try {
            PreparedStatementProxy statement = chain.connection_prepareStatement(connection, sql, autoGeneratedKeys);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("ConnectionPrepareStatementAfter", new Date(startMillis),
                                                            timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext("stmt.autoGeneratedKeys", autoGeneratedKeys);
                fireEvent(event);
            }

            return statement;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionPrepareStatementError", new Date(startMillis),
                                                            ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext("stmt.autoGeneratedKeys", autoGeneratedKeys);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionPrepareStatementError", new Date(startMillis),
                                                            ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext("stmt.autoGeneratedKeys", autoGeneratedKeys);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int resultSetType, int resultSetConcurrency)
                                                                                                                      throws SQLException {
        if (!isTraceConnectionEnable()) {
            return chain.connection_prepareStatement(connection, sql, resultSetType, resultSetConcurrency);
        }

        long startMillis = System.currentTimeMillis();

        {
            TraceEvent event = new TraceEvent("ConnectionPrepareStatementBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_SQL, sql);
            event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
            event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
            fireEvent(event);
        }

        try {
            PreparedStatementProxy statement = chain.connection_prepareStatement(connection, sql, resultSetType,
                                                                                 resultSetConcurrency);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("ConnectionPrepareStatementAfter", new Date(startMillis),
                                                            timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
                event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
                fireEvent(event);
            }

            return statement;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionPrepareStatementError", new Date(startMillis),
                                                            ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
                event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionPrepareStatementError", new Date(startMillis),
                                                            ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
                event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int resultSetType, int resultSetConcurrency,
                                                              int resultSetHoldability) throws SQLException {
        if (!isTraceConnectionEnable()) {
            return chain.connection_prepareStatement(connection, sql, resultSetType, resultSetConcurrency,
                                                     resultSetHoldability);
        }

        long startMillis = System.currentTimeMillis();

        {
            TraceEvent event = new TraceEvent("ConnectionPrepareStatementBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_SQL, sql);
            event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
            event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
            event.putContext(TRACE_STMT_RS_HOLDABILITY, resultSetHoldability);
            fireEvent(event);
        }

        try {
            PreparedStatementProxy statement = chain.connection_prepareStatement(connection, sql, resultSetType,
                                                                                 resultSetConcurrency,
                                                                                 resultSetHoldability);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("ConnectionPrepareStatementAfter", new Date(startMillis),
                                                            timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
                event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
                event.putContext(TRACE_STMT_RS_HOLDABILITY, resultSetHoldability);
                fireEvent(event);
            }

            return statement;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionPrepareStatementError", new Date(startMillis),
                                                            ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
                event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
                event.putContext(TRACE_STMT_RS_HOLDABILITY, resultSetHoldability);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionPrepareStatementError", new Date(startMillis),
                                                            ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
                event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
                event.putContext(TRACE_STMT_RS_HOLDABILITY, resultSetHoldability);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, int[] columnIndexes) throws SQLException {
        if (!isTraceConnectionEnable()) {
            return chain.connection_prepareStatement(connection, sql, columnIndexes);
        }

        long startMillis = System.currentTimeMillis();

        {
            TraceEvent event = new TraceEvent("ConnectionPrepareStatementBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_SQL, sql);
            event.putContext(TRACE_STMT_COLUMN_INDEXES, columnIndexes);
            fireEvent(event);
        }

        try {
            PreparedStatementProxy statement = chain.connection_prepareStatement(connection, sql, columnIndexes);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("ConnectionPrepareStatementAfter", new Date(startMillis),
                                                            timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_COLUMN_INDEXES, columnIndexes);
                fireEvent(event);
            }

            return statement;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionPrepareStatementError", new Date(startMillis),
                                                            ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_COLUMN_INDEXES, columnIndexes);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionPrepareStatementError", new Date(startMillis),
                                                            ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_COLUMN_INDEXES, columnIndexes);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public PreparedStatementProxy connection_prepareStatement(FilterChain chain, ConnectionProxy connection,
                                                              String sql, String[] columnNames) throws SQLException {
        if (!isTraceConnectionEnable()) {
            return chain.connection_prepareStatement(connection, sql, columnNames);
        }

        long startMillis = System.currentTimeMillis();

        {
            TraceEvent event = new TraceEvent("ConnectionPrepareStatementBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_SQL, sql);
            event.putContext(TRACE_STMT_COLUMN, columnNames);
            fireEvent(event);
        }

        try {
            PreparedStatementProxy statement = chain.connection_prepareStatement(connection, sql, columnNames);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("ConnectionPrepareStatementAfter", new Date(startMillis),
                                                            timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_COLUMN, columnNames);
                fireEvent(event);
            }

            return statement;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionPrepareStatementError", new Date(startMillis),
                                                            ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_COLUMN, columnNames);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionPrepareStatementError", new Date(startMillis),
                                                            ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_COLUMN, columnNames);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql)
                                                                                                                   throws SQLException {
        if (!isTraceConnectionEnable()) {
            return chain.connection_prepareCall(connection, sql);
        }

        long startMillis = System.currentTimeMillis();

        {
            TraceEvent event = new TraceEvent("ConnectionPrecallBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_SQL, sql);
            fireEvent(event);
        }

        try {
            CallableStatementProxy statement = chain.connection_prepareCall(connection, sql);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("ConnectionPrecallAfter", new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                fireEvent(event);
            }

            return statement;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionPrecallError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionPrecallError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql,
                                                         int resultSetType, int resultSetConcurrency)
                                                                                                     throws SQLException {
        if (!isTraceConnectionEnable()) {
            return chain.connection_prepareCall(connection, sql, resultSetType, resultSetConcurrency);
        }

        long startMillis = System.currentTimeMillis();

        {
            TraceEvent event = new TraceEvent("ConnectionPrecallBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_SQL, sql);
            event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
            event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
            fireEvent(event);
        }

        try {
            CallableStatementProxy statement = chain.connection_prepareCall(connection, sql, resultSetType,
                                                                            resultSetConcurrency);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("ConnectionPrecallAfter", new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
                event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
                fireEvent(event);
            }

            return statement;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionPrecallError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
                event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionPrecallError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
                event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public CallableStatementProxy connection_prepareCall(FilterChain chain, ConnectionProxy connection, String sql,
                                                         int resultSetType, int resultSetConcurrency,
                                                         int resultSetHoldability) throws SQLException {
        if (!isTraceConnectionEnable()) {
            return chain.connection_prepareCall(connection, sql, resultSetType, resultSetConcurrency,
                                                resultSetHoldability);
        }

        long startMillis = System.currentTimeMillis();

        {
            TraceEvent event = new TraceEvent("ConnectionPrecallBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_SQL, sql);
            event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
            event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
            event.putContext(TRACE_STMT_RS_HOLDABILITY, resultSetHoldability);
            fireEvent(event);
        }

        try {
            CallableStatementProxy statement = chain.connection_prepareCall(connection, sql, resultSetType,
                                                                            resultSetConcurrency, resultSetHoldability);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("ConnectionPrecallAfter", new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
                event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
                event.putContext(TRACE_STMT_RS_HOLDABILITY, resultSetHoldability);
                fireEvent(event);
            }

            return statement;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionPrecallError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
                event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
                event.putContext(TRACE_STMT_RS_HOLDABILITY, resultSetHoldability);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionPrecallError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
                event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
                event.putContext(TRACE_STMT_RS_HOLDABILITY, resultSetHoldability);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public StatementProxy connection_createStatement(FilterChain chain, ConnectionProxy connection) throws SQLException {
        if (!isTraceConnectionEnable()) {
            return chain.connection_createStatement(connection);
        }

        long startMillis = System.currentTimeMillis();

        {
            TraceEvent event = new TraceEvent("ConnectionCreateStatementBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            fireEvent(event);
        }

        try {
            StatementProxy statement = chain.connection_createStatement(connection);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("ConnectionCreateStatementAfter", new Date(startMillis),
                                                            timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                fireEvent(event);
            }

            return statement;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionCreateStatementError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionCreateStatementError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public StatementProxy connection_createStatement(FilterChain chain, ConnectionProxy connection, int resultSetType,
                                                     int resultSetConcurrency) throws SQLException {
        if (!isTraceConnectionEnable()) {
            return chain.connection_createStatement(connection, resultSetType, resultSetConcurrency);
        }

        long startMillis = System.currentTimeMillis();

        {
            TraceEvent event = new TraceEvent("ConnectionCreateStatementBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
            event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
            fireEvent(event);
        }

        try {
            StatementProxy statement = chain.connection_createStatement(connection, resultSetType, resultSetType);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("ConnectionCreateStatementAfter", new Date(startMillis),
                                                            timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
                event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
                fireEvent(event);
            }

            return statement;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionCreateStatementError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
                event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionCreateStatementError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
                event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public StatementProxy connection_createStatement(FilterChain chain, ConnectionProxy connection, int resultSetType,
                                                     int resultSetConcurrency, int resultSetHoldability)
                                                                                                        throws SQLException {
        if (!isTraceConnectionEnable()) {
            return chain.connection_createStatement(connection, resultSetType, resultSetConcurrency,
                                                    resultSetHoldability);
        }

        long startMillis = System.currentTimeMillis();

        {
            TraceEvent event = new TraceEvent("ConnectionCreateStatementBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
            event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
            event.putContext(TRACE_STMT_RS_HOLDABILITY, resultSetHoldability);

            fireEvent(event);
        }

        try {
            StatementProxy statement = chain.connection_createStatement(connection, resultSetType, resultSetType,
                                                                        resultSetHoldability);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("ConnectionCreateStatementAfter", new Date(startMillis),
                                                            timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
                event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
                event.putContext(TRACE_STMT_RS_HOLDABILITY, resultSetHoldability);
                fireEvent(event);
            }

            return statement;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionCreateStatementError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
                event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
                event.putContext(TRACE_STMT_RS_HOLDABILITY, resultSetHoldability);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("ConnectionCreateStatementError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_RS_TYPE, resultSetType);
                event.putContext(TRACE_STMT_RS_CONCURRENCY, resultSetConcurrency);
                event.putContext(TRACE_STMT_RS_HOLDABILITY, resultSetHoldability);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        if (!isTraceStatementEnable()) {
            return chain.statement_execute(statement, sql);
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("StatementExecuteBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_ID, statement.getId());
            event.putContext(TRACE_STMT_SQL, sql);

            fireEvent(event);
        }

        try {
            boolean result = chain.statement_execute(statement, sql);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("StatementExecuteAfter", new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                fireEvent(event);
            }

            return result;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, int autoGeneratedKeys)
                                                                                                                    throws SQLException {
        if (!isTraceStatementEnable()) {
            return chain.statement_execute(statement, sql, autoGeneratedKeys);
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("StatementExecuteBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_SQL, sql);
            event.putContext(TRACE_STMT_ID, statement.getId());
            event.putContext("stmt.autoGeneratedKeys", autoGeneratedKeys);

            fireEvent(event);
        }

        try {
            boolean result = chain.statement_execute(statement, sql, autoGeneratedKeys);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("StatementExecuteAfter", new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext("stmt.autoGeneratedKeys", autoGeneratedKeys);
                fireEvent(event);
            }

            return result;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext("stmt.autoGeneratedKeys", autoGeneratedKeys);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext("stmt.autoGeneratedKeys", autoGeneratedKeys);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, int columnIndexes[])
                                                                                                                  throws SQLException {
        if (!isTraceStatementEnable()) {
            return chain.statement_execute(statement, sql, columnIndexes);
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("StatementExecuteBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_ID, statement.getId());
            event.putContext(TRACE_STMT_SQL, sql);
            event.putContext(TRACE_STMT_COLUMN_INDEXES, columnIndexes);

            fireEvent(event);
        }

        try {
            boolean result = chain.statement_execute(statement, sql, columnIndexes);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("StatementExecuteAfter", new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_COLUMN_INDEXES, columnIndexes);
                fireEvent(event);
            }

            return result;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_COLUMN_INDEXES, columnIndexes);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_COLUMN_INDEXES, columnIndexes);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public boolean statement_execute(FilterChain chain, StatementProxy statement, String sql, String columnNames[])
                                                                                                                   throws SQLException {
        if (!isTraceStatementEnable()) {
            return chain.statement_execute(statement, sql, columnNames);
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("StatementExecuteBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_ID, statement.getId());
            event.putContext(TRACE_STMT_SQL, sql);
            event.putContext(TRACE_STMT_COLUMN_INDEXES, columnNames);

            fireEvent(event);
        }

        try {
            boolean result = chain.statement_execute(statement, sql, columnNames);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("StatementExecuteAfter", new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_COLUMN, columnNames);
                fireEvent(event);
            }

            return result;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_COLUMN_INDEXES, columnNames);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_COLUMN_INDEXES, columnNames);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public int[] statement_executeBatch(FilterChain chain, StatementProxy statement) throws SQLException {
        if (!isTraceStatementEnable()) {
            return chain.statement_executeBatch(statement);
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("StatementExecuteBatchBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_ID, statement.getId());

            fireEvent(event);
        }

        try {
            int[] updateCounts = chain.statement_executeBatch(statement);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("StatementExecuteBatchAfter", new Date(startMillis),
                                                            timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext("stmt.updateCounts", updateCounts);
                fireEvent(event);
            }

            return updateCounts;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteBatchError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteBatchError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public ResultSetProxy statement_executeQuery(FilterChain chain, StatementProxy statement, String sql)
                                                                                                         throws SQLException {
        if (!isTraceStatementEnable()) {
            return chain.statement_executeQuery(statement, sql);
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("StatementExecuteQueryBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_ID, statement.getId());
            event.putContext(TRACE_STMT_SQL, sql);

            fireEvent(event);
        }

        try {
            ResultSetProxy resultSet = chain.statement_executeQuery(statement, sql);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("StatementExecuteQueryAfter", new Date(startMillis),
                                                            timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_RS_ID, resultSet.getId());
                fireEvent(event);
            }

            resultSetOpenAfter(resultSet);

            return resultSet;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteQueryError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteQueryError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        if (!isTraceStatementEnable()) {
            return chain.statement_executeUpdate(statement, sql);
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("StatementExecuteUpdateBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_ID, statement.getId());
            event.putContext(TRACE_STMT_SQL, sql);

            fireEvent(event);
        }

        try {
            int updateCount = chain.statement_executeUpdate(statement, sql);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("StatementExecuteUpdateAfter", new Date(startMillis),
                                                            timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_UPDATE_COUNT, updateCount);
                fireEvent(event);
            }

            return updateCount;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteUpdateError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteUpdateError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, int autoGeneratedKeys)
                                                                                                                      throws SQLException {
        if (!isTraceStatementEnable()) {
            return chain.statement_executeUpdate(statement, sql, autoGeneratedKeys);
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("StatementExecuteUpdateBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_ID, statement.getId());
            event.putContext(TRACE_STMT_SQL, sql);
            event.putContext("stmt.autoGeneratedKeys", autoGeneratedKeys);

            fireEvent(event);
        }

        try {
            int updateCount = chain.statement_executeUpdate(statement, sql, autoGeneratedKeys);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("StatementExecuteUpdateAfter", new Date(startMillis),
                                                            timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext("stmt.autoGeneratedKeys", autoGeneratedKeys);
                event.putContext(TRACE_STMT_UPDATE_COUNT, updateCount);
                fireEvent(event);
            }

            return updateCount;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteUpdateError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext("stmt.autoGeneratedKeys", autoGeneratedKeys);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteUpdateError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext("stmt.autoGeneratedKeys", autoGeneratedKeys);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, int columnIndexes[])
                                                                                                                    throws SQLException {
        if (!isTraceStatementEnable()) {
            return chain.statement_executeUpdate(statement, sql, columnIndexes);
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("StatementExecuteUpdateBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_ID, statement.getId());
            event.putContext(TRACE_STMT_SQL, sql);
            event.putContext(TRACE_STMT_COLUMN_INDEXES, columnIndexes);

            fireEvent(event);
        }

        try {
            int updateCount = chain.statement_executeUpdate(statement, sql, columnIndexes);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("StatementExecuteUpdateAfter", new Date(startMillis),
                                                            timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_COLUMN_INDEXES, columnIndexes);
                event.putContext(TRACE_STMT_UPDATE_COUNT, updateCount);
                fireEvent(event);
            }

            return updateCount;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteUpdateError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_COLUMN_INDEXES, columnIndexes);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteUpdateError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_COLUMN_INDEXES, columnIndexes);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public int statement_executeUpdate(FilterChain chain, StatementProxy statement, String sql, String columnNames[])
                                                                                                                     throws SQLException {
        if (!isTraceStatementEnable()) {
            return chain.statement_executeUpdate(statement, sql, columnNames);
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("StatementExecuteUpdateBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_ID, statement.getId());
            event.putContext(TRACE_STMT_SQL, sql);
            event.putContext(TRACE_STMT_COLUMN, columnNames);

            fireEvent(event);
        }

        try {
            int updateCount = chain.statement_executeUpdate(statement, sql, columnNames);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("StatementExecuteUpdateAfter", new Date(startMillis),
                                                            timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_COLUMN, columnNames);
                event.putContext(TRACE_STMT_UPDATE_COUNT, updateCount);
                fireEvent(event);
            }

            return updateCount;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteUpdateError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_COLUMN, columnNames);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementExecuteUpdateError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                event.putContext(TRACE_STMT_COLUMN, columnNames);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public void statement_addBatch(FilterChain chain, StatementProxy statement, String sql) throws SQLException {
        if (!isTraceStatementEnable()) {
            chain.statement_addBatch(statement, sql);
            return;
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("StatementAddBatchBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_ID, statement.getId());
            event.putContext(TRACE_STMT_SQL, sql);

            fireEvent(event);
        }

        try {
            chain.statement_addBatch(statement, sql);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("StatementAddBatchAfter", new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                fireEvent(event);
            }
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementAddBatchError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementAddBatchError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, sql);
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public void statement_cancel(FilterChain chain, StatementProxy statement) throws SQLException {
        if (!isTraceStatementEnable()) {
            chain.statement_cancel(statement);
            return;
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("StatementCancelBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_ID, statement.getId());

            fireEvent(event);
        }

        try {
            chain.statement_cancel(statement);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("StatementCancelAfter", new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                fireEvent(event);
            }
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementCancelError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementCancelError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public void statement_clearBatch(FilterChain chain, StatementProxy statement) throws SQLException {
        if (!isTraceStatementEnable()) {
            chain.statement_clearBatch(statement);
            return;
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("StatementClearBatchBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_ID, statement.getId());

            fireEvent(event);
        }

        try {
            chain.statement_clearBatch(statement);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("StatementClearBatchAfter", new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                fireEvent(event);
            }
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementClearBatchError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementClearBatchError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public void statement_close(FilterChain chain, StatementProxy statement) throws SQLException {
        if (!isTraceStatementEnable()) {
            chain.statement_close(statement);
            return;
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("StatementCloseBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_ID, statement.getId());

            fireEvent(event);
        }

        try {
            chain.statement_close(statement);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("StatementCloseAfter", new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                fireEvent(event);
            }
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementCloseError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("StatementCloseError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public ResultSetProxy statement_getGeneratedKeys(FilterChain chain, StatementProxy statement) throws SQLException {
        ResultSetProxy resultSet = super.statement_getGeneratedKeys(chain, statement);

        if (resultSet != null) {
            resultSetOpenAfter(resultSet);
        }

        return resultSet;
    }

    @Override
    public ResultSetProxy statement_getResultSet(FilterChain chain, StatementProxy statement) throws SQLException {
        ResultSetProxy resultSet = super.statement_getResultSet(chain, statement);

        if (resultSet != null) {
            resultSetOpenAfter(resultSet);
        }

        return resultSet;
    }

    @SuppressWarnings("unchecked")
    protected SortedMap<Integer, Object> getParameters(PreparedStatementProxy statement) throws SQLException {
        SortedMap<Integer, Object> parameters = (SortedMap<Integer, Object>) statement.getAttributes().get("trace.stmt.params");

        if (parameters == null) {
            parameters = new TreeMap<Integer, Object>();
            statement.getAttributes().put("trace.stmt.params", parameters);
        }

        return parameters;
    }

    @Override
    public boolean preparedStatement_execute(FilterChain chain, PreparedStatementProxy statement) throws SQLException {
        if (!isTraceStatementEnable()) {
            return chain.preparedStatement_execute(statement);
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("PreparedStatementExecuteBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_ID, statement.getId());
            event.putContext(TRACE_STMT_SQL, statement.getSql());
            event.putContext(TRACE_STMT_PARAMS, getParameters(statement));

            fireEvent(event);
        }

        try {
            boolean firstResultSet = chain.preparedStatement_execute(statement);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("PreparedStatementExecuteAfter", new Date(startMillis),
                                                            timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, statement.getSql());
                event.putContext(TRACE_STMT_PARAMS, getParameters(statement));
                event.putContext("stmt.firstResultSet", firstResultSet);
                fireEvent(event);
            }

            return firstResultSet;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("PreparedStatementExecuteError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, statement.getSql());
                event.putContext(TRACE_STMT_PARAMS, getParameters(statement));
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("PreparedStatementExecuteError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, statement.getSql());
                event.putContext(TRACE_STMT_PARAMS, getParameters(statement));
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public ResultSetProxy preparedStatement_executeQuery(FilterChain chain, PreparedStatementProxy statement)
                                                                                                             throws SQLException {
        if (!isTraceStatementEnable()) {
            return chain.preparedStatement_executeQuery(statement);
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("PreparedStatementExecuteQueryBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_ID, statement.getId());
            event.putContext(TRACE_STMT_SQL, statement.getSql());
            event.putContext(TRACE_STMT_PARAMS, getParameters(statement));

            fireEvent(event);
        }

        try {
            ResultSetProxy resultSet = chain.preparedStatement_executeQuery(statement);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("PreparedStatementExecuteQueryAfter",
                                                            new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, statement.getSql());
                event.putContext(TRACE_STMT_PARAMS, getParameters(statement));
                event.putContext(TRACE_RS_ID, resultSet.getId());
                fireEvent(event);
            }

            resultSetOpenAfter(resultSet);

            return resultSet;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("PreparedStatementExecuteQueryError",
                                                            new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, statement.getSql());
                event.putContext(TRACE_STMT_PARAMS, getParameters(statement));
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("PreparedStatementExecuteQueryError",
                                                            new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, statement.getSql());
                event.putContext(TRACE_STMT_PARAMS, getParameters(statement));
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public int preparedStatement_executeUpdate(FilterChain chain, PreparedStatementProxy statement) throws SQLException {
        if (!isTraceStatementEnable()) {
            return chain.preparedStatement_executeUpdate(statement);
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("PreparedStatementExecuteUpdateBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_ID, statement.getId());
            event.putContext(TRACE_STMT_SQL, statement.getSql());
            event.putContext(TRACE_STMT_PARAMS, getParameters(statement));

            fireEvent(event);
        }

        try {
            int updateCount = chain.preparedStatement_executeUpdate(statement);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("PreparedStatementExecuteQueryAfter",
                                                            new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, statement.getSql());
                event.putContext(TRACE_STMT_PARAMS, getParameters(statement));
                event.putContext(TRACE_STMT_UPDATE_COUNT, updateCount);
                fireEvent(event);
            }

            return updateCount;
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("PreparedStatementExecuteUpdateError",
                                                            new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, statement.getSql());
                event.putContext(TRACE_STMT_PARAMS, getParameters(statement));
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("PreparedStatementExecuteUpdateError",
                                                            new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, statement.getSql());
                event.putContext(TRACE_STMT_PARAMS, getParameters(statement));
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public void preparedStatement_clearParameters(FilterChain chain, PreparedStatementProxy statement)
                                                                                                      throws SQLException {
        if (!isTraceStatementEnable()) {
            chain.preparedStatement_clearParameters(statement);
            return;
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("PreparedStatementClearParametersBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_ID, statement.getId());
            event.putContext(TRACE_STMT_PARAMS, getParameters(statement));
            event.putContext(TRACE_STMT_SQL, statement.getSql());

            fireEvent(event);
        }

        try {
            chain.preparedStatement_clearParameters(statement);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("PreparedStatementClearParametersAfter",
                                                            new Date(startMillis), timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, statement.getSql());
                event.putContext(TRACE_STMT_PARAMS, getParameters(statement));
                fireEvent(event);
            }
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("PreparedStatementClearParametersError",
                                                            new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, statement.getSql());
                event.putContext(TRACE_STMT_PARAMS, getParameters(statement));
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("PreparedStatementClearParametersError",
                                                            new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, statement.getSql());
                event.putContext(TRACE_STMT_PARAMS, getParameters(statement));
                fireEvent(event);
            }
            throw ex;
        }
    }

    @Override
    public void preparedStatement_addBatch(FilterChain chain, PreparedStatementProxy statement) throws SQLException {
        if (!isTraceStatementEnable()) {
            chain.preparedStatement_addBatch(statement);
            return;
        }

        long startMillis = System.currentTimeMillis();

        ConnectionProxy connection = statement.getConnectionProxy();
        {
            TraceEvent event = new TraceEvent("PreparedStatementAddBatchBefore", new Date(startMillis));
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_ID, statement.getId());
            event.putContext(TRACE_STMT_SQL, statement.getSql());
            event.putContext(TRACE_STMT_PARAMS, getParameters(statement));

            fireEvent(event);
        }

        try {
            chain.preparedStatement_addBatch(statement);

            long timespan = System.currentTimeMillis() - startMillis;
            {
                TraceAfterEvent event = new TraceAfterEvent("PreparedStatementAddBatchAfter", new Date(startMillis),
                                                            timespan);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, statement.getSql());
                event.putContext(TRACE_STMT_PARAMS, getParameters(statement));
                fireEvent(event);
            }
        } catch (SQLException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("PreparedStatementAddBatchError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, statement.getSql());
                event.putContext(TRACE_STMT_PARAMS, getParameters(statement));
                fireEvent(event);
            }
            throw ex;
        } catch (RuntimeException ex) {
            {
                TraceErrorEvent event = new TraceErrorEvent("PreparedStatementAddBatchError", new Date(startMillis), ex);
                event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
                event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
                event.putContext(TRACE_CONN_ID, connection.getId());
                event.putContext(TRACE_STMT_ID, statement.getId());
                event.putContext(TRACE_STMT_SQL, statement.getSql());
                event.putContext(TRACE_STMT_PARAMS, getParameters(statement));
                fireEvent(event);
            }
            throw ex;
        }
    }

    // //////////////////

    public void fireEvent(TraceEvent event) {
        JdbcTraceManager.getInstance().fireTraceEvent(event);
    }

    // //////////////////////////////////////////////
    @Override
    public void preparedStatement_setArray(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           Array x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setArray(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setArray(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setAsciiStream(FilterChain chain, PreparedStatementProxy statement,
                                                 int parameterIndex, java.io.InputStream x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setAsciiStream(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "InputStream");

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setAsciiStream(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setAsciiStream(FilterChain chain, PreparedStatementProxy statement,
                                                 int parameterIndex, java.io.InputStream x, int length)
                                                                                                       throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setAsciiStream(chain, statement, parameterIndex, x, length);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "InputStream");
        param.put("length", length);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setAsciiStream(statement, parameterIndex, x, length);
    }

    @Override
    public void preparedStatement_setAsciiStream(FilterChain chain, PreparedStatementProxy statement,
                                                 int parameterIndex, java.io.InputStream x, long length)
                                                                                                        throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setAsciiStream(chain, statement, parameterIndex, x, length);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "InputStream");
        param.put("length", length);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setAsciiStream(statement, parameterIndex, x, length);
    }

    @Override
    public void preparedStatement_setBigDecimal(FilterChain chain, PreparedStatementProxy statement,
                                                int parameterIndex, BigDecimal x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setBigDecimal(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setBigDecimal(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setBinaryStream(FilterChain chain, PreparedStatementProxy statement,
                                                  int parameterIndex, java.io.InputStream x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setBinaryStream(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<InputStream>");

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setBinaryStream(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setBinaryStream(FilterChain chain, PreparedStatementProxy statement,
                                                  int parameterIndex, java.io.InputStream x, int length)
                                                                                                        throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setBinaryStream(chain, statement, parameterIndex, x, length);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<InputStream>");
        param.put("length", length);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setBinaryStream(statement, parameterIndex, x, length);
    }

    @Override
    public void preparedStatement_setBinaryStream(FilterChain chain, PreparedStatementProxy statement,
                                                  int parameterIndex, java.io.InputStream x, long length)
                                                                                                         throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setBinaryStream(chain, statement, parameterIndex, x, length);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<InputStream>");
        param.put("length", length);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setBinaryStream(statement, parameterIndex, x, length);
    }

    @Override
    public void preparedStatement_setBlob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          Blob x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setBlob(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<Blob>");

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setBlob(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setBlob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          InputStream inputStream) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setBlob(chain, statement, parameterIndex, inputStream);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<InputStream>");

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setBlob(statement, parameterIndex, inputStream);
    }

    @Override
    public void preparedStatement_setBlob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          InputStream inputStream, long length) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setBlob(chain, statement, parameterIndex, inputStream, length);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<InputStream>");
        param.put("length", length);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setBlob(statement, parameterIndex, inputStream, length);
    }

    @Override
    public void preparedStatement_setBoolean(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                             boolean x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setBoolean(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setBoolean(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setByte(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          byte x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setByte(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setByte(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setBytes(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           byte x[]) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setBytes(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setBytes(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setCharacterStream(FilterChain chain, PreparedStatementProxy statement,
                                                     int parameterIndex, java.io.Reader reader) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setCharacterStream(chain, statement, parameterIndex, reader);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<Reader>");

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setCharacterStream(statement, parameterIndex, reader);
    }

    @Override
    public void preparedStatement_setCharacterStream(FilterChain chain, PreparedStatementProxy statement,
                                                     int parameterIndex, java.io.Reader reader, int length)
                                                                                                           throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setCharacterStream(chain, statement, parameterIndex, reader, length);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<Reader>");
        param.put("length", length);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setCharacterStream(statement, parameterIndex, reader, length);
    }

    @Override
    public void preparedStatement_setCharacterStream(FilterChain chain, PreparedStatementProxy statement,
                                                     int parameterIndex, java.io.Reader reader, long length)
                                                                                                            throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setCharacterStream(chain, statement, parameterIndex, reader, length);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<Reader>");
        param.put("length", length);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setCharacterStream(statement, parameterIndex, reader, length);
    }

    @Override
    public void preparedStatement_setClob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          Clob x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setClob(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<Clob>");

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setClob(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setClob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          Reader reader) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setClob(chain, statement, parameterIndex, reader);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<Reader>");

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setClob(statement, parameterIndex, reader);
    }

    @Override
    public void preparedStatement_setClob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          Reader reader, long length) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setClob(chain, statement, parameterIndex, reader, length);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<Clob>");
        param.put("length", length);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setClob(statement, parameterIndex, reader, length);
    }

    @Override
    public void preparedStatement_setDate(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          java.sql.Date x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setDate(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setDate(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setDate(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          java.sql.Date x, Calendar calendar) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setDate(chain, statement, parameterIndex, x, calendar);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);
        param.put("calendar", calendar);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setDate(statement, parameterIndex, x, calendar);
    }

    @Override
    public void preparedStatement_setDouble(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                            double x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setDouble(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setDouble(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setFloat(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           float x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setFloat(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setFloat(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setInt(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, int x)
                                                                                                                        throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setInt(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setInt(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setLong(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          long x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setLong(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setLong(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setNCharacterStream(FilterChain chain, PreparedStatementProxy statement,
                                                      int parameterIndex, Reader value) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setNCharacterStream(chain, statement, parameterIndex, value);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<Clob>");

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setNCharacterStream(statement, parameterIndex, value);
    }

    @Override
    public void preparedStatement_setNCharacterStream(FilterChain chain, PreparedStatementProxy statement,
                                                      int parameterIndex, Reader value, long length)
                                                                                                    throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setNCharacterStream(chain, statement, parameterIndex, value, length);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<Clob>");
        param.put("length", length);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setNCharacterStream(statement, parameterIndex, value, length);
    }

    @Override
    public void preparedStatement_setNClob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           NClob value) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setNClob(chain, statement, parameterIndex, value);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<Clob>");

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setNClob(statement, parameterIndex, value);
    }

    @Override
    public void preparedStatement_setNClob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           Reader reader) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setNClob(chain, statement, parameterIndex, reader);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<Clob>");

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setNClob(statement, parameterIndex, reader);
    }

    @Override
    public void preparedStatement_setNClob(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           Reader reader, long length) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setNClob(chain, statement, parameterIndex, reader, length);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<Clob>");
        param.put("length", length);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setNClob(statement, parameterIndex, reader, length);
    }

    @Override
    public void preparedStatement_setNString(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                             String value) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setNString(chain, statement, parameterIndex, value);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", value);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setNString(statement, parameterIndex, value);
    }

    @Override
    public void preparedStatement_setNull(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          int sqlType) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setNull(chain, statement, parameterIndex, sqlType);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", null);
        param.put("sqlType", sqlType);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setNull(statement, parameterIndex, sqlType);
    }

    @Override
    public void preparedStatement_setNull(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          int sqlType, String typeName) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setNull(chain, statement, parameterIndex, sqlType, typeName);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", null);
        param.put("sqlType", sqlType);
        param.put("typeName", typeName);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setNull(statement, parameterIndex, sqlType, typeName);
    }

    @Override
    public void preparedStatement_setObject(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                            Object x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setObject(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        if (x instanceof InputStream) {
            param.put("value", "<InputStream>");
        } else if (x instanceof Reader) {
            param.put("value", "<Reader>");
        } else if (x instanceof Clob) {
            param.put("value", "<Clob>");
        } else if (x instanceof NClob) {
            param.put("value", "<NClob>");
        } else if (x instanceof Blob) {
            param.put("value", "<Blob>");
        } else {
            param.put("value", x);
        }

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setObject(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setObject(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                            Object x, int targetSqlType) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setObject(chain, statement, parameterIndex, x, targetSqlType);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        if (x instanceof InputStream) {
            param.put("value", "<InputStream>");
        } else if (x instanceof Reader) {
            param.put("value", "<Reader>");
        } else if (x instanceof Clob) {
            param.put("value", "<Clob>");
        } else if (x instanceof NClob) {
            param.put("value", "<NClob>");
        } else if (x instanceof Blob) {
            param.put("value", "<Blob>");
        } else {
            param.put("value", x);
        }
        param.put("targetSqlType", targetSqlType);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setObject(statement, parameterIndex, x, targetSqlType);
    }

    @Override
    public void preparedStatement_setObject(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                            Object x, int targetSqlType, int scaleOrLength) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setObject(chain, statement, parameterIndex, x, targetSqlType, scaleOrLength);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        if (x instanceof InputStream) {
            param.put("value", "<InputStream>");
        } else if (x instanceof Reader) {
            param.put("value", "<Reader>");
        } else if (x instanceof Clob) {
            param.put("value", "<Clob>");
        } else if (x instanceof NClob) {
            param.put("value", "<NClob>");
        } else if (x instanceof Blob) {
            param.put("value", "<Blob>");
        } else {
            param.put("value", x);
        }
        param.put("targetSqlType", targetSqlType);
        param.put("scaleOrLength", scaleOrLength);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setObject(statement, parameterIndex, x, targetSqlType, scaleOrLength);
    }

    @Override
    public void preparedStatement_setRef(FilterChain chain, PreparedStatementProxy statement, int parameterIndex, Ref x)
                                                                                                                        throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setRef(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setRef(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setRowId(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           RowId x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setRowId(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setRowId(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setShort(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                           short x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setShort(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setShort(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setSQLXML(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                            SQLXML xmlObject) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setSQLXML(chain, statement, parameterIndex, xmlObject);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<SQLXML>");

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setSQLXML(statement, parameterIndex, xmlObject);
    }

    @Override
    public void preparedStatement_setString(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                            String x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setString(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setString(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setTime(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          java.sql.Time x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setTime(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setTime(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setTime(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                          java.sql.Time x, Calendar calendar) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setTime(chain, statement, parameterIndex, x, calendar);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);
        param.put("calendar", calendar);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setTime(statement, parameterIndex, x, calendar);
    }

    @Override
    public void preparedStatement_setTimestamp(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                               java.sql.Timestamp x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setTimestamp(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setTimestamp(statement, parameterIndex, x);
    }

    @Override
    public void preparedStatement_setTimestamp(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                               java.sql.Timestamp x, Calendar calendar) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setTimestamp(chain, statement, parameterIndex, x, calendar);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);
        param.put("calendar", calendar);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setTimestamp(statement, parameterIndex, x, calendar);
    }

    @Override
    public void preparedStatement_setUnicodeStream(FilterChain chain, PreparedStatementProxy statement,
                                                   int parameterIndex, java.io.InputStream x, int length)
                                                                                                         throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setUnicodeStream(chain, statement, parameterIndex, x, length);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", "<InputStream>");
        param.put("length", length);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setUnicodeStream(statement, parameterIndex, x, length);
    }

    @Override
    public void preparedStatement_setURL(FilterChain chain, PreparedStatementProxy statement, int parameterIndex,
                                         java.net.URL x) throws SQLException {
        if (!isTraceStatementEnable()) {
            super.preparedStatement_setURL(chain, statement, parameterIndex, x);
            return;
        }

        SortedMap<Integer, Object> parameters = getParameters(statement);

        Map<String, Object> param = new HashMap<String, Object>();
        param.put("value", x);

        parameters.put(parameterIndex, param);

        chain.preparedStatement_setURL(statement, parameterIndex, x);
    }

    @Override
    public boolean resultSet_next(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        if (!isTraceResultSetEnable()) {
            return chain.resultSet_next(resultSet);
        }

        boolean hasMore = chain.resultSet_next(resultSet);

        StatementProxy statement = resultSet.getStatementProxy();
        ConnectionProxy connection = statement.getConnectionProxy();

        {
            TraceEvent event = new TraceEvent("ResultSetNext", new Date());
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_SQL, resultSet.getSql());
            event.putContext(TRACE_STMT_ID, statement.getId());
            event.putContext(TRACE_RS_ID, resultSet.getId());
            event.putContext("rs.hasMore", hasMore);

            if (hasMore) {
                List<Object> row = getCurrentRow(resultSet);
                event.putContext("rs.row", row);
                event.putContext(TRACE_RS_CURSOR_INDEX, resultSet.getCursorIndex());
            }

            fireEvent(event);
        }

        return hasMore;
    }

    @Override
    public boolean resultSet_previous(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        if (!isTraceResultSetEnable()) {
            return chain.resultSet_previous(resultSet);
        }

        boolean hasMore = chain.resultSet_previous(resultSet);

        StatementProxy statement = resultSet.getStatementProxy();
        ConnectionProxy connection = statement.getConnectionProxy();

        {
            TraceEvent event = new TraceEvent("ResultSetPrevious", new Date());
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_SQL, resultSet.getSql());
            event.putContext(TRACE_STMT_ID, statement.getId());
            event.putContext(TRACE_RS_ID, resultSet.getId());
            event.putContext("rs.hasMore", hasMore);

            if (hasMore) {
                event.putContext("rs.row", getCurrentRow(resultSet));
                event.putContext(TRACE_RS_CURSOR_INDEX, resultSet.getCursorIndex());
            }

            fireEvent(event);
        }

        return hasMore;
    }

    @Override
    public void resultSet_close(FilterChain chain, ResultSetProxy resultSet) throws SQLException {
        if (!isTraceResultSetEnable()) {
            chain.resultSet_close(resultSet);
            return;
        }

        chain.resultSet_close(resultSet);

        StatementProxy statement = resultSet.getStatementProxy();
        ConnectionProxy connection = statement.getConnectionProxy();

        {
            TraceEvent event = new TraceEvent("ResultSetCloseAfter", new Date());
            event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
            event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
            event.putContext(TRACE_CONN_ID, connection.getId());
            event.putContext(TRACE_STMT_SQL, resultSet.getSql());
            event.putContext(TRACE_STMT_ID, statement.getId());
            event.putContext(TRACE_RS_ID, resultSet.getId());

            event.putContext(TRACE_RS_CURSOR_INDEX, resultSet.getCursorIndex());
            event.putContext("rs.fetchRowIndex", resultSet.getCursorIndex());

            fireEvent(event);
        }
    }

    private List<Object> getCurrentRow(ResultSetProxy resultSet) throws SQLException {
        ResultSetMetaData meta = resultSet.getMetaData();
        int columnCount = meta.getColumnCount();

        List<Object> row = new ArrayList<Object>(columnCount);

        for (int i = 1; i <= columnCount; ++i) {
            Object value = resultSet.getObject(i);
            if (value instanceof Blob) {
                value = "<BLOB>";
            }
            row.add(value);
        }
        return row;
    }

    protected void resultSetOpenAfter(ResultSetProxy resultSet) throws SQLException {
        if (!isTraceResultSetEnable()) {
            return;
        }

        resultSet.setConstructNano();

        StatementProxy statement = resultSet.getStatementProxy();
        ConnectionProxy connection = statement.getConnectionProxy();

        TraceEvent event = new TraceEvent("ResultSetPrevious", new Date());
        event.putContext(TRACE_CONN_INFO, connection.getProperties().clone());
        event.putContext(TRACE_CONN_CONNECTED_TIME, connection.getConnectedTime());
        event.putContext(TRACE_CONN_ID, connection.getId());
        event.putContext(TRACE_STMT_SQL, resultSet.getSql());
        event.putContext(TRACE_STMT_ID, statement.getId());
        event.putContext(TRACE_RS_ID, resultSet.getId());

        List<Map<String, Object>> columns = new ArrayList<Map<String, Object>>();
        ResultSetMetaData meta = resultSet.getMetaData();
        int columnCount = meta.getColumnCount();
        for (int i = 1; i <= columnCount; ++i) {
            Map<String, Object> column = new HashMap<String, Object>();

            column.put("name", meta.getColumnName(i));
            column.put("type", meta.getColumnType(i));

            columns.add(column);
        }

        event.putContext("rs.columns", columns);

        fireEvent(event);
    }

}
