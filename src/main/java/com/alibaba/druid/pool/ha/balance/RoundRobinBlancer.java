package com.alibaba.druid.pool.ha.balance;

import java.sql.SQLException;

import com.alibaba.druid.pool.ha.MultiDataSource;
import com.alibaba.druid.pool.ha.MultiDataSourceConnection;

public class RoundRobinBlancer implements Balancer {

    @Override
    public int indexFor(MultiDataSourceConnection connection, String sql) throws SQLException {
        MultiDataSource dataSource = connection.getHaDataSource();

        int size = dataSource.getDataSources().size();
        long connectionId = (int) connection.getId();

        return (int) (connectionId % size);
    }

}
