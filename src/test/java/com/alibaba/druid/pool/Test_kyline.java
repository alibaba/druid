package com.alibaba.druid.pool;

import java.sql.Connection;
import java.sql.DriverManager;

import junit.framework.TestCase;

public class Test_kyline extends TestCase {

    private String url      = "jdbc:mysql://10.20.141.150:8066/amoeba";
    private String user     = "root";
    private String password = "12345";
    private String driver   = "com.mysql.jdbc.Driver";

    public void test_0() throws Exception {
        DruidDataSource ds = new DruidDataSource();
        ds.setUrl(url);
        ds.setUsername(user);
        ds.setPassword(password);
        ds.setDriverClassName(driver);

        DriverManager.getConnection(url, user, password);

        Connection conn = ds.getConnection();
        conn.close();

        ds.close();
    }
}
