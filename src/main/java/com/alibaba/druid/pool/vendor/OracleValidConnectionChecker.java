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

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.ValidConnectionChecker;

public class OracleValidConnectionChecker implements ValidConnectionChecker, Serializable {

    private static final long serialVersionUID = -2227528634302168877L;

    private static final Log  LOG              = LogFactory.getLog(OracleValidConnectionChecker.class);

    private Method            ping;

    // The timeout (apparently the timeout is ignored?)
    private static Object[]   params           = new Object[] { new Integer(5000) };

    @SuppressWarnings({ "unchecked", "rawtypes" })
    public OracleValidConnectionChecker(){
        try {
            Class oracleConnection = Thread.currentThread().getContextClassLoader().loadClass("oracle.jdbc.driver.OracleConnection");
            ping = oracleConnection.getMethod("pingDatabase", new Class[] { Integer.TYPE });
        } catch (Exception e) {
            throw new RuntimeException("Unable to resolve pingDatabase method:", e);
        }
    }

    public boolean isValidConnection(Connection c) {
        try {
            Integer status = (Integer) ping.invoke(c, params);

            // Error
            if (status.intValue() < 0) {
                return false;
            }
        } catch (Exception e) {
            LOG.warn("Unexpected error in pingDatabase", e);
        }

        // OK
        return true;
    }
}
