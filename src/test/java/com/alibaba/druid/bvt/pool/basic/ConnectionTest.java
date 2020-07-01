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
package com.alibaba.druid.bvt.pool.basic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import com.alibaba.druid.PoolTestCase;
import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class ConnectionTest extends PoolTestCase {

    private MockDriver      driver;
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        super.setUp();

        driver = new MockDriver();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(driver);
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(2);
        dataSource.setMaxIdle(2);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(true);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat");
    }

    protected void tearDown() throws Exception {
        dataSource.close();
        super.tearDown();
    }

    public void test_prepare() throws Exception {
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement("SELECT 1", ResultSet.FETCH_FORWARD, ResultSet.CONCUR_READ_ONLY);
        stmt.close();

        conn.close();
    }

    public void test_prepare2() throws Exception {
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement("SELECT 1", ResultSet.FETCH_FORWARD, ResultSet.CONCUR_READ_ONLY,
                                                       ResultSet.HOLD_CURSORS_OVER_COMMIT);
        stmt.close();

        conn.close();
    }

    public void test_prepare3() throws Exception {
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement("SELECT 1", new int[0]);
        stmt.close();

        conn.close();
    }

    public void test_prepare4() throws Exception {
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement("SELECT 1", new String[0]);
        stmt.close();

        conn.close();
    }

    public void test_prepare5() throws Exception {
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement("SELECT 1", Statement.RETURN_GENERATED_KEYS);
        stmt.close();

        conn.close();
    }

    public void test_prepareCall() throws Exception {
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareCall("SELECT 1");
        stmt.close();

        conn.close();
    }

    public void test_prepareCall1() throws Exception {
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareCall("SELECT 1", ResultSet.FETCH_FORWARD, ResultSet.CONCUR_READ_ONLY);
        stmt.close();

        conn.close();
    }

    public void test_prepareCall2() throws Exception {
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareCall("SELECT 1", ResultSet.FETCH_FORWARD, ResultSet.CONCUR_READ_ONLY,
                                                  ResultSet.HOLD_CURSORS_OVER_COMMIT);
        stmt.close();

        conn.close();
    }

    public void test_create() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();
        stmt.close();

        conn.close();
    }

    public void test_create1() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement(ResultSet.FETCH_FORWARD, ResultSet.CONCUR_READ_ONLY);
        stmt.close();

        conn.close();
    }

    public void test_create2() throws Exception {
        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement(ResultSet.FETCH_FORWARD, ResultSet.CONCUR_READ_ONLY,
                                              ResultSet.HOLD_CURSORS_OVER_COMMIT);
        stmt.close();

        conn.close();
    }
}
