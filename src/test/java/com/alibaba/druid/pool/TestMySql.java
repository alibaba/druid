package com.alibaba.druid.pool;

import java.sql.Connection;

import junit.framework.TestCase;

public class TestMySql extends TestCase {

    private DruidDataSource dataSource = new DruidDataSource();

    protected void setUp() throws Exception {
        String jdbcUrl = "jdbc:mysql://10.249.193.126/tddl5_00";
        String user = "tddl5";
        String password = "tddl5";

        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_0() throws Exception {
        Connection conn = dataSource.getConnection();

        conn.close();
    }
}
