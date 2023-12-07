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
package com.alibaba.druid.pool;

import com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl;
import com.alibaba.druid.util.JdbcUtils;
import com.alibaba.druid.util.StringUtils;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Properties;

/**
 * @author wenshao [szujobs@hotmail.com]
 * @since 0.2.21
 */
public class ValidConnectionCheckerAdapter implements ValidConnectionChecker {
    @Override
    public boolean isValidConnection(Connection conn, String query, int validationQueryTimeout) throws Exception {
        if (StringUtils.isEmpty(query)) {
            return true;
        }
        return execValidQuery(conn, query, validationQueryTimeout);
    }

    public static boolean execValidQuery(Connection conn, String query, int validationQueryTimeout) throws Exception {
        // using raw connection for createStatement to speed up validation by skipping all filters.
        Connection rawConn;
        if (conn instanceof ConnectionProxyImpl) {
            rawConn = ((ConnectionProxyImpl) conn).getConnectionRaw();
        } else {
            rawConn = conn;
        }

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = rawConn.createStatement();
            if (validationQueryTimeout > 0) {
                stmt.setQueryTimeout(validationQueryTimeout);
            }
            rs = stmt.executeQuery(query);
            return rs.next();
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
    }

    @Override
    public void configFromProperties(Properties properties) {
    }

}
