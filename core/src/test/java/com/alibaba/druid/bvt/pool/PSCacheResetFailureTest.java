/*
 * Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.alibaba.druid.bvt.pool;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.pool.DruidConnectionHolder;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.alibaba.druid.pool.DruidPooledPreparedStatement;
import com.alibaba.druid.pool.ExceptionSorter;
import org.junit.jupiter.api.Test;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class PSCacheResetFailureTest {
    @Test
    public void clearParametersFailureClosesNewStatement() throws Exception {
        checkResetFailure(false, false, false, false);
    }

    @Test
    public void clearBatchFailureClosesNewStatement() throws Exception {
        checkResetFailure(true, false, false, false);
    }

    @Test
    public void clearParametersFailureEvictsCachedStatement() throws Exception {
        checkResetFailure(false, true, false, false);
    }

    @Test
    public void clearBatchFailureEvictsCachedStatement() throws Exception {
        checkResetFailure(true, true, false, false);
    }

    @Test
    public void closeFailureDoesNotReplaceResetFailure() throws Exception {
        checkResetFailure(false, false, true, false);
    }

    @Test
    public void fatalResetFailureStillClosesStatement() throws Exception {
        checkResetFailure(false, false, false, true);
    }

    @Test
    public void fatalCloseFailureStillDiscardsConnection() throws Exception {
        checkResetFailure(false, false, true, false, true);
    }

    @Test
    public void sharedStatementIsNotCachedAfterResetFailure() throws Exception {
        checkSharedResetFailure(false, false, false);
    }

    @Test
    public void sharedStatementIsClosedAfterBothResetsFail() throws Exception {
        checkSharedResetFailure(true, false, false);
    }

    @Test
    public void failedSharedStatementDoesNotRemoveReplacement() throws Exception {
        checkSharedResetFailure(false, true, false);
    }

    @Test
    public void replacedSharedStatementDoesNotDecrementReplacementCount() throws Exception {
        checkSharedResetFailure(false, false, true);
    }

    private void checkSharedResetFailure(boolean failAgain, boolean replaceAfter,
                                         boolean replaceBefore) throws Exception {
        List<ResetFailureStatement> statements = new ArrayList<>();
        try (DruidDataSource dataSource = new DruidDataSource()) {
            dataSource.setDriver(new MockDriver() {
                @Override
                public MockPreparedStatement createMockPreparedStatement(MockConnection conn, String sql) {
                    ResetFailureStatement statement = new ResetFailureStatement(conn, sql, false);
                    statements.add(statement);
                    return statement;
                }
            });
            dataSource.setUrl("jdbc:mock:shared-reset-failure");
            dataSource.setMaxActive(1);
            dataSource.setTestWhileIdle(false);
            dataSource.setPoolPreparedStatements(true);
            dataSource.setSharePreparedStatements(true);
            dataSource.setMaxPoolPreparedStatementPerConnectionSize(10);

            try (DruidPooledConnection connection = dataSource.getConnection()) {
                String sql = "select ?";
                connection.prepareStatement(sql).close();
                DruidPooledPreparedStatement first =
                        (DruidPooledPreparedStatement) connection.prepareStatement(sql);
                DruidPooledPreparedStatement second =
                        (DruidPooledPreparedStatement) connection.prepareStatement(sql);
                assertSame(first.getPreparedStatementHolder(), second.getPreparedStatementHolder());
                ResetFailureStatement raw = statements.get(0);
                DruidPooledPreparedStatement replacement = null;
                if (replaceBefore) {
                    dataSource.setSharePreparedStatements(false);
                    replacement = (DruidPooledPreparedStatement) connection.prepareStatement(sql);
                    replacement.close();
                }

                SQLException resetError = new SQLException("reset failed");
                raw.resetError = resetError;
                assertSame(resetError, assertThrows(SQLException.class, first::close));
                assertEquals(1, first.getPreparedStatementHolder().getInUseCount());
                assertEquals(0, raw.closeCount);
                assertEquals(1, dataSource.getCachedPreparedStatementCount());
                if (replaceAfter) {
                    replacement = (DruidPooledPreparedStatement) connection.prepareStatement(sql);
                    replacement.close();
                    assertEquals(2, dataSource.getCachedPreparedStatementCount());
                }

                if (failAgain) {
                    assertSame(resetError, assertThrows(SQLException.class, second::close));
                } else {
                    raw.resetError = null;
                    second.close();
                }
                assertTrue(first.isClosed());
                assertTrue(second.isClosed());
                assertTrue(raw.isClosed());
                assertEquals(1, raw.closeCount);
                assertEquals(0, first.getPreparedStatementHolder().getInUseCount());
                assertEquals(1, dataSource.getClosedPreparedStatementCount());
                assertEquals(replacement == null ? 0 : 1, dataSource.getCachedPreparedStatementCount());
                assertEquals(replacement == null ? 0 : 1,
                        connection.getConnectionHolder().getStatementPool().size());
                if (replacement != null) {
                    DruidPooledPreparedStatement next =
                            (DruidPooledPreparedStatement) connection.prepareStatement(sql);
                    assertSame(replacement.getPreparedStatementHolder(), next.getPreparedStatementHolder());
                    assertFalse(statements.get(1).isClosed());
                    next.close();
                }
            }
        }
    }

    private void checkResetFailure(boolean failOnBatch, boolean cached,
                                   boolean failOnClose, boolean fatal) throws Exception {
        checkResetFailure(failOnBatch, cached, failOnClose, fatal, false);
    }

    private void checkResetFailure(boolean failOnBatch, boolean cached,
                                   boolean failOnClose, boolean fatal, boolean fatalClose) throws Exception {
        List<ResetFailureStatement> statements = new ArrayList<>();
        SQLException resetError = new SQLFeatureNotSupportedException("statement reset failed");
        SQLException closeError = new SQLException("statement close failed");
        List<SQLException> sortedErrors = new ArrayList<>();
        List<Integer> closeCountsAtSort = new ArrayList<>();

        try (DruidDataSource dataSource = new DruidDataSource()) {
            dataSource.setDriver(new MockDriver() {
                @Override
                public MockPreparedStatement createMockPreparedStatement(MockConnection conn, String sql) {
                    ResetFailureStatement statement = new ResetFailureStatement(conn, sql, failOnBatch);
                    statements.add(statement);
                    return statement;
                }
            });
            dataSource.setUrl("jdbc:mock:reset-failure");
            dataSource.setMaxActive(1);
            dataSource.setTestWhileIdle(false);
            dataSource.setPoolPreparedStatements(true);
            dataSource.setMaxPoolPreparedStatementPerConnectionSize(10);
            dataSource.setExceptionSorter(new ExceptionSorter() {
                @Override
                public boolean isExceptionFatal(SQLException error) {
                    closeCountsAtSort.add(statements.get(0).closeCount);
                    sortedErrors.add(error);
                    return fatal || (fatalClose && error == closeError);
                }

                @Override
                public void configFromProperties(Properties properties) {
                }
            });

            try (DruidPooledConnection connection = dataSource.getConnection()) {
                String sql = "select ?";
                if (cached) {
                    connection.prepareStatement(sql).close();
                    assertEquals(1, dataSource.getCachedPreparedStatementCount());
                }
                DruidPooledPreparedStatement statement =
                        (DruidPooledPreparedStatement) connection.prepareStatement(sql);
                DruidConnectionHolder connectionHolder = connection.getConnectionHolder();
                ResetFailureStatement raw = statements.get(0);
                assertSame(raw, statement.getPreparedStatementHolder().statement);
                ResultSet resultSet = statement.executeQuery();
                raw.resetError = resetError;
                raw.closeError = failOnClose ? closeError : null;
                raw.failBeforeClose = fatalClose;

                assertSame(resetError, assertThrows(SQLException.class, statement::close));
                assertTrue(statement.isClosed());
                if (fatalClose) {
                    assertTrue(raw.getConnection().isClosed());
                } else {
                    assertTrue(raw.isClosed());
                }
                assertTrue(resultSet.isClosed());
                assertEquals(1, raw.closeCount);
                assertEquals(0, statement.getPreparedStatementHolder().getInUseCount());
                assertTrue(connectionHolder.getStatementPool().getMap().isEmpty());
                assertEquals(0, dataSource.getCachedPreparedStatementCount());
                assertEquals(1, dataSource.getClosedPreparedStatementCount());
                assertEquals(failOnClose ? 2 : 1, dataSource.getErrorCount());
                assertEquals(failOnClose ? 2 : 1, sortedErrors.size());
                assertEquals(Integer.valueOf(1), closeCountsAtSort.get(0));
                assertSame(resetError, sortedErrors.get(0));

                if (failOnClose) {
                    assertEquals(1, resetError.getSuppressed().length);
                    assertSame(closeError, resetError.getSuppressed()[0]);
                    assertSame(closeError, sortedErrors.get(1));
                } else {
                    assertEquals(0, resetError.getSuppressed().length);
                }

                statement.close();
                assertEquals(1, raw.failureCount);
                assertEquals(1, raw.closeCount);
                assertEquals(0, statement.getPreparedStatementHolder().getInUseCount());

                if (fatal || fatalClose) {
                    assertNull(connection.getConnectionHolder());
                } else {
                    assertFalse(connection.isClosed());
                }
            }
            assertEquals(1, statements.get(0).failureCount);
            assertEquals(failOnClose ? 2 : 1, dataSource.getErrorCount());
        }
    }

    private static class ResetFailureStatement extends MockPreparedStatement {
        private final boolean failOnBatch;
        private SQLException resetError;
        private SQLException closeError;
        private boolean failBeforeClose;
        private int closeCount;
        private int failureCount;

        ResetFailureStatement(MockConnection connection, String sql, boolean failOnBatch) {
            super(connection, sql);
            this.failOnBatch = failOnBatch;
        }

        @Override
        public ResultSet executeQuery() {
            return new MockResultSet(this);
        }

        @Override
        public void clearParameters() throws SQLException {
            if (!failOnBatch && resetError != null) {
                failureCount++;
                throw resetError;
            }
            super.clearParameters();
        }

        @Override
        public void clearBatch() throws SQLException {
            if (failOnBatch && resetError != null) {
                failureCount++;
                throw resetError;
            }
            super.clearBatch();
        }

        @Override
        public void close() throws SQLException {
            closeCount++;
            if (failBeforeClose && closeError != null) {
                throw closeError;
            }
            super.close();
            if (closeError != null) {
                throw closeError;
            }
        }
    }
}
