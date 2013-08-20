package com.alibaba.druid.bvt.filter;

import java.sql.Clob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.filter.FilterAdapter;
import com.alibaba.druid.filter.FilterChain;
import com.alibaba.druid.mock.MockClob;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ClobProxyImpl;
import com.alibaba.druid.proxy.jdbc.ResultSetProxy;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.util.JdbcUtils;

public class StatFilterOpenClobCountTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
        dataSource.setTestOnBorrow(false);
        dataSource.getProxyFilters().add(new FilterAdapter() {

            @Override
            public Clob resultSet_getClob(FilterChain chain, ResultSetProxy result, int columnIndex)
                                                                                                    throws SQLException {
                return new ClobProxyImpl(result.getStatementProxy().getConnectionProxy().getDirectDataSource(),
                                         result.getStatementProxy().getConnectionProxy(), new MockClob());
            }

            @Override
            public Clob resultSet_getClob(FilterChain chain, ResultSetProxy result, String columnLabel)
                                                                                                       throws SQLException {
                return new ClobProxyImpl(result.getStatementProxy().getConnectionProxy().getDirectDataSource(),
                                         result.getStatementProxy().getConnectionProxy(), new MockClob());
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

        Assert.assertEquals(0, sqlStat.getClobOpenCount());

        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.getClob(1);
        rs.getClob(2);
        rs.close();
        stmt.close();

        conn.close();

        Assert.assertEquals(2, sqlStat.getClobOpenCount());

        sqlStat.reset();
        Assert.assertEquals(0, sqlStat.getClobOpenCount());
    }

    public void test_stat_1() throws Exception {
        Connection conn = dataSource.getConnection();

        String sql = "select 'x'";
        PreparedStatement stmt = conn.prepareStatement("select 'x'");

        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);

        Assert.assertEquals(0, sqlStat.getClobOpenCount());

        ResultSet rs = stmt.executeQuery();
        rs.next();
        rs.getClob("1");
        rs.getClob("2");
        rs.getClob("3");
        rs.close();
        stmt.close();

        conn.close();

        Assert.assertEquals(3, sqlStat.getClobOpenCount());

        sqlStat.reset();
        Assert.assertEquals(0, sqlStat.getClobOpenCount());
    }
}
