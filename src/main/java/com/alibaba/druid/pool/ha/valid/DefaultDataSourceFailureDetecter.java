package com.alibaba.druid.pool.ha.valid;

import java.sql.Connection;
import java.sql.Statement;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class DefaultDataSourceFailureDetecter implements DataSourceFailureDetecter {

    private final static Log LOG           = LogFactory.getLog(DefaultDataSourceFailureDetecter.class);

    private long             maxWaitMillis = 3000;

    private String           validateSql   = "SELECT 1";

    public long getMaxWaitMillis() {
        return maxWaitMillis;
    }

    public void setMaxWaitMillis(long maxWaitMillis) {
        this.maxWaitMillis = maxWaitMillis;
    }

    public String getValidateSql() {
        return validateSql;
    }

    public void setValidateSql(String validateSql) {
        this.validateSql = validateSql;
    }

    @Override
    public boolean isValid(DruidDataSource dataSource) {
        Connection conn = null;

        try {
            conn = dataSource.getConnection(maxWaitMillis);

            if (conn == null) {
                return false;
            }

            return isValidConnection(dataSource, conn);

        } catch (Exception ex) {
            LOG.error("check datasource valid errror", ex);
        } finally {
            JdbcUtils.close(conn);
        }

        return false;
    }

    public boolean isValidConnection(DruidDataSource dataSource, Connection conn) {
        Statement stmt = null;

        try {
            stmt = conn.createStatement();
            stmt.execute(getValidateSql());
        } catch (Exception ex) {
            LOG.error("check datasource valid errror", ex);
        } finally {
            JdbcUtils.close(stmt);
        }

        return true;
    }

}
