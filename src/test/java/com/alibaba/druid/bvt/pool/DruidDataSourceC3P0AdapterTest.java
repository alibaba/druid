package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSourceC3P0Adapter;

public class DruidDataSourceC3P0AdapterTest extends TestCase {

    private DruidDataSourceC3P0Adapter dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSourceC3P0Adapter();
        dataSource.setJdbcUrl("jdbc:mock:xxx");
        dataSource.setInitialPoolSize(1);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_conn_err() throws Exception {
        Exception error = null;
        try {
            dataSource.getConnection(null, null);
        } catch (UnsupportedOperationException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
    
    public void test_getDriverClass() throws Exception {
        Assert.assertNull(dataSource.getDriverClass());
        
        Connection conn = dataSource.getConnection();
        conn.close();
        
        Assert.assertEquals(MockDriver.class.getName(), dataSource.getDriverClass());
        Assert.assertEquals(MockDriver.instance, dataSource.getDriver());
    }
    
    public void test_getJdbcUrl() throws Exception {
        Assert.assertEquals("jdbc:mock:xxx", dataSource.getJdbcUrl());
    }
}
