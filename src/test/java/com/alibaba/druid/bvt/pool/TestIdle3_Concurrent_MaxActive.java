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
import java.util.Random;
import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestIdle3_Concurrent_MaxActive extends TestCase {

    private MockDriver      driver;
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();

        driver = new MockDriver();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(14);
        dataSource.setMaxIdle(14);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat");

    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_idle2() throws Exception {
        // first connect
        {
            Assert.assertEquals(0, dataSource.getCreateCount());
            Assert.assertEquals(0, dataSource.getActiveCount());

            Connection conn = dataSource.getConnection();

            Assert.assertEquals(dataSource.getInitialSize(), dataSource.getCreateCount());
            Assert.assertEquals(dataSource.getInitialSize(), driver.getConnections().size());
            Assert.assertEquals(1, dataSource.getActiveCount());

            conn.close();
            Assert.assertEquals(0, dataSource.getDestroyCount());
            Assert.assertEquals(1, driver.getConnections().size());
            Assert.assertEquals(1, dataSource.getCreateCount());
            Assert.assertEquals(0, dataSource.getActiveCount());
        }

        for (int i = 0; i < 10; ++i) {
            concurrent(200);
            Assert.assertEquals(dataSource.getMaxActive(), dataSource.getPoolingCount());
            dataSource.shrink();
            Assert.assertEquals(dataSource.getMinIdle(), dataSource.getPoolingCount());
        }

        // 连续打开关闭单个连接
        for (int i = 0; i < 10; ++i) {
            Assert.assertEquals(0, dataSource.getActiveCount());
            Connection conn = dataSource.getConnection();

            Assert.assertEquals(1, dataSource.getActiveCount());
            conn.close();
        }
        // Assert.assertEquals(2, dataSource.getPoolingCount());

    }

    private void concurrent(final int threadCount) throws Exception {
        Thread[] threads = new Thread[threadCount];
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);

        for (int i = 0; i < threadCount; ++i) {
            threads[i] = new Thread("thread-" + i) {

                public void run() {
                    try {
                        startLatch.await();
                        Connection conn = dataSource.getConnection();
                        long millis = new Random().nextInt(5) + 10;
                        Thread.sleep(millis);
                        conn.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
        }

        startLatch.countDown();
        for (int i = 0; i < threadCount; ++i) {
            threads[i].start();
        }

        endLatch.await();

        // int max = count > dataSource.getMaxActive() ? dataSource.getMaxActive() : count;
        // Assert.assertEquals(max, driver.getConnections().size());
    }
}
