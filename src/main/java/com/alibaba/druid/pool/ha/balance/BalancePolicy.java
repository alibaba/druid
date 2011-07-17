package com.alibaba.druid.pool.ha.balance;

import java.sql.SQLException;

import com.alibaba.druid.pool.ha.MultiDataSourceConnection;

public interface BalancePolicy {
    int indexFor(MultiDataSourceConnection connection, String sql) throws SQLException;
}
