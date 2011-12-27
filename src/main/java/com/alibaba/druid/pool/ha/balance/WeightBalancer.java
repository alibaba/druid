package com.alibaba.druid.pool.ha.balance;

import java.sql.SQLException;

import com.alibaba.druid.pool.ha.DataSourceChangedEvent;
import com.alibaba.druid.pool.ha.DataSourceHolder;
import com.alibaba.druid.pool.ha.MultiConnectionHolder;
import com.alibaba.druid.pool.ha.MultiDataSource;
import com.alibaba.druid.pool.ha.MultiDataSourceConnection;
import com.alibaba.druid.util.ThreadLocalRandom;

public class WeightBalancer extends AbstractBalancer {

    private int totalWeight = 0;

    public void afterDataSourceChanged(DataSourceChangedEvent event) {
        computeTotalWeight();
    }

    public int produceRandomNumber() {
        if (totalWeight == 0) {
            return 0;
        }

        return ThreadLocalRandom.current().nextInt(totalWeight);
    }

    public void computeTotalWeight() {
        int totalWeight = 0;
        for (DataSourceHolder holder : getMultiDataSource().getDataSources().values()) {
            if (!holder.isEnable()) {
                holder.setWeightRegionBegin(-1);
                holder.setWeightRegionEnd(-1);
                continue;
            }
            holder.setWeightRegionBegin(totalWeight);
            totalWeight += holder.getWeight();
            holder.setWeightRegionEnd(totalWeight);
        }
        this.totalWeight = totalWeight;

        getMultiDataSource().notFailSignal();
    }

    @Override
    public MultiConnectionHolder getConnection(MultiDataSourceConnection conn, String sql) throws SQLException {
        MultiDataSource multiDataSource = conn.getMultiDataSource();

        DataSourceHolder dataSource = null;

        int randomNumber = produceRandomNumber();
        DataSourceHolder first = null;

        for (DataSourceHolder item : multiDataSource.getDataSources().values()) {
            if (!item.isEnable()) {
                continue;
            }

            if (first == null) {
                first = item;
            }

            if (randomNumber >= item.getWeightRegionBegin() && randomNumber < item.getWeightRegionEnd()) {
                if (!item.isEnable()) {
                    continue;
                }

                if (item.getDataSource().isBusy()) {
                    multiDataSource.incrementBusySkipCount();
                    break;
                }

                dataSource = item;
            }
        }

        if (dataSource == null) {
            dataSource = first;
        }

        if (dataSource == null) {
            throw new SQLException("cannot get connection. enabledDataSourceCount "
                                   + multiDataSource.getEnabledDataSourceCount());
        }

        return dataSource.getConnection();
    }

}
