package com.alibaba.druid.bvt.pool.ha;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.HADataSource;

public class HADataSourceTest extends TestCase {

    private DruidDataSource dataSourceA;
    private DruidDataSource dataSourceB;

    private HADataSource    dataSourceHA;

    protected void setUp() throws Exception {
        dataSourceA = new DruidDataSource();
        dataSourceA.setUrl("jdbc:mock:x1");

        dataSourceB = new DruidDataSource();
        dataSourceB.setUrl("jdbc:mock:x1");

        dataSourceHA = new HADataSource();
        dataSourceHA.addDataSource(dataSourceA);
        dataSourceHA.addDataSource(dataSourceB);
    }

    protected void tearDown() throws Exception {
        dataSourceHA.close();
    }

    public void test_0() throws Exception {
        Connection conn = dataSourceHA.getConnection();

        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT 1");
        rs.close();
        stmt.close();

        conn.close();
    }

    public void test_1() throws Exception {
        Connection conn = dataSourceHA.getConnection();

        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
        ResultSet rs = stmt.executeQuery("SELECT 1");
        rs.close();

        MockStatement mockStmt = stmt.unwrap(MockStatement.class);
        Assert.assertEquals(ResultSet.TYPE_SCROLL_INSENSITIVE, mockStmt.getResultSetType());
        Assert.assertEquals(ResultSet.CONCUR_UPDATABLE, mockStmt.getResultSetConcurrency());
        Assert.assertEquals(ResultSet.TYPE_SCROLL_INSENSITIVE, stmt.getResultSetType());
        Assert.assertEquals(ResultSet.CONCUR_UPDATABLE, stmt.getResultSetConcurrency());
        stmt.close();

        conn.close();
    }

    public void test_2() throws Exception {
        Connection conn = dataSourceHA.getConnection();

        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE,
                                              ResultSet.CLOSE_CURSORS_AT_COMMIT);
        ResultSet rs = stmt.executeQuery("SELECT 1");
        rs.close();
        
        MockStatement mockStmt = stmt.unwrap(MockStatement.class);
        Assert.assertEquals(ResultSet.TYPE_SCROLL_INSENSITIVE, mockStmt.getResultSetType());
        Assert.assertEquals(ResultSet.CONCUR_UPDATABLE, mockStmt.getResultSetConcurrency());
        Assert.assertEquals(ResultSet.CLOSE_CURSORS_AT_COMMIT, mockStmt.getResultSetHoldability());
        Assert.assertEquals(ResultSet.TYPE_SCROLL_INSENSITIVE, stmt.getResultSetType());
        Assert.assertEquals(ResultSet.CONCUR_UPDATABLE, stmt.getResultSetConcurrency());
        Assert.assertEquals(ResultSet.CLOSE_CURSORS_AT_COMMIT, stmt.getResultSetHoldability());
        
        stmt.close();

        conn.close();
    }
}
