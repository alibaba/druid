package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DruidDataSourceTest_fill {
    private DruidDataSource dataSource;

    private int maxActive = 10;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setMaxActive(maxActive);
        dataSource.setTestOnBorrow(false);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_fill_0() throws Exception {
        int fillCount = dataSource.fill(3);
        assertEquals(3, fillCount);
    }

    @Test
    public void test_fill_1() throws Exception {
        int fillCount = dataSource.fill(1000);
        assertEquals(maxActive, fillCount);
    }

    @Test
    public void test_fill_2() throws Exception {
        int fillCount = dataSource.fill(maxActive);
        assertEquals(maxActive, fillCount);
    }

    @Test
    public void test_fill_3() throws Exception {
        int fillCount = dataSource.fill();
        assertEquals(maxActive, fillCount);
    }

    @Test
    public void test_fill_5() throws Exception {
        Exception error = null;
        try {
            dataSource.fill(-1);
        } catch (IllegalArgumentException e) {
            error = e;
        }
        assertNotNull(error);
    }
}
