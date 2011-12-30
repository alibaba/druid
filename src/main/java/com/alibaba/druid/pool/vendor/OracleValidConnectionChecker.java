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
import java.sql.Connection;
import java.sql.SQLException;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.PoolableConnection;
import com.alibaba.druid.pool.ValidConnectionChecker;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.util.OracleUtils;

public class OracleValidConnectionChecker implements ValidConnectionChecker, Serializable {

    private static final long serialVersionUID = -2227528634302168877L;

    private static final Log  LOG              = LogFactory.getLog(OracleValidConnectionChecker.class);

    public OracleValidConnectionChecker(){

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
            if (conn instanceof PoolableConnection) {
                conn = ((PoolableConnection) conn).getConnection();
            }

            if (conn instanceof ConnectionProxy) {
                conn = ((ConnectionProxy) conn).getConnectionRaw();
            }

            int status = OracleUtils.pingDatabase(conn);

            // Error
            if (status < 0) {
                return false;
            }

            return true;
        } catch (Exception e) {
            LOG.warn("Unexpected error in pingDatabase", e);
        }

        // OK
        return true;
    }
}
