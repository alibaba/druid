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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import com.alibaba.druid.PoolTestCase;
import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class Case0 extends PoolTestCase {

    public void test_0() throws Exception {

        final DruidDataSource dataSource = new DruidDataSource();

        dataSource.setDriver(new MockDriver() {

        });
        dataSource.setUrl("jdbc:mock:");

        dataSource.setMinIdle(0);
        dataSource.setMaxActive(2);
        dataSource.setMaxIdle(2);

        Connection conn1 = dataSource.getConnection();
        Connection conn2 = dataSource.getConnection();

        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch completeLatch = new CountDownLatch(1);
        final AtomicInteger waitCount = new AtomicInteger();
        Thread t = new Thread() {

            public void run() {
                try {
                    startLatch.countDown();
                    waitCount.incrementAndGet();
                    Connection conn = dataSource.getConnection();
                    waitCount.decrementAndGet();
                    conn.close();

                    completeLatch.countDown();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        t.start();

        startLatch.await();
        Assert.assertFalse(completeLatch.await(1, TimeUnit.SECONDS));
        conn1.close();
        Assert.assertTrue(completeLatch.await(1, TimeUnit.SECONDS));
        conn2.close();
        Assert.assertTrue(completeLatch.await(1, TimeUnit.SECONDS));

        dataSource.close();
    }
}
