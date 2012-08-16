package com.alibaba.druid.bvt.filter.config;

import java.sql.Connection;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.config.ConfigFilter;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class ConfigLoadFilterTest2 extends TestCase {

    private DruidDataSource dataSource;
    private ConfigFilter    configFilter;

    protected void setUp() throws Exception {
        configFilter = new ConfigFilter();
        configFilter.setFile("bvt/config/config-2.properties");
        configFilter.setKey("MFwwDQYJKoZIhvcNAQEBBQADSwAwSAJBAMmVkLy+Cy/3rQ1OyH7nz1tT8Bu2KEU+z5LbeZ1yue/RN4KlcOEalj/K9Ev05Lpfu9cOdYnYM8ka1MiQdHLyJP0CAwEAAQ==");

        dataSource = new DruidDataSource();
        dataSource.getProxyFilters().add(configFilter);
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_config() throws Exception {
        Assert.assertEquals(true, dataSource.isTestOnBorrow()); // default
        Assert.assertEquals(8, dataSource.getMaxActive());
        
        dataSource.init();

        Assert.assertEquals(false, dataSource.isTestOnBorrow());
        Assert.assertEquals(12, dataSource.getMaxActive());
        Assert.assertEquals("jdbc:mock:config-2", dataSource.getUrl());
        Assert.assertEquals(MockDriver.instance, dataSource.getDriver());
        Assert.assertEquals("abc", dataSource.getPassword());

        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
