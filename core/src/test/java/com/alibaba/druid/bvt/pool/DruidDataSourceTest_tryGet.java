package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DruidDataSourceTest_tryGet {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setMaxActive(1);
        dataSource.setInitialSize(1);
        dataSource.setTestOnBorrow(false);
        dataSource.init();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_0() throws Exception {
        DruidPooledConnection conn1 = this.dataSource.tryGetConnection();
        assertNotNull(conn1);

        DruidPooledConnection conn2 = this.dataSource.tryGetConnection();
        assertNull(conn2);

        conn1.close();
    }
}
