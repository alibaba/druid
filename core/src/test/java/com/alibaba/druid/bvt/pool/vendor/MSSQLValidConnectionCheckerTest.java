package com.alibaba.druid.bvt.pool.vendor;

import com.alibaba.druid.PoolTestCase;
import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.vendor.MSSQLValidConnectionChecker;

import java.sql.SQLException;

public class MSSQLValidConnectionCheckerTest extends PoolTestCase {
    public void test_0() throws Exception {
        MSSQLValidConnectionChecker checker = new MSSQLValidConnectionChecker();

        MockConnection conn = new MockConnection();

        assertTrue(checker.isValidConnection(conn, "select 1", 10));
    }

    public void test_closed() throws Exception {
        MSSQLValidConnectionChecker checker = new MSSQLValidConnectionChecker();

        MockConnection conn = new MockConnection();

        conn.close();
        assertFalse(checker.isValidConnection(conn, "select 1", 10));
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
        assertNotNull(error);
        assertSame(error, conn.getError());
    }
}
