package com.alibaba.druid.bvt.pool;

import static org.junit.Assert.*;


import java.sql.Connection;

import junit.framework.TestCase;


import com.alibaba.druid.pool.DruidDataSource;

public class DruidDataSourceTest_initSqls extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setInitialSize(1);
        dataSource.setDefaultReadOnly(true);
        dataSource.setDefaultTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
        dataSource.setDefaultCatalog("c123");
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void testDefault() throws Exception {
        Connection conn = dataSource.getConnection();

        assertEquals(true, conn.isReadOnly());
        assertEquals(Connection.TRANSACTION_SERIALIZABLE, conn.getTransactionIsolation());
        assertEquals("c123", conn.getCatalog());

        conn.close();
    }

}
