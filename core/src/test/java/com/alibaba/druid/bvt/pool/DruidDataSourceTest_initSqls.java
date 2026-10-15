package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;

import static org.junit.jupiter.api.Assertions.*;

public class DruidDataSourceTest_initSqls {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setInitialSize(1);
        dataSource.setDefaultReadOnly(true);
        dataSource.setDefaultTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        dataSource.setDefaultCatalog("c123");
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void testDefault() throws Exception {
        Connection conn = dataSource.getConnection();

        assertEquals(true, conn.isReadOnly());
        assertEquals(Connection.TRANSACTION_SERIALIZABLE, conn.getTransactionIsolation());
        assertEquals("c123", conn.getCatalog());

        conn.close();
    }
}
