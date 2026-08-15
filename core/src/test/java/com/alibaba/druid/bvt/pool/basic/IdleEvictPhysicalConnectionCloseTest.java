package com.alibaba.druid.bvt.pool.basic;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.GetConnectionTimeoutException;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class IdleEvictPhysicalConnectionCloseTest {
    private DruidDataSource dataSource;
    private MockDriver driver;

    @BeforeEach
    protected void setUp() throws Exception {
        driver = driverWithFailingStatementClose();
        dataSource = new DruidDataSource();
        dataSource.setDriver(driver);
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
        assertIdleEvictClosesPhysicalConnections();
    }

    @Test
    public void test_idle_evict_closes_physical_connection_with_stat_filter() throws Exception {
        dataSource.close();

        dataSource = new DruidDataSource();
        dataSource.setDriver(driver);
        dataSource.setUrl("jdbc:mock:idle_evict_close_stat");
        dataSource.setFilters("stat");
        dataSource.setTestOnBorrow(false);
        dataSource.setMaxActive(10);
        dataSource.setMinIdle(2);
        dataSource.setMinEvictableIdleTimeMillis(10);
        dataSource.setMaxEvictableIdleTimeMillis(100);
        dataSource.setMaxWait(20);
        dataSource.init();

        assertIdleEvictClosesPhysicalConnections();
    }

    private void assertIdleEvictClosesPhysicalConnections() throws Exception {
        long closeCountBefore = driver.getConnectionCloseCount();
        int connectionsSizeBefore = driver.getConnectionsSize();
        connect(6);

        List<MockConnection> physicalConnections = new ArrayList<>(driver.getConnections());
        assertEquals(6, dataSource.getPoolingCount());
        assertEquals(connectionsSizeBefore + 6, driver.getConnectionsSize());

        Thread.sleep(20);
        dataSource.shrink(true);

        assertEquals(2, dataSource.getPoolingCount());
        assertEquals(connectionsSizeBefore + 2, driver.getConnectionsSize());
        assertEquals(4, dataSource.getDestroyCount());
        assertEquals(4, driver.getConnectionCloseCount() - closeCountBefore);
        assertEvictedPhysicalConnectionsClosed(physicalConnections, driver.getConnections());

        Thread.sleep(100);
        dataSource.shrink(true);

        assertEquals(0, dataSource.getPoolingCount());
        assertEquals(connectionsSizeBefore, driver.getConnectionsSize());
        assertEquals(6, dataSource.getDestroyCount());
        assertEquals(6, driver.getConnectionCloseCount() - closeCountBefore);
        assertEvictedPhysicalConnectionsClosed(physicalConnections, driver.getConnections());
    }

    private static void assertEvictedPhysicalConnectionsClosed(
            List<MockConnection> physicalConnections,
            List<MockConnection> remainingConnections
    ) throws SQLException {
        Set<Long> remainingIds = new HashSet<>();
        for (MockConnection remainingConnection : remainingConnections) {
            remainingIds.add(remainingConnection.getId());
        }

        for (MockConnection physicalConnection : physicalConnections) {
            if (!remainingIds.contains(physicalConnection.getId())) {
                assertTrue(
                        physicalConnection.isClosed(),
                        "evicted physical connection must be closed even when wrapper statement.close() fails"
                );
            }
        }
    }

    private static MockDriver driverWithFailingStatementClose() {
        return new MockDriver() {
            @Override
            public MockStatement createMockStatement(MockConnection conn) {
                return new MockStatement(conn) {
                    @Override
                    public void close() throws SQLException {
                        throw new SQLException("statement close fails");
                    }
                };
            }
        };
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
