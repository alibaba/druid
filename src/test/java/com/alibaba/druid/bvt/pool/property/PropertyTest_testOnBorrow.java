package com.alibaba.druid.bvt.pool.property;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;


public class PropertyTest_testOnBorrow extends TestCase {
    private DruidDataSource dataSource;

    public void test_true() {
        System.setProperty("druid.testOnBorrow", "true");
        dataSource = new DruidDataSource();
        Assert.assertTrue(dataSource.isTestOnBorrow());
    }
    
    public void test_false() {
        System.setProperty("druid.testOnBorrow", "false");
        dataSource = new DruidDataSource();
        Assert.assertFalse(dataSource.isTestOnBorrow());
        
        Assert.assertNull(dataSource.getWallStatMap());
    }
    
    protected void tearDown() throws Exception {
        System.clearProperty("druid.testOnBorrow");
        JdbcUtils.close(dataSource);
    }
}
