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
package com.alibaba.druid.benckmark.pool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;

import javax.sql.DataSource;

import org.junit.Assert;
import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;

import com.alibaba.druid.TestUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.jolbox.bonecp.BoneCPDataSource;

public class Case3 extends TestCase {

    private String  jdbcUrl;
    private String  user;
    private String  password;
    private String  driverClass;
    private int     initialSize                   = 1;
    private int     minIdle                       = 1;
    private int     maxIdle                       = 14;
    private int     maxActive                     = 14;
    private int     maxWait                       = -1;
    private String  validationQuery               = "SELECT 1"; // "SELECT 1";
    private int     threadCount                   = 10;
    private int     TEST_COUNT                    = 3;
    final int       LOOP_COUNT                    = 1000 * 100;
    private boolean testOnBorrow                  = false;
    private String  connectionProperties          = "";        // "bigStringTryClob=true;clientEncoding=GBK;defaultRowPrefetch=50;serverEncoding=ISO-8859-1";
    private String  sql                           = "SELECT 1";
    private long    timeBetweenEvictionRunsMillis = 60000;
    private long    minEvictableIdleTimeMillis    = 60000;

    protected void setUp() throws Exception {
        jdbcUrl = "jdbc:fake:dragoon_v25masterdb";
        user = "dragoon25";
        password = "dragoon25";
        driverClass = "com.alibaba.druid.mock.MockDriver";
        // connectionProperties = "connectSleep=3;executeSleep=1";

        // jdbcUrl = "jdbc:mysql://a.b.c.d:3306/druid2";
        // user = "root";
        // password = "root";
        // driverClass = "com.mysql.jdbc.Driver";
    }

    public void test_perf() throws Exception {
        for (int i = 0; i < 5; ++i) {
            druid();
            dbcp();
            // boneCP();
        }
    }

    public void boneCP() throws Exception {
        BoneCPDataSource dataSource = new BoneCPDataSource();
        // dataSource.(10);
        // dataSource.setMaxActive(50);
        dataSource.setMinConnectionsPerPartition(minIdle);
        dataSource.setMaxConnectionsPerPartition(maxIdle);

        dataSource.setDriverClass(driverClass);
        dataSource.setJdbcUrl(jdbcUrl);
        // dataSource.setPoolPreparedStatements(true);
        // dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setConnectionTestStatement(validationQuery);
        dataSource.setPartitionCount(1);
        Properties connectionProperties = new Properties();
        connectionProperties.put("connectSleep", "3");
        connectionProperties.put("executeSleep", "1");
        dataSource.setDriverProperties(connectionProperties);

        for (int i = 0; i < TEST_COUNT; ++i) {
            p0(dataSource, "boneCP", threadCount);
        }
        System.out.println();
    }

    public void druid() throws Exception {

        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setMaxWait(maxWait);
        dataSource.setMinIdle(minIdle);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setConnectionProperties(connectionProperties);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);

        for (int i = 0; i < TEST_COUNT; ++i) {
            p0(dataSource, "druid", threadCount);
        }
        dataSource.close();
        System.out.println();
    }

    public void dbcp() throws Exception {
        final BasicDataSource dataSource = new BasicDataSource();

        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setMaxWait(maxWait);
        dataSource.setMinIdle(minIdle);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setConnectionProperties(connectionProperties);
        dataSource.setTimeBetweenEvictionRunsMillis(timeBetweenEvictionRunsMillis);
        dataSource.setMinEvictableIdleTimeMillis(minEvictableIdleTimeMillis);

        for (int i = 0; i < TEST_COUNT; ++i) {
            p0(dataSource, "dbcp", threadCount);
        }
        // dataSource.close();
        System.out.println();
    }

    private void p0(final DataSource dataSource, String name, int threadCount) throws Exception {
        final AtomicInteger count = new AtomicInteger();
        final AtomicInteger errorCount = new AtomicInteger();

        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        for (int i = 0; i < threadCount; ++i) {
            Thread thread = new Thread() {

                public void run() {
                    try {
                        startLatch.await();

                        for (int i = 0; i < LOOP_COUNT; ++i) {
                            Connection conn = dataSource.getConnection();
                            Statement stmt = conn.createStatement();
                            ResultSet rs = stmt.executeQuery(sql);
                            while (rs.next()) {
                                rs.getInt(1);
                            }
                            rs.close();
                            stmt.close();

                            conn.close();
                            count.incrementAndGet();
                        }
                    } catch (Throwable ex) {
                        errorCount.incrementAndGet();
                        ex.printStackTrace();
                    } finally {
                        endLatch.countDown();
                    }
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

        Assert.assertEquals(LOOP_COUNT * threadCount, count.get());
        Thread.sleep(1);

        System.out.println("thread " + threadCount + " " + name + " millis : "
                           + NumberFormat.getInstance().format(millis) + ", YGC " + ygc + " FGC " + fullGC);
    }
}
