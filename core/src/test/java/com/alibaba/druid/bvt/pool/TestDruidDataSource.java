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
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestDruidDataSource extends TestCase {
    protected void tearDown() throws Exception {
        DruidDataSourceStatManager.clear();
    }

    protected void setUp() throws Exception {
        assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_0() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();

        Class.forName("com.alibaba.druid.mock.MockDriver");

        dataSource.setInitialSize(10);
        dataSource.setMaxActive(10);
        dataSource.setDriverClassName("com.alibaba.druid.mock.MockDriver");
        dataSource.setUrl("jdbc:mock:xxx");

        assertEquals(0, dataSource.getConnectCount());
        assertEquals(0, dataSource.getConnectErrorCount());
        assertEquals(0, dataSource.getCloseCount());
        assertEquals(0, dataSource.getPoolingCount());

        Connection conn = dataSource.getConnection();

        assertTrue(conn instanceof DruidPooledConnection);

        assertEquals(1, dataSource.getConnectCount());
        assertEquals(0, dataSource.getConnectErrorCount());
        assertEquals(0, dataSource.getCloseCount());
        assertEquals(0, dataSource.getRecycleCount());
        assertEquals(1, dataSource.getActiveCount());
        assertEquals(9, dataSource.getPoolingCount());

        conn.close();

        assertEquals(1, dataSource.getConnectCount());
        assertEquals(0, dataSource.getConnectErrorCount());
        assertEquals(1, dataSource.getCloseCount());
        assertEquals(1, dataSource.getRecycleCount());
        assertEquals(0, dataSource.getActiveCount());
        assertEquals(10, dataSource.getPoolingCount());

        conn.close(); // 重复close

        assertEquals(1, dataSource.getConnectCount());
        assertEquals(0, dataSource.getConnectErrorCount());
        assertEquals(1, dataSource.getCloseCount());
        assertEquals(1, dataSource.getRecycleCount());
        assertEquals(0, dataSource.getActiveCount());

        dataSource.close();
    }
}
