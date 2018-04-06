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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.NumberFormat;
import java.util.concurrent.CountDownLatch;

import javax.sql.DataSource;

import junit.framework.TestCase;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.util.Assert;

import com.alibaba.druid.TestUtil;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class Oracle_Case4 extends TestCase {

    private String  jdbcUrl;
    private String  user;
    private String  password;
    private String  driverClass;
    private int     maxIdle                    = 40;
    private int     maxActive                  = 50;
    private int     maxWait                    = 5000;
    private String  validationQuery            = "SELECT 1 FROM DUAL";
    private int     threadCount                = 1;
    private int     loopCount                  = 5;
    final int       LOOP_COUNT                 = 1000 * 1;
    private boolean testOnBorrow               = false;
    private boolean preparedStatementCache     = true;
    private int     preparedStatementCacheSize = 50;
    private String  properties = "defaultRowPrefetch=50";

    private String  SQL;

    protected void setUp() throws Exception {
//         jdbcUrl = "jdbc:oracle:thin:@a.b.c.d:1521:ocnauto";
//         user = "alibaba";
//         password = "ccbuauto";
//         SQL = "SELECT * FROM WP_ORDERS WHERE ID = ?";

        jdbcUrl = "jdbc:oracle:thin:@a.b.c.d:1521:ointest3";
        user = "alibaba";
        password = "deYcR7facWSJtCuDpm2r";
        SQL = "SELECT * FROM AV_INFO WHERE ID = ?";

        driverClass = "oracle.jdbc.driver.OracleDriver";
    }

    public void test_druid() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();

        dataSource.setMaxActive(maxActive);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setMaxWait(maxWait);
        dataSource.setPoolPreparedStatements(preparedStatementCache);
        dataSource.setMaxOpenPreparedStatements(preparedStatementCacheSize);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setConnectionProperties(properties);
        dataSource.setUseOracleImplicitCache(true);
        dataSource.init();

        // printAV_INFO(dataSource);
        // printTables(dataSource);
        // printWP_ORDERS(dataSource);

        for (int i = 0; i < loopCount; ++i) {
            p0(dataSource, "druid", threadCount);
        }
        System.out.println();
    }

    public void test_dbcp() throws Exception {
        final BasicDataSource dataSource = new BasicDataSource();

        dataSource.setMaxActive(maxActive);
        dataSource.setMaxIdle(maxIdle);
        dataSource.setMaxWait(maxWait);
        dataSource.setPoolPreparedStatements(preparedStatementCache);
        dataSource.setMaxOpenPreparedStatements(preparedStatementCacheSize);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery(validationQuery);
        dataSource.setTestOnBorrow(testOnBorrow);
        dataSource.setConnectionProperties(properties);
        
//        printAV_INFO(dataSource);

        for (int i = 0; i < loopCount; ++i) {
            p0(dataSource, "dbcp", threadCount);
        }
        System.out.println();
    }

    private void printWP_ORDERS(DruidDataSource dataSource) throws SQLException {
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT * FROM WP_ORDERS");

        JdbcUtils.printResultSet(rs);

        rs.close();
        stmt.close();
        conn.close();
    }

    private void printAV_INFO(DataSource dataSource) throws SQLException {
        String sql = "SELECT DISTINCT ID FROM AV_INFO WHERE ROWNUM <= 10";
        Connection conn = dataSource.getConnection();
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);

        JdbcUtils.printResultSet(rs);

        rs.close();
        stmt.close();
        conn.close();
    }

    protected void printTables(DruidDataSource dataSource) throws SQLException {
        Connection conn = dataSource.getConnection();

        ResultSet rs = conn.getMetaData().getTables(null, "ALIBABA", null, new String[] { "TABLE" });
        JdbcUtils.printResultSet(rs);
        rs.close();

        conn.close();
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
                            
                            int mod = i % 500;
                            
                            String sql = SQL; // + " AND ROWNUM <= " + (mod + 1);
                            PreparedStatement stmt = conn.prepareStatement(sql);
                            stmt.setInt(1, 61);
                            ResultSet rs = stmt.executeQuery();
                            int rowCount = 0;
                            while (rs.next()) {
                                rowCount++;
                            }
                            // Assert.isTrue(!rs.isClosed());
                            rs.close();
                            // Assert.isTrue(!stmt.isClosed());
                            stmt.close();
                            Assert.isTrue(stmt.isClosed());
                            conn.close();
                            Assert.isTrue(conn.isClosed());
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
