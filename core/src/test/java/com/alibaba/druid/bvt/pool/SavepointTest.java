package com.alibaba.druid.bvt.pool;

import static org.junit.Assert.*;


import java.sql.Connection;
import java.sql.Savepoint;
import java.sql.Statement;

import junit.framework.TestCase;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.pool.DruidDataSource;

public class SavepointTest extends TestCase {
    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setInitialSize(1);

    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_multi_savepoint() throws Exception {
        Connection conn = dataSource.getConnection();
        MockConnection physicalConn = conn.unwrap(MockConnection.class);

        assertEquals(true, conn.getAutoCommit());
        assertEquals(true, physicalConn.getAutoCommit());

        conn.setAutoCommit(false);

        assertEquals(false, conn.getAutoCommit());
        assertEquals(false, physicalConn.getAutoCommit());

        Savepoint[] savepoints = new Savepoint[100];
        for (int i = 0; i < savepoints.length; ++i) {
            Statement stmt = conn.createStatement();
            stmt.execute("insert t (" + i + ")");
            stmt.close();
            savepoints[i] = conn.setSavepoint();

            assertEquals(i + 1, physicalConn.getSavepoints().size());
            for (int j = 0; j <= i; ++j) {
                assertTrue(physicalConn.getSavepoints().contains(savepoints[j]));
            }
        }

        // rollback single
        conn.rollback(savepoints[99]);
        assertEquals(99, physicalConn.getSavepoints().size());

        // release single
        conn.releaseSavepoint(savepoints[97]);
        assertEquals(98, physicalConn.getSavepoints().size());

        // rollback multi
        conn.rollback(savepoints[90]);
        assertEquals(90, physicalConn.getSavepoints().size());

        // rollback all
        conn.rollback();
        assertEquals(0, physicalConn.getSavepoints().size());

        conn.close();
    }
}
