package com.alibaba.druid.bvt.pool.vendor;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.vendor.YashanDbValidConnectionChecker;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

public class YashanDbValidConnectionCheckerTest {

    @Test
    public void test_valid_connection() throws Exception {
        YashanDbValidConnectionChecker checker = new YashanDbValidConnectionChecker();
        MockConnection conn = new MockConnection();

        assertTrue(checker.isValidConnection(conn, "SELECT 'x' FROM DUAL", 10));
    }

    @Test
    public void test_default_query_null() throws Exception {
        YashanDbValidConnectionChecker checker = new YashanDbValidConnectionChecker();
        MockConnection conn = new MockConnection();

        // When validateQuery is null, should use default "SELECT 'x' FROM DUAL"
        assertTrue(checker.isValidConnection(conn, null, 10));
    }

    @Test
    public void test_default_query_empty() throws Exception {
        YashanDbValidConnectionChecker checker = new YashanDbValidConnectionChecker();
        MockConnection conn = new MockConnection();

        // When validateQuery is empty, should use default "SELECT 'x' FROM DUAL"
        assertTrue(checker.isValidConnection(conn, "", 10));
    }

    @Test
    public void test_closed_connection() throws Exception {
        YashanDbValidConnectionChecker checker = new YashanDbValidConnectionChecker();
        MockConnection conn = new MockConnection();
        conn.close();

        assertFalse(checker.isValidConnection(conn, "SELECT 'x' FROM DUAL", 10));
    }

    @Test
    public void test_error_connection() throws Exception {
        YashanDbValidConnectionChecker checker = new YashanDbValidConnectionChecker();
        MockConnection conn = new MockConnection();
        conn.setError(new SQLException("test error"));

        SQLException error = null;
        try {
            checker.isValidConnection(conn, "SELECT 'x' FROM DUAL", 10);
        } catch (SQLException ex) {
            error = ex;
        }
        assertNotNull(error);
        assertSame(error, conn.getError());
    }
}
