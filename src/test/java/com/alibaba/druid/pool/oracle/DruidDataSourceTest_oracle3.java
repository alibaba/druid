package com.alibaba.druid.pool.oracle;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.OracleValidConnectionChecker;

/**
 * 这个场景测试minIdle > maxActive
 * 
 * @author wenshao [szujobs@hotmail.com]
 */
public class DruidDataSourceTest_oracle3 extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:oracle:thin:@localhost:1521:sonardb");
        dataSource.setUsername("jira");
        dataSource.setPassword("jira");
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(14);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(false);
        dataSource.setTestOnBorrow(false);
        dataSource.setValidationQuery("SELECT 1 FROM DUAL");
        dataSource.setFilters("stat,log4j");
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_error() throws Exception {
        dataSource.init();
        Assert.assertTrue(dataSource.isOracle());
        Assert.assertTrue(dataSource.getValidConnectionChecker() instanceof OracleValidConnectionChecker);

        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1 FROM DUAL");
            while (rs.next()) {

            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1 FROM DUAL");
            while (rs.next()) {

            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT 1 FROM DUAL");
            while (rs.next()) {

            }
            rs.close();
            stmt.close();
            conn.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
