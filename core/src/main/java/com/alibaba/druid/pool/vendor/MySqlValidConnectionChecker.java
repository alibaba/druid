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

import com.alibaba.druid.pool.ValidConnectionChecker;
import com.alibaba.druid.pool.ValidConnectionCheckerAdapter;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.StringUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Properties;

public class MySqlValidConnectionChecker extends ValidConnectionCheckerAdapter implements ValidConnectionChecker, Serializable {
    public static final int DEFAULT_VALIDATION_QUERY_TIMEOUT = 1;
    /**
     * @see <a href="https://dev.mysql.com/doc/connectors/en/connector-j-usagenotes-j2ee-concepts-connection-pooling.html">Connection Pooling with Connector/J</a>
     * <p>
     * specify a validation query in your connection pool that starts with {@literal /}* ping *{@literal /}.
     * Note that the syntax must be exactly as specified. This will cause the driver send a ping to the server
     * and return a dummy lightweight result set. When using a ReplicationConnection or LoadBalancedConnection,
     * the ping will be sent across all active connections.
     */
    public static final String DEFAULT_VALIDATION_QUERY = "/* ping */ SELECT 1";

    private static final long serialVersionUID = 1L;
    private static final Log LOG = LogFactory.getLog(MySqlValidConnectionChecker.class);

    /** using ping SQL by default */
    private boolean usePingMethod = true;

    public MySqlValidConnectionChecker() {
        configFromProperties(System.getProperties());
    }

    @Override
    public void configFromProperties(Properties properties) {
        if (properties == null) {
            return;
        }

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

    public boolean isValidConnection(Connection conn,
                                     String validateQuery,
                                     int validationQueryTimeout) throws Exception {
        if (conn.isClosed()) {
            return false;
        }

        if (usePingMethod || StringUtils.isEmpty(validateQuery)) {
            validateQuery = DEFAULT_VALIDATION_QUERY;
        }

        return ValidConnectionCheckerAdapter.execValidQuery(conn, validateQuery, validationQueryTimeout);
    }

}
