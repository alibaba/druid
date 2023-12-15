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

import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.ValidConnectionChecker;
import com.alibaba.druid.pool.ValidConnectionCheckerAdapter;
import com.alibaba.druid.util.StringUtils;

import java.sql.Connection;

public class OceanBaseValidConnectionChecker extends ValidConnectionCheckerAdapter implements ValidConnectionChecker {
    private String commonValidateQuery = "SELECT 'x' FROM DUAL";
    /**
     * MySQL:
     * specify a validation query in your connection pool that starts with {@literal /}* ping *{@literal /}.
     * Note that the syntax must be exactly as specified. This will cause the driver send a ping to the server
     * and return a dummy lightweight result set. When using a ReplicationConnection or LoadBalancedConnection,
     * the ping will be sent across all active connections.
     */
    private String mysqlValidateQuery = "/* ping */ SELECT 1";
    private DbType dbType;

    public OceanBaseValidConnectionChecker() {
        configFromProperties(System.getProperties());
        dbType = null;
    }

    public OceanBaseValidConnectionChecker(DbType dbType) {
        this.dbType = dbType;
        configFromProperties(System.getProperties());
    }

    public boolean isValidConnection(final Connection conn,
                                     String validateQuery,
                                     int validationQueryTimeout) throws Exception {
        if (conn.isClosed()) {
            return false;
        }

        if (StringUtils.isEmpty(validateQuery)) {
            if (DbType.mysql.equals(dbType)) {
                validateQuery = mysqlValidateQuery;
            } else {
                validateQuery = commonValidateQuery;
            }
        }

        return ValidConnectionCheckerAdapter.execValidQuery(conn, validateQuery, validationQueryTimeout);
    }
}
