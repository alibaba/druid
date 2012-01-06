/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2006, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package com.alibaba.druid.pool.vendor;

import java.io.Serializable;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.ValidConnectionChecker;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.util.DruidLoaderUtils;
import com.alibaba.druid.util.JdbcUtils;

public class OracleValidConnectionChecker implements ValidConnectionChecker, Serializable {

    private static final long     serialVersionUID = -2227528634302168877L;

    private static final Log      LOG              = LogFactory.getLog(OracleValidConnectionChecker.class);

    private final Class<?>        clazz;
    private final Method          ping;
    private final static Object[] params           = new Object[] { new Integer(5000) };

    public OracleValidConnectionChecker(){
        try {
            clazz = DruidLoaderUtils.loadClass("oracle.jdbc.driver.OracleConnection");
            ping = clazz.getMethod("pingDatabase", new Class[] { Integer.TYPE });
        } catch (Exception e) {
            throw new RuntimeException("Unable to resolve pingDatabase method:", e);
        }
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

        if (valiateQuery == null) {
            return true;
        }

        try {
            if (conn instanceof DruidPooledConnection) {
                conn = ((DruidPooledConnection) conn).getConnection();
            }

            if (conn instanceof ConnectionProxy) {
                conn = ((ConnectionProxy) conn).getRawObject();
            }

            // unwrap
            if (clazz.isAssignableFrom(conn.getClass())) {
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
