package com.alibaba.druid.pool;

import java.sql.Connection;
import java.util.Properties;

/**
 * Common ValidConnectionChecker for JDBC4 to use Connection.isValid.
 *
 * @Author : kimmking(kimmking@apache.org)
 * @create 2023/3/4 16:48
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
