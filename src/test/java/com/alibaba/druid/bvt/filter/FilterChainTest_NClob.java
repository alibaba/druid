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
package com.alibaba.druid.bvt.filter;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.mock.MockNClob;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl;
import com.alibaba.druid.proxy.jdbc.NClobProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxyImpl;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxyImpl;
import com.alibaba.druid.util.JdbcUtils;

public class FilterChainTest_NClob extends TestCase {

    private DruidDataSource dataSource;
    private StatementProxy  statement;
    private MockResultSet   mockResultSet;

    private int             invokeCount = 0;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        ConnectionProxyImpl conn = new ConnectionProxyImpl(dataSource, null, new Properties(), 0);
        statement = new StatementProxyImpl(conn, null, 1);

        mockResultSet = new MockResultSet(null) {

            public Object getObject(int columnIndex) throws SQLException {
                invokeCount++;
                return new MockNClob();
            }
        };
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);

        invokeCount = 0;
    }

    public void test_resultSet_getClob() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);

        Clob clob = chain.resultSet_getClob(new ResultSetProxyImpl(statement, mockResultSet, 1, null), 1);

        Assert.assertTrue(clob instanceof NClobProxy);
        Assert.assertEquals(1, invokeCount);
    }

    public void test_resultSet_getClob_1() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);

        Clob clob = chain.resultSet_getClob(new ResultSetProxyImpl(statement, mockResultSet, 1, null), "1");

        Assert.assertTrue(clob instanceof NClobProxy);
        Assert.assertEquals(1, invokeCount);
    }

    public void test_resultSet_getObject() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);

        Clob clob = (Clob) chain.resultSet_getObject(new ResultSetProxyImpl(statement, mockResultSet, 1, null), 1);

        Assert.assertTrue(clob instanceof NClobProxy);
        Assert.assertEquals(1, invokeCount);
    }

    public void test_resultSet_getObject_1() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);

        Clob clob = (Clob) chain.resultSet_getObject(new ResultSetProxyImpl(statement, mockResultSet, 1, null), "1");

        Assert.assertTrue(clob instanceof NClobProxy);
        Assert.assertEquals(1, invokeCount);
    }
    
    public void test_resultSet_getObject_2() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);
        
        Clob clob = (Clob) chain.resultSet_getObject(new ResultSetProxyImpl(statement, mockResultSet, 1, null), 1, Collections.<String, Class<?>>emptyMap());
        
        Assert.assertTrue(clob instanceof NClobProxy);
        Assert.assertEquals(1, invokeCount);
    }
    
    public void test_resultSet_getObject_3() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);
        
        Clob clob = (Clob) chain.resultSet_getObject(new ResultSetProxyImpl(statement, mockResultSet, 1, null), "1", Collections.<String, Class<?>>emptyMap());
        
        Assert.assertTrue(clob instanceof NClobProxy);
        Assert.assertEquals(1, invokeCount);
    }
}
