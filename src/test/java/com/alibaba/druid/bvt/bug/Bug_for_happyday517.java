package com.alibaba.druid.bvt.bug;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.DruidDataSourceStatManager;

public class Bug_for_happyday517 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setFilters("stat");

    }

    protected void tearDown() throws Exception {
        dataSource.close();
        Assert.assertEquals(0, DruidDataSourceStatManager.getInstance().getDataSourceList().size());
    }

    public void test_for_happyday517() throws Exception {
        Connection conn = dataSource.getConnection();
        
        Statement stmt = conn.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
        
        MockStatement mockStmt = stmt.unwrap(MockStatement.class);
        
        Assert.assertEquals(ResultSet.TYPE_SCROLL_SENSITIVE, mockStmt.getResultSetType());
        Assert.assertEquals(ResultSet.CONCUR_UPDATABLE, mockStmt.getResultSetConcurrency());
        
        stmt.close();

        conn.close();
    }
}
