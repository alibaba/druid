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
package com.alibaba.druid.bvt.mock;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import junit.framework.TestCase;

import org.junit.Assert;

public class MockExecuteTest extends TestCase {

    public void test_0() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mock:");
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT 2");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(2, rs.getInt(1));
        rs.close();

        stmt.close();
        conn.close();
    }

    public void test_1() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mock:");
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT NULL");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(0, rs.getInt(1));
        Assert.assertEquals(null, rs.getObject(1));
        rs.close();

        stmt.close();
        conn.close();
    }

    public void test_2() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mock:");
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT NOW()");
        Assert.assertTrue(rs.next());
        Assert.assertTrue(rs.getObject(1) instanceof Timestamp);
        rs.close();

        stmt.close();
        conn.close();
    }

    public void test_3() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mock:");
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT NOW() FROM DUAL");
        Assert.assertTrue(rs.next());
        Assert.assertTrue(rs.getObject(1) instanceof Timestamp);
        rs.close();

        stmt.close();
        conn.close();
    }

    public void test_4() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mock:");
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT 'ABCDE' FROM DUAL");
        Assert.assertTrue(rs.next());
        Assert.assertEquals("ABCDE", rs.getString(1));
        rs.close();

        stmt.close();
        conn.close();
    }

    public void test_5() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mock:");
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT true FROM DUAL");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(true, rs.getBoolean(1));

        rs.close();

        stmt.close();
        conn.close();
    }

    public void test_6() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mock:");
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT false FROM DUAL");
        Assert.assertTrue(rs.next());
        Assert.assertEquals(false, rs.getBoolean(1));

        rs.close();

        stmt.close();
        conn.close();
    }
}
