package com.alibaba.druid.pvt.pool;

import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

public class PSCacheTest5 {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:x1");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(10);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    @Test
    public void test_0() throws Exception {
        MockPreparedStatement mockStmt = null;
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("select 1");
            mockStmt = ps.unwrap(MockPreparedStatement.class);
            ps.execute();
            conn.close();
        }
        for (int i = 0; i < 1000; ++i) {
            Connection conn = dataSource.getConnection();
            PreparedStatement ps = conn.prepareStatement("select 1");

            assertSame(mockStmt, ps.unwrap(MockPreparedStatement.class));

            ps.execute();
            ps.close();
            conn.close();
        }
    }
}
