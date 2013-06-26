package com.alibaba.druid.bvt.pool.vendor;

import java.sql.SQLException;

import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.vendor.MSSQLValidConnectionChecker;

public class MSSQLValidConnectionCheckerTest extends TestCase {

    public void test_0() throws Exception {
        MSSQLValidConnectionChecker checker = new MSSQLValidConnectionChecker();

        MockConnection conn = new MockConnection();

        Assert.assertTrue(checker.isValidConnection(conn, "select 1", 10));

    }

    public void test_closed() throws Exception {
        MSSQLValidConnectionChecker checker = new MSSQLValidConnectionChecker();

        MockConnection conn = new MockConnection();

        conn.close();
        Assert.assertFalse(checker.isValidConnection(conn, "select 1", 10));
    }

    public void test_error() throws Exception {
        MSSQLValidConnectionChecker checker = new MSSQLValidConnectionChecker();

        MockConnection conn = new MockConnection();
        conn.setError(new SQLException());

        Assert.assertFalse(checker.isValidConnection(conn, "select 1", 10));
    }
}
