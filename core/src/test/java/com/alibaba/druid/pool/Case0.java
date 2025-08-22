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
package com.alibaba.druid.pool;

import java.sql.Connection;

import static org.junit.*;
import junit.framework.TestCase;

import com.alibaba.druid.util.JMXUtils;

public class Case0 extends TestCase {
    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass;
    private int initialSize = 1;
    private int minPoolSize = 1;
    private int maxPoolSize = 2;
    private int maxActive = 2;

    protected void setUp() throws Exception {
        jdbcUrl = "jdbc:fake:dragoon_v25masterdb";
        user = "dragoon25";
        password = "dragoon25";
        driverClass = "com.alibaba.druid.mock.MockDriver";
    }

    public void test_singleThread() throws Exception {
        final DruidDataSource dataSource = new DruidDataSource();

        JMXUtils.register("com.alibaba.druid:type=DruidDataSource", dataSource);

        Class.forName("com.alibaba.druid.mock.MockDriver");

        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minPoolSize);
        dataSource.setMaxIdle(maxPoolSize);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setFilters("stat");

        final int LOOP_COUNT = 1000 * 1000;

        assertEquals(0, dataSource.getCreateCount());
        assertEquals(0, dataSource.getDestroyCount());
        assertEquals(0, dataSource.getPoolingCount());

        for (int i = 0; i < LOOP_COUNT; ++i) {
            Connection conn = dataSource.getConnection();

            conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);

            assertEquals(initialSize, dataSource.getCreateCount());

            assertEquals(i + 1, dataSource.getConnectCount());
            assertEquals(1, dataSource.getActiveCount());
            assertEquals(i, dataSource.getCloseCount());
            assertEquals(0, dataSource.getConnectErrorCount());
            assertEquals(initialSize - 1, dataSource.getPoolingCount());
            assertEquals(i, dataSource.getRecycleCount());

            conn.close();

            assertEquals(i + 1, dataSource.getConnectCount());
            assertEquals(0, dataSource.getActiveCount());
            assertEquals(i + 1, dataSource.getCloseCount());
            assertEquals(0, dataSource.getConnectErrorCount());
            assertEquals(initialSize, dataSource.getPoolingCount());
            assertEquals(i + 1, dataSource.getRecycleCount());
        }

        assertEquals(initialSize, dataSource.getCreateCount());
        assertEquals(0, dataSource.getDestroyCount());

        dataSource.close();
        assertEquals(dataSource.getCreateCount(), dataSource.getDestroyCount());
    }
}
