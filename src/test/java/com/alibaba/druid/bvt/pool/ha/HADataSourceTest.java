package com.alibaba.druid.bvt.pool.ha;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.ha.HADataSource;


public class HADataSourceTest extends TestCase {
    private DruidDataSource dataSourceA;
    private DruidDataSource dataSourceB;
    
    private HADataSource dataSourceHA;
    
    protected void setUp() throws Exception {
        dataSourceA = new DruidDataSource();
        dataSourceA.setUrl("jdbc:mock:x1");
        
        dataSourceB = new DruidDataSource();
        dataSourceB.setUrl("jdbc:mock:x1");
        
        dataSourceHA = new HADataSource();
        dataSourceHA.addDataSource(dataSourceA);
        dataSourceHA.addDataSource(dataSourceB);
    }
    
    protected void tearDown() throws Exception {
        dataSourceHA.close();
    }
    
    public void test_connect_close() throws Exception {
        Connection conn = dataSourceHA.getConnection();
        
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT 1");
        rs.close();
        stmt.close();
        
        conn.close();
    }
}
