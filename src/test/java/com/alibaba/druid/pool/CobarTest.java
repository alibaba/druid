package com.alibaba.druid.pool;

import java.sql.Connection;

import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;
import com.alibaba.druid.util.JdbcUtils;

import junit.framework.TestCase;


public class CobarTest extends TestCase {
    private String jdbcUrl;
    private String user;
    private String password;
    private String driverClass;
    private DruidDataSource dataSource;
    
    protected void setUp() throws Exception {
        jdbcUrl = "jdbc:mysql://10.20.146.88:8066/pt_dragoon_masterdb_test?useUnicode=true&characterEncoding=UTF-8";
        user = "pt_dragoon_test";
        password = "pt_dragoon_test";
        driverClass = "com.mysql.jdbc.Driver";
    }
    
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_0() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setDriverClassName(driverClass);
        dataSource.setUrl(jdbcUrl);
        dataSource.setUsername(user);
        dataSource.setPassword(password);
        dataSource.setFilters("stat");
        dataSource.setExceptionSorter(MySqlExceptionSorter.class.getName());

        Connection conn = dataSource.getConnection();
        
        conn.close();
        
        dataSource.close();
    }
}
