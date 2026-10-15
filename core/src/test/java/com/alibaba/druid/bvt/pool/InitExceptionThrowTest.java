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

public class InitExceptionThrowTest {
    private DruidDataSource dataSource = new DruidDataSource();

    private int connectCount;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource.setInitExceptionThrow(false);
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDriver(new MockDriver() {
            public Connection connect(String url, Properties info) throws SQLException {
                if (connectCount++ < 1) {
                    throw new SQLException("");
                }
                return super.connect(url, info);
            }
        });
        dataSource.setInitialSize(2);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_pool() throws Exception {
        DruidPooledConnection conn = dataSource.getConnection();
        conn.close();
    }
}
