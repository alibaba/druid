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

public class StatFilterReadStringLengthTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
        dataSource.setTestOnBorrow(false);
        dataSource.getProxyFilters().add(new FilterAdapter() {

            @Override
            public String resultSet_getString(FilterChain chain, ResultSetProxy result, int columnIndex)
                                                                                                        throws SQLException {
                return "123456";
            }

            @Override
            public String resultSet_getString(FilterChain chain, ResultSetProxy result, String columnIndex)
                                                                                                           throws SQLException {
                return "1234567";
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

        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.getString(1);
        rs.close();
        stmt.close();

        conn.close();

        Assert.assertEquals(6, sqlStat.getReadStringLength());

        sqlStat.reset();
        Assert.assertEquals(0, sqlStat.getReadStringLength());
    }

    public void test_stat_1() throws Exception {
        Connection conn = dataSource.getConnection();

        String sql = "select 'x'";
        PreparedStatement stmt = conn.prepareStatement("select 'x'");

        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);

        Assert.assertEquals(0, sqlStat.getReadStringLength());

        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.getString("1");
        rs.close();
        stmt.close();

        conn.close();

        Assert.assertEquals(7, sqlStat.getReadStringLength());

        sqlStat.reset();
        Assert.assertEquals(0, sqlStat.getReadStringLength());
    }
}
