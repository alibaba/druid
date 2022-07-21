package com.alibaba.druid.pool.saphana;

import com.alibaba.druid.DbType;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.SAPHanaValidConnectionChecker;
import junit.framework.TestCase;
import org.junit.Assert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author nukiyoam
 */
public class SAPHanaTest extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:sap://localhost:443/?currentSchema=DBADMIN");
        dataSource.setUsername("admin");
        dataSource.setPassword("admin");
        dataSource.setFilters("log4j");
        dataSource.setValidationQuery("SELECT CURRENT_SCHEMA FROM DUMMY");
        dataSource.setInitialSize(1);
        dataSource.setMaxActive(14);
        dataSource.setMinIdle(1);
        dataSource.setMinEvictableIdleTimeMillis(300 * 1000); // 300 / 10
        dataSource.setTimeBetweenEvictionRunsMillis(180 * 1000); // 180 / 10
        dataSource.setTestWhileIdle(false);
        dataSource.setTestOnBorrow(false);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_saphana() throws Exception {

        dataSource.init();
        Assert.assertEquals(DbType.sap_hana.name(), dataSource.getDbType());
        Assert.assertTrue(dataSource.getValidConnectionChecker() instanceof SAPHanaValidConnectionChecker);


        try {
            Connection conn = dataSource.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT CURRENT_SCHEMA FROM DUMMY");
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
            ResultSet rs = stmt.executeQuery("SELECT CURRENT_SCHEMA FROM DUMMY");
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
            ResultSet rs = stmt.executeQuery("SELECT CURRENT_SCHEMA FROM DUMMY");
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
