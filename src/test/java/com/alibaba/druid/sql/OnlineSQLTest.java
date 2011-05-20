package com.alibaba.druid.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.TestCase;

public class OnlineSQLTest extends TestCase {
    private String url = "jdbc:mysql://10.20.129.146/dragoon_v25monitordb_online";
    private String user = "dragoon";
    private String password = "dragoon";
    
    
    
    public void test_list_sql() throws Exception {
        Connection conn = DriverManager.getConnection(url, user, password);
        
        int count = 0;
        String sql = "SELECT id, value FROM m_sql_const";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(sql);
        while (rs.next()) {
            String value = rs.getString(2);
            System.out.println(value);
            System.out.println();
            count++;
        }
        rs.close();
        stmt.close();
        
        System.out.println("COUNT : " + count);
        
        conn.close();
    }
}
