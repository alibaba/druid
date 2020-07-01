package com.alibaba.druid.pool.mysql;

import com.alibaba.druid.pool.DruidDataSource;
import junit.framework.TestCase;
import org.junit.Assert;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Created by wenshao on 16/8/5.
 */
public class MySqlTest extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://raspberrypi_mysql:3306/druid_test_db?allowMultiQueries=true");
        dataSource.setUsername("druid_test");
        dataSource.setPassword("druid_test");
        dataSource.setFilters("log4j");
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(true);
        dataSource.setTestWhileIdle(true);
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_mysql() throws Exception {
        Connection connection = dataSource.getConnection();

        System.out.println("----------- : " + connection.unwrap(Connection.class).getClass());

        Statement stmt = connection.createStatement();
        stmt.execute("select 1;select 1");

        ResultSet rs = stmt.getResultSet();
        Assert.assertFalse(rs.isClosed());

        Assert.assertTrue(stmt.getMoreResults());
        Assert.assertTrue(rs.isClosed());

        ResultSet rs2 = stmt.getResultSet();
        Assert.assertFalse(rs2.isClosed());
        rs2.close();
        Assert.assertTrue(rs2.isClosed());

        stmt.close();

        connection.close();
    }
}
