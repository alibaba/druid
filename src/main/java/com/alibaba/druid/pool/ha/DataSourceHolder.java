package com.alibaba.druid.pool.ha;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.druid.pool.DruidDataSource;

public class DataSourceHolder {

    private DruidDataSource  dataSource;
    private boolean          enable;
    private final AtomicLong connectCount      = new AtomicLong();
    private final AtomicLong connectErrorCount = new AtomicLong();

    public DataSourceHolder(DruidDataSource dataSource){
        this.dataSource = dataSource;
    }

    public void resetStat() {
        connectCount.set(0);
    }

    public boolean isEnable() {
        return enable;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public DruidDataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DruidDataSource dataSource) {
        this.dataSource = dataSource;
    }

    public long getConnectCount() {
        return connectCount.get();
    }

    public void incrementConnectCount() {
        connectCount.incrementAndGet();
    }

    public String getUrl() {
        if (dataSource == null) {
            return null;
        }

        return dataSource.getUrl();
    }

    public Connection getConnection() throws SQLException {
        connectCount.incrementAndGet();
        try {
            return dataSource.getConnection();
        } catch (SQLException ex) {
            connectErrorCount.incrementAndGet();
            throw ex;
        } catch (RuntimeException ex) {
            connectErrorCount.incrementAndGet();
            throw ex;
        }
    }
}
