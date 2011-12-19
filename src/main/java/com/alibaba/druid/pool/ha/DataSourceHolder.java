package com.alibaba.druid.pool.ha;

import java.io.Closeable;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

import com.alibaba.druid.pool.DruidDataSource;

public class DataSourceHolder implements Closeable {

    private final DruidDataSource  dataSource;
    private boolean          enable            = true;
    private final AtomicLong connectCount      = new AtomicLong();
    private final AtomicLong connectErrorCount = new AtomicLong();

    public DataSourceHolder(DruidDataSource dataSource){
        if (dataSource == null) {
            throw new IllegalArgumentException("dataSource is null");
        }
        this.dataSource = dataSource;
    }

    public void resetState() {
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

    public long getConnectCount() {
        return connectCount.get();
    }

    public void incrementConnectCount() {
        connectCount.incrementAndGet();
    }

    public String getUrl() {
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

    @Override
    public void close() {
        dataSource.close();
    }
    
    public void restart() {
        dataSource.resetStat();
    }
}
