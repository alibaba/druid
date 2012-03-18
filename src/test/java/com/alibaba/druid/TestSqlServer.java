package com.alibaba.druid;

import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;


public class TestSqlServer extends TestCase {
    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass;
    private String SQL;

    protected void setUp() throws Exception {
        // jdbcUrl = "jdbc:oracle:thin:@10.20.149.85:1521:ocnauto";
        // user = "alibaba";
        // password = "ccbuauto";
        // SQL = "SELECT * FROM WP_ORDERS WHERE ID = ?";

        jdbcUrl = "jdbc:jtds:sqlserver://10.16.16.28:1433/druid_db";
        user = "sa";
        password = "hello123";
        SQL = "SELECT * FROM AV_INFO WHERE ID = ?";

        driverClass = "net.sourceforge.jtds.jdbc.Driver";
    }
    public void test_0 () throws Exception {
        Class.forName(driverClass);
        
        Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
        conn.close();
    }
}
