package com.alibaba.druid.pool.vendor;

import com.alibaba.druid.pool.ValidConnectionChecker;
import com.alibaba.druid.pool.ValidConnectionCheckerAdapter;
import com.alibaba.druid.util.JdbcUtils;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class HiveValidConnectionChecker extends ValidConnectionCheckerAdapter implements ValidConnectionChecker, Serializable {
    private static final long serialVersionUID = -3642139641360283076L;
    public static final String DEFAULT_VALIDATION_QUERY = "SELECT 1";
    private int defaultQueryTimeout;

    @Override
    public boolean isValidConnection(Connection conn, String validateQuery, int validationQueryTimeout) throws Exception {
        int queryTimeout = validationQueryTimeout <= 0 ? defaultQueryTimeout : validationQueryTimeout;
        if (conn.isClosed() || !conn.isValid(queryTimeout)) {
            return false;
        }
        String query = validateQuery;
        if (validateQuery == null || validateQuery.isEmpty()) {
            query = DEFAULT_VALIDATION_QUERY;
        }
        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            if (validationQueryTimeout > 0) {
                stmt.setQueryTimeout(validationQueryTimeout);
            }
            rs = stmt.executeQuery(query);
            return true;
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }
    }
}
