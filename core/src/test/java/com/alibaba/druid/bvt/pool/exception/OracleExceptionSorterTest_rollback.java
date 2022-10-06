package com.alibaba.druid.bvt.pool.exception;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import junit.framework.TestCase;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.vendor.OracleExceptionSorter;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.test.util.OracleMockDriver;
import com.alibaba.druid.util.JdbcUtils;

public class OracleExceptionSorterTest_rollback extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());

        dataSource = new DruidDataSource();

        dataSource.setExceptionSorter(new OracleExceptionSorter());

        dataSource.setDriver(new OracleMockDriver());
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(100);
    }

    @Override
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_connect() throws Exception {
        String sql = "SELECT 1";
        {
            DruidPooledConnection conn = dataSource.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.execute();
            pstmt.close();
            conn.close();

            assertEquals(0, dataSource.getActiveCount());
            assertEquals(1, dataSource.getPoolingCount());
            assertEquals(1, dataSource.getCreateCount());
        }

        DruidPooledConnection conn = dataSource.getConnection();
        MockConnection mockConn = conn.unwrap(MockConnection.class);
        assertNotNull(mockConn);

        conn.setAutoCommit(false);

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.execute();

        SQLException exception = new SQLException("xx", "xxx", 28);
        mockConn.setError(exception);

        Exception rollbackError = null;
        try {
            conn.rollback();
        } catch (Exception ex) {
            rollbackError = ex;
        }
        assertNotNull(rollbackError);

        conn.close();

        {
            Connection conn2 = dataSource.getConnection();
            conn2.close();
        }
        assertEquals(0, dataSource.getActiveCount());
        assertTrue(dataSource.getPoolingCount() >= 1);
        assertTrue(dataSource.getCreateCount() >= 2);
    }

}
