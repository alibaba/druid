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
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestConcurrent extends TestCase {

    private MockDriver      driver;
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();

        driver = new MockDriver();
        driver.setLogExecuteQueryEnable(false);

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(100);
        dataSource.setMaxIdle(100);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(10 * 1000);
        dataSource.setTimeBetweenEvictionRunsMillis(1 * 10);
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat");

        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertEquals(0, dataSource.getPoolingCount());
        Assert.assertEquals(true, dataSource.isEnable());
    }

    protected void tearDown() throws Exception {
        dataSource.close();

        int size = DruidDataSourceStatManager.getInstance().getDataSourceList().size();
        if (size > 0) {
            for (DruidDataSource dataSource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
                dataSource.close();
                System.out.println("unclosed datasource : " + dataSource.getObjectName() + ", url : " + dataSource.getUrl());
            }
            Assert.fail("size : " + size);
        }
    }

    public void test_0() throws Exception {
        // 第一次建立连接
        {
            Assert.assertEquals(0, dataSource.getActiveCount());
            Assert.assertEquals(0, dataSource.getPoolingCount());
            Assert.assertEquals(true, dataSource.isEnable());

            Connection conn = dataSource.getConnection();

            Assert.assertEquals(1, dataSource.getActiveCount());
            Assert.assertEquals(0, dataSource.getPoolingCount());
            Assert.assertEquals(true, dataSource.isEnable());

            conn.close();

            Assert.assertEquals(0, dataSource.getActiveCount());
            Assert.assertEquals(1, dataSource.getPoolingCount());
        }

        Assert.assertEquals(true, dataSource.isEnable());

        // 连续打开关闭单个连接
        for (int i = 0; i < 1000; ++i) {
            Assert.assertEquals(0, dataSource.getActiveCount());
            Connection conn = dataSource.getConnection();

            Assert.assertEquals(1, dataSource.getActiveCount());

            conn.close();

            Assert.assertEquals(0, dataSource.getActiveCount());
            Assert.assertEquals(1, dataSource.getPoolingCount());
            Assert.assertEquals(true, dataSource.isEnable());
        }

        // 使用单个线程模拟并发打开10个连接
        for (int i = 0; i < 1000 * 1; ++i) {
            final int COUNT = 10;
            Connection[] connections = new Connection[COUNT];

            for (int j = 0; j < connections.length; ++j) {
                connections[j] = dataSource.getConnection();

                Assert.assertEquals(j + 1, dataSource.getActiveCount());
            }

            Assert.assertEquals(0, dataSource.getDestroyCount());
            Assert.assertEquals(COUNT, dataSource.getActiveCount());
            Assert.assertEquals(COUNT, dataSource.getCreateCount());
            Assert.assertEquals(0, dataSource.getPoolingCount());

            for (int j = 0; j < connections.length; ++j) {
                connections[j].close();
                Assert.assertEquals(j + 1, dataSource.getPoolingCount());
            }

            Assert.assertEquals(0, dataSource.getActiveCount());
            Assert.assertEquals(COUNT, dataSource.getPoolingCount());
        }

        // 2个并发
        for (int i = 0; i < 3; ++i) {
            concurrent(2);
        }

        // 5个并发
        for (int i = 0; i < 3; ++i) {
            concurrent(5);
        }

        // 10并发
        for (int i = 0; i < 3; ++i) {
            concurrent(10);
        }

        // 20并发
        for (int i = 0; i < 3; ++i) {
            concurrent(20);
        }

        // 50并发
        for (int i = 0; i < 3; ++i) {
            concurrent(50);
        }

        // 100并发
        for (int i = 0; i < 3; ++i) {
            concurrent(100);
        }
    }

    /**
     * 并发执行10000次
     * 
     * @param threadCount
     * @throws InterruptedException
     */
    private void concurrent(final int threadCount) throws InterruptedException {
        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; ++i) {
            threads[i] = new Thread() {

                public void run() {
                    try {
                        startLatch.await();

                        for (int i = 0; i < 1000 * 1; ++i) {
                            Connection conn = dataSource.getConnection();
                            Statement stmt = conn.createStatement();
                            ResultSet rs = stmt.executeQuery("SELECT 1");
                            rs.close();
                            stmt.close();
                            conn.close();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
            threads[i].start();
        }

        dataSource.shrink();
        Assert.assertEquals("actveCount != 0", 0, dataSource.getActiveCount());
        Assert.assertEquals("minIdle != poolingCount", dataSource.getMinIdle(), dataSource.getPoolingCount());

        System.out.println(threadCount + "-threads start");
        startLatch.countDown();
        endLatch.await();
        System.out.println(threadCount + "-threads complete");

        Assert.assertEquals(0, dataSource.getActiveCount());
        Assert.assertTrue(threadCount >= dataSource.getPoolingCount());
    }
}
