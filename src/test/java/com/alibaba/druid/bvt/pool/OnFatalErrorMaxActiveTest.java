package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.PoolTestCase;
import com.alibaba.druid.pool.DruidAbstractDataSource;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

import java.lang.reflect.Field;
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

        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }

        field.set(dataSource, true);


        Connection conn_0 = dataSource.getConnection();
        assertTrue(dataSource.isOnFatalError());

        SQLException error = null;
        try {
            Connection conn_1 = dataSource.getConnection();
        } catch (SQLException ex) {
            error = ex;
        }
        assertNotNull(error);
        assertEquals("onFatalError, activeCount 1, onFatalErrorMaxActive 1", error.getMessage());

        Statement stmt = conn_0.createStatement();
        ResultSet rs = stmt.executeQuery("select 1");
        assertFalse(dataSource.isOnFatalError());
        rs.close();
        stmt.close();
        conn_0.close();
    }
}
