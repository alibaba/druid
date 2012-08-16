package com.alibaba.druid.bvt.filter.config;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.config.ConfigFilter;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class ConfigLoadFilterTest1 extends TestCase {

    private DruidDataSource dataSource;
    private ConfigFilter    configFilter;

    protected void setUp() throws Exception {
        configFilter = new ConfigFilter();
        configFilter.setFile("bvt/config/config-1.properties");

        dataSource = new DruidDataSource();
        dataSource.getProxyFilters().add(configFilter);
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_config() throws Exception {
        Assert.assertEquals(true, dataSource.isTestOnBorrow()); // default
        Assert.assertEquals(8, dataSource.getMaxActive());
        
        Assert.assertEquals(1, dataSource.getProxyFilters().size());
        
        dataSource.init();

        Assert.assertEquals(false, dataSource.isTestOnBorrow());
        Assert.assertEquals(11, dataSource.getMaxActive());
        Assert.assertEquals("jdbc:mock:config-1", dataSource.getUrl());
        Assert.assertEquals(MockDriver.instance, dataSource.getDriver());
        Assert.assertEquals(3, dataSource.getProxyFilters().size());

        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
