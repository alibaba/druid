package com.alibaba.druid.bvt.pool.basic;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.GetConnectionTimeoutException;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class MaxPhyTimeMillisTest {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxActive(50);
        dataSource.setMinIdle(5);
        dataSource.setMinEvictableIdleTimeMillis(10);
        dataSource.setPhyTimeoutMillis(100);
        dataSource.setMaxWait(20);
        dataSource.init();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_max() throws Exception {
        connect(10);

        assertEquals(10, dataSource.getPoolingCount());
        Thread.sleep(20);
        dataSource.shrink(true);
        assertEquals(5, dataSource.getPoolingCount());

        Thread.sleep(100);
        dataSource.shrink(true);
        assertEquals(0, dataSource.getPoolingCount());
    }

    public int connect(int count) throws Exception {
        int successCount = 0;
        Connection[] connections = new Connection[count];
        for (int i = 0; i < count; ++i) {
            try {
                connections[i] = dataSource.getConnection();
                successCount++;
            } catch (GetConnectionTimeoutException e) {
                // skip
            }
        }

        for (int i = 0; i < count; ++i) {
            JdbcUtils.close(connections[i]);
        }

        return successCount;
    }
}
