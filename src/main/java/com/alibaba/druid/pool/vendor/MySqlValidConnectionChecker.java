package com.alibaba.druid.pool.vendor;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.SQLException;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.ValidConnectionChecker;
import com.alibaba.druid.util.MySqlUtils;

public class MySqlValidConnectionChecker implements ValidConnectionChecker, Serializable {

    private static final long serialVersionUID = 1L;
    private static final Log  LOG              = LogFactory.getLog(MySqlValidConnectionChecker.class);

    public MySqlValidConnectionChecker(){

    }

    public boolean isValidConnection(Connection conn, String valiateQuery, int validationQueryTimeout) {
        try {
            if (conn.isClosed()) {
                return false;
            }
        } catch (SQLException ex) {
            // skip
            return false;
        }

        if (valiateQuery == null) {
            return true;
        }

        try {
            MySqlUtils.ping(conn);
            return true;
        } catch (Exception e) {
            LOG.warn("Unexpected error in ping", e);
            return false;
        }

    }

}
