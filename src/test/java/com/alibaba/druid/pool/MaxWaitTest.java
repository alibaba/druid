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
package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

import EDU.oswego.cs.dl.util.concurrent.CountDown;
import junit.framework.TestCase;

public class MaxWaitTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://localhost:3306/sonar");
        dataSource.setUsername("sonar");
        dataSource.setPassword("sonar");
        dataSource.setFilters("stat");
        dataSource.setMaxWait(1000);
        dataSource.setInitialSize(3);
        dataSource.setMinIdle(3);
        dataSource.setMaxActive(3);
    }

    public void test_maxWait() throws Exception {
        final CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 20; ++i) {
            Thread thread = new Thread() {

                public void run() {
                    try {
                        for (int i = 0; i < 10; ++i) {
                            try {
                                Connection conn = dataSource.getConnection();
                                Statement stmt = conn.createStatement();
                                stmt.execute("select sleep(" + (i % 5 + 1) + ")");
                                stmt.close();
                                conn.close();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    } finally {
                        latch.countDown();
                    }
                }
            };
            thread.start();
        }

        latch.await();
    }
}
