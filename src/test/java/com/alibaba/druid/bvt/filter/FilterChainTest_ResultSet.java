package com.alibaba.druid.bvt.filter;

import java.sql.Clob;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.filter.FilterChainImpl;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.proxy.jdbc.ResultSetProxyImpl;
import com.alibaba.druid.proxy.jdbc.StatementProxy;
import com.alibaba.druid.proxy.jdbc.StatementProxyImpl;
import com.alibaba.druid.util.JdbcUtils;

public class FilterChainTest_ResultSet extends TestCase {

    private DruidDataSource dataSource;
    private StatementProxy  statement;
    private MockResultSet   mockResultSet;

    private int             invokeCount = 0;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        statement = new StatementProxyImpl(null, null, 1);

        mockResultSet = new MockResultSet(null) {

            public Object getObject(int columnIndex) throws SQLException {
                invokeCount++;
                return new MockResultSet(null);
            }
        };
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);

        invokeCount = 0;
    }

    public void test_resultSet_getObject() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);

        ResultSet clob = (ResultSet) chain.resultSet_getObject(new ResultSetProxyImpl(statement, mockResultSet, 1, null), 1);

        Assert.assertTrue(clob instanceof ResultSetProxy);
        Assert.assertEquals(1, invokeCount);
    }

    public void test_resultSet_getObject_1() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);

        ResultSet clob = (ResultSet) chain.resultSet_getObject(new ResultSetProxyImpl(statement, mockResultSet, 1, null), "1");

        Assert.assertTrue(clob instanceof ResultSetProxy);
        Assert.assertEquals(1, invokeCount);
    }
    
    public void test_resultSet_getObject_2() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);
        
        ResultSet clob = (ResultSet) chain.resultSet_getObject(new ResultSetProxyImpl(statement, mockResultSet, 1, null), 1, Collections.<String, Class<?>>emptyMap());
        
        Assert.assertTrue(clob instanceof ResultSetProxy);
        Assert.assertEquals(1, invokeCount);
    }
    
    public void test_resultSet_getObject_3() throws Exception {
        FilterChainImpl chain = new FilterChainImpl(dataSource);
        
        ResultSet clob = (ResultSet) chain.resultSet_getObject(new ResultSetProxyImpl(statement, mockResultSet, 1, null), "1", Collections.<String, Class<?>>emptyMap());
        
        Assert.assertTrue(clob instanceof ResultSetProxy);
        Assert.assertEquals(1, invokeCount);
    }
}
