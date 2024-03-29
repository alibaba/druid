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
import com.alibaba.druid.util.StringUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.util.Properties;

public class OracleValidConnectionChecker extends ValidConnectionCheckerAdapter implements ValidConnectionChecker, Serializable {
    private static final long serialVersionUID = -2227528634302168877L;

    private int timeout = 1;

    private String defaultValidateQuery = "SELECT 'x' FROM DUAL";

    public OracleValidConnectionChecker() {
        configFromProperties(System.getProperties());
    }

    @Override
    public void configFromProperties(Properties properties) {
        if (properties == null) {
            return;
        }

        String property = properties.getProperty("druid.oracle.pingTimeout");
        if (property != null && property.length() > 0) {
            int value = Integer.parseInt(property);
            setTimeout(value);
        }
    }

    public void setTimeout(int seconds) {
        this.timeout = seconds;
    }

    public boolean isValidConnection(Connection conn,
                                     String validateQuery,
                                     int validationQueryTimeout) throws Exception {
        if (conn.isClosed()) {
            return false;
        }

        if (StringUtils.isEmpty(validateQuery)) {
            validateQuery = this.defaultValidateQuery;
        }

        int queryTimeout = validationQueryTimeout <= 0 ? timeout : validationQueryTimeout;

        return ValidConnectionCheckerAdapter.execValidQuery(conn, validateQuery, queryTimeout);
    }
}
