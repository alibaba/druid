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

import static org.junit.Assert.*;


import java.sql.Connection;

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
        assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_default() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");

        assertEquals(0, dataSource.getProxyFilters().size());

        dataSource.setFilters("stat");

        JdbcStatManager.getInstance().reset();

        dataSource.init();
        JdbcDataSourceStat stat = dataSource.getDataSourceStat();

        assertEquals(0, stat.getConnectionStat().getConnectCount());
        assertEquals(1, dataSource.getProxyFilters().size());

        for (int i = 0; i < 2; ++i) {
            Connection conn = dataSource.getConnection();

            assertEquals(1, stat.getConnectionStat().getConnectCount());
            assertEquals(0, stat.getConnectionStat().getCloseCount());

            conn.close();

            assertEquals(1, stat.getConnectionStat().getConnectCount());
            assertEquals(0, stat.getConnectionStat().getCloseCount()); // logic
            // close不会导致计数器＋1
        }

        dataSource.close();

        // assertEquals(0, JdbcStatManager.getInstance().getDataSources().size());

        assertEquals(1, stat.getConnectionStat().getConnectCount());
        assertEquals(1, stat.getConnectionStat().getCloseCount());

        JdbcStatManager.getInstance().reset();

        assertEquals(1, stat.getConnectionStat().getConnectCount());
        assertEquals(1, stat.getConnectionStat().getCloseCount());
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
        assertNotNull(error);

        assertEquals(0, dataSource.getProxyFilters().size());

        dataSource.setFilters("stat");

        JdbcStatManager.getInstance().reset();

        // assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
        // assertEquals(0, JdbcStatManager.getInstance().getDataSources().size());

        assertEquals(1, dataSource.getProxyFilters().size());

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
        assertEquals(1, dataSource.getProxyFilters().size());

        JdbcStatManager.getInstance().reset();

        dataSource.init();
        JdbcDataSourceStat stat = dataSource.getDataSourceStat();

        assertEquals(10, stat.getConnectionStat().getConnectCount());

        for (int i = 0; i < 10; ++i) {
            Connection conn = dataSource.getConnection();
            conn.close();
        }

        assertEquals(10, stat.getConnectionStat().getConnectCount());

        dataSource.close();
    }
}
