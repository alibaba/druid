package com.alibaba.druid.test;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.util.JdbcUtils;
import junit.framework.TestCase;
import org.junit.Assert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by wenshao on 10/12/2016.
 */
public class RaspberryPiMySqlTest extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://raspberrypi_mysql:3306/druid_test_db?allowMultiQueries=true");
        dataSource.setUsername("druid_test");
        dataSource.setPassword("druid_test");
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(true);
        dataSource.setTestWhileIdle(true);
        dataSource.setInitVariants(true);
        dataSource.setInitGlobalVariants(true);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_mysql() throws Exception {
        DruidPooledConnection connection = dataSource.getConnection();

        System.out.println("variables : " + connection.getVariables());
        System.out.println("gloabl variables : " + connection.getGloablVariables());

        connection.close();
    }
}
