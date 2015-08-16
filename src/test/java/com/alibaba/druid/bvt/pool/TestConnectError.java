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
import java.sql.SQLException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestConnectError extends TestCase {

    private DruidDataSource dataSource;
    private MockDriver      driver;

    protected void setUp() throws Exception {
        driver = new MockDriver() {

            private AtomicInteger count = new AtomicInteger();

            public Connection connect(String url, Properties info) throws SQLException {
                if (count.getAndIncrement() % 2 == 0) {
                    throw new SQLException();
                }

                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    throw new SQLException();
                }

                return super.connect(url, info);
            }
        };

        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        dataSource = new DruidDataSource();
        dataSource.setDriver(driver);
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeoutMillis(1000 * 180);
        dataSource.setLogAbandoned(true);
        dataSource.setTimeBetweenEvictionRunsMillis(10);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000);
        dataSource.setMaxActive(20);
        dataSource.setUrl("jdbc:mock:TestConnectError");

    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_connect_error() throws Exception {
        Assert.assertEquals(0, dataSource.getCreateErrorCount());

        int count = 10;
        Connection[] connections = new Connection[count];
        for (int i = 0; i < count; ++i) {
            connections[i] = dataSource.getConnection();
        }

        for (int i = 0; i < count; ++i) {
            connections[i].close();
        }

        Assert.assertEquals(10, dataSource.getCreateErrorCount());
    }
}
