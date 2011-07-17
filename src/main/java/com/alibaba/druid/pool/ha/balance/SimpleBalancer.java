package com.alibaba.druid.pool.ha.balance;

import java.sql.Connection;
import java.sql.SQLException;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.MultiDataSource;
import com.alibaba.druid.pool.ha.MultiDataSourceConnection;

public class SimpleBalancer implements Balancer {

    private int maxActive = 8;

    public int getMaxActive() {
        return maxActive;
    }

    public void setMaxActive(int maxActive) {
        this.maxActive = maxActive;
    }

    @Override
    public Connection getConnection(MultiDataSourceConnection connectionProxy, String sql) throws SQLException {
        MultiDataSource multiDataSource = connectionProxy.getHaDataSource();

        Object[] array = multiDataSource.getDataSources().toArray();

        DruidDataSource minDataSource = null;
        int min = 0;
        for (Object item : array) {
            DruidDataSource dataSource = (DruidDataSource) item;

            if (!dataSource.isEnable()) {
                continue;
            }

            int activeCount = dataSource.getActiveCount();

            if (minDataSource == null || activeCount < min) {
                minDataSource = dataSource;
                min = activeCount;
            }

            if (activeCount >= maxActive) {
                continue;
            }

            return dataSource.getConnection();
        }

        if (minDataSource != null) {
            return minDataSource.getConnection();
        }

        throw new SQLException("can not get connection, no availabe datasources");
    }
}
