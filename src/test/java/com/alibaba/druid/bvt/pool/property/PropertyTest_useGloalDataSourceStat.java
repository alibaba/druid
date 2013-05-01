package com.alibaba.druid.bvt.pool.property;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;


public class PropertyTest_useGloalDataSourceStat extends TestCase {
    private DruidDataSource dataSource;

    public void test_true() {
        System.setProperty("druid.useGloalDataSourceStat", "true");
        dataSource = new DruidDataSource();
        Assert.assertTrue(dataSource.isUseGloalDataSourceStat());
    }
    
    public void test_false() {
        System.setProperty("druid.useGloalDataSourceStat", "false");
        dataSource = new DruidDataSource();
        Assert.assertFalse(dataSource.isUseGloalDataSourceStat());
    }
    
    protected void tearDown() throws Exception {
        System.clearProperty("druid.useGloalDataSourceStat");
        JdbcUtils.close(dataSource);
    }
}
