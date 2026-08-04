package com.alibaba.druid.bvt.pool.vendor;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.vendor.YashanDbValidConnectionChecker;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Properties;

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

    @Test
    public void test_fallback_timeout_zero() throws Exception {
        YashanDbValidConnectionChecker checker = new YashanDbValidConnectionChecker();
        MockConnection conn = new MockConnection();

        // When validationQueryTimeout is 0, should fall back to default timeout (1 second)
        assertTrue(checker.isValidConnection(conn, "SELECT 'x' FROM DUAL", 0));
    }

    @Test
    public void test_fallback_timeout_negative() throws Exception {
        YashanDbValidConnectionChecker checker = new YashanDbValidConnectionChecker();
        MockConnection conn = new MockConnection();

        // When validationQueryTimeout is negative, should fall back to default timeout (1 second)
        assertTrue(checker.isValidConnection(conn, "SELECT 'x' FROM DUAL", -1));
    }

    @Test
    public void test_set_timeout() throws Exception {
        YashanDbValidConnectionChecker checker = new YashanDbValidConnectionChecker();
        checker.setTimeout(5);
        MockConnection conn = new MockConnection();

        // Custom timeout should be used as fallback when validationQueryTimeout is 0
        assertTrue(checker.isValidConnection(conn, "SELECT 'x' FROM DUAL", 0));
    }

    @Test
    public void test_config_from_properties() throws Exception {
        YashanDbValidConnectionChecker checker = new YashanDbValidConnectionChecker();
        Properties props = new Properties();
        props.setProperty("druid.yashandb.pingTimeout", "3");
        checker.configFromProperties(props);

        MockConnection conn = new MockConnection();
        // Timeout from properties (3 seconds) should be used as fallback
        assertTrue(checker.isValidConnection(conn, "SELECT 'x' FROM DUAL", 0));
    }

    @Test
    public void test_config_from_properties_null() throws Exception {
        YashanDbValidConnectionChecker checker = new YashanDbValidConnectionChecker();
        // Should not throw when properties is null
        checker.configFromProperties(null);
    }

    @Test
    public void test_explicit_timeout_takes_precedence() throws Exception {
        YashanDbValidConnectionChecker checker = new YashanDbValidConnectionChecker();
        checker.setTimeout(5);
        MockConnection conn = new MockConnection();

        // When validationQueryTimeout > 0, it should take precedence over the fallback timeout
        assertTrue(checker.isValidConnection(conn, "SELECT 'x' FROM DUAL", 10));
    }
}
