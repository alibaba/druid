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

import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledResultSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLFeatureNotSupportedException;

import static org.junit.jupiter.api.Assertions.*;

public class DruidPooledResultSetTest {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void testWrap() throws Exception {
        String sql = "select ?";
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "xxx");
        ResultSet rs = stmt.executeQuery();

        ResultSet raw = rs.unwrap(ResultSet.class);

        assertTrue(raw instanceof MockResultSet);

        rs.close();

        conn.close();
    }

    @Test
    public void test_notSupport() throws Exception {
        String sql = "select ?";
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "xxx");
        DruidPooledResultSet rs = (DruidPooledResultSet) stmt.executeQuery();

        Exception error = null;
        try {
            rs.getObject(1, String.class);
        } catch (SQLFeatureNotSupportedException e) {
            error = e;
        }
        assertNotNull(error);

        rs.close();

        conn.close();
    }

    @Test
    public void test_notSupport_1() throws Exception {
        String sql = "select ?";
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "xxx");
        DruidPooledResultSet rs = (DruidPooledResultSet) stmt.executeQuery();

        Exception error = null;
        try {
            rs.getObject("1", String.class);
        } catch (SQLFeatureNotSupportedException e) {
            error = e;
        }
        assertNotNull(error);

        rs.close();

        conn.close();
    }

    @Test
    public void test_rowCount() throws Exception {
        String sql = "select ?";
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "xxx");
        DruidPooledResultSet rs = (DruidPooledResultSet) stmt.executeQuery();

        assertEquals(true, rs.next());
        assertEquals(false, rs.next());

        assertEquals(1, rs.getFetchRowCount());

        assertEquals(true, rs.previous());
        assertEquals(false, rs.previous());

        assertEquals(1, rs.getFetchRowCount());

        assertEquals(true, rs.next());
        assertEquals(false, rs.next());

        assertEquals(1, rs.getFetchRowCount());

        assertFalse(rs.rowUpdated());

        rs.close();

        conn.close();
    }
}
