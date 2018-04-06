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
import java.sql.Statement;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestCommit extends TestCase {

    private MockDriver      driver;
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();

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
        dataSource.setRemoveAbandoned(true);
    }

    protected void tearDown() throws Exception {
        Assert.assertEquals(true, dataSource.getCreateTimespanNano() > 0);
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_prepare() throws Exception {
        Connection conn = dataSource.getConnection();

        {
            DruidPooledConnection wrap = conn.unwrap(DruidPooledConnection.class);
            Assert.assertTrue(conn.isWrapperFor(DruidPooledConnection.class));
            Assert.assertNotNull(wrap);
        }

        {
            Statement wrap = conn.unwrap(Statement.class);
            Assert.assertTrue(!conn.isWrapperFor(Statement.class));
            Assert.assertNull(wrap);
        }

        conn.setAutoCommit(false);
        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement();
        stmt.execute("SELECT 1");
        stmt.close();
        Assert.assertEquals(1, dataSource.getActiveConnectionStackTrace().size());
        Assert.assertEquals(1, dataSource.getActiveConnections().size());
        conn.rollback();
        conn.close();

        Assert.assertEquals(1, dataSource.getStartTransactionCount());
        Assert.assertEquals(0, dataSource.getCommitCount());
        Assert.assertEquals(1, dataSource.getRollbackCount());

        Assert.assertEquals(0, dataSource.getActiveConnectionStackTrace().size());
        Assert.assertEquals(0, dataSource.getActiveConnections().size());
    }
}
