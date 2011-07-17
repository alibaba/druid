package com.alibaba.druid.pool.ha.balance;

import java.sql.SQLException;

import com.alibaba.druid.pool.ha.MultiDataSourceConnection;

public interface Balancer {
    int indexFor(MultiDataSourceConnection connection, String sql) throws SQLException;
}
