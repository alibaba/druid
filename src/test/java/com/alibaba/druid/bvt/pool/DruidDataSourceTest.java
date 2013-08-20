package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class DruidDataSourceTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }
    
    public void test_getInitStackTrace() {
        String stackTrace = dataSource.getInitStackTrace();
        Assert.assertTrue(stackTrace.indexOf("com.alibaba.druid.bvt.pool.DruidDataSourceTest.setUp") != -1);
    }
    
    public void test_restart() throws Exception {
        Assert.assertEquals(true, dataSource.isInited());
        dataSource.restart();
        Assert.assertEquals(false, dataSource.isInited());
        
        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
