package com.alibaba.druid.pool.ha.balance;

import java.sql.Connection;
import java.sql.SQLException;

import com.alibaba.druid.pool.ha.MultiDataSourceConnection;

public interface Balancer {
    Connection getConnection(MultiDataSourceConnection connectionProxy, String sql) throws SQLException;
}
