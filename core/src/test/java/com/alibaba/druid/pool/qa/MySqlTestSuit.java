package com.alibaba.druid.pool.qa;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class MySqlTestSuit {
    private DruidDataSource dataSource;

    @BeforeEach
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

    @AfterEach
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    @Test
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
