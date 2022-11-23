package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import junit.framework.TestCase;

import java.sql.SQLException;

public class UrlNotSetTest extends TestCase {
    protected DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setMaxWait(10);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_wait() throws Exception {
        Exception error = null;
        try {
            DruidPooledConnection conn = dataSource.getConnection();
            conn.close();
        } catch (SQLException ex) {
            error = ex;
        }
        assertEquals("url not set", error.getMessage());
    }
}
