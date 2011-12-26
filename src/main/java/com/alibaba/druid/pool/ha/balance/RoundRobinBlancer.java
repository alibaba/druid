package com.alibaba.druid.pool.ha.balance;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.ha.DataSourceHolder;
import com.alibaba.druid.pool.ha.MultiConnectionHolder;
import com.alibaba.druid.pool.ha.MultiDataSource;
import com.alibaba.druid.pool.ha.MultiDataSourceConnection;

public class RoundRobinBlancer implements Balancer {

    private final static Log    LOG             = LogFactory.getLog(RoundRobinBlancer.class);

    private final AtomicInteger indexErrorCount = new AtomicInteger();

    public RoundRobinBlancer(){

    }

    @Override
    public MultiConnectionHolder getConnection(MultiDataSourceConnection connectionProxy, String sql)
                                                                                                     throws SQLException {
        MultiDataSource multiDataSource = connectionProxy.getMultiDataSource();

        int tryCount = 0;

        for (;;) {
            int size = multiDataSource.getDataSources().size();
            long connectionId = (int) connectionProxy.getId();

            if (size == 0) {
                throw new SQLException("can not get connection, no availabe datasources");
            }

            int index = (int) (connectionId % size);

            DataSourceHolder first = null;
            DataSourceHolder dataSource = null;

            try {
                int itemIndex = 0;
                for (DataSourceHolder item : multiDataSource.getDataSources().values()) {
                    if (!item.isEnable()) {
                        continue;
                    }
                    
                    if (first == null) {
                        first = item;
                    }
                    
                    if (itemIndex == index) {
                        dataSource = item;
                        break;
                    }
                    itemIndex++;
                }
                
                if (dataSource == null) {
                    dataSource = first;
                }
            } catch (Exception ex) {
                indexErrorCount.incrementAndGet();
                if (LOG.isDebugEnabled()) {
                    LOG.debug("getDataSource error, index : " + index, ex);
                }
                continue;
            }

            if (dataSource == null) {
                throw new SQLException("can not get real connection.");
            }

            MultiConnectionHolder conn = null;

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
