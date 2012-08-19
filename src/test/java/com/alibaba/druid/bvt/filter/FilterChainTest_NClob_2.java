package com.alibaba.druid.bvt.filter;

import java.sql.Clob;
import java.sql.SQLException;
import java.util.Collections;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.mock.MockCallableStatement;
import com.alibaba.druid.mock.MockNClob;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxy;
import com.alibaba.druid.proxy.jdbc.CallableStatementProxyImpl;
import com.alibaba.druid.proxy.jdbc.ClobProxy;
import com.alibaba.druid.proxy.jdbc.NClobProxy;
import com.alibaba.druid.util.JdbcUtils;

public class FilterChainTest_NClob_2 extends TestCase {

    private DruidDataSource        dataSource;
    private CallableStatementProxy statement;

    private int                    invokeCount = 0;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        MockCallableStatement mockStmt = new MockCallableStatement(null, "") {

            @Override
            public Object getObject(int parameterIndex) throws SQLException {
                invokeCount++;
                return new MockNClob();
            }
        };

        statement = new CallableStatementProxyImpl(null, mockStmt, "", 1);

    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);

        invokeCount = 0;
    }

    public void test_getClob() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);

        Clob clob = chain.callableStatement_getClob(statement, 1);

        Assert.assertTrue(clob instanceof NClobProxy);
        Assert.assertEquals(1, invokeCount);
    }
    
    public void test_getClob_1() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);
        
        Clob clob = chain.callableStatement_getClob(statement, "1");
        
        Assert.assertTrue(clob instanceof NClobProxy);
        Assert.assertEquals(1, invokeCount);
    }
    

    public void test_getObject() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);

        Clob clob = (Clob) chain.callableStatement_getObject(statement, 1);

        Assert.assertTrue(clob instanceof NClobProxy);
        Assert.assertEquals(1, invokeCount);
    }
    
    public void test_getObject_1() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);
        
        Clob clob = (Clob) chain.callableStatement_getObject(statement, "1");
        
        Assert.assertTrue(clob instanceof NClobProxy);
        Assert.assertEquals(1, invokeCount);
    }
    
    public void test_getObject_2() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);

        Clob clob = (Clob) chain.callableStatement_getObject(statement, 1, Collections.<String, Class<?>>emptyMap());

        Assert.assertTrue(clob instanceof NClobProxy);
        Assert.assertEquals(1, invokeCount);
    }
    
    public void test_getObject_3() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);
        
        Clob clob = (Clob) chain.callableStatement_getObject(statement, "1", Collections.<String, Class<?>>emptyMap());
        
        Assert.assertTrue(clob instanceof NClobProxy);
        Assert.assertEquals(1, invokeCount);
    }
}
