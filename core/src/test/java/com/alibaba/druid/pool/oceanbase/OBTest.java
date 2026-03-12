package com.alibaba.druid.pool.oceanbase;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

public class OBTest {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://a.b.c.d:45447/");
        dataSource.setUsername("admin");
        dataSource.setPassword("admin");
        dataSource.setFilters("log4j");
    }

    @Test
    public void test_connect() throws Exception {
        {
            Connection conn = dataSource.getConnection();

            conn.setAutoCommit(false);

            conn.close();
        }
        {
            Connection conn = dataSource.getConnection();

            conn.setAutoCommit(true);

            conn.close();
        }
    }
}
