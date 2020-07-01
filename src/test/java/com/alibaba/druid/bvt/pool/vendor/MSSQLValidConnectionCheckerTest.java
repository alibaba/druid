package com.alibaba.druid.bvt.pool.vendor;

import java.sql.SQLException;

import com.alibaba.druid.PoolTestCase;
import junit.framework.TestCase;

import org.junit.Assert;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.vendor.MSSQLValidConnectionChecker;

public class MSSQLValidConnectionCheckerTest extends PoolTestCase {

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

        SQLException error = null;
        try {
            checker.isValidConnection(conn, "select 1", 10);
        } catch (SQLException ex) {
            error = ex;
        }
        Assert.assertNotNull(error);
        Assert.assertSame(error, conn.getError());
    }
}
