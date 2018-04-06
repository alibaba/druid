/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcDataSourceStat;
import com.alibaba.druid.stat.JdbcStatManager;

public class DruidDataSourceFilterTest extends TestCase {

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();
    }

    protected void tearDown() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_filter() throws Exception {
        System.out.println(System.nanoTime()/(1000 * 1000));
        DruidDataSource dataSource = new DruidDataSource();

        Assert.assertEquals(0, dataSource.getProxyFilters().size());

        dataSource.setFilters("stat");

        Assert.assertEquals(1, dataSource.getProxyFilters().size());

        dataSource.close();
    }

    public void test_filter_3() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");

        Assert.assertEquals(0, dataSource.getProxyFilters().size());

        dataSource.setFilters("stat");

        JdbcStatManager.getInstance().reset();
        
        dataSource.init();
        JdbcDataSourceStat dataSourceStat =dataSource.getDataSourceStat();
//        Assert.assertEquals(1, JdbcStatManager.getInstance().getDataSources().size());
//        JdbcDataSourceStat dataSourceStat = JdbcStatManager.getInstance().getDataSources().values().iterator().next();

        Assert.assertEquals(0, dataSourceStat.getConnectionStat().getConnectCount());
        Assert.assertEquals(1, dataSource.getProxyFilters().size());

        for (int i = 0; i < 2; ++i) {
            Connection conn = dataSource.getConnection();

            Assert.assertEquals(1, dataSourceStat.getConnectionStat().getConnectCount());
            Assert.assertEquals(0, dataSourceStat.getConnectionStat().getCloseCount());

            conn.close();

            Assert.assertEquals(1, dataSourceStat.getConnectionStat().getConnectCount());
            Assert.assertEquals(0, dataSourceStat.getConnectionStat().getCloseCount()); // logic
                                                                                                       // close不会导致计数器＋1
        }

        Assert.assertEquals(1, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        dataSource.close();

        Assert.assertEquals(1, dataSourceStat.getConnectionStat().getConnectCount());
        Assert.assertEquals(1, dataSourceStat.getConnectionStat().getCloseCount());
    }
}
