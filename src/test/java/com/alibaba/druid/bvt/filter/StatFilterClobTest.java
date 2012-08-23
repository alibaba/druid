package com.alibaba.druid.bvt.filter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockClob;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.proxy.jdbc.ClobProxy;
import com.alibaba.druid.stat.JdbcSqlStat;
import com.alibaba.druid.util.JdbcUtils;

public class StatFilterClobTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();

        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");
        dataSource.setTestOnBorrow(false);

        dataSource.init();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_clob() throws Exception {
        String sql = "select ?";

        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement(sql);

        MockClob clob = new MockClob();
        stmt.setClob(1, clob);

        ResultSet rs = stmt.executeQuery();
        rs.next();
        Assert.assertTrue(rs.getObject(1) instanceof ClobProxy);
        rs.close();

        stmt.close();

        conn.close();
        
        JdbcSqlStat sqlStat = dataSource.getDataSourceStat().getSqlStat(sql);
        Assert.assertNotNull(sqlStat);
        
        Assert.assertEquals(1, sqlStat.getClobOpenCount());
        sqlStat.reset();
        Assert.assertEquals(0, sqlStat.getClobOpenCount());
    }

}
