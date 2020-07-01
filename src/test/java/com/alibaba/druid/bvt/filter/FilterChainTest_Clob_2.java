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

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.mock.MockCallableStatement;
import com.alibaba.druid.mock.MockClob;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxyImpl;
import com.alibaba.druid.proxy.jdbc.ClobProxy;
import com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl;
import com.alibaba.druid.util.JdbcUtils;

public class FilterChainTest_Clob_2 extends TestCase {

    private DruidDataSource        dataSource;
    private CallableStatementProxy statement;

    private int                    invokeCount = 0;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        MockCallableStatement mockStmt = new MockCallableStatement(null, "") {

            @Override
            public Object getObject(int parameterIndex) throws SQLException {
                invokeCount++;
                return new MockClob();
            }
        };

        statement = new CallableStatementProxyImpl(new ConnectionProxyImpl(null, null, null, 0), mockStmt, "", 1);

    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);

        invokeCount = 0;
    }

    public void test_getClob() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);

        Clob clob = chain.callableStatement_getClob(statement, 1);

        Assert.assertTrue(clob instanceof ClobProxy);
        Assert.assertEquals(1, invokeCount);
    }

    public void test_getClob_1() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);

        Clob clob = chain.callableStatement_getClob(statement, "1");

        Assert.assertTrue(clob instanceof ClobProxy);
        Assert.assertEquals(1, invokeCount);
    }

    public void test_getObject() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);

        Clob clob = (Clob) chain.callableStatement_getObject(statement, 1);

        Assert.assertTrue(clob instanceof ClobProxy);
        Assert.assertEquals(1, invokeCount);
    }

    public void test_getObject_1() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);

        Clob clob = (Clob) chain.callableStatement_getObject(statement, "1");

        Assert.assertTrue(clob instanceof ClobProxy);
        Assert.assertEquals(1, invokeCount);
    }

    public void test_getObject_2() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);

        Clob clob = (Clob) chain.callableStatement_getObject(statement, 1, Collections.<String, Class<?>> emptyMap());

        Assert.assertTrue(clob instanceof ClobProxy);
        Assert.assertEquals(1, invokeCount);
    }

    public void test_getObject_3() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);

        Clob clob = (Clob) chain.callableStatement_getObject(statement, "1", Collections.<String, Class<?>> emptyMap());

        Assert.assertTrue(clob instanceof ClobProxy);
        Assert.assertEquals(1, invokeCount);
    }
}
