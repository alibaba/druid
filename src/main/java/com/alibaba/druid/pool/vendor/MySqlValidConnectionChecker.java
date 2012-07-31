package com.alibaba.druid.pool.vendor;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.ValidConnectionChecker;
import com.alibaba.druid.proxy.jdbc.ConnectionProxy;
import com.alibaba.druid.support.logging.Log;
import com.alibaba.druid.support.logging.LogFactory;
import com.alibaba.druid.util.JdbcUtils;

public class MySqlValidConnectionChecker implements ValidConnectionChecker, Serializable {

    private static final long serialVersionUID    = 1L;
    private static final Log  LOG                 = LogFactory.getLog(MySqlValidConnectionChecker.class);

    private Class<?>          clazz;
    private Method            ping;
    private boolean           driverHasPingMethod = false;

    public MySqlValidConnectionChecker(){
        try {
            clazz = JdbcUtils.loadDriverClass("com.mysql.jdbc.Connection");
            ping = clazz.getMethod("ping");
            if (ping != null) {
                driverHasPingMethod = true;
            }
        } catch (Exception e) {
            LOG.warn("Cannot resolve com.mysq.jdbc.Connection.ping method.  Will use 'SELECT 1' instead.", e);
        }
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

        if (driverHasPingMethod) {
            if (conn instanceof DruidPooledConnection) {
                conn = ((DruidPooledConnection) conn).getConnection();
            }

            if (conn instanceof ConnectionProxy) {
                conn = ((ConnectionProxy) conn).getRawObject();
            }

            if (clazz.isAssignableFrom(conn.getClass())) {
                try {
                    ping.invoke(conn);
                    return true;
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getCause();
                    if (cause instanceof SQLException) {
                        return false;
                    }
                    
                    LOG.warn("Unexpected error in ping", e);
                    return false;
                } catch (Exception e) {
                    LOG.warn("Unexpected error in ping", e);
                    return false;
                }
            }
        }

        Statement stmt = null;
        ResultSet rs = null;
        try {
            stmt = conn.createStatement();
            if (validationQueryTimeout > 0) {
                stmt.setQueryTimeout(validationQueryTimeout);
            }
            rs = stmt.executeQuery(valiateQuery);
            return true;
        } catch (SQLException e) {
            return false;
        } catch (Exception e) {
            LOG.warn("Unexpected error in ping", e);
            return false;
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
        }

    }

}
