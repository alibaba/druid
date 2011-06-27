package com.alibaba.druid.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.TestCase;

public class TestOnlineSQLTest3 extends TestCase {

    private String       url      = "jdbc:mysql://10.249.192.221/test";
    private String       user     = "dragoon";
    private String       password = "dragoon";

    protected Connection conn;

    public void setUp() throws Exception {
        conn = DriverManager.getConnection(url, user, password);
    }

    public void tearDown() throws Exception {
        if (conn != null) {
            conn.close();
            conn = null;
        }
    }
    
    public void test_0 () throws Exception {
        String sql = "SELECT * FROM m_sql_const";
        Statement stmt = conn.createStatement();
        
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            long id = rs.getLong(1);
            String value = rs.getString(2);
            handle(id, value);
        }
        rs.close();
        
        stmt.close();
    }
    
    void handle(long id, String value) {
        String sql = value.toString();
        
        
        
        System.out.println(sql);
    }
}
