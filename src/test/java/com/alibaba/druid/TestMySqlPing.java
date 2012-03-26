package com.alibaba.druid;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;
import oracle.jdbc.OracleConnection;


public class TestMySqlPing extends TestCase {
    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass;

    protected void setUp() throws Exception {
        // jdbcUrl = "jdbc:oracle:thin:@10.20.149.85:1521:ocnauto";
        // user = "alibaba";
        // password = "ccbuauto";
        // SQL = "SELECT * FROM WP_ORDERS WHERE ID = ?";

        jdbcUrl = "jdbc:mysql://10.20.147.142:3308/dragoon_v25_masterdb";
        user = "dragoon_admin";
        password = "dragoon_root";

        driverClass = "com.mysql.jdbc.Driver";
    }

    public void test_o() throws Exception {
        Class.forName(driverClass);

        Connection conn = DriverManager.getConnection(jdbcUrl, user, password);

        com.mysql.jdbc.Connection mysqlConn = (com.mysql.jdbc.Connection) conn;

        for (int i = 0; i < 10; ++i) {
            ping_1000(mysqlConn);
            select_1000(mysqlConn);
        }

        conn.close();
    }

    private void ping_1000(com.mysql.jdbc.Connection oracleConn) throws SQLException {
        long startMillis = System.currentTimeMillis();
        final int COUNT = 10000;
        for (int i = 0; i < COUNT; ++i) {
            pring(oracleConn);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("ping : " + millis);
    }
    
    private void select_1000(com.mysql.jdbc.Connection oracleConn) throws SQLException {
        long startMillis = System.currentTimeMillis();
        final int COUNT = 10000;
        for (int i = 0; i < COUNT; ++i) {
            select(oracleConn);
        }
        long millis = System.currentTimeMillis() - startMillis;
        System.out.println("select : " + millis);
    }

    public void pring(com.mysql.jdbc.Connection oracleConn) throws SQLException {
        oracleConn.ping();
    }

    public void select(com.mysql.jdbc.Connection oracleConn) throws SQLException {
        Statement stmt = oracleConn.createStatement();
        stmt.execute("SELECT 'x'");
        stmt.close();
    }
}
