package com.alibaba.druid.proxy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

import javax.management.openmbean.TabularData;

import junit.framework.TestCase;

import com.alibaba.druid.stat.JdbcStatManager;
import com.alibaba.fastjson.JSON;

public class TestOracle extends TestCase {
    private String jdbcUrl;
    private String user;
    private String password;

    public void setUp() throws Exception {
        jdbcUrl = "jdbc:wrap-jdbc:filters=default:name=oracleTest:jdbc:oracle:thin:@10.20.149.85:1521:ocnauto";
        jdbcUrl = "jdbc:wrap-jdbc:filters=default:name=oracleTest:jdbc:mock:xx";
        user = "alibaba";
        password = "ccbuauto";
    }
    
    public void test_0 () throws Exception {
        Connection conn = DriverManager.getConnection(jdbcUrl, user, password);
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT 1 FROM DUAL");
        rs.next();
        rs.close();
        stmt.close();
        conn.close();
        
        TabularData dataSourcesList = JdbcStatManager.getInstance().getDataSourceList();
        String text = JSON.toJSONString(dataSourcesList);
        System.out.println(JSON.toJSONString(JSON.parse(text), true));
    }
}
