package com.alibaba.druid;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.stat.JdbcTraceManager;
import com.alibaba.druid.util.JMXUtils;

public class TestTraceFilter extends TestCase {

    public void test_loop() throws Exception {
        DruidDataSource dataSource = new DruidDataSource();
        dataSource.setFilters("stat,trace");
        dataSource.setUrl("jdbc:mock:");
        
        JMXUtils.register("com.alibaba.dragoon:type=JdbcTraceManager", JdbcTraceManager.getInstance());
        
        for (int i = 0; i < 1000; ++i) {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1");
            rs.next();
            rs.close();
            stmt.close();
            conn.close();
            
            Thread.sleep(1000);
        }
    }
}
