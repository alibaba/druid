package com.alibaba.druid.pool.mysql;

import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;

import java.sql.Connection;

/**
 * Created by wenshao on 16/8/5.
 */
public class MySqlTest extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://100.69.167.129:3906/");
        dataSource.setUsername("pyson");
        dataSource.setPassword("pyson");
        dataSource.setFilters("log4j");
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(false);
        dataSource.setTestWhileIdle(true);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_mysql() throws Exception {
        Connection connection = dataSource.getConnection();
        connection.close();
    }
}
