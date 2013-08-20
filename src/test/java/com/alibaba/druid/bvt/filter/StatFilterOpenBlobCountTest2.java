package com.alibaba.druid.bvt.filter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.mock.MockBlob;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.util.JdbcUtils;

public class StatFilterOpenBlobCountTest2 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
        dataSource.setTestOnBorrow(false);
        dataSource.getProxyFilters().add(new FilterAdapter() {

            @Override
            public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, int columnIndex)
                                                                                                        throws SQLException {
                return new MockBlob();
            }

            @Override
            public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, String columnLabel)
                                                                                                           throws SQLException {
                return new MockBlob();
            }

            @Override
            public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, int columnIndex,
                                              java.util.Map<String, Class<?>> map) throws SQLException {
                return new MockBlob();
            }

            @Override
            public Object resultSet_getObject(FilterChain chain, ResultSetProxy result, String columnIndex,
                                              java.util.Map<String, Class<?>> map) throws SQLException {
                return new MockBlob();
            }
        });
        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_stat() throws Exception {
        Connection conn = dataSource.getConnection();

        String sql = "select 'x'";
        PreparedStatement stmt = conn.prepareStatement("select 'x'");

        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);

        Assert.assertEquals(0, sqlStat.getBlobOpenCount());

        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.getObject(1);
        rs.getObject(2);
        rs.close();
        stmt.close();

        conn.close();

        Assert.assertEquals(2, sqlStat.getBlobOpenCount());

        sqlStat.reset();
        Assert.assertEquals(0, sqlStat.getBlobOpenCount());
    }

    public void test_stat_1() throws Exception {
        Connection conn = dataSource.getConnection();

        String sql = "select 'x'";
        PreparedStatement stmt = conn.prepareStatement("select 'x'");

        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);

        Assert.assertEquals(0, sqlStat.getBlobOpenCount());

        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.getObject("1");
        rs.getObject("2");
        rs.getObject("3");
        rs.close();
        stmt.close();

        conn.close();

        Assert.assertEquals(3, sqlStat.getBlobOpenCount());

        sqlStat.reset();
        Assert.assertEquals(0, sqlStat.getBlobOpenCount());
    }

    public void test_stat_2() throws Exception {
        Connection conn = dataSource.getConnection();

        String sql = "select 'x'";
        PreparedStatement stmt = conn.prepareStatement("select 'x'");

        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);

        Assert.assertEquals(0, sqlStat.getBlobOpenCount());

        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.getObject(1, Collections.<String, Class<?>> emptyMap());
        rs.getObject(2, Collections.<String, Class<?>> emptyMap());
        rs.getObject(3, Collections.<String, Class<?>> emptyMap());
        rs.getObject(4, Collections.<String, Class<?>> emptyMap());
        rs.close();
        stmt.close();

        conn.close();

        Assert.assertEquals(4, sqlStat.getBlobOpenCount());

        sqlStat.reset();
        Assert.assertEquals(0, sqlStat.getBlobOpenCount());
    }

    public void test_stat_4() throws Exception {
        Connection conn = dataSource.getConnection();

        String sql = "select 'x'";
        PreparedStatement stmt = conn.prepareStatement("select 'x'");

        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);

        Assert.assertEquals(0, sqlStat.getBlobOpenCount());

        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.getObject("1", Collections.<String, Class<?>> emptyMap());
        rs.getObject("2", Collections.<String, Class<?>> emptyMap());
        rs.getObject("3", Collections.<String, Class<?>> emptyMap());
        rs.getObject("4", Collections.<String, Class<?>> emptyMap());
        rs.getObject("5", Collections.<String, Class<?>> emptyMap());
        rs.close();
        stmt.close();

        conn.close();

        Assert.assertEquals(5, sqlStat.getBlobOpenCount());

        sqlStat.reset();
        Assert.assertEquals(0, sqlStat.getBlobOpenCount());
    }
}
