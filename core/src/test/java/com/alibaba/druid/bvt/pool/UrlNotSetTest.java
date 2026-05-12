package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class UrlNotSetTest {
    protected DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setMaxWait(10);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_wait() throws Exception {
        Exception error = null;
        try {
            DruidPooledConnection conn = dataSource.getConnection();
            assertNull(conn);
            //conn.close();
        } catch (SQLException ex) {
            error = ex;
        }
        //assertEquals("url not set", error.getMessage());
    }
}
