package com.alibaba.druid.bvt.pool.property;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;


public class PropertyTest_validationQuery extends TestCase {
    private DruidDataSource dataSource;

    public void test_validationQuery() {
        System.setProperty("druid.validationQuery", "select 1");
        dataSource = new DruidDataSource();
        Assert.assertEquals("select 1", dataSource.getValidationQuery());
    }
    
    protected void tearDown() throws Exception {
        System.clearProperty("druid.validationQuery");
        JdbcUtils.close(dataSource);
    }
}
