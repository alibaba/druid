package com.alibaba.druid.bvt.proxy.filter;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.management.openmbean.TabularData;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.stat.JdbcStatManager;

public class StatFilterTest extends TestCase {

    public void setUp() throws Exception {
        JdbcStatManager.getInstance().reset();
    }
    
    public void tearDown() throws Exception {
        JdbcStatManager.getInstance().reset();
    }

    public void test_stat() throws Exception {
        Connection conn = DriverManager.getConnection("jdbc:wrap-jdbc:filters=default:jdbc:mock:xx");
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT 1");
        while (rs.next()) {
            rs.getInt(1);
        }
        rs.close();
        stmt.close();
        
        conn.close();
        
        TabularData sqlList = JdbcStatManager.getInstance().getSqlList();
        Assert.assertEquals(1, sqlList.size());
    }
}
