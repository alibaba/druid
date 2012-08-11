package com.alibaba.druid.bvt.pool;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockCallableStatement;
import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockResultSet;
import com.alibaba.druid.pool.DruidDataSource;

public class CallableStatmentTest extends TestCase {

    private DruidDataSource dataSource;

    protected void setUp() throws Exception {
        dataSource = new DruidDataSource();
        dataSource.setUrl("jdbc:mock:xxx");
        dataSource.setTestOnBorrow(false);
        dataSource.setPoolPreparedStatements(true);
        dataSource.setMaxOpenPreparedStatements(100);
        dataSource.setFilters("log4j");

        dataSource.setDriver(new MockDriver() {

            public MockCallableStatement createMockCallableStatement(MockConnection conn, String sql) {
                return new MyMockCallableStatement(conn, sql);
            }
        });
    }

    protected void tearDown() throws Exception {
        dataSource.close();
    }

    public void test_connect() throws Exception {
        MockCallableStatement rawStmt = null;
        MockResultSet rawRs = null;
        {
            Connection conn = dataSource.getConnection();

            CallableStatement stmt = conn.prepareCall("select 1");
            stmt.execute();
            rawStmt = stmt.unwrap(MockCallableStatement.class);

            ResultSet rs = (ResultSet) stmt.getObject(0);
            
            rawRs = rs.unwrap(MockResultSet.class);
            
            rs.next();
            
            rs.close();
            stmt.close();
            
            Assert.assertFalse(rawStmt.isClosed());
            Assert.assertTrue(rawRs.isClosed());
            
            rawRs = rs.unwrap(MockResultSet.class);
            Assert.assertNotNull(rawRs);

            conn.close();
        }

        {
            Connection conn = dataSource.getConnection();

            CallableStatement stmt = conn.prepareCall("select 1");
            stmt.execute();

            Assert.assertSame(rawStmt, stmt.unwrap(MockCallableStatement.class));
            Assert.assertFalse(rawStmt.isClosed());

            stmt.getObject(0);

            ResultSet rs = (ResultSet) stmt.getObject(0);
            rs.next();
            rs.close();

            stmt.close();

            conn.close();
        }
    }

    public static class MyMockCallableStatement extends MockCallableStatement {

        public MyMockCallableStatement(MockConnection conn, String sql){
            super(conn, sql);
        }

        public Object getObject(int index) throws SQLException {
            return this.getConnection().getDriver().createResultSet(this);
        }
    }
}
