package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DruidDataSourceShrinkTest {
    protected DruidDataSource dataSource;
    protected ExecutorService executor;

    @BeforeEach
    public void setUp() {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setMinIdle(1);

        executor = Executors.newFixedThreadPool(2);
    }

    @AfterEach
    public void tearDown() {
        JdbcUtils.close(dataSource);
        executor.shutdownNow();
    }

    @Test
    public void test() throws Exception {
        for (int i = 0; i < 100; i++) {
            dataSource.fill();
            assertEquals(8, dataSource.getPoolingCount());
            dataSource.shrink(false, false);
            assertEquals(dataSource.getMinIdle(), dataSource.getPoolingCount());
        }
    }
}
