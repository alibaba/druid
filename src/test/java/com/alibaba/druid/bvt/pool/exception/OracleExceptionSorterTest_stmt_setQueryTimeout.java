package com.alibaba.druid.bvt.pool.exception;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.vendor.OracleExceptionSorter;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.test.util.OracleMockDriver;
import com.alibaba.druid.util.JdbcUtils;

public class OracleExceptionSorterTest_stmt_setQueryTimeout extends PoolTestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        super.setUp();

        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
        
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

        super.tearDown();
    }

    public void test_connect() throws Exception {
        String sql = "SELECT 1";
        {
            DruidPooledConnection conn = dataSource.getConnection();

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.execute();
            pstmt.close();
            conn.close();
        }

        DruidPooledConnection conn = dataSource.getConnection();
        MockConnection mockConn = conn.unwrap(MockConnection.class);
        Assert.assertNotNull(mockConn);

        Statement stmt = conn.createStatement();

        SQLException exception = new SQLException("xx", "xxx", 28);
        mockConn.setError(exception);

        SQLException stmtErrror = null;
        try {
            stmt.setQueryTimeout(10);
        } catch (SQLException ex) {
            stmtErrror = ex;
        }
        Assert.assertNotNull(stmtErrror);
        Assert.assertSame(exception, stmtErrror);
        
        SQLException commitError = null;
        try {
            conn.commit();
        } catch (SQLException ex) {
            commitError = ex;
        }

        Assert.assertNotNull(commitError);
        Assert.assertSame(exception, commitError.getCause());

        conn.close();
    }

}
