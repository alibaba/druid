package com.alibaba.druid.bvt.pool.basic;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class MaxEvictableIdleTimeMillisTest2 {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxActive(50);
        dataSource.setMinIdle(5);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_error() throws Exception {
        Exception error = null;
        try {
            dataSource.setMinEvictableIdleTimeMillis(20);
            dataSource.setMaxEvictableIdleTimeMillis(30);
            dataSource.setMinEvictableIdleTimeMillis(100);
            dataSource.setMaxWait(20);
            dataSource.init();
        } catch (Exception ex) {
            error = ex;
            ex.printStackTrace();
        }
        assertNotNull(error);
        assertTrue(dataSource.isInited());
        assertEquals(30, dataSource.getMaxEvictableIdleTimeMillis());
    }
}
