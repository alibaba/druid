package com.alibaba.druid.pool;

import java.sql.Connection;

import junit.framework.TestCase;

import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;

public class TestOracle extends TestCase {

    private String jdbcUrl;
    private String user;
    private String password;

    public void setUp() throws Exception {
        jdbcUrl = "jdbc:oracle:thin:@10.20.149.85:1521:ocnauto";
        user = "alibaba";
        password = "ccbuauto";
    }

    public void test_0() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        // dataSource.setFilters("stat");
        dataSource.setExceptionSoter(MySqlExceptionSorter.class.getName());

        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
