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
import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestActiveTrace2 extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();

        dataSource = new DruidDataSource();
        dataSource.setRemoveAbandoned(true);
        dataSource.setRemoveAbandonedTimeoutMillis(100);
        dataSource.setLogAbandoned(true);
        dataSource.setTimeBetweenEvictionRunsMillis(10);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000);
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setMaxActive(4);
        dataSource.setInitialSize(4);
        dataSource.init();
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_activeTrace() throws Exception {
        int threadCount = 8;
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; ++i) {
            Thread thread = new Thread() {
                public void run() {
                    try {
                        task();
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
            threads[i] = thread;
            thread.start();
        }
        endLatch.await();
    }

    private void task() throws Exception {
        int count = 1000;
        int i = 0;
        try {
            for (; i < count; ++i) {
                Connection conn = dataSource.getConnection();
                Assert.assertNotNull(conn);
                Thread.sleep(0);
                conn.close();
                Assert.assertTrue(conn.isClosed());
            }
        } finally {
            Assert.assertEquals(count, i);
        }
    }
}
