package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.alibaba.druid.util.JdbcUtils;

public class PSCacheTest extends TestCase {

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

    public void test_noTxn() throws Exception {
        Connection conn = dataSource.getConnection();

        String sql = "select 1";

        PreparedStatement stmt0 = conn.prepareStatement(sql);
        DruidPooledPreparedStatement pooledStmt0 = (DruidPooledPreparedStatement) stmt0;
        stmt0.close();

        PreparedStatement stmt1 = conn.prepareStatement(sql);
        DruidPooledPreparedStatement pooledStmt1 = (DruidPooledPreparedStatement) stmt1;

        Assert.assertEquals(1, pooledStmt1.getPreparedStatementHolder().getInUseCount());
        Assert.assertNotSame(pooledStmt1.getPreparedStatementHolder(), pooledStmt0.getPreparedStatementHolder()); // same

        PreparedStatement stmt2 = conn.prepareStatement(sql);
        DruidPooledPreparedStatement pooledStmt2 = (DruidPooledPreparedStatement) stmt2;

        Assert.assertNotSame(pooledStmt1.getPreparedStatementHolder(), pooledStmt2.getPreparedStatementHolder()); // not same

        stmt1.close();
        stmt2.close();

        conn.close();
    }

    public void test_txn() throws Exception {
        Connection conn = dataSource.getConnection();

        conn.setAutoCommit(false);

        String sql = "select 1";

        PreparedStatement stmt0 = conn.prepareStatement(sql);
        DruidPooledPreparedStatement pooledStmt0 = (DruidPooledPreparedStatement) stmt0;
        stmt0.close();

        PreparedStatement stmt1 = conn.prepareStatement(sql);
        DruidPooledPreparedStatement pooledStmt1 = (DruidPooledPreparedStatement) stmt1;

        Assert.assertEquals(1, pooledStmt1.getPreparedStatementHolder().getInUseCount());
        Assert.assertNotSame(pooledStmt1.getPreparedStatementHolder(), pooledStmt0.getPreparedStatementHolder()); // same

        PreparedStatement stmt2 = conn.prepareStatement(sql);
        DruidPooledPreparedStatement pooledStmt2 = (DruidPooledPreparedStatement) stmt2;

        Assert.assertNotSame(pooledStmt1.getPreparedStatementHolder(), pooledStmt2.getPreparedStatementHolder()); // not same

        stmt1.close();
        stmt2.close();

        conn.close();
    }
}
