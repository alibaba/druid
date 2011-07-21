package com.alibaba.druid.pool.vendor;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.ValidConnectionChecker;
import com.alibaba.druid.util.JdbcUtils;

/**
 * A MSSQLValidConnectionChecker.
 */
public class MSSQLValidConnectionChecker implements ValidConnectionChecker, Serializable {

    private static final long   serialVersionUID = 1L;

    private static final String QUERY            = "SELECT 1";
    private static final Log    LOG              = LogFactory.getLog(MSSQLValidConnectionChecker.class);

    public boolean isValidConnection(final Connection c, String valiateQuery, int validationQueryTimeout) {
        try {
            if (c.isClosed()) {
                return false;
            }
        } catch (SQLException ex) {
         // skip 
            return false;
        }

        if (valiateQuery == null) {
            return true;
        }

        Statement stmt = null;

        try {
            stmt = c.createStatement();
            stmt.setQueryTimeout(validationQueryTimeout);
            stmt.execute(QUERY);
            return true;
        } catch (SQLException e) {
            LOG.warn("warning: connection validation failed for current managed connection.");
            return false;
        } finally {
            JdbcUtils.close(stmt);
        }
    }

}
