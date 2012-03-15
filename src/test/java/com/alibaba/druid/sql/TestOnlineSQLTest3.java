package com.alibaba.druid.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.TestCase;

import com.alibaba.druid.util.JdbcUtils;

public class TestOnlineSQLTest3 extends TestCase {

    private String       url      = "jdbc:mysql://10.20.144.27/dragoon_v25_masterdb";
    private String       user     = "dragoon_test";
    private String       password = "dragoon_test";

    protected Connection conn;

    public void setUp() throws Exception {
        Class.forName("com.mysql.jdbc.Driver");
        conn = DriverManager.getConnection(url, user, password);
    }

    public void tearDown() throws Exception {
        if (conn != null) {
            conn.close();
            conn = null;
        }
    }

    public void test_0() throws Exception {
//        ResultSet rs = conn.getMetaData().getTables(null, null, null, null);
//        JdbcUtils.printResultSet(rs);
        
        String sql = "SELECT User,Password FROM mysql.user;";
        Statement stmt = conn.createStatement();

        ResultSet rs = stmt.executeQuery(sql);
        JdbcUtils.printResultSet(rs);
        
        stmt.close();
    }
}
