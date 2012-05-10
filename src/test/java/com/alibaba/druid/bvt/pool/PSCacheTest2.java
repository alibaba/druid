package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.alibaba.druid.util.JdbcUtils;

public class PSCacheTest2 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:x1");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(10);
        dataSource.setSharePreparedStatements(true);
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_0() throws Exception {
        Connection conn = dataSource.getConnection();

        String sql = "select 1";

        {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.close();
        }

        PreparedStatement stmt0 = conn.prepareStatement(sql);
        DruidPooledPreparedStatement pooledStmt0 = (DruidPooledPreparedStatement) stmt0;

        Assert.assertEquals(1, pooledStmt0.getPreparedStatementHolder().getInUseCount());

        PreparedStatement stmt1 = conn.prepareStatement(sql);
        DruidPooledPreparedStatement pooledStmt1 = (DruidPooledPreparedStatement) stmt1;

        Assert.assertSame(pooledStmt0.getPreparedStatementHolder(), pooledStmt1.getPreparedStatementHolder());

        stmt0.close();
        stmt1.close();

        conn.close();
    }

    public void test_txn() throws Exception {
        Connection conn = dataSource.getConnection();
        
        conn.setAutoCommit(true);

        String sql = "select 1";

        {
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.close();
        }

        PreparedStatement stmt0 = conn.prepareStatement(sql);
        DruidPooledPreparedStatement pooledStmt0 = (DruidPooledPreparedStatement) stmt0;

        Assert.assertEquals(1, pooledStmt0.getPreparedStatementHolder().getInUseCount());

        PreparedStatement stmt1 = conn.prepareStatement(sql);
        DruidPooledPreparedStatement pooledStmt1 = (DruidPooledPreparedStatement) stmt1;

        Assert.assertSame(pooledStmt0.getPreparedStatementHolder(), pooledStmt1.getPreparedStatementHolder());

        stmt0.close();
        stmt1.close();

        conn.close();
    }
}
