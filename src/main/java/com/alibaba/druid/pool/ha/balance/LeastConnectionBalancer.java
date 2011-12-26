package com.alibaba.druid.pool.ha.balance;

import java.sql.SQLException;

import com.alibaba.druid.pool.ha.DataSourceChangedEvent;
import com.alibaba.druid.pool.ha.MultiConnectionHolder;
import com.alibaba.druid.pool.ha.MultiDataSourceConnection;

public class LeastConnectionBalancer extends AbstractBalancer {

    @Override
    public void afterDataSourceChanged(DataSourceChangedEvent event) {

    }

    @Override
    public MultiConnectionHolder getConnection(MultiDataSourceConnection connectionProxy, String sql)
                                                                                                     throws SQLException {
        return null;
    }

}
