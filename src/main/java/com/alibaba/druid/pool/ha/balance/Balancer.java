package com.alibaba.druid.pool.ha.balance;

import java.sql.SQLException;

import com.alibaba.druid.pool.ha.DataSourceChangedEvent;
import com.alibaba.druid.pool.ha.MultiConnectionHolder;
import com.alibaba.druid.pool.ha.MultiDataSource;
import com.alibaba.druid.pool.ha.MultiDataSourceConnection;

public interface Balancer {

    boolean isInited();

    void init(MultiDataSource dataSource);

    void afterDataSourceChanged(DataSourceChangedEvent event);

    MultiConnectionHolder getConnection(MultiDataSourceConnection connectionProxy, String sql) throws SQLException;
}
