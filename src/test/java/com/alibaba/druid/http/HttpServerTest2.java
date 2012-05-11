package com.alibaba.druid.http;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class HttpServerTest2 extends TestCase {
    private DruidDataSource dataSource;
    
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xx");
        dataSource.setFilters("stat");
        dataSource.init();
        
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select 1");
            ResultSet rs = stmt.executeQuery();
            rs.close();
            stmt.close();
            conn.close();
        }
    }
    
    public void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_httpServer() throws Exception {
        HttpServer server = new HttpServer();
        server.start();
        server.join();
    }
}
