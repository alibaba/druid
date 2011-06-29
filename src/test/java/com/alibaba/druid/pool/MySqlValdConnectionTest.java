package com.alibaba.druid.pool;

import java.sql.Connection;

import junit.framework.TestCase;

public class MySqlValdConnectionTest extends TestCase {
    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass;
    
    public void setUp () throws Exception {
        jdbcUrl = "jdbc:mysql://10.20.129.146/dragoon_v25masterdb?useUnicode=true&characterEncoding=UTF-8";
        user = "dragoon25";
        password = "dragoon25";
        driverClass = "com.mysql.jdbc.Driver";
    }
    public void test_0 () throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setFilters("stat");
        
        Connection conn = dataSource.getConnection();
        conn.close();
    }
}
