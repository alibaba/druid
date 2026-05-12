package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

public class TransactionTest {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
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
