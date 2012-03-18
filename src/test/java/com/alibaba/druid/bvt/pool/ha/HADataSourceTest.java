package com.alibaba.druid.bvt.pool.ha;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.HADataSource;
import com.alibaba.druid.pool.ha.MultiDataSourceStatement;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class HADataSourceTest extends TestCase {

    private DruidDataSource dataSourceA;
    private DruidDataSource dataSourceB;

    private HADataSource    dataSourceHA;

    protected void setUp() throws Exception {
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
        
        dataSourceA = new DruidDataSource();
        dataSourceA.setUrl("jdbc:mock:ha1");
        dataSourceA.setFilters("trace");

        dataSourceB = new DruidDataSource();
        dataSourceB.setUrl("jdbc:mock:ha2");
        dataSourceB.setFilters("stat");

        dataSourceHA = new HADataSource();
        dataSourceHA.setMaster(dataSourceA);
        dataSourceHA.setSlave(dataSourceB);
    }

    protected void tearDown() throws Exception {
        dataSourceHA.close();
        
        for (DruidDataSource dataSource : DruidDataSourceStatManager.getDruidDataSourceInstances()) {
            dataSource.close();
        }
        
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_createStatement_0() throws Exception {
        Connection conn = dataSourceHA.getConnection();

        Statement stmt = conn.createStatement();

        stmt.setMaxFieldSize(100);
        stmt.setMaxRows(201);
        stmt.setEscapeProcessing(true);
        stmt.setQueryTimeout(101);
        stmt.setCursorName("cName");
        stmt.setFetchDirection(ResultSet.FETCH_REVERSE);
        stmt.setFetchSize(202);
        {
            MultiDataSourceStatement dsStmt = stmt.unwrap(MultiDataSourceStatement.class);
            Assert.assertTrue(dsStmt.getId() > 0);

            Assert.assertEquals(100, stmt.getMaxFieldSize());
            Assert.assertEquals(201, stmt.getMaxRows());
            Assert.assertEquals(true, dsStmt.isEscapeProcessing().booleanValue());
            Assert.assertEquals(101, stmt.getQueryTimeout());
            Assert.assertEquals("cName", dsStmt.getCursorName());
            Assert.assertEquals(ResultSet.FETCH_REVERSE, stmt.getFetchDirection());
            Assert.assertEquals(202, stmt.getFetchSize());
        }

        ResultSet rs = stmt.executeQuery("SELECT 1");
        rs.close();

        MockStatement mockStmt = stmt.unwrap(MockStatement.class);
        Assert.assertEquals(100, mockStmt.getMaxFieldSize());
        Assert.assertEquals(201, mockStmt.getMaxRows());
        Assert.assertEquals(true, mockStmt.isEscapeProcessing());
        Assert.assertEquals(101, mockStmt.getQueryTimeout());
        Assert.assertEquals("cName", mockStmt.getCursorName());
        Assert.assertEquals(ResultSet.FETCH_REVERSE, mockStmt.getFetchDirection());
        Assert.assertEquals(202, mockStmt.getFetchSize());

        {
            MultiDataSourceStatement dsStmt = stmt.unwrap(MultiDataSourceStatement.class);
            Assert.assertTrue(dsStmt.getId() > 0);

            Assert.assertEquals(100, stmt.getMaxFieldSize());
            Assert.assertEquals(201, stmt.getMaxRows());
            Assert.assertEquals(true, dsStmt.isEscapeProcessing().booleanValue());
            Assert.assertEquals(101, stmt.getQueryTimeout());
            Assert.assertEquals("cName", dsStmt.getCursorName());
            Assert.assertEquals(ResultSet.FETCH_REVERSE, stmt.getFetchDirection());
            Assert.assertEquals(202, stmt.getFetchSize());
        }

        stmt.close();

        conn.close();
    }

    public void test_createStatement_1() throws Exception {
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

    public void test_createStatement_2() throws Exception {
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

    public void test_createStatement_3() throws Exception {
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

    public void test_createStatement_4() throws Exception {
        Connection conn = dataSourceHA.getConnection();

        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE,
                                              ResultSet.CLOSE_CURSORS_AT_COMMIT);
        stmt.executeUpdate("SET @user = 'xxx'");

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

    public void test_close() throws Exception {
        Connection conn = dataSourceHA.getConnection();

        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE,
                                              ResultSet.CLOSE_CURSORS_AT_COMMIT);

        stmt.getMaxFieldSize();
        stmt.getMaxRows();
        stmt.cancel();
        stmt.getWarnings();
        stmt.clearWarnings();
        stmt.getResultSet();
        stmt.getUpdateCount();
        stmt.getMoreResults();
        stmt.getMoreResults(1);
        stmt.getResultSetConcurrency();
        stmt.getResultSetHoldability();
        stmt.getResultSetType();
        stmt.getConnection();
        stmt.getGeneratedKeys();
        stmt.isClosed();
        stmt.close();

        conn.close();
    }
}
