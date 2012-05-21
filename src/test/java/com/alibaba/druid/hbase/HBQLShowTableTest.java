package com.alibaba.druid.hbase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;

import com.alibaba.druid.util.JdbcUtils;

import junit.framework.TestCase;


public class HBQLShowTableTest extends TestCase {
    public void test_select_0 () throws Exception {
        HDriver driver = new HDriver();
        Connection conn = driver.connect("jdbc:druid-hbase:10.20.153.63", new Properties());
        
        PreparedStatement stmt = conn.prepareStatement("SHOW TABLES");
        stmt.setInt(1, 6);
        
        ResultSet rs = stmt.executeQuery();
        JdbcUtils.printResultSet(rs);
//        while (rs.next()) {
//            System.out.print(rs.getString("name"));
//            System.out.print(' ');
//            System.out.print(rs.getString("owner"));
//            System.out.print(' ');
//            System.out.print(rs.getString("familys"));
//            System.out.print(' ');
//            System.out.print(rs.getLong("maxFileSize"));
//            System.out.print(' ');
//            System.out.print(rs.getLong("memStoreFlushSize"));
//            System.out.print(' ');
//            System.out.print(rs.getString("regionSplitPolicyClassName"));
//            System.out.print(' ');
//            System.out.println();
//        }
        rs.close();
        stmt.close();
        
        conn.close();
    }
}
