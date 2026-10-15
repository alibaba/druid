package com.alibaba.druid.pool;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

public class TestMySql {
    private DruidDataSource dataSource = new DruidDataSource();

    @BeforeEach
    protected void setUp() throws Exception {
        String jdbcUrl = "jdbc:mysql://10.249.193.126/tddl5_00";
        String user = "tddl5";
        String password = "tddl5";

        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_0() throws Exception {
        Connection conn = dataSource.getConnection();

        conn.close();
    }
}
