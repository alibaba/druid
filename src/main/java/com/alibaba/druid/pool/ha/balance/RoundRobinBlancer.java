package com.alibaba.druid.pool.ha.balance;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.ha.DataSourceHolder;
import com.alibaba.druid.pool.ha.MultiDataSource;
import com.alibaba.druid.pool.ha.MultiDataSourceConnection;

public class RoundRobinBlancer implements Balancer {

    private final static Log    LOG             = LogFactory.getLog(RoundRobinBlancer.class);

    private final AtomicInteger indexErrorCount = new AtomicInteger();

    public RoundRobinBlancer(){

    }

    @Override
    public Connection getConnection(MultiDataSourceConnection connectionProxy, String sql) throws SQLException {
        MultiDataSource multiDataSource = connectionProxy.getHaDataSource();

        int tryCount = 0;

        for (;;) {
            int size = multiDataSource.getDataSources().size();
            long connectionId = (int) connectionProxy.getId();

            if (size == 0) {
                throw new SQLException("can not get connection, no availabe datasources");
            }

            int index = (int) (connectionId % size);

            DataSourceHolder dataSource = null;

            try {
                // 处理并发时的错误
                List<DataSourceHolder> dataSources = new ArrayList<DataSourceHolder>(multiDataSource.getDataSources().values());
                dataSource = dataSources.get(index);
            } catch (Exception ex) {
                indexErrorCount.incrementAndGet();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getDataSource error, index : " + index, ex);
                }
                continue;
            }

            assert dataSource != null;

            if (!dataSource.isEnable()) {
                multiDataSource.handleNotAwailableDatasource(dataSource);
                continue;
            }

            Connection conn = null;

            try {
                tryCount++;
                conn = dataSource.getConnection();
            } catch (SQLException ex) {
                LOG.error("getConnection error", ex);

                if (tryCount >= size) {
                    throw ex;
                }

                continue;
            }

            return conn;
        }
    }

    public long getIndexErrorCount() {
        return indexErrorCount.get();
    }
}
