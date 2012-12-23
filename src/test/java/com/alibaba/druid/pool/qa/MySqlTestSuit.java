package com.alibaba.druid.pool.qa;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class MySqlTestSuit extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        //System.setProperty("druid.log.rs", "false");
        //System.setProperty("druid.log.stmt", "false");
        
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://192.168.122.26:3306/druid");
        dataSource.setUsername("root");
        dataSource.setPassword("root");
        dataSource.setFilters("stat,log4j");
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(false);
        dataSource.setTestWhileIdle(true);
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_suit() throws Exception {

        createTable();
        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("INSERT INTO T (FID) VALUES (1)");
            stmt.close();
            stmt.close();
            conn.close();
        }
        {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("select * from T where fid = ?");
            stmt.setInt(1, 1);
            ResultSet rs = stmt.executeQuery();
            rs.close();
            stmt.close();
            stmt.close();
            conn.close();
        }
        
        {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            stmt.execute("INSERT INTO T (FID) VALUES (2)");
            stmt.close();
            stmt.close();
            conn.close();
        }
        dropTable();
    }

    private void dropTable() throws SQLException {
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("DROP TABLE T");
            stmt.execute();
            conn.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createTable() throws SQLException {
        try {
            Connection conn = dataSource.getConnection();
            PreparedStatement stmt = conn.prepareStatement("CREATE TABLE IF NOT EXISTS T (FID INT)");
            stmt.execute();
            conn.close();
            stmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
