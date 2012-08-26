package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;

public class DruidDataSourceTest1 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_oracle() throws Exception {
        dataSource.setOracle(true);

        dataSource.init();

        Exception error = null;
        try {
            dataSource.setOracle(false);
        } catch (IllegalStateException e) {
            error = e;
        }
        Assert.assertNotNull(error);
    }
    
    public void test_transactionQueryTimeout() throws Exception {
        dataSource.setTransactionQueryTimeout(123456);
        
        Assert.assertEquals(123456, dataSource.getTransactionQueryTimeout());
    }
    
    public void test_dupCloseLogEnable() throws Exception {
        Assert.assertFalse(dataSource.isDupCloseLogEnable());
        
        dataSource.setDupCloseLogEnable(true);
        
        Assert.assertTrue(dataSource.isDupCloseLogEnable());
    }
    
    public void test_getClosedPreparedStatementCount() throws Exception {
        Assert.assertEquals(0, dataSource.getClosedPreparedStatementCount());
        Assert.assertEquals(0, dataSource.getPreparedStatementCount());
        
        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement("select 1");
        stmt.close();
        
        Assert.assertEquals(1, dataSource.getPreparedStatementCount());
        Assert.assertEquals(1, dataSource.getClosedPreparedStatementCount());
    }
    
    public void test_getDriverMajorVersion() throws Exception {
        Assert.assertEquals(-1, dataSource.getDriverMajorVersion());
        dataSource.init();
        Assert.assertEquals(0, dataSource.getDriverMajorVersion());
    }
    
    public void test_getDriverMinorVersion() throws Exception {
        Assert.assertEquals(-1, dataSource.getDriverMinorVersion());
        dataSource.init();
        Assert.assertEquals(0, dataSource.getDriverMinorVersion());
    }
    
    public void test_getExceptionSorterClassName() throws Exception {
        Assert.assertNull(dataSource.getExceptionSorterClassName());
    }
}
