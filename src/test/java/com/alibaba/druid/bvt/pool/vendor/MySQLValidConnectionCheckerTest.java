package com.alibaba.druid.bvt.pool.vendor;

import java.sql.Connection;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.MySqlValidConnectionChecker;

public class MySQLValidConnectionCheckerTest extends PoolTestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        System.setProperty("druid.mysql.usePingMethod", "false");

        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setDbType("mysql");
        dataSource.setValidationQuery("select 1");
        dataSource.setValidConnectionChecker(new MySqlValidConnectionChecker());
        dataSource.setInitialSize(1);
        dataSource.setTestOnBorrow(true);
    }

    protected void tearDown() throws Exception {
        dataSource.close();

        System.clearProperty("druid.mysql.usePingMethod");
    }

    public void test_connect() throws Exception {
        {
            Connection conn = dataSource.getConnection();
            conn.close();
        }

        MySqlValidConnectionChecker checker = (MySqlValidConnectionChecker) dataSource.getValidConnectionChecker();
        Assert.assertFalse(checker.isUsePingMethod());

        dataSource.setConnectionProperties("druid.mysql.usePingMethod=true");

        Assert.assertTrue(checker.isUsePingMethod());

        Connection conn = dataSource.getConnection();
        conn.close();
    }

}
