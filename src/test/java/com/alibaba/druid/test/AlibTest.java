package com.alibaba.druid.test;

import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;

import java.sql.Connection;

public class AlibTest extends TestCase {
    protected DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://127.0.0.1:8507");
        dataSource.setUsername("root");
        dataSource.setPassword("root");

    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_for_alib() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();
    }


}
