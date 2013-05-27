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
package com.alibaba.druid.pool.vendor;

import java.io.Serializable;
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

public class OracleValidConnectionChecker extends ValidConnectionCheckerAdapter implements ValidConnectionChecker, Serializable {

    private static final long     serialVersionUID = -2227528634302168877L;

    private static final Log      LOG              = LogFactory.getLog(OracleValidConnectionChecker.class);

    private final Class<?>        clazz;
    private final Method          ping;
    private final static Object[] params           = new Object[] { new Integer(5000) };

    public OracleValidConnectionChecker(){
        try {
            clazz = JdbcUtils.loadDriverClass("oracle.jdbc.driver.OracleConnection");
            if (clazz != null) {
                ping = clazz.getMethod("pingDatabase", new Class[] { Integer.TYPE });
            } else {
                ping = null;
            }
        } catch (Exception e) {
            throw new RuntimeException("Unable to resolve pingDatabase method:", e);
        }
        
        configFromProperties(System.getProperties());
    }
    
    @Override
    public void configFromProperties(Properties properties) {
        String property = properties.getProperty("druid.oracle.pingTimeout");
        if (property != null && property.length() > 0) {
            int value = Integer.parseInt(property);
            setTimeout(value);
        }
    }
    
    public void setTimeout(int timeout) {
        params[0] = timeout;
    }

    public boolean isValidConnection(Connection conn, String valiateQuery, int validationQueryTimeout) {
        try {
            if (conn.isClosed()) {
                return false;
            }
        } catch (SQLException ex) {
            // skip
            return false;
        }

        try {
            if (conn instanceof DruidPooledConnection) {
                conn = ((DruidPooledConnection) conn).getConnection();
            }

            if (conn instanceof ConnectionProxy) {
                conn = ((ConnectionProxy) conn).getRawObject();
            }

            // unwrap
            if (clazz != null && clazz.isAssignableFrom(conn.getClass())) {
                Integer status = (Integer) ping.invoke(conn, params);

                // Error
                if (status.intValue() < 0) {
                    return false;
                }

                return true;
            }

            Statement stmt = null;
            ResultSet rs = null;
            try {
                stmt = conn.createStatement();
                rs = stmt.executeQuery(valiateQuery);
                return true;
            } catch (SQLException e) {
                return false;
            } catch (Exception e) {
                LOG.warn("Unexpected error in ping", e);
                return false;
            } finally {
                JdbcUtils.close(rs);
                JdbcUtils.close(stmt);
            }
        } catch (Exception e) {
            LOG.warn("Unexpected error in pingDatabase", e);
        }

        // OK
        return true;
    }
}
