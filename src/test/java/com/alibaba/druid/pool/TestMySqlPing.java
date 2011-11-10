package com.alibaba.druid.pool;

import java.sql.DriverManager;
import java.sql.SQLException;

import junit.framework.TestCase;

public class TestMySqlPing extends TestCase {

    public void test_ping() throws Exception {
        String url = "jdbc:mysql://10.20.147.142:3308/dragoon_v25_masterdb";
        String user = "dragoon_admin";
        String password = "dragoon_root";

        Class.forName("com.mysql.jdbc.Driver");

        com.mysql.jdbc.Connection conn = (com.mysql.jdbc.Connection) DriverManager.getConnection(url, user, password);
        ping(conn);
        conn.close();
    }

    public void ping(com.mysql.jdbc.Connection conn) throws Exception {
        System.out.println(conn.getClass());
        conn.ping();
    }
}
