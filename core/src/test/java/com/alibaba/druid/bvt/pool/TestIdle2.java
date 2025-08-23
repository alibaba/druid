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

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestIdle2 extends TestCase {
    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();
    }

    protected void tearDown() throws Exception {
        assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_idle2() throws Exception {
        MockDriver driver = new MockDriver();

        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(14);
        dataSource.setMaxActive(14);
        dataSource.setMaxIdle(14);
        dataSource.setMinIdle(14);
        dataSource.setMinEvictableIdleTimeMillis(50 * 1);
        dataSource.setTimeBetweenEvictionRunsMillis(10);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");

        {
            assertEquals(0, dataSource.getCreateCount());
            assertEquals(0, dataSource.getActiveCount());

            Connection conn = dataSource.getConnection();

            assertEquals(dataSource.getInitialSize(), dataSource.getCreateCount());
            assertEquals(dataSource.getInitialSize(), driver.getConnections().size());
            assertEquals(1, dataSource.getActiveCount());

            conn.close();
            assertEquals(0, dataSource.getDestroyCount());
            assertEquals(true, dataSource.getPoolingCount() == driver.getConnections().size());
            assertEquals(0, dataSource.getActiveCount());
        }

        String text = dataSource.toString();
        System.out.println(text);

        {
            int count = 14;
            Connection[] connections = new Connection[count];
            for (int i = 0; i < count; ++i) {
                connections[i] = dataSource.getConnection();
                assertEquals(i + 1, dataSource.getActiveCount());
            }
            assertEquals(dataSource.getMaxActive(), dataSource.getCreateCount());
            assertEquals(count, driver.getConnections().size());
            for (int i = 0; i < count; ++i) {
                connections[i].close();
                assertEquals(count - i - 1, dataSource.getActiveCount());
            }
            assertEquals(dataSource.getMaxActive(), dataSource.getCreateCount());
            assertEquals(0, dataSource.getActiveCount());
            assertEquals(14, driver.getConnections().size());
        }

        for (int i = 0; i < 100; ++i) {
            assertEquals(0, dataSource.getActiveCount());
            Connection conn = dataSource.getConnection();

            assertEquals(1, dataSource.getActiveCount());

            Thread.sleep(1);
            conn.close();
        }
        assertEquals(true, dataSource.getPoolingCount() == dataSource.getMaxActive());

        dataSource.close();
    }
}
