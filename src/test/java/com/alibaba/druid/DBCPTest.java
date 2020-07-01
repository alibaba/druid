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
package com.alibaba.druid;

import java.sql.Connection;
import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;

import com.alibaba.druid.mock.MockDriver;

public class DBCPTest extends TestCase {

    public void test_max() throws Exception {
        Class.forName("com.alibaba.druid.mock.MockDriver");
        
        final BasicDataSource dataSource = new BasicDataSource();
//        final DruidDataSource dataSource = new DruidDataSource();
        dataSource.setInitialSize(3);
        dataSource.setMaxActive(20);
        dataSource.setMaxIdle(20);
        dataSource.setDriverClassName("com.alibaba.druid.mock.MockDriver");
        dataSource.setUrl("jdbc:mock:xxx");

        final int THREAD_COUNT = 200;
        final CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);
        final CountDownLatch startLatch = new CountDownLatch(1);
        Thread[] threads = new Thread[THREAD_COUNT];
        for (int i = 0; i < THREAD_COUNT; ++i) {
            threads[i] = new Thread() {

                public void run() {
                    try {
                        startLatch.await();
                        for (int i = 0; i < 1000; ++i) {
                            Connection conn = dataSource.getConnection();
                            Thread.sleep(1);
                            conn.close();
                        }
                    } catch (Exception e) {
                    } finally {
                        endLatch.countDown();
                    }
                }
            };
            threads[i].start();
        }
        
        startLatch.countDown();
        
        endLatch.await();
        
//        System.out.println(dataSource.getNumIdle());
        System.out.println(MockDriver.instance.getConnections().size());
        System.out.println(MockDriver.instance.getConnectionCloseCount());
    }
}
