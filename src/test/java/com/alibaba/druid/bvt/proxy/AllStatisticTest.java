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
package com.alibaba.druid.bvt.proxy;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.concurrent.atomic.AtomicLong;

import javax.management.openmbean.TabularData;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.proxy.DruidDriver;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.util.JdbcUtils;

public class AllStatisticTest extends TestCase {

    String             url              = "jdbc:wrap-jdbc:filters=default,commonLogging,log4j:name=statTest:jdbc:derby:classpath:petstore-db";

    private AtomicLong fetchRowCout     = new AtomicLong();

    Connection         globalConnection = null;

    protected void setUp() throws Exception {
        JdbcStatManager stat = JdbcStatManager.getInstance();

        stat.reset();

        Class.forName("com.alibaba.druid.proxy.DruidDriver");

        Connection conn = DriverManager.getConnection(url);

        int size = stat.getConnectionList().size();
        Assert.assertTrue(size >= 1);
        conn.close();

        TabularData connectionList = stat.getConnectionList();

        Assert.assertEquals(connectionList.size(), size - 1);

        stat.reset();

        globalConnection = DriverManager.getConnection(url);
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(globalConnection);
        DruidDriver.getProxyDataSources().clear();
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
    }

    public void test_stmt() throws Exception {

        // ////////////////////////

        f1();
        f2();
        f3();

    }

    public void f1() throws Exception {

        Statement pstmt = null;
        ResultSet rs = null;

        try {

            pstmt = globalConnection.createStatement();
            rs = pstmt.executeQuery("SELECT * FROM ITEM WHERE LISTPRICE > 10");
            while (rs.next()) {
                fetchRowCout.incrementAndGet();
                rs.getObject(1);
            }
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(pstmt);
        }
    }

    public void f2() throws Exception {

        Connection conn = null;
        Statement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(url);

            pstmt = conn.createStatement();
            rs = pstmt.executeQuery("SELECT * FROM ITxEM WHERE LISTPRICE > 10");
            while (rs.next()) {
                fetchRowCout.incrementAndGet();
                rs.getObject(1);
            }
        } catch (SQLException ex) {
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(pstmt);
            JdbcUtils.close(conn);
        }
    }

    public void f3() throws Exception {

        Connection conn = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            conn = DriverManager.getConnection(url);

            pstmt = conn.prepareStatement("SELECT * FROM ITEM WHERE LISTPRICE > ?");
            pstmt.setBigDecimal(1, new BigDecimal(10));
            rs = pstmt.executeQuery();
            while (rs.next()) {
                fetchRowCout.incrementAndGet();
                rs.getObject(1);
            }
        } finally {
            JdbcUtils.close(rs);
            JdbcUtils.close(pstmt);
            JdbcUtils.close(conn);
        }
    }
}
