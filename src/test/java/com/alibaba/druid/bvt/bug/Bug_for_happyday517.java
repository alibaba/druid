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
package com.alibaba.druid.bvt.bug;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import com.alibaba.druid.PoolTestCase;
import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class Bug_for_happyday517 extends PoolTestCase {

    private DruidDataSource dataSource;
    private MockDriver      driver;
    private int originalDataSourceCount = 0;

    protected void setUp() throws Exception {
        super.setUp();

        originalDataSourceCount = DruidDataSourceStatManager.getInstance().getDataSourceList().size();
        
        driver = new MockDriver();
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat,trace,log4j,encoding");

    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(originalDataSourceCount, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        super.tearDown();
    }

    public void test_for_happyday517_0() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        MockStatement mockStmt = stmt.unwrap(MockStatement.class);

        Assert.assertEquals(ResultSet.TYPE_SCROLL_SENSITIVE, mockStmt.getResultSetType());
        Assert.assertEquals(ResultSet.CONCUR_UPDATABLE, mockStmt.getResultSetConcurrency());

        stmt.close();

        conn.close();
    }

    public void test_for_happyday517_1() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE,
                                              ResultSet.CLOSE_CURSORS_AT_COMMIT);

        MockStatement mockStmt = stmt.unwrap(MockStatement.class);

        Assert.assertEquals(ResultSet.TYPE_SCROLL_SENSITIVE, mockStmt.getResultSetType());
        Assert.assertEquals(ResultSet.CONCUR_UPDATABLE, mockStmt.getResultSetConcurrency());
        Assert.assertEquals(ResultSet.CLOSE_CURSORS_AT_COMMIT, mockStmt.getResultSetHoldability());

        stmt.close();

        conn.close();
    }

    public void test_for_happyday517_2() throws Exception {
        Connection conn = dataSource.getConnection();

        String sql = "select 1";
        Statement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        MockPreparedStatement mockStmt = stmt.unwrap(MockPreparedStatement.class);

        Assert.assertEquals(ResultSet.TYPE_SCROLL_SENSITIVE, mockStmt.getResultSetType());
        Assert.assertEquals(ResultSet.CONCUR_UPDATABLE, mockStmt.getResultSetConcurrency());

        stmt.close();

        conn.close();
    }

    public void test_for_happyday517_3() throws Exception {
        Connection conn = dataSource.getConnection();

        String sql = "select 1";
        Statement stmt = conn.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE,
                                               ResultSet.CLOSE_CURSORS_AT_COMMIT);

        MockPreparedStatement mockStmt = stmt.unwrap(MockPreparedStatement.class);

        Assert.assertEquals(ResultSet.TYPE_SCROLL_SENSITIVE, mockStmt.getResultSetType());
        Assert.assertEquals(ResultSet.CONCUR_UPDATABLE, mockStmt.getResultSetConcurrency());
        Assert.assertEquals(ResultSet.CLOSE_CURSORS_AT_COMMIT, mockStmt.getResultSetHoldability());

        stmt.close();

        conn.close();
    }

    public void test_for_happyday517_4() throws Exception {
        Connection conn = dataSource.getConnection();

        String sql = "select 1";
        Statement stmt = conn.prepareCall(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

        Statement mockStmt = stmt.unwrap(Statement.class);

        Assert.assertEquals(ResultSet.TYPE_SCROLL_SENSITIVE, mockStmt.getResultSetType());
        Assert.assertEquals(ResultSet.CONCUR_UPDATABLE, mockStmt.getResultSetConcurrency());

        stmt.close();

        conn.close();
    }

    public void test_for_happyday517_5() throws Exception {
        Connection conn = dataSource.getConnection();

        String sql = "select 1";
        Statement stmt = conn.prepareCall(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE,
                                          ResultSet.CLOSE_CURSORS_AT_COMMIT);

        Statement mockStmt = stmt.unwrap(Statement.class);

        Assert.assertEquals(ResultSet.TYPE_SCROLL_SENSITIVE, mockStmt.getResultSetType());
        Assert.assertEquals(ResultSet.CONCUR_UPDATABLE, mockStmt.getResultSetConcurrency());
        Assert.assertEquals(ResultSet.CLOSE_CURSORS_AT_COMMIT, mockStmt.getResultSetHoldability());

        stmt.close();

        conn.close();
    }
}
