package com.alibaba.druid.bvt.pool.vendor;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.mock.MockStatement;
import com.alibaba.druid.pool.vendor.MySqlValidConnectionChecker;
import com.alibaba.druid.proxy.jdbc.ConnectionProxyImpl;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;

public class MySQLValidationTimeoutTest {
    private MySqlValidConnectionChecker checker() {
        MySqlValidConnectionChecker checker = new MySqlValidConnectionChecker();
        checker.setUsePingMethod(true);
        return checker;
    }

    static class TimeoutTrackingConnection extends MockConnection {
        int currentTimeout;
        int attempts;
        int reads;
        int executions;
        String executedSql;
        int statementTimeout;
        int timeoutDuringQuery;
        Throwable getFailure;
        Throwable setFailure;
        Throwable queryFailure;
        Throwable restoreFailure;
        boolean statementClosed;
        boolean resultsetClosed;
        boolean firstSetDone;
        boolean closed;

        TimeoutTrackingConnection(int initialTimeout) {
            this.currentTimeout = initialTimeout;
        }

        @Override
        public boolean isClosed() throws SQLException {
            return closed;
        }

        @Override
        public int getNetworkTimeout() throws SQLException {
            reads++;
            if (getFailure != null) {
                if (getFailure instanceof SQLException) {
                    throw (SQLException) getFailure;
                }
                if (getFailure instanceof RuntimeException) {
                    throw (RuntimeException) getFailure;
                }
                if (getFailure instanceof Error) {
                    throw (Error) getFailure;
                }
                throw new SQLException(getFailure);
            }
            return currentTimeout;
        }

        @Override
        public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
            attempts++;
            if (!firstSetDone) {
                firstSetDone = true;
                if (setFailure != null) {
                    if (setFailure instanceof SQLException) {
                        throw (SQLException) setFailure;
                    }
                    if (setFailure instanceof RuntimeException) {
                        throw (RuntimeException) setFailure;
                    }
                    if (setFailure instanceof Error) {
                        throw (Error) setFailure;
                    }
                    throw new SQLException(setFailure);
                }
                if (executor == null) {
                    throw new SQLException("executor null");
                }
                executor.execute(() -> currentTimeout = milliseconds);
            } else {
                if (restoreFailure != null) {
                    if (restoreFailure instanceof SQLException) {
                        throw (SQLException) restoreFailure;
                    }
                    if (restoreFailure instanceof RuntimeException) {
                        throw (RuntimeException) restoreFailure;
                    }
                    if (restoreFailure instanceof Error) {
                        throw (Error) restoreFailure;
                    }
                    throw new SQLException(restoreFailure);
                }
                if (executor == null) {
                    throw new SQLException("executor null");
                }
                executor.execute(() -> currentTimeout = milliseconds);
            }
        }

        @Override
        public Statement createStatement() throws SQLException {
            return new MockStatement(this) {
                int queryTimeout;
                @Override
                public ResultSet executeQuery(String sql) throws SQLException {
                    executedSql = sql;
                    executions++;
                    statementTimeout = queryTimeout;
                    timeoutDuringQuery = currentTimeout;
                    if (queryFailure != null) {
                        if (queryFailure instanceof SQLException) {
                            throw (SQLException) queryFailure;
                        }
                        if (queryFailure instanceof RuntimeException) {
                            throw (RuntimeException) queryFailure;
                        }
                        if (queryFailure instanceof Error) {
                            throw (Error) queryFailure;
                        }
                        throw new SQLException(queryFailure);
                    }
                    return new MockResultSet(this) {
                        @Override
                        public boolean next() throws SQLException {
                            return true;
                        }
                        @Override
                        public void close() throws SQLException {
                            resultsetClosed = true;
                        }
                    };
                }
                @Override
                public void close() throws SQLException {
                    statementClosed = true;
                }
                @Override
                public void setQueryTimeout(int seconds) throws SQLException {
                    this.queryTimeout = seconds;
                }
                @Override
                public int getQueryTimeout() throws SQLException {
                    return queryTimeout;
                }
            };
        }
    }

    @Test
    public void testPositiveTimeoutRestoresAndCloses() throws Exception {
        for (int original : new int[]{0, 5000}) {
            TimeoutTrackingConnection conn = new TimeoutTrackingConnection(original);
            boolean valid = checker().isValidConnection(conn, null, 1);
            assertTrue(valid);
            assertEquals(original, conn.currentTimeout);
            assertEquals(2, conn.attempts);
            assertEquals(1, conn.executions);
            assertEquals(1000, conn.timeoutDuringQuery);
            assertEquals("/* ping */ SELECT 1", conn.executedSql);
            assertEquals(1, conn.statementTimeout);
            assertTrue(conn.statementClosed);
            assertTrue(conn.resultsetClosed);
        }
    }

    @Test
    public void testZeroOrNegativeNoSet() throws Exception {
        for (int timeout : new int[]{0, -1}) {
            TimeoutTrackingConnection conn = new TimeoutTrackingConnection(1000);
            MySqlValidConnectionChecker checker = checker();
            boolean valid = checker.isValidConnection(conn, "/* ping */ SELECT 1", timeout);
            assertTrue(valid);
            assertEquals(0, conn.attempts, "no set for timeout=" + timeout);
            assertEquals(0, conn.reads);
            assertEquals(1, conn.executions);
            assertEquals(1000, conn.currentTimeout);
        }
    }

    @Test
    public void testCustomSqlWithoutPingMethod() throws Exception {
        TimeoutTrackingConnection conn = new TimeoutTrackingConnection(2000);
        MySqlValidConnectionChecker checker = new MySqlValidConnectionChecker();
        checker.setUsePingMethod(false);
        boolean valid = checker.isValidConnection(conn, "SELECT 1", 1);
        assertTrue(valid);
        assertEquals("SELECT 1", conn.executedSql);
        assertEquals(0, conn.attempts);
        assertEquals(0, conn.reads);
        assertEquals(2000, conn.timeoutDuringQuery);
        assertEquals(1, conn.statementTimeout);
        assertEquals(2000, conn.currentTimeout);
    }

    @Test
    public void testCustomExactPingMarkerRespected() throws Exception {
        TimeoutTrackingConnection conn = new TimeoutTrackingConnection(0);
        MySqlValidConnectionChecker checker = checker();
        checker.setUsePingMethod(false);
        String customPing = "/* ping */ SELECT 42";
        boolean valid = checker.isValidConnection(conn, customPing, 3);
        assertTrue(valid);
        assertEquals(customPing, conn.executedSql);
        assertEquals(2, conn.attempts);
        assertEquals(3000, conn.timeoutDuringQuery);
    }

    @Test
    public void testTighterOrEqualNoSet() throws Exception {
        int[] originals = {500, 1000};
        for (int original : originals) {
            TimeoutTrackingConnection conn = new TimeoutTrackingConnection(original);
            MySqlValidConnectionChecker checker = checker();
            boolean valid = checker.isValidConnection(conn, "/* ping */ SELECT 1", 1);
            assertTrue(valid);
            assertEquals(0, conn.attempts, "no set for original=" + original);
            assertEquals(original, conn.timeoutDuringQuery);
            assertEquals(original, conn.currentTimeout);
            assertEquals(1, conn.executions);
        }
    }

    @Test
    public void testOverflowClamp() throws Exception {
        TimeoutTrackingConnection conn = new TimeoutTrackingConnection(0);
        MySqlValidConnectionChecker checker = checker();
        boolean valid = checker.isValidConnection(conn, "/* ping */ SELECT 1", Integer.MAX_VALUE);
        assertTrue(valid);
        assertEquals(2, conn.attempts);
        assertEquals(Integer.MAX_VALUE, conn.timeoutDuringQuery);
        assertEquals(0, conn.currentTimeout);
    }

    @Test
    public void testUnsupportedGetAndSetFallback() throws Exception {
        Throwable[] failures = {new SQLFeatureNotSupportedException(), new AbstractMethodError()};
        for (Throwable failure : failures) {
            TimeoutTrackingConnection conn = new TimeoutTrackingConnection(1000);
            conn.getFailure = failure;
            MySqlValidConnectionChecker checker = checker();
            boolean valid = checker.isValidConnection(conn, "/* ping */ SELECT 1", 1);
            assertTrue(valid);
            assertEquals(0, conn.attempts);
            assertEquals(1, conn.executions);
            assertEquals(1000, conn.currentTimeout);

            TimeoutTrackingConnection connSet = new TimeoutTrackingConnection(0);
            connSet.setFailure = failure;
            boolean validSet = checker.isValidConnection(connSet, "/* ping */ SELECT 1", 1);
            assertTrue(validSet);
            assertEquals(1, connSet.attempts);
            assertEquals(1, connSet.executions);
            assertEquals(0, connSet.currentTimeout);
        }
    }

    @Test
    public void testGenuineSQLExceptionFromGetOrSet() throws Exception {
        SQLException expected = new SQLException("get failure");
        TimeoutTrackingConnection conn = new TimeoutTrackingConnection(0);
        conn.getFailure = expected;
        MySqlValidConnectionChecker checker = checker();
        SQLException thrown = assertThrows(SQLException.class, () -> checker.isValidConnection(conn, "/* ping */ SELECT 1", 1));
        assertSame(expected, thrown);
        assertEquals(0, conn.attempts);
        assertEquals(0, conn.executions);

        SQLException expectedSet = new SQLException("set failure");
        TimeoutTrackingConnection conn2 = new TimeoutTrackingConnection(0);
        conn2.setFailure = expectedSet;
        SQLException thrownSet = assertThrows(SQLException.class, () -> checker.isValidConnection(conn2, "/* ping */ SELECT 1", 1));
        assertSame(expectedSet, thrownSet);
        assertEquals(1, conn2.attempts);
        assertEquals(0, conn2.executions);
    }

    @Test
    public void testQueryErrorRestoresAndPropagates() throws Exception {
        TimeoutTrackingConnection conn = new TimeoutTrackingConnection(0);
        SQLException queryError = new SQLException("query error");
        conn.queryFailure = queryError;
        MySqlValidConnectionChecker checker = checker();
        SQLException thrown = assertThrows(SQLException.class, () -> checker.isValidConnection(conn, "/* ping */ SELECT 1", 1));
        assertSame(queryError, thrown);
        assertEquals(1, conn.executions);
        assertEquals(2, conn.attempts);
        assertEquals(0, conn.currentTimeout);
    }

    @Test
    public void testQueryErrorAndRestoreErrorSuppressed() throws Exception {
        TimeoutTrackingConnection conn = new TimeoutTrackingConnection(0);
        SQLException queryError = new SQLException("query error");
        SQLException restoreError = new SQLException("restore error");
        conn.queryFailure = queryError;
        conn.restoreFailure = restoreError;
        MySqlValidConnectionChecker checker = checker();
        SQLException thrown = assertThrows(SQLException.class, () -> checker.isValidConnection(conn, "/* ping */ SELECT 1", 1));
        assertSame(queryError, thrown);
        assertEquals(1, conn.executions);
        assertEquals(2, conn.attempts);
        assertEquals(1, thrown.getSuppressed().length);
        assertSame(restoreError, thrown.getSuppressed()[0]);
    }

    @Test
    public void testRestoreOnlyFailurePropagates() throws Exception {
        TimeoutTrackingConnection conn = new TimeoutTrackingConnection(0);
        SQLException restoreError = new SQLException("restore error");
        conn.restoreFailure = restoreError;
        MySqlValidConnectionChecker checker = checker();
        SQLException thrown = assertThrows(SQLException.class, () -> checker.isValidConnection(conn, "/* ping */ SELECT 1", 1));
        assertSame(restoreError, thrown);
        assertEquals(1, conn.executions);
        assertEquals(2, conn.attempts);
    }

    @Test
    public void testClosedReturnsFalseNoGetter() throws Exception {
        TimeoutTrackingConnection conn = new TimeoutTrackingConnection(1000);
        conn.closed = true;
        MySqlValidConnectionChecker checker = checker();
        boolean valid = checker.isValidConnection(conn, "/* ping */ SELECT 1", 1);
        assertFalse(valid);
        assertEquals(0, conn.attempts);
        assertEquals(0, conn.executions);
    }

    @Test
    public void testWrapperBypassesProxy() throws Exception {
        TimeoutTrackingConnection raw = new TimeoutTrackingConnection(0);
        ConnectionProxyImpl proxy = new ConnectionProxyImpl(null, raw, new Properties(), 1) {
            @Override
            public boolean isClosed() throws SQLException {
                return raw.isClosed();
            }
            @Override
            public int getNetworkTimeout() throws SQLException {
                throw new AssertionError("should not be called");
            }
            @Override
            public void setNetworkTimeout(Executor executor, int milliseconds) throws SQLException {
                throw new AssertionError("should not be called");
            }
        };
        MySqlValidConnectionChecker checker = checker();
        boolean valid = checker.isValidConnection(proxy, "/* ping */ SELECT 1", 1);
        assertTrue(valid);
        assertEquals(1000, raw.timeoutDuringQuery);
        assertEquals(0, raw.currentTimeout);
        assertEquals(2, raw.attempts);
    }
    @Test
    public void testSameFailureObjectRemainsPrimary() throws Exception {
        TimeoutTrackingConnection conn = new TimeoutTrackingConnection(0);
        SQLException failure = new SQLException("validation and restore failed");
        conn.queryFailure = failure;
        conn.restoreFailure = failure;
        SQLException thrown = assertThrows(SQLException.class,
                () -> checker().isValidConnection(conn, null, 1));
        assertSame(failure, thrown);
        assertEquals(0, thrown.getSuppressed().length);
    }

}
