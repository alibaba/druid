package com.alibaba.druid.hbase;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;

import junit.framework.TestCase;

public class HBaseDriverInsertTest extends TestCase {

    public void test_select_0() throws Exception {
        HBaseDriver driver = new HBaseDriver();
        Connection conn = driver.connect("jdbc:druid-hbase:10.20.153.63", new Properties());

        PreparedStatement stmt = conn.prepareStatement("INSERT INTO test_user (id, name, gender) VALUES (?, ?, ?)");
        stmt.setInt(1, 33);
        stmt.setString(2, "ljw");
        stmt.setString(3, "M");
        

        stmt.execute();

        stmt.close();

        conn.close();
    }
}
