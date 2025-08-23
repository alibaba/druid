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
package com.alibaba.druid.bvt.mock;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;


public class MockExecuteTest extends PoolTestCase {
    public void test_0() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mock:");
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT 2");
        assertTrue(rs.next());
        assertEquals(2, rs.getInt(1));
        rs.close();

        stmt.close();
        conn.close();
    }

    public void test_1() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mock:");
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT NULL");
        assertTrue(rs.next());
        assertEquals(0, rs.getInt(1));
        assertEquals(null, rs.getObject(1));
        rs.close();

        stmt.close();
        conn.close();
    }

    public void test_2() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mock:");
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT NOW()");
        assertTrue(rs.next());
        assertTrue(rs.getObject(1) instanceof Timestamp);
        rs.close();

        stmt.close();
        conn.close();
    }

    public void test_3() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mock:");
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT NOW() FROM DUAL");
        assertTrue(rs.next());
        assertTrue(rs.getObject(1) instanceof Timestamp);
        rs.close();

        stmt.close();
        conn.close();
    }

    public void test_4() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mock:");
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT 'ABCDE' FROM DUAL");
        assertTrue(rs.next());
        assertEquals("ABCDE", rs.getString(1));
        rs.close();

        stmt.close();
        conn.close();
    }

    public void test_5() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mock:");
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT true FROM DUAL");
        assertTrue(rs.next());
        assertEquals(true, rs.getBoolean(1));

        rs.close();

        stmt.close();
        conn.close();
    }

    public void test_6() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:mock:");
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery("SELECT false FROM DUAL");
        assertTrue(rs.next());
        assertEquals(false, rs.getBoolean(1));

        rs.close();

        stmt.close();
        conn.close();
    }
}
