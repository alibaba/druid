package com.alibaba.druid.hbase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import junit.framework.TestCase;


public class HBaseDriverTest extends TestCase {
    public void test_0 () throws Exception {
        HBaseDriver driver = new HBaseDriver();
        Connection conn = driver.connect("jdbc:druid-hbase::10.20.153.63", new Properties());
        
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM test");
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            System.out.print(rs.getString("id"));
        }
        rs.close();
        stmt.close();
        
        conn.close();
    }
}
