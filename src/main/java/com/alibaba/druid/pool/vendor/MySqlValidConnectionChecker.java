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
package com.alibaba.druid.pool.vendor;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.ValidConnectionChecker;
import com.alibaba.druid.pool.ValidConnectionCheckerAdapter;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.Utils;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class MySqlValidConnectionChecker extends ValidConnectionCheckerAdapter implements ValidConnectionChecker, Serializable {

    public static final int DEFAULT_VALIDATION_QUERY_TIMEOUT = 1;
    public static final String DEFAULT_VALIDATION_QUERY = "SELECT 1";

    private static final long serialVersionUID = 1L;
    private static final Log  LOG              = LogFactory.getLog(MySqlValidConnectionChecker.class);

    private PingOperation validateQueryPingOperation;
    private PingOperation nativePingOperation;
    private boolean  usePingMethod = true;

    public interface PingOperation {
        boolean ping(Connection conn, String validateQuery, int validationQueryTimeout) throws Exception;
    }

    public class ValidationQueryPingOperation implements PingOperation {
        @Override
        public boolean ping(Connection conn, String validateQuery, int validationQueryTimeout) throws Exception {
            String query = validateQuery;
            if (validateQuery == null || validateQuery.isEmpty()) {
                query = DEFAULT_VALIDATION_QUERY;
            }

            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt = conn.createStatement();
                if (validationQueryTimeout > 0) {
                    stmt.setQueryTimeout(validationQueryTimeout);
                }
                rs = stmt.executeQuery(query);

                return true;
            } finally {
                JdbcUtils.close(rs);
                JdbcUtils.close(stmt);
            }
        }
    }

    public class NativePingOperation implements PingOperation {

        public class Pair<L, R> {
            private final L left;
            private final R right;

            public Pair(L left, R right) {
                this.left = left;
                this.right = right;
            }

            public L getLeft() {
                return left;
            }

            public R getRight() {
                return right;
            }
        }

        private Pair<Class<?>, Method> defaultConnectionPingMethod = null;
        private Pair<Class<?>, Method> replicationConnectionPingMethod = null;
        private Pair<Class<?>, Method> loadBalancedConnectionPingMethod = null;

        public NativePingOperation() {
            // Try to load MySQLConnection
            try {
                Class<?> connectionClass = Utils.loadClass("com.mysql.jdbc.MySQLConnection");
                if (connectionClass == null) {
                    connectionClass = Utils.loadClass("com.mysql.cj.jdbc.ConnectionImpl");
                }

                if (connectionClass != null) {
                    Method ping = connectionClass.getMethod("pingInternal", boolean.class, int.class);
                    if (ping != null) {
                        defaultConnectionPingMethod = new Pair<Class<?>, Method>(connectionClass, ping);
                    }
                }
            } catch (Exception e) {
                LOG.warn("Cannot resolve com.mysql.jdbc.Connection.pingInternal method.  Will use 'SELECT 1' instead.", e);
            }

            // If current driver support replication connection, load it
            try {
                Class<?> connectionClass = Utils.loadClass("com.mysql.jdbc.ReplicationConnection");
                if (connectionClass != null) {
                    Method ping = connectionClass.getMethod("ping");
                    if (ping != null) {
                        replicationConnectionPingMethod = new Pair<Class<?>, Method>(connectionClass, ping);
                    }
                }
            }  catch (Exception e) {
                LOG.warn("Cannot resolve com.mysql.jdbc.ReplicationConnection.ping method.", e);
            }

            // If current driver support load-balanced connection, also load it
            try {
                Class<?> connectionClass = Utils.loadClass("com.mysql.jdbc.LoadBalancedConnection");
                if (connectionClass != null) {
                    Method ping = connectionClass.getMethod("ping");
                    if (ping != null) {
                        loadBalancedConnectionPingMethod = new Pair<Class<?>, Method>(connectionClass, ping);
                    }
                }
            }  catch (Exception e) {
                LOG.warn("Cannot resolve com.mysql.jdbc.LoadBalancedConnection.ping method.", e);
            }
        }

        @Override
        public boolean ping(Connection conn, String validateQuery, int validationQueryTimeout) throws Exception {
            try {
                if (replicationConnectionPingMethod != null && replicationConnectionPingMethod.getLeft().isAssignableFrom(conn.getClass())) {
                    replicationConnectionPingMethod.getRight().invoke(conn);
                } else if (loadBalancedConnectionPingMethod != null && loadBalancedConnectionPingMethod.getLeft().isAssignableFrom(conn.getClass())) {
                    loadBalancedConnectionPingMethod.getRight().invoke(conn);
                } else if (defaultConnectionPingMethod != null && defaultConnectionPingMethod.getLeft().isAssignableFrom(conn.getClass())) {
                    defaultConnectionPingMethod.getRight().invoke(conn, true, validationQueryTimeout * 1000);
                } else {
                    return false;
                }

                return true;
            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof SQLException) {
                    throw (SQLException) cause;
                }
                throw e;
            }
        }
    }

    public MySqlValidConnectionChecker(){
        validateQueryPingOperation = new ValidationQueryPingOperation();
        nativePingOperation = new NativePingOperation();

        configFromProperties(System.getProperties());
    }

    @Override
    public void configFromProperties(Properties properties) {
        String property = properties.getProperty("druid.mysql.usePingMethod");
        if ("true".equals(property)) {
            setUsePingMethod(true);
        } else if ("false".equals(property)) {
            setUsePingMethod(false);
        }
    }

    public boolean isUsePingMethod() {
        return usePingMethod;
    }

    public void setUsePingMethod(boolean usePingMethod) {
        this.usePingMethod = usePingMethod;
    }

    public boolean isValidConnection(Connection conn, String validateQuery, int validationQueryTimeout) throws Exception {
        if (conn.isClosed()) {
            return false;
        }

        if (validationQueryTimeout < 0) {
            validationQueryTimeout = DEFAULT_VALIDATION_QUERY_TIMEOUT;
        }

        if (usePingMethod) {
            if (conn instanceof DruidPooledConnection) {
                conn = ((DruidPooledConnection) conn).getConnection();
            }

            if (conn instanceof ConnectionProxy) {
                conn = ((ConnectionProxy) conn).getRawObject();
            }

            if (nativePingOperation.ping(conn, validateQuery, validationQueryTimeout)) {
                return true;
            }
        }

        return validateQueryPingOperation.ping(conn, validateQuery, validationQueryTimeout);
    }
}
