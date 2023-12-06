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

import java.sql.Connection;
import java.util.Properties;

/**
 * Common ValidConnectionChecker for JDBC4 to use Connection.isValid.
 *
 * Author : kimmking(kimmking@apache.org)
 * create 2023/3/4 16:48
 * Since 1.2.17
 */
public class JDBC4ValidConnectionChecker implements ValidConnectionChecker {
    @Override
    public boolean isValidConnection(Connection c, String query, int validationQueryTimeout) throws Exception {
        Connection conn = c;
        if (conn instanceof DruidPooledConnection) {
            conn = ((DruidPooledConnection) conn).getConnection();
        }
        return conn.isValid(validationQueryTimeout);
    }

    @Override
    public void configFromProperties(Properties properties) {
    }
}
