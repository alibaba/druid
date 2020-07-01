package com.alibaba.druid.bvt.pool.property;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;


public class PropertyTest_testWhileIdle extends TestCase {
    private DruidDataSource dataSource;

    public void test_true() {
        System.setProperty("druid.testWhileIdle", "true");
        dataSource = new DruidDataSource();
        Assert.assertTrue(dataSource.isTestWhileIdle());
    }
    
    public void test_false() {
        System.setProperty("druid.testWhileIdle", "false");
        dataSource = new DruidDataSource();
        Assert.assertFalse(dataSource.isTestWhileIdle());
    }
    
    protected void tearDown() throws Exception {
        System.clearProperty("druid.testWhileIdle");
        JdbcUtils.close(dataSource);
    }
}
