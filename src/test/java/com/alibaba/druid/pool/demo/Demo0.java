package com.alibaba.druid.pool.demo;

import java.sql.Connection;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JMXUtils;

public class Demo0 extends TestCase {
    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass;
    private int    initialSize = 10;
    private int    minPoolSize = 1;
    private int    maxPoolSize = 2;
    private int    maxActive   = 2;

    protected void setUp() throws Exception {
        jdbcUrl = "jdbc:fake:dragoon_v25masterdb";
        user = "dragoon25";
        password = "dragoon25";
        driverClass = "com.alibaba.druid.mock.MockDriver";
    }

    public void test_0() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        
        JMXUtils.register("com.alibaba:type=DruidDataSource", dataSource);

        dataSource.setInitialSize(initialSize);
        dataSource.setMaxActive(maxActive);
        dataSource.setMinIdle(minPoolSize);
        dataSource.setMaxIdle(maxPoolSize);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(true);

        Connection conn = dataSource.getConnection();
        conn.close();
        
        System.out.println();
    }
}
