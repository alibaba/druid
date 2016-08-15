/*
 * Copyright 1999-2101 Alibaba Group Holding Ltd.
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

public class ParamTest extends TestCase {

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();
    }

    protected void tearDown() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_default() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");

        Assert.assertEquals(0, dataSource.getProxyFilters().size());

        dataSource.setFilters("stat");

        JdbcStatManager.getInstance().reset();

        dataSource.init();
        JdbcDataSourceStat stat = dataSource.getDataSourceStat();

        Assert.assertEquals(0, stat.getConnectionStat().getConnectCount());
        Assert.assertEquals(1, dataSource.getProxyFilters().size());

        for (int i = 0; i < 2; ++i) {
            Connection conn = dataSource.getConnection();

            Assert.assertEquals(1, stat.getConnectionStat().getConnectCount());
            Assert.assertEquals(0, stat.getConnectionStat().getCloseCount());

            conn.close();

            Assert.assertEquals(1, stat.getConnectionStat().getConnectCount());
            Assert.assertEquals(0, stat.getConnectionStat().getCloseCount()); // logic
                                                                              // close不会导致计数器＋1
        }

        dataSource.close();

        // Assert.assertEquals(0, JdbcStatManager.getInstance().getDataSources().size());

        Assert.assertEquals(1, stat.getConnectionStat().getConnectCount());
        Assert.assertEquals(1, stat.getConnectionStat().getCloseCount());

        JdbcStatManager.getInstance().reset();

        Assert.assertEquals(1, stat.getConnectionStat().getConnectCount());
        Assert.assertEquals(1, stat.getConnectionStat().getCloseCount());
    }

    public void test_zero() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");
        dataSource.setInitialSize(0);
        dataSource.setMinIdle(0);
        dataSource.setMaxIdle(0);

        Exception error = null;
        try {
            dataSource.setMaxActive(0);
        } catch (Exception e) {
            error = e;
        }
        Assert.assertNotNull(error);

        Assert.assertEquals(0, dataSource.getProxyFilters().size());

        dataSource.setFilters("stat");

        JdbcStatManager.getInstance().reset();

        // Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
        // Assert.assertEquals(0, JdbcStatManager.getInstance().getDataSources().size());

        Assert.assertEquals(1, dataSource.getProxyFilters().size());

        dataSource.getConnection();

        dataSource.close();
    }

    public void test_1() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");
        dataSource.setInitialSize(10);
        dataSource.setMaxActive(10);
        dataSource.setMinIdle(0);
        dataSource.setMaxIdle(10);

        dataSource.setFilters("stat");
        Assert.assertEquals(1, dataSource.getProxyFilters().size());

        JdbcStatManager.getInstance().reset();

        dataSource.init();
        JdbcDataSourceStat stat = dataSource.getDataSourceStat();

        Assert.assertEquals(10, stat.getConnectionStat().getConnectCount());

        for (int i = 0; i < 10; ++i) {
            Connection conn = dataSource.getConnection();
            conn.close();
        }

        Assert.assertEquals(10, stat.getConnectionStat().getConnectCount());

        dataSource.close();
    }
}
