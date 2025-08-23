package com.alibaba.druid.bvt.pool;

import static org.junit.Assert.*;


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Iterator;

import junit.framework.TestCase;


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
        assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
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

        assertEquals(true, stmt.isClosed());
        assertEquals(true, rs.isClosed());

        rs.close();
        stmt.close();

        dataSource.shrink();

        JdbcStatManager.getInstance().getDataSourceList();
        assertEquals(1, DruidDataSourceStatManager.getInstance().getDataSourceList().size());

        assertEquals(1, dataSource.getDataSourceStat().getSqlList().size());

        Iterator<JdbcSqlStat> iterator = dataSource.getDataSourceStat().getSqlStatMap().values().iterator();
        JdbcSqlStat sql_0 = iterator.next();

        // there are no JdbcSqlStat of 'SELECT 1' as connection validation will skip all filters now.
        // assertEquals("SELECT 1", sql_0.getSql());
        // assertNull(sql_0.getFile());
        // assertNull(sql_0.getName());

        assertEquals("SELECT NOW()", sql_0.getSql());
        assertEquals("test_file", sql_0.getFile());
        assertEquals("select_now", sql_0.getName());
    }
}
