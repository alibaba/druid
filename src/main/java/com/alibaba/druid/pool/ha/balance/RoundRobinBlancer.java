package com.alibaba.druid.pool.ha.balance;

import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.druid.logging.Log;
import com.alibaba.druid.logging.LogFactory;
import com.alibaba.druid.pool.ha.DataSourceChangedEvent;
import com.alibaba.druid.pool.ha.DataSourceHolder;
import com.alibaba.druid.pool.ha.MultiConnectionHolder;
import com.alibaba.druid.pool.ha.MultiDataSource;
import com.alibaba.druid.pool.ha.MultiDataSourceConnection;

public class RoundRobinBlancer extends AbstractBalancer {

    private final static Log    LOG             = LogFactory.getLog(RoundRobinBlancer.class);

    private final AtomicInteger indexErrorCount = new AtomicInteger();

    public RoundRobinBlancer(){

    }

    public void afterDataSourceChanged(DataSourceChangedEvent event) {

    }

    @Override
    public MultiConnectionHolder getConnection(MultiDataSourceConnection conn, String sql) throws SQLException {
        MultiDataSource multiDataSource = conn.getMultiDataSource();

        int size = multiDataSource.getDataSources().size();
        long connectionId = (int) conn.getId();

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
        }

        if (dataSource == null) {
            throw new SQLException("can not get real connection.");
        }

        MultiConnectionHolder holder = null;

        return holder;
    }

    public long getIndexErrorCount() {
        return indexErrorCount.get();
    }
}
