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

import static org.junit.Assert.*;


import java.sql.Connection;
import java.sql.Date;
import java.sql.Statement;

import javax.sql.DataSource;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.vendor.NullExceptionSorter;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class TestDataSourceBasic extends PoolTestCase {
    private MockDriver driver;
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
        dataSource.setFilters("stat,trace");
        dataSource.setRemoveAbandoned(true);
        dataSource.setExceptionSorterClassName(null);

        assertTrue(dataSource.getExceptionSorter() instanceof NullExceptionSorter);
        dataSource.setExceptionSorterClassName("");
        assertTrue(dataSource.getExceptionSorter() instanceof NullExceptionSorter);
    }

    protected void tearDown() throws Exception {
        if (dataSource.getCreateCount() > 0) {
            assertEquals(true, dataSource.getCreateTimespanNano() > 0);
        }
        dataSource.close();
        assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        super.tearDown();
    }

    public void test_toCompositeData() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();
        dataSource.getCompositeData();
    }

    public void test_prepare() throws Exception {
        Connection conn = dataSource.getConnection();

        {
            DruidPooledConnection wrap = conn.unwrap(DruidPooledConnection.class);
            assertTrue(conn.isWrapperFor(DruidPooledConnection.class));
            assertNotNull(wrap);
        }

        {
            Statement wrap = conn.unwrap(Statement.class);
            assertTrue(!conn.isWrapperFor(Statement.class));
            assertNull(wrap);
        }

        conn.setAutoCommit(false);
        conn.setAutoCommit(false);
        Statement stmt = conn.createStatement();
        stmt.execute("SELECT 1");
        stmt.close();

        assertEquals(1, dataSource.getActiveConnectionStackTrace().size());
        assertEquals(1, dataSource.getActiveConnections().size());
        conn.commit();
        conn.close();

        assertEquals(1, dataSource.getStartTransactionCount());
        assertEquals(1, dataSource.getCommitCount());
        assertEquals(0, dataSource.getRollbackCount());

        assertEquals(0, dataSource.getActiveConnectionStackTrace().size());
        assertEquals(0, dataSource.getActiveConnections().size());
    }

    public void test_wrap() throws Exception {
        assertTrue(!dataSource.isWrapperFor(Date.class));
        assertTrue(!dataSource.isWrapperFor(null));
        assertTrue(dataSource.isWrapperFor(DataSource.class));

        assertTrue(dataSource.unwrap(Date.class) == null);
        assertTrue(dataSource.unwrap(null) == null);
        assertTrue(dataSource.unwrap(DataSource.class) != null);
    }
}
