package com.alibaba.druid.hbase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import com.alibaba.druid.hdriver.HDriver;

import junit.framework.TestCase;


public class HBaseDriverTest_notRowFilter extends TestCase {
    public void test_select_0 () throws Exception {
        HDriver driver = new HDriver();
        Connection conn = driver.connect("jdbc:druid-hbase:10.20.153.63", new Properties());
        
        PreparedStatement stmt = conn.prepareStatement("SELECT id, name, gender, salary FROM test_user where name <= ?");
        stmt.setString(1, "user-5");
        
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            System.out.print(rs.getInt("id"));
            System.out.print(' ');
            System.out.print(rs.getString("name"));
            System.out.print(' ');
            System.out.print(rs.getString("gender"));
            System.out.print(' ');
            System.out.print(rs.getBigDecimal("salary"));
            System.out.println();
        }
        rs.close();
        stmt.close();
        
        conn.close();
    }
}
