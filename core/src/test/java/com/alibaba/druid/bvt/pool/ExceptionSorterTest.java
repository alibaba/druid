package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.vendor.MockExceptionSorter;
import com.alibaba.druid.util.JdbcUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static org.junit.jupiter.api.Assertions.*;

public class ExceptionSorterTest {
    private DruidDataSource dataSource;

    @BeforeEach
    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(true);
        dataSource.setDbType("mysql");
        dataSource.setPoolPreparedStatements(true);
        dataSource.setFilters("stat");

        dataSource.init();
    }

    @AfterEach
    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    @Test
    public void test_exceptionSorter() throws Exception {
        assertTrue(dataSource.getExceptionSorter() instanceof MockExceptionSorter,
                dataSource.getExceptionSorterClassName());

        Connection conn = dataSource.getConnection();
        MockConnection mockConn = conn.unwrap(MockConnection.class);

        PreparedStatement stmt = conn.prepareStatement("select 1");

        stmt.execute();

        mockConn.close();

        Exception stmtClosedError = null;
        try {
            stmt.close();
        } catch (Exception ex) {
            stmtClosedError = ex;
        }
        assertNotNull(stmtClosedError);
        conn.close();
    }
}
