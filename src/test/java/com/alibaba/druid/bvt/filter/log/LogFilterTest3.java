package com.alibaba.druid.bvt.filter.log;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class LogFilterTest3 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        System.setProperty("druid.log.stmt.executableSql", "true");
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:derby:classpath:petstore-db");
        dataSource.setFilters("log4j,slf4j");
    }
    
    public void test_select() throws Exception {
        Connection conn = dataSource.getConnection();
        
        PreparedStatement stmt = conn.prepareStatement("SELECT * FROM ITEM WHERE LISTPRICE > ?");
        stmt.setInt(1, 10);
        
        for (int i = 0; i < 10; ++i) {
            ResultSet rs = stmt.executeQuery();
            rs.close();
        }
        
        stmt.close();
        
        conn.close();
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
        
        System.clearProperty("druid.log.stmt.executableSql");
    }
}
