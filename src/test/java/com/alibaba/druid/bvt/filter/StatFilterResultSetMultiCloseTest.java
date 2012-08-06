package com.alibaba.druid.bvt.filter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.druid.util.Histogram;
import com.alibaba.druid.util.JdbcUtils;

public class StatFilterResultSetMultiCloseTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(100);

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_stat() throws Exception {

        final String sql = "SELECT 1";
        Assert.assertTrue(dataSource.isInited());

        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);

        Assert.assertNull(sqlStat);

        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.close();

        sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);
        Assert.assertNotNull(sqlStat);

        Histogram histogram = sqlStat.getExecuteAndResultHoldTimeHistogram();
        Assert.assertEquals("first failed", 1, histogram.getValue(0) + histogram.getValue(1) + histogram.getValue(2));

        rs.close();

        Assert.assertEquals("second failed", 1, histogram.getValue(0) + histogram.getValue(1) + histogram.getValue(2));

        stmt.close();

        conn.close();

        Assert.assertEquals(1, histogram.getValue(0) + histogram.getValue(1) + histogram.getValue(2));
    }
}
