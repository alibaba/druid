package com.alibaba.druid.bvt.pool.basic;

import java.sql.SQLException;

import junit.framework.TestCase;

import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.pool.PoolableStatement;

public class PoolableStatementTest3 extends TestCase {

    public void test_clearResultSetError() throws Exception {
        final MockResultSet rs = new MockResultSet(null) {

            public void close() throws SQLException {
                throw new SQLException();
            }
        };

        PoolableStatement stmt = new PoolableStatement(null, null) {

            public void close() throws SQLException {
                resultSetTrace.add(rs);
                clearResultSet();
            }
        };
        stmt.close();

    }
}
