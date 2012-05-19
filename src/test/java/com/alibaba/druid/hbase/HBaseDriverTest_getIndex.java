package com.alibaba.druid.hbase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import junit.framework.TestCase;


public class HBaseDriverTest_getIndex extends TestCase {
    public void test_select_0 () throws Exception {
        HDriver driver = new HDriver();
        Connection conn = driver.connect("jdbc:druid-hbase:10.20.153.63", new Properties());
        
        PreparedStatement stmt = conn.prepareStatement("SELECT id, name, gender, salary FROM test_user where id >= 3 and id <= ?");
        stmt.setInt(1, 6);
        
        ResultSet rs = stmt.executeQuery();
        while (rs.next()) {
            System.out.print(rs.getInt(1));
            System.out.print(' ');
            System.out.print(rs.getString(2));
            System.out.print(' ');
            System.out.print(rs.getString(3));
            System.out.print(' ');
            System.out.print(rs.getBigDecimal(4));
            System.out.println();
        }
        rs.close();
        stmt.close();
        
        conn.close();
    }
}
