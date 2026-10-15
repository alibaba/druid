package com.alibaba.druid.pool;

import com.alibaba.druid.pool.vendor.MySqlExceptionSorter;
import com.alibaba.druid.util.JdbcUtils;
import com.mysql.jdbc.Driver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class ExeptionSorterTest {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setDriver(new MyDriver());
    }

    @AfterEach
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    @Test
    public void test_dataSource() throws Exception {
        Method method = DruidDataSource.class.getDeclaredMethod("initExceptionSorter");
        method.setAccessible(true);
        method.invoke(dataSource);

        assertEquals(dataSource.getExceptionSorter().getClass(), MySqlExceptionSorter.class);
    }

    public static class MyDriver extends Driver {
        /**
         * Construct a new driver and register it with DriverManager
         *
         * @throws SQLException if a database error occurs.
         */
        public MyDriver() throws SQLException {
        }
    }
}
