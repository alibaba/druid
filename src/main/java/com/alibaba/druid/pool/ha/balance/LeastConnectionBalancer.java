/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.pool.ha.balance;

import java.sql.SQLException;

import com.alibaba.druid.pool.ha.DataSourceChangedEvent;
import com.alibaba.druid.pool.ha.DataSourceHolder;
import com.alibaba.druid.pool.ha.MultiConnectionHolder;
import com.alibaba.druid.pool.ha.MultiDataSource;
import com.alibaba.druid.pool.ha.MultiDataSourceConnection;

public class LeastConnectionBalancer extends AbstractBalancer {

    @Override
    public void afterDataSourceChanged(DataSourceChangedEvent event) {

    }

    @Override
    public MultiConnectionHolder getConnection(MultiDataSourceConnection conn, String sql) throws SQLException {
        MultiDataSource multiDataSource = conn.getMultiDataSource();

        DataSourceHolder dataSource = null;
        int leastCount = -1;

        for (DataSourceHolder item : multiDataSource.getDataSources().values()) {
            if (!item.isEnable()) {
                continue;
            }

            int activeCount = item.getDataSource().getActiveCount();
            if (dataSource == null) {
                leastCount = activeCount;
                dataSource = item;
            } else {
                if (leastCount > item.getDataSource().getActiveCount()) {
                    dataSource = item;
                    leastCount = activeCount;
                }
            }
        }

        if (dataSource == null) {
            throw new SQLException("can not get real connection, enableDataSourceCount "
                                   + multiDataSource.getEnabledDataSourceCount());
        }

        return dataSource.getConnection();
    }

}
