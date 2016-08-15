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
package com.alibaba.druid;

import java.lang.management.ManagementFactory;
import java.sql.Connection;
import java.util.concurrent.CountDownLatch;

import javax.management.ObjectName;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;

public class TestIdel3 extends TestCase {

    public void test_idle2() throws Exception {
        MockDriver driver = new MockDriver();

        final DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(14);
        dataSource.setMaxIdle(14);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(3 * 100); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(18 * 10); // 180 / 10
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat");

        ManagementFactory.getPlatformMBeanServer().registerMBean(dataSource,
                                                                 new ObjectName("com.alibaba:type=DataSource"));

        // 第一次创建连接
        {
            Assert.assertEquals(0, dataSource.getCreateCount());
            Assert.assertEquals(0, dataSource.getActiveCount());

            Connection conn = dataSource.getConnection();

            Assert.assertEquals(dataSource.getInitialSize(), dataSource.getCreateCount());
            Assert.assertEquals(dataSource.getInitialSize(), driver.getConnections().size());
            Assert.assertEquals(1, dataSource.getActiveCount());

            conn.close();
            Assert.assertEquals(0, dataSource.getDestroyCount());
            Assert.assertEquals(2, driver.getConnections().size());
            Assert.assertEquals(2, dataSource.getCreateCount());
            Assert.assertEquals(0, dataSource.getActiveCount());
        }

        {
            // 并发创建14个
            int count = 14;
            Connection[] connections = new Connection[count];
            for (int i = 0; i < count; ++i) {
                connections[i] = dataSource.getConnection();
                Assert.assertEquals(i + 1, dataSource.getActiveCount());
            }

            Assert.assertEquals(dataSource.getMaxActive(), dataSource.getCreateCount());
            Assert.assertEquals(count, driver.getConnections().size());

            // 全部关闭
            for (int i = 0; i < count; ++i) {
                connections[i].close();
                Assert.assertEquals(count - i - 1, dataSource.getActiveCount());
            }

            Assert.assertEquals(dataSource.getMaxActive(), dataSource.getCreateCount());
            Assert.assertEquals(0, dataSource.getActiveCount());
            Assert.assertEquals(14, driver.getConnections().size());
        }

        concurrent(dataSource, 100, 1000);
        concurrent(dataSource, 1, 1000 * 1000);
        Thread.sleep(1000 * 10);
        concurrent(dataSource, 1, 1000 * 1000);
        Thread.sleep(1000 * 10);
        concurrent(dataSource, 1000, 1000 * 1000);

        Assert.assertEquals(driver.getConnections().size(), dataSource.getPoolingCount());
        Assert.assertEquals(0, dataSource.getActiveCount());

        // 连续打开关闭单个连接
        for (int i = 0; i < 1000; ++i) {
            Assert.assertEquals(0, dataSource.getActiveCount());
            Connection conn = dataSource.getConnection();

            Assert.assertEquals(1, dataSource.getActiveCount());

            Thread.sleep(10);
            conn.close();
        }
        // Assert.assertEquals(2, dataSource.getPoolingCount());

        Thread.sleep(1000 * 100);
        dataSource.close();

    }

    private void concurrent(final DruidDataSource dataSource, int threadCount, final int loopCount)
                                                                                                   throws InterruptedException {

        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; ++i) {
            threads[i] = new Thread("thread-" + i) {

                public void run() {
                    try {
                        startLatch.await();
                        for (int i = 0; i < loopCount; ++i) {
                            Connection conn = null;

                            conn = dataSource.getConnection();
                            conn.close();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
        }

        for (int i = 0; i < threadCount; ++i) {
            threads[i].start();
        }
        startLatch.countDown();
        System.out.println("concurrent start...");
        endLatch.await();
        System.out.println("concurrent end");
    }
}
