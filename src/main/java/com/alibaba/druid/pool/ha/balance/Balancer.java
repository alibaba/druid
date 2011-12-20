package com.alibaba.druid.pool.ha.balance;

import java.sql.SQLException;

import com.alibaba.druid.pool.ha.MultiConnectionHolder;
import com.alibaba.druid.pool.ha.MultiDataSourceConnection;

public interface Balancer {
    MultiConnectionHolder getConnection(MultiDataSourceConnection connectionProxy, String sql) throws SQLException;
}
