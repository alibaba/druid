package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.MockExceptionSorter;
import com.alibaba.druid.util.JdbcUtils;

public class ExceptionSorterTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(true);
        dataSource.setDbType("mysql");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setFilters("stat");

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_exceptionSorter() throws Exception {
        Assert.assertTrue(dataSource.getExceptionSorterClassName(),
                          dataSource.getExceptionSorter() instanceof MockExceptionSorter);

        Connection conn = dataSource.getConnection();
        MockConnection mockConn = conn.unwrap(MockConnection.class);

        PreparedStatement stmt = conn.prepareStatement("select 1");

        stmt.execute();

        mockConn.close();

        Exception stmtClosedError = null;
        try {
            stmt.close();
        } catch (Exception ex) {
            stmtClosedError = ex;
        }
        Assert.assertNotNull(stmtClosedError);
        conn.close();
    }
}
