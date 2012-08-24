package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcSqlStat;

public class DumpTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setFilters("stat");

        dataSource.setPoolPreparedStatements(true);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void testWrap() throws Exception {
        String sql = "select ?";
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "xxx");
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.close();

        conn.close();

        dataSource.dump();
    }

    public void testToString() throws Exception {
        String sql = "select ?, ?";
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "xxx");
        stmt.setInt(2, 33);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.close();

        conn.close();

        dataSource.toString();
    }

    public void test_getPoolingConnectionInfo() throws Exception {
        String sql = "select ?, ?";
        Connection conn = dataSource.getConnection();
        Connection conn1 = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "xxx");
        stmt.setInt(2, 33);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.close();

        conn.close();
        conn1.close();

        List<Map<String, Object>> poolingList = dataSource.getPoolingConnectionInfo();
        Assert.assertEquals(2, poolingList.size());
    }

    public void test_getStatData() throws Exception {
        String sql = "select ?, ?";
        Connection conn = dataSource.getConnection();
        Connection conn1 = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setString(1, "xxx");
        stmt.setInt(2, 33);
        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.close();

        conn.close();
        conn1.close();

        Map<String, Object> statData = dataSource.getStatData();
        Assert.assertEquals(2, statData.get("PoolingCount"));
        Assert.assertEquals(2, statData.get("PoolingPeak"));
        Assert.assertEquals(2L, statData.get("LogicConnectCount"));
        Assert.assertEquals(2L, statData.get("LogicCloseCount"));
        Assert.assertEquals(0L, statData.get("LogicConnectErrorCount"));

        Assert.assertEquals(1, dataSource.getSqlStatMap().size());
        JdbcSqlStat sqlStat = dataSource.getSqlStatMap().get(sql);
        Assert.assertNotNull(sqlStat);
        Assert.assertNotNull(dataSource.getSqlStat(sqlStat.getId()));
        Assert.assertNotNull(dataSource.getSqlStat((int) sqlStat.getId()));
    }

}
