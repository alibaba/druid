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
import java.sql.Statement;
import java.util.concurrent.CountDownLatch;

import junit.framework.TestCase;

public class TestMockPerf extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xx");
        dataSource.setInitialSize(3);
        dataSource.setMinIdle(3);
        dataSource.setMaxActive(20);
        dataSource.setDbType("mysql");
        dataSource.setFilters("stat");
        dataSource.init();
    }

    public void test_perf() throws Exception {
        final CountDownLatch latch = new CountDownLatch(10);
        for (int i = 0; i < 10; ++i) {
            Thread thread = new Thread() {
                public void run() {
                    try {
                        long startMillis = System.currentTimeMillis();
                        perf();
                        long millis = System.currentTimeMillis() - startMillis;
                        System.out.println("millis : " + millis);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        latch.countDown();
                    }
                }
            };
            thread.start();
     
        }
        
        latch.await();
    }

    public void perf() throws Exception {
        for (int i = 0; i < 1000 * 1000; ++i) {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("SELECT " + i % 1000);
            stmt.close();
            conn.close();
        }
    }
}
