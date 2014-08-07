package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.stat.JdbcStatManager;

public class TestOnBorrowFileAndNameTest extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        DruidDataSourceStatManager.clear();

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setName("com.alibaba.dragoon.monitor");
        dataSource.setMinIdle(0);
        dataSource.setPoolPreparedStatements(false);
        dataSource.setTestOnBorrow(true);
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setFilters("stat");

    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_stat() throws Exception {

        String sql = "SELECT NOW()";
        
        JdbcSqlStat.setContextSqlName("select_now");
        JdbcSqlStat.setContextSqlFile("test_file");

        Connection conn = dataSource.getConnection();
        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();

        conn.close();

        Assert.assertEquals(true, stmt.isClosed());
        Assert.assertEquals(true, rs.isClosed());

        rs.close();
        stmt.close();

        dataSource.shrink();

        JdbcStatManager.getInstance().getDataSourceList();
        Assert.assertEquals(1, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
        
        Assert.assertEquals(2, dataSource.getDataSourceStat().getSqlList().size());
        
        Iterator<JdbcSqlStat> iterator = dataSource.getDataSourceStat().getSqlStatMap().values().iterator();
        JdbcSqlStat sql_0 = iterator.next();
        JdbcSqlStat sql_1 = iterator.next();
        
        Assert.assertEquals("SELECT 1", sql_0.getSql());
        Assert.assertNull(sql_0.getFile());
        Assert.assertNull(sql_0.getName());
        
        Assert.assertEquals("SELECT NOW()", sql_1.getSql());
        Assert.assertEquals("test_file", sql_1.getFile());
        Assert.assertEquals("select_now", sql_1.getName());
    }
}
