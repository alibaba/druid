package com.alibaba.druid.pvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class PSCacheTest5 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:x1");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(10);
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

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
            
            Assert.assertSame(mockStmt, ps.unwrap(MockPreparedStatement.class));
            
            ps.execute();
            ps.close();
            conn.close();
        }
    }
}
