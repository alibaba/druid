package com.alibaba.druid.bvt.pool.exception;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.vendor.OceanBaseOracleExceptionSorter;
import com.alibaba.druid.pool.vendor.OracleExceptionSorter;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.test.util.OracleMockDriver;
import com.alibaba.druid.util.JdbcUtils;
import junit.framework.TestCase;
import org.junit.Assert;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class OceanBaseOracleExceptionSorterTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        Assert.assertEquals(0, JdbcStatManager.getInstance().getSqlList().size());
        
        dataSource = new DruidDataSource();

        dataSource.setExceptionSorter(new OceanBaseOracleExceptionSorter());

        dataSource.setDriver(new OracleMockDriver());
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setFilters("log4j");
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
        }

        DruidPooledConnection conn = dataSource.getConnection();
        PreparedStatement pstmt = conn.prepareStatement(sql);
        pstmt.setFetchSize(1000);

        SQLException exception = new SQLException("xx", "xxx", 28);
        boolean fatal = false;
        try {
            conn.handleException(exception);
        } catch (SQLException ex) {
            fatal = true;
        }
        Assert.assertTrue(fatal);

        pstmt.close();

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
