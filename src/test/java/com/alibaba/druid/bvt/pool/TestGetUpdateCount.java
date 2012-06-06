package com.alibaba.druid.bvt.pool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import junit.framework.Assert;
import junit.framework.TestCase;

import com.alibaba.druid.mock.MockConnection;
import com.alibaba.druid.mock.MockDriver;
import com.alibaba.druid.mock.MockPreparedStatement;
import com.alibaba.druid.mock.MockPreparedStatementFactory;
import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.util.JdbcUtils;

public class TestGetUpdateCount extends TestCase {

    private DruidDataSource dataSource;
    private MockDriver      driver;

    protected void setUp() throws Exception {
        driver = new MockDriver();
        driver.setPreparedStatementFactory(new MockPreparedStatementFactory() {

            @Override
            public MockPreparedStatement createMockPreparedStatement(MockConnection conn, String sql) {
                return new MyPreparedStatement(conn, sql);
            }

        });

        // /////////////////////////////

        dataSource = new DruidDataSource();

        dataSource.setDriver(driver);
        dataSource.setUrl("jdbc:mock:xx");
        dataSource.setFilters("stat");
    }

    protected void tearDown() throws Exception {
        JdbcUtils.close(dataSource);
    }

    public void test_executeQuery() throws Exception {
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement("select ?");
        
        MyPreparedStatement myStmt = stmt.unwrap(MyPreparedStatement.class);
        
        Assert.assertNull(myStmt.updateCount);
        
        stmt.setString(1, "xxx");
        ResultSet rs = stmt.executeQuery();
        
        Assert.assertNull(myStmt.updateCount);
        
        rs.close();
        stmt.close();
        conn.close();
    }
    
    public void test_execute() throws Exception {
        Connection conn = dataSource.getConnection();

        PreparedStatement stmt = conn.prepareStatement("update t set id = ?");
        
        MyPreparedStatement myStmt = stmt.unwrap(MyPreparedStatement.class);
        
        Assert.assertNull(myStmt.updateCount);
        
        stmt.setString(1, "xxx");
        stmt.execute();
        
        Assert.assertNotNull(myStmt.updateCount);
        
        Assert.assertEquals(1, stmt.getUpdateCount());
        
        stmt.close();
        conn.close();
    }

    public static class MyPreparedStatement extends MockPreparedStatement {

        Integer updateCount = null;

        public MyPreparedStatement(MockConnection conn, String sql){
            super(conn, sql);
        }

        @Override
        public int getUpdateCount() throws SQLException {
            if (updateCount != null) {
                throw new SQLException("illegal state");
            }
            
            updateCount = 1;
            return updateCount;
        }
    }
}
