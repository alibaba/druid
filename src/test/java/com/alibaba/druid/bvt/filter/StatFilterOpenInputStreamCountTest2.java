package com.alibaba.druid.bvt.filter;

import java.io.ByteArrayInputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.util.JdbcUtils;

public class StatFilterOpenInputStreamCountTest2 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
        dataSource.setTestOnBorrow(false);
        dataSource.getProxyFilters().add(new FilterAdapter() {

            @Override
            public java.io.InputStream resultSet_getAsciiStream(FilterChain chain, ResultSetProxy result,
                                                                int columnIndex) throws SQLException {
                return new ByteArrayInputStream(new byte[0]);
            }

            @Override
            public java.io.InputStream resultSet_getAsciiStream(FilterChain chain, ResultSetProxy result,
                                                                String columnLabel) throws SQLException {
                return new ByteArrayInputStream(new byte[0]);
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

        Assert.assertEquals(0, sqlStat.getInputStreamOpenCount());

        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.getAsciiStream(1);
        rs.getAsciiStream(2);
        rs.close();
        stmt.close();

        conn.close();

        Assert.assertEquals(2, sqlStat.getInputStreamOpenCount());

        sqlStat.reset();
        Assert.assertEquals(0, sqlStat.getInputStreamOpenCount());
    }

    public void test_stat_1() throws Exception {
        Connection conn = dataSource.getConnection();

        String sql = "select 'x'";
        PreparedStatement stmt = conn.prepareStatement("select 'x'");

        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);

        Assert.assertEquals(0, sqlStat.getInputStreamOpenCount());

        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.getAsciiStream("1");
        rs.getAsciiStream("2");
        rs.getAsciiStream("3");
        rs.close();
        stmt.close();

        conn.close();

        Assert.assertEquals(3, sqlStat.getInputStreamOpenCount());

        sqlStat.reset();
        Assert.assertEquals(0, sqlStat.getInputStreamOpenCount());
    }
}
