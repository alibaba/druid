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
package com.alibaba.druid.benckmark.pool;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.text.NumberFormat;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicLong;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;
import org.logicalcobwebs.proxool.ProxoolDataSource;

import com.alibaba.druid.TestUtil;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.jolbox.bonecp.BoneCPDataSource;
import com.mchange.v2.c3p0.ComboPooledDataSource;
import com.taobao.datasource.LocalTxDataSourceDO;
import com.taobao.datasource.TaobaoDataSourceFactory;
import com.taobao.datasource.resource.adapter.jdbc.local.LocalTxDataSource;

/**
 * TestOnBo 类Case1.java的实现描述：TODO 类实现描述
 * 
 * @author admin 2011-5-28 下午03:47:40
 */
public class Case1 extends TestCase {

    private String            jdbcUrl;
    private String            user;
    private String            password;
    private String            driverClass;
    private int               initialSize      = 10;
    private int               minPoolSize      = 10;
    private int               maxPoolSize      = 50;
    private int               maxActive        = 50;
    private String            validationQuery  = "SELECT 1";
    private int               threadCount      = 5;
    private int               loopCount        = 10;
    final int                 LOOP_COUNT       = 1000 * 1 * 1 / threadCount;

    private static AtomicLong physicalConnStat = new AtomicLong();

    public static class TestDriver extends MockDriver {

        public static TestDriver instance = new TestDriver();

        public boolean acceptsURL(String url) throws SQLException {
            if (url.startsWith("jdbc:test:")) {
                return true;
            }
            return super.acceptsURL(url);
        }

        public Connection connect(String url, Properties info) throws SQLException {
            physicalConnStat.incrementAndGet();
            return super.connect("jdbc:mock:case1", info);
        }
    }

    protected void setUp() throws Exception {
        DriverManager.registerDriver(TestDriver.instance);

        user = "dragoon25";
        password = "dragoon25";

        // jdbcUrl = "jdbc:h2:mem:";
        // driverClass = "org.h2.Driver";
        jdbcUrl = "jdbc:test:case1:";
        driverClass = "com.alibaba.druid.benckmark.pool.Case1$TestDriver";

        physicalConnStat.set(0);
    }

    public void test_druid() throws Exception {
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

    public void test_jobss() throws Exception {
        LocalTxDataSourceDO dataSourceDO = new LocalTxDataSourceDO();
        dataSourceDO.setBlockingTimeoutMillis(1000 * 60);
        dataSourceDO.setMaxPoolSize(maxPoolSize);
        dataSourceDO.setMinPoolSize(minPoolSize);

        dataSourceDO.setDriverClass(driverClass);
        dataSourceDO.setConnectionURL(jdbcUrl);
        dataSourceDO.setUserName(user);
        dataSourceDO.setPassword(password);

        LocalTxDataSource tx = TaobaoDataSourceFactory.createLocalTxDataSource(dataSourceDO);
        DataSource dataSource = tx.getDatasource();

        for (int i = 0; i < loopCount; ++i) {
            p0(dataSource, "jboss-datasource", threadCount);
        }
        System.out.println();
    }

    public void test_dbcp() throws Exception {
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

    public void test_bonecp() throws Exception {
        BoneCPDataSource dataSource = new BoneCPDataSource();
        // dataSource.(10);
        // dataSource.setMaxActive(50);
        dataSource.setMinConnectionsPerPartition(minPoolSize);
        dataSource.setMaxConnectionsPerPartition(maxPoolSize);

        dataSource.setDriverClass(driverClass);
        dataSource.setJdbcUrl(jdbcUrl);
        dataSource.setStatementsCacheSize(100);
        dataSource.setServiceOrder("LIFO");
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

    public void test_c3p0() throws Exception {
        ComboPooledDataSource dataSource = new ComboPooledDataSource();
        // dataSource.(10);
        // dataSource.setMaxActive(50);
        dataSource.setMinPoolSize(minPoolSize);
        dataSource.setMaxPoolSize(maxPoolSize);

        dataSource.setDriverClass(driverClass);
        dataSource.setJdbcUrl(jdbcUrl);
        // dataSource.setPoolPreparedStatements(true);
        // dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setUser(user);
        dataSource.setPassword(password);

        for (int i = 0; i < loopCount; ++i) {
            p0(dataSource, "c3p0", threadCount);
        }
        System.out.println();
    }

    public void test_proxool() throws Exception {
        ProxoolDataSource dataSource = new ProxoolDataSource();
        // dataSource.(10);
        // dataSource.setMaxActive(50);
        dataSource.setMinimumConnectionCount(minPoolSize);
        dataSource.setMaximumConnectionCount(maxPoolSize);

        dataSource.setDriver(driverClass);
        dataSource.setDriverUrl(jdbcUrl);
        // dataSource.setPoolPreparedStatements(true);
        // dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setUser(user);
        dataSource.setPassword(password);

        for (int i = 0; i < loopCount; ++i) {
            p0(dataSource, "proxool", threadCount);
        }
        System.out.println();
    }

    public void test_tomcat_jdbc() throws Exception {
        org.apache.tomcat.jdbc.pool.DataSource dataSource = new org.apache.tomcat.jdbc.pool.DataSource();
        // dataSource.(10);
        dataSource.setMaxIdle(maxPoolSize);
        dataSource.setMinIdle(minPoolSize);
        dataSource.setMaxActive(maxPoolSize);

        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        // dataSource.setPoolPreparedStatements(true);
        // dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        for (int i = 0; i < loopCount; ++i) {
            p0(dataSource, "tomcat-jdbc", threadCount);
        }
        System.out.println();
    }

    private void p0(final DataSource dataSource, String name, int threadCount) throws Exception {

        final CountDownLatch startLatch = new CountDownLatch(1);
        final CountDownLatch endLatch = new CountDownLatch(threadCount);
        final CountDownLatch dumpLatch = new CountDownLatch(1);

        Thread[] threads = new Thread[threadCount];
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

                    try {
                        dumpLatch.await();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            };
            threads[i] = thread;
            thread.start();
        }
        long startMillis = System.currentTimeMillis();
        long startYGC = TestUtil.getYoungGC();
        long startFullGC = TestUtil.getFullGC();
        startLatch.countDown();
        endLatch.await();

        long[] threadIdArray = new long[threads.length];
        for (int i = 0; i < threads.length; ++i) {
            threadIdArray[i] = threads[i].getId();
        }
        ThreadInfo[] threadInfoArray = ManagementFactory.getThreadMXBean().getThreadInfo(threadIdArray);

        dumpLatch.countDown();

        long blockedCount = 0;
        long waitedCount = 0;
        for (int i = 0; i < threadInfoArray.length; ++i) {
            ThreadInfo threadInfo = threadInfoArray[i];
            blockedCount += threadInfo.getBlockedCount();
            waitedCount += threadInfo.getWaitedCount();
        }

        long millis = System.currentTimeMillis() - startMillis;
        long ygc = TestUtil.getYoungGC() - startYGC;
        long fullGC = TestUtil.getFullGC() - startFullGC;

        System.out.println("thread " + threadCount + " " + name + " millis : "
                           + NumberFormat.getInstance().format(millis) + "; YGC " + ygc + " FGC " + fullGC
                           + " blocked "
                           + NumberFormat.getInstance().format(blockedCount) //
                           + " waited " + NumberFormat.getInstance().format(waitedCount) + " physicalConn "
                           + physicalConnStat.get());

    }
}
