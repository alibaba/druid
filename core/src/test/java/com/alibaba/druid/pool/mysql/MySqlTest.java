package com.alibaba.druid.pool.mysql;

import com.alibaba.druid.pool.DruidDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Created by wenshao on 16/8/5.
 */
public class MySqlTest {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mysql://127.0.0.1:3306/druid_test_db?allowMultiQueries=true");
        dataSource.setUsername("druid_test");
        dataSource.setPassword("druid_test");
        dataSource.setFilters("log4j");
        dataSource.setValidationQuery("SELECT 1");
        dataSource.setTestOnBorrow(true);
        dataSource.setTestWhileIdle(true);
    }

    @AfterEach
    protected void tearDown() throws Exception {
        dataSource.close();
    }

    @Test
    public void test_mysql() throws Exception {
        Connection connection = dataSource.getConnection();

        System.out.println("----------- : " + connection.unwrap(Connection.class).getClass());

        Statement stmt = connection.createStatement();
        stmt.execute("select 1;select 1");

        ResultSet rs = stmt.getResultSet();
        assertFalse(rs.isClosed());

        assertTrue(stmt.getMoreResults());
        assertTrue(rs.isClosed());

        ResultSet rs2 = stmt.getResultSet();
        assertFalse(rs2.isClosed());
        rs2.close();
        assertTrue(rs2.isClosed());

        stmt.close();

        connection.close();
    }
}
