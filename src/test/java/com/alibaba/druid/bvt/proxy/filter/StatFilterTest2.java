package com.alibaba.druid.bvt.proxy.filter;

import java.sql.Connection;
import java.sql.Statement;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcStatContext;
import com.alibaba.druid.stat.JdbcStatManager;

public class StatFilterTest2 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:");
        dataSource.setFilters("stat");
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_0() throws Exception {

        Connection conn = dataSource.getConnection();

        Statement stmt = conn.createStatement();

        setStatSql("Select ?");
        stmt.execute("select 1");

        stmt.close();

        conn.close();
    }

    public final static void setStatSql(String val) {
        JdbcStatContext context = JdbcStatManager.getInstance().getStatContext();
        if (context == null) {
            context = JdbcStatManager.getInstance().createStatContext();
            JdbcStatManager.getInstance().setStatContext(context);
        }

        context.setSql(val);
    }
}
