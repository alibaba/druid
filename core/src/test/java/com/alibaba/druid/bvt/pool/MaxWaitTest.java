package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import junit.framework.TestCase;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

public class MaxWaitTest extends TestCase {
    protected DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:driver");
        dataSource.setMaxWait(100);
        dataSource.setDriver(new MockDriver() {
            @Override
            public Connection connect(String url, Properties info) throws SQLException {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    // skip
                }
                return super.connect(url, info);
            }
        });
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
        assertTrue(error.getMessage().contains("createElapseMillis "));
    }
}
