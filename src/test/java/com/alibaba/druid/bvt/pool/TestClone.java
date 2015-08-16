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
package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;


public class TestClone extends TestCase {
    private DruidDataSource dataSource;
    
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUsername("xxx1");
        dataSource.setPassword("ppp");
        dataSource.setUrl("jdbc:mock:xx");
        dataSource.setFilters("stat");
        dataSource.setMaxOpenPreparedStatements(30);
    }
    
    public void test_clone() throws Exception {
        Connection conn_0 = dataSource.getConnection();
        Connection conn_1 = dataSource.getConnection();
        Connection conn_2 = dataSource.getConnection();
        
        DruidDataSource clone = (DruidDataSource) dataSource.clone();
        clone.init();
        
        Connection conn_3 = dataSource.getConnection();
        Connection conn_4 = dataSource.getConnection();
        Connection conn_5 = dataSource.getConnection();
        
        conn_3.close();
        conn_4.close();
        conn_5.close();
        
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
        Assert.assertFalse(conn_1.isClosed());
        Assert.assertFalse(conn_2.isClosed());
        
        MockConnection mockConn_0 = conn_0.unwrap(MockConnection.class);
        MockConnection mockConn_1 = conn_1.unwrap(MockConnection.class);
        MockConnection mockConn_2 = conn_2.unwrap(MockConnection.class);
        
        Assert.assertFalse(mockConn_0.isClosed());
        Assert.assertFalse(mockConn_1.isClosed());
        Assert.assertFalse(mockConn_2.isClosed());
        
        conn_0.close(); // no error
        conn_1.close(); // no error
        conn_2.close(); // no error
        
        // real connection already closed
        Assert.assertTrue(mockConn_0.isClosed());
        Assert.assertTrue(mockConn_1.isClosed());
        Assert.assertTrue(mockConn_2.isClosed());
        
        // now is new dataSource;
        dataSource = clone;
        
        Connection conn_6 = dataSource.getConnection();
        Connection conn_7 = dataSource.getConnection();
        Connection conn_8 = dataSource.getConnection();
        
        conn_6.close();
        conn_7.close();
        conn_8.close();
        
    }
    
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }
}
