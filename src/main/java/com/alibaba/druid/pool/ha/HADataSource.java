package com.alibaba.druid.pool.ha;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import com.alibaba.druid.pool.DruidDataSource;

public class HADataSource extends MultiDataSource implements DataSource {

    private AtomicInteger requestCount = new AtomicInteger();

    @Override
    public Connection getConnection() throws SQLException {
        int requestNumber = requestCount.getAndIncrement();
        int size = dataSources.size();
        int index = requestNumber % size;

        DruidDataSource dataSource = dataSources.get(index);
        Connection conn = dataSource.getConnection();

        return conn;
    }



}
