package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.PoolTestCase;
import com.alibaba.druid.pool.DruidAbstractDataSource;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.util.JdbcUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class OnFatalErrorMaxActiveTest extends PoolTestCase {
    protected DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");
        dataSource.setOnFatalErrorMaxActive(1);
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_for_fatalError() throws Exception {
        Field field = DruidAbstractDataSource.class.getDeclaredField("onFatalError");
        field.setAccessible(true);

        SQLException faltalError = new SQLException();
        {
            Connection conn = dataSource.getConnection();

            Method method = DruidDataSource.class.getDeclaredMethod("handleFatalError", DruidPooledConnection.class, SQLException.class, String.class);
            method.setAccessible(true);
            method.invoke(dataSource, conn.unwrap(DruidPooledConnection.class), faltalError, "select 'x'");
            conn.close();
        }

        Connection conn_0 = dataSource.getConnection();
        assertTrue(dataSource.isOnFatalError());

        SQLException error = null;
        try {
            Connection conn_1 = dataSource.getConnection();
        } catch (SQLException ex) {
            error = ex;
        }
        assertNotNull(error);
        assertTrue(error.getMessage().startsWith("onFatalError, activeCount 1, onFatalErrorMaxActive 1"));
        System.out.println(error.getMessage());
        assertNotNull(error.getCause());
        assertSame(faltalError, error.getCause());

        Statement stmt = conn_0.createStatement();
        ResultSet rs = stmt.executeQuery("select 1");
        assertFalse(dataSource.isOnFatalError());
        rs.close();
        stmt.close();
        conn_0.close();
    }
}
