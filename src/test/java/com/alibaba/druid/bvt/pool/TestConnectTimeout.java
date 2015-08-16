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
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class TestConnectTimeout extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUsername("xxx1");
        dataSource.setPassword("ppp");
        dataSource.setUrl("jdbc:mock:xx");
        dataSource.setFilters("stat");
        dataSource.setMaxOpenPreparedStatements(30);
        dataSource.setMaxActive(4);
        dataSource.setMaxWait(1000);
        dataSource.setMinIdle(0);
        dataSource.setInitialSize(1);
        dataSource.init();
    }

    public void testConnectTimeout() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            conn.close();
            dataSource.shrink();
            Assert.assertEquals(0, dataSource.getPoolingCount());
        }

        final List<Connection> connections = new ArrayList<Connection>();
        for (int i = 0; i < 3; ++i) {
            Connection conn = dataSource.getConnection();
            connections.add(conn);
        }

        final AtomicLong errorCount = new AtomicLong();
        final int THREAD_COUNT = 10;
        final CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        for (int i = 0; i < THREAD_COUNT; ++i) {
            Thread thread = new Thread() {

                public void run() {
                    try {
                        for (int i = 0; i < 100; ++i) {
                            Connection conn = dataSource.getConnection();
                            Thread.sleep(1);
                            conn.close();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        errorCount.incrementAndGet();
                    } finally {
                        latch.countDown();
                    }
                }
            };
            thread.start();
        }

        latch.await();
        Assert.assertEquals(0, errorCount.get());
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }
}
