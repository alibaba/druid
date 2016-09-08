package com.alibaba.druid.bvt.filter.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class LogFilterTest5 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:derby:classpath:petstore-db");
        dataSource.setFilters("log4j2");
    }
    
    public void test_select() throws Exception {
        Connection conn = dataSource.getConnection();
        
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ITEM WHERE LISTPRICE > 10");
        
        for (int i = 0; i < 10; ++i) {
            ResultSet rs = stmt.executeQuery();
            rs.close();
        }
        
        stmt.close();
        
        conn.close();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }
}
