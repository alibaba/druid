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
package com.alibaba.druid.bvt.proxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.util.JdbcUtils;

public class BatchReadTest extends TestCase {

    private static String create_url = "jdbc:wrap-jdbc:filters=default,commonLogging,log4j:name=batchReadTest:jdbc:derby:memory:batchDB;create=true";

    protected void setUp() throws Exception {
        Class.forName("com.alibaba.druid.proxy.DruidDriver");

        Connection conn = DriverManager.getConnection(create_url);

        JdbcStatManager.getInstance();

        createTable();

        conn.close();
    }

    private void createTable() throws SQLException {
        Connection conn = DriverManager.getConnection(create_url);
        Statement stmt = conn.createStatement();
        stmt.execute("CREATE TABLE T_USER (ID INTEGER, NAME VARCHAR(50), BIRTHDATE TIMESTAMP)");
        stmt.close();
        conn.close();
    }

    private void dropTable() throws SQLException {
        Connection conn = DriverManager.getConnection(create_url);
        Statement stmt = conn.createStatement();
        stmt.execute("DROP TABLE T_USER");
        stmt.close();
        conn.close();
    }

    protected void tearDown() throws Exception {
        dropTable();
        
        DruidDriver.getProxyDataSources().clear();
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }

    public void test_stmt_batch() throws Exception {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(create_url);

            stmt = conn.createStatement();

            stmt.isClosed();
            stmt.isPoolable();

            stmt.addBatch("INSERT INTO T_USER (ID, NAME, BIRTHDATE) VALUES (1, 'A', NULL)");
            stmt.addBatch("INSERT INTO T_USER (ID, NAME, BIRTHDATE) VALUES (2, 'B', NULL)");
            stmt.executeBatch();

            for (;;) {
                boolean moreResults = stmt.getMoreResults();

                if (moreResults) {
                    rs = stmt.getResultSet();
                    JdbcUtils.printResultSet(rs, System.out);
                    JdbcUtils.close(rs);
                    continue;
                }

                int updateCount = stmt.getUpdateCount();
                if (updateCount == -1) {
                    break;
                }
            }

        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(conn);
        }
    }

    public void test_pstmt_batch() throws Exception {

        Connection conn = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(create_url);

            stmt = conn.createStatement();

            pstmt = conn.prepareStatement("INSERT INTO T_USER (ID, NAME, BIRTHDATE) VALUES (?, ?, ?)");

            pstmt.setInt(1, 1);
            pstmt.setString(2, "A");
            pstmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.addBatch();

            pstmt.setInt(1, 2);
            pstmt.setString(2, "B");
            pstmt.setTimestamp(3, new java.sql.Timestamp(System.currentTimeMillis()));
            pstmt.addBatch();

            int[] updateCounts = pstmt.executeBatch();

            Assert.assertArrayEquals(new int[] { 1, 1 }, updateCounts);

            pstmt.setFetchDirection(stmt.getFetchDirection());
            pstmt.setFetchSize(pstmt.getFetchSize());
            ResultSet keys = stmt.getGeneratedKeys();
            JdbcUtils.close(keys);

            // just call
            stmt.getConnection();
            stmt.setMaxFieldSize(stmt.getMaxFieldSize());
            stmt.setMaxRows(stmt.getMaxRows());
            stmt.getQueryTimeout();
            stmt.getResultSetConcurrency();
            stmt.getResultSetHoldability();
            stmt.getResultSetType();
            stmt.setPoolable(true);
            stmt.getWarnings();

            pstmt.getMetaData();
            pstmt.getParameterMetaData();
            pstmt.getWarnings();

            stmt.execute("SELECT * FROM T_USER");
            for (;;) {
                rs = stmt.getResultSet();

                rs.getWarnings();

                if (rs != null) {
                    JdbcUtils.printResultSet(rs, System.out);
                    JdbcUtils.close(rs);
                }

                if ((stmt.getMoreResults() == false) && (stmt.getUpdateCount() == -1)) {
                    break;
                }
            }
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(stmt);
            JdbcUtils.close(pstmt);
            JdbcUtils.close(conn);
        }
    }
}
