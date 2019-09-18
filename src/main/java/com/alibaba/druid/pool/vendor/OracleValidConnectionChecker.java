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
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.*;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.ValidConnectionChecker;
import com.alibaba.druid.pool.ValidConnectionCheckerAdapter;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;

public class OracleValidConnectionChecker extends ValidConnectionCheckerAdapter implements ValidConnectionChecker, Serializable {

    private static final long            serialVersionUID = -2227528634302168877L;

    private static final Log             LOG              = LogFactory.getLog(OracleValidConnectionChecker.class);
    private static final ExecutorService EXECUTOR_SERVICE = Executors.newFixedThreadPool(5);

    private int               timeout              = 1;

    private String            defaultValidateQuery = "SELECT 'x' FROM DUAL";

    public OracleValidConnectionChecker(){
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

    public void setTimeout(int seconds) {
        this.timeout = seconds;
    }

    public boolean isValidConnection(Connection conn, String validateQuery, int validationQueryTimeout) throws Exception {
        if (validateQuery == null || validateQuery.isEmpty()) {
            validateQuery = this.defaultValidateQuery;
        }

        if (conn.isClosed()) {
            return false;
        }

        if (conn instanceof DruidPooledConnection) {
            conn = ((DruidPooledConnection) conn).getConnection();
        }

        if (conn instanceof ConnectionProxy) {
            conn = ((ConnectionProxy) conn).getRawObject();
        }

        if (validateQuery == null || validateQuery.isEmpty()) {
            return true;
        }

        final int queryTimeout = validationQueryTimeout <= 0 ? timeout : validationQueryTimeout;

        final Connection finalConn = conn;
        final String finalValidateQuery = validateQuery;
        Future<Boolean> future = EXECUTOR_SERVICE.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                Statement stmt = null;
                ResultSet rs = null;
                try {
                    stmt = finalConn.createStatement();
                    stmt.setQueryTimeout(queryTimeout);
                    rs = stmt.executeQuery(finalValidateQuery);
                    return Boolean.TRUE;
                } catch (Exception e){
                    String msg = "query error when check oracle connection";
                    LOG.info(msg);
                    LOG.debug(msg, e);
                    return Boolean.FALSE;
                } finally {
                    JdbcUtils.close(rs);
                    JdbcUtils.close(stmt);
                }
            }
        });

        Boolean result = Boolean.TRUE;
        try {
            result = future.get(queryTimeout, TimeUnit.SECONDS);
        } catch (Exception e) {
            String msg = "check oracle connection error";
            LOG.info(msg);
            LOG.debug(msg, e);
            result = Boolean.FALSE;
        }
        return result;
    }
}
