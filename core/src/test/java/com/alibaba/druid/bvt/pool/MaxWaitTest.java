package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

public class MaxWaitTest {
    protected DruidDataSource dataSource;

    @BeforeEach
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

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
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
