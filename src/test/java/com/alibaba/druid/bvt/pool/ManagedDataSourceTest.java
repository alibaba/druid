package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.Assert;

import junit.framework.TestCase;

import com.alibaba.druid.pool.DruidDataSource;

public class ManagedDataSourceTest extends TestCase {

    private DruidDataSource dataSource;

    public void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
    }

    public void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_managed() throws Exception {
        DriverManager.getConnection("jdbc:mock:aaa");
        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }

        {
            dataSource.setEnable(false);
            SQLException error = null;
            try {
                dataSource.getConnection();
            } catch (SQLException ex) {
                error = ex;
            }
            Assert.assertNotNull(error);
        }

        dataSource.setEnable(true);
        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }
    }
}
