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
import com.alibaba.druid.util.JdbcUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class PGValidConnectionChecker extends ValidConnectionCheckerAdapter implements ValidConnectionChecker, Serializable {

    private static final long serialVersionUID     = -2227528634302168877L;

    private int               defaultQueryTimeout  = 1;

    private String            defaultValidateQuery = "SELECT 'x'";

    public PGValidConnectionChecker(){
        configFromProperties(System.getProperties());
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

        int queryTimeout = validationQueryTimeout <= 0 ? defaultQueryTimeout : validationQueryTimeout;

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            if (queryTimeout >= 0) {
                //pgsql Driver 9.0以及以下版本不支持setQueryTimeout，可通过设置queryTimeout<0兼容低版本
                stmt.setQueryTimeout(queryTimeout);
            }
            rs = stmt.executeQuery(validateQuery);
            return true;
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
    }
}
