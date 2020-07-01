package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;

public class DruidDataSourceTest_initSql extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        List<Object> sqlList = new ArrayList<Object>();
        sqlList.add("select 123");
        sqlList.add(null);
        sqlList.add("");

        dataSource.setConnectionInitSqls(sqlList);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void testDefault() throws Exception {
        Connection conn = dataSource.getConnection();

        MockConnection mockConn = conn.unwrap(MockConnection.class);

        Assert.assertEquals("select 123", mockConn.getLastSql());

        conn.close();
    }

}
