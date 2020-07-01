package com.alibaba.druid.bvt.filter;

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

public class StatFilterReadBytesLengthTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
        dataSource.setTestOnBorrow(false);
        dataSource.getProxyFilters().add(new FilterAdapter() {

            @Override
            public byte[] resultSet_getBytes(FilterChain chain, ResultSetProxy result, int columnIndex)
                                                                                                       throws SQLException {
                return new byte[6];
            }

            @Override
            public byte[] resultSet_getBytes(FilterChain chain, ResultSetProxy result, String columnIndex)
                                                                                                          throws SQLException {
                return new byte[7];
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

        Assert.assertEquals(0, sqlStat.getReadStringLength());
        Assert.assertEquals(0, sqlStat.getReadBytesLength());

        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.getBytes(1);
        rs.close();
        stmt.close();

        conn.close();

        Assert.assertEquals(0, sqlStat.getReadStringLength());
        Assert.assertEquals(6, sqlStat.getReadBytesLength());

        sqlStat.reset();
        Assert.assertEquals(0, sqlStat.getReadStringLength());
        Assert.assertEquals(0, sqlStat.getReadBytesLength());
    }

    public void test_stat_1() throws Exception {
        Connection conn = dataSource.getConnection();

        String sql = "select 'x'";
        PreparedStatement stmt = conn.prepareStatement("select 'x'");

        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);

        Assert.assertEquals(0, sqlStat.getReadStringLength());
        Assert.assertEquals(0, sqlStat.getReadBytesLength());

        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.getBytes("1");
        rs.close();
        stmt.close();

        conn.close();

        Assert.assertEquals(0, sqlStat.getReadStringLength());
        Assert.assertEquals(7, sqlStat.getReadBytesLength());

        sqlStat.reset();
        Assert.assertEquals(0, sqlStat.getReadStringLength());
        Assert.assertEquals(0, sqlStat.getReadBytesLength());
    }
}
