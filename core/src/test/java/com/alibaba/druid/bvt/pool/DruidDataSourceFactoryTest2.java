package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidDataSourceFactory;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class DruidDataSourceFactoryTest2 {
    private DruidDataSource dataSource;

    @AfterEach
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    @Test
    public void test_factory() throws Exception {
        Properties properties = new Properties();
        properties.put(DruidDataSourceFactory.PROP_POOLPREPAREDSTATEMENTS, "false");
        properties.put(DruidDataSourceFactory.PROP_MAXOPENPREPAREDSTATEMENTS, "100");

        dataSource = (DruidDataSource) DruidDataSourceFactory.createDataSource(properties);

        assertFalse(dataSource.isPoolPreparedStatements());
    }
}
