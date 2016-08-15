package com.alibaba.druid.bvt.pool.basic;

import java.sql.Connection;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.GetConnectionTimeoutException;
import com.alibaba.druid.util.JdbcUtils;

import junit.framework.TestCase;

public class MaxEvictableIdleTimeMillisTest2 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxActive(50);
        dataSource.setMinIdle(5);
        

    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_error() throws Exception {
        Exception error = null;
        try {
            dataSource.setMinEvictableIdleTimeMillis(20);
            dataSource.setMaxEvictableIdleTimeMillis(30);
            dataSource.setMinEvictableIdleTimeMillis(100);
            dataSource.setMaxWait(20);
            dataSource.init();
        } catch (Exception ex) {
            error = ex;
            ex.printStackTrace();
        }
        Assert.assertNotNull(error);
        Assert.assertTrue(dataSource.isInited());
        Assert.assertEquals(30, dataSource.getMaxEvictableIdleTimeMillis());
    }
    
   
}
