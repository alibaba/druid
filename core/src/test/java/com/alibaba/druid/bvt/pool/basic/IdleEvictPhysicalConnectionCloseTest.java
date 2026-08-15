package com.alibaba.druid.bvt.pool.basic;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.GetConnectionTimeoutException;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IdleEvictPhysicalConnectionCloseTest {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:idle_evict_close");
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxActive(10);
        dataSource.setMinIdle(2);
        dataSource.setMinEvictableIdleTimeMillis(10);
        dataSource.setMaxEvictableIdleTimeMillis(100);
        dataSource.setMaxWait(20);
        dataSource.init();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_idle_evict_closes_physical_connection() throws Exception {
        long closeCountBefore = MockDriver.instance.getConnectionCloseCount();
        int connectionsSizeBefore = MockDriver.instance.getConnectionsSize();
        connect(6);

        assertEquals(6, dataSource.getPoolingCount());
        assertEquals(connectionsSizeBefore + 6, MockDriver.instance.getConnectionsSize());

        Thread.sleep(20);
        dataSource.shrink(true);

        assertEquals(2, dataSource.getPoolingCount());
        assertEquals(connectionsSizeBefore + 2, MockDriver.instance.getConnectionsSize());
        assertEquals(4, dataSource.getDestroyCount());
        assertEquals(4, MockDriver.instance.getConnectionCloseCount() - closeCountBefore);

        Thread.sleep(100);
        dataSource.shrink(true);

        assertEquals(0, dataSource.getPoolingCount());
        assertEquals(connectionsSizeBefore, MockDriver.instance.getConnectionsSize());
        assertEquals(6, dataSource.getDestroyCount());
        assertEquals(6, MockDriver.instance.getConnectionCloseCount() - closeCountBefore);
    }

    @Test
    public void test_idle_evict_closes_physical_connection_with_stat_filter() throws Exception {
        dataSource.close();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:idle_evict_close_stat");
        dataSource.setFilters("stat");
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxActive(10);
        dataSource.setMinIdle(2);
        dataSource.setMinEvictableIdleTimeMillis(10);
        dataSource.setMaxEvictableIdleTimeMillis(100);
        dataSource.setMaxWait(20);
        dataSource.init();

        long closeCountBefore = MockDriver.instance.getConnectionCloseCount();
        int connectionsSizeBefore = MockDriver.instance.getConnectionsSize();
        connect(6);

        assertEquals(6, dataSource.getPoolingCount());
        assertEquals(connectionsSizeBefore + 6, MockDriver.instance.getConnectionsSize());

        Thread.sleep(20);
        dataSource.shrink(true);

        assertEquals(2, dataSource.getPoolingCount());
        assertEquals(connectionsSizeBefore + 2, MockDriver.instance.getConnectionsSize());
        assertTrue(dataSource.getDestroyCount() >= 4);
        assertTrue(MockDriver.instance.getConnectionCloseCount() - closeCountBefore >= 4);
    }

    private int connect(int count) throws Exception {
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
