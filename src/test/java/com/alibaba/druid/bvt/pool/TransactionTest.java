package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class TransactionTest extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }
    
    public void test_txn() throws Exception {
        Connection conn = dataSource.getConnection();
        
        conn.setAutoCommit(false);
        for (int i = 0; i < 100; ++i) {
            PreparedStatement stmt = conn.prepareStatement("select + " + (i % 10));
            stmt.executeUpdate();
            stmt.close();
        }
        conn.commit();
        conn.close();
    }
}
