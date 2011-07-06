package com.alibaba.druid.bvt.pool;

import java.sql.Connection;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class ManagedDataSourceTest extends TestCase {

    private DruidDataSource dataSource;

    public void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
    }

    public void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_managed() throws Exception {
        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
