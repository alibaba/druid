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

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.ValidConnectionChecker;
import com.alibaba.druid.pool.ValidConnectionCheckerAdapter;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.Utils;

public class MySqlValidConnectionChecker extends ValidConnectionCheckerAdapter implements ValidConnectionChecker, Serializable {

    public static final int DEFAULT_VALIDATION_QUERY_TIMEOUT = 1;
    public static final String DEFAULT_VALIDATION_QUERY = "SELECT 1";

    private static final long serialVersionUID = 1L;
    private static final Log  LOG              = LogFactory.getLog(MySqlValidConnectionChecker.class);

    private Class<?> clazz;
    private Method   ping;
    private boolean  usePingMethod = false;

    public MySqlValidConnectionChecker(){
        try {
            clazz = Utils.loadClass("com.mysql.jdbc.MySQLConnection");
            if (clazz == null) {
                clazz = Utils.loadClass("com.mysql.cj.jdbc.ConnectionImpl");
            }

            if (clazz != null) {
                ping = clazz.getMethod("pingInternal", boolean.class, int.class);
            }

            if (ping != null) {
                usePingMethod = true;
            }
        } catch (Exception e) {
            LOG.warn("Cannot resolve com.mysql.jdbc.Connection.ping method.  Will use 'SELECT 1' instead.", e);
        }

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

        if (usePingMethod) {
            if (conn instanceof DruidPooledConnection) {
                conn = ((DruidPooledConnection) conn).getConnection();
            }

            if (conn instanceof ConnectionProxy) {
                conn = ((ConnectionProxy) conn).getRawObject();
            }

            if (clazz.isAssignableFrom(conn.getClass())) {
                if (validationQueryTimeout < 0) {
                    validationQueryTimeout = DEFAULT_VALIDATION_QUERY_TIMEOUT;
                }

                try {
                    ping.invoke(conn, true, validationQueryTimeout * 1000);
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof SQLException) {
                        throw (SQLException) cause;
                    }
                    throw e;
                }
                return true;
            }
        }

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
