package com.alibaba.druid.hbase;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Properties;

import junit.framework.TestCase;

public class HBaseDriverInsertTest extends TestCase {

    public void test_select_0() throws Exception {
        HBaseDriver driver = new HBaseDriver();
        Connection conn = driver.connect("jdbc:druid-hbase:10.20.153.63", new Properties());

        for (int i = 0; i < 10; ++i) {
            PreparedStatement stmt = conn.prepareStatement("INSERT INTO test_user (id, name, gender, salary) VALUES (?, ?, ?, ?)");
            stmt.setInt(1, i);
            stmt.setString(2, "user-" + i);
            stmt.setString(3, "M");
            stmt.setBigDecimal(4, new BigDecimal((i + 1) * 1000));

            stmt.execute();

            stmt.close();
        }

        conn.close();
    }
}
