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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import com.alibaba.druid.PoolTestCase;
import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;


public class TestClone extends PoolTestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        super.setUp();

        dataSource = new DruidDataSource();

        dataSource.setUsername("xxx1");
        dataSource.setPassword("ppp");
        dataSource.setUrl("jdbc:mock:xx");
        dataSource.setFilters("stat");
        dataSource.setMaxOpenPreparedStatements(30);
    }

    public void test_clone() throws Exception {
        Connection conn_0 = dataSource.getConnection();

        DruidDataSource clone = (DruidDataSource) dataSource.clone();
        clone.init();

        dataSource.close();

        Assert.assertEquals(dataSource.getUrl(), clone.getUrl());
        Assert.assertEquals(dataSource.getUsername(), clone.getUsername());
        Assert.assertEquals(dataSource.getPassword(), clone.getPassword());
        Assert.assertEquals(dataSource.getFilterClassNames(), clone.getFilterClassNames());
        Assert.assertEquals(dataSource.getMaxOpenPreparedStatements(), clone.getMaxOpenPreparedStatements());

        PreparedStatement ps_0 = conn_0.prepareStatement("select 1");
        ResultSet rs = ps_0.executeQuery();
        rs.next();
        rs.close();
        ps_0.close();

        // dataSource is closed, but connections is not closed
        Assert.assertFalse(conn_0.isClosed());

        MockConnection mockConn_0 = conn_0.unwrap(MockConnection.class);

        Assert.assertFalse(mockConn_0.isClosed());

        conn_0.close(); // no error

        // real connection already closed
        Assert.assertTrue(mockConn_0.isClosed());

        // now is new dataSource;
        dataSource = clone;

        Connection conn_1 = dataSource.getConnection();

        conn_1.close();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
        super.tearDown();
    }
}
