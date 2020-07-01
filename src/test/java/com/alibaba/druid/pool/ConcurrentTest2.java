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
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

public class ConcurrentTest2 extends TestCase {

    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass;
    private int    minPoolSize = 1;
    private int    maxPoolSize = 8;
    private int    maxActive   = 500;

    protected void setUp() throws Exception {
        // jdbcUrl =
        // "jdbc:mysql://a.b.c.d/dragoon_v25masterdb?useUnicode=true&characterEncoding=UTF-8";
        // user = "dragoon25";
        // password = "dragoon25";
        // driverClass = "com.mysql.jdbc.Driver";

        jdbcUrl = "jdbc:fake:dragoon_v25masterdb";
        user = "dragoon25";
        password = "dragoon25";
        driverClass = "com.alibaba.druid.mock.MockDriver";
    }

    public void test_concurrent_2() throws Exception {
        final DruidDataSource dataSource = new DruidDataSource();

        Class.forName("com.alibaba.druid.mock.MockDriver");

        dataSource.setInitialSize(10);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minPoolSize);
        dataSource.setMaxIdle(maxPoolSize);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setMaxWait(1000 * 60 * 1);

        final int THREAD_COUNT = 50;
        final int LOOP_COUNT = 1000 * 10;

        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);
        
        final CyclicBarrier barrier = new CyclicBarrier(5, new Runnable() {
            
            @Override
            public void run() {
                dataSource.shrink();
            }
        });

        for (int threadIndex = 0; threadIndex < THREAD_COUNT; ++threadIndex) {
            Thread thread = new Thread() {

                public void run() {
                    try {
                        startLatch.await();

                        for (int i = 0; i < LOOP_COUNT; ++i) {
                            Connection conn = dataSource.getConnection();
                            conn.close();
                            
                            barrier.await(10, TimeUnit.SECONDS);
                        }
                    } catch (Throwable e) {
                        e.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
            thread.start();
        }

        startLatch.countDown();
        endLatch.await();
        System.out.println("connectCount : " + dataSource.getConnectCount());

//        Assert.assertEquals(THREAD_COUNT * LOOP_COUNT, dataSource.getConnectCount());
//        Assert.assertEquals(THREAD_COUNT * LOOP_COUNT, dataSource.getCloseCount());
//        Assert.assertEquals(0, dataSource.getConnectErrorCount());
//        Assert.assertEquals(0, dataSource.getActiveCount());
    }
}
