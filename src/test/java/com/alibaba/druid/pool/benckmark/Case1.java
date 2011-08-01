/*
 * Copyright 1999-2011 Alibaba Group Holding Ltd.
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
package com.alibaba.druid.pool.benckmark;

import java.sql.Connection;
import java.text.NumberFormat;
import java.util.concurrent.CountDownLatch;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;

import com.alibaba.druid.TestUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.jolbox.bonecp.BoneCPDataSource;

/**
 * TestOnBo 类Case1.java的实现描述：TODO 类实现描述
 * 
 * @author admin 2011-5-28 下午03:47:40
 */
public class Case1 extends TestCase {

    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass;
    private int    initialSize     = 10;
    private int    minPoolSize     = 10;
    private int    maxPoolSize     = 50;
    private int    maxActive       = 50;
    private String validationQuery = "SELECT 1";
    private int    threadCount     = 40;
    private int    loopCount       = 4;
    final int      LOOP_COUNT      = 1000 * 10;

    protected void setUp() throws Exception {
        jdbcUrl = "jdbc:fake:dragoon_v25masterdb";
        user = "dragoon25";
        password = "dragoon25";
        driverClass = "com.alibaba.druid.mock.MockDriver";
    }

    public void test_0() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minPoolSize);
        dataSource.setMaxIdle(maxPoolSize);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(false);

        for (int i = 0; i < loopCount; ++i) {
            p0(dataSource, "druid", threadCount);
        }
        System.out.println();
    }

    public void ftest_1() throws Exception {
        final BasicDataSource dataSource = new BasicDataSource();

        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minPoolSize);
        dataSource.setMaxIdle(maxPoolSize);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(false);

        for (int i = 0; i < loopCount; ++i) {
            p0(dataSource, "dbcp", threadCount);
        }
        System.out.println();
    }

    public void test_2() throws Exception {
        BoneCPDataSource dataSource = new BoneCPDataSource();
        // dataSource.(10);
        // dataSource.setMaxActive(50);
        dataSource.setMinConnectionsPerPartition(minPoolSize);
        dataSource.setMaxConnectionsPerPartition(maxPoolSize);

        dataSource.setDriverClass(driverClass);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setStatementsCacheSize(100);
        // dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        // dataSource.setConnectionTestStatement("SELECT 1");
        dataSource.setPartitionCount(1);
        dataSource.setAcquireIncrement(5);
        dataSource.setIdleConnectionTestPeriod(0L);
        // dataSource.setDisableConnectionTracking(true);

        for (int i = 0; i < loopCount; ++i) {
            p0(dataSource, "boneCP", threadCount);
        }
        System.out.println();
    }

    private void p0(final DataSource dataSource, String name, int threadCount) throws Exception {

        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; ++i) {
            Thread thread = new Thread() {

                public void run() {
                    try {
                        startLatch.await();

                        for (int i = 0; i < LOOP_COUNT; ++i) {
                            Connection conn = dataSource.getConnection();
                            conn.close();
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                    endLatch.countDown();
                }
            };
            thread.start();
        }
        long startMillis = System.currentTimeMillis();
        long startYGC = TestUtil.getYoungGC();
        long startFullGC = TestUtil.getFullGC();
        startLatch.countDown();
        endLatch.await();

        long millis = System.currentTimeMillis() - startMillis;
        long ygc = TestUtil.getYoungGC() - startYGC;
        long fullGC = TestUtil.getFullGC() - startFullGC;

        System.out.println("thread " + threadCount + " " + name + " millis : "
                           + NumberFormat.getInstance().format(millis) + ", YGC " + ygc + " FGC " + fullGC);
    }
}
