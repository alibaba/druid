package com.alibaba.druid.bvt.filter.config;

import java.sql.Connection;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

import junit.framework.Assert;
import junit.framework.TestCase;

public class ConfigLoadFilterTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("druid-configFile=bvt/config/config-0.properties");
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_config() throws Exception {
        Assert.assertEquals(true, dataSource.isTestOnBorrow()); // default
        Assert.assertEquals(8, dataSource.getMaxActive());
        
        dataSource.init();

        Assert.assertEquals(false, dataSource.isTestOnBorrow());
        Assert.assertEquals(10, dataSource.getMaxActive());
        Assert.assertEquals("jdbc:mock:config-0", dataSource.getUrl());
        Assert.assertEquals(MockDriver.instance, dataSource.getDriver());

        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
