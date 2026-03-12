package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class FullTest {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);

        dataSource.init();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_restart() throws Exception {
        assertEquals(false, dataSource.isFull());
        dataSource.fill();

        assertEquals(true, dataSource.isFull());
        Connection conn = dataSource.getConnection();
        assertEquals(true, dataSource.isFull());
        conn.close();
        assertEquals(true, dataSource.isFull());
    }
}
