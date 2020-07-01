package com.alibaba.druid.bvt.pool;

import java.util.Properties;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.util.JdbcUtils;

public class DruidDataSourceFactoryTest2 extends TestCase {

    private DruidDataSource dataSource;

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_factory() throws Exception {
        Properties properties = new Properties();
        properties.put(DruidDataSourceFactory.PROP_POOLPREPAREDSTATEMENTS, "false");
        properties.put(DruidDataSourceFactory.PROP_MAXOPENPREPAREDSTATEMENTS, "100");

        dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);

        Assert.assertFalse(dataSource.isPoolPreparedStatements());
    }
}
